package com.hz6826.clockin.config;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.ConfigOptions;

import java.util.*;

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
    public boolean getMysqlUseSSL() { return Boolean.parseBoolean(mysqlUseSSL); }
    public void setMysqlUseSSL(boolean mysqlUseSSL) { this.mysqlUseSSL = String.valueOf(mysqlUseSSL); }

    @ConfigEntry(comment = "SQLite database file path (if using SQLite)")
    private String sqliteFilePath = "clockin.db";

    public String getSqliteFilePath() { return sqliteFilePath; }
    public void setSqliteFilePath(String sqliteFilePath) { this.sqliteFilePath = sqliteFilePath; }

    @ConfigEntry(comment = "Currency name")
    private String currencyName = "bits";

    public String getCurrencyName() { return currencyName; }
    public void setCurrencyName(String currencyName) { this.currencyName = currencyName; }

    @ConfigEntry(comment = "Enable/Disable physical currency (deposits/withdrawals)")
    private String enablePhysicalCurrency = "true";

    public boolean getEnablePhysicalCurrency() { return Boolean.parseBoolean(enablePhysicalCurrency); }
    public void setEnablePhysicalCurrency(boolean enablePhysicalCurrency) { this.enablePhysicalCurrency = String.valueOf(enablePhysicalCurrency); }

    @ConfigEntry(comment = "Custom physical currency item id (if enabled), set empty to disable this coin")
    private String physicalCurrencyItemId_1 = "clockin:coin_1";
    @ConfigEntry private String physicalCurrencyItemId_5 = "clockin:coin_5";
    @ConfigEntry private String physicalCurrencyItemId_10 = "clockin:coin_10";
    @ConfigEntry private String physicalCurrencyItemId_20 = "clockin:coin_20";
    @ConfigEntry private String physicalCurrencyItemId_50 = "clockin:coin_50";
    @ConfigEntry private String physicalCurrencyItemId_100 = "clockin:coin_100";
    @ConfigEntry private String physicalCurrencyItemId_500 = "clockin:coin_500";
    @ConfigEntry private String physicalCurrencyItemId_1000 = "clockin:coin_1000";
    @ConfigEntry private String physicalCurrencyItemId_5000 = "clockin:coin_5000";
    @ConfigEntry private String physicalCurrencyItemId_10000 = "clockin:coin_10000";

    public String getPhysicalCurrencyItemId_1() { return physicalCurrencyItemId_1; }
    public void setPhysicalCurrencyItemId_1(String physicalCurrencyItemId_1) { this.physicalCurrencyItemId_1 = physicalCurrencyItemId_1; }
    public String getPhysicalCurrencyItemId_5() { return physicalCurrencyItemId_5; }
    public void setPhysicalCurrencyItemId_5(String physicalCurrencyItemId_5) { this.physicalCurrencyItemId_5 = physicalCurrencyItemId_5; }
    public String getPhysicalCurrencyItemId_10() { return physicalCurrencyItemId_10; }
    public void setPhysicalCurrencyItemId_10(String physicalCurrencyItemId_10) { this.physicalCurrencyItemId_10 = physicalCurrencyItemId_10; }
    public String getPhysicalCurrencyItemId_20() { return physicalCurrencyItemId_20; }
    public void setPhysicalCurrencyItemId_20(String physicalCurrencyItemId_20) { this.physicalCurrencyItemId_20 = physicalCurrencyItemId_20; }
    public String getPhysicalCurrencyItemId_50() { return physicalCurrencyItemId_50; }
    public void setPhysicalCurrencyItemId_50(String physicalCurrencyItemId_50) { this.physicalCurrencyItemId_50 = physicalCurrencyItemId_50; }
    public String getPhysicalCurrencyItemId_100() { return physicalCurrencyItemId_100; }
    public void setPhysicalCurrencyItemId_100(String physicalCurrencyItemId_100) { this.physicalCurrencyItemId_100 = physicalCurrencyItemId_100; }
    public String getPhysicalCurrencyItemId_500() { return physicalCurrencyItemId_500; }
    public void setPhysicalCurrencyItemId_500(String physicalCurrencyItemId_500) { this.physicalCurrencyItemId_500 = physicalCurrencyItemId_500; }
    public String getPhysicalCurrencyItemId_1000() { return physicalCurrencyItemId_1000; }
    public void setPhysicalCurrencyItemId_1000(String physicalCurrencyItemId_1000) { this.physicalCurrencyItemId_1000 = physicalCurrencyItemId_1000; }
    public String getPhysicalCurrencyItemId_5000() { return physicalCurrencyItemId_5000; }
    public void setPhysicalCurrencyItemId_5000(String physicalCurrencyItemId_5000) { this.physicalCurrencyItemId_5000 = physicalCurrencyItemId_5000; }
    public String getPhysicalCurrencyItemId_10000() { return physicalCurrencyItemId_10000; }
    public void setPhysicalCurrencyItemId_10000(String physicalCurrencyItemId_10000) { this.physicalCurrencyItemId_10000 = physicalCurrencyItemId_10000; }

    public String getPhysicalCurrencyItemId(int amount) {
        if (amount == 1) {
            return getPhysicalCurrencyItemId_1();
        } else if (amount == 5) {
            return getPhysicalCurrencyItemId_5();
        } else if (amount == 10) {
            return getPhysicalCurrencyItemId_10();
        } else if (amount == 20) {
            return getPhysicalCurrencyItemId_20();
        } else if (amount == 50) {
            return getPhysicalCurrencyItemId_50();
        } else if (amount == 100) {
            return getPhysicalCurrencyItemId_100();
        } else if (amount == 500) {
            return getPhysicalCurrencyItemId_500();
        } else if (amount == 1000) {
            return getPhysicalCurrencyItemId_1000();
        } else if (amount == 5000) {
            return getPhysicalCurrencyItemId_5000();
        } else if (amount == 10000) {
            return getPhysicalCurrencyItemId_10000();
        } else {
            return "";
        }
    }
    public Map<String, Integer> getPhysicalCurrencyItemIds() {
        return physicalCurrencyItemIds;
    }

    public List<Map.Entry<Integer, String>> getPhysicalCurrencyItemIdsSorted() {
        return physicalCurrencyItemIdsSorted;
    }

    private final Map<String, Integer> physicalCurrencyItemIds = new HashMap<>();
    private final List<Map.Entry<Integer, String>> physicalCurrencyItemIdsSorted;

    {
        List<Map.Entry<Integer, String>> physicalCurrencyItemIdsSorted = new ArrayList<>();
        if(!getPhysicalCurrencyItemId_1().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(10000, getPhysicalCurrencyItemId_10000()));
        if(!getPhysicalCurrencyItemId_5().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(5000, getPhysicalCurrencyItemId_5000()));
        if(!getPhysicalCurrencyItemId_10().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(1000, getPhysicalCurrencyItemId_1000()));
        if(!getPhysicalCurrencyItemId_20().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(500, getPhysicalCurrencyItemId_500()));
        if(!getPhysicalCurrencyItemId_50().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(100, getPhysicalCurrencyItemId_100()));
        if(!getPhysicalCurrencyItemId_100().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(50, getPhysicalCurrencyItemId_50()));
        if(!getPhysicalCurrencyItemId_500().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(20, getPhysicalCurrencyItemId_20()));
        if(!getPhysicalCurrencyItemId_1000().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(10, getPhysicalCurrencyItemId_10()));
        if(!getPhysicalCurrencyItemId_5000().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(5, getPhysicalCurrencyItemId_5()));
        if(!getPhysicalCurrencyItemId_10000().isBlank()) physicalCurrencyItemIdsSorted.add(new AbstractMap.SimpleEntry<>(1, getPhysicalCurrencyItemId_1()));
        this.physicalCurrencyItemIdsSorted = physicalCurrencyItemIdsSorted;

        for (Map.Entry<Integer, String> entry : this.physicalCurrencyItemIdsSorted) {
            getPhysicalCurrencyItemIds().put(entry.getValue(), entry.getKey());
        }

    }



    static BasicConfig config = new BasicConfig();
    public static BasicConfig getConfig() {
        return config;
    }
}
