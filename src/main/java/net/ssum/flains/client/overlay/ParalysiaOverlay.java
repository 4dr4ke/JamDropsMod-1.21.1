package net.ssum.flains.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.ssum.flains.Flains;

@EventBusSubscriber(modid = Flains.MOD_ID, value = Dist.CLIENT)
public class ParalysiaOverlay {
    private static final ResourceLocation sprite = ResourceLocation.fromNamespaceAndPath(
            "flains", "textures/gui/paralysia_overlay.png"
    );

    private static boolean visible = false;
    private static int alphaInt = 0;

    public static void setVisible(boolean value) {
        visible = value;
        alphaInt = 0;
    }

    public static boolean isVisible() {
        return visible;
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        if (alphaInt < 255) {
            alphaInt += 60;
        }
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        if (!visible) return;

        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        GuiGraphics graphics = event.getGuiGraphics();

        float alpha = alphaInt / 255f;
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.blit(
                sprite,
                0, 0,
                screenWidth, screenHeight,
                0, 0,
                256, 256,
                256, 256
        );

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }


}
