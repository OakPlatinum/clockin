package com.hz6826.clockin.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import com.hz6826.clockin.api.FabricUtils;

import java.time.LocalDate;

public class InfoCommand {
    public static void showInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().getPlayerOrThrow();
        int year, month;
        try {
            year = context.getArgument("year", Integer.class);
            month = context.getArgument("month", Integer.class);
        } catch (IllegalArgumentException e) {
            // If not provided, set to current date
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }
        player.sendMessage(FabricUtils.generateCalendarMonthTitle(year, month));
    }
}
