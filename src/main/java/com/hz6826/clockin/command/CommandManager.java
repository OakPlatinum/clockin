package com.hz6826.clockin.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;

public class CommandManager {
    public CommandManager() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(literal("clockin")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.translatable("command.clockin.clockin.loading"), false);

                    return 1;
                }))));

    }
}
