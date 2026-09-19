package com.meowkings.witch_atelier.world.tree;

import com.meowkings.witch_atelier.registries.WitchAtelierModTreePlacers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public final class SilverwoodTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<SilverwoodTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> trunkPlacerParts(instance).apply(instance, SilverwoodTrunkPlacer::new));

    public SilverwoodTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return WitchAtelierModTreePlacers.SILVERWOOD_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos origin, TreeConfiguration config) {
        placeLogAt(level, blockSetter, random, config, origin, 0, -1, -2, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, 1, -1, -1, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, -2, -1, 0, Direction.Axis.X);
        placeLogAt(level, blockSetter, random, config, origin, 3, -1, 0, Direction.Axis.X);
        placeLogAt(level, blockSetter, random, config, origin, -1, -1, 1, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, 2, -1, 1, Direction.Axis.X);
        placeLogAt(level, blockSetter, random, config, origin, 1, -1, 3, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, 0, 0, -1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, -1, 0, 0, Direction.Axis.X);
        placeLogAt(level, blockSetter, random, config, origin, 0, 0, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 0, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 2, 0, 0, Direction.Axis.X);
        placeLogAt(level, blockSetter, random, config, origin, 0, 0, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 0, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 0, 2, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, 0, 1, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 1, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 0, 1, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 1, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 0, 2, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 0, 2, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 2, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 0, 3, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 3, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 0, 3, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 3, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 0, 4, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 4, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 4, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 0, 5, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 5, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 0, 5, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 5, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, -1, 5, 2, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, -1, 6, -1, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, 2, 6, -1, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, 0, 6, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 6, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 0, 6, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 6, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, -2, 6, 3, Direction.Axis.X);
        placeLogAt(level, blockSetter, random, config, origin, -2, 7, -2, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, 3, 7, -2, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, 1, 7, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, -1, 7, 1, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 2, 7, 2, Direction.Axis.Z);
        placeLogAt(level, blockSetter, random, config, origin, 3, 7, 3, Direction.Axis.X);
        placeLogAt(level, blockSetter, random, config, origin, -1, 8, 0, Direction.Axis.Y);
        placeLogAt(level, blockSetter, random, config, origin, 1, 8, 1, Direction.Axis.Y);

        return List.of(new FoliagePlacer.FoliageAttachment(origin, 0, false));
    }

    private void placeLogAt(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, TreeConfiguration config, BlockPos origin, int x, int y, int z, Direction.Axis axis) {
        BlockPos logPos = origin.offset(x, y, z);

        if (y < 0) {
            BlockState state = withAxis(config.trunkProvider.getState(random, logPos), axis);
            blockSetter.accept(logPos, state);
            return;
        }

        placeLog(level, blockSetter, random, logPos, config, state -> withAxis(state, axis));
    }

    private static BlockState withAxis(BlockState state, Direction.Axis axis) {
        if (state.hasProperty(RotatedPillarBlock.AXIS)) {
            return state.setValue(RotatedPillarBlock.AXIS, axis);
        }

        return state;
    }
}
