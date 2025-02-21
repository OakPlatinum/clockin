package com.hz6826.clockin.api;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.hz6826.clockin.ClockIn;

public class PhysicalCurrencyUtil {
    private static final ConcurrentHashMap<String, List<Map.Entry<Integer, String>>> cache = new ConcurrentHashMap<>();
    private static volatile int cacheHash = ClockIn.CONFIG.physicalCurrencyItemIds().hashCode();

    public static List<Map.Entry<Integer, String>> getPhysicalCurrencyItemIdsSorted() {
        int currentHash = ClockIn.CONFIG.physicalCurrencyItemIds().hashCode();
        if (currentHash != cacheHash) {
            cacheHash = currentHash;
            cache.put("sortedCurrencyItems", ClockIn.CONFIG.physicalCurrencyItemIds().entrySet().stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getValue(), entry.getKey()))
                    .sorted(Map.Entry.<Integer, String>comparingByKey().reversed())
                    .collect(Collectors.toList()));
        }
        return cache.get("sortedCurrencyItems");
    }
}
