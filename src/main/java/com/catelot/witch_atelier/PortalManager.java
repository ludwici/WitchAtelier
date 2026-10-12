package com.catelot.witch_atelier;

import com.catelot.witch_atelier.registries.WitchAtelierModBlockTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public class PortalManager {
    private static final long INPUT_TIMEOUT_TICKS = 600;
    private static final Map<UUID, PortalInput> playerInputs = new HashMap<>();

    private record PortalInput(String code, List<BlockPos> positions, BlockPos corner, Direction.Axis axis, ResourceKey<Level> dimension, long lastClickTick) {
    }

    public static void onWoodSymbolClicked(Level world, BlockPos pos, Player player, String symbol) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        Object[] frame = PortalScanner.findFrameData(world, pos, "wood");
        if (frame == null) {
            return;
        }

        BlockPos corner = (BlockPos) frame[0];
        Direction.Axis axis = (Direction.Axis) frame[1];
        UUID uuid = player.getUUID();
        long now = world.getGameTime();
        PortalInput previous = playerInputs.get(uuid);

        if (previous == null || now < previous.lastClickTick()
                || now - previous.lastClickTick() >= INPUT_TIMEOUT_TICKS
                || !previous.corner().equals(corner) || previous.axis() != axis
                || !previous.dimension().equals(world.dimension())) {
            closeBothSides(world, corner);
            previous = new PortalInput("", List.of(), corner, axis, world.dimension(), now);
        }

        String code = previous.code() + symbol;
        List<BlockPos> positions = new ArrayList<>(previous.positions());
        positions.add(pos);

        serverLevel.sendParticles(ParticleTypes.WHITE_ASH, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);

        if (code.length() == 4) {
            playerInputs.remove(uuid);
            activate(world, corner, axis, code, positions);
        } else {
            playerInputs.put(uuid, new PortalInput(code, positions, corner, axis, world.dimension(), now));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        playerInputs.remove(event.getEntity().getUUID());
    }

    private static void activate(Level world, BlockPos woodCorner, Direction.Axis woodAxis, String code, List<BlockPos> positions) {
        char[] chars = code.toCharArray();
        Arrays.sort(chars);
        String sortedInput = new String(chars);
        PortalDatabase.StonePortal stone = PortalDatabase.getStonePortal(world, sortedInput);
        if (stone == null) {
            return;
        }

        Object[] stoneFrame = PortalScanner.findFrameData(world, stone.corner(), "stone");
        if (stoneFrame == null || !stone.corner().equals(stoneFrame[0]) || stone.axis() != stoneFrame[1]
                || !sortedInput.equals(PortalScanner.getPortalCode(world, stone.corner(), stone.axis()))) {
            return;
        }

        closeBothSides(world, woodCorner);
        closeBothSides(world, stone.corner());
        PortalDatabase.addActiveLink(world, woodCorner, stone.corner());
        fillPortal(world, woodCorner, woodAxis);
        fillPortal(world, stone.corner(), stone.axis());
        spawnChainParticles(world, positions, woodAxis);
    }

    private static void spawnChainParticles(Level world, List<BlockPos> positions, Direction.Axis axis) {
        if (world instanceof ServerLevel serverLevel) {
            for (int i = 0; i < positions.size(); i++) {
                BlockPos p1 = positions.get(i);
                BlockPos p2 = positions.get((i + 1) % positions.size());

                double offX = (axis == Direction.Axis.X) ? 0 : 0.7;
                double offZ = (axis == Direction.Axis.X) ? 0.7 : 0;

                drawHighDensityLine(serverLevel, p1, p2, offX, offZ);
                drawHighDensityLine(serverLevel, p1, p2, -offX, -offZ);
            }
            world.playSound(null, positions.get(0), net.minecraft.sounds.SoundEvents.BEACON_ACTIVATE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.5F);
        }
    }

    private static void drawHighDensityLine(ServerLevel level, BlockPos start, BlockPos end, double offsetX, double offsetZ) {
        double x1 = start.getX() + 0.5 + offsetX, y1 = start.getY() + 0.5, z1 = start.getZ() + 0.5 + offsetZ;
        double x2 = end.getX() + 0.5 + offsetX, y2 = end.getY() + 0.5, z2 = end.getZ() + 0.5 + offsetZ;

        double dist = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2) + Math.pow(z2-z1, 2));
        int steps = Math.max(1, (int) (dist * 20));

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double px = x1 + (x2 - x1) * t;
            double py = y1 + (y2 - y1) * t;
            double pz = z1 + (z2 - z1) * t;

            level.sendParticles(ParticleTypes.END_ROD, px, py, pz, 1, 0, 0, 0, 0.01);
            if (i % 4 == 0) {
                level.sendParticles(ParticleTypes.INSTANT_EFFECT, px, py, pz, 1, 0, 0, 0, 0);
            }
        }
    }

    private static void fillPortal(Level world, BlockPos corner, Direction.Axis axis) {
        BlockState state = com.catelot.witch_atelier.registries.WitchAtelierModBlocks.PORTAL_CORE_NEW.get().defaultBlockState();
        Direction facing = (axis == Direction.Axis.X) ? Direction.EAST : Direction.SOUTH;
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        world.setBlock(corner, state, 3);
    }

    public static void registerStonePortal(Level world, BlockPos pos) {
        Object[] data = PortalScanner.findFrameData(world, pos, "stone");
        if (data != null) {
            BlockPos corner = (BlockPos) data[0];
            Direction.Axis axis = (Direction.Axis) data[1];
            String code = PortalScanner.getPortalCode(world, corner, axis);
            if (code.length() == 4) {
                PortalDatabase.saveStonePortal(world, code, corner, (axis == Direction.Axis.X ? "x" : "z"));
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof Level world) || world.isClientSide()) {
            return;
        }

        BlockState state = event.getState();
        BlockPos pos = event.getPos();
        if (isWoodPart(state)) {
            Object[] frame = PortalScanner.findFrameData(world, pos, "wood");
            if (frame != null) {
                closeBothSides(world, (BlockPos) frame[0]);
            }
        } else if (isStonePart(state)) {
            Object[] frame = PortalScanner.findFrameData(world, pos, "stone");
            if (frame != null) {
                BlockPos corner = (BlockPos) frame[0];
                closeBothSides(world, corner);
                PortalDatabase.removeStonePortal(world, corner);
            }
        }
    }

    private static boolean isWoodPart(BlockState state) {
        return state.is(WitchAtelierModBlockTags.WOOD_PORTAL_PARTS);
    }

    private static boolean isStonePart(BlockState state) {
        return state.is(WitchAtelierModBlockTags.STONE_PORTAL_PARTS);
    }

    private static void closeBothSides(Level world, BlockPos currentCorner) {
        BlockPos otherSide = PortalDatabase.removeLinkByPos(world, currentCorner);
        clearArea(world, currentCorner);
        if (otherSide != null) {
            clearArea(world, otherSide);
        }
    }

    private static void clearArea(Level world, BlockPos corner) {
        for (int dy = 0; dy <= 1; dy++) {
            BlockPos p = corner.above(dy);
            if (world.getBlockState(p).getBlock() == com.catelot.witch_atelier.registries.WitchAtelierModBlocks.PORTAL_CORE_NEW.get()) {
                world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }
}
