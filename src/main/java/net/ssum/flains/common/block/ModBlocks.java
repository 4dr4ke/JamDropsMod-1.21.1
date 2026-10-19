package net.ssum.flains.common.block;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Flains.MOD_ID);

    // flowers
    public static final DeferredBlock<FlowerBlock> HARMONY_FLOWER = BLOCKS.register("harmony_flower",
            () -> new FlowerBlock(
                    MobEffects.DAMAGE_RESISTANCE, // TODO: harmony effect
                    20,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)
            )
    );

    public static final DeferredBlock<Block> ROSE = BLOCKS.register("rose",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)) // TODO: thorn protection effect
    );

    public static final DeferredBlock<Block> LAVENDER = BLOCKS.register("lavender",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<Block> CORNFLOWER = BLOCKS.register("cornflower",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
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

