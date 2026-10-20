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

                        // ADD STUFF TO THE TAB HERE:
                        // Blocks
                        output.accept(ModBlocks.HARMONY_FLOWER.get());
                        output.accept(ModBlocks.ROSE.get());
                        output.accept(ModBlocks.LAVENDER.get());
                        output.accept(ModBlocks.CORNFLOWER.get());
                        output.accept(ModBlocks.CLOVERS.get());
                        output.accept(ModBlocks.THICK_BUSH.get());

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
