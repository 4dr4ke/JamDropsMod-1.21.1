package net.ssum.flains.client.model.item;

import net.minecraft.resources.ResourceLocation;
import net.ssum.flains.Flains;
import net.ssum.flains.common.item.custom.JavelinItem;
import software.bernie.geckolib.model.GeoModel;

public class JavelinItemModel extends GeoModel<JavelinItem> {
    private final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "geo/javelin.geo.json");
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "textures/item/javelin.png");
    private final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "animations/javelin.animation.json");


    @Override
    public ResourceLocation getModelResource(JavelinItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(JavelinItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(JavelinItem animatable) {
        return ANIMATION;
    }
}
