package com.hz6826.clockin.config;

import io.wispforest.owo.config.annotation.Config;

@Config(name = "clock-in-config", wrapperName = "ClockInConfig")
public class ClockInConfigWrapper {
    public String databaseType = "mysql";

    public String mysqlHost = "localhost";
    public int mysqlPort = 3306;
    public String mysqlUsername = "root";
    public String mysqlPassword = "root";
    public String mysqlDatabase = "clockin";
    public boolean mysqlUseSSL = false;

    public String sqliteFilePath = "clockin.db";
    public String currencyName = "bits";

    public boolean enablePhysicalCurrency = true;  // Deprecated
    public String physicalCurrencyItemId_1 = "clockin:coin_1";
    public String physicalCurrencyItemId_5 = "clockin:coin_5";
    public String physicalCurrencyItemId_10 = "clockin:coin_10";
    public String physicalCurrencyItemId_20 = "clockin:coin_20";
    public String physicalCurrencyItemId_50 = "clockin:coin_50";
    public String physicalCurrencyItemId_100 = "clockin:coin_100";
    public String physicalCurrencyItemId_500 = "clockin:coin_500";
    public String physicalCurrencyItemId_1000 = "clockin:coin_1000";
    public String physicalCurrencyItemId_5000 = "clockin:coin_5000";
    public String physicalCurrencyItemId_10000 = "clockin:coin_10000";
}
