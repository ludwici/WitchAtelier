package com.catelot.witch_atelier.item;

import com.catelot.witch_atelier.WitchAtelier;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.world.phys.Vec3;
import net.minecraft.Util;

import java.util.List;
import java.util.EnumMap;


@EventBusSubscriber(modid = WitchAtelier.MODID)
public abstract class SylphShoesItem extends ArmorItem {
    public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

    @SubscribeEvent
    public static void registerArmorMaterial(RegisterEvent event) {
        event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
            ArmorMaterial armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.BODY, 6);
            }), 9, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), () -> Ingredient.of(), List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "leather"))), 2f, 0f);
            registerHelper.register(ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "sylph_shoes"), armorMaterial);
            ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
        });
    }

    public SylphShoesItem(ArmorItem.Type type, Item.Properties properties) {
        super(ARMOR_MATERIAL, type, properties);
    }

    public static class Boots extends SylphShoesItem {
        public Boots(Item.Properties properties) {
            super(ArmorItem.Type.BOOTS, properties);
        }

        private final ResourceLocation armorTexture = ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "textures/entities/leather_layer_1.png");

        @Override
        public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
            return armorTexture;
        }
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.FEET;
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return stack.getDamageValue() < 99;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level instanceof ServerLevel) {
            if (entity instanceof Player player) {
                if (player.getItemBySlot(EquipmentSlot.FEET).is(this)) {
                    if (player.isInWater()) {
                        stack.setDamageValue(99);
                    }
                }
            }
        }
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        Level world = entity.level();

        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                entity.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ELYTRA_GLIDE);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                world.addParticle(ParticleTypes.HAPPY_VILLAGER,
                        entity.getX() + (world.random.nextDouble() - 0.5),
                        entity.getY(),
                        entity.getZ() + (world.random.nextDouble() - 0.5),
                        0, -0.1, 0);
            }
        }

        if (entity instanceof Player player) {
            if (player.isShiftKeyDown()) {
                player.setDeltaMovement(0, 0.01, 0);
                player.fallDistance = 0;

                if (player.zza > 0) {
                    Vec3 look = player.getLookAngle();
                    double p = 2.5;
                    player.setDeltaMovement(look.x * p, look.y * p, look.z * p);

                    if (!world.isClientSide) {
                        ServerLevel sWorld = (ServerLevel) world;
                        sWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.0F, 1.5F);
                        sWorld.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY(), player.getZ(), 50, 0.5, 0.5, 0.5, 0.2);
                    }
                }
            }
        }

        return true;
    }
}
