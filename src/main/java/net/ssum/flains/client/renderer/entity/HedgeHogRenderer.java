package net.ssum.flains.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.ssum.flains.client.model.entity.DeerModel;
import net.ssum.flains.client.model.entity.HedgeHogModel;
import net.ssum.flains.common.entity.custom.DeerEntity;
import net.ssum.flains.common.entity.custom.HedgeHogEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HedgeHogRenderer extends GeoEntityRenderer<HedgeHogEntity> {

    public HedgeHogRenderer(EntityRendererProvider.Context context) {
        super(context, new HedgeHogModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, HedgeHogEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
