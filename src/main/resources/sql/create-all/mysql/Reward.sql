CREATE TABLE IF NOT EXISTS rewards (
    id INT NOT NULL AUTO_INCREMENT,
    `key` VARCHAR(255) NOT NULL,
    translatable_key VARCHAR(255) NOT NULL,
    item_list_serialized TEXT,
    money DOUBLE,
    raffle_tickets INT,
    makeup_cards INT,
    PRIMARY KEY (id),
    UNIQUE KEY `key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci