package com.catelot.witch_atelier.item;

import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.component.CustomData;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;

import com.catelot.witch_atelier.world.inventory.CanvasGUIMenu;

import io.netty.buffer.Unpooled;

public class DrawingDataItem extends Item {
    public static final int PAGE_COUNT = 12;
    public static final int DRAWING_SIZE = 289;
    public DrawingDataItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
        InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        if (entity instanceof ServerPlayer serverPlayer) {
            compactLegacyPages(entity.getItemInHand(hand));
            if (entity.getItemInHand(hand).isEmpty()) {
                return ar;
            }
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("item.witch_atelier.drawing_data");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                    FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                    packetBuffer.writeBlockPos(entity.blockPosition());
                    packetBuffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                    return new CanvasGUIMenu(id, inventory, packetBuffer);
                }
            }, buf -> {
                buf.writeBlockPos(entity.blockPosition());
                buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
            });
        }
        return ar;
    }

    public static int getRemainingPages(ItemStack notebook) {
        if (!notebook.is(WitchAtelierModItems.DRAWING_DATA.get())) {
            return 0;
        }
        return PAGE_COUNT - Math.clamp(notebook.getDamageValue(), 0, PAGE_COUNT);
    }

    private static void compactLegacyPages(ItemStack notebook) {
        CompoundTag tag = notebook.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains("TornPages")) {
            return;
        }

        int tornPages = tag.getInt("TornPages") & ((1 << PAGE_COUNT) - 1);
        int selected = Math.clamp(tag.getInt("NotebookPage"), 0, PAGE_COUNT - 1);
        int remaining = 0;
        int newSelected = 0;
        byte[][] drawings = new byte[PAGE_COUNT][];
        for (int page = 0; page < PAGE_COUNT; page++) {
            if ((tornPages & (1 << page)) == 0) {
                byte[] drawing = tag.getByteArray("DrawingDataPage" + page);
                if (page == 0 && drawing.length != DRAWING_SIZE) {
                    drawing = tag.getByteArray("DrawingData");
                }
                drawings[remaining++] = drawing;
                if (page < selected) {
                    newSelected++;
                }
            }
            tag.remove("DrawingDataPage" + page);
        }
        tag.remove("DrawingData");
        tag.remove("TornPages");
        for (int page = 0; page < remaining; page++) {
            if (drawings[page].length == DRAWING_SIZE) {
                tag.putByteArray("DrawingDataPage" + page, drawings[page]);
            }
        }
        tag.putInt("NotebookPage", Math.clamp(newSelected, 0, Math.max(0, remaining - 1)));
        tag.remove("WasCircleFull");
        notebook.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        notebook.setDamageValue(PAGE_COUNT - remaining);
        if (remaining == 0) {
            notebook.shrink(1);
        }
    }

    public static boolean tearCurrentPage(ServerPlayer player, ItemStack notebook) {
        if (!notebook.is(WitchAtelierModItems.DRAWING_DATA.get())) {
            return false;
        }

        int remaining = getRemainingPages(notebook);
        CompoundTag notebookTag = notebook.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int page = notebookTag.getInt("NotebookPage");
        if (remaining <= 0 || page < 0 || page >= remaining) {
            return false;
        }

        if (notebookTag.getByteArray("DrawingDataPage0").length != DRAWING_SIZE) {
            byte[] firstPage = notebookTag.getByteArray("DrawingData");
            if (firstPage.length == DRAWING_SIZE) {
                notebookTag.putByteArray("DrawingDataPage0", firstPage);
            }
        }
        notebookTag.remove("DrawingData");

        byte[] drawing = notebookTag.getByteArray("DrawingDataPage" + page);
        if (drawing.length != DRAWING_SIZE) {
            drawing = new byte[DRAWING_SIZE];
        }

        ItemStack sheet = new ItemStack(WitchAtelierModItems.MAGIC_PAGE_ITEM.get());
        CompoundTag sheetTag = new CompoundTag();
        sheetTag.putByteArray("DrawingData", drawing);
        sheet.set(DataComponents.CUSTOM_DATA, CustomData.of(sheetTag));

        for (int i = page; i < remaining - 1; i++) {
            String nextKey = "DrawingDataPage" + (i + 1);
            String currentKey = "DrawingDataPage" + i;
            if (notebookTag.contains(nextKey)) {
                notebookTag.putByteArray(currentKey, notebookTag.getByteArray(nextKey));
            } else {
                notebookTag.remove(currentKey);
            }
        }
        notebookTag.remove("DrawingDataPage" + (remaining - 1));
        notebookTag.putInt("NotebookPage", Math.clamp(page, 0, Math.max(0, remaining - 2)));
        notebookTag.remove("WasCircleFull");
        notebook.set(DataComponents.CUSTOM_DATA, CustomData.of(notebookTag));
        notebook.setDamageValue(PAGE_COUNT - (remaining - 1));

        if (remaining == 1) {
            notebook.shrink(1);
        }

        if (!player.getInventory().add(sheet)) {
            player.drop(sheet, false);
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.4F);
        player.inventoryMenu.broadcastChanges();
        return true;
    }
}
