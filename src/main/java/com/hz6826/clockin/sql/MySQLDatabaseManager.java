package com.hz6826.clockin.sql;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.hz6826.clockin.sql.model.mysql.DailyClockInRecord;
import com.hz6826.clockin.sql.model.mysql.User;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class MySQLDatabaseManager implements DatabaseManager{
    private final String url;
    private final String username;
    private final String password;

    public MySQLDatabaseManager() {
        String host = BasicConfig.getConfig().getMysqlHost();
        int port = BasicConfig.getConfig().getMysqlPort();
        String database = BasicConfig.getConfig().getMysqlDatabase();
        String useSSL = BasicConfig.getConfig().getMysqlUseSSL();
        url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL;
        username = BasicConfig.getConfig().getMysqlUsername();
        password = BasicConfig.getConfig().getMysqlPassword();
        try {
            Connection conn = getConn();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean ping() {
        // TODO: ping method
        return true;
    }

    @Override
    public void createTables() {
        executeUpdate(User.createTableSQL());
        executeUpdate(DailyClockInRecord.createTableSQL());
    }

    @Override
    public void dropTables() {
        executeUpdate("DROP TABLE IF EXISTS users");
        executeUpdate("DROP TABLE IF EXISTS daily_clock_in_records");
    }

    @Override
    public void executeUpdate(String sql) {
        try {
            Statement stmt = getConn().createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to execute update: " + e.getMessage());
        }
    }

    @Override
    public ResultSet executeQuery(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to execute query: " + e.getMessage());
            return null;
        }
    }

    // User methods
    @Override
    public User getOrCreateUser(String uuid, String playerName) {
        User user = getUserByUUID(uuid);
        if (user == null) {
            user = new User(uuid, playerName, 0, 0, 0);
            this.updateUser(user);  // TODO
        } else if (!Objects.equals(user.getPlayerName(), playerName)) {
            user.setPlayerName(playerName);
        }
        return user;
    }

    @Override
    public User getUserByUUID(String uuid){
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM users WHERE uuid =?")) {
            preparedStatement.setString(1, uuid);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                ClockIn.LOGGER.warn(uuid);
                return new User(rs.getString("uuid"), rs.getString("player_name"), rs.getDouble("balance"), rs.getInt("raffle_ticket"), rs.getInt("makeup_card"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get user by UUID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User getUserByName(String playerName) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM users WHERE player_name =?")) {
            preparedStatement.setString(1, playerName);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("uuid"), rs.getString("player_name"), rs.getDouble("balance"), rs.getInt("raffle_ticket"), rs.getInt("makeup_card"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get user by name: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateUser(User user) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("UPDATE users SET player_name =?, balance =?, raffle_ticket =? WHERE uuid =?")) {
            preparedStatement.setString(1, user.getPlayerName());
            preparedStatement.setDouble(2, user.getBalance());
            preparedStatement.setInt(3, user.getRaffleTicket());
            preparedStatement.setString(4, user.getUuid());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to update user: " + e.getMessage());
        }
    }

    @Override
    public List<UserWithAccountAbstract> getUsersSortedByBalance() {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM users ORDER BY balance DESC")) {
            ResultSet rs = preparedStatement.executeQuery();
            List<UserWithAccountAbstract> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(rs.getString("uuid"), rs.getString("player_name"), rs.getDouble("balance"), rs.getInt("raffle_ticket"), rs.getInt("makeup_card")));
            }
            return users;
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get users sorted by balance: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<UserWithAccountAbstract> getUsersSortedByRaffleTicket() {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM users ORDER BY raffle_ticket DESC")) {
            ResultSet rs = preparedStatement.executeQuery();
            List<UserWithAccountAbstract> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(rs.getString("uuid"), rs.getString("player_name"), rs.getDouble("balance"), rs.getInt("raffle_ticket"), rs.getInt("makeup_card")));
            }
            return users;
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get users sorted by raffle ticket: " + e.getMessage());
        }
        return null;
    }

    // Daily Clock In Record methods
    // daily-clock_in_records table
    // id|INT NOT NULL AUTO_INCREMENT
    // date|DATE
    // uuid|VARCHAR(36)
    // time|TIME
    @Override
    public DailyClockInRecord getDailyClockInRecordOrNull(String uuid, Date date) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM daily_clock_in_records WHERE date =?")) {
            preparedStatement.setDate(1, date);
            ResultSet rs = preparedStatement.executeQuery();
            int cnt = 1;
            while (rs.next()) {
                if (rs.getString("uuid").equals(uuid)) {
                    return new DailyClockInRecord(rs.getDate("date"), rs.getString("uuid"), rs.getTime("time"), cnt);
                }
                cnt++;
            }
            return null;
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get daily clock in record: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteDailyClockInRecord(DailyClockInRecord record) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("DELETE FROM daily_clock_in_records WHERE date =? AND uuid =?")) {
            preparedStatement.setDate(1, record.getDate());
            preparedStatement.setString(2, record.getUuid());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to delete daily clock in record: " + e.getMessage());
        }
    }

    @Override
    public void dailyClockIn(String uuid, Date date, Time time){
        try (PreparedStatement preparedStatement = getConn().prepareStatement("INSERT INTO daily_clock_in_records (date, uuid, time) VALUES (?,?,?)")) {
            preparedStatement.setDate(1, date);
            preparedStatement.setString(2, uuid);
            preparedStatement.setTime(3, time);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to daily clock in: " + e.getMessage());
        }
    }

    @Override
    public Connection getConn() throws SQLException {
        try {
            // conn.setAutoCommit(true);
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get connection: " + e.getMessage());
            throw new SQLException("Failed to get connection.");
        }
    }

    private static final DatabaseManager instance = new MySQLDatabaseManager();
    public static DatabaseManager getInstance() {
        return instance;
    }
}
