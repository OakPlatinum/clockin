package com.hz6826.clockin.sql;

import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.sql.*;
import java.util.List;

@Environment(EnvType.SERVER)
public interface DatabaseManager {
    void createTables();
    void dropTables();

    // User methods
    UserWithAccountAbstract getOrCreateUser(String uuid, String playerName);

    UserWithAccountAbstract getUserByUUID(String uuid);

    UserWithAccountAbstract getUserByName(String playerName);

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

    RewardInterface getRewardOrNew(String key);

    RewardInterface createOrUpdateReward(RewardInterface reward);
}
