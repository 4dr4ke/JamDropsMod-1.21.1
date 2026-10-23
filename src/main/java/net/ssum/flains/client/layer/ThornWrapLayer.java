package net.ssum.flains.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.ssum.flains.common.effect.ModEffects;

public class ThornWrapLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation TEXTURE = ResourceLocation
            .fromNamespaceAndPath("flains", "textures/entity/thorn_wrap.png");

    public static final ModelLayerLocation THORN_LAYER = new ModelLayerLocation(ResourceLocation
            .fromNamespaceAndPath("flains", "thorn_wrap"), "main");


    private final HumanoidModel<AbstractClientPlayer> model;

    public ThornWrapLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new HumanoidModel<>(modelSet.bakeLayer(THORN_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!player.hasEffect(ModEffects.THORN_PROTECTION)) return;
        if (player.hurtTime <= 0) return;

        this.getParentModel().copyPropertiesTo(this.model);
        this.model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        float progress = Mth.clamp((player.hurtTime - partialTick) / (float) player.hurtDuration, 0f, 1f);
        int alpha = (int) (progress * 255);
        int color = FastColor.ARGB32.color(alpha, 255, 255, 255);

        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        this.model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, color);

        //this.model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, -1);
    }
}
