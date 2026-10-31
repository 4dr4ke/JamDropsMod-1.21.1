package net.ssum.flains.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.ssum.flains.client.model.entity.GudannaModel;
import net.ssum.flains.common.entity.custom.GudannaEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GudannaRenderer extends GeoEntityRenderer<GudannaEntity> {

    public GudannaRenderer(EntityRendererProvider.Context context) {
        super(context, new GudannaModel());
        this.shadowRadius = 1f;
    }
}
