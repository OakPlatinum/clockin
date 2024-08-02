package com.hz6826.clockin.sql;

import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.hz6826.clockin.sql.model.mysql.DailyClockInRecord;
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

    void updateUser(User user);

    List<UserWithAccountAbstract> getUsersSortedByBalance();

    List<UserWithAccountAbstract> getUsersSortedByRaffleTicket();

    DailyClockInRecordInterface getDailyClockInRecordOrNull(String uuid, Date date);

    void deleteDailyClockInRecord(DailyClockInRecord record);

    void dailyClockIn(String uuid, Date date, Time time);

    Connection getConn() throws SQLException;

    void executeUpdate(String sql);

    ResultSet executeQuery(PreparedStatement preparedStatement);
}
