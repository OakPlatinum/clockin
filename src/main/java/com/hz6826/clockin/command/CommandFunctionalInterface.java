package com.hz6826.clockin.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
public interface CommandFunctionalInterface {
    void execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
}
