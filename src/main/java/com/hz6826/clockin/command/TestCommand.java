package com.hz6826.clockin.command;

import com.hz6826.clockin.sql.User;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class TestCommand {
    public static int test(CommandContext<ServerCommandSource> context) {
        User user = new User("00000000-0000-0000-0000-000000000000", "123", 0, 0, 0);
        user.save();
        return 1;
    }
}
