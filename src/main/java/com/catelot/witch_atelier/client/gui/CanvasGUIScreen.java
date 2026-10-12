package com.catelot.witch_atelier.client.gui;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.item.DrawingDataItem;
import com.catelot.witch_atelier.network.NotebookPageNetwork;
import com.catelot.witch_atelier.network.NotebookTearPageNetwork;
import com.catelot.witch_atelier.network.WitchNetwork;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.components.Button;
import com.catelot.witch_atelier.world.inventory.CanvasGUIMenu;
import com.mojang.blaze3d.systems.RenderSystem;

public class CanvasGUIScreen extends AbstractContainerScreen<CanvasGUIMenu> {
private static final ResourceLocation PAGE_TEXTURE = ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "textures/screens/magic_canvas.png");
private static final ResourceLocation NOTEBOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "textures/screens/notebook_canvas.png");

    private final Player entity;
    private Button leftBtn;
    private Button rightBtn;
    private Button tearBtn;
    private boolean isNotebookMode = false;

    private byte[] localCache = new byte[289];
    private boolean isInitialized = false;
    private boolean tearRequested = false;
    private int currentPage = 0;

    private final int CELL_SIZE = 5;
    private final int STEP = 6;
    private final int X_OFFSET = 14;
    private final int Y_OFFSET = 12;

    public CanvasGUIScreen(CanvasGUIMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.entity = container.entity;
        this.imageWidth = 129;
        this.imageHeight = 139;
        CompoundTag tag = this.entity.getMainHandItem().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        this.currentPage = Math.clamp(tag.getInt("NotebookPage"), 0, Math.max(0, getRemainingPages() - 1));
    }
    @Override
    public void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        super.init();

        int mid_x = this.leftPos + (this.imageWidth / 2);
        int btn_y = this.topPos + 120;

        this.leftBtn = this.addRenderableWidget(Button.builder(Component.literal("<"), (b) -> changePage(-1)).bounds(mid_x - 45, btn_y, 20, 20).build());
        this.rightBtn = this.addRenderableWidget(Button.builder(Component.literal(">"), (b) -> changePage(1)).bounds(mid_x + 25, btn_y, 20, 20).build());
        this.tearBtn = this.addRenderableWidget(Button.builder(Component.literal("✂"), button -> {
            if (!tearRequested && this.menu.isNotebook && getRemainingPages() > 0) {
                tearRequested = true;
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new NotebookTearPageNetwork(this.menu.containerId));
            }
        }).bounds(mid_x - 10, btn_y, 20, 20).build());

        updateButtonVisibility();
    }

    private void changePage(int delta) {
        int newPage = Math.clamp(this.currentPage + delta, 0, Math.max(0, getRemainingPages() - 1));
        if (newPage == this.currentPage) {
            return;
        }
        saveCurrentPageToItem();
        this.currentPage = newPage;
        loadPageFromItem(this.currentPage);
        sendPageToServer(this.currentPage);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.minecraft.player != null) {
            boolean isNotebook = this.menu.isNotebook;

            if (this.leftBtn != null) {
                this.leftBtn.visible = isNotebook;
                this.leftBtn.active = isNotebook && currentPage > 0;
            }
            if (this.rightBtn != null) {
                this.rightBtn.visible = isNotebook;
                this.rightBtn.active = isNotebook && currentPage + 1 < getRemainingPages();
            }
            if (this.tearBtn != null) {
                this.tearBtn.visible = isNotebook;
                this.tearBtn.active = isNotebook && !tearRequested && this.currentPage < getRemainingPages();
            }
        }
    }
    private void updateButtonVisibility() {
        if (this.minecraft.player != null) {
            boolean isNotebook = this.menu.isNotebook;

            if (this.leftBtn != null) {
                this.leftBtn.visible = isNotebook;
                this.leftBtn.active = isNotebook && currentPage > 0;
            }
            if (this.rightBtn != null) {
                this.rightBtn.visible = isNotebook;
                this.rightBtn.active = isNotebook && currentPage + 1 < getRemainingPages();
            }
            if (this.tearBtn != null) {
                this.tearBtn.visible = isNotebook;
                this.tearBtn.active = isNotebook && !tearRequested && this.currentPage < getRemainingPages();
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        updateButtonVisibility();

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        ItemStack stack = this.minecraft.player.getMainHandItem();
        String registryName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();

        ResourceLocation currentTexture = registryName.contains("drawing_data") ? PAGE_TEXTURE : NOTEBOOK_TEXTURE;

        guiGraphics.blit(currentTexture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderCanvas(guiGraphics);
        if (this.menu.isNotebook && getRemainingPages() > 0) {
            String pageText = (this.currentPage + 1) + " / " + getRemainingPages();
            guiGraphics.drawCenteredString(this.font, pageText, this.leftPos + this.imageWidth / 2, this.topPos + 142, 0xFFFFFF);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    private void renderCanvas(GuiGraphics guiGraphics) {
        int x_start = this.leftPos + X_OFFSET;
        int y_start = this.topPos + Y_OFFSET;

        if (!this.isInitialized) {
            loadPageFromItem(currentPage);
            isInitialized = true;
        }

        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                int di = i - 8;
                int dj = j - 8;
                if (di * di + dj * dj > 68) {
                    continue;
                }

                int px = x_start + i * STEP;
                int py = y_start + j * STEP;

                if (this.localCache[j * 17 + i] == 1) {
                    guiGraphics.fill(px, py, px + CELL_SIZE, py + CELL_SIZE, 0xFF000000);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int b) {
        this.updateDrawing(mx, my, b);
        return super.mouseClicked(mx, my, b);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int b, double dx, double dy) {
        this.updateDrawing(mx, my, b);
        return super.mouseDragged(mx, my, b, dx, dy);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.leftPos && mouseX < this.leftPos + this.imageWidth
            && mouseY >= this.topPos && mouseY < this.topPos + this.imageHeight;
    }

    private void updateDrawing(double mX, double mY, int mB) {
        boolean hasPen = false;
        boolean hasInk = false;
        for (ItemStack item : entity.getInventory().items) {
            String id = item.getDescriptionId();
            if (id.contains("pen")) {
                hasPen = true;
            }
            if (id.contains("ink") || id.contains("creative")) {
                hasInk = true;
            }
        }
        if (!hasPen || !hasInk || tearRequested || (this.menu.isNotebook && this.currentPage >= getRemainingPages())) {
            return;
        }

        double startX = this.leftPos + X_OFFSET;
        double startY = this.topPos + Y_OFFSET;

        int gridX = (int) Math.floor((mX - startX) / STEP);
        int gridY = (int) Math.floor((mY - startY) / STEP);

        if (gridX >= 0 && gridX < 17 && gridY >= 0 && gridY < 17) {
            int di = gridX - 8;
            int dj = gridY - 8;
            if (di * di + dj * dj > 68) {
                return;
            }

            byte inkState = (byte) (mB == 0 ? 1 : 0);
            int idx = gridY * 17 + gridX;

            if (this.localCache[idx] != inkState) {
                this.localCache[idx] = inkState;
                try {
                    WitchNetwork packet = new WitchNetwork(idx, (int) inkState);
                    net.neoforged.neoforge.network.PacketDistributor.sendToServer(packet);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void updateDrawing(double mX, double mY, int mB, double dX, double dY) {
        updateDrawing(mX, mY, mB);
    }

    @Override
    public void removed() {
        if (!tearRequested) {
            saveCurrentPageToItem();
            sendPageToServer(this.currentPage);
        }
        super.removed();
    }

    @Override
    public void onClose() {
        if (!tearRequested) {
            saveCurrentPageToItem();
            sendPageToServer(this.currentPage);
        }
        super.onClose();
    }

    private void saveCurrentPageToItem() {
        ItemStack stack = this.entity.getMainHandItem();
        if (stack.isEmpty() || tearRequested || (this.menu.isNotebook && this.currentPage >= getRemainingPages())) {
            return;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (this.menu.isNotebook) {
            tag.putByteArray("DrawingDataPage" + this.currentPage, this.localCache);
        } else {
            tag.putByteArray("DrawingData", this.localCache);
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private void loadPageFromItem(int page) {
        ItemStack stack = this.entity.getMainHandItem();
        if (stack.isEmpty()) {
            this.localCache = new byte[289];
            return;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        byte[] data = this.menu.isNotebook ? tag.getByteArray("DrawingDataPage" + page) : tag.getByteArray("DrawingData");
        if (this.menu.isNotebook && page == 0 && data.length != 289) {
            data = tag.getByteArray("DrawingData");
        }

        if (data.length == 289) {
            this.localCache = data.clone();
        }
        else {
            this.localCache = new byte[289];
        }
    }

    private int getRemainingPages() {
        return this.menu.isNotebook ? DrawingDataItem.getRemainingPages(this.entity.getMainHandItem()) : 1;
    }

    private void sendPageToServer(int page) {
        try {
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                new NotebookPageNetwork(page)
            );
        } catch (Exception ex) {
            WitchAtelier.LOGGER.info("");
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
