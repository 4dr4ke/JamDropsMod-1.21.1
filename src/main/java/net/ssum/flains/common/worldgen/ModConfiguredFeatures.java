package net.ssum.flains.common.worldgen;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.ssum.flains.Flains;
import net.ssum.flains.common.block.ModBlocks;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_FLOWERS = registerKey("bloom_flowers");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_BUSHES = registerKey("bloom_bushes");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_CLOVERS = registerKey("bloom_clovers");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_LITTLE_GRASS = registerKey("bloom_little_grass");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        SimpleWeightedRandomList.Builder<BlockState> petals = SimpleWeightedRandomList.builder();
        for (int i = 1; i <= 4; i++) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                petals.add(ModBlocks.CLOVERS.get().defaultBlockState()
                        .setValue(PinkPetalsBlock.AMOUNT, i)
                        .setValue(PinkPetalsBlock.FACING, direction), 1);
            }
        }

        register(context, BLOOM_CLOVERS, Feature.FLOWER, new RandomPatchConfiguration(
                96, 6, 1,
                PlacementUtils.onlyWhenEmpty(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(
                                new WeightedStateProvider(petals)
                        )
                )
        ));

        register(context, BLOOM_BUSHES, Feature.FLOWER,
                new RandomPatchConfiguration(
                        96, 6, 2,
                        PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(new WeightedStateProvider(
                                        SimpleWeightedRandomList.<BlockState>builder()
                                                .add(ModBlocks.THICK_BUSH.get().defaultBlockState(), 1)
                                                .build())))));

        register(context, BLOOM_LITTLE_GRASS, Feature.FLOWER,
                new RandomPatchConfiguration(
                        96, 6, 2,
                        PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(new WeightedStateProvider(
                                        SimpleWeightedRandomList.<BlockState>builder()
                                                .add(ModBlocks.LITTLE_GRASS.get().defaultBlockState(), 1)
                                                .build())))));

        register(context, BLOOM_FLOWERS, Feature.FLOWER,
                new RandomPatchConfiguration(
                        96, 6, 2,
                        PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(new WeightedStateProvider(
                                        SimpleWeightedRandomList.<BlockState>builder()
                                                .add(Blocks.POPPY.defaultBlockState(), 2)
                                                .add(Blocks.CORNFLOWER.defaultBlockState(), 1)
                                                .add(Blocks.ALLIUM.defaultBlockState(), 2)
                                                .add(Blocks.AZURE_BLUET.defaultBlockState(), 1)
                                                .add(ModBlocks.CORNFLOWER.get().defaultBlockState(), 2)
                                                .add(ModBlocks.HARMONY_FLOWER.get().defaultBlockState(), 1)
                                                .add(ModBlocks.LAVENDER.get().defaultBlockState(), 2)
                                                .add(ModBlocks.ROSE.get().defaultBlockState(), 1)
                                                .build())))));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, name)
        );
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration
    ) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
