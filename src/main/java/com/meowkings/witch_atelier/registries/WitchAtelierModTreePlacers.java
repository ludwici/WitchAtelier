package com.meowkings.witch_atelier.registries;

import com.meowkings.witch_atelier.WitchAtelier;
import com.meowkings.witch_atelier.world.tree.SilverwoodFoliagePlacer;
import com.meowkings.witch_atelier.world.tree.SilverwoodTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class WitchAtelierModTreePlacers {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, WitchAtelier.MODID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, WitchAtelier.MODID);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<SilverwoodTrunkPlacer>> SILVERWOOD_TRUNK_PLACER = TRUNK_PLACERS.register("silverwood_trunk_placer", () -> new TrunkPlacerType<>(SilverwoodTrunkPlacer.CODEC));
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<SilverwoodFoliagePlacer>> SILVERWOOD_FOLIAGE_PLACER = FOLIAGE_PLACERS.register("silverwood_foliage_placer", () -> new FoliagePlacerType<>(SilverwoodFoliagePlacer.CODEC));

    private WitchAtelierModTreePlacers() {
    }
}
