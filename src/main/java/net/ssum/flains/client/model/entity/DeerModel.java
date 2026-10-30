package net.ssum.flains.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ssum.flains.common.entity.custom.DeerEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DeerModel extends GeoModel<DeerEntity> {
    private final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("flains", "geo/deer.geo.json");
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("flains", "textures/entity/deer1.png");
    private final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("flains", "animations/deer.animation.json");

    @Override
    public void setCustomAnimations(DeerEntity animatable, long instanceId, AnimationState<DeerEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("head");
        if (head == null) return;

        EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
        head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
    }

    @Override
    public ResourceLocation getModelResource(DeerEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DeerEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(DeerEntity animatable) {
        return ANIMATION;
    }
}
