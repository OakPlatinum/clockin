package com.hz6826.clockin.sql.model.sqlite;

import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;

import java.sql.Date;
import java.sql.Time;

/**
 * @param rank Not in clock in record table, generated automatically by DatabaseManager.
 */
public record DailyClockInRecord(Date date, String uuid, Time time, int rank) implements DailyClockInRecordInterface {

    public static String createTableSQL() {
        return """
                CREATE TABLE IF NOT EXISTS daily_clock_in_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date DATE,
                    uuid TEXT,
                    time TIME
                );
                """;
    }
}
