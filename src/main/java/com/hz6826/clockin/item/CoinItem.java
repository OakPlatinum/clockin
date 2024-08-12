package com.hz6826.clockin.item;

import com.hz6826.clockin.config.BasicConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class CoinItem extends Item {
    private final int denomination;

    public CoinItem(int denomination, Settings settings) {
        super(settings);
        this.denomination = denomination;
    }

    @Override
    public Text getName() {
        return Text.translatable("item.clockin.coin", denomination, BasicConfig.getConfig().getCurrencyName());
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable("item.clockin.coin", denomination, BasicConfig.getConfig().getCurrencyName());
    }
}
