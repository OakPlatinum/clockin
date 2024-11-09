package com.hz6826.clockin.sql.model.sqlite;

import com.hz6826.clockin.sql.model.interfaces.MailInterface;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public class Mail implements MailInterface {
    private final String senderUuid;  // If admin, senderUuid is 00000000-0000-0000-0000-000000000000
    private final String receiverUuid;
    private final Timestamp sendTime;
    private final String content;
    private final String serializedAttachment;
    private final boolean isRead;
    private final boolean isAttachmentFetched;

    public Mail(String senderUuid, String receiverUuid, Timestamp sendTime, String content, String serializedAttachment, boolean isRead, boolean isAttachmentFetched) {
        this.senderUuid = senderUuid;
        this.receiverUuid = receiverUuid;
        this.sendTime = sendTime;
        this.content = content;
        this.serializedAttachment = serializedAttachment;
        this.isRead = isRead;
        this.isAttachmentFetched = isAttachmentFetched;
    }

    @Override
    public String getSenderUuid() {
        return senderUuid;
    }

    @Override
    public String getReceiverUuid() {
        return receiverUuid;
    }

    @Override
    public Timestamp getSendTime() {
        return sendTime;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getSerializedAttachment() {
        return serializedAttachment;
    }

    @Override
    public boolean getRead() {
        return isRead;
    }

    @Override
    public boolean getAttachmentFetched() {
        return isAttachmentFetched;
    }

    @Contract(pure = true)
    public static @NotNull String createTableSQL() {
        return """
                CREATE TABLE IF NOT EXISTS mails (
                    id INT NOT NULL AUTO_INCREMENT,
                    sender_uuid VARCHAR(36) NOT NULL,
                    receiver_uuid VARCHAR(36) NOT NULL,
                    send_time DATETIME NOT NULL,
                    content TEXT,
                    serialized_attachment TEXT,
                    is_read BOOLEAN NOT NULL DEFAULT false,
                    is_attachment_fetched BOOLEAN NOT NULL DEFAULT false,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
    }
}
