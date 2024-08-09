package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class UtilsCommand {
    public static void showMainHandItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getServer().sendMessage(FabricUtils.generateItemStackComponent(context.getSource().getPlayerOrThrow().getMainHandStack()));
    }
}
