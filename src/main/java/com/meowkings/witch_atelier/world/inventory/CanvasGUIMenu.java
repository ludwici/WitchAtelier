package com.meowkings.witch_atelier.world.inventory;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import com.meowkings.witch_atelier.registries.WitchAtelierModMenus;


public class CanvasGUIMenu extends AbstractContainerMenu {
    public final Level world;
    public final Player entity;
    public boolean isNotebook = false;

    public CanvasGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(WitchAtelierModMenus.CANVAS_GUI.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();

        ItemStack stack = this.entity.getMainHandItem();
        if (!stack.isEmpty()) {
            CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
            if (cd != null && cd.copyTag().contains("NotebookPage")) {
                this.isNotebook = true;
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
