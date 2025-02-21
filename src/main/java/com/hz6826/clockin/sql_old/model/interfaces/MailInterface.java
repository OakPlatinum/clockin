package com.hz6826.clockin.sql_old.model.interfaces;

import java.sql.Timestamp;

public interface MailInterface {
    int getId();

    String getSenderUuid();

    String getReceiverUuid();

    Timestamp getSendTime();

    String getContent();

    String getSerializedAttachment();

    boolean getRead();

    boolean getAttachmentFetched();
}
