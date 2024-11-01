package com.hz6826.clockin.sql;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import com.hz6826.clockin.sql.model.interfaces.MailInterface;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.hz6826.clockin.sql.model.sqlite.DailyClockInRecord;
import com.hz6826.clockin.sql.model.sqlite.Mail;
import com.hz6826.clockin.sql.model.sqlite.Reward;
import com.hz6826.clockin.sql.model.sqlite.User;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.SERVER)
public class SQLiteDatabaseManager implements DatabaseManager{
    private final String url;

    public SQLiteDatabaseManager() {
        String filePathString = BasicConfig.getConfig().getSqliteFilePath();
        url = "jdbc:sqlite:" + filePathString.replace("\\", "/");
        try {
            Connection conn = getConn();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTables() {
        executeUpdate(User.createTableSQL());
        executeUpdate(DailyClockInRecord.createTableSQL());
        executeUpdate(Reward.createTableSQL());
        executeUpdate(Mail.createTableSQL());
    }

    @Override
    public void dropTables() {
        executeUpdate("DROP TABLE IF EXISTS users");
        executeUpdate("DROP TABLE IF EXISTS daily_clock_in_records");
        executeUpdate("DROP TABLE IF EXISTS rewards");
        executeUpdate("DROP TABLE IF EXISTS mails");
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
            try (PreparedStatement preparedStatement = getConn().prepareStatement("INSERT INTO users (uuid, player_name, balance, raffle_ticket, makeup_card) VALUES (?,?, 0, 0, 0)")) {
                preparedStatement.setString(1, uuid);
                preparedStatement.setString(2, playerName);
                preparedStatement.executeUpdate();
                ClockIn.LOGGER.info("Created user " + playerName + " [" + uuid + "]");
            } catch (SQLException e) {
                ClockIn.LOGGER.error("Failed to create user: " + e.getMessage());
            }
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
    public void updateUser(UserWithAccountAbstract user) {
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
    public void deleteDailyClockInRecord(DailyClockInRecordInterface record) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("DELETE FROM daily_clock_in_records WHERE date =? AND uuid =?")) {
            preparedStatement.setDate(1, record.date());
            preparedStatement.setString(2, record.uuid());
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
    public List<DailyClockInRecordInterface> getDailyClockInRecords(Date date) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM daily_clock_in_records WHERE date = ?")) {
            preparedStatement.setDate(1, date);
            ResultSet rs = preparedStatement.executeQuery();
            List<DailyClockInRecordInterface> records = new ArrayList<>();
            while (rs.next()) {
                records.add(new DailyClockInRecord(rs.getDate("date"), rs.getString("uuid"), rs.getTime("time"), 1));
            }
            return records;
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get daily clock in records: " + e.getMessage());
        }
        return null;
    }

    @Override
    public int getPlayerDailyClockInCount(String uuid) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT COUNT(*) FROM daily_clock_in_records WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get player daily clock in count: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getPlayerDailyClockInCount(String uuid, int month) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT COUNT(*) FROM daily_clock_in_records WHERE uuid = ? AND strftime('%m', date) = ?;")) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setInt(2, month);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get player daily clock in count: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getPlayerDailyClockInCount(String uuid, Date start, Date end) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT COUNT(*) FROM daily_clock_in_records WHERE uuid = ? AND date >= ? AND date <= ?")) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setDate(2, start);
            preparedStatement.setDate(3, end);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get player daily clock in count: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Connection getConn() throws SQLException {
        try {
            // conn.setAutoCommit(true);
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get connection: " + e.getMessage());
            throw new SQLException("Failed to get connection.");
        }
    }

    @Override
    public RewardInterface getRewardOrNew(String key) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM rewards WHERE [key] = ?")) {
            preparedStatement.setString(1, key);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Reward(rs.getString("key"), rs.getString("translatable_key"), rs.getString("item_list_serialized"), rs.getDouble("money"), rs.getInt("raffle_tickets"), rs.getInt("makeup_cards"));
            } else {
                return new Reward(key, "", "", 0, 0, 0);
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get reward: " + e.getMessage());
        }
        return null;
    }

    @Override
    public RewardInterface createOrUpdateReward(RewardInterface reward) {
        if (getRewardOrNew(reward.getKey()).isNew()){
            try (PreparedStatement preparedStatement = getConn().prepareStatement("INSERT INTO rewards ([key], translatable_key, item_list_serialized, money, raffle_tickets, makeup_cards) VALUES (?,?,?,?,?,?)")) {
                preparedStatement.setString(1, reward.getKey());
                preparedStatement.setString(2, reward.getTranslatableKey());
                preparedStatement.setString(3, reward.getItemListSerialized());
                preparedStatement.setDouble(4, reward.getMoney());
                preparedStatement.setInt(5, reward.getRaffleTickets());
                preparedStatement.setInt(6, reward.getMakeupCards());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                ClockIn.LOGGER.error("Failed to create reward: " + e.getMessage());
            }
        } else {
            try (PreparedStatement preparedStatement = getConn().prepareStatement("UPDATE rewards SET translatable_key =?, item_list_serialized =?, money =?, raffle_tickets =?, makeup_cards =? WHERE [key] = ?")) {
                preparedStatement.setString(1, reward.getTranslatableKey());
                preparedStatement.setString(2, reward.getItemListSerialized());
                preparedStatement.setDouble(3, reward.getMoney());
                preparedStatement.setInt(4, reward.getRaffleTickets());
                preparedStatement.setInt(5, reward.getMakeupCards());
                preparedStatement.setString(6, reward.getKey());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                ClockIn.LOGGER.error("Failed to update reward: " + e.getMessage());
            }
        }
        return getRewardOrNew(reward.getKey());
    }

    @Override
    public void sendMail(String senderUuid, String receiverUuid, Date sendTime, String content, String serializedAttachment, boolean isRead, boolean isAttachmentFetched) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("INSERT INTO mails (sender_uuid, receiver_uuid, send_time, content, serialized_attachment, is_read, is_attachment_fetched) VALUES (?,?,?,?,?,?,?)")) {
            preparedStatement.setString(1, senderUuid);
            preparedStatement.setString(2, receiverUuid);
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(sendTime.getTime()));
            preparedStatement.setString(4, content);
            preparedStatement.setString(5, serializedAttachment);
            preparedStatement.setBoolean(6, isRead);
            preparedStatement.setBoolean(7, isAttachmentFetched);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to send mail: " + e.getMessage());
        }
    }

    @Override
    public List<MailInterface> getMails(String receiverUuid) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM mails WHERE receiver_uuid = ?")) {
            preparedStatement.setString(1, receiverUuid);
            ResultSet rs = preparedStatement.executeQuery();
            List<MailInterface> mails = new ArrayList<>();
            while (rs.next()) {
                mails.add(new com.hz6826.clockin.sql.model.mysql.Mail(rs.getString("sender_uuid"), rs.getString("receiver_uuid"), rs.getDate("send_time"), rs.getString("content"), rs.getString("serialized_attachment"), rs.getBoolean("is_read"), rs.getBoolean("is_attachment_fetched")));
            }
            return mails;
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get mails: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void setAttachmentFetched(MailInterface mail) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("UPDATE mails SET is_attachment_fetched = ? WHERE sender_uuid = ? AND receiver_uuid = ? AND send_time = ?")) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, mail.getSenderUuid());
            preparedStatement.setString(3, mail.getReceiverUuid());
            preparedStatement.setTimestamp(4, new java.sql.Timestamp(mail.getSendTime().getTime()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to set attachment fetched: " + e.getMessage());
        }
    }

    private static final DatabaseManager instance = new SQLiteDatabaseManager();
    public static DatabaseManager getInstance() {
        return instance;
    }
}
