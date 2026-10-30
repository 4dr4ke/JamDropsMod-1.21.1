package net.ssum.flains.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.block.ModBlocks;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.item.custom.HornGasMaskItem;
import net.ssum.flains.common.item.custom.JavelinItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Flains.MOD_ID);

    public static final DeferredItem<Item> JAVELIN = ITEMS.register(
            "javelin", () -> new JavelinItem(new Item.Properties().attributes(SwordItem.createAttributes(
                    Tiers.STONE, 5, -2.4f
            )))
    );

    public static final DeferredItem<Item> HORN_GAS_MASK = ITEMS.register(
            "horn_gas_mask", () -> new HornGasMaskItem(new Item.Properties())
    );

    public static final DeferredItem<Item> MOUMOUTON_HORN = ITEMS.register(
            "moumouton_horn", () -> new Item(new Item.Properties())
    );

    public static final DeferredItem<Item> DURABLE_WOOL_FIBER = ITEMS.register(
            "durable_wool_fiber", () -> new Item(new Item.Properties())
    );

    // dead wood
    public static final DeferredItem<BlockItem> DEAD_WOOD_DOOR = ITEMS.registerSimpleBlockItem(
            "dead_wood_door", ModBlocks.DEAD_WOOD_DOOR
    );

    public static final DeferredItem<BlockItem> DEAD_WOOD_LOG = ITEMS.registerSimpleBlockItem(
            "dead_wood_log", ModBlocks.DEAD_WOOD_LOG
    );

    public static final DeferredItem<BlockItem> DEAD_WOOD_WOOD = ITEMS.registerSimpleBlockItem(
            "dead_wood_wood", ModBlocks.DEAD_WOOD_WOOD
    );

    public static final DeferredItem<BlockItem> DEAD_WOOD_PLANKS = ITEMS.registerSimpleBlockItem(
            "dead_wood_planks", ModBlocks.DEAD_WOOD_PLANKS
    );

    public static final DeferredItem<BlockItem> DEAD_WOOD_SLAB = ITEMS.registerSimpleBlockItem(
            "dead_wood_slab", ModBlocks.DEAD_WOOD_SLAB
    );

    public static final DeferredItem<BlockItem> DEAD_WOOD_STAIRS = ITEMS.registerSimpleBlockItem(
            "dead_wood_stairs", ModBlocks.DEAD_WOOD_STAIRS
    );

    public static final DeferredItem<BlockItem> STRIPPED_DEAD_WOOD_WOOD = ITEMS.registerSimpleBlockItem(
            "stripped_dead_wood_wood", ModBlocks.STRIPPED_DEAD_WOOD_WOOD
    );

    public static final DeferredItem<BlockItem> STRIPPED_DEAD_WOOD_LOG = ITEMS.registerSimpleBlockItem(
            "stripped_dead_wood_log", ModBlocks.STRIPPED_DEAD_WOOD_LOG
    );

    // spawn eggs
    public static final DeferredItem<Item> MOUMOUTON_SPAWN_EGG = ITEMS.register("moumouton_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    ModEntities.MOUMOUTON,
                    0xecdaf0, 0xfff780,
                    new Item.Properties()
            )
    );

    public static final DeferredItem<Item> DEER_SPAWN_EGG = ITEMS.register("deer_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    ModEntities.DEER,
                    0xc28e5d, 0xd6ba9f,
                    new Item.Properties()
            )
    );

    // plants
    public static final DeferredItem<BlockItem> CLOVERS = ITEMS.registerSimpleBlockItem(
            "clovers", ModBlocks.CLOVERS
    );

    public static final DeferredItem<BlockItem> THICK_BUSH = ITEMS.registerSimpleBlockItem(
            "thick_bush", ModBlocks.THICK_BUSH
    );

    public static final DeferredItem<BlockItem> LITTLE_GRASS = ITEMS.registerSimpleBlockItem(
            "little_grass", ModBlocks.LITTLE_GRASS
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

    public static final DeferredItem<BlockItem> DEAD_HARMONY_FLOWER = ITEMS.registerSimpleBlockItem(
            "dead_harmony_flower", ModBlocks.DEAD_HARMONY_FLOWER
    );

    public static final DeferredItem<BlockItem> DEAD_ROSE = ITEMS.registerSimpleBlockItem(
            "dead_rose", ModBlocks.DEAD_ROSE
    );

    public static final DeferredItem<BlockItem> DEAD_LAVENDER = ITEMS.registerSimpleBlockItem(
            "dead_lavender", ModBlocks.DEAD_LAVENDER
    );

    public static final DeferredItem<BlockItem> DEAD_CORNFLOWER = ITEMS.registerSimpleBlockItem(
            "dead_cornflower", ModBlocks.DEAD_CORNFLOWER
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}