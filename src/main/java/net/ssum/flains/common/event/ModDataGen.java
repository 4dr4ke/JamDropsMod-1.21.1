package net.ssum.flains.common.event;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.common.worldgen.ModConfiguredFeatures;
import net.ssum.flains.common.worldgen.ModPlacedFeatures;
import net.ssum.flains.common.worldgen.biome.ModBiomes;

import java.util.Set;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class ModDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(),
                new DatapackBuiltinEntriesProvider(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider(),
                        new RegistrySetBuilder()
                                .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
                                .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
                                .add(Registries.BIOME, ModBiomes::bootstrap),
                        Set.of(Flains.MOD_ID)));
    }
}
