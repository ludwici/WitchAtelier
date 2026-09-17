package com.meowkings.witch_atelier;

import com.meowkings.witch_atelier.WitchAtelier;
import com.meowkings.witch_atelier.registries.WitchAtelierModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public final class PortalTeleporter {
    private static final Map<UUID, Integer> PORTAL_TIMER = new HashMap<>();
    private static final Map<UUID, Long> COOLDOWN = new HashMap<>();

    private PortalTeleporter() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Level level = player.level();
        UUID uuid = player.getUUID();
        long gameTime = level.getGameTime();
        if (COOLDOWN.getOrDefault(uuid, 0L) > gameTime) {
            return;
        }

        BlockPos feet = player.blockPosition();
        if (!findMagicNearby(level, feet)) {
            resetCharge(player);
            return;
        }

        BlockPos target = PortalDatabase.getTeleportTarget(level, feet);
        if (target == null) {
            resetCharge(player);
            return;
        }

        int ticks = PORTAL_TIMER.getOrDefault(uuid, 0) + 1;
        PORTAL_TIMER.put(uuid, ticks);
        if (ticks > 1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, ticks / 2, false, false, false));
        }

        if (ticks < 15) {
            return;
        }

        PORTAL_TIMER.remove(uuid);
        player.removeEffect(MobEffects.MOVEMENT_SPEED);
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 10, 1, false, false));
        player.teleportTo((ServerLevel) level,
                target.getX() + 0.5D,
                target.getY() + 0.5D,
                target.getZ() + 0.5D,
                player.getYRot(), player.getXRot());
        COOLDOWN.put(uuid, gameTime + 60L);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        PORTAL_TIMER.remove(uuid);
        COOLDOWN.remove(uuid);
    }

    private static void resetCharge(ServerPlayer player) {
        if (PORTAL_TIMER.remove(player.getUUID()) != null) {
            player.removeEffect(MobEffects.MOVEMENT_SPEED);
        }
    }

    private static boolean findMagicNearby(Level level, BlockPos pos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos check = pos.offset(dx, 0, dz);
                if (isPortal(level, check) || isPortal(level, check.above())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isPortal(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(WitchAtelierModBlocks.PORTAL_CORE_NEW.get());
    }
}
