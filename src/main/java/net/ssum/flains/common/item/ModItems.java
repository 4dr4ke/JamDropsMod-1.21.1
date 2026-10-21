package net.ssum.flains.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.block.ModBlocks;
import net.ssum.flains.common.item.custom.HornGasMaskItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Flains.MOD_ID);

    public static final DeferredItem<Item> HORN_GAS_MASK = ITEMS.register(
            "horn_gas_mask", () -> new HornGasMaskItem(new Item.Properties())
    );

    public static final DeferredItem<Item> MOUMOUTON_HORN = ITEMS.register(
            "moumouton_horn", () -> new Item(new Item.Properties())
    );

    public static final DeferredItem<Item> DURABLE_WOOL_FIBER = ITEMS.register(
            "durable_wool_fiber", () -> new Item(new Item.Properties())
    );

    // plants
    public static final DeferredItem<BlockItem> CLOVERS = ITEMS.registerSimpleBlockItem(
            "clovers", ModBlocks.CLOVERS
    );

    public static final DeferredItem<BlockItem> THICK_BUSH = ITEMS.registerSimpleBlockItem(
            "thick_bush", ModBlocks.THICK_BUSH
    );

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