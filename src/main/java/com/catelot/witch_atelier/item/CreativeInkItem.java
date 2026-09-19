package com.catelot.witch_atelier.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class CreativeInkItem extends Item {
    public CreativeInkItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }
}
