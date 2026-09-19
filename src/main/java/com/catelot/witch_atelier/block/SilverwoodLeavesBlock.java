package com.catelot.witch_atelier.block;

import com.catelot.witch_atelier.registries.WitchAtelierModBlocks;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SilverwoodLeavesBlock extends Block {
    public SilverwoodLeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);

        if (random.nextFloat() < 0.001F && world.isDay() && world.getBlockState(pos.below()).is(Blocks.AIR)) {
            ItemEntity seed = new ItemEntity(world, pos.getX(), pos.getY() - 1, pos.getZ(), new ItemStack(WitchAtelierModItems.SILVERWOOD_SEED.get()));
            seed.setPickUpDelay(10);
            world.addFreshEntity(seed);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean result = super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);

        if (player.getMainHandItem().is(Items.SHEARS) && world instanceof ServerLevel serverLevel) {
            ItemEntity leaves = new ItemEntity(serverLevel, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(WitchAtelierModBlocks.SILVERWOOD_LEAVES.get()));
            leaves.setPickUpDelay(10);
            serverLevel.addFreshEntity(leaves);
        }

        return result;
    }
}
