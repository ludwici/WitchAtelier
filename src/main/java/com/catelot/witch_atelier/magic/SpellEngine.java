package com.catelot.witch_atelier.magic;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public final class SpellEngine {

    private SpellEngine() {
    }

    public static class SpellStats {
        public List<SimpleParticleType> elements = new ArrayList<>();
        public int totalPower = 0;
        public int tInward = 0;
        public int tUp = 0;
        public int tDown = 0;
        public int tLeft = 0;
        public int tRight = 0;
        public int vortexBalance = 0;
        public boolean hasTrigger = false;

        public int getOutwardCount() {
            int c = 0;
            if (tUp > 0) {
                c++;
            }
            if (tDown > 0) {
                c++;
            }
            if (tLeft > 0) {
                c++;
            }
            if (tRight > 0) {
                c++;
            }
            return c;
        }

        public boolean isAura() {
            return tInward > 0 && getOutwardCount() == 0;
        }

        public boolean isSimpleBurst() {
            return (tInward + tUp + tDown + tLeft + tRight) == 0;
        }

        public boolean hasAnyT() {
            return (tInward + tUp + tDown + tLeft + tRight) > 0;
        }

        public double getRangeMod() {
            return Math.max(0.3, 1.0 + (vortexBalance * 0.3));
        }

        public double getRadiusMod() {
            return vortexBalance < 0 ? 3.8 : (vortexBalance > 0 ? 1.1 : 2.0);
        }

        public double getPowerMod() {
            return Math.max(0.5, 1.0 + (vortexBalance * 0.2));
        }
    }

    public static class CanvasZone {
        public static final int NONE = 0;
        public static final int ELEMENT_CORE = 1;
        public static final int MOD_UP = 2;
        public static final int MOD_DOWN = 3;
        public static final int MOD_LEFT = 4;
        public static final int MOD_RIGHT = 5;
    }

    public static int getZoneAt(int x, int y) {
        if (x >= 5 && x <= 11 && y >= 5 && y <= 11) {
            return CanvasZone.ELEMENT_CORE;
        }
        if (x >= 6 && x <= 10) {
            if (y >= 1 && y <= 4) {
                return CanvasZone.MOD_UP;
            }
            if (y >= 12 && y <= 15) {
                return CanvasZone.MOD_DOWN;
            }
        }
        if (y >= 6 && y <= 10) {
            if (x >= 1 && x <= 4) {
                return CanvasZone.MOD_LEFT;
            }
            if (x >= 12 && x <= 15) {
                return CanvasZone.MOD_RIGHT;
            }
        }
        return CanvasZone.NONE;
    }

    public static void processCanvasUpdate(Player player, int lastIndex) {
        Level world = player.level();
        ItemStack notebook = player.getMainHandItem();
        CompoundTag tag = notebook.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String key = tag.contains("NotebookPage") ? "DrawingDataPage" + tag.getInt("NotebookPage") : "DrawingData";
        byte[] drawingData = tag.getByteArray(key);

        if (drawingData.length == 289) {
            boolean isCircleFull = isFullCircle(drawingData);
            boolean wasCircleFullBefore = tag.getBoolean("WasCircleFull");

            if (isCircleFull && !wasCircleFullBefore) {
                SpellStats stats = scanCanvas(drawingData);
                if (!stats.elements.isEmpty() && world instanceof ServerLevel serverLevel) {
                    Vec3 startPosition = player.getEyePosition(1.0F).add(player.getLookAngle().scale(0.5));
                    executeMagicCore(serverLevel, player, startPosition, player.getLookAngle(), stats, false);
                }

                tag.putBoolean("WasCircleFull", true);
                notebook.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            } else if (!isCircleFull && wasCircleFullBefore) {
                tag.putBoolean("WasCircleFull", false);
                notebook.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }

        if (lastIndex == 2000) {
            ItemStack magicPage = new ItemStack(WitchAtelierModItems.MAGIC_PAGE_ITEM.get());
            if (!player.getInventory().add(magicPage)) {
                player.drop(magicPage, false);
            }
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.4F);
        }
    }

    private static void executeMagicCore(ServerLevel lvl, Entity source, Vec3 pos, Vec3 look, SpellStats stats, boolean isBlock) {
        if (stats.elements.isEmpty()) {
            return;
        }
        if (stats.hasTrigger) {
            List<String> elList = new ArrayList<>();
            for (SimpleParticleType el : stats.elements) {
                elList.add(BuiltInRegistries.PARTICLE_TYPE.getKey(el).toString());
            }
            String joinedElements = String.join(",", elList);
            int spellType = stats.isSimpleBurst() ? 0 : (stats.isAura() ? 2 : 1);
            String recipe = joinedElements + "|" + stats.totalPower + "|" + stats.vortexBalance + "|" + spellType;
            if (isBlock) {
                net.minecraft.world.entity.AreaEffectCloud mine = new net.minecraft.world.entity.AreaEffectCloud(lvl, pos.x, pos.y, pos.z);
                mine.setRadius(0.8F);
                mine.setDuration(6000);
                mine.setWaitTime(0);
                mine.addTag("MagicTrap");
                mine.addTag("SpellRecipe:" + recipe);
                lvl.addFreshEntity(mine);
            } else if (source instanceof Player p) {
                p.getPersistentData().putBoolean("MagicShieldActive", true);
                p.getPersistentData().putString("TriggerRecipe", recipe);
            }
            lvl.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 0.5F);
            return;
        }
        if (!stats.hasAnyT()) {
            if (stats.vortexBalance != 0) {
                for (double h = 0; h < 4.0; h += 0.4) {
                    double spiralRadius = (stats.vortexBalance < 0) ? h * 0.6 : 0.6;
                    Vec3 pointPos = pos.add(0, h, 0);
                    spawnSpiral(lvl, pointPos, new Vec3(0, 1, 0), stats.elements.get(0), (int) (h * 15), spiralRadius, true);
                    double effectRadius = (stats.vortexBalance < 0) ? 3.0 : 1.5;
                    applyEffectsAt(lvl, source, pointPos, new Vec3(0, 1, 0), stats.elements.get(0), 1.5 * stats.getPowerMod(), effectRadius);
                }
                lvl.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PHANTOM_FLAP, SoundSource.PLAYERS, 1.0F, 1.2F);
            } else {
                for (SimpleParticleType el : stats.elements) {
                    lvl.sendParticles(el, pos.x, pos.y, pos.z, 20, 0.3, 0.3, 0.3, 0.1);
                }
            }
            return;
        }
        if (stats.isAura()) {
            if (isBlock) {
                for (SimpleParticleType el : stats.elements) {
                    spawnBlockAura(lvl, pos, el, stats);
                }
            }
            else if (source instanceof LivingEntity le) {
                for (SimpleParticleType el : stats.elements) {
                    spawnPlayerAura(lvl, le, el, stats);
                }
            }
        } else {
            launchSpell(lvl, source, pos, look, stats);
        }
        lvl.playSound(null, pos.x, pos.y, pos.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.1F);
    }

    private static void launchSpell(ServerLevel lvl, Entity source, Vec3 pos, Vec3 look, SpellStats s) {
        List<Vec3> vectors = new ArrayList<>();
        int outCount = s.getOutwardCount();
        if (s.tInward > 0 && outCount > 0) {
            float spread = (outCount == 2) ? 35f : (outCount >= 3 ? 65f : 15f);
            int rays = (outCount == 3) ? 5 : 3;
            for (int i = 0; i < rays; i++) {
                float angle = -spread / 2f + (spread / (rays - 1)) * i;
                vectors.add(look.yRot((float) Math.toRadians(angle)));
            }
        } else {
            if (s.tUp > 0) {
                vectors.add(look);
            }
            if (s.tDown > 0) {
                vectors.add(look.scale(-1).add(0, -0.2, 0).normalize());
            }
            if (s.tLeft > 0) {
                vectors.add(look.yRot((float) Math.toRadians(90)));
            }
            if (s.tRight > 0) {
                vectors.add(look.yRot((float) Math.toRadians(-90)));
            }
            if (outCount == 4) {
                vectors.clear();
                vectors.add(new Vec3(0, 1, 0));
            }
        }
        double baseDist = 5.0 + (s.totalPower * 0.25);
        double finalDist = baseDist * s.getRangeMod();
        for (Vec3 v : vectors) {
            for (SimpleParticleType el : s.elements) {
                spawnRay(lvl, source, pos, v, finalDist, el, s);
            }
        }
    }

    private static void spawnRay(ServerLevel lvl, Entity source, Vec3 start, Vec3 dir, double dist, SimpleParticleType el, SpellStats s) {
        double hitRadius = s.getRadiusMod();
        for (double d = 0; d < dist; d += 0.3) {
            Vec3 pos = start.add(dir.scale(d));
            lvl.sendParticles(el, pos.x, pos.y, pos.z, 1, 0.02, 0.02, 0.02, 0);
            if (s.vortexBalance != 0) {
                double spiralRad = (s.vortexBalance < 0) ? 0.8 : 0.3;
                spawnSpiral(lvl, pos, dir, el, (int) (d * 10), spiralRad, s.vortexBalance < 0);
            }
            BlockPos bPos = BlockPos.containing(pos.x, pos.y, pos.z);
            if (!lvl.getBlockState(bPos).canBeReplaced() && s.vortexBalance <= 0) {
                spawnBlockAura(lvl, pos, el, s);
                break;
            }
            double falloff = Math.max(0.1, 1.0 - (d / dist));
            double pwr = (1.0 + s.totalPower * 0.08) * s.getPowerMod() * falloff;
            applyEffectsAt(lvl, source, pos, dir, el, pwr, hitRadius);
        }
    }

    private static void spawnSpiral(ServerLevel lvl, Vec3 pos, Vec3 dir, SimpleParticleType el, int ticks, double radius, boolean isWide) {
        double angle = ticks * 0.8;
        Vec3 axis = dir.normalize();
        Vec3 perp = Math.abs(axis.y) < 0.9 ? axis.cross(new Vec3(0, 1, 0)) : axis.cross(new Vec3(1, 0, 0));
        perp = perp.normalize().scale(radius);
        Vec3 rotated = perp.scale(Math.cos(angle)).add(axis.cross(perp).scale(Math.sin(angle)));
        lvl.sendParticles(el, pos.add(rotated).x, pos.add(rotated).y, pos.add(rotated).z, 1, 0, 0, 0, 0);
        if (isWide) {
            lvl.sendParticles(el, pos.subtract(rotated).x, pos.subtract(rotated).y, pos.subtract(rotated).z, 1, 0, 0, 0, 0);
        }
    }

    private static void applyEffectsAt(ServerLevel lvl, Entity src, Vec3 p, Vec3 d, SimpleParticleType el, double pwr, double radius) {
        AABB box = new AABB(p.x - radius, p.y - radius, p.z - radius, p.x + radius, p.y + radius, p.z + radius);
        List<LivingEntity> targets = lvl.getEntitiesOfClass(LivingEntity.class, box);
        BlockPos bPos = BlockPos.containing(p.x, p.y, p.z);
        for (LivingEntity le : targets) {
            if (src != null && le == src) {
                continue;
            }
            le.hurtMarked = true;
            if (el == ParticleTypes.FLAME) {
                le.hurt(lvl.damageSources().onFire(), (float) (4.0 * pwr));
                le.igniteForSeconds((int) (5 * pwr));
            }
            else if (el == ParticleTypes.CLOUD) {
                le.setDeltaMovement(d.scale(2.0 * pwr).add(0, 0.3, 0));
            }
            else if (el == ParticleTypes.SNOWFLAKE) {
                le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (100 * pwr), 2));
            }
            else if (el == ParticleTypes.SOUL) {
                le.hurt(lvl.damageSources().generic(), (float) (2.5 * pwr));
                le.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, (int) (200 * pwr), 1));
            }
            else if (el == ParticleTypes.END_ROD) {
                le.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (int) (100 * pwr), 0));
            }
            else if (el == ParticleTypes.FALLING_WATER) {
                le.clearFire();
                le.setDeltaMovement(le.getDeltaMovement().add(d.scale(0.1 * pwr)));
            }
        }
        if (el == ParticleTypes.FALLING_WATER && lvl.getBlockState(bPos).getBlock() == Blocks.FARMLAND) {
            lvl.setBlock(bPos, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7), 3);
        }
    }

    private static void spawnPlayerAura(ServerLevel lvl, LivingEntity le, SimpleParticleType el, SpellStats s) {
        double m = s.getPowerMod();
        if (el == ParticleTypes.FLAME) {
            le.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, (int) (1200 * m), 0));
        }
        else if (el == ParticleTypes.END_ROD) {
            le.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, (int) (2400 * m), 0, true, false));
        }
        else if (el == ParticleTypes.FALLING_WATER) {
            le.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, (int) (1200 * m), 1));
        }
        else if (el == ParticleTypes.CLOUD) {
            le.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, (int) (1200 * m), 0));
            le.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, (int) (1200 * m), 0));
        }
        else if (el == ParticleTypes.SOUL) {
            le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (600 * m), 10));
            le.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, (int) (600 * m), 2));
        }
        else if (el == ParticleTypes.INSTANT_EFFECT) {
            le.addEffect(new MobEffectInstance(MobEffects.LUCK, (int) (2400 * m), 1));
        }
        lvl.sendParticles(el, le.getX(), le.getY() + 1, le.getZ(), 25, 0.5, 0.5, 0.5, 0.05);
    }

    private static void spawnBlockAura(ServerLevel lvl, Vec3 pos, SimpleParticleType el, SpellStats s) {
        BlockPos bPos = BlockPos.containing(pos.x, pos.y, pos.z);
        if (lvl.isEmptyBlock(bPos)) {
            bPos = bPos.below();
        }
        BlockState st = lvl.getBlockState(bPos);
        if (el == ParticleTypes.FLAME) {
            if (st.is(Blocks.SAND)) {
                lvl.setBlock(bPos, Blocks.GLASS.defaultBlockState(), 3);
            } else if (st.is(Blocks.COBBLESTONE)) {
                lvl.setBlock(bPos, Blocks.STONE.defaultBlockState(), 3);
            }
        } else if (el == ParticleTypes.END_ROD) {
            BlockPos lightPos = lvl.isEmptyBlock(bPos) ? bPos : bPos.above();
            lvl.setBlock(lightPos, Blocks.LIGHT.defaultBlockState(), 3);
        } else if (el == ParticleTypes.FALLING_WATER) {
            if (st.isSolid()) {
                lvl.setBlock(bPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
            }
        } else if (el == ParticleTypes.SOUL) {
            if (!st.isAir()) {
                FallingBlockEntity.fall(lvl, bPos, st);
            }
        } else if (el == ParticleTypes.INSTANT_EFFECT) {
            lvl.setBlock(bPos.above(), Blocks.AMETHYST_CLUSTER.defaultBlockState(), 3);
        } else if (el == ParticleTypes.SNOWFLAKE) {
            lvl.setBlock(bPos, Blocks.POWDER_SNOW.defaultBlockState(), 3);
        }
        lvl.sendParticles(el, pos.x, pos.y, pos.z, 20, 0.3, 0.3, 0.3, 0.05);
    }

    private static SpellStats scanCanvas(byte[] d) {
        SpellStats s = new SpellStats();
        int c = 8;
        for (int y = 5; y <= 11; y++) {
            for (int x = 5; x <= 11; x++) {
                if (d[y * 17 + x] == 1) {
                    SimpleParticleType found = findStrictSigilAt(d, x, y);
                    if (found != null && !s.elements.contains(found)) {
                        s.elements.add(found);
                    }
                }
            }
        }
        if (s.elements.contains(ParticleTypes.FALLING_WATER) && s.elements.contains(ParticleTypes.CLOUD)) {
            s.elements.remove(ParticleTypes.FALLING_WATER);
            s.elements.remove(ParticleTypes.CLOUD);
            s.elements.add(ParticleTypes.SNOWFLAKE);
        }
        for (int y = 0; y < 17; y++) {
            for (int x = 0; x < 17; x++) {
                int zone = getZoneAt(x, y);
                if (zone == CanvasZone.NONE || zone == CanvasZone.ELEMENT_CORE) {
                    continue;
                }
                if (d[y * 17 + x] == 1 && d[(y + 2) * 17 + x] == 1 && d[(y + 1) * 17 + x - 1] == 1 && d[(y + 1) * 17 + x + 1] == 1 && d[(y + 1) * 17 + x] == 0) {
                    s.hasTrigger = true;
                }
                if (d[y * 17 + x] == 0) {
                    continue;
                }
                checkTModifier(d, x, y, s, c);
                if (x > 0 && x < 16 && y > 0 && y < 16 && d[y * 17 + x] == 1 && d[(y + 1) * 17 + x - 1] == 1 && d[(y - 1) * 17 + x + 1] == 1) {
                    if (zone == CanvasZone.MOD_UP || zone == CanvasZone.MOD_DOWN) {
                        s.vortexBalance++;
                    } else {
                        s.vortexBalance--;
                    }
                }
                if (x > 0 && x < 16 && y > 0 && y < 16 && d[y * 17 + x] == 1 && d[(y - 1) * 17 + x - 1] == 1 && d[(y + 1) * 17 + x + 1] == 1) {
                    if (zone == CanvasZone.MOD_LEFT || zone == CanvasZone.MOD_RIGHT) {
                        s.vortexBalance++;
                    } else {
                        s.vortexBalance--;
                    }
                }
            }
        }
        return s;
    }

    private static SimpleParticleType findStrictSigilAt(byte[] d, int x, int y) {
        if (x <= 13 && y <= 13 && d[y*17+x]==1 && d[y*17+x+3]==1 && d[(y+3)*17+x]==1 && d[(y+3)*17+x+3]==1 && d[(y+1)*17+x+1]==1) {
            return ParticleTypes.INSTANT_EFFECT;
        }
        int c00 = d[(y - 1) * 17 + x - 1];
        int c01 = d[(y - 1) * 17 + x];
        int c02 = d[(y - 1) * 17 + x + 1];
        int c10 = d[y * 17 + x - 1];
        int c11 = d[y * 17 + x];
        int c12 = d[y * 17 + x + 1];
        int c20 = d[(y + 1) * 17 + x - 1];
        int c21 = d[(y + 1) * 17 + x];
        int c22 = d[(y + 1) * 17 + x + 1];
        if (c00==1 && c01==1 && c02==1 && c10==1 && c11==1 && c12==1 && c20==1 && c21==1 && c22==1) {
            return ParticleTypes.INSTANT_EFFECT;
        }
        if (c00==1 && c01==1 && c02==1 && c11==1 && c20==1 && c21==1 && c22==1 && c10==0) {
            return ParticleTypes.SOUL;
        }
        if (c01==1 && c10==1 && c11==1 && c12==1 && c21==1 && c00==0 && c02==0) {
            return ParticleTypes.FLAME;
        }
        if (c01==1 && c02==1 && c11==1 && c21==1 && c20==1 && c00==0 && c10==0) {
            return ParticleTypes.FALLING_WATER;
        }
        if (c00==1 && c02==1 && c11==1 && c20==1 && c22==1 && c01==0) {
            return ParticleTypes.CLOUD;
        }
        if (c00==1 && c01==1 && c02==1 && c10==1 && c12==1 && c20==1 && c21==1 && c22==1 && c11==0) {
            return ParticleTypes.END_ROD;
        }
        return null;
    }

    public static void applySheetMagic(LevelAccessor level, double x, double y, double z, ItemStack itemStack, Direction face) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        byte[] drawingData = tag.getByteArray("DrawingData");
        if (drawingData.length == 0) {
            for (String key : tag.getAllKeys()) {
                if (key.startsWith("DrawingDataPage")) {
                    drawingData = tag.getByteArray(key);
                    break;
                }
            }
        }
        if (drawingData.length != 289) {
            return;
        }
        SpellStats stats = scanCanvas(drawingData);
        if (!stats.elements.isEmpty()) {
            Vec3 direction = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());
            executeMagicCore(serverLevel, null, new Vec3(x + 0.5 + direction.x * 0.5, y + 0.5 + direction.y * 0.5, z + 0.5 + direction.z * 0.5), direction, stats, true);
            itemStack.shrink(1);
        }
    }

    private static boolean isFullCircle(byte[] d) {
        for (int k = 0; k < 289; k++) {
            int dx = k % 17 - 8;
            int dy = k / 17 - 8;
            if (dx * dx + dy * dy >= 56 && dx * dx + dy * dy <= 68) {
                if (d[k] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void checkTModifier(byte[] d, int x, int y, SpellStats s, int c) {
        if (x > 0 && x < 16 && d[y * 17 + x - 1] == 1 && d[y * 17 + x + 1] == 1) {
            int len = checkLegLength(d, x, y, 0, (y < c ? 1 : -1));
            if (len > 0) {
                s.totalPower += len;
                if (y < c) {
                    s.tUp = len;
                } else {
                    s.tDown = len;
                }
            }
            else {
                len = checkLegLength(d, x, y, 0, (y < c ? -1 : 1));
                if (len > 0) {
                    s.totalPower += len;
                    s.tInward += len;
                }
            }
        }
        if (y > 0 && y < 16 && d[(y - 1) * 17 + x] == 1 && d[(y + 1) * 17 + x] == 1) {
            int len = checkLegLength(d, x, y, (x < c ? 1 : -1), 0);
            if (len > 0) {
                s.totalPower += len;
                if (x < c) {
                    s.tLeft = len;
                } else {
                    s.tRight = len;
                }
            }
            else {
                len = checkLegLength(d, x, y, (x < c ? -1 : 1), 0);
                if (len > 0) {
                    s.totalPower += len;
                    s.tInward += len;
                }
            }
        }
    }

    private static int checkLegLength(byte[] d, int x, int y, int sx, int sy) {
        int l = 0;
        for (int k = 1; k <= 6; k++) {
            int nx = x + k * sx;
            int ny = y + k * sy;
            if (nx >= 0 && nx < 17 && ny >= 0 && ny < 17 && d[ny * 17 + nx] == 1) {
                l++;
            }
            else {
                break;
            }
        }
        return l;
    }
    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player) || player.level().isClientSide() || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!player.getPersistentData().getBoolean("MagicShieldActive")) {
            return;
        }

        String recipe = player.getPersistentData().getString("TriggerRecipe");
        player.getPersistentData().putBoolean("MagicShieldActive", false);

        Entity directEntity = event.getSource().getDirectEntity();
        Vec3 direction = directEntity != null
                ? directEntity.position().subtract(player.position()).normalize()
                : new Vec3(0, 1, 0);

        runUnpackedSpell(serverLevel, player, player.position(), direction, recipe, false);
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity target = event.getEntity();
        if (!(target instanceof LivingEntity) || target.level().isClientSide() || target.tickCount % 5 != 0) {
            return;
        }
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB searchBox = target.getBoundingBox().inflate(0.3);
        List<Entity> traps = serverLevel.getEntities((Entity) null, searchBox, entity -> entity != null && entity.getTags().contains("MagicTrap"));

        for (Entity trap : traps) {
            String recipe = "";
            for (String tag : trap.getTags()) {
                if (tag.startsWith("SpellRecipe:")) {
                    recipe = tag.substring("SpellRecipe:".length());
                    break;
                }
            }

            if (recipe.isEmpty()) {
                continue;
            }

            runUnpackedSpell(serverLevel, null, trap.position(), new Vec3(0, 1, 0), recipe, true);
            trap.discard();
            serverLevel.playSound(null, trap.getX(), trap.getY(), trap.getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0F, 1.2F);
        }
    }

    private static ResourceLocation resourceLocationFromString(String id) {
        String value = id.trim();
        int separator = value.indexOf(':');
        if (separator < 0) {
            return ResourceLocation.withDefaultNamespace(value);
        }

        String namespace = value.substring(0, separator);
        String path = value.substring(separator + 1);
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    private static void runUnpackedSpell(ServerLevel level, Entity source, Vec3 position, Vec3 direction, String recipe, boolean isBlock) {
        try {
            String[] parts = recipe.split("\\|");
            if (parts.length < 4) {
                return;
            }

            String[] elementIds = parts[0].split(",");
            int power = Integer.parseInt(parts[1]);
            int vortex = Integer.parseInt(parts[2]);
            int type = Integer.parseInt(parts[3]);

            SpellStats stats = new SpellStats();
            stats.totalPower = power;
            stats.vortexBalance = vortex;
            stats.hasTrigger = false;

            for (String id : elementIds) {
                try {
                    ResourceLocation resourceLocation = resourceLocationFromString(id);
                    if (BuiltInRegistries.PARTICLE_TYPE.get(resourceLocation) instanceof SimpleParticleType particleType) {
                        stats.elements.add(particleType);
                    }
                } catch (RuntimeException ignored) {
                }
            }

            if (stats.elements.isEmpty()) {
                stats.elements.add(ParticleTypes.WITCH);
            }

            if (type == 1) {
                stats.tUp = power;
            } else if (type == 2) {
                stats.tInward = power;
            }

            executeMagicCore(level, source, position, direction, stats, isBlock);
        } catch (RuntimeException exception) {
            WitchAtelier.LOGGER.error("Failed to unpack spell recipe: {}", recipe, exception);
        }
    }

}
