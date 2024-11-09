package com.hz6826.clockin.sql.model.interfaces;

import java.sql.Timestamp;

public interface MailInterface {
    String getSenderUuid();

    String getReceiverUuid();

    Timestamp getSendTime();

    String getContent();

    String getSerializedAttachment();

    boolean getRead();

    boolean getAttachmentFetched();
}
