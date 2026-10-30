package net.ssum.flains.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ssum.flains.Flains;
import net.ssum.flains.client.model.entity.JavelinProjectileModel;
import net.ssum.flains.common.entity.custom.JavelinProjectileEntity;

public class JavelinProjectileRenderer extends EntityRenderer<JavelinProjectileEntity> {
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "textures/entity/javelin_projectile.png");
    private JavelinProjectileModel model;

    public JavelinProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new JavelinProjectileModel(context.bakeLayer(JavelinProjectileModel.LAYER_LOCATION));
    }

    @Override
    public void render(JavelinProjectileEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        if (!p_entity.isGrounded()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, p_entity.yRotO, p_entity.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(p_entity.getRenderingRotation(partialTick) * 5f));
            poseStack.translate(0, -1.0f, 0);
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(p_entity.groundedOffset.y));
            poseStack.mulPose(Axis.XP.rotationDegrees(p_entity.groundedOffset.x));
            poseStack.translate(0, -1.0f, 0);
        }

        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(
                bufferSource, this.model.renderType(this.getTextureLocation(p_entity)),false, false);
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(JavelinProjectileEntity javelinProjectileEntity) {
        return this.TEXTURE;
    }
}
