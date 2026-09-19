package com.catelot.witch_atelier;

import com.catelot.witch_atelier.WitchAtelier;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public class PortalManager {
    private static final Map<UUID, String> playerInputs = new HashMap<>();
    private static final Map<UUID, List<BlockPos>> playerClickPositions = new HashMap<>();

    public static void onWoodSymbolClicked(Level world, BlockPos pos, Player player, String symbol) {
        if (world.isClientSide()) {
            return;
        }
        UUID uuid = player.getUUID();

        String current = playerInputs.getOrDefault(uuid, "");
        List<BlockPos> positions = playerClickPositions.getOrDefault(uuid, new ArrayList<>());

        if (current.isEmpty()) {
            Object[] woodData = PortalScanner.findFrameData(world, pos, "wood");
            if (woodData != null) {
                closeBothSides(world, (BlockPos) woodData[0]);
            }
        }

        current += symbol;
        positions.add(pos);

        playerInputs.put(uuid, current);
        playerClickPositions.put(uuid, positions);

        if (world instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.WHITE_ASH, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
        }

        if (current.length() == 4) {
            activate(world, pos, current, new ArrayList<>(positions));
            playerInputs.put(uuid, "");
            playerClickPositions.put(uuid, new ArrayList<>());
        }
    }

    public static void onSymbolTimeout(Level world, BlockPos pos, String symbol) {
        if (world.isClientSide()) {
            return;
        }
        playerInputs.clear();
        playerClickPositions.clear();
    }

    private static void activate(Level world, BlockPos pos, String code, List<BlockPos> positions) {
        Object[] woodData = PortalScanner.findFrameData(world, pos, "wood");
        char[] chars = code.toCharArray();
        Arrays.sort(chars);
        String sortedInput = new String(chars);
        String entry = PortalDatabase.getStoneEntry(world, sortedInput);

        if (woodData != null && entry != null) {
            try {
                String[] parts = entry.split(":");
                String[] xyz = parts[1].split(",");
                BlockPos stoneCorner = new BlockPos(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));

                BlockPos woodCorner = (BlockPos) woodData[0];
                Direction.Axis woodAxis = (Direction.Axis) woodData[1];
                Direction.Axis stoneAxis = parts[2].equals("x") ? Direction.Axis.X : Direction.Axis.Z;

                PortalDatabase.addActiveLink(world, woodCorner, stoneCorner);

                fillPortal(world, woodCorner, woodAxis);
                fillPortal(world, stoneCorner, stoneAxis);

                spawnChainParticles(world, positions, woodAxis);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        int steps = (int) (dist * 20);

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
        BlockPos pos = event.getPos();
        String name = world.getBlockState(pos).getBlock().toString().toLowerCase();
        if (name.contains("frame") || name.contains("symbol")) {
            Object[] data = PortalScanner.findFrameData(world, pos, "frame");
            if (data != null) {
                closeBothSides(world, (BlockPos) data[0]);
            }
        }
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
