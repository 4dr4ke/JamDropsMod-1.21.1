package net.ssum.flains.common.event;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.client.layer.ThornWrapLayer;
import net.ssum.flains.client.renderer.BramblespikeRenderer;
import net.ssum.flains.client.renderer.ExplodingBlossomRenderer;
import net.ssum.flains.client.renderer.MoumoutonRenderer;
import net.ssum.flains.common.block.ModBlocks;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.item.ModItems;

@EventBusSubscriber(modid = Flains.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    public static final ModelLayerLocation THORN_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("flains", "thorn_wrap"), "main");

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(THORN_LAYER,
                () -> LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.5f), 0f), 64, 32)
        );
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new ThornWrapLayer(renderer, event.getEntityModels()));
            }
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                ModEntities.MOUMOUTON.get(),
                MoumoutonRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.BRAMBLESPIKE.get(),
                BramblespikeRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.EXPLODING_BLOSSOM.get(),
                ExplodingBlossomRenderer::new
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
