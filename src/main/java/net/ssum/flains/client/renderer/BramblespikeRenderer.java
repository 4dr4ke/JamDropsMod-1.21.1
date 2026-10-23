package net.ssum.flains.client.renderer;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.ssum.flains.client.model.BramblespikeModel;
import net.ssum.flains.common.entity.custom.BramblespikeEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BramblespikeRenderer extends GeoEntityRenderer<BramblespikeEntity> {

    public BramblespikeRenderer(EntityRendererProvider.Context context) {
        super(context, new BramblespikeModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public boolean shouldRender(BramblespikeEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        if (livingEntity.isWaiting())
            return false;
        return super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }
}
