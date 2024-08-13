package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
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
                Text rewardText = FabricUtils.giveReward(context.getSource().getPlayerOrThrow(), "daily_reward");
                if(rewardText == null) {
                    rewardText = Text.translatable("command.clockin.reward.null");
                }
                context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success", Text.literal(String.valueOf(finalDailyClockInRecord.rank())).formatted(Formatting.GOLD)).formatted(Formatting.GREEN), false);
                Text finalRewardText = rewardText;
                context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.reward", finalRewardText), false);

                // Give Cumulate Reward
                int cumulateCount = ClockInServer.DATABASE_MANAGER.getPlayerDailyClockInCount(context.getSource().getPlayerOrThrow().getUuidAsString());
                Text cumulateRewardText = FabricUtils.giveReward(context.getSource().getPlayerOrThrow(), "cumulate_reward_" + cumulateCount);
                if(cumulateRewardText != null) {
                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.cumulate.total.reward", Text.literal(String.valueOf(cumulateCount)).formatted(Formatting.GOLD)), false);
                    context.getSource().sendFeedback(() -> cumulateRewardText, false);
                }
                // Give Monthly Cumulate Reward
                int monthlyCumulateCount = ClockInServer.DATABASE_MANAGER.getPlayerDailyClockInCount(context.getSource().getPlayerOrThrow().getUuidAsString(), LocalDate.now().getMonthValue());
                Text monthlyCumulateRewardText = FabricUtils.giveReward(context.getSource().getPlayerOrThrow(), "cumulate_reward_monthly_" + monthlyCumulateCount);
                if(monthlyCumulateRewardText != null) {
                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.cumulate.monthly.reward", Text.literal(String.valueOf(monthlyCumulateCount)).formatted(Formatting.GOLD)), false);
                    context.getSource().sendFeedback(() -> monthlyCumulateRewardText, false);
                }
                Text specificDateRewardText = FabricUtils.giveReward(context.getSource().getPlayerOrThrow(), "daily_reward_" + date.toString().replace("-", ""));
                if(specificDateRewardText != null) {
                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.specific.date.reward", Text.literal(date.toString().replace("-", "")).formatted(Formatting.GOLD)), false);
                    context.getSource().sendFeedback(() -> specificDateRewardText, false);
                }
                // Broadcast Daily Clock-in
                Text message = Text.translatable("command.clockin.dailyclockin.success.broadcast", context.getSource().getPlayerOrThrow().getName(), Text.literal(String.valueOf(finalDailyClockInRecord.rank())).formatted(Formatting.GOLD)).formatted(Formatting.GREEN);
                context.getSource().getServer().getPlayerManager().broadcast(message, false);
            }
        } else {
            DailyClockInRecordInterface finalDailyClockInRecord1 = dailyClockInRecord;
            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.already", Text.literal(String.valueOf(finalDailyClockInRecord1.rank())).formatted(Formatting.GOLD), Text.literal(finalDailyClockInRecord1.time().toLocalTime().format(dateTimeFormatter)).formatted(Formatting.BLUE)), false);
        }
    }
}
