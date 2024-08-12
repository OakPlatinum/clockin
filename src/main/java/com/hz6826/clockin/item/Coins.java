package com.hz6826.clockin.item;

import com.hz6826.clockin.ClockIn;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class Coins {
    public static final Item COIN_1 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_1"),
                    new CoinItem(1, new FabricItemSettings().maxCount(64).rarity(Rarity.COMMON)));
    public static final Item COIN_5 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_5"),
                    new CoinItem(5, new FabricItemSettings().maxCount(64).rarity(Rarity.COMMON)));
    public static final Item COIN_10 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_10"),
                    new CoinItem(10, new FabricItemSettings().maxCount(64).rarity(Rarity.COMMON)));
    public static final Item COIN_20 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_20"),
                    new CoinItem(20, new FabricItemSettings().maxCount(64).rarity(Rarity.UNCOMMON)));

    public static final Item COIN_50 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_50"),
                    new CoinItem(50, new FabricItemSettings().maxCount(64).rarity(Rarity.UNCOMMON)));
    public static final Item COIN_100 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_100"),
                    new CoinItem(100, new FabricItemSettings().maxCount(64).rarity(Rarity.UNCOMMON)));
    public static final Item COIN_500 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_500"),
                    new CoinItem(500, new FabricItemSettings().maxCount(64).rarity(Rarity.RARE)));
    public static final Item COIN_1000 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_1000"),
                    new CoinItem(1000, new FabricItemSettings().maxCount(64).rarity(Rarity.RARE)));
    public static final Item COIN_5000 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_5000"),
                    new CoinItem(5000, new FabricItemSettings().maxCount(64).rarity(Rarity.EPIC)));
    public static final Item COIN_10000 =
            Registry.register(Registries.ITEM, new Identifier(ClockIn.MOD_ID, "coin_10000"),
                    new CoinItem(10000, new FabricItemSettings().maxCount(64).rarity(Rarity.EPIC)));

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.add(COIN_1);
            content.add(COIN_5);
            content.add(COIN_10);
            content.add(COIN_20);
            content.add(COIN_50);
            content.add(COIN_100);
            content.add(COIN_500);
            content.add(COIN_1000);
            content.add(COIN_5000);
            content.add(COIN_10000);
        });
    }
}
