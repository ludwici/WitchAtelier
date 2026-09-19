package com.catelot.witch_atelier.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class SilverwoodPlanksBlock extends Block {
    public SilverwoodPlanksBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1f, 10f));
    }
}
