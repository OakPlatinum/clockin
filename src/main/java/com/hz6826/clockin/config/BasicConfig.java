package com.hz6826.clockin.config;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.ConfigOptions;

public class BasicConfig extends Config {
    public BasicConfig() {
        // Replace "mymod" with your mod's ID
        super(ConfigOptions.mod("clockin"));
    }

    @ConfigEntry(comment = "Type of database to use (MySQL or SQLite(W.I.P.))")
    private String databaseType = "mysql";

    public String getDatabaseType() { return databaseType.toLowerCase();}
    public void setDatabaseType(String databaseType) { this.databaseType = databaseType.toLowerCase();}

    @ConfigEntry(comment = "MySQL database credentials (if using MySQL)")
    private String mysqlHost = "localhost";
    @ConfigEntry private int mysqlPort = 3306;
    @ConfigEntry private String mysqlUsername = "root";
    @ConfigEntry private String mysqlPassword = "password";
    @ConfigEntry private String mysqlDatabase = "clockin";
    @ConfigEntry private String mysqlUseSSL = "false";
    public String getMysqlHost() { return mysqlHost; }
    public void setMysqlHost(String mysqlHost) { this.mysqlHost = mysqlHost;}
    public int getMysqlPort() { return mysqlPort; }
    public void setMysqlPort(int mysqlPort) { this.mysqlPort = mysqlPort; }
    public String getMysqlUsername() { return mysqlUsername; }
    public void setMysqlUsername(String mysqlUsername) { this.mysqlUsername = mysqlUsername; }
    public String getMysqlPassword() { return mysqlPassword;}
    public void setMysqlPassword(String mysqlPassword) { this.mysqlPassword = mysqlPassword;}
    public String getMysqlDatabase() { return mysqlDatabase;}
    public void setMysqlDatabase(String mysqlDatabase) { this.mysqlDatabase = mysqlDatabase;}
    public String getMysqlUseSSL() { return mysqlUseSSL; }
    public void setMysqlUseSSL(boolean mysqlUseSSL) { this.mysqlUseSSL = String.valueOf(mysqlUseSSL); }

    @ConfigEntry(comment = "SQLite database file path (if using SQLite)")
    private String sqliteFilePath = "clockin.db";

    public String getSqliteFilePath() { return sqliteFilePath; }
    public void setSqliteFilePath(String sqliteFilePath) { this.sqliteFilePath = sqliteFilePath; }

    @ConfigEntry(comment = "Currency name")
    private String currencyName = "bits";

    public String getCurrencyName() { return currencyName; }
    public void setCurrencyName(String currencyName) { this.currencyName = currencyName; }

    static BasicConfig config = new BasicConfig();
    public static BasicConfig getConfig() {
        return config;
    }
}
