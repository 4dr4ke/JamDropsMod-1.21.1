package net.ssum.flains.common.block;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.block.custom.ThickBushBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Flains.MOD_ID);

    // plants
    public static final DeferredBlock<Block> CLOVERS = BLOCKS.register("clovers",
            () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS))
    );

    public static final DeferredBlock<Block> THICK_BUSH = BLOCKS.register("thick_bush",
            () -> new ThickBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    // flowers
    public static final DeferredBlock<FlowerBlock> HARMONY_FLOWER = BLOCKS.register("harmony_flower",
            () -> new FlowerBlock(
                    MobEffects.DAMAGE_RESISTANCE, // TODO: harmony effect
                    20,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)
            )
    );

    public static final DeferredBlock<Block> ROSE = BLOCKS.register("rose",
            () -> new FlowerBlock(MobEffects.SATURATION, 5.0F,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)) // TODO: thorn protection effect
    );

    public static final DeferredBlock<Block> LAVENDER = BLOCKS.register("lavender",
            () -> new FlowerBlock(MobEffects.SATURATION, 5.0F,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<Block> CORNFLOWER = BLOCKS.register("cornflower",
            () -> new FlowerBlock(MobEffects.SATURATION, 5.0F,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

//    public static final DeferredBlock<Block> SMALL_POPPY = BLOCKS.register("small_poppy",
//            () -> new Block(BlockBehaviour.Properties.of()
//                    .noCollission()
//                    .instabreak()
//                    .sound(SoundType.GRASS)
//            )
//    );

