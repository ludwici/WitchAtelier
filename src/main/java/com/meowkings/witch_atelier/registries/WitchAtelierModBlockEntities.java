package com.meowkings.witch_atelier.registries;

import com.meowkings.witch_atelier.WitchAtelier;
import com.meowkings.witch_atelier.block.entity.EvaporationFlaskBlockEntity;
import com.meowkings.witch_atelier.block.entity.SymbolCircleBlockEntity;
import com.meowkings.witch_atelier.block.entity.SymbolRhombusBlockEntity;
import com.meowkings.witch_atelier.block.entity.SymbolSquareBlockEntity;
import com.meowkings.witch_atelier.block.entity.SymbolTriangleBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public final class WitchAtelierModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, WitchAtelier.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EvaporationFlaskBlockEntity>> EVAPORATIONFLASK = register("evaporationflask", WitchAtelierModBlocks.EVAPORATIONFLASK, EvaporationFlaskBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SymbolSquareBlockEntity>> SYMBOL_SQUARE = register("symbol_square", WitchAtelierModBlocks.SYMBOL_SQUARE, SymbolSquareBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SymbolCircleBlockEntity>> SYMBOL_CIRCLE = register("symbol_circle", WitchAtelierModBlocks.SYMBOL_CIRCLE, SymbolCircleBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SymbolTriangleBlockEntity>> SYMBOL_TRIANGLE = register("symbol_triangle", WitchAtelierModBlocks.SYMBOL_TRIANGLE, SymbolTriangleBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SymbolRhombusBlockEntity>> SYMBOL_RHOMBUS = register("symbol_rhombus", WitchAtelierModBlocks.SYMBOL_RHOMBUS, SymbolRhombusBlockEntity::new);

    private WitchAtelierModBlockEntities() {
    }

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(
            String registryName,
            DeferredHolder<Block, Block> block,
            BlockEntityType.BlockEntitySupplier<T> supplier
    ) {
        return REGISTRY.register(
                registryName,
                () -> BlockEntityType.Builder.of(supplier, block.get()).build(null)
        );
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, EVAPORATIONFLASK.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SYMBOL_SQUARE.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SYMBOL_CIRCLE.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SYMBOL_TRIANGLE.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SYMBOL_RHOMBUS.get(), SidedInvWrapper::new);
    }
}
