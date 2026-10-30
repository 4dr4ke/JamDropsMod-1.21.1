package net.ssum.flains.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.ssum.flains.client.model.entity.BramblerModel;
import net.ssum.flains.common.entity.custom.BramblerEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BramblerRenderer extends GeoEntityRenderer<BramblerEntity> {

    public BramblerRenderer(EntityRendererProvider.Context context) {
        super(context, new BramblerModel());
        this.shadowRadius = 0.5f;
    }
}
