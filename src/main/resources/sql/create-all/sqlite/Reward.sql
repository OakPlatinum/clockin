CREATE TABLE IF NOT EXISTS rewards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    [key] TEXT NOT NULL,
    translatable_key TEXT NOT NULL,
    item_list_serialized TEXT,
    money REAL,
    raffle_tickets INTEGER,
    makeup_cards INTEGER,
    UNIQUE ("key")
);