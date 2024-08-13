package com.hz6826.clockin.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class WIPCommand {
    public static void WIP(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.error.work_in_progress"), false);
    }
}
