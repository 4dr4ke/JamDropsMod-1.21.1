package net.ssum.flains.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;
import software.bernie.geckolib.model.GeoModel;

public class MoumoutonModel extends GeoModel<MoumoutonEntity> {

    private final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "geo/moumouton.geo.json");
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "textures/entity/moumouton.png");
    private final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "animations/moumouton.animation.json");

    @Override
    public ResourceLocation getModelResource(MoumoutonEntity animatable) {
        return this.MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MoumoutonEntity animatable) {
        return this.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MoumoutonEntity animatable) {
        return this.ANIMATIONS;
    }
}
