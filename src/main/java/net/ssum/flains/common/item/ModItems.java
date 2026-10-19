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
    public static final DeferredItem<BlockItem> HARMONY_FLOWER = ITEMS.registerSimpleBlockItem(
            "harmony_flower", ModBlocks.HARMONY_FLOWER
    );

    public static final DeferredItem<BlockItem> ROSE = ITEMS.registerSimpleBlockItem(
            "rose", ModBlocks.ROSE
    );

    public static final DeferredItem<BlockItem> LAVENDER = ITEMS.registerSimpleBlockItem(
            "lavender", ModBlocks.LAVENDER
    );

    public static final DeferredItem<BlockItem> CORNFLOWER = ITEMS.registerSimpleBlockItem(
            "cornflower", ModBlocks.CORNFLOWER
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}