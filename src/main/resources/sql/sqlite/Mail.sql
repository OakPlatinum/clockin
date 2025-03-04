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
);