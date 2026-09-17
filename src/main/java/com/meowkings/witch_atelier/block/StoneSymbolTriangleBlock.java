package com.meowkings.witch_atelier.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

public class StoneSymbolTriangleBlock extends Block {
    public StoneSymbolTriangleBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1f, 10f));
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        if (!world.isClientSide()) {
            com.meowkings.witch_atelier.PortalManager.registerStonePortal(world, pos);
        }
    }
}
