package com.hz6826.clockin.sql;

import io.ebean.Model;
import io.ebean.annotation.DbMigration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "mails")
@Getter
@Setter
public class Mail extends Model {
    @Id
    private int id;
    @Column(length = 36, nullable = false)
    private String senderUuid;
    @Column(length = 36, nullable = false)
    private String receiverUuid;
    @Column(nullable = false)
    private Timestamp sendTime;
    @Column(length = 65535)
    private String content;
    @Column(length = 65535)
    private String serializedAttachment;
    @Column(nullable = false)
    private boolean isRead;
    @Column(nullable = false)
    private boolean isAttachmentFetched;

    public Mail(int id, String senderUuid, String receiverUuid, Timestamp sendTime, String content, String serializedAttachment, boolean isRead, boolean isAttachmentFetched) {
        this.id = id;
        this.senderUuid = senderUuid;
        this.receiverUuid = receiverUuid;
        this.sendTime = sendTime;
        this.content = content;
        this.serializedAttachment = serializedAttachment;
        this.isRead = isRead;
        this.isAttachmentFetched = isAttachmentFetched;
    }
}
