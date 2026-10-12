package com.catelot.witch_atelier;

import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.catelot.witch_atelier.registries.WitchAtelierModBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.*;

public class PortalDatabase {
    public record StonePortal(BlockPos corner, Direction.Axis axis) {
    }

    public static StonePortal getStonePortal(Level world, String code) {
        String entry = getStoneEntry(world, code);
        if (entry == null) {
            return null;
        }

        String[] fields = entry.split(":", -1);
        if (fields.length != 3 || (!fields[2].equals("x") && !fields[2].equals("z"))) {
            return null;
        }

        BlockPos pos = parsePos(fields[1]);
        if (pos == null) {
            return null;
        }

        return new StonePortal(pos, fields[2].equals("x") ? Direction.Axis.X : Direction.Axis.Z);
    }

    public static void removeStonePortal(Level world, BlockPos corner) {
        PortalSavedData data = PortalSavedData.get(world);
        StringBuilder entries = new StringBuilder();
        boolean changed = false;

        for (String entry : data.portalDatabase.split("\\|")) {
            if (entry.isEmpty()) {
                continue;
            }
            String[] fields = entry.split(":", -1);
            if (fields.length == 3 && corner.equals(parsePos(fields[1]))) {
                changed = true;
            } else {
                entries.append(entry).append('|');
            }
        }

        if (changed) {
            data.portalDatabase = entries.toString();
            data.setDirty();
        }
    }

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
        removeLinkByPos(world, posS);
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
            String[] sides = link.split(">", -1);
            if (sides.length != 2) {
                newLinks.append(link).append('|');
                continue;
            }
            BlockPos p1 = parsePos(sides[0]);
            BlockPos p2 = parsePos(sides[1]);
            if (p1 == null || p2 == null) {
                newLinks.append(link).append('|');
                continue;
            }
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
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static BlockPos getTeleportTarget(Level world, BlockPos pPos) {
        PortalSavedData vars = PortalSavedData.get(world);
        for (String link : vars.portalLinks.split("\\|")) {
            if (link.isEmpty() || !link.contains(">")) {
                continue;
            }
            String[] sides = link.split(">", -1);
            if (sides.length != 2) {
                continue;
            }
            BlockPos first = parsePos(sides[0]);
            BlockPos second = parsePos(sides[1]);
            if (first == null || second == null) {
                continue;
            }
            BlockPos[] ends = {first, second};
            for (int i = 0; i < 2; i++) {
                BlockPos corner = ends[i];
                if (Math.abs(pPos.getY() - corner.getY()) > 1) {
                    continue;
                }

                BlockPos corePos = corner;
                BlockState state = world.getBlockState(corePos);
                if (!state.is(WitchAtelierModBlocks.PORTAL_CORE_NEW.get())) {
                    BlockPos[] neighbors = {corner.north(), corner.south(), corner.east(), corner.west()};
                    for (BlockPos n : neighbors) {
                        if (world.getBlockState(n).is(WitchAtelierModBlocks.PORTAL_CORE_NEW.get())) {
                            state = world.getBlockState(n);
                            corePos = n;
                            break;
                        }
                    }
                }

                if (state.is(WitchAtelierModBlocks.PORTAL_CORE_NEW.get()) && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    Direction f = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    double dx = Math.abs(pPos.getX() - corner.getX());
                    double dz = Math.abs(pPos.getZ() - corner.getZ());

                    if (f == Direction.EAST || f == Direction.WEST) {
                        if (dz < 0.1 && dx >= 0 && dx <= 1.1) {
                            return ends[1 - i];
                        }
                    }
                    else {
                        if (dx < 0.1 && dz >= 0 && dz <= 1.1) {
                            return ends[1 - i];
                        }
                    }
                }
            }
        }
        return null;
    }
}
