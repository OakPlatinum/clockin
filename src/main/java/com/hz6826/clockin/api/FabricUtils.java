package com.hz6826.clockin.api;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FabricUtils {
    public static String CURRENCY_NAME = BasicConfig.getConfig().getCurrencyName();

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
    public static void giveItemList(@NotNull List<ItemStack> stackList, ServerPlayerEntity player) {
        for (ItemStack stack : stackList) {
            giveItem(stack, player);
        }
    }
    public static void giveItem(@NotNull ItemStack stack, ServerPlayerEntity player) {
        if (!stack.isEmpty()) {
            player.giveItemStack(stack);
        }
    }
    public static Text generateReadableReward(RewardInterface reward){
        MutableText text = Text.empty();
        if(!reward.getItemListSerialized().isBlank()) {
            ArrayList<ItemStack> itemStackList = deserializeItemStackList(reward.getItemListSerialized());
            MutableText itemText = Text.empty();
            for (ItemStack itemStack: itemStackList) {
                MutableText itemStackName = (MutableText) itemStack.getName();
                itemStackName.setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(itemStack))));
                itemStackName.formatted(itemStack.getRarity().formatting);
                itemText.append(itemStackName).append("x" + itemStack.getCount() + "  ");
            }
            text.append(Text.translatable("command.clockin.reward.title.item", itemText)).append("\n");
        }
        if(reward.getMoney() != 0) text.append(Text.translatable("command.clockin.reward.title.money", reward.getMoney(), CURRENCY_NAME)).append("\n");
        if(reward.getRaffleTickets() != 0) text.append(Text.translatable("command.clockin.reward.title.raffle_ticket", reward.getRaffleTickets())).append("\n");
        if(reward.getMakeupCards() != 0) text.append(Text.translatable("command.clockin.reward.title.makeup_card", reward.getMakeupCards())).append("\n");
        return text;
    }
}
