package com.catelot.witch_atelier.block;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import com.catelot.witch_atelier.registries.WitchAtelierModBlocks;
import com.catelot.witch_atelier.block.entity.SymbolSquareBlockEntity;

public class SymbolSquareBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final IntegerProperty FRAME_TYPE = IntegerProperty.create("frame_type", 0, 7);

    public SymbolSquareBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.0f, 10.0f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FRAME_TYPE, 0));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide()) {
            com.catelot.witch_atelier.PortalManager.onWoodSymbolClicked(world, pos, player, "S");
            world.scheduleTick(pos, this, 600);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        com.catelot.witch_atelier.PortalManager.onSymbolTimeout(world, pos, "S");
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
            } else if (up && down) {
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
        return b == WitchAtelierModBlocks.WOOD_FRAME.get() || b == WitchAtelierModBlocks.SYMBOL_SQUARE.get() || b == WitchAtelierModBlocks.SYMBOL_CIRCLE.get() || b == WitchAtelierModBlocks.SYMBOL_TRIANGLE.get()
                || b == WitchAtelierModBlocks.SYMBOL_RHOMBUS.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SymbolSquareBlockEntity(pos, state);
    }
}
