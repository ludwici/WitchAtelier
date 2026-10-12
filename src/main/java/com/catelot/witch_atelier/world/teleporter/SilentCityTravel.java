package com.catelot.witch_atelier.world.teleporter;

import com.catelot.witch_atelier.WitchAtelier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SilentCityTravel {
    public static final ResourceKey<Level> SILENT_CITY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "silent_city_dimension")
    );

    private SilentCityTravel() {
    }

    public static boolean enter(ServerPlayer player) {
        return player.level().dimension() != SILENT_CITY && !player.isOnPortalCooldown() && travel(player, SILENT_CITY);
    }

    public static boolean leave(ServerPlayer player) {
        return player.level().dimension() != Level.OVERWORLD && travel(player, Level.OVERWORLD);
    }

    private static boolean travel(ServerPlayer player, ResourceKey<Level> dimension) {
        ServerLevel destination = player.server.getLevel(dimension);
        if (destination == null) {
            return false;
        }

        Vec3 position = dimension == SILENT_CITY ? findSkyPosition(player, destination) : findSafePosition(player, destination);
        if (position == null) {
            player.setPortalCooldown(100);
            player.displayClientMessage(Component.translatable("message.witch_atelier.no_safe_destination"), true);
            return false;
        }

        DimensionTransition.PostDimensionTransition afterTravel = DimensionTransition.PLAY_PORTAL_SOUND;
        if (dimension == SILENT_CITY) {
            afterTravel = afterTravel.then(entity -> {
                if (entity instanceof ServerPlayer arrivedPlayer) {
                    arrivedPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0));
                }
            });
        }

        return player.changeDimension(new DimensionTransition(
                destination, position, Vec3.ZERO, player.getYRot(), player.getXRot(), afterTravel
        )) != null;
    }

    private static Vec3 findSkyPosition(ServerPlayer player, ServerLevel destination) {
        return findClearPosition(player, destination, player.getY());
    }

    private static Vec3 findSafePosition(ServerPlayer player, ServerLevel destination) {
        int surfaceY = destination.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(player.getX()), Mth.floor(player.getZ()));
        return findClearPosition(player, destination, surfaceY);
    }

    private static Vec3 findClearPosition(ServerPlayer player, ServerLevel destination, double startY) {
        double x = player.getX();
        double z = player.getZ();
        double maxY = destination.getMaxBuildHeight() - player.getBbHeight();

        for (double y = Math.max(startY, destination.getMinBuildHeight() + 1); y < maxY; y++) {
            if (isClear(destination, player, x, y, z)) {
                return new Vec3(x, y, z);
            }
        }

        return null;
    }

    private static boolean isClear(ServerLevel level, ServerPlayer player, double x, double y, double z) {
        BlockPos feet = BlockPos.containing(x, y, z);
        if (!level.getBlockState(feet).isAir() || !level.getBlockState(feet.above()).isAir()) {
            return false;
        }

        double halfWidth = player.getBbWidth() / 2.0;
        AABB bounds = new AABB(x - halfWidth, y, z - halfWidth, x + halfWidth, y + player.getBbHeight(), z + halfWidth);
        return level.getWorldBorder().isWithinBounds(bounds) && level.noCollision(player, bounds);
    }
}
