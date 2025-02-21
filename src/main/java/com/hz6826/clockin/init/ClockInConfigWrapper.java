package com.hz6826.clockin.init;

import io.wispforest.owo.config.annotation.Config;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
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

    public Map<String, Integer> physicalCurrencyItemIds = new HashMap<> () {{
        put("clockin:coin_1", 1);
        put("clockin:coin_5", 5);
        put("clockin:coin_10", 10);
        put("clockin:coin_20", 20);
        put("clockin:coin_50", 50);
        put("clockin:coin_100", 100);
        put("clockin:coin_500", 500);
        put("clockin:coin_1000", 1000);
        put("clockin:coin_5000", 5000);
        put("clockin:coin_10000", 10000);
    }};

}
