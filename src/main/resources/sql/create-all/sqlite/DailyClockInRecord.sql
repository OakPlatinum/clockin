CREATE TABLE IF NOT EXISTS daily_clock_in_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date DATE,
    uuid TEXT,
    time TIME
);