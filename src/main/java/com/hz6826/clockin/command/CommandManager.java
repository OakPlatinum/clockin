package com.hz6826.clockin.command;

import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.*;

public class CommandManager {
    public CommandManager() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(literal("clockin")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.clockin.loading"), false);
                    return 1;
                })
                .then(literal("clockin")
                        .executes(context -> {
                            if(context.getSource().isExecutedByPlayer()){
                                Date date = Date.valueOf(LocalDate.now());
                                Time time = Time.valueOf(LocalTime.now());
                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Text.translatable("command.clockin.formatter.time").toString());
                                DailyClockInRecordInterface dailyClockInRecord = ClockInServer.DATABASE_MANAGER.getDailyClockInRecordOrNull(Objects.requireNonNull(context.getSource().getPlayer()).getUuidAsString(), date);
                                if(dailyClockInRecord == null) {
                                    ClockInServer.DATABASE_MANAGER.dailyClockIn(context.getSource().getPlayer().getUuidAsString(), date, time);
                                    dailyClockInRecord = ClockInServer.DATABASE_MANAGER.getDailyClockInRecordOrNull(context.getSource().getPlayer().getUuidAsString(), date);
                                    if(dailyClockInRecord == null) {
                                        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.failed").formatted(Formatting.RED), false);
                                        return -1;
                                    } else {
                                        DailyClockInRecordInterface finalDailyClockInRecord = dailyClockInRecord;
                                        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success", Text.literal(String.valueOf(finalDailyClockInRecord.getRank())).formatted(Formatting.GOLD)).formatted(Formatting.GREEN), false);
                                        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.success.reward", ""), false); // TODO: Add reward
                                    }
                                } else {
                                    DailyClockInRecordInterface finalDailyClockInRecord1 = dailyClockInRecord;
                                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.dailyclockin.already", Text.literal(String.valueOf(finalDailyClockInRecord1.getRank())).formatted(Formatting.GOLD), Text.literal(finalDailyClockInRecord1.getTime().toLocalTime().format(dateTimeFormatter)).formatted(Formatting.BLUE)), false);
                                }
                            }
                            return 1;
                        }))
        )));

    }
}
