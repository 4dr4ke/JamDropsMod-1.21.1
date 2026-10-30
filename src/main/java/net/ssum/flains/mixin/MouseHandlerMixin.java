package net.ssum.flains.mixin;

import net.minecraft.client.MouseHandler;
import net.ssum.flains.client.ClientCameraSlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    @Inject(method = "turnPlayer", at = @At("HEAD"))
    private void flains$slowCamera(CallbackInfo ci) {
        double factor = ClientCameraSlow.getFactor();
        this.accumulatedDX *= factor;
        this.accumulatedDY *= factor;
    }
}
