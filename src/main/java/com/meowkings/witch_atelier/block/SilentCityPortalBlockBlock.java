package com.meowkings.witch_atelier.block;

import com.meowkings.witch_atelier.WitchAtelier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SilentCityPortalBlockBlock extends Block {
    private static final VoxelShape SHAPE = box(-16, 0, -16, 32, 1, 32);
    private static final ResourceKey<Level> SILENT_CITY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "silent_city_dimension")
    );

    public SilentCityPortalBlockBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.AMETHYST).strength(1f, 10f).noOcclusion().isRedstoneConductor((state, getter, position) -> false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);

        if (entity instanceof ServerPlayer player && player.level().dimension() != SILENT_CITY) {
            ServerLevel destination = player.server.getLevel(SILENT_CITY);
            if (destination != null) {
                teleportPlayer(player, destination);
            }
        }

        if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide()) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 1));
        }
    }

    private static void teleportPlayer(ServerPlayer player, ServerLevel destination) {
        player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
        player.teleportTo(destination, player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));

        for (MobEffectInstance effect : player.getActiveEffects()) {
            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect, false));
        }

        player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
    }
}
