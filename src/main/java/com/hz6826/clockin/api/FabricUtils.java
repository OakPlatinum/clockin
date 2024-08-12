package com.hz6826.clockin.api;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FabricUtils {
    public static String CURRENCY_NAME = BasicConfig.getConfig().getCurrencyName();

    public static String serializeItemStackList(List<ItemStack> stackList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ItemStack stack : stackList) {
            stringBuilder
                    .append(Registries.ITEM.getId(stack.getItem()))
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
        if(serializedStackList.isBlank()) return stackList;
        String[] strings = serializedStackList.split(";");
        for (String string : strings) {
            String[] s = string.split("\\|");
            RegistryKey<Item> itemKey = RegistryKey.of(Registries.ITEM.getKey(), new Identifier(s[0]));

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
    public static void giveItemList(@NotNull List<ItemStack> stackList, PlayerEntity player) {
        for (ItemStack stack : stackList) {
            giveItem(stack, player);
        }
    }
    public static void giveItem(@NotNull ItemStack stack, PlayerEntity player) {
        if (!stack.isEmpty()) {
            if (player.getInventory().getEmptySlot() >= 1) {
                player.giveItemStack(stack);
            } else {
                player.dropItem(stack, false);
            }
        }
    }
    public static Text generateReadableReward(RewardInterface reward){
        MutableText text = Text.empty();
        if(!reward.getItemListSerialized().isBlank()) {
            ArrayList<ItemStack> itemStackList = deserializeItemStackList(reward.getItemListSerialized());
            MutableText itemText = Text.empty();
            for (ItemStack itemStack: itemStackList) {
                itemText.append(generateItemStackComponent(itemStack));
            }
            text.append(Text.translatable("command.clockin.reward.title.item", itemText)).append("\n");
        }
        if(reward.getMoney() != 0) text.append(Text.translatable("command.clockin.reward.title.money", reward.getMoney(), CURRENCY_NAME)).append("\n");
        if(reward.getRaffleTickets() != 0) text.append(Text.translatable("command.clockin.reward.title.raffle_ticket", reward.getRaffleTickets())).append("\n");
        if(reward.getMakeupCards() != 0) text.append(Text.translatable("command.clockin.reward.title.makeup_card", reward.getMakeupCards())).append("\n");
        return text;
    }
    public static MutableText generateItemStackComponent(ItemStack itemStack) {
        MutableText itemText = Text.empty();
        MutableText itemStackName = (MutableText) itemStack.getName();
        itemStackName.setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(itemStack))));
        itemStackName.formatted(itemStack.getRarity().formatting);
        itemText.append(itemStackName).append(Text.literal("x" + itemStack.getCount() + "  ").formatted(Formatting.GRAY));
        return itemText;
    }

    public static Text giveReward(ServerPlayerEntity player, String rewardString){
        RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(rewardString);
        Text rewardText = null;
        if(!reward.isNew()) {
            FabricUtils.giveItemList(FabricUtils.deserializeItemStackList(reward.getItemListSerialized()), player);
            UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
            user.addBalance(reward.getMoney());
            user.addRaffleTicket(reward.getRaffleTickets());
            user.addMakeupCard(reward.getMakeupCards());
            rewardText = FabricUtils.generateReadableReward(reward);
        }
        return rewardText;
    }

    public static Text giveReward(PlayerEntity player, String rewardString){
        RewardInterface reward = ClockInServer.DATABASE_MANAGER.getRewardOrNew(rewardString);
        Text rewardText = null;
        if(!reward.isNew()) {
            FabricUtils.giveItemList(FabricUtils.deserializeItemStackList(reward.getItemListSerialized()), player);
            UserWithAccountAbstract user = ClockInServer.DATABASE_MANAGER.getUserByUUID(player.getUuidAsString());
            user.addBalance(reward.getMoney());
            user.addRaffleTicket(reward.getRaffleTickets());
            user.addMakeupCard(reward.getMakeupCards());
            rewardText = FabricUtils.generateReadableReward(reward);
        }
        return rewardText;
    }

    public static @NotNull ArrayList<ItemStack> parseAmountToPhysicalMoney(int amount){
        ArrayList<ItemStack> itemStackList = new ArrayList<>();
        // Sort the currency items by key (denomination) in descending order
        List<Map.Entry<Integer, String>> sortedCurrencyItems = BasicConfig.getConfig().getPhysicalCurrencyItemIdsSorted();

        // Iterate over the sorted currency items
        for (Map.Entry<Integer, String> entry : sortedCurrencyItems) {
            int denomination = entry.getKey();
            String itemId = entry.getValue();
            int itemCount = 0;

            // Determine how many of this denomination can be used
            while (amount >= denomination) {
                amount -= denomination; // Subtract the denomination from the amount
                itemCount++; // Increment the count of items
            }
            itemStackList.add(new ItemStack(Registries.ITEM.get(new Identifier(itemId)), itemCount));
        }

        return itemStackList; // Return the list of ItemStacks
    }
    public static void givePhysicalMoney(ServerPlayerEntity player, int amount){
        ArrayList<ItemStack> itemStackList = parseAmountToPhysicalMoney(amount);
        FabricUtils.giveItemList(itemStackList, player);
    }
    public static void givePhysicalMoney(PlayerEntity player, int amount){
        ArrayList<ItemStack> itemStackList = parseAmountToPhysicalMoney(amount);
        FabricUtils.giveItemList(itemStackList, player);
    }

    public static int parsePhysicalMoneyToAmount(ArrayList<ItemStack> itemStackList){
        int amount = 0;

        Map<String, Integer> currencyMap = BasicConfig.getConfig().getPhysicalCurrencyItemIds();

        for (ItemStack itemStack : itemStackList) {
            int itemCount = itemStack.getCount();
            String itemName = Registries.ITEM.getId(itemStack.getItem()).toString();

            Integer denomination = currencyMap.get(itemName);
            if (denomination == null) {
                ClockIn.LOGGER.error("Invalid physical currency item: " + itemName + " with count: " + itemCount);
                continue;
            }

            amount += itemCount * denomination;
        }
        return amount;
    }
}
