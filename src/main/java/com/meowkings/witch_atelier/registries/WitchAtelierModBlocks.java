package com.meowkings.witch_atelier.registries;

import com.meowkings.witch_atelier.WitchAtelier;
import com.meowkings.witch_atelier.block.DepartureAltarBlock;
import com.meowkings.witch_atelier.block.EvaporationflaskBlock;
import com.meowkings.witch_atelier.block.PortalCoreNewBlock;
import com.meowkings.witch_atelier.block.SilentCityDimensionPortalBlock;
import com.meowkings.witch_atelier.block.SilentCityPortalBlockBlock;
import com.meowkings.witch_atelier.block.SilverwoodBranchMatureBlock;
import com.meowkings.witch_atelier.block.SilverwoodBranchYoungBlock;
import com.meowkings.witch_atelier.block.SilverwoodLeavesBlock;
import com.meowkings.witch_atelier.block.SilverwoodLogBlock;
import com.meowkings.witch_atelier.block.SilverwoodPlanksBlock;
import com.meowkings.witch_atelier.block.SilverwoodSaplingBlock;
import com.meowkings.witch_atelier.block.StoneFrameBlock;
import com.meowkings.witch_atelier.block.StoneSymbolCircleBlock;
import com.meowkings.witch_atelier.block.StoneSymbolRhombusBlock;
import com.meowkings.witch_atelier.block.StoneSymbolSquareBlock;
import com.meowkings.witch_atelier.block.StoneSymbolTriangleBlock;
import com.meowkings.witch_atelier.block.SymbolCircleBlock;
import com.meowkings.witch_atelier.block.SymbolRhombusBlock;
import com.meowkings.witch_atelier.block.SymbolSquareBlock;
import com.meowkings.witch_atelier.block.SymbolTriangleBlock;
import com.meowkings.witch_atelier.block.WoodFrameBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class WitchAtelierModBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(WitchAtelier.MODID);

    public static final DeferredBlock<Block> SILVERWOOD_LOG = REGISTRY.register("silverwood_log", SilverwoodLogBlock::new);
    public static final DeferredBlock<Block> SILVERWOOD_LEAVES = REGISTRY.register("silverwood_leaves", SilverwoodLeavesBlock::new);
    public static final DeferredBlock<Block> SILVERWOOD_SAPLING = REGISTRY.register("silverwood_sapling", SilverwoodSaplingBlock::new);
    public static final DeferredBlock<Block> SILVERWOOD_BRANCH_YOUNG = REGISTRY.register("silverwood_branch_young", SilverwoodBranchYoungBlock::new);
    public static final DeferredBlock<Block> SILVERWOOD_BRANCH_MATURE = REGISTRY.register("silverwood_branch_mature", SilverwoodBranchMatureBlock::new);
    public static final DeferredBlock<Block> EVAPORATIONFLASK = REGISTRY.register("evaporationflask", EvaporationflaskBlock::new);
    public static final DeferredBlock<Block> SILVERWOOD_PLANKS = REGISTRY.register("silverwood_planks", SilverwoodPlanksBlock::new);
    public static final DeferredBlock<Block> SILENT_CITY_DIMENSION_PORTAL = REGISTRY.register("silent_city_dimension_portal", SilentCityDimensionPortalBlock::new);
    public static final DeferredBlock<Block> DEPARTURE_ALTAR = REGISTRY.register("departure_altar", DepartureAltarBlock::new);
    public static final DeferredBlock<Block> SILENT_CITY_PORTAL_BLOCK = REGISTRY.register("silent_city_portal_block", SilentCityPortalBlockBlock::new);
    public static final DeferredBlock<Block> SYMBOL_SQUARE = REGISTRY.register("symbol_square", SymbolSquareBlock::new);
    public static final DeferredBlock<Block> SYMBOL_CIRCLE = REGISTRY.register("symbol_circle", SymbolCircleBlock::new);
    public static final DeferredBlock<Block> SYMBOL_TRIANGLE = REGISTRY.register("symbol_triangle", SymbolTriangleBlock::new);
    public static final DeferredBlock<Block> SYMBOL_RHOMBUS = REGISTRY.register("symbol_rhombus", SymbolRhombusBlock::new);
    public static final DeferredBlock<Block> STONE_FRAME = REGISTRY.register("stone_frame", StoneFrameBlock::new);
    public static final DeferredBlock<Block> STONE_SYMBOL_CIRCLE = REGISTRY.register("stone_symbol_circle", StoneSymbolCircleBlock::new);
    public static final DeferredBlock<Block> STONE_SYMBOL_SQUARE = REGISTRY.register("stone_symbol_square", StoneSymbolSquareBlock::new);
    public static final DeferredBlock<Block> STONE_SYMBOL_TRIANGLE = REGISTRY.register("stone_symbol_triangle", StoneSymbolTriangleBlock::new);
    public static final DeferredBlock<Block> STONE_SYMBOL_RHOMBUS = REGISTRY.register("stone_symbol_rhombus", StoneSymbolRhombusBlock::new);
    public static final DeferredBlock<Block> PORTAL_CORE_NEW = REGISTRY.register("portal_core_new", PortalCoreNewBlock::new);
    public static final DeferredBlock<Block> WOOD_FRAME = REGISTRY.register("wood_frame", WoodFrameBlock::new);

    private WitchAtelierModBlocks() {
    }
}
