package com.catelot.witch_atelier.block;

import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;

import com.catelot.witch_atelier.registries.WitchAtelierModBlocks;

public class WoodFrameBlock extends Block {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final IntegerProperty FRAME_TYPE = IntegerProperty.create("frame_type", 0, 7);

    public WoodFrameBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(1f, 10f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FRAME_TYPE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, FRAME_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(FRAME_TYPE, 0);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, world, pos, oldState, moving);
        this.updateFrame(state, world, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, world, pos, neighborBlock, fromPos, moving);
        this.updateFrame(state, world, pos);
    }

private void updateFrame(BlockState state, Level world, BlockPos pos) {
    if (!world.isClientSide()) {
        Direction face = state.getValue(FACING);

        boolean up = isFrame(world, pos.above());
        boolean down = isFrame(world, pos.below());

        Direction dL = face.getCounterClockWise();
        Direction dR = face.getClockWise();

        boolean left = isFrame(world, pos.relative(dL));
        boolean right = isFrame(world, pos.relative(dR));

        int type = state.getValue(FRAME_TYPE);

        if (down && right && !up && !left) {
            type = 5;
        }
        else if (down && left && !up && !right) {
            type = 4;
        }
        else if (up && left && !down && !right) {
            type = 7;
        }
        else if (up && right && !down && !left) {
            type = 6;
        }

        else if (left && right) {
            if (isFrame(world, pos.relative(dL).below()) || isFrame(world, pos.relative(dR).below())) {
                type = 1;
            } else {
                type = 3;
            }
        }
        else if (up && down) {
            if (isFrame(world, pos.above().relative(dR)) || isFrame(world, pos.below().relative(dR))) {
                type = 2;
            } else {
                type = 0;
            }
        }

        if (state.getValue(FRAME_TYPE) != type) {
            world.setBlock(pos, state.setValue(FRAME_TYPE, type), 3);
        }
    }
}


    private boolean isFrame(Level world, BlockPos pos) {
        Block b = world.getBlockState(pos).getBlock();
        return b == WitchAtelierModBlocks.WOOD_FRAME.get() ||
               b == WitchAtelierModBlocks.SYMBOL_SQUARE.get() ||
               b == WitchAtelierModBlocks.SYMBOL_CIRCLE.get() ||
               b == WitchAtelierModBlocks.SYMBOL_TRIANGLE.get() ||
               b == WitchAtelierModBlocks.SYMBOL_RHOMBUS.get();
    }
}
