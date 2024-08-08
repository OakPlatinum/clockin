package com.hz6826.clockin.sql;

import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.hz6826.clockin.sql.model.mysql.User;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.sql.*;
import java.util.List;

@Environment(EnvType.SERVER)
public interface DatabaseManager {
    boolean ping();
    void createTables();
    void dropTables();

    // User methods
    User getOrCreateUser(String uuid, String playerName);

    User getUserByUUID(String uuid);

    User getUserByName(String playerName);

    void updateUser(UserWithAccountAbstract user);

    List<UserWithAccountAbstract> getUsersSortedByBalance();

    List<UserWithAccountAbstract> getUsersSortedByRaffleTicket();

    DailyClockInRecordInterface getDailyClockInRecordOrNull(String uuid, Date date);

    void deleteDailyClockInRecord(DailyClockInRecordInterface record);

    void dailyClockIn(String uuid, Date date, Time time);

    List<DailyClockInRecordInterface> getDailyClockInRecords(Date date);

    int getPlayerDailyClockInCount(String uuid);

    int getPlayerDailyClockInCount(String uuid, int month);

    int getPlayerDailyClockInCount(String uuid, Date start, Date end);

    Connection getConn() throws SQLException;

    void executeUpdate(String sql);

    ResultSet executeQuery(PreparedStatement preparedStatement);

    // Reward methods
    // public static @NotNull String createTableSQL() {
    //        return "CREATE TABLE IF NOT EXISTS rewards (" +
    //                "id INT NOT NULL AUTO_INCREMENT," +
    //                "key VARCHAR(255) NOT NULL," +
    //                "translatable_key VARCHAR(255) NOT NULL," +
    //                "item_list_serialized TEXT," +
    //                "money DOUBLE," +
    //                "raffle_tickets INT," +
    //                "makeup_cards INT," +
    //                "PRIMARY KEY (id)" +
    //                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
    //    }
    RewardInterface getRewardOrNew(String key);

    RewardInterface createOrUpdateReward(RewardInterface reward);
}
