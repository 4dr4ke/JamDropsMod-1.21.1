package net.ssum.flains.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.ssum.flains.client.model.entity.MoumoutonModel;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MoumoutonRenderer extends GeoEntityRenderer<MoumoutonEntity> {

    public MoumoutonRenderer(EntityRendererProvider.Context context) {
        super(context, new MoumoutonModel());
        this.shadowRadius = 0.5f;
    }


}
