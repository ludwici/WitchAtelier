package com.catelot.witch_atelier.item;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import com.catelot.witch_atelier.registries.WitchAtelierModMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Comparator;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public class SilverwoodSeedItem extends Item {
    private static final int SCAN_INTERVAL = 10;
    private static final double SEARCH_RADIUS = 3.0D;
    private static final double EAT_DISTANCE_SQR = 4.0D;
    private static final int PARASITE_DURATION = 168000;
    private static final int PARASITE_AMPLIFIER = 1;

    public SilverwoodSeedItem() {
        super(new Item.Properties().rarity(Rarity.EPIC).food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.3F).alwaysEdible().build()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(itemStack, world, entity);
        if (!world.isClientSide()) {
            entity.addEffect(new MobEffectInstance(WitchAtelierModMobEffects.SILVERWOOD_PARASITE, PARASITE_DURATION, PARASITE_AMPLIFIER, true, false));
        }
        return result;
    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || !(event.getTarget() instanceof Animal animal)) {
            return;
        }

        Player player = event.getEntity();
        if (!player.getMainHandItem().is(WitchAtelierModItems.SILVERWOOD_SEED.get()) || player.level().isClientSide()) {
            return;
        }

        player.getMainHandItem().shrink(1);
        animal.setInLove(player);
        animal.addEffect(new MobEffectInstance(WitchAtelierModMobEffects.SILVERWOOD_PARASITE, PARASITE_DURATION, PARASITE_AMPLIFIER, true, true));

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART, animal.getX(), animal.getY() + 1.7D, animal.getZ(), 10, 0.5D, 0.2D, 0.5D, 0.0D);
        }
    }

    @SubscribeEvent
    public static void onAnimalTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Animal animal) || animal.level().isClientSide()) {
            return;
        }
        if (animal.tickCount % SCAN_INTERVAL != 0 || animal.getRandom().nextFloat() >= 0.5F) {
            return;
        }

        // TODO: check performance. Check similar logic in Fox finding berries

        ItemEntity seed = animal.level()
                .getEntitiesOfClass(ItemEntity.class, animal.getBoundingBox().inflate(SEARCH_RADIUS), item -> item.isAlive() && item.getItem().is(WitchAtelierModItems.SILVERWOOD_SEED.get()))
                .stream()
                .min(Comparator.comparingDouble(animal::distanceToSqr))
                .orElse(null);

        if (seed == null) {
            return;
        }

        animal.getNavigation().moveTo(seed, 1.0D);
        if (animal.distanceToSqr(seed) > EAT_DISTANCE_SQR) {
            return;
        }

        seed.getItem().shrink(1);
        if (seed.getItem().isEmpty()) {
            seed.discard();
        }
        animal.addEffect(new MobEffectInstance(WitchAtelierModMobEffects.SILVERWOOD_PARASITE, PARASITE_DURATION, PARASITE_AMPLIFIER, true, true));
    }
}
