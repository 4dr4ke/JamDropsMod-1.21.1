package net.ssum.flains.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.ssum.flains.client.model.BramblespikeModel;
import net.ssum.flains.client.model.ExplodingBlossomModel;
import net.ssum.flains.common.entity.custom.BramblespikeEntity;
import net.ssum.flains.common.entity.custom.ExplodingBlossomEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ExplodingBlossomRenderer extends GeoEntityRenderer<ExplodingBlossomEntity> {

    public ExplodingBlossomRenderer(EntityRendererProvider.Context context) {
        super(context, new ExplodingBlossomModel());
        this.shadowRadius = 0f;
    }
}
