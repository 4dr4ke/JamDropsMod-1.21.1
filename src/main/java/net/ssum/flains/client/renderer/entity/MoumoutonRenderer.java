package net.ssum.flains.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.ssum.flains.client.model.entity.MoumoutonModel;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MoumoutonRenderer extends GeoEntityRenderer<MoumoutonEntity> {

    public MoumoutonRenderer(EntityRendererProvider.Context context) {
        super(context, new MoumoutonModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, MoumoutonEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
