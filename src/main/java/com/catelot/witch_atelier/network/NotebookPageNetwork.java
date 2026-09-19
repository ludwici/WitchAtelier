package com.catelot.witch_atelier.network;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import com.catelot.witch_atelier.world.inventory.CanvasGUIMenu;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NotebookPageNetwork(int page) implements CustomPacketPayload {
    public static final Type<NotebookPageNetwork> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "notebook_page"));
    public static final StreamCodec<FriendlyByteBuf, NotebookPageNetwork> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, NotebookPageNetwork::page, NotebookPageNetwork::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(NotebookPageNetwork message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null || !(player.containerMenu instanceof CanvasGUIMenu)) {
                return;
            }

            ItemStack stack = player.getMainHandItem();
            if (stack.isEmpty() || !stack.is(WitchAtelierModItems.DRAWING_DATA.get())) {
                return;
            }

            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = customData.copyTag();
            int page = Math.max(0, Math.min(11, message.page()));

            tag.putInt("NotebookPage", page);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            player.containerMenu.broadcastChanges();
        });
    }
}
