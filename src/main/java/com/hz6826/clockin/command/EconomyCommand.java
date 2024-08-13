package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class EconomyCommand {
    public static String CURRENCY_NAME = BasicConfig.getConfig().getCurrencyName();

    public static void deposit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ArrayList<ItemStack> itemList = new ArrayList<>();
        Inventory inventory = context.getSource().getPlayerOrThrow().getInventory();
        for (int i = 0; i < 36; i++) {  // from inventory
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()
                    && BasicConfig.getConfig().getPhysicalCurrencyItemIds().containsKey(Registries.ITEM.getId(itemStack.getItem()).toString())) {
                itemList.add(itemStack);
                inventory.removeStack(i);
            }
        }
        UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(context.getSource().getPlayerOrThrow().getUuidAsString());
        int amount = FabricUtils.parsePhysicalMoneyToAmount(itemList);
        user.addBalance(amount);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.economy.deposit", amount, CURRENCY_NAME).formatted(Formatting.GREEN), false);
    }

    public static void withdraw(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
        int amount = (int) Math.floor(user.getBalance());
        withdrawInner(context, amount);
    }

    public static void withdrawWithAmount(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        withdrawInner(context, IntegerArgumentType.getInteger(context, "amount"));
    }

    private static void withdrawInner(CommandContext<ServerCommandSource> context, int amount) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
        if (!user.hasEnoughBalance(amount)) {
            context.getSource().sendError(Text.translatable("command.clockin.economy.not_enough_money", amount, CURRENCY_NAME));
            return;
        }
        List<ItemStack> itemList = FabricUtils.parseAmountToPhysicalMoney(amount);
        FabricUtils.giveItemList(itemList, player);
        user.subtractBalance(amount);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.economy.withdraw", amount, CURRENCY_NAME).formatted(Formatting.GREEN), false);
    }

    public static void transfer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity toPlayer = EntityArgumentType.getPlayer(context, "to");
        if (toPlayer.equals(context.getSource().getPlayer())) {
            context.getSource().sendError(Text.translatable("command.clockin.economy.transfer.self"));
            return;
        }
        UserWithAccountAbstract fromUser = ClockInServer.DATABASE_MANAGER.getUserByUUID(context.getSource().getPlayerOrThrow().getUuidAsString());
        UserWithAccountAbstract toUser = ClockInServer.DATABASE_MANAGER.getUserByUUID(toPlayer.getUuidAsString());
        double amount = DoubleArgumentType.getDouble(context, "amount");
        if (!fromUser.hasEnoughBalance(amount)) {
            context.getSource().sendError(Text.translatable("command.clockin.economy.transfer.failed", amount, CURRENCY_NAME));
            return;
        }
        fromUser.transferBalance(amount, toUser);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.economy.transfer.success", amount, CURRENCY_NAME, toPlayer.getDisplayName()).formatted(Formatting.GREEN), false);
    }

    public static void getBalance(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(context.getSource().getPlayerOrThrow().getUuidAsString());
        double balance = user.getBalance();
        int rank = user.getBalanceRank();
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.economy.balance", balance, CURRENCY_NAME, rank).formatted(Formatting.AQUA), false);
    }
}
