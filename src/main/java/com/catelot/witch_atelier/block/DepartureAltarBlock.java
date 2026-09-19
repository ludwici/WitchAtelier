package com.catelot.witch_atelier.block;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import com.google.common.collect.ImmutableMap;

public class DepartureAltarBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<AttachFace> FACE = FaceAttachedHorizontalDirectionalBlock.FACE;
    private final ImmutableMap<BlockState, VoxelShape> shapes = this.makeShapes();

    public DepartureAltarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
    }

    private ImmutableMap<BlockState, VoxelShape> makeShapes() {
        return this.getShapeForEachState(state -> switch (state.getValue(FACING)) {
            case NORTH -> switch (state.getValue(FACE)) {
                case FLOOR -> box(-16, 0, -16, 32, 1, 32);
                case WALL -> box(-16, -16, 15, 32, 32, 16);
                case CEILING -> box(-16, 15, -16, 32, 16, 32);
            };
            case EAST -> switch (state.getValue(FACE)) {
                case FLOOR -> box(-16, 0, -16, 32, 1, 32);
                case WALL -> box(0, -16, -16, 1, 32, 32);
                case CEILING -> box(-16, 15, -16, 32, 16, 32);
            };
            case WEST -> switch (state.getValue(FACE)) {
                case FLOOR -> box(-16, 0, -16, 32, 1, 32);
                case WALL -> box(15, -16, -16, 16, 32, 32);
                case CEILING -> box(-16, 15, -16, 32, 16, 32);
            };
            default -> switch (state.getValue(FACE)) {
                case FLOOR -> box(-16, 0, -16, 32, 1, 32);
                case WALL -> box(-16, -16, 0, 32, 32, 1);
                case CEILING -> box(-16, 15, -16, 32, 16, 32);
            };
        });
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return shapes.get(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, FACE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACE, faceForDirection(context.getNearestLookingDirection())).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    private AttachFace faceForDirection(Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR;
        }
        else {
            return AttachFace.WALL;
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockstate, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
        super.useWithoutItem(blockstate, world, pos, entity, hit);

        if (entity instanceof ServerPlayer serverPlayer && !serverPlayer.level().isClientSide()) {
            ResourceKey<Level> destinationType = Level.OVERWORLD;
            if (serverPlayer.level().dimension() == destinationType) {
                return InteractionResult.PASS;
            }
            ServerLevel nextLevel = serverPlayer.server.getLevel(destinationType);
            if (nextLevel != null) {
                serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
                serverPlayer.teleportTo(nextLevel, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), serverPlayer.getYRot(), serverPlayer.getXRot());
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
                for (MobEffectInstance effectInstance : serverPlayer.getActiveEffects()) {
                    serverPlayer.connection.send(new ClientboundUpdateMobEffectPacket(serverPlayer.getId(), effectInstance, false));
                }
                serverPlayer.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
            }
        }
        if (!entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 1));
        }

        return InteractionResult.SUCCESS;
    }
}
