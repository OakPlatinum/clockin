CREATE TABLE IF NOT EXISTS daily_clock_in_records (
    id INT NOT NULL AUTO_INCREMENT,
    date DATE,
    uuid VARCHAR(36),
    time TIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci