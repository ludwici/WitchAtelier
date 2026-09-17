package com.meowkings.witch_atelier.client.gui;

import com.meowkings.witch_atelier.WitchAtelier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import com.meowkings.witch_atelier.world.inventory.MagicGuideGUIMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;

public class MagicGuideGUIScreen extends AbstractContainerScreen<MagicGuideGUIMenu> {
    private int currentPage = 0;
    private static final int TOTAL_PAGES = 11;
    private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            WitchAtelier.MODID,
            "textures/screens/magic_guide_gui.png"
    );

    public MagicGuideGUIScreen(MagicGuideGUIMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.imageWidth = 280;
        this.imageHeight = 180;
    }

    @Override
    public void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.addRenderableWidget(
                Button.builder(Component.literal("<"), button -> {
                            if (currentPage > 0) {
                                currentPage--;
                            }
                        })
                        .bounds(this.leftPos + 25, this.topPos + 145, 20, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal(">"), button -> {
                            if (currentPage < TOTAL_PAGES - 1) {
                                currentPage++;
                            }
                        })
                        .bounds(this.leftPos + 235, this.topPos + 145, 20, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BOOK_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderPageContent(guiGraphics);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    private void renderPageContent(GuiGraphics g) {
        int x = this.leftPos;
        int y = this.topPos;
        Component title = Component.empty();
        Component description = Component.empty();
        byte[] pattern = new byte[289];

        switch (currentPage) {
            case 0 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.basics.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.basics.description");
            }
            case 1 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.fire.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.fire.description");
                pattern[8 * 17 + 8] = 1;
                pattern[7 * 17 + 8] = 1;
                pattern[9 * 17 + 8] = 1;
                pattern[8 * 17 + 7] = 1;
                pattern[8 * 17 + 9] = 1;
            }
            case 2 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.light.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.light.description");
                for (int i = 7; i <= 9; i++) {
                    pattern[7 * 17 + i] = 1;
                    pattern[9 * 17 + i] = 1;
                }
                pattern[8 * 17 + 7] = 1;
                pattern[8 * 17 + 9] = 1;
            }
            case 3 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.water.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.water.description");
                pattern[7 * 17 + 8] = 1;
                pattern[7 * 17 + 9] = 1;
                pattern[8 * 17 + 8] = 1;
                pattern[9 * 17 + 7] = 1;
                pattern[9 * 17 + 8] = 1;
            }
            case 4 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.wind.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.wind.description");
                pattern[7 * 17 + 7] = 1;
                pattern[7 * 17 + 9] = 1;
                pattern[8 * 17 + 8] = 1;
                pattern[9 * 17 + 7] = 1;
                pattern[9 * 17 + 9] = 1;
            }
            case 5 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.earth.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.earth.description");
                pattern[7 * 17 + 7] = 1;
                pattern[7 * 17 + 9] = 1;
                pattern[8 * 17 + 7] = 1;
                pattern[8 * 17 + 8] = 1;
                pattern[8 * 17 + 9] = 1;
                pattern[9 * 17 + 7] = 1;
                pattern[9 * 17 + 9] = 1;
            }
            case 6 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.crystal.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.crystal.description");
                for (int i = 7; i <= 9; i++) {
                    for (int j = 7; j <= 9; j++) {
                        pattern[i * 17 + j] = 1;
                    }
                }
            }
            case 7 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.ice.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.ice.description");
                pattern[6 * 17 + 6] = 1;
                pattern[6 * 17 + 7] = 1;
                pattern[7 * 17 + 6] = 1;
                pattern[8 * 17 + 5] = 1;
                pattern[8 * 17 + 6] = 1;
                pattern[9 * 17 + 10] = 1;
                pattern[9 * 17 + 12] = 1;
                pattern[10 * 17 + 11] = 1;
                pattern[11 * 17 + 10] = 1;
                pattern[11 * 17 + 12] = 1;
            }
            case 8 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.vectors.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.vectors.description");
                pattern[2 * 17 + 8] = 1;
                pattern[1 * 17 + 7] = 1;
                pattern[1 * 17 + 8] = 1;
                pattern[1 * 17 + 9] = 1;
            }
            case 9 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.vortices.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.vortices.description");
                pattern[3 * 17 + 4] = 1;
                pattern[4 * 17 + 3] = 1;
                pattern[5 * 17 + 2] = 1;
            }
            case 10 -> {
                title = Component.translatable("screen.witch_atelier.magic_guide.trigger.title");
                description = Component.translatable("screen.witch_atelier.magic_guide.trigger.description");
                pattern[14 * 17 + 8] = 1;
                pattern[15 * 17 + 7] = 1;
                pattern[15 * 17 + 9] = 1;
                pattern[16 * 17 + 8] = 1;
            }
        }

        g.drawString(this.font, title, x + 40, y + 25, 0x5D2906, false);
        renderSplitText(g, description, x + 35, y + 45, 100);
        renderTutorialGrid(g, x + 150, y + 25, pattern);
    }
    private void renderSplitText(GuiGraphics g, Component text, int x, int y, int width) {
        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(text, width);
        int currentY = y;
        for (net.minecraft.util.FormattedCharSequence line : lines) {
            g.drawString(this.font, line, x, currentY, 0x333333, false);
            currentY += 10;
        }
    }

    private void renderTutorialGrid(GuiGraphics g, int x, int y, byte[] pattern) {
        int s = 5;
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                int px = x + i * s;
                int py = y + j * s;
                int di = i - 8;
                int dj = j - 8;

                if (di * di + dj * dj <= 70) {
                    g.fill(px, py, px + s - 1, py + s - 1, 0xFFF5E6C8);

                    if (i >= 5 && i <= 11 && j >= 5 && j <= 11) {
                        g.fill(px, py, px + s - 1, py + s - 1, 0x15000000);
                    }

                    if (pattern[j * 17 + i] == 1) {
                        g.fill(px, py, px + s - 1, py + s - 1, 0xFF000000);
                    }
                }
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }
}
