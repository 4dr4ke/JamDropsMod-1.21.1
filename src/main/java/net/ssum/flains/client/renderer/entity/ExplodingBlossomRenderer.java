package net.ssum.flains.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.ssum.flains.client.model.entity.ExplodingBlossomModel;
import net.ssum.flains.common.entity.custom.ExplodingBlossomEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ExplodingBlossomRenderer extends GeoEntityRenderer<ExplodingBlossomEntity> {

    public ExplodingBlossomRenderer(EntityRendererProvider.Context context) {
        super(context, new ExplodingBlossomModel());
        this.shadowRadius = 0f;
    }
}
