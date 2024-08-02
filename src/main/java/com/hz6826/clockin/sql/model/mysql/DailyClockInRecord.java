package com.hz6826.clockin.sql.model.mysql;

import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.sql.Date;
import java.sql.Time;

@Environment(EnvType.SERVER)
public class DailyClockInRecord implements DailyClockInRecordInterface {
    private final Date date;
    private final String uuid;
    private final Time time;

    private final int rank; // Not in clock in record table, generated automatically by DatabaseManager.

    public DailyClockInRecord(Date date, String uuid, Time time, int rank){
        this.date = date;
        this.uuid = uuid;
        this.time = time;
        this.rank = rank;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public Time getTime() {
        return time;
    }

    @Override
    public int getRank() {
        return rank;
    }

    public static String createTableSQL() {
        return "CREATE TABLE IF NOT EXISTS daily_clock_in_records (" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "date DATE, " +
                "uuid VARCHAR(36), " +
                "time TIME," +
                "PRIMARY KEY (id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
    }
}
