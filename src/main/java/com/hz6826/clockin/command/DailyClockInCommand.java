package com.hz6826.clockin.command;

import com.hz6826.clockin.api.Util;
import com.hz6826.clockin.sql.DailyClockInRecord;
import com.hz6826.clockin.sql.DatabaseManager;
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
        DailyClockInRecord record = DatabaseManager.getDailyClockInRecordOrNull(context.getSource().getPlayerOrThrow().getUuidAsString(), date);
        if(record == null) {
            DailyClockInRecord record2 = DatabaseManager.dailyClockIn(context.getSource().getPlayerOrThrow().getUuidAsString(), date, time);
            if(record2 == null) {
                context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.failed").formatted(Formatting.RED), false);
            } else {
                // Give Daily Reward
                Text rewardText = Util.giveReward(context.getSource().getPlayerOrThrow(), "daily_reward");
                if(rewardText == null) {
                    rewardText = Text.translatable("command.clockin.reward.null");
                }
                context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success", Text.literal(String.valueOf(DatabaseManager.getPlayerDailyClockInRank(record2))).formatted(Formatting.GOLD)).formatted(Formatting.GREEN), false);
                Text finalRewardText = rewardText;
                context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.reward", finalRewardText), false);

                // Give Cumulate Reward
                int cumulateCount = DatabaseManager.getPlayerDailyClockInCount(context.getSource().getPlayerOrThrow().getUuidAsString());
                Text cumulateRewardText = Util.giveReward(context.getSource().getPlayerOrThrow(), "cumulate_reward_" + cumulateCount);
                if(cumulateRewardText != null) {
                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.cumulate.total.reward", Text.literal(String.valueOf(cumulateCount)).formatted(Formatting.GOLD)), false);
                    context.getSource().sendFeedback(() -> cumulateRewardText, false);
                }
                // Give Monthly Cumulate Reward
                int monthlyCumulateCount = DatabaseManager.getPlayerDailyClockInCount(context.getSource().getPlayerOrThrow().getUuidAsString(), LocalDate.now().getMonthValue());
                Text monthlyCumulateRewardText = Util.giveReward(context.getSource().getPlayerOrThrow(), "cumulate_reward_monthly_" + monthlyCumulateCount);
                if(monthlyCumulateRewardText != null) {
                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.cumulate.monthly.reward", Text.literal(String.valueOf(monthlyCumulateCount)).formatted(Formatting.GOLD)), false);
                    context.getSource().sendFeedback(() -> monthlyCumulateRewardText, false);
                }
                Text specificDateRewardText = Util.giveReward(context.getSource().getPlayerOrThrow(), "daily_reward_" + date.toString().replace("-", ""));
                if(specificDateRewardText != null) {
                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.specific.date.reward", Text.literal(date.toString().replace("-", "")).formatted(Formatting.GOLD)), false);
                    context.getSource().sendFeedback(() -> specificDateRewardText, false);
                }
                // Broadcast Daily Clock-in
                Text message = Text.translatable("command.clockin.dailyclockin.success.broadcast", context.getSource().getPlayerOrThrow().getName(), Text.literal(String.valueOf(DatabaseManager.getPlayerDailyClockInRank(record2))).formatted(Formatting.GOLD)).formatted(Formatting.GREEN);
                context.getSource().getServer().getPlayerManager().broadcast(message, false);
            }
        } else {
            context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.already", Text.literal(String.valueOf(DatabaseManager.getPlayerDailyClockInRank(record))).formatted(Formatting.GOLD), Text.literal(record.getTime().toLocalTime().format(dateTimeFormatter)).formatted(Formatting.BLUE)), false);
        }
    }
}
