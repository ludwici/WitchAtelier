package com.catelot.witch_atelier.potion;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.registries.WitchAtelierModBlocks;
import com.catelot.witch_atelier.registries.WitchAtelierModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public class SilverwoodParasiteMobEffect extends MobEffect {
    private static final int EFFECT_DURATION = 168000;
    private static final int EFFECT_AMPLIFIER = 1;

    public SilverwoodParasiteMobEffect() {
        super(MobEffectCategory.HARMFUL, -1);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        MobEffectInstance effect = entity.getEffect(WitchAtelierModMobEffects.SILVERWOOD_PARASITE);
        if (effect != null && effect.getDuration() <= 20 && entity.level().getBlockState(entity.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
            entity.level().setBlock(entity.blockPosition(), WitchAtelierModBlocks.SILVERWOOD_SAPLING.get().defaultBlockState(), 3);
            entity.setHealth(0);
        }
        return super.applyEffectTick(entity, amplifier);
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(WitchAtelierModMobEffects.SILVERWOOD_PARASITE)) {
            return;
        }
        if (entity.level().getBlockState(entity.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
            entity.level().setBlock(entity.blockPosition(), WitchAtelierModBlocks.SILVERWOOD_SAPLING.get().defaultBlockState(), 3);
        }
    }

    @SubscribeEvent
    public static void onEntityDamaged(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || !entity.hasEffect(WitchAtelierModMobEffects.SILVERWOOD_PARASITE)) {
            return;
        }

        entity.addEffect(new MobEffectInstance(WitchAtelierModMobEffects.SILVERWOOD_PARASITE, EFFECT_DURATION, EFFECT_AMPLIFIER, true, false));
    }

    @SubscribeEvent
    public static void registerMobEffectExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInGui(MobEffectInstance effect) {
                return false;
            }
        }, WitchAtelierModMobEffects.SILVERWOOD_PARASITE.get());
    }
}
