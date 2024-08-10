package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class UtilsCommand {
    public static void showMainHandItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(EntityType.PLAYER, context.getSource().getPlayerOrThrow().getUuid(), context.getSource().getPlayerOrThrow().getName()));
        context.getSource().getServer().sendMessage(Text.translatable("", context.getSource().getPlayerOrThrow().getName().getWithStyle(Style.EMPTY.withBold(true).withHoverEvent(hoverEvent)), FabricUtils.generateItemStackComponent(context.getSource().getPlayerOrThrow().getMainHandStack())));
    }
}
