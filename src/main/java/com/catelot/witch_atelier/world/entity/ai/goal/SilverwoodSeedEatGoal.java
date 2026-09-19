package com.catelot.witch_atelier.world.entity.ai.goal;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.potion.SilverwoodParasiteMobEffect;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Comparator;
import java.util.EnumSet;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public class SilverwoodSeedEatGoal extends Goal {
    private static final int GOAL_PRIORITY = 5;
    private static final int MIN_SEARCH_INTERVAL = 20;
    private static final int SEARCH_INTERVAL_VARIANCE = 21;
    private static final int PATH_RECALCULATION_INTERVAL = 10;
    private static final double SEARCH_RADIUS = 3.0D;
    private static final double MAX_FOLLOW_DISTANCE_SQR = 64.0D;
    private static final double EAT_DISTANCE_SQR = 4.0D;
    private static final double MOVEMENT_SPEED = 1.0D;

    private final Animal animal;
    private ItemEntity targetSeed;
    private int nextSearchTick;
    private int pathRecalculationCooldown;

    public SilverwoodSeedEatGoal(Animal animal) {
        this.animal = animal;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Animal animal)) {
            return;
        }

        boolean alreadyAttached = animal.goalSelector.getAvailableGoals().stream().anyMatch(wrappedGoal -> wrappedGoal.getGoal() instanceof SilverwoodSeedEatGoal);
        if (alreadyAttached) {
            return;
        }

        animal.goalSelector.addGoal(GOAL_PRIORITY, new SilverwoodSeedEatGoal(animal));
    }

    @Override
    public boolean canUse() {
        if (animal.tickCount < nextSearchTick) {
            return false;
        }

        nextSearchTick = animal.tickCount + MIN_SEARCH_INTERVAL + animal.getRandom().nextInt(SEARCH_INTERVAL_VARIANCE);
        targetSeed = findNearestSeed();
        return targetSeed != null;
    }

    @Override
    public boolean canContinueToUse() {
        return isValidTarget() && animal.distanceToSqr(targetSeed) <= MAX_FOLLOW_DISTANCE_SQR;
    }

    @Override
    public void start() {
        pathRecalculationCooldown = 0;
        moveToTarget();
    }

    @Override
    public void stop() {
        animal.getNavigation().stop();
        targetSeed = null;
    }

    @Override
    public void tick() {
        if (!isValidTarget()) {
            return;
        }

        animal.getLookControl().setLookAt(targetSeed, 30.0F, 30.0F);

        if (animal.distanceToSqr(targetSeed) <= EAT_DISTANCE_SQR) {
            eatTargetSeed();
            return;
        }

        if (pathRecalculationCooldown > 0) {
            pathRecalculationCooldown--;
            return;
        }

        pathRecalculationCooldown = PATH_RECALCULATION_INTERVAL;
        moveToTarget();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private ItemEntity findNearestSeed() {
        return animal.level()
                .getEntitiesOfClass(ItemEntity.class, animal.getBoundingBox().inflate(SEARCH_RADIUS), this::isSeed)
                .stream()
                .min(Comparator.comparingDouble(animal::distanceToSqr))
                .orElse(null);
    }

    private boolean isValidTarget() {
        return targetSeed != null && isSeed(targetSeed);
    }

    private boolean isSeed(ItemEntity itemEntity) {
        return itemEntity.isAlive() && !itemEntity.getItem().isEmpty() && itemEntity.getItem().is(WitchAtelierModItems.SILVERWOOD_SEED.get());
    }

    private void moveToTarget() {
        if (targetSeed != null) {
            animal.getNavigation().moveTo(targetSeed, MOVEMENT_SPEED);
        }
    }

    private void eatTargetSeed() {
        targetSeed.getItem().shrink(1);
        if (targetSeed.getItem().isEmpty()) {
            targetSeed.discard();
        }

        animal.addEffect(SilverwoodParasiteMobEffect.createInstance(true));
        animal.getNavigation().stop();
        targetSeed = null;
    }
}
