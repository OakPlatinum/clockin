package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class UtilsCommand {
    public static void showMainHandItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getServer().getPlayerManager().broadcast(Text.translatable("command.clockin.utils.show_item", context.getSource().getPlayerOrThrow().getDisplayName(), FabricUtils.generateItemStackComponent(context.getSource().getPlayerOrThrow().getMainHandStack())).formatted(Formatting.AQUA), false);
    }
}
