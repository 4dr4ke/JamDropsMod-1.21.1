package net.ssum.flains.common.item;


import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.block.ModBlocks;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.item.custom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    public static final List<DeferredItem<Item>> MOD_SPAWN_EGGS = new ArrayList<>();

    private static DeferredItem<Item> registerSpawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> entityType, int bg, int highlight) {
        DeferredItem<Item> spawnEgg = ITEMS.register(name,
                () -> new DeferredSpawnEggItem(entityType, bg, highlight, new Item.Properties()));
        MOD_SPAWN_EGGS.add(spawnEgg);
        return spawnEgg;
    }

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Flains.MOD_ID);

    public static final DeferredItem<Item> JAVELIN = ITEMS.register(
            "javelin", () -> new JavelinItem(new Item.Properties().attributes(SwordItem.createAttributes(
                    Tiers.STONE, 4, -2.4f
            )).stacksTo(16))
    );

    public static final DeferredItem<Item> HORN_GAS_MASK = ITEMS.register(
            "horn_gas_mask", () -> new HornGasMaskItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .durability(250)
                    .setNoRepair()
            )
    );

    public static final DeferredItem<Item> MOUMOUTON_HORN = ITEMS.register(
            "moumouton_horn", () -> new Item(new Item.Properties())
    );

    public static final DeferredItem<Item> GOLDEN_APPLE_ON_A_STICK = ITEMS.register(
            "golden_apple_on_a_stick", () -> new Item(new Item.Properties())
    );

    public static final DeferredItem<Item> DURABLE_WOOL_FIBER = ITEMS.register(
            "durable_wool_fiber", () -> new Item(new Item.Properties())
    );

    // spawn eggs
    public static final DeferredItem<Item> MOUMOUTON_SPAWN_EGG = registerSpawnEgg(
            "moumouton_spawn_egg", ModEntities.MOUMOUTON, 0xecdaf0, 0xfff780);

    public static final DeferredItem<Item> DEER_SPAWN_EGG = registerSpawnEgg(
            "deer_spawn_egg", ModEntities.DEER, 0xc28e5d, 0xd6ba9f);

    public static final DeferredItem<Item> GUDANNA_SPAWN_EGG = registerSpawnEgg(
            "gudanna_spawn_egg", ModEntities.GUDANNA, 0x69442d, 0x6fb848);

    public static final DeferredItem<Item> HEDGEHOG_SPAWN_EGG = registerSpawnEgg(
            "hedgehog_spawn_egg", ModEntities.HEDGEHOG, 0xad7940, 0xffe7cc);


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