package com.hz6826.clockin.config;

import io.wispforest.owo.config.annotation.Config;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    public final Map<String, Integer> physicalCurrencyItemIds = new HashMap<>(Map.of(
            "minecraft:gold_ingot", 100,
            "minecraft:diamond", 500,
            "minecraft:netherite_scrap", 1000,
            "minecraft:emerald", 5000,
            "minecraft:diamond_block", 5000,
            "minecraft:netherite_block", 10000
    ));

    private final ConcurrentHashMap<String, List<Map.Entry<Integer, String>>> cache = new ConcurrentHashMap<>();
    private volatile int cacheHash = physicalCurrencyItemIds.hashCode();

    public List<Map.Entry<Integer, String>> getPhysicalCurrencyItemIdsSorted() {
        int currentHash = physicalCurrencyItemIds.hashCode();
        if (currentHash != cacheHash) {
            cacheHash = currentHash;
            cache.put("sortedCurrencyItems", physicalCurrencyItemIds.entrySet().stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getValue(), entry.getKey()))
                    .sorted(Map.Entry.<Integer, String>comparingByKey().reversed())
                    .collect(Collectors.toList()));
        }
        return cache.get("sortedCurrencyItems");
    }

}
