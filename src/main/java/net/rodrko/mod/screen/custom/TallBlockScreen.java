package net.rodrko.mod.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.rodrko.mod.StinkyMod;

public class TallBlockScreen extends AbstractContainerScreen<TallBlockMenu> {
    public static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID,"textures/gui/tall_block/tall_block_gui.png");
    private static final ResourceLocation COIL_PROGRESS =
            ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID, "textures/gui/coil_progress.png");
    private static final ResourceLocation OVAL_FILLED =
            ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID, "textures/gui/oval.png");
    private static final ResourceLocation WHITE_BARS =
            ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID, "textures/gui/whitebar.png");

    private static final int COIL_X = 34;
    private static final int COIL_Y = 78;
    private static final int COIL_WIDTH = 44;
    private static final int COIL_HEIGHT = 12;

    private static final int OVAL_X = 78;
    private static final int OVAL_Y = 86;
    private static final int OVAL_HEIGHT = 6;
    private static final int OVAL_WIDTH = 18;

    private static final int RIGHT_BAR_X = 120;
    private static final int RIGHT_BAR_Y = 49; // bottom of bar
    private static final int RIGHT_BAR_WIDTH = 8;
    private static final int RIGHT_BAR_HEIGHT = 24;

    private static final int LEFT_BAR_X = 46;
    private static final int LEFT_BAR_Y = 49; // bottom of bar
    private static final int LEFT_BAR_WIDTH = 8;
    private static final int LEFT_BAR_HEIGHT = 24;

    public TallBlockScreen(TallBlockMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 176;
        this.imageHeight = 196;
        this.inventoryLabelY = this.imageHeight -  94;

        System.out.println("SCREEN OPENED! FuelTime = " + menu.getFuelTime());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);


        int fuelTime = this.menu.getFuelTime();

        if (fuelTime > 0) {
            float fuelPercent = Math.min(1.0F, (float) fuelTime / 216F);
            int ovalWidth = (int) (18F * fuelPercent);
            RenderSystem.setShaderTexture(0, OVAL_FILLED);
            guiGraphics.blit(OVAL_FILLED,
                    x + OVAL_X + (18 - ovalWidth),
                    y + OVAL_Y,
                    18 - ovalWidth, 0,
                    ovalWidth, 6, 18, 6);
        }

        // white bars
        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();

        // only calculate percent if max progress is > 0
        float progressPercent = maxProgress > 0 ? Math.min(1.0F, (float) progress / maxProgress) : 0F;

        int barHeight = (int) (LEFT_BAR_HEIGHT * progressPercent);

        if (barHeight > 0) {
            RenderSystem.setShaderTexture(0, WHITE_BARS);

            // left bar
            guiGraphics.blit(WHITE_BARS,
                    x + LEFT_BAR_X,
                    y + LEFT_BAR_Y -barHeight,
                    0, LEFT_BAR_HEIGHT - barHeight,
                    LEFT_BAR_WIDTH,
                    barHeight,
                    LEFT_BAR_WIDTH,
                    LEFT_BAR_HEIGHT);
            // right bar
            guiGraphics.blit(WHITE_BARS,
                    x + RIGHT_BAR_X,
                    y + RIGHT_BAR_Y - barHeight,
                    0, RIGHT_BAR_HEIGHT - barHeight,
                    RIGHT_BAR_WIDTH,
                    barHeight,
                    RIGHT_BAR_WIDTH,
                    RIGHT_BAR_HEIGHT);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}
