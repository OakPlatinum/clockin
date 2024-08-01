package com.hz6826.clockin.sql;

import java.sql.*;
import java.util.*;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.sql.model.mysql.User;

public class MySQLDatabaseManager implements DatabaseManager{
    private Connection conn;
    private String url;

    public MySQLDatabaseManager() {
        String host = BasicConfig.MySQLConfig.getConfig().getMysqlHost();
        int port = BasicConfig.MySQLConfig.getConfig().getMysqlPort();
        String database = BasicConfig.MySQLConfig.getConfig().getMysqlDatabase();
        boolean useSSL = BasicConfig.MySQLConfig.getConfig().getMysqlUseSSL();
        url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL;
        String user = BasicConfig.MySQLConfig.getConfig().getMysqlUsername();
        String password = BasicConfig.MySQLConfig.getConfig().getMysqlPassword();
        try {
            conn = DriverManager.getConnection(url, user, password);
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
    public void reconnecting() {
        String user = BasicConfig.MySQLConfig.getConfig().getMysqlUsername();
        String password = BasicConfig.MySQLConfig.getConfig().getMysqlPassword();
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTables() {
        User.createTable();
    }

    @Override
    public void dropTables() {

    }

    public void executeUpdate(String sql) {
        try {
            Statement stmt = getConn().createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to execute update: " + e.getMessage());
        }
    }

    public ResultSet executeQuery(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to execute query: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Connection getConn(int timeout) {
        int initialTimeout = timeout;
        while (true) {
            try {
                if (conn != null && conn.isValid(timeout)) {
                    return conn;
                } else {
                    if (timeout > 100) {
                        throw new SQLException("Connection timeout after " + initialTimeout + "ms.");
                    } else {
                        timeout += 10;
                    }
                }
            } catch (SQLException e) {
                ClockIn.LOGGER.error("Failed to get connection: " + e.getMessage());
                timeout += 10;
            }
        }
    }

    // User methods
    public User getOrCreateUser(String uuid, String playerName) {
        User user = getUserByUUID(uuid);
        if (user == null) {
            user = new User(uuid, playerName, 0, 0);
            user.save();
        } else if (!Objects.equals(user.getPlayerName(), playerName)) {
            user.setPlayerName(playerName);
        }
        return user;
    }

    public User getUserByUUID(String uuid){
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM users WHERE uuid =?")) {
            preparedStatement.setString(1, uuid);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("uuid"), rs.getString("player_name"), rs.getInt("clock_in_count"), rs.getInt("total_clock_in_time"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get user by UUID: " + e.getMessage());
        }
        return null;
    }

    public User getUserByName(String playerName) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM users WHERE player_name =?")) {
            preparedStatement.setString(1, playerName);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("uuid"), rs.getString("player_name"), rs.getInt("clock_in_count"), rs.getInt("total_clock_in_time"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get user by name: " + e.getMessage());
        }
        return null;
    }

    public void updateUser(User user) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("UPDATE users SET player_name =?, balance =?, raffle_ticket =? WHERE uuid =?")) {
            preparedStatement.setString(1, user.getPlayerName());
            preparedStatement.setDouble(2, user.getBalance());
            preparedStatement.setInt(3, user.getRaffleTicket());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to update user: " + e.getMessage());
        }
    }

    public List<User> getUsersSortedByBalance() {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM users ORDER BY balance DESC")) {
            ResultSet rs = preparedStatement.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("uuid"), rs.getString("player_name"), rs.getDouble("balance"), rs.getInt("raffle_ticket")));
            }
            return users;
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get users sorted by balance: " + e.getMessage());
        }
        return null;
    }

    public List<User> getUsersSortedByRaffleTicket() {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM users ORDER BY raffle_ticket DESC")) {
            ResultSet rs = preparedStatement.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("uuid"), rs.getString("player_name"), rs.getDouble("balance"), rs.getInt("raffle_ticket")));
            }
            return users;
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get users sorted by raffle ticket: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Connection getConn() {
        return getConn(10);
    }

    private static final MySQLDatabaseManager instance = new MySQLDatabaseManager();
    public static MySQLDatabaseManager getInstance() {
        return instance;
    }
}
