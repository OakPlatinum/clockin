package com.hz6826.clockin.init;

import io.ebean.Database;
import io.ebean.DatabaseBuilder;
import io.ebean.datasource.DataSourceConfig;

import com.hz6826.clockin.ClockIn;
import io.ebean.datasource.DataSourcePool;

import java.io.File;
import java.sql.Connection;

public class DatabaseConn {

    public static Database bootstrap() {
        DataSourcePool dataSource = getDataSource();
        if (!dataSource.isDataSourceUp()) {
            if (getDataSource().dataSourceDownReason().getMessage().startsWith("Unknown database")) {
                String m = "Database not found! If you are using MySQL, please connect to your database and use the command below to initialize:\n   CREATE DATABASE %s;".formatted(ClockIn.CONFIG.mysqlDatabase);
                ClockIn.LOGGER.error(m);
                throw new RuntimeException(m);
            }
        }
        DatabaseBuilder dbb = Database.builder()
                .dataSource(getDataSource())
                .ddlGenerate(true)
                .ddlRun(true)
                .ddlCreateOnly(true);
        Database db;
        try {
            db = dbb.build();
        } catch (Exception e) {
            ClockIn.LOGGER.warn("ClockIn database table already exists. Skipping...", e);
            db = dbb.ddlRun(false).build();
        }
        return db;
    }

    //
    private static DataSourcePool getDataSource() {
        if(ClockIn.CONFIG.getDatabaseType().equalsIgnoreCase("mysql")) {
            return new DataSourceConfig()
                    .setName("db")
                    .setUrl("jdbc:mysql://" + ClockIn.CONFIG.getMysqlHost() + ":" + ClockIn.CONFIG.getMysqlPort() + "/" + ClockIn.CONFIG.getMysqlDatabase() + "?useSSL=" + ClockIn.CONFIG.isMysqlUseSSL())
                    .setUsername(ClockIn.CONFIG.getMysqlUsername())
                    .setPassword(ClockIn.CONFIG.getMysqlPassword())
                    .setFailOnStart(false)
                    .build();
        } else {  // SQLite for default
            if(!ClockIn.CONFIG.getDatabaseType().equalsIgnoreCase("sqlite"))
                ClockIn.LOGGER.warn("Invalid database type! Use SQLite for default.");
            DataSourceConfig config = new DataSourceConfig()
                    .setName("db")
                    .setUsername("")
                    .setPassword("")
                    .setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)
                    .setMinConnections(1)
                    .setMaxConnections(1)
                    .setHeartbeatFreqSecs(-1);
            if(ClockIn.CONFIG.getSqliteFilePath().endsWith(".sqlite")) {
                return config.setUrl("jdbc:sqlite:" + ClockIn.CONFIG.getSqliteFilePath()).build();
            } else {
                ClockIn.LOGGER.warn("Invalid SQLite filepath! Use clockin.sqlite for default.");
                return config.setUrl("jdbc:sqlite:clockin.sqlite").build();
            }
        }
    }

}
