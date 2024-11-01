package com.hz6826.clockin.sql.model.interfaces;

import java.util.Date;

public interface MailInterface {
    String getSenderUuid();

    String getReceiverUuid();

    Date getSendTime();

    String getContent();

    String getSerializedAttachment();

    boolean getRead();

    boolean getAttachmentFetched();
}
