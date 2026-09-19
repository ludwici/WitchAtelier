package com.catelot.witch_atelier.item.inventory;

import com.catelot.witch_atelier.WitchAtelier;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;

import com.catelot.witch_atelier.world.inventory.CanvasGUIMenu;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;

import javax.annotation.Nonnull;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public class DrawingDataInventoryCapability extends ComponentItemHandler {
    @SubscribeEvent
    public static void onItemDropped(ItemTossEvent event) {
        if (event.getEntity().getItem().getItem() == WitchAtelierModItems.DRAWING_DATA.get()) {
            Player player = event.getPlayer();
            if (player.containerMenu instanceof CanvasGUIMenu) {
                player.closeContainer();
            }
        }
    }

    public DrawingDataInventoryCapability(MutableDataComponentHolder parent) {
        super(parent, DataComponents.CONTAINER, 9);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() != WitchAtelierModItems.DRAWING_DATA.get();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return super.getStackInSlot(slot).copy();
    }
}
