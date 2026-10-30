package net.ssum.flains.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Flains.MOD_ID);

    public static final Supplier<CreativeModeTab> FLAINS_TAB = CREATIVE_MODE_TAB.register("flains_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.ROSE.get()))
                    .title(Component.translatable("itemGroup.flains"))
                    .displayItems((itemDisplayParameters, output) -> {

                        // Items
                        output.accept(ModItems.HORN_GAS_MASK.get());
                        output.accept(ModItems.MOUMOUTON_HORN.get());
                        output.accept(ModItems.DURABLE_WOOL_FIBER.get());
                        output.accept(ModItems.MOUMOUTON_SPAWN_EGG.get());
                        output.accept(ModItems.DEER_SPAWN_EGG.get());
                        output.accept(ModItems.JAVELIN.get());

                        // Blocks
                        output.accept(ModBlocks.HARMONY_FLOWER.get());
                        output.accept(ModBlocks.ROSE.get());
                        output.accept(ModBlocks.LAVENDER.get());
                        output.accept(ModBlocks.CORNFLOWER.get());
                        output.accept(ModBlocks.DEAD_HARMONY_FLOWER.get());
                        output.accept(ModBlocks.DEAD_ROSE.get());
                        output.accept(ModBlocks.DEAD_LAVENDER.get());
                        output.accept(ModBlocks.DEAD_CORNFLOWER.get());
                        output.accept(ModBlocks.CLOVERS.get());
                        output.accept(ModBlocks.THICK_BUSH.get());
                        output.accept(ModBlocks.LITTLE_GRASS.get());
                        output.accept(ModBlocks.DEAD_WOOD_LOG.get());
                        output.accept(ModBlocks.DEAD_WOOD_PLANKS.get());
                        output.accept(ModBlocks.DEAD_WOOD_WOOD.get());
                        output.accept(ModBlocks.STRIPPED_DEAD_WOOD_WOOD.get());
                        output.accept(ModBlocks.STRIPPED_DEAD_WOOD_LOG.get());
                        output.accept(ModBlocks.DEAD_WOOD_DOOR.get());
                        output.accept(ModBlocks.DEAD_WOOD_SLAB.get());
                        output.accept(ModBlocks.DEAD_WOOD_STAIRS.get());

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
