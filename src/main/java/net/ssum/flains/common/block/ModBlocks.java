package net.ssum.flains.common.block;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Flains.MOD_ID);

    // Flowers
    public static final DeferredBlock<FlowerBlock> THORN_FLOWER = BLOCKS.register("thorn_flower",
            () -> new FlowerBlock(MobEffects.DAMAGE_RESISTANCE, 120, BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
            )
    );

    public static final DeferredBlock<Block> SMALL_POPPY = BLOCKS.register("small_poppy",
            () -> new Block(BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
