package com.meowkings.witch_atelier.item.inventory;

import com.meowkings.witch_atelier.WitchAtelier;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;

import com.meowkings.witch_atelier.world.inventory.MagicGuideGUIMenu;
import com.meowkings.witch_atelier.registries.WitchAtelierModItems;

import javax.annotation.Nonnull;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public class MagicGuideInventoryCapability extends ComponentItemHandler {
    @SubscribeEvent
    public static void onItemDropped(ItemTossEvent event) {
        if (event.getEntity().getItem().getItem() == WitchAtelierModItems.MAGIC_GUIDE.get()) {
            Player player = event.getPlayer();
            if (player.containerMenu instanceof MagicGuideGUIMenu) {
                player.closeContainer();
            }
        }
    }

    public MagicGuideInventoryCapability(MutableDataComponentHolder parent) {
        super(parent, DataComponents.CONTAINER, 9);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() != WitchAtelierModItems.MAGIC_GUIDE.get();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return super.getStackInSlot(slot).copy();
    }
}
