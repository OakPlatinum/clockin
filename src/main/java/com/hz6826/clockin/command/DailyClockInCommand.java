package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DailyClockInCommand {
    public static void dailyClockIn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Date date = Date.valueOf(LocalDate.now());
        Time time = Time.valueOf(LocalTime.now());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DailyClockInRecordInterface dailyClockInRecord = ClockInServer.DATABASE_MANAGER.getDailyClockInRecordOrNull(context.getSource().getPlayerOrThrow().getUuidAsString(), date);
        if(dailyClockInRecord == null) {
            ClockInServer.DATABASE_MANAGER.dailyClockIn(context.getSource().getPlayerOrThrow().getUuidAsString(), date, time);
            dailyClockInRecord = ClockInServer.DATABASE_MANAGER.getDailyClockInRecordOrNull(context.getSource().getPlayerOrThrow().getUuidAsString(), date);
            if(dailyClockInRecord == null) {
                context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.failed").formatted(Formatting.RED), false);
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
                // Broadcast Daily Clock-in
                Text message = Text.translatable("command.clockin.dailyclockin.success.broadcast", context.getSource().getPlayerOrThrow().getName(), Text.literal(String.valueOf(finalDailyClockInRecord.getRank())).formatted(Formatting.GOLD)).formatted(Formatting.GREEN);
                context.getSource().getServer().getPlayerManager().broadcast(message, false);
            }
        } else {
            DailyClockInRecordInterface finalDailyClockInRecord1 = dailyClockInRecord;
            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.already", Text.literal(String.valueOf(finalDailyClockInRecord1.getRank())).formatted(Formatting.GOLD), Text.literal(finalDailyClockInRecord1.getTime().toLocalTime().format(dateTimeFormatter)).formatted(Formatting.BLUE)), false);
        }
    }
}
