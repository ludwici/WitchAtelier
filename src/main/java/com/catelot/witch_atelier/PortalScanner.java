package com.catelot.witch_atelier;

import com.catelot.witch_atelier.registries.WitchAtelierModBlocks;
import com.catelot.witch_atelier.registries.WitchAtelierModBlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class PortalScanner {
    public static Object[] findFrameData(Level world, BlockPos pos, String type) {
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
            for (int dx = -2; dx <= 1; dx++) {
                for (int dy = -2; dy <= 1; dy++) {
                    BlockPos corner = (axis == Direction.Axis.X) ? pos.offset(dx, dy, 0) : pos.offset(0, dy, dx);
                    if (checkAt(world, corner, axis, type)) {
                        return new Object[]{corner, axis};
                    }
                }
            }
        }
        return null;
    }

    private static boolean checkAt(Level world, BlockPos corner, Direction.Axis axis, String type) {
        TagKey<Block> parts = switch (type) {
            case "wood" -> WitchAtelierModBlockTags.WOOD_PORTAL_PARTS;
            case "stone" -> WitchAtelierModBlockTags.STONE_PORTAL_PARTS;
            default -> null;
        };
        if (parts == null) {
            return false;
        }

        for (int a = -1; a <= 2; a++) {
            for (int b = -1; b <= 2; b++) {
                BlockPos p = (axis == Direction.Axis.X) ? corner.offset(a, b, 0) : corner.offset(0, b, a);
                BlockState state = world.getBlockState(p);
                boolean isInternal = (a >= 0 && a <= 1 && b >= 0 && b <= 1);

                if (!isInternal) {
                    if (!state.is(parts)) {
                        return false;
                    }
                } else if (!world.isEmptyBlock(p) && !state.is(WitchAtelierModBlocks.PORTAL_CORE_NEW.get())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getPortalCode(Level world, BlockPos corner, Direction.Axis axis) {
        List<String> symbols = new ArrayList<>();
        for (int a = -1; a <= 2; a++) {
            for (int b = -1; b <= 2; b++) {
                if (a >= 0 && a <= 1 && b >= 0 && b <= 1) {
                    continue;
                }
                BlockPos p = (axis == Direction.Axis.X) ? corner.offset(a, b, 0) : corner.offset(0, b, a);
                BlockState state = world.getBlockState(p);

                if (state.is(WitchAtelierModBlocks.SYMBOL_CIRCLE.get()) || state.is(WitchAtelierModBlocks.STONE_SYMBOL_CIRCLE.get())) {
                    symbols.add("C");
                } else if (state.is(WitchAtelierModBlocks.SYMBOL_SQUARE.get()) || state.is(WitchAtelierModBlocks.STONE_SYMBOL_SQUARE.get())) {
                    symbols.add("S");
                } else if (state.is(WitchAtelierModBlocks.SYMBOL_TRIANGLE.get()) || state.is(WitchAtelierModBlocks.STONE_SYMBOL_TRIANGLE.get())) {
                    symbols.add("T");
                } else if (state.is(WitchAtelierModBlocks.SYMBOL_RHOMBUS.get()) || state.is(WitchAtelierModBlocks.STONE_SYMBOL_RHOMBUS.get())) {
                    symbols.add("R");
                }
            }
        }
        Collections.sort(symbols);
        StringBuilder sb = new StringBuilder();
        for (String s : symbols) {
            sb.append(s);
        }
        return sb.toString();
    }

}
