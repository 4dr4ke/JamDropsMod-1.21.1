package net.ssum.flains.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MoumoutonModel extends GeoModel<MoumoutonEntity> {

    private final ResourceLocation ADULT_MODEL = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "geo/moumouton.geo.json");
    private final ResourceLocation ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "textures/entity/moumouton.png");
    private final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "animations/moumouton.animation.json");

    private final ResourceLocation SHEARED_ADULT_MODEL = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "geo/moumouton_sheared.geo.json");
    private final ResourceLocation SHEARED_ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "textures/entity/moumouton_sheared.png");

    private final ResourceLocation BABY_MODEL = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "geo/moumouton_baby.geo.json");
    private final ResourceLocation BABY_TEXTURE = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "textures/entity/moumouton_baby.png");

    @Override
    public void setCustomAnimations(MoumoutonEntity animatable, long instanceId, AnimationState<MoumoutonEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("head");
        if (head == null) return;

        EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(head.getRotX() + data.headPitch() * Mth.DEG_TO_RAD);
        head.setRotY(head.getRotY() + data.netHeadYaw() * Mth.DEG_TO_RAD);
    }

    @Override
    public ResourceLocation getModelResource(MoumoutonEntity animatable) {
        if (animatable.isBaby())
            return this.BABY_MODEL;
        else {
            if (animatable.isSheared())
                return this.SHEARED_ADULT_MODEL;
            else
                return this.ADULT_MODEL;
        }

        //return animatable.isBaby() ? this.BABY_MODEL : this.ADULT_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MoumoutonEntity animatable) {
        if (animatable.isBaby())
            return this.BABY_TEXTURE;
        else {
            if (animatable.isSheared())
                return this.SHEARED_ADULT_TEXTURE;
            else
                return this.ADULT_TEXTURE;
        }

        //return animatable.isBaby() ? this.BABY_TEXTURE : this.ADULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MoumoutonEntity animatable) {
        return this.ANIMATIONS;
    }
}
