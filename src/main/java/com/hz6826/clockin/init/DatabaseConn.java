package com.hz6826.clockin.init;

import io.ebean.Database;
import io.ebean.datasource.DataSourceConfig;

import com.hz6826.clockin.ClockIn;
import io.ebean.datasource.DataSourcePool;

/*
* 那个用的是库，叫 ebean，它会自己管理的，额那个库里有了
* 用工厂awa
* */

public class DatabaseConn {
    public static Database bootstrap() {
        return Database.builder().dataSource(getDataSource()).build();
    }

    //
    private static DataSourcePool getDataSource() {
        if(ClockIn.CONFIG.databaseType().equalsIgnoreCase("mysql")) {
            return new DataSourceConfig()
                    .setName("db")
                    .setUrl("jdbc:mysql://" + ClockIn.CONFIG.mysqlHost() + ":" + ClockIn.CONFIG.mysqlPort() + "/" + ClockIn.CONFIG.mysqlDatabase())
                    .setUsername(ClockIn.CONFIG.mysqlUsername())
                    .setPassword(ClockIn.CONFIG.mysqlPassword())
                    .build();
        } else {  // SQLite for default
            if(!ClockIn.CONFIG.databaseType().equalsIgnoreCase("sqlite"))
                ClockIn.LOGGER.warn("Invalid database type! Use SQLite for default.");
            if(ClockIn.CONFIG.sqliteFilePath().endsWith(".db")) {
                return new DataSourceConfig()
                        .setName("db")
                        .setUrl("jdbc:sqlite:" + ClockIn.CONFIG.sqliteFilePath())
                        .build();
            } else {
                ClockIn.LOGGER.warn("Invalid SQLite filepath! Use clockin.db for default.");
                return new DataSourceConfig()
                        .setName("db")
                        .setUrl("jdbc:sqlite:clockin.db")
                        .build();
            }
        }
    }

}
