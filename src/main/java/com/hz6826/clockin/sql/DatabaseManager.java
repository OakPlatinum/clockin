package com.hz6826.clockin.sql;

import java.sql.Connection;

public interface DatabaseManager {
    boolean ping();
    void reconnecting();
    void createTables();
    void dropTables();
    Connection getConn();
    Connection getConn(int timeout);
}
