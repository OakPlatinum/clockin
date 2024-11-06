package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.server.ClockInServer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class MailCommand {
    public static final int PAGE_SIZE = 10;

    public static void sendMail(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        // TODO: Implement mail sending
    }

    public static void getMails(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        var player = context.getSource().getPlayerOrThrow();
        var mails = ClockInServer.DBM.getMails(player.getUuidAsString(), 1, PAGE_SIZE);
        FabricUtils.displayMailList(player, mails);
    }

    public static void getMailsWithPage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {

    }

    public static void getAttachment(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {

    }
}
