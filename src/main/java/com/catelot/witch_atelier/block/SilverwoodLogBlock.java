package com.catelot.witch_atelier.block;

import com.catelot.witch_atelier.registries.WitchAtelierModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SilverwoodLogBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public SilverwoodLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return RotatedPillarBlock.rotatePillar(state, rotation);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        tryGrowBranch(world, pos.west(), random);
        tryGrowBranch(world, pos.east(), random);
        tryGrowBranch(world, pos.north(), random);
        tryGrowBranch(world, pos.south(), random);
    }

    private static void tryGrowBranch(ServerLevel world, BlockPos position, RandomSource random) {
        if (random.nextFloat() < 0.005F && world.getBlockState(position).is(Blocks.AIR)) {
            world.setBlock(position, WitchAtelierModBlocks.SILVERWOOD_BRANCH_YOUNG.get().defaultBlockState(), 3);
        }
    }
}
