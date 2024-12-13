package com.hz6826.clockin.sql;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import com.hz6826.clockin.sql.model.interfaces.MailInterface;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.hz6826.clockin.sql.model.mysql.DailyClockInRecord;
import com.hz6826.clockin.sql.model.mysql.Mail;
import com.hz6826.clockin.sql.model.mysql.Reward;
import com.hz6826.clockin.sql.model.mysql.User;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.SERVER)
public class MySQLDatabaseManager implements DatabaseManager{
    private final String url;
    private final String username;
    private final String password;

    public MySQLDatabaseManager() {
        String host = BasicConfig.getConfig().getMysqlHost();
        int port = BasicConfig.getConfig().getMysqlPort();
        String database = BasicConfig.getConfig().getMysqlDatabase();
        boolean useSSL = BasicConfig.getConfig().getMysqlUseSSL();
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
            ClockIn.LOGGER.error("Failed to execute update: " + e.getMessage(), e);
        }
    }

    @Override
    public ResultSet executeQuery(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to execute query: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to get user by UUID: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to get user by name: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to update user: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to get users sorted by balance: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to get users sorted by raffle ticket: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to get daily clock in record: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void deleteDailyClockInRecord(@NotNull DailyClockInRecordInterface record) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("DELETE FROM daily_clock_in_records WHERE date =? AND uuid =?")) {
            preparedStatement.setDate(1, record.date());
            preparedStatement.setString(2, record.uuid());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to delete daily clock in record: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to daily clock in: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to get daily clock in records: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to get player daily clock in count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int getPlayerDailyClockInCount(String uuid, int month) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT COUNT(*) FROM daily_clock_in_records WHERE uuid = ? AND MONTH(date) = ?")) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setInt(2, month);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get player daily clock in count: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to get player daily clock in count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public Connection getConn() throws SQLException {
        try {
            // conn.setAutoCommit(true);
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get connection: " + e.getMessage(), e);
            throw new SQLException("Failed to get connection.");
        }
    }

    // Reward methods
    @Override
    public RewardInterface getRewardOrNew(String key) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM rewards WHERE `key` = ?")) {
            preparedStatement.setString(1, key);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Reward(rs.getString("key"), rs.getString("translatable_key"), rs.getString("item_list_serialized"), rs.getDouble("money"), rs.getInt("raffle_tickets"), rs.getInt("makeup_cards"));
            } else {
                return new Reward(key, "", "", 0, 0, 0);
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get reward: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public RewardInterface createOrUpdateReward(RewardInterface reward) {
        if (getRewardOrNew(reward.getKey()).isNew()){
            try (PreparedStatement preparedStatement = getConn().prepareStatement("INSERT INTO rewards (`key`, translatable_key, item_list_serialized, money, raffle_tickets, makeup_cards) VALUES (?,?,?,?,?,?)")) {
                preparedStatement.setString(1, reward.getKey());
                preparedStatement.setString(2, reward.getTranslatableKey());
                preparedStatement.setString(3, reward.getItemListSerialized());
                preparedStatement.setDouble(4, reward.getMoney());
                preparedStatement.setInt(5, reward.getRaffleTickets());
                preparedStatement.setInt(6, reward.getMakeupCards());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                ClockIn.LOGGER.error("Failed to create reward: " + e.getMessage(), e);
            }
        } else {
            try (PreparedStatement preparedStatement = getConn().prepareStatement("UPDATE rewards SET translatable_key =?, item_list_serialized =?, money =?, raffle_tickets =?, makeup_cards =? WHERE `key` = ?")) {
                preparedStatement.setString(1, reward.getTranslatableKey());
                preparedStatement.setString(2, reward.getItemListSerialized());
                preparedStatement.setDouble(3, reward.getMoney());
                preparedStatement.setInt(4, reward.getRaffleTickets());
                preparedStatement.setInt(5, reward.getMakeupCards());
                preparedStatement.setString(6, reward.getKey());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                ClockIn.LOGGER.error("Failed to update reward: " + e.getMessage(), e);
            }
        }
        return getRewardOrNew(reward.getKey());
    }

    // package com.hz6826.clockin.sql.model.mysql;
    //
    //import com.hz6826.clockin.sql.model.interfaces.MailInterface;
    //
    //import java.util.Date;
    //
    //public class Mail implements MailInterface {
    //    private final String senderUuid;  // If admin, senderUuid is 00000000-0000-0000-0000-000000000000
    //    private final String receiverUuid;
    //    private final Date sendTime;
    //    private final String content;
    //    private final String serializedAttachment;
    //    private final boolean isAttachmentFetched;
    //
    //    public Mail(String senderUuid, String receiverUuid, Date sendTime, String content, String serializedAttachment, boolean isAttachmentFetched) {
    //        this.senderUuid = senderUuid;
    //        this.receiverUuid = receiverUuid;
    //        this.sendTime = sendTime;
    //        this.content = content;
    //        this.serializedAttachment = serializedAttachment;
    //        this.isAttachmentFetched = isAttachmentFetched;
    //    }
    //
    //    public String getSenderUuid() {
    //        return senderUuid;
    //    }
    //
    //    public String getReceiverUuid() {
    //        return receiverUuid;
    //    }
    //
    //    public Date getSendTime() {
    //        return sendTime;
    //    }
    //
    //    public String getContent() {
    //        return content;
    //    }
    //
    //    public String getSerializedAttachment() {
    //        return serializedAttachment;
    //    }
    //
    //    public boolean getAttachmentFetched() {
    //        return isAttachmentFetched;
    //    }
    //}

    // Mail methods
    @Override
    public void sendMail(String senderUuid, String receiverUuid, Timestamp sendTime, String content, String serializedAttachment, boolean isRead, boolean isAttachmentFetched) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("INSERT INTO mails (sender_uuid, receiver_uuid, send_time, content, serialized_attachment, is_read, is_attachment_fetched) VALUES (?,?,?,?,?,?,?)")) {
            preparedStatement.setString(1, senderUuid);
            preparedStatement.setString(2, receiverUuid);
            preparedStatement.setTimestamp(3, sendTime);
            preparedStatement.setString(4, content);
            preparedStatement.setString(5, serializedAttachment);
            preparedStatement.setBoolean(6, isRead);
            preparedStatement.setBoolean(7, isAttachmentFetched);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to send mail: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MailInterface> getMails(String receiverUuid, int page, int pageSize) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM mails WHERE receiver_uuid = ? ORDER BY send_time DESC LIMIT ? OFFSET ?")) {
            preparedStatement.setString(1, receiverUuid);
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, (page - 1) * pageSize);
            ResultSet rs = preparedStatement.executeQuery();
            List<MailInterface> mails = new ArrayList<>();
            while (rs.next()) {
                mails.add(new Mail(rs.getInt("id"), rs.getString("sender_uuid"), rs.getString("receiver_uuid"), rs.getTimestamp("send_time"), rs.getString("content"), rs.getString("serialized_attachment"), rs.getBoolean("is_read"), rs.getBoolean("is_attachment_fetched")));
            }
            return mails;
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get mails: " + e.getMessage(), e);
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
            ClockIn.LOGGER.error("Failed to set attachment fetched: " + e.getMessage(), e);
        }
    }

    @Override
    public int getMailCount(String receiverUuid) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT COUNT(*) FROM mails WHERE receiver_uuid = ?")) {
            preparedStatement.setString(1, receiverUuid);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get mail count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public MailInterface getMailById(int id) {
        try (PreparedStatement preparedStatement = getConn().prepareStatement("SELECT * FROM mails WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Mail(rs.getInt("id"), rs.getString("sender_uuid"), rs.getString("receiver_uuid"), rs.getTimestamp("send_time"), rs.getString("content"), rs.getString("serialized_attachment"), rs.getBoolean("is_read"), rs.getBoolean("is_attachment_fetched"));
            }
        } catch (SQLException e) {
            ClockIn.LOGGER.error("Failed to get mail by id: " + e.getMessage(), e);
        }
        return null;
    }


    private static final DatabaseManager instance = new MySQLDatabaseManager();
    public static DatabaseManager getInstance() {
        return instance;
    }
}
