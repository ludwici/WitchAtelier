package com.catelot.witch_atelier.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;

import com.catelot.witch_atelier.block.SilentCityDimensionPortalBlock;

public class SilentCityDimensionItem extends Item {
    public SilentCityDimensionItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player entity = context.getPlayer();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ItemStack itemstack = context.getItemInHand();
        Level world = context.getLevel();
        if (!entity.mayUseItemAt(pos, context.getClickedFace(), itemstack)) {
            return InteractionResult.FAIL;
        } else {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            boolean success = false;
            if (world.isEmptyBlock(pos) && true) {
                SilentCityDimensionPortalBlock.portalSpawn(world, pos);
                itemstack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(context.getHand()));
                success = true;
            }
            return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
    }
}
