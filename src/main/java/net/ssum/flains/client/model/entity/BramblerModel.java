package net.ssum.flains.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ssum.flains.common.entity.custom.BramblerEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BramblerModel extends GeoModel<BramblerEntity> {
    private final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("flains", "geo/brambler.geo.json");
    private final ResourceLocation TEXTURE1 = ResourceLocation.fromNamespaceAndPath("flains", "textures/entity/brambler.png");
    private final ResourceLocation TEXTURE2 = ResourceLocation.fromNamespaceAndPath("flains", "textures/entity/brambler_arm_crossed.png");
    private final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("flains", "animations/brambler.animation.json");

    @Override
    public void setCustomAnimations(BramblerEntity animatable, long instanceId, AnimationState<BramblerEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("head");
        if (head == null) return;

        EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
        head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
    }

    @Override
    public ResourceLocation getModelResource(BramblerEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BramblerEntity animatable) {
        return animatable.isAttacking() ? TEXTURE1 : TEXTURE2;
    }

    @Override
    public ResourceLocation getAnimationResource(BramblerEntity animatable) {
        return ANIMATION;
    }
}
