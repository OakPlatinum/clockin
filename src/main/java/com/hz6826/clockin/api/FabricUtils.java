package com.hz6826.clockin.api;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.MailInterface;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FabricUtils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
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
                ClockIn.LOGGER.error("Invalid NBT data for item: " + s[0], e);
            }
        }
        return stackList;
    }

    public static boolean giveItemList(@NotNull List<ItemStack> stackList, PlayerEntity player, boolean convertToMail) {
        List<ItemStack> remainingStackList = new ArrayList<>();
        List<Integer> candidateSlots = new ArrayList<>();
        PlayerInventory inv = player.getInventory();
        for (ItemStack stack : stackList) {
            int candidateSlot = getCandidateSlot(stack, inv);
            if(!convertToMail && candidateSlot == -1){
                return false;
            }
            candidateSlots.add(candidateSlot);
            inv.insertStack(candidateSlot, stack);
        }
        for (int i = 0; i < stackList.size(); i++) {
            if(candidateSlots.get(i) == -1) {
                remainingStackList.add(stackList.get(i));
            } else {
                insertStack(stackList.get(i), player, candidateSlots.get(i));
            }
        }
        sendRewardMail(player, remainingStackList, "Remaining Reward " + generateRandomString());
        if(!remainingStackList.isEmpty()) {
            Text remainingItemsText = Text.translatable("command.clockin.reward.give.success.mail.receiver.item")
                    .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, generateReadableRewardItemList(remainingStackList)))).formatted(Formatting.UNDERLINE);
            player.sendMessage(Text.translatable("command.clockin.reward.give.success.mail.receiver", remainingItemsText).formatted(Formatting.GREEN));
        }
        return true;
    }
    private static String generateRandomString() {
        int length = 8;
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }
    public static void sendRewardMail(PlayerEntity player, List<ItemStack> stackList, String content) {
        ClockInServer.DBM.sendMail(ClockInServer.DBM.SERVER_UUID, player.getUuidAsString(), Timestamp.valueOf(LocalDateTime.now()), content, serializeItemStackList(stackList), false, false);
    }
    public static int getCandidateSlot(ItemStack stack, PlayerInventory inv) {
        int candidateSlot = canItemBeAdded(stack, inv);  // FIXME: this only tests for hot-bar slots
        if (candidateSlot == -1) candidateSlot = inv.getEmptySlot();
        return candidateSlot;
    }
    public static int canItemBeAdded(ItemStack stack, PlayerInventory inventory) {
        for (int i = 0; i < 36; i++) {
            ItemStack existingStack = inventory.main.get(i);
            if (inventory.canStackAddMore(existingStack, stack)) {
                return i;
            }
        }
        return -1;
    }
    public static void insertStack(@NotNull ItemStack stack, PlayerEntity player, int slot) {
        if (stack.isEmpty()) return;
        player.getInventory().insertStack(slot, stack);
    }
    public static Text generateReadableReward(RewardInterface reward){
        MutableText text = Text.empty();
        if(!reward.getItemListSerialized().isBlank()) {
            ArrayList<ItemStack> itemStackList = deserializeItemStackList(reward.getItemListSerialized());
            text.append(Text.translatable("command.clockin.reward.title.item", generateReadableRewardItemList(itemStackList))).append("\n");
        }
        if(reward.getMoney() != 0) text.append(Text.translatable("command.clockin.reward.title.money", reward.getMoney(), CURRENCY_NAME)).append("\n");
        if(reward.getRaffleTickets() != 0) text.append(Text.translatable("command.clockin.reward.title.raffle_ticket", reward.getRaffleTickets())).append("\n");
        if(reward.getMakeupCards() != 0) text.append(Text.translatable("command.clockin.reward.title.makeup_card", reward.getMakeupCards())).append("\n");
        return text;
    }
    public static Text generateReadableRewardItemList(List<ItemStack> itemStackList){
        MutableText itemText = Text.empty();
        for (ItemStack itemStack: itemStackList) {
            itemText.append(generateItemStackComponent(itemStack));
        }
        return itemText;
    }
    public static MutableText generateItemStackComponent(ItemStack itemStack) {
        MutableText itemText = Text.empty();
        MutableText itemStackName = (MutableText) itemStack.getName();
        itemStackName.setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(itemStack))));
        itemStackName.formatted(itemStack.getRarity().formatting);
        itemText.append(itemStackName).append(Text.literal("x" + itemStack.getCount() + "  ").formatted(Formatting.GRAY));
        return itemText;
    }

    public static Text giveReward(PlayerEntity player, String rewardString) {
        RewardInterface reward = ClockInServer.DBM.getRewardOrNew(rewardString);
        Text rewardText = null;
        if(!reward.isNew()) {
            FabricUtils.giveItemList(FabricUtils.deserializeItemStackList(reward.getItemListSerialized()), player, true);
            UserWithAccountAbstract user = ClockInServer.DBM.getUserByUUID(player.getUuidAsString());
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
    public static void givePhysicalMoney(PlayerEntity player, int amount){
        ArrayList<ItemStack> itemStackList = parseAmountToPhysicalMoney(amount);
        FabricUtils.giveItemList(itemStackList, player, true);
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

    public static void displayMailListTitle(PlayerEntity player){
        player.sendMessage(Text.translatable("command.clockin.mail.title").formatted(Formatting.GOLD), false);
    }

    public static void displayMailList(PlayerEntity player, List<MailInterface> mailList){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MutableText text = Text.empty();
        for (MailInterface mail : mailList) {
            Text senderNameText = (Objects.equals(mail.getSenderUuid(), ClockInServer.DBM.SERVER_UUID) ?
                    Text.translatable("command.clockin.system").formatted(Formatting.GOLD) :
                    Text.literal(ClockInServer.DBM.getUserByUUID(mail.getSenderUuid()).getPlayerName()).formatted(Formatting.BLUE)).formatted(Formatting.BOLD);
            MutableText mailText = Text.empty();
            mailText.append(Text.translatable(
                    "command.clockin.mail.content.overview",
                    mail.getSendTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(dateTimeFormatter),
                    senderNameText,
                    Text.literal(mail.getContent())
            ));
            if(mail.getSerializedAttachment() != null && !mail.getSerializedAttachment().isBlank()) {
                Text rewardTextTooltip = FabricUtils.generateReadableRewardItemList(FabricUtils.deserializeItemStackList(mail.getSerializedAttachment()));
                MutableText rewardText = Text.translatable("command.clockin.mail.content.overview.reward").setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, rewardTextTooltip)));
                if(mail.getAttachmentFetched()){
                    rewardText = rewardText.formatted(Formatting.GRAY, Formatting.STRIKETHROUGH);
                } else {
                    rewardText = rewardText.formatted(Formatting.AQUA)
                            .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clockin mail getAttachment " + mail.getId())));
                }
                mailText.append(Text.literal("  ").append(rewardText));
            }
            text.append(mailText).append("\n");
        }
        player.sendMessage(text, false);
    }

    public static void displayMailListBottomBar(PlayerEntity player, int page, int pageCount){
        MutableText text = Text.empty();
        text.append(page > 1 ?
                Text.translatable("command.clockin.mail.content.overview.bar.previous")
                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clockin mail get " + (page - 1))))
                        .formatted(Formatting.AQUA, Formatting.BOLD, Formatting.UNDERLINE):
                Text.translatable("command.clockin.mail.content.overview.bar.previous")
                        .formatted(Formatting.GRAY, Formatting.ITALIC));
        text.append(Text.literal("  "));
        text.append(Text.translatable("command.clockin.mail.content.overview.bar.page", page, pageCount).formatted(Formatting.AQUA, Formatting.BOLD));
        text.append(Text.literal("  "));
        text.append(page < pageCount ?
                Text.translatable("command.clockin.mail.content.overview.bar.next")
                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clockin mail get " + (page + 1))))
                        .formatted(Formatting.AQUA, Formatting.BOLD, Formatting.UNDERLINE):
                Text.translatable("command.clockin.mail.content.overview.bar.next")
                        .formatted(Formatting.GRAY, Formatting.ITALIC));
        player.sendMessage(text, false);
    }
}
