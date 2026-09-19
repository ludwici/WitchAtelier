package com.catelot.witch_atelier.item;

import com.catelot.witch_atelier.magic.SpellEngine;
import com.catelot.witch_atelier.world.inventory.CanvasGUIMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class MagicPageItemItem extends Item {
    public MagicPageItemItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(world, player, hand);

        if (player instanceof ServerPlayer serverPlayer) {
            BlockPos position = serverPlayer.blockPosition();
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("item.witch_atelier.magic_page_item");
                }

                @Override
                public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                    return false;
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player menuPlayer) {
                    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(position);
                    return new CanvasGUIMenu(id, inventory, buffer);
                }
            }, position);
        }

        return result;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos position = context.getClickedPos();
        SpellEngine.applySheetMagic(
                context.getLevel(),
                position.getX(),
                position.getY(),
                position.getZ(),
                context.getItemInHand(),
                context.getClickedFace()
        );
        return InteractionResult.SUCCESS;
    }
}
