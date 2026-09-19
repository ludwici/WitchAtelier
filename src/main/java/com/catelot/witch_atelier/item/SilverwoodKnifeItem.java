package com.catelot.witch_atelier.item;

import com.catelot.witch_atelier.registries.WitchAtelierModBlocks;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SilverwoodKnifeItem extends Item {
    public SilverwoodKnifeItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return 1;
    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level world, BlockState blockState, BlockPos pos, LivingEntity entity) {
        itemStack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity entity, LivingEntity sourceEntity) {
        itemStack.hurtAndBreak(2, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 2;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos position = context.getClickedPos();
        ItemStack knife = context.getItemInHand();

        if (!(world instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        if (world.getBlockState(position).is(WitchAtelierModBlocks.SILVERWOOD_BRANCH_YOUNG.get())) {
            cutBranch(serverLevel, position, knife, new ItemStack(WitchAtelierModItems.SILVERWOOD_TWIG.get()));
        } else if (world.getBlockState(position).is(WitchAtelierModBlocks.SILVERWOOD_BRANCH_MATURE.get())) {
            cutBranch(serverLevel, position, knife, new ItemStack(WitchAtelierModItems.SILVERWOOD_FIBER.get()));
        }

        return InteractionResult.SUCCESS;
    }

    private static void cutBranch(ServerLevel level, BlockPos position, ItemStack knife, ItemStack drop) {
        knife.hurtAndBreak(1, level, null, item -> {
        });
        level.setBlock(position, Blocks.AIR.defaultBlockState(), 3);

        ItemEntity droppedItem = new ItemEntity(level, position.getX(), position.getY(), position.getZ(), drop);
        droppedItem.setPickUpDelay(10);
        level.addFreshEntity(droppedItem);
    }
}
