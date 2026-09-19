package com.catelot.witch_atelier.registries;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.potion.SilverwoodParasiteMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class WitchAtelierModMobEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, WitchAtelier.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> SILVERWOOD_PARASITE = REGISTRY.register("silverwood_parasite", SilverwoodParasiteMobEffect::new);

    private WitchAtelierModMobEffects() {
    }
}
