package com.catelot.witch_atelier.network;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.item.DrawingDataItem;
import com.catelot.witch_atelier.world.inventory.CanvasGUIMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NotebookTearPageNetwork(int containerId) implements CustomPacketPayload {
    public static final Type<NotebookTearPageNetwork> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "notebook_tear_page"));
    public static final StreamCodec<FriendlyByteBuf, NotebookTearPageNetwork> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, NotebookTearPageNetwork::containerId, NotebookTearPageNetwork::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(NotebookTearPageNetwork message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)
                    || !(player.containerMenu instanceof CanvasGUIMenu menu)
                    || menu.containerId != message.containerId()
                    || !menu.isNotebook) {
                return;
            }

            if (DrawingDataItem.tearCurrentPage(player, menu.getEditingStack())) {
                player.closeContainer();
            }
        });
    }
}
