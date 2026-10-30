package net.ssum.flains.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.custom.BramblespikeEntity;
import software.bernie.geckolib.model.GeoModel;

public class BramblespikeModel extends GeoModel<BramblespikeEntity> {
    private final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "geo/bramblespike.geo.json");
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "textures/entity/bramblespike.png");
    private final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "animations/bramblespike.animation.json");

    @Override
    public ResourceLocation getModelResource(BramblespikeEntity animatable) {
        return this.MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BramblespikeEntity animatable) {
        return this.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BramblespikeEntity animatable) {
        return this.ANIMATION;
    }
}
