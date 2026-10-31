package net.ssum.flains.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ssum.flains.common.entity.custom.DeerEntity;
import net.ssum.flains.common.entity.custom.GudannaEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GudannaModel extends GeoModel<GudannaEntity> {
    private final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("flains", "geo/gudanna.geo.json");
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("flains", "textures/entity/gudanna.png");
    private final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("flains", "animations/gudanna.animation.json");

//    @Override
//    public void setCustomAnimations(GudannaEntity animatable, long instanceId, AnimationState<GudannaEntity> animationState) {
//        GeoBone head = this.getAnimationProcessor().getBone("head");
//        GeoBone jaw = this.getAnimationProcessor().getBone("jaw");
//        if (jaw == null) return;
//
//        EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
//        head.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
//        head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
//        jaw.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
//        jaw.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
//    }

    @Override
    public ResourceLocation getModelResource(GudannaEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GudannaEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GudannaEntity animatable) {
        return ANIMATION;
    }
}
