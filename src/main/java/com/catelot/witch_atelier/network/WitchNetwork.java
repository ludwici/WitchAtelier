package com.catelot.witch_atelier.network;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.magic.SpellEngine;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import com.catelot.witch_atelier.world.inventory.CanvasGUIMenu;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WitchNetwork(int index, int state) implements CustomPacketPayload {
    public static final Type<WitchNetwork> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "drawing_sync"));
    public static final StreamCodec<FriendlyByteBuf, WitchNetwork> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            WitchNetwork::index,
            ByteBufCodecs.VAR_INT,
            WitchNetwork::state,
            WitchNetwork::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WitchNetwork message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null || !(player.containerMenu instanceof CanvasGUIMenu)) {
                return;
            }

            if (message.state() != 0 && message.state() != 1) {
                return;
            }

            if (message.index() == 2000) {
                SpellEngine.processCanvasUpdate(player, 2000);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.closeContainer();
                }
                return;
            }

            if (message.index() < 0 || message.index() >= 289) {
                return;
            }

            ItemStack pen = ItemStack.EMPTY;
            ItemStack ink = ItemStack.EMPTY;
            int inkSlot = -1;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item.isEmpty()) {
                    continue;
                }

                if (item.is(WitchAtelierModItems.MAGIC_PEN.get())) {
                    pen = item;
                }

                if (item.is(WitchAtelierModItems.MAGIC_INK.get()) || item.is(WitchAtelierModItems.CREATIVE_INK.get())) {
                    ink = item;
                    inkSlot = i;
                }
            }

            if (pen.isEmpty() || ink.isEmpty()) {
                return;
            }

            if (message.state() == 1 && !ink.is(WitchAtelierModItems.CREATIVE_INK.get())) {
                int damage = ink.getDamageValue() + 1;
                ink.setDamageValue(damage);

                if (damage >= ink.getMaxDamage()) {
                    player.getInventory().setItem(inkSlot, new ItemStack(Items.GLASS_BOTTLE));
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.containerMenu.broadcastChanges();
                        serverPlayer.inventoryMenu.broadcastChanges();
                    }

                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
                } else if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.inventoryMenu.broadcastChanges();
                }
            }

            ItemStack notebook = player.getMainHandItem();
            if (notebook.isEmpty()) {
                return;
            }

            CustomData customData = notebook.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = customData.copyTag();
            String key = tag.contains("NotebookPage") ? "DrawingDataPage" + tag.getInt("NotebookPage") : "DrawingData";

            byte[] data = tag.getByteArray(key);
            if (data.length != 289) {
                data = new byte[289];
            }

            data[message.index()] = (byte) message.state();
            tag.putByteArray(key, data);
            notebook.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            try {
                SpellEngine.processCanvasUpdate(player, message.index());
            } catch (Throwable throwable) {
                WitchAtelier.LOGGER.error("Failed to process canvas update", throwable);
            }
        });
    }
}
