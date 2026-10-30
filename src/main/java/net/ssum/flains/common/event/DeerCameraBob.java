package net.ssum.flains.common.event;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.custom.DeerEntity;

@EventBusSubscriber(modid = Flains.MOD_ID, value = Dist.CLIENT)
public class DeerCameraBob {

    private static float phase, phaseO;
    private static float amp, ampO;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused()) return;

        phaseO = phase;
        ampO = amp;

        if (mc.player != null && mc.player.getVehicle() instanceof DeerEntity mount) {
            double dx = mount.getX() - mount.xo;
            double dz = mount.getZ() - mount.zo;
            float speed = (float) Math.sqrt(dx * dx + dz * dz);

            boolean boosting = mount.isBoosting();
            float targetAmp = speed < 0.02F ? 0.0F : (boosting ? 0.7F : 0.3F);

            amp += (targetAmp - amp) * 0.3F;
            phase += speed * (boosting ? 0.55F : 0.4F);
        } else {
            amp += (0.0F - amp) * 0.2F;
        }
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.options.bobView().get()) return;
        if (!mc.options.getCameraType().isFirstPerson()) return;

        float pt = (float) event.getPartialTick();
        float a = Mth.lerp(pt, ampO, amp);
        if (a < 0.001F) return;
        float p = Mth.lerp(pt, phaseO, phase);

        event.setRoll(event.getRoll() + Mth.sin(p * Mth.PI) * a * 0.9F);
        event.setPitch(event.getPitch() + Math.abs(Mth.cos(p * Mth.PI)) * a * 0.7F);
    }
}