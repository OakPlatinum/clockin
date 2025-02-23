package com.hz6826.clockin.init;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ClockInConfig {
    @JsonProperty("database.type") public String databaseType = "sqlite";

    @JsonProperty("database.mysql.host") public String mysqlHost = "localhost";
    @JsonProperty("database.mysql.port") public int mysqlPort = 3306;
    @JsonProperty("database.mysql.username") public String mysqlUsername = "root";
    @JsonProperty("database.mysql.password") public String mysqlPassword = "root";
    @JsonProperty("database.mysql.database") public String mysqlDatabase = "clockin";
    @JsonProperty("database.mysql.use_ssl") public boolean mysqlUseSSL = false;

    @JsonProperty("database.sqlite.file_path") public String sqliteFilePath = "clockin.sqlite";

    @JsonProperty("currency.name")
    public String currencyName = "bits";

    @JsonProperty("currency.enable_physical")
    public boolean enablePhysicalCurrency = true;

    @JsonProperty("currency.physical_currency_item_ids")
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
