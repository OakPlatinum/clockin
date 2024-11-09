package com.hz6826.clockin.sql;

import com.hz6826.clockin.sql.model.interfaces.DailyClockInRecordInterface;
import com.hz6826.clockin.sql.model.interfaces.MailInterface;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.sql.*;
import java.util.List;

@Environment(EnvType.SERVER)
public interface DatabaseManager {
    String ADMIN_UUID = "00000000-0000-0000-0000-000000000000";
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

    // Mail methods
    void sendMail(String senderUuid, String receiverUuid, Date sendTime, String content, String serializedAttachment, boolean isRead, boolean isAttachmentFetched);

    List<MailInterface> getMails(String receiverUuid, int page, int pageSize);

    void setAttachmentFetched(MailInterface mail);

    int getMailCount(String receiverUuid);
}
