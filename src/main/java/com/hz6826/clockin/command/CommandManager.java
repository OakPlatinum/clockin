package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static net.minecraft.server.command.CommandManager.*;

public class CommandManager {
    public static String CURRENCY_NAME = BasicConfig.getConfig().getCurrencyName();
    public CommandManager() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(literal("clockin")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.clockin.loading"), false);
                    return 1;
                })
                .then(literal("dailyclockin")
                        .executes(context -> {
                            if(context.getSource().isExecutedByPlayer()){
                                Date date = Date.valueOf(LocalDate.now());
                                Time time = Time.valueOf(LocalTime.now());
                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                                DailyClockInRecordInterface dailyClockInRecord = ClockInServer.DATABASE_MANAGER.getDailyClockInRecordOrNull(context.getSource().getPlayerOrThrow().getUuidAsString(), date);
                                if(dailyClockInRecord == null) {
                                    ClockInServer.DATABASE_MANAGER.dailyClockIn(context.getSource().getPlayerOrThrow().getUuidAsString(), date, time);
                                    dailyClockInRecord = ClockInServer.DATABASE_MANAGER.getDailyClockInRecordOrNull(context.getSource().getPlayerOrThrow().getUuidAsString(), date);
                                    if(dailyClockInRecord == null) {
                                        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.failed").formatted(Formatting.RED), false);
                                        return -1;
                                    } else {
                                        DailyClockInRecordInterface finalDailyClockInRecord = dailyClockInRecord;
                                        RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew("daily_reward");
                                        Text rewardText = Text.translatable("command.clockin.reward.null");
                                        if(reward != null) {
                                            FabricUtils.giveItemList(FabricUtils.deserializeItemStackList(reward.getItemListSerialized()), context.getSource().getPlayerOrThrow());
                                            UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(context.getSource().getPlayerOrThrow().getUuidAsString());
                                            user.addBalance(reward.getMoney());
                                            user.addRaffleTicket(reward.getRaffleTickets());
                                            user.addMakeupCard(reward.getMakeupCards());
                                            rewardText = FabricUtils.generateReadableReward(reward);
                                        }
                                        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success", Text.literal(String.valueOf(finalDailyClockInRecord.getRank())).formatted(Formatting.GOLD)).formatted(Formatting.GREEN), false);
                                        Text finalReward_string = rewardText;
                                        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.reward", finalReward_string), false);
                                    }
                                } else {
                                    DailyClockInRecordInterface finalDailyClockInRecord1 = dailyClockInRecord;
                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.already", Text.literal(String.valueOf(finalDailyClockInRecord1.getRank())).formatted(Formatting.GOLD), Text.literal(finalDailyClockInRecord1.getTime().toLocalTime().format(dateTimeFormatter)).formatted(Formatting.BLUE)), false);
                                }
                            }
                            return 1;
                        }))
                .then(literal("admin")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("reward")
                                .then(literal("set")
                                        .then(argument("key", StringArgumentType.string())
                                                .then(literal("itemList")
                                                        .then(literal("fromHot-bar").executes(context -> {
                                                            final String key = StringArgumentType.getString(context, "key");
                                                            ArrayList<ItemStack> itemList = new ArrayList<>();
                                                            for (int i = 0; i < 9; i++) {
                                                                ItemStack itemStack = context.getSource().getPlayerOrThrow().getInventory().getStack(i);
                                                                if(!itemStack.isEmpty()){
                                                                    itemList.add(itemStack);
                                                                }
                                                            }
                                                            RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(key);
                                                            reward.setItemListSerialized(FabricUtils.serializeItemStackList(itemList));
                                                            ClockInServer.DATABASE_MANAGER.createOrUpdateReward(reward);
                                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.item.success", key).formatted(Formatting.GREEN), false);
                                                            return 1;
                                                        }))
                                                        .then(literal("fromInventory").executes(context -> {
                                                            final String key = StringArgumentType.getString(context, "key");
                                                            ArrayList<ItemStack> itemList = new ArrayList<>();
                                                            for (int i = 0; i < 36; i++) {
                                                                ItemStack itemStack = context.getSource().getPlayerOrThrow().getInventory().getStack(i);
                                                                if(!itemStack.isEmpty()){
                                                                    itemList.add(itemStack);
                                                                }
                                                            }
                                                            RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(key);
                                                            reward.setItemListSerialized(FabricUtils.serializeItemStackList(itemList));
                                                            ClockInServer.DATABASE_MANAGER.createOrUpdateReward(reward);
                                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.item.success", key).formatted(Formatting.GREEN), false);
                                                            return 1;
                                                        }))
                                                        .then(literal("fromMainHand").executes(context -> {
                                                            final String key = StringArgumentType.getString(context, "key");
                                                            ArrayList<ItemStack> itemList = new ArrayList<>();
                                                            ItemStack itemStack = context.getSource().getPlayerOrThrow().getMainHandStack();
                                                            if(!itemStack.isEmpty()){
                                                                itemList.add(itemStack);
                                                            }
                                                            RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(key);
                                                            reward.setItemListSerialized(FabricUtils.serializeItemStackList(itemList));
                                                            ClockInServer.DATABASE_MANAGER.createOrUpdateReward(reward);
                                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.item.success", key).formatted(Formatting.GREEN), false);
                                                            return 1;
                                                        }))
                                                )
                                                .then(literal("money")
                                                        .then(argument("amount", IntegerArgumentType.integer()).executes(context -> {
                                                            final String key = StringArgumentType.getString(context, "key");
                                                            final int amount = IntegerArgumentType.getInteger(context, "amount");
                                                            RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(key);
                                                            reward.setMoney(amount);
                                                            ClockInServer.DATABASE_MANAGER.createOrUpdateReward(reward);
                                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.money.success", key, amount, CURRENCY_NAME).formatted(Formatting.GREEN), false);
                                                            return 1;
                                                        }))
                                                )
                                                .then(literal("raffleTicket")
                                                        .then(argument("amount", IntegerArgumentType.integer()).executes(context -> {
                                                            final String key = StringArgumentType.getString(context, "key");
                                                            final int amount = IntegerArgumentType.getInteger(context, "amount");
                                                            RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(key);
                                                            reward.setRaffleTickets(amount);
                                                            ClockInServer.DATABASE_MANAGER.createOrUpdateReward(reward);
                                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.raffle_ticket.success", key, amount).formatted(Formatting.GREEN), false);
                                                            return 1;
                                                        }))
                                                )
                                                .then(literal("makeupCard")
                                                        .then(argument("amount", IntegerArgumentType.integer()).executes(context -> {
                                                            final String key = StringArgumentType.getString(context, "key");
                                                            final int amount = IntegerArgumentType.getInteger(context, "amount");
                                                            RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(key);
                                                            reward.setMakeupCards(amount);
                                                            ClockInServer.DATABASE_MANAGER.createOrUpdateReward(reward);
                                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.set.makeup_card.success", key, amount).formatted(Formatting.GREEN), false);
                                                            return 1;
                                                        }))
                                                )
                                        )
                                )
                        )
                )
        )));

    }
}
