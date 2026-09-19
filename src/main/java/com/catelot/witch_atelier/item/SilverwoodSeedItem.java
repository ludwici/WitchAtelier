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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public class SilverwoodSeedItem extends Item {
    private static final int PARASITE_DURATION = 168000;
    private static final int PARASITE_AMPLIFIER = 1;

    public SilverwoodSeedItem(Item.Properties properties) {
        super(properties);
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
            applyParasite(entity, false);
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
        applyParasite(animal, true);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART, animal.getX(), animal.getY() + 1.7D, animal.getZ(), 10, 0.5D, 0.2D, 0.5D, 0.0D);
        }
    }

    public static void applyParasite(LivingEntity entity, boolean showParticles) {
        entity.addEffect(new MobEffectInstance(WitchAtelierModMobEffects.SILVERWOOD_PARASITE, PARASITE_DURATION, PARASITE_AMPLIFIER, true, showParticles));
    }
}
