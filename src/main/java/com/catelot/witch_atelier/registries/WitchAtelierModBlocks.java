package com.catelot.witch_atelier.registries;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.block.DepartureAltarBlock;
import com.catelot.witch_atelier.block.EvaporationflaskBlock;
import com.catelot.witch_atelier.block.PortalCoreNewBlock;
import com.catelot.witch_atelier.block.SilentCityDimensionPortalBlock;
import com.catelot.witch_atelier.block.SilentCityPortalBlockBlock;
import com.catelot.witch_atelier.block.SilverwoodBranchMatureBlock;
import com.catelot.witch_atelier.block.SilverwoodBranchYoungBlock;
import com.catelot.witch_atelier.block.SilverwoodLeavesBlock;
import com.catelot.witch_atelier.block.SilverwoodLogBlock;
import com.catelot.witch_atelier.block.SilverwoodPlanksBlock;
import com.catelot.witch_atelier.block.SilverwoodSaplingBlock;
import com.catelot.witch_atelier.block.StoneFrameBlock;
import com.catelot.witch_atelier.block.StoneSymbolCircleBlock;
import com.catelot.witch_atelier.block.StoneSymbolRhombusBlock;
import com.catelot.witch_atelier.block.StoneSymbolSquareBlock;
import com.catelot.witch_atelier.block.StoneSymbolTriangleBlock;
import com.catelot.witch_atelier.block.SymbolCircleBlock;
import com.catelot.witch_atelier.block.SymbolRhombusBlock;
import com.catelot.witch_atelier.block.SymbolSquareBlock;
import com.catelot.witch_atelier.block.SymbolTriangleBlock;
import com.catelot.witch_atelier.block.WoodFrameBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class WitchAtelierModBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(WitchAtelier.MODID);

    public static final DeferredBlock<Block> SILVERWOOD_LOG = REGISTRY.register("silverwood_log", () -> new SilverwoodLogBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2f, 10f).noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, position) -> false).ignitedByLava()));
    public static final DeferredBlock<Block> SILVERWOOD_LEAVES = REGISTRY.register("silverwood_leaves", () -> new SilverwoodLeavesBlock(BlockBehaviour.Properties.of().sound(SoundType.AZALEA_LEAVES).strength(0.1f, 1f).requiresCorrectToolForDrops().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, position) -> false).ignitedByLava()));
    public static final DeferredBlock<Block> SILVERWOOD_SAPLING = REGISTRY.register("silverwood_sapling", () -> new SilverwoodSaplingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().sound(SoundType.GRASS).strength(-1, 3600000).noCollission().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> SILVERWOOD_BRANCH_YOUNG = REGISTRY.register("silverwood_branch_young", () -> new SilverwoodBranchYoungBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).strength(-1, 3600000).noCollission().randomTicks().isRedstoneConductor((state, getter, position) -> false)));
    public static final DeferredBlock<Block> SILVERWOOD_BRANCH_MATURE = REGISTRY.register("silverwood_branch_mature", () -> new SilverwoodBranchMatureBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).strength(-1, 3600000).noCollission().isRedstoneConductor((state, getter, position) -> false)));
    public static final DeferredBlock<Block> EVAPORATIONFLASK = REGISTRY.register("evaporationflask", () -> new EvaporationflaskBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).strength(1f, 10f).noOcclusion().isRedstoneConductor((state, getter, position) -> false)));
    public static final DeferredBlock<Block> SILVERWOOD_PLANKS = REGISTRY.register("silverwood_planks", () -> new SilverwoodPlanksBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1f, 10f)));
    public static final DeferredBlock<Block> SILENT_CITY_DIMENSION_PORTAL = REGISTRY.register("silent_city_dimension_portal", () -> new SilentCityDimensionPortalBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().pushReaction(PushReaction.BLOCK).strength(-1.0F).sound(SoundType.GLASS).lightLevel(state -> 0).noLootTable()));
    public static final DeferredBlock<Block> DEPARTURE_ALTAR = REGISTRY.register("departure_altar", () -> new DepartureAltarBlock(BlockBehaviour.Properties.of().sound(SoundType.AMETHYST).strength(-1, 3600000).requiresCorrectToolForDrops().noCollission().isRedstoneConductor((state, getter, position) -> false)));
    public static final DeferredBlock<Block> SILENT_CITY_PORTAL_BLOCK = REGISTRY.register("silent_city_portal_block", () -> new SilentCityPortalBlockBlock(BlockBehaviour.Properties.of().sound(SoundType.AMETHYST).strength(1f, 10f).noOcclusion().isRedstoneConductor((state, getter, position) -> false)));
    public static final DeferredBlock<Block> SYMBOL_SQUARE = REGISTRY.register("symbol_square", () -> new SymbolSquareBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.0f, 10.0f).noOcclusion()));
    public static final DeferredBlock<Block> SYMBOL_CIRCLE = REGISTRY.register("symbol_circle", () -> new SymbolCircleBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.0f, 10.0f).noOcclusion()));
    public static final DeferredBlock<Block> SYMBOL_TRIANGLE = REGISTRY.register("symbol_triangle", () -> new SymbolTriangleBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.0f, 10.0f).noOcclusion()));
    public static final DeferredBlock<Block> SYMBOL_RHOMBUS = REGISTRY.register("symbol_rhombus", () -> new SymbolRhombusBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.0f, 10.0f).noOcclusion()));
    public static final DeferredBlock<Block> STONE_FRAME = REGISTRY.register("stone_frame", () -> new StoneFrameBlock(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(1f, 10f)));
    public static final DeferredBlock<Block> STONE_SYMBOL_CIRCLE = REGISTRY.register("stone_symbol_circle", () -> new StoneSymbolCircleBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1f, 10f)));
    public static final DeferredBlock<Block> STONE_SYMBOL_SQUARE = REGISTRY.register("stone_symbol_square", () -> new StoneSymbolSquareBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1f, 10f)));
    public static final DeferredBlock<Block> STONE_SYMBOL_TRIANGLE = REGISTRY.register("stone_symbol_triangle", () -> new StoneSymbolTriangleBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1f, 10f)));
    public static final DeferredBlock<Block> STONE_SYMBOL_RHOMBUS = REGISTRY.register("stone_symbol_rhombus", () -> new StoneSymbolRhombusBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1f, 10f)));
    public static final DeferredBlock<Block> PORTAL_CORE_NEW = REGISTRY.register("portal_core_new", () -> new PortalCoreNewBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).strength(-1.0f, 3600000.0f).noCollission().noOcclusion().lightLevel(state -> 15).isRedstoneConductor((state, getter, position) -> false)));
    public static final DeferredBlock<Block> WOOD_FRAME = REGISTRY.register("wood_frame", () -> new WoodFrameBlock(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(1f, 10f).noOcclusion()));

    private WitchAtelierModBlocks() {
    }
}
