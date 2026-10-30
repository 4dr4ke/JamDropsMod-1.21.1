package net.ssum.flains.client.layer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.ssum.flains.common.entity.custom.DeerEntity;

public class StaminaBarLayer {
    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (!(mc.player.getVehicle() instanceof DeerEntity deer)) return;

        int x = graphics.guiWidth() / 2 - BAR_WIDTH / 2;
        int y = graphics.guiHeight() - 29;

        float ratio = deer.getStaminaRatio();
        int filled = (int) (BAR_WIDTH * ratio);

        graphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xFF000000);
        graphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xFF3A3A3A);

        int color = ratio > 0.5F ? 0xFF55FF55 : ratio > 0.25F ? 0xFFFFAA00 : 0xFFFF5555;
        graphics.fill(x, y, x + filled, y + BAR_HEIGHT, color);
    }
}
