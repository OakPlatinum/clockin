package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
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
                                        // Give Daily Reward
                                        DailyClockInRecordInterface finalDailyClockInRecord = dailyClockInRecord;
                                        RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew("daily_reward");
                                        Text rewardText = Text.translatable("command.clockin.reward.null");
                                        if(!reward.isNew()) {
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
                                        // Give Cumulate Reward
                                        int cumulateCount = ClockInServer.DATABASE_MANAGER.getPlayerDailyClockInCount(context.getSource().getPlayerOrThrow().getUuidAsString());
                                        RewardInterface cumulateReward = ClockInServer.DATABASE_MANAGER.getRewardOrNew("cumulate_reward_" + cumulateCount);
                                        if(!cumulateReward.isNew()) {
                                            FabricUtils.giveItemList(FabricUtils.deserializeItemStackList(cumulateReward.getItemListSerialized()), context.getSource().getPlayerOrThrow());
                                            UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(context.getSource().getPlayerOrThrow().getUuidAsString());
                                            user.addBalance(cumulateReward.getMoney());
                                            user.addRaffleTicket(cumulateReward.getRaffleTickets());
                                            user.addMakeupCard(cumulateReward.getMakeupCards());
                                            final Text cumulateRewardText = FabricUtils.generateReadableReward(cumulateReward);
                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.cumulate.total.reward", Text.literal(String.valueOf(cumulateCount)).formatted(Formatting.GOLD)), false);
                                            context.getSource().sendFeedback(() -> cumulateRewardText, false);
                                        }
                                        // Give Monthly Cumulate Reward
                                        int monthlyCumulateCount = ClockInServer.DATABASE_MANAGER.getPlayerDailyClockInCount(context.getSource().getPlayerOrThrow().getUuidAsString(), LocalDate.now().getMonthValue());
                                        RewardInterface monthlyCumulateReward = ClockInServer.DATABASE_MANAGER.getRewardOrNew("cumulate_reward_monthly_" + monthlyCumulateCount);
                                        if(!monthlyCumulateReward.isNew()) {
                                            FabricUtils.giveItemList(FabricUtils.deserializeItemStackList(monthlyCumulateReward.getItemListSerialized()), context.getSource().getPlayerOrThrow());
                                            UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(context.getSource().getPlayerOrThrow().getUuidAsString());
                                            user.addBalance(monthlyCumulateReward.getMoney());
                                            user.addRaffleTicket(monthlyCumulateReward.getRaffleTickets());
                                            user.addMakeupCard(monthlyCumulateReward.getMakeupCards());
                                            final Text monthlyCumulateRewardText = FabricUtils.generateReadableReward(monthlyCumulateReward);
                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.cumulate.total.reward", Text.literal(String.valueOf(monthlyCumulateCount)).formatted(Formatting.GOLD)), false);
                                            context.getSource().sendFeedback(() -> monthlyCumulateRewardText, false);
                                        }
                                    }
                                } else {
                                    DailyClockInRecordInterface finalDailyClockInRecord1 = dailyClockInRecord;
                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.already", Text.literal(String.valueOf(finalDailyClockInRecord1.getRank())).formatted(Formatting.GOLD), Text.literal(finalDailyClockInRecord1.getTime().toLocalTime().format(dateTimeFormatter)).formatted(Formatting.BLUE)), false);
                                }
                            }
                            return 1;
                        }))
                .then(literal("calendar")
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("还在加紧制作中，敬请期待！").formatted(Formatting.RED), false);
                            return 1;
                        })
                )
                .then(literal("admin")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("reward")
                                .then(literal("get")
                                        .then(argument("key", StringArgumentType.string()).executes(context -> {
                                            final String key = StringArgumentType.getString(context, "key");
                                            RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(key);
                                            if(reward.isNew()){
                                                context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.get.null", key).formatted(Formatting.RED), false);
                                            }else{
                                                Text rewardText = FabricUtils.generateReadableReward(reward);
                                                context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.get.success", key).formatted(Formatting.GREEN), false);
                                                context.getSource().sendFeedback(() -> rewardText, false);
                                            }
                                            return 1;
                                        }))
                                )
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
                                                        .then(argument("amount", DoubleArgumentType.doubleArg()).executes(context -> {
                                                            final String key = StringArgumentType.getString(context, "key");
                                                            final double amount = DoubleArgumentType.getDouble(context, "amount");
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
                                .then(literal("give")
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(argument("key", StringArgumentType.string()).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final String key = StringArgumentType.getString(context, "key");
                                                    RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(key);
                                                    if(reward.isNew()){
                                                        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.get.null", key, player.getName()).formatted(Formatting.RED), false);
                                                    }else{
                                                        FabricUtils.giveItemList(FabricUtils.deserializeItemStackList(reward.getItemListSerialized()), player);
                                                        UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                        user.addBalance(reward.getMoney());
                                                        user.addRaffleTicket(reward.getRaffleTickets());
                                                        user.addMakeupCard(reward.getMakeupCards());
                                                        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.reward.give.success", player.getName(), key).formatted(Formatting.GREEN), false);
                                                    }
                                                    return 1;
                                                }))
                                        )
                                )
                        )
                        .then(literal("money")
                                .then(literal("get")
                                        .then(argument("player", EntityArgumentType.player()).executes(context -> {
                                            final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                            final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.get.money", player.getName(), user.getBalance(), CURRENCY_NAME).formatted(Formatting.GREEN), false);
                                            return 1;
                                        }))
                                )
                                .then(literal("set")
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(argument("amount", DoubleArgumentType.doubleArg()).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                    user.setBalance(DoubleArgumentType.getDouble(context, "amount"));
                                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.set.money.success", player.getName(), DoubleArgumentType.getDouble(context, "amount"), CURRENCY_NAME).formatted(Formatting.GREEN), false);
                                                    return 1;
                                                }))
                                        )
                                )
                                .then(literal("give")
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                    user.addBalance(DoubleArgumentType.getDouble(context, "amount"));
                                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.give.money.success", DoubleArgumentType.getDouble(context, "amount"), CURRENCY_NAME, player.getName()).formatted(Formatting.GREEN), false);
                                                    return 1;
                                                }))
                                        )
                                )
                                .then(literal("take")
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                    user.subtractBalance(DoubleArgumentType.getDouble(context, "amount"));
                                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.take.money.success", DoubleArgumentType.getDouble(context, "amount"), CURRENCY_NAME, player.getName()).formatted(Formatting.GREEN), false);
                                                    return 1;
                                                }))
                                        )
                                )
                        )
                        .then(literal("raffleTicket")
                                .then(literal("get")
                                        .then(argument("player", EntityArgumentType.player()).executes(context -> {
                                            final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                            final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.get.raffle_ticket", player.getName(), user.getRaffleTicket()).formatted(Formatting.GREEN), false);
                                            return 1;
                                        }))
                                )
                                .then(literal("set")
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                    user.setRaffleTicket(IntegerArgumentType.getInteger(context, "amount"));
                                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.set.raffle_ticket.success", player.getName(), IntegerArgumentType.getInteger(context, "amount")).formatted(Formatting.GREEN), false);
                                                    return 1;
                                                }))
                                        )
                                )
                                .then(literal("give")
                                         .then(argument("player", EntityArgumentType.player())
                                                .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                    user.addRaffleTicket(IntegerArgumentType.getInteger(context, "amount"));
                                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.give.raffle_ticket.success", IntegerArgumentType.getInteger(context, "amount"), player.getName()).formatted(Formatting.GREEN), false);
                                                    return 1;
                                                }))
                                         )
                                )
                                .then(literal("take")
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                    user.removeRaffleTicket(IntegerArgumentType.getInteger(context, "amount"));
                                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.take.raffle_ticket.success", IntegerArgumentType.getInteger(context, "amount"), player.getName()).formatted(Formatting.GREEN), false);
                                                    return 1;
                                                }))
                                        )
                                )
                        )
                        .then(literal("makeupCard")
                                .then(literal("get")
                                        .then(argument("player", EntityArgumentType.player()).executes(context -> {
                                            final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                            final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.get.makeup_card", player.getName(), user.getMakeupCard()).formatted(Formatting.GREEN), false);
                                            return 1;
                                        }))
                                )
                                .then(literal("set")
                                         .then(argument("player", EntityArgumentType.player())
                                                .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                    user.setMakeupCard(IntegerArgumentType.getInteger(context, "amount"));
                                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.set.makeup_card.success", player.getName(), IntegerArgumentType.getInteger(context, "amount")).formatted(Formatting.GREEN), false);
                                                    return 1;
                                                }))
                                         )
                                )
                                .then(literal("give")
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                    user.addMakeupCard(IntegerArgumentType.getInteger(context, "amount"));
                                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.give.makeup_card.success", IntegerArgumentType.getInteger(context, "amount"), player.getName()).formatted(Formatting.GREEN), false);
                                                    return 1;
                                                }))
                                        )
                                )
                                .then(literal("take")
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> {
                                                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    final UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
                                                    user.removeMakeupCard(IntegerArgumentType.getInteger(context, "amount"));
                                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.player.take.makeup_card.success", IntegerArgumentType.getInteger(context, "amount"), player.getName()).formatted(Formatting.GREEN), false);
                                                    return 1;
                                                }))
                                        )
                                )
                        )
                )
        )));

    }
}
