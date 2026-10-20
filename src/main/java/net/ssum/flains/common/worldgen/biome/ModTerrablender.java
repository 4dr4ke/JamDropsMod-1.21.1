package net.ssum.flains.common.worldgen.biome;

import net.minecraft.resources.ResourceLocation;
import net.ssum.flains.Flains;
import terrablender.api.Regions;

public class ModTerrablender {
    public static void registerBiomes() {
        Regions.register(new ModOverworldRegion(
                ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "overworld"),
                10
        ));
    }
}
