package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.server.ClockInServer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MailCommand {
    public static final int PAGE_SIZE = 5;

    public static void sendMail(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        // TODO: Implement mail sending
    }

    public static void getMails(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        var player = context.getSource().getPlayerOrThrow();
        int mailCount = ClockInServer.DBM.getMailCount(player.getUuidAsString());
        if (mailCount == 0) {
            player.sendMessage(Text.translatable("command.clockin.mail.no_mail").formatted(Formatting.GRAY), false);
            return;
        }
        int pageCount = (int) Math.ceil((double) mailCount / PAGE_SIZE);
        var mails = ClockInServer.DBM.getMails(player.getUuidAsString(), 1, PAGE_SIZE);
        FabricUtils.displayMailListTitle(player);
        FabricUtils.displayMailList(player, mails);
        FabricUtils.displayMailListBottomBar(player, 1, pageCount);
    }

    public static void getMailsWithPage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        var player = context.getSource().getPlayerOrThrow();
        int mailCount = ClockInServer.DBM.getMailCount(player.getUuidAsString());
        if (mailCount == 0) {
            player.sendMessage(Text.translatable("command.clockin.mail.no_mail").formatted(Formatting.GRAY), false);
            return;
        }
        int pageCount = (int) Math.ceil((double) mailCount / PAGE_SIZE);
        int page = context.getArgument("page", Integer.class);
        if (page < 1 || page > pageCount) {
            player.sendMessage(Text.translatable("command.clockin.mail.invalid_page").formatted(Formatting.RED), false);
            return;
        }
        var mails = ClockInServer.DBM.getMails(player.getUuidAsString(), page, PAGE_SIZE);
        FabricUtils.displayMailListTitle(player);
        FabricUtils.displayMailList(player, mails);
        FabricUtils.displayMailListBottomBar(player, page, pageCount);
    }

    public static void getAttachment(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {

    }
}
