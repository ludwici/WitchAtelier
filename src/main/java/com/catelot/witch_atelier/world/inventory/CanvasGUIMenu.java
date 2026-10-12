package com.catelot.witch_atelier.world.inventory;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import com.catelot.witch_atelier.registries.WitchAtelierModMenus;


public class CanvasGUIMenu extends AbstractContainerMenu {
    public final Level world;
    public final Player entity;
    public final boolean isNotebook;
    private final int openedSlot;
    private final ItemStack openedStack;

    public CanvasGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(WitchAtelierModMenus.CANVAS_GUI.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();

        this.openedSlot = inv.selected;
        this.openedStack = inv.player.getMainHandItem();
        this.isNotebook = openedStack.is(WitchAtelierModItems.DRAWING_DATA.get());
    }

    public ItemStack getEditingStack() {
        if (entity.getInventory().selected != openedSlot || entity.getMainHandItem() != openedStack) {
            return ItemStack.EMPTY;
        }

        if (!openedStack.is(WitchAtelierModItems.DRAWING_DATA.get()) && !openedStack.is(WitchAtelierModItems.MAGIC_PAGE_ITEM.get())) {
            return ItemStack.EMPTY;
        }

        return openedStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return player == entity && !getEditingStack().isEmpty();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
