package net.ssum.flains.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlainsClientHooks {

    public static boolean isJumpPressed() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }
}
