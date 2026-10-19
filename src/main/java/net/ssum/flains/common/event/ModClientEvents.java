package net.ssum.flains.common.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.client.renderer.entity.MoumoutonRenderer;
import net.ssum.flains.common.entity.ModEntities;

@EventBusSubscriber(modid = Flains.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                ModEntities.MOUMOUTON.get(),
                MoumoutonRenderer::new
        );
    }
}
