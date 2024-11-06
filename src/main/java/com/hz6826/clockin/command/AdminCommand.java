package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class AdminCommand {
    public static String CURRENCY_NAME = BasicConfig.getConfig().getCurrencyName();
    public static void getReward(CommandContext<ServerCommandSource> context){
        final String key = StringArgumentType.getString(context, "key");
        RewardInterface reward = ClockInServer.DBM.getRewardOrNew(key);
        if (reward.isNew()) {
            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.get.null", key).formatted(Formatting.RED), false);
        } else {
            Text rewardText = FabricUtils.generateReadableReward(reward);
            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.get.success", key).formatted(Formatting.GREEN), false);
            context.getSource().sendFeedback(() -> rewardText, false);
        }
    }

    public static void setRewardItemListFromHotBar(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final String key = StringArgumentType.getString(context, "key");
        ArrayList<ItemStack> itemList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = context.getSource().getPlayerOrThrow().getInventory().getStack(i);
            if (!itemStack.isEmpty()) {
                itemList.add(itemStack);
            }
        }
        RewardInterface reward = ClockInServer.DBM.getRewardOrNew(key);
        reward.setItemListSerialized(FabricUtils.serializeItemStackList(itemList));
        ClockInServer.DBM.createOrUpdateReward(reward);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.item.success", key).formatted(Formatting.GREEN), false);
    }

    public static void setRewardItemListFromInventory(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final String key = StringArgumentType.getString(context, "key");
        ArrayList<ItemStack> itemList = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = context.getSource().getPlayerOrThrow().getInventory().getStack(i);
            if (!itemStack.isEmpty()) {
                itemList.add(itemStack);
            }
        }
        RewardInterface reward = ClockInServer.DBM.getRewardOrNew(key);
        reward.setItemListSerialized(FabricUtils.serializeItemStackList(itemList));
        ClockInServer.DBM.createOrUpdateReward(reward);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.item.success", key).formatted(Formatting.GREEN), false);
    }

    public static void setRewardItemListFromMainHand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final String key = StringArgumentType.getString(context, "key");
        ArrayList<ItemStack> itemList = new ArrayList<>();
        ItemStack itemStack = context.getSource().getPlayerOrThrow().getMainHandStack();
        if (!itemStack.isEmpty()) {
            itemList.add(itemStack);
        }
        RewardInterface reward = ClockInServer.DBM.getRewardOrNew(key);
        reward.setItemListSerialized(FabricUtils.serializeItemStackList(itemList));
        ClockInServer.DBM.createOrUpdateReward(reward);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.item.success", key).formatted(Formatting.GREEN), false);
    }

    public static void setRewardMoney(CommandContext<ServerCommandSource> context){
        final String key = StringArgumentType.getString(context, "key");
        final double amount = DoubleArgumentType.getDouble(context, "amount");
        RewardInterface reward = ClockInServer.DBM.getRewardOrNew(key);
        reward.setMoney(amount);
        ClockInServer.DBM.createOrUpdateReward(reward);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.money.success", key, amount, CURRENCY_NAME).formatted(Formatting.GREEN), false);
    }

    public static void setRewardRaffleTicket(CommandContext<ServerCommandSource> context){
        final String key = StringArgumentType.getString(context, "key");
        final int amount = IntegerArgumentType.getInteger(context, "amount");
        RewardInterface reward = ClockInServer.DBM.getRewardOrNew(key);
        reward.setRaffleTickets(amount);
        ClockInServer.DBM.createOrUpdateReward(reward);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.raffle_ticket.success", key, amount).formatted(Formatting.GREEN), false);
    }

    public static void setRewardMakeupCard(CommandContext<ServerCommandSource> context){
        final String key = StringArgumentType.getString(context, "key");
        final int amount = IntegerArgumentType.getInteger(context, "amount");
        RewardInterface reward = ClockInServer.DBM.getRewardOrNew(key);
        reward.setMakeupCards(amount);
        ClockInServer.DBM.createOrUpdateReward(reward);
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.makeup_card.success", key, amount).formatted(Formatting.GREEN), false);
    }

    public static void giveRewardToPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final String key = StringArgumentType.getString(context, "key");
        Text rewardText = FabricUtils.giveReward(player, key);
        if(rewardText == null){
            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.get.null", key, player.getName()).formatted(Formatting.RED), false);
        }else{
            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.give.success", player.getName(), key).formatted(Formatting.GREEN), false);
        }
    }

    public static void getPlayerBalance(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.get.money", player.getName(), user.getBalance(), CURRENCY_NAME).formatted(Formatting.GREEN), false);
    }

    public static void setPlayerBalance(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        user.setBalance(DoubleArgumentType.getDouble(context, "amount"));
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.set.money.success", player.getName(), DoubleArgumentType.getDouble(context, "amount"), CURRENCY_NAME).formatted(Formatting.GREEN), false);
    }

    public static void addPlayerBalance(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        user.addBalance(DoubleArgumentType.getDouble(context, "amount"));
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.give.money.success", DoubleArgumentType.getDouble(context, "amount"), CURRENCY_NAME, player.getName()).formatted(Formatting.GREEN), false);
    }

    public static void subtractPlayerBalance(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        user.subtractBalance(DoubleArgumentType.getDouble(context, "amount"));
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.take.money.success", DoubleArgumentType.getDouble(context, "amount"), CURRENCY_NAME, player.getName()).formatted(Formatting.GREEN), false);
    }

    public static void getPlayerRaffleTicket(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.get.raffle_ticket", player.getName(), user.getRaffleTicket()).formatted(Formatting.GREEN), false);
    }

    public static void setPlayerRaffleTicket(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        user.setRaffleTicket(IntegerArgumentType.getInteger(context, "amount"));
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.set.raffle_ticket.success", player.getName(), IntegerArgumentType.getInteger(context, "amount")).formatted(Formatting.GREEN), false);
    }

    public static void addPlayerRaffleTicket(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        user.addRaffleTicket(IntegerArgumentType.getInteger(context, "amount"));
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.give.raffle_ticket.success", IntegerArgumentType.getInteger(context, "amount"), player.getName()).formatted(Formatting.GREEN), false);
    }

    public static void removePlayerRaffleTicket(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        user.removeRaffleTicket(IntegerArgumentType.getInteger(context, "amount"));
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.take.raffle_ticket.success", IntegerArgumentType.getInteger(context, "amount"), player.getName()).formatted(Formatting.GREEN), false);
    }

    public static void getPlayerMakeupCard(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.get.makeup_card", player.getName(), user.getMakeupCard()).formatted(Formatting.GREEN), false);
    }

    public static void setPlayerMakeupCard(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        user.setMakeupCard(IntegerArgumentType.getInteger(context, "amount"));
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.set.makeup_card.success", player.getName(), IntegerArgumentType.getInteger(context, "amount")).formatted(Formatting.GREEN), false);
    }

    public static void addPlayerMakeupCard(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        user.addMakeupCard(IntegerArgumentType.getInteger(context, "amount"));
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.give.makeup_card.success", IntegerArgumentType.getInteger(context, "amount"), player.getName()).formatted(Formatting.GREEN), false);
    }

    public static void removePlayerMakeupCard(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
        user.removeMakeupCard(IntegerArgumentType.getInteger(context, "amount"));
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.take.makeup_card.success", IntegerArgumentType.getInteger(context, "amount"), player.getName()).formatted(Formatting.GREEN), false);
    }
}
