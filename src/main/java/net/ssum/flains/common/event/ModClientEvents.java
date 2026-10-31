package net.ssum.flains.common.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.ssum.flains.Flains;
import net.ssum.flains.client.ClientCameraSlow;
import net.ssum.flains.client.layer.StaminaBarLayer;
import net.ssum.flains.client.layer.ThornWrapLayer;
import net.ssum.flains.client.overlay.ParalysiaOverlay;
import net.ssum.flains.client.renderer.entity.*;
import net.ssum.flains.common.block.ModBlocks;
import net.ssum.flains.common.effect.ModEffects;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.entity.custom.DeerEntity;
import net.ssum.flains.common.entity.custom.GudannaEntity;
import net.ssum.flains.common.item.ModItems;
import net.ssum.flains.common.network.GudannaAttackPayload;
import net.ssum.flains.common.particle.ModParticles;
import net.ssum.flains.common.particle.advanced.ButterflyParticle;

@EventBusSubscriber(modid = Flains.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    // gui layers
    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "stamina_bar"),
                StaminaBarLayer::render
        );
    }

    @SubscribeEvent
    public static void onLayerPre(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.getVehicle() instanceof DeerEntity) {
                event.setCanceled(true);
            }
        }
    }

    // layers
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

    // particle providers
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.YELLOW_BUTTERFLY.get(), ButterflyParticle.Provider::new);
        event.registerSpriteSet(ModParticles.RED_BUTTERFLY.get(), ButterflyParticle.Provider::new);
        event.registerSpriteSet(ModParticles.BLUE_BUTTERFLY.get(), ButterflyParticle.Provider::new);

    }

    // entity renderers
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                ModEntities.MOUMOUTON.get(),
                MoumoutonRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.DEER.get(),
                DeerRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.GUDANNA.get(),
                GudannaRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.HEDGEHOG.get(),
                HedgeHogRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.BRAMBLESPIKE.get(),
                BramblespikeRenderer::new
        );
    }

    // foliage block colors
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null) {
                return BiomeColors.getAverageFoliageColor(level, pos);
            }
            return FoliageColor.getDefaultColor();
        }, ModBlocks.THICK_BUSH.get());

        event.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null) {
                return BiomeColors.getAverageFoliageColor(level, pos);
            }
            return FoliageColor.getDefaultColor();
        }, ModBlocks.LITTLE_GRASS.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> FoliageColor.getDefaultColor(), ModItems.THICK_BUSH.get());
        event.register((stack, tintIndex) -> FoliageColor.getDefaultColor(), ModItems.LITTLE_GRASS.get());
    }

    // paralysia effect
    @SubscribeEvent
    public static void onPlayerTickParalysia(PlayerTickEvent.Post event) {
        // unlock player's camera when the paralysia effect is gone
        if (!event.getEntity().hasEffect(ModEffects.PARALYSIA)) {
            if (ParalysiaOverlay.isVisible()) {
                ClientCameraSlow.setFactor(1);
                ParalysiaOverlay.setVisible(false);
            }
            return;
        }

        // lock player's camera when he has the paralysia effect
        if (!ParalysiaOverlay.isVisible()) {
            ClientCameraSlow.setFactor(0.01);
            ParalysiaOverlay.setVisible(true);
        }
    }

    @SubscribeEvent
    public static void onMovementInputParalysia(MovementInputUpdateEvent event) {
        if (!event.getEntity().hasEffect(ModEffects.PARALYSIA)) return;

        Input input = event.getInput();

        // prevent player from jumping
        input.jumping = false;

        // prevent player from moving
        input.forwardImpulse = 0.0F;
        input.leftImpulse = 0.0F;
        input.up = false;
        input.down = false;
        input.left = false;
        input.right = false;
    }

    // gudanna payload
    @SubscribeEvent
    public static void onAttackKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof GudannaEntity) {
            PacketDistributor.sendToServer(new GudannaAttackPayload());
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
