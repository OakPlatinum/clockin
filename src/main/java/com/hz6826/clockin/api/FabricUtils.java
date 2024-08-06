package com.hz6826.clockin.api;

import com.hz6826.clockin.ClockIn;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FabricUtils {

    public static String serializeItemStackList(List<ItemStack> stackList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ItemStack stack : stackList) {
            stringBuilder
                    .append(stack.getItem().toString())
                    .append("|")
                    .append(stack.getCount())
                    .append("|")
                    .append(stack.getOrCreateNbt().toString())
                    .append(";");
        }
        return stringBuilder.toString();
    }
    public static ArrayList<ItemStack> deserializeItemStackList(String serializedStackList){
        ArrayList<ItemStack> stackList = new ArrayList<>();
        String[] strings = serializedStackList.split(";");
        for (String string : strings) {
            String[] s = string.split("\\|");
            // 1. 创建 RegistryKey<Item>
            RegistryKey<Item> itemKey = RegistryKey.of(Registries.ITEM.getKey(), new Identifier(s[0]));

            // 2. 获取物品条目
            Optional<RegistryEntry.Reference<Item>> itemEntryOptional = Registries.ITEM.getEntry(itemKey);
            RegistryEntry<Item> itemEntry = itemEntryOptional.orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + s[0]));
            try {
                ItemStack itemStack = new ItemStack(itemEntry, Integer.parseInt(s[1]), Optional.ofNullable(StringNbtReader.parse(s[2])));
                stackList.add(itemStack);
            } catch (CommandSyntaxException e) {
                ClockIn.LOGGER.error("Invalid NBT data for item: " + s[0]);
            }
        }
        return stackList;
    }
    public void giveItemList(List<ItemStack> stackList, ServerPlayerEntity player) {
        for (ItemStack stack : stackList) {
            giveItem(stack, player);
        }
    }
    public void giveItem(ItemStack stack, ServerPlayerEntity player) {
        if (!stack.isEmpty()) {
            player.dropItem(stack, false);
        }
    }
}
