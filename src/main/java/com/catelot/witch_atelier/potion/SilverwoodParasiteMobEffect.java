package com.catelot.witch_atelier.potion;

import com.catelot.witch_atelier.registries.WitchAtelierModBlocks;
import com.catelot.witch_atelier.registries.WitchAtelierModMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;

public class SilverwoodParasiteMobEffect extends MobEffect {
    private static final int EFFECT_DURATION = 168000;
    private static final int EFFECT_AMPLIFIER = 1;
    private static final int TERMINAL_DURATION = 20;

    public SilverwoodParasiteMobEffect() {
        super(MobEffectCategory.HARMFUL, -1);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration == TERMINAL_DURATION;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide() && isStandingOnGrass(entity)) {
            entity.setHealth(0.0F);
        }

        return true;
    }

    @Override
    public void onMobHurt(LivingEntity entity, int amplifier, DamageSource damageSource, float amount) {
        if (entity.level().isClientSide()) {
            return;
        }

        entity.addEffect(createInstance(false));
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        if (reason != Entity.RemovalReason.KILLED || entity.level().isClientSide() || !isStandingOnGrass(entity)) {
            return;
        }

        entity.level().setBlock(entity.blockPosition(), WitchAtelierModBlocks.SILVERWOOD_SAPLING.get().defaultBlockState(), 3);
    }

    public static MobEffectInstance createInstance(boolean showParticles) {
        return new MobEffectInstance(WitchAtelierModMobEffects.SILVERWOOD_PARASITE, EFFECT_DURATION, EFFECT_AMPLIFIER, true, showParticles);
    }

    private static boolean isStandingOnGrass(LivingEntity entity) {
        return entity.level().getBlockState(entity.blockPosition().below()).is(Blocks.GRASS_BLOCK);
    }
}
