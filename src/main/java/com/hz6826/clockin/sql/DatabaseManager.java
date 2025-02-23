package com.hz6826.clockin.sql;

import io.ebean.DB;
import io.ebean.annotation.Transactional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@SuppressWarnings("unused")  // TODO: WILL BE DELETED
@Environment(EnvType.SERVER)
public class DatabaseManager {
    String SERVER_UUID = "00000000-0000-0000-0000-000000000000";

    @Transactional
    public static User getOrCreateUser(String uuid, String playerName) {
        User user = getUserByUUID(uuid);
        if (user == null) {
            user = new User(uuid, playerName, 0, 0, 0);
            user.save();
        }
        return user;
    }

    @Transactional
    public static User getUserByUUID(String uuid) {
        return DB.createQuery(User.class).where().eq("uuid", uuid).findOne();
    }

    @Transactional
    public static User getUserByName(String playerName) {
        return DB.createQuery(User.class).where().eq("player_name", playerName).findOne();
    }

    @Transactional
    @Deprecated
    public static void updateUser(User user) {
        user.update();
    }

    @Transactional
    public static List<User> getUsersSortedByBalance() {
        return DB.createQuery(User.class).orderBy("balance DESC").findList();
    }

    @Transactional
    public static List<User> getUsersSortedByRaffleTicket() {
        return DB.createQuery(User.class).orderBy("raffle_ticket DESC").findList();
    }

    @Transactional
    public static DailyClockInRecord getDailyClockInRecordOrNull(String uuid, Date date) {
        return DB.createQuery(DailyClockInRecord.class).where().eq("uuid", uuid).and().eq("date", date).findOne();
    }

    @Transactional
    @Deprecated
    public static boolean deleteDailyClockInRecord(DailyClockInRecord record) {
        return record.delete();
    }

    @Transactional
    public static int dailyClockIn(String uuid, Date date, Time time) {
        if (!DB.createQuery(DailyClockInRecord.class).where().eq("uuid", uuid).and().eq("date", date).exists()) {
            return -1;
        } else {
            DailyClockInRecord record = new DailyClockInRecord(date, uuid, time);
            record.save();
            return 0;
        }
    }

    @Transactional
    public static List<DailyClockInRecord> getDailyClockInRecords(Date date) {
        return DB.createQuery(DailyClockInRecord.class).where().eq("date", date).findList();
    }

    @Transactional
    public static int getPlayerDailyClockInCount(String uuid) {
        return DB.createQuery(DailyClockInRecord.class).where().eq("uuid", uuid).findCount();
    }

    @Transactional
    public static int getPlayerDailyClockInCount(String uuid, int month) {
        return DB.createQuery(DailyClockInRecord.class).where().eq("uuid", uuid).raw("month(date) = ?", month).findCount();
    }

    // TODO: WORK IN PROGRESS


}
