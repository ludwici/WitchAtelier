package com.meowkings.witch_atelier.registries;

import com.meowkings.witch_atelier.WitchAtelier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public final class WitchAtelierModTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WitchAtelier.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WITCH_ATELIER_TAB = REGISTRY.register(
            "witch_atelier_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.witch_atelier.witch_atelier_tab"))
                    .icon(() -> new ItemStack(Items.OMINOUS_BOTTLE))
                    .displayItems((parameters, tabData) -> {
                        tabData.accept(WitchAtelierModBlocks.SILVERWOOD_LOG.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.SILVERWOOD_LEAVES.get().asItem());
                        tabData.accept(WitchAtelierModItems.SILVERWOOD_SEED.get());
                        tabData.accept(WitchAtelierModBlocks.SILVERWOOD_SAPLING.get().asItem());
                        tabData.accept(WitchAtelierModItems.SILVERWOOD_KNIFE.get());
                        tabData.accept(WitchAtelierModItems.SILVERWOOD_FIBER.get());
                        tabData.accept(WitchAtelierModItems.SILVERWOOD_TWIG.get());
                        tabData.accept(WitchAtelierModBlocks.EVAPORATIONFLASK.get().asItem());
                        tabData.accept(WitchAtelierModItems.DRAWING_DATA.get());
                        tabData.accept(WitchAtelierModItems.MAGIC_PAGE_ITEM.get());
                        tabData.accept(WitchAtelierModItems.MAGIC_PEN.get());
                        tabData.accept(WitchAtelierModItems.MAGIC_INK.get());
                        tabData.accept(WitchAtelierModItems.CREATIVE_INK.get());
                        tabData.accept(WitchAtelierModItems.MAGIC_GUIDE.get());
                        tabData.accept(WitchAtelierModBlocks.SILVERWOOD_PLANKS.get().asItem());
                        tabData.accept(WitchAtelierModItems.SILVERWOOD_STICK.get());
                        tabData.accept(WitchAtelierModBlocks.DEPARTURE_ALTAR.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.SILENT_CITY_PORTAL_BLOCK.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.SYMBOL_SQUARE.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.SYMBOL_CIRCLE.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.SYMBOL_TRIANGLE.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.SYMBOL_RHOMBUS.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.STONE_FRAME.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.STONE_SYMBOL_CIRCLE.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.STONE_SYMBOL_SQUARE.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.STONE_SYMBOL_TRIANGLE.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.STONE_SYMBOL_RHOMBUS.get().asItem());
                        tabData.accept(WitchAtelierModBlocks.WOOD_FRAME.get().asItem());
                        tabData.accept(WitchAtelierModItems.WOOD_BLOOD_STICK.get());
                        tabData.accept(WitchAtelierModItems.SYLPH_SHOES_BOOTS.get());
                    })
                    .withSearchBar()
                    .build()
    );

    private WitchAtelierModTabs() {
    }

    @SubscribeEvent
    public static void buildVanillaTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(WitchAtelierModItems.SILENT_CITY_DIMENSION.get());
        }
    }
}
