package com.catelot.witch_atelier.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import com.catelot.witch_atelier.registries.WitchAtelierModItems;

public class MagicInkItem extends Item {
    public MagicInkItem() {
        super(new Item.Properties().durability(1000).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
        return Ingredient.of(new ItemStack(WitchAtelierModItems.MAGIC_INK.get())).test(repairitem);
    }
}
