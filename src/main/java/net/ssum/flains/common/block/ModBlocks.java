package net.ssum.flains.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.block.custom.LittleGrassBlock;
import net.ssum.flains.common.block.custom.ModFlammableRotatedPillarBlock;
import net.ssum.flains.common.block.custom.ThickBushBlock;
import net.ssum.flains.common.effect.ModEffects;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Flains.MOD_ID);

    // dead wood
    public static final DeferredBlock<DoorBlock> DEAD_WOOD_DOOR = BLOCKS.register("dead_wood_door",
            () -> new DoorBlock(
                    BlockSetType.OAK,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOD)
                            .strength(3.0f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<Block> DEAD_WOOD_LOG = BLOCKS.register("dead_wood_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));

    public static final DeferredBlock<Block> DEAD_WOOD_WOOD = BLOCKS.register("dead_wood_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)));

    public static final DeferredBlock<Block> STRIPPED_DEAD_WOOD_LOG = BLOCKS.register("stripped_dead_wood_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG)));

    public static final DeferredBlock<Block> STRIPPED_DEAD_WOOD_WOOD = BLOCKS.register("stripped_dead_wood_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD)));

    public static final DeferredBlock<Block> DEAD_WOOD_PLANKS = BLOCKS.register("dead_wood_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });

    public static final DeferredBlock<Block> DEAD_WOOD_SLAB = BLOCKS.register("dead_wood_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });

    public static final DeferredBlock<Block> DEAD_WOOD_STAIRS = BLOCKS.register("dead_wood_stairs",
            () -> new StairBlock(DEAD_WOOD_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });


    // plants
    public static final DeferredBlock<Block> CLOVERS = BLOCKS.register("clovers",
            () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS))
    );

    public static final DeferredBlock<Block> THICK_BUSH = BLOCKS.register("thick_bush",
            () -> new ThickBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<Block> LITTLE_GRASS = BLOCKS.register("little_grass",
            () -> new LittleGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    // flowers
    public static final DeferredBlock<FlowerBlock> HARMONY_FLOWER = BLOCKS.register("harmony_flower",
            () -> new FlowerBlock(ModEffects.HARMONY, 20,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<FlowerBlock> DEAD_HARMONY_FLOWER = BLOCKS.register("dead_harmony_flower",
            () -> new FlowerBlock(MobEffects.WITHER, 5,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<Block> ROSE = BLOCKS.register("rose",
            () -> new FlowerBlock(ModEffects.THORN_PROTECTION, 35f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<FlowerBlock> DEAD_ROSE = BLOCKS.register("dead_rose",
            () -> new FlowerBlock(MobEffects.WITHER, 5,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<Block> LAVENDER = BLOCKS.register("lavender",
            () -> new FlowerBlock(MobEffects.SATURATION, 5.0f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<FlowerBlock> DEAD_LAVENDER = BLOCKS.register("dead_lavender",
            () -> new FlowerBlock(MobEffects.WITHER, 5,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<Block> CORNFLOWER = BLOCKS.register("cornflower",
            () -> new FlowerBlock(MobEffects.SATURATION, 5.0f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<FlowerBlock> DEAD_CORNFLOWER = BLOCKS.register("dead_cornflower",
            () -> new FlowerBlock(MobEffects.WITHER, 5,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

