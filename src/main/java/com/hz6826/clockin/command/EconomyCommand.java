package com.hz6826.clockin.command;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.api.Util;
import com.hz6826.clockin.sql.DatabaseManager;
import com.hz6826.clockin.sql.User;
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

public class EconomyCommand {
    public static final String CURRENCY_NAME = ClockIn.CONFIG.getCurrencyName();

    public static void deposit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ArrayList<ItemStack> itemList = new ArrayList<>();
        Inventory inventory = context.getSource().getPlayerOrThrow().getInventory();
        for (int i = 0; i < 36; i++) {  // from inventory
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()
                    && ClockIn.CONFIG.getPhysicalCurrencyItemIds().containsKey(Registries.ITEM.getId(itemStack.getItem()).toString())) {
                itemList.add(itemStack);
                inventory.removeStack(i);
            }
        }
        User user = DatabaseManager.getUserByUUID(context.getSource().getPlayerOrThrow().getUuidAsString());
        int amount = Util.parsePhysicalMoneyToAmount(itemList);
        user.addBalance(amount);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.economy.deposit", amount, CURRENCY_NAME).formatted(Formatting.GREEN), false);
    }

    public static void withdraw(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        User user = DatabaseManager.getUserByUUID(player.getUuidAsString());
        int amount = (int) Math.floor(user.getBalance());
        withdrawInner(context, amount);
    }

    public static void withdrawWithAmount(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        withdrawInner(context, IntegerArgumentType.getInteger(context, "amount"));
    }

    private static void withdrawInner(CommandContext<ServerCommandSource> context, int amount) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        User user = DatabaseManager.getUserByUUID(player.getUuidAsString());
        if (!user.hasEnoughBalance(amount)) {
            context.getSource().sendError(Text.translatable("command.clockin.economy.not_enough_money", amount, CURRENCY_NAME));
            return;
        }
        Util.givePhysicalMoney(player, amount);
        user.subtractBalance(amount);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.economy.withdraw", amount, CURRENCY_NAME).formatted(Formatting.GREEN), false);
    }

    public static void transfer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity toPlayer = EntityArgumentType.getPlayer(context, "to");
        if (toPlayer.equals(context.getSource().getPlayer())) {
            context.getSource().sendError(Text.translatable("command.clockin.economy.transfer.self"));
            return;
        }
        User fromUser = DatabaseManager.getUserByUUID(context.getSource().getPlayerOrThrow().getUuidAsString());
        User toUser = DatabaseManager.getUserByUUID(toPlayer.getUuidAsString());
        double amount = DoubleArgumentType.getDouble(context, "amount");
        if (!fromUser.hasEnoughBalance(amount)) {
            context.getSource().sendError(Text.translatable("command.clockin.economy.transfer.failed", amount, CURRENCY_NAME));
            return;
        }
        fromUser.transferBalance(amount, toUser);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.economy.transfer.success", amount, CURRENCY_NAME, toPlayer.getDisplayName()).formatted(Formatting.GREEN), false);
    }

    public static void getBalance(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        User user = DatabaseManager.getUserByUUID(context.getSource().getPlayerOrThrow().getUuidAsString());
        double balance = user.getBalance();
        int rank = DatabaseManager.getUsersSortedByBalance().indexOf(user);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.economy.balance", balance, CURRENCY_NAME, rank).formatted(Formatting.AQUA), false);
    }
}
