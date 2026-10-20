package net.ssum.flains.common.event;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.client.renderer.MoumoutonRenderer;
import net.ssum.flains.common.block.ModBlocks;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.item.ModItems;

@EventBusSubscriber(modid = Flains.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                ModEntities.MOUMOUTON.get(),
                MoumoutonRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null) {
                return BiomeColors.getAverageFoliageColor(level, pos);
            }
            return FoliageColor.getDefaultColor();
        }, ModBlocks.THICK_BUSH.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> FoliageColor.getDefaultColor(), ModItems.THICK_BUSH.get());
    }
}
