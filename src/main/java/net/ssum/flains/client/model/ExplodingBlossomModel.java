package net.ssum.flains.client.model;

import net.minecraft.resources.ResourceLocation;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.custom.BramblespikeEntity;
import net.ssum.flains.common.entity.custom.ExplodingBlossomEntity;
import software.bernie.geckolib.model.GeoModel;

public class ExplodingBlossomModel extends GeoModel<ExplodingBlossomEntity> {
    private final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "geo/exploding_blossom.geo.json");
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "textures/entity/exploding_blossom.png");
    private final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "animations/exploding_blossom.animation.json");

    @Override
    public ResourceLocation getModelResource(ExplodingBlossomEntity animatable) {
        return this.MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ExplodingBlossomEntity animatable) {
        return this.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ExplodingBlossomEntity animatable) {
        return this.ANIMATION;
    }
}
