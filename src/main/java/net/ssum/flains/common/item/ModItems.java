package net.ssum.flains.common.item;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.block.ModBlocks;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Flains.MOD_ID);

    // flowers
    public static final DeferredItem<BlockItem> THORN_FLOWER = ITEMS.registerSimpleBlockItem(
            "thorn_flower", ModBlocks.THORN_FLOWER
    );

    public static final DeferredItem<BlockItem> SMALL_POPPY = ITEMS.registerSimpleBlockItem(
            "small_poppy", ModBlocks.SMALL_POPPY
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}