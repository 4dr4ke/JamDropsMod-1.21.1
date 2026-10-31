package net.ssum.flains.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ssum.flains.common.entity.custom.HedgeHogEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HedgeHogModel extends GeoModel<HedgeHogEntity> {
    private final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("flains", "geo/hedgehog.geo.json");
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("flains", "textures/entity/hedgehog.png");
    private final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("flains", "animations/hedgehog.animation.json");

    @Override
    public void setCustomAnimations(HedgeHogEntity animatable, long instanceId, AnimationState<HedgeHogEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("head");
        if (head == null) return;

        EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
        head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
    }

    @Override
    public ResourceLocation getModelResource(HedgeHogEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(HedgeHogEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HedgeHogEntity animatable) {
        return ANIMATION;
    }
}
