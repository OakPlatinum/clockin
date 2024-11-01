package com.hz6826.clockin.sql.model.mysql;

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
                    id INT NOT NULL AUTO_INCREMENT,
                    date DATE,
                    uuid VARCHAR(36),
                    time TIME,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
    }
}
