package com.catelot.witch_atelier;

import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.*;

public class PortalDatabase {
    public static void saveStonePortal(Level world, String code, BlockPos center, String axis) {
        PortalSavedData vars = PortalSavedData.get(world);
        String entry = code + ":" + center.getX() + "," + center.getY() + "," + center.getZ() + ":" + axis + "|";
        String[] entries = vars.portalDatabase.split("\\|");
        StringBuilder sb = new StringBuilder();
        for (String e : entries) {
            if (!e.startsWith(code + ":") && !e.isEmpty()) {
                sb.append(e).append("|");
            }
        }
        sb.append(entry);
        vars.portalDatabase = sb.toString();
        vars.setDirty();
    }

    public static String getStoneEntry(Level world, String code) {
        PortalSavedData vars = PortalSavedData.get(world);
        for (String entry : vars.portalDatabase.split("\\|")) {
            if (entry.startsWith(code + ":")) {
                return entry;
            }
        }
        return null;
    }

    public static void addActiveLink(Level world, BlockPos posW, BlockPos posS) {
        PortalSavedData vars = PortalSavedData.get(world);
        removeLinkByPos(world, posW);
        vars.portalLinks += posW.getX() + "," + posW.getY() + "," + posW.getZ() + ">" +
                             posS.getX() + "," + posS.getY() + "," + posS.getZ() + "|";
        vars.setDirty();
    }

    public static BlockPos removeLinkByPos(Level world, BlockPos currentPos) {
        PortalSavedData vars = PortalSavedData.get(world);
        String[] links = vars.portalLinks.split("\\|");
        StringBuilder newLinks = new StringBuilder();
        BlockPos otherSide = null;
        for (String link : links) {
            if (link.isEmpty() || !link.contains(">")) {
                continue;
            }
            String[] sides = link.split(">");
            BlockPos p1 = parsePos(sides[0]);
            BlockPos p2 = parsePos(sides[1]);
            if (p1.distSqr(currentPos) < 2.5 || p2.distSqr(currentPos) < 2.5) {
                otherSide = p1.distSqr(currentPos) < 2.5 ? p2 : p1;
            } else newLinks.append(link).append("|");
        }
        vars.portalLinks = newLinks.toString();
        vars.setDirty();
        return otherSide;
    }

    private static BlockPos parsePos(String s) {
        try {
            String[] xyz = s.split(",");
            return new BlockPos(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
        } catch (Exception e) { return new BlockPos(0, 0, 0); }
    }

    public static BlockPos getTeleportTarget(Level world, BlockPos pPos) {
        PortalSavedData vars = PortalSavedData.get(world);
        for (String link : vars.portalLinks.split("\\|")) {
            if (link.isEmpty() || !link.contains(">")) {
                continue;
            }
            String[] sides = link.split(">");
            for (int i = 0; i < 2; i++) {
                BlockPos corner = parsePos(sides[i]);
                if (Math.abs(pPos.getY() - corner.getY()) > 1) {
                    continue;
                }

                BlockPos corePos = corner;
                BlockState state = world.getBlockState(corePos);
                if (!state.getBlock().toString().contains("portal_core_new")) {
                    BlockPos[] neighbors = {corner.north(), corner.south(), corner.east(), corner.west()};
                    for (BlockPos n : neighbors) {
                        if (world.getBlockState(n).getBlock().toString().contains("portal_core_new")) {
                            state = world.getBlockState(n);
                            corePos = n;
                            break;
                        }
                    }
                }

                if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    Direction f = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    double dx = Math.abs(pPos.getX() - corner.getX());
                    double dz = Math.abs(pPos.getZ() - corner.getZ());

                    if (f == Direction.EAST || f == Direction.WEST) {
                        if (dz < 0.1 && dx >= 0 && dx <= 1.1) {
                            return parsePos(sides[1 - i]);
                        }
                    }
                    else {
                        if (dx < 0.1 && dz >= 0 && dz <= 1.1) {
                            return parsePos(sides[1 - i]);
                        }
                    }
                }
            }
        }
        return null;
    }
}
