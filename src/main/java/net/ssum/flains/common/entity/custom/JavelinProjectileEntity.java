package net.ssum.flains.common.entity.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.item.ModItems;

public class JavelinProjectileEntity extends AbstractArrow {
    private float rotation = 360;
    public Vec2 groundedOffset;

    public JavelinProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public JavelinProjectileEntity(LivingEntity shooter, Level level) {
        super(ModEntities.JAVELIN_PROJECTILE.get(), shooter, level, new ItemStack(ModItems.JAVELIN.get()), null);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.JAVELIN.get());
    }

    public float getRenderingRotation(float partialTick) {
        Entity owner = this.getOwner();
        float xRot = owner != null ? owner.getViewXRot(partialTick) : 0;

        rotation += 0.008f;
        if (rotation >= 360) {
            rotation = -90 + xRot * 0.17f;
        }

        return rotation;
    }

    public boolean isGrounded() {
        return inGround;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 4);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
//
        if (result.getDirection() == Direction.SOUTH) groundedOffset = new Vec2(-90f, 180f);
        if (result.getDirection() == Direction.NORTH) groundedOffset = new Vec2(-90f, 0f);
        if (result.getDirection() == Direction.EAST) groundedOffset = new Vec2(90f, 90f);
        if (result.getDirection() == Direction.WEST) groundedOffset = new Vec2(-90f, 90f);
        if (result.getDirection() == Direction.DOWN) groundedOffset = new Vec2(180f, 180f);
        if (result.getDirection() == Direction.UP) groundedOffset = new Vec2(0f, -180f);

    }
}
