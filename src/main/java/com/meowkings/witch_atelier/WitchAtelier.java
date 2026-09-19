package com.meowkings.witch_atelier;

import com.meowkings.witch_atelier.registries.WitchAtelierModBlockEntities;
import com.meowkings.witch_atelier.registries.WitchAtelierModBlocks;
import com.meowkings.witch_atelier.registries.WitchAtelierModItems;
import com.meowkings.witch_atelier.registries.WitchAtelierModMenus;
import com.meowkings.witch_atelier.registries.WitchAtelierModMobEffects;
import com.meowkings.witch_atelier.network.ModNetworking;
import com.meowkings.witch_atelier.registries.WitchAtelierModTabs;
import com.meowkings.witch_atelier.registries.WitchAtelierModTreePlacers;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(WitchAtelier.MODID)
public final class WitchAtelier {
    public static final String MODID = "witch_atelier";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WitchAtelier(IEventBus modEventBus) {
        WitchAtelierModBlocks.REGISTRY.register(modEventBus);
        WitchAtelierModBlockEntities.REGISTRY.register(modEventBus);
        WitchAtelierModItems.REGISTRY.register(modEventBus);
        WitchAtelierModTabs.REGISTRY.register(modEventBus);
        WitchAtelierModTreePlacers.TRUNK_PLACERS.register(modEventBus);
        WitchAtelierModTreePlacers.FOLIAGE_PLACERS.register(modEventBus);
        WitchAtelierModMobEffects.REGISTRY.register(modEventBus);
        WitchAtelierModMenus.REGISTRY.register(modEventBus);

        modEventBus.addListener(ModNetworking::register);
    }
}
