package net.ssum.flains.common.worldgen;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.ssum.flains.Flains;
import net.ssum.flains.common.block.ModBlocks;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> BLOOM_FLOWERS = registerKey("bloom_flowers");
    public static final ResourceKey<PlacedFeature> BLOOM_BUSHES = registerKey("bloom_bushes");
    public static final ResourceKey<PlacedFeature> BLOOM_TREES = registerKey("bloom_trees");
    public static final ResourceKey<PlacedFeature> BLOOM_CLOVERS = registerKey("bloom_clovers");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, BLOOM_CLOVERS,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOM_CLOVERS),
                List.of(
                        CountPlacement.of(4),
                        RarityFilter.onAverageOnceEvery(2),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()
                )
        );

        register(context, BLOOM_BUSHES,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOM_BUSHES),
                List.of(
                        CountPlacement.of(8),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()
                )
        );

        register(context, BLOOM_FLOWERS,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOM_FLOWERS),
                List.of(
                        CountPlacement.of(8),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()
                )
        );

        register(context, BLOOM_TREES,
                configuredFeatures.getOrThrow(TreeFeatures.OAK),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(6, 0.1F, 1), Blocks.OAK_SAPLING)
        );
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, name));
    }

    private static void register(
            BootstrapContext<PlacedFeature> context,
            ResourceKey<PlacedFeature> key,
            Holder<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers
    ) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
