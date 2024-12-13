package com.hz6826.clockin.command;

import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.server.ClockInServer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

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
        var player = context.getSource().getPlayerOrThrow();
        int mailId = context.getArgument("mail_id", Integer.class);
        var mail = ClockInServer.DBM.getMailById(mailId);
        if (mail == null || !mail.getReceiverUuid().equals(player.getUuidAsString())) {
            player.sendMessage(Text.translatable("command.clockin.mail.invalid_mail_id").formatted(Formatting.RED), false);

        } else {
            String serializedAttachment = mail.getSerializedAttachment();
            if (serializedAttachment == null || serializedAttachment.isBlank()) {
                player.sendMessage(Text.translatable("command.clockin.mail.no_attachment").formatted(Formatting.RED), false);
            }
            else if (mail.getAttachmentFetched()) {
                player.sendMessage(Text.translatable("command.clockin.mail.attachment_already_fetched").formatted(Formatting.RED), false);
            } else {
                List<ItemStack> itemList = FabricUtils.deserializeItemStackList(serializedAttachment);
                if (player.getInventory().getEmptySlot() == -1 || FabricUtils.giveItemList(itemList, player, false)) {
                    player.sendMessage(Text.translatable("command.clockin.mail.no_slot_for_attachment").formatted(Formatting.RED));
                } else {
                    ClockInServer.DBM.setAttachmentFetched(mail);
                    player.sendMessage(Text.translatable("command.clockin.mail.attachment_fetched").formatted(Formatting.GREEN), false);
                }
            }
        }
        // TODO
    }
}
