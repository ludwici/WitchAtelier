package com.meowkings.witch_atelier;

import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
        for (int a = -1; a <= 2; a++) {
            for (int b = -1; b <= 2; b++) {
                BlockPos p = (axis == Direction.Axis.X) ? corner.offset(a, b, 0) : corner.offset(0, b, a);
                String name = world.getBlockState(p).getBlock().toString().toLowerCase();
                boolean isInternal = (a >= 0 && a <= 1 && b >= 0 && b <= 1);

                if (!isInternal) {
                    if (!name.contains(type) && !name.contains("symbol")) {
                        return false;
                    }
                } else {
                    if (!world.isEmptyBlock(p) && !name.contains("portal_core")) {
                        return false;
                    }
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
            String n = world.getBlockState(p).getBlock().toString().toLowerCase();

            if (n.contains("circle")) {
                symbols.add("C");
            }
            else if (n.contains("square")) {
                symbols.add("S");
            }
            else if (n.contains("triangle")) {
                symbols.add("T");
            }
            else if (n.contains("rhombus")) {
                symbols.add("R");
            }
        }
    }
    Collections.sort(symbols);
    StringBuilder sb = new StringBuilder();
    for(String s : symbols) {
        sb.append(s);
    }
    return sb.toString();
}

}
