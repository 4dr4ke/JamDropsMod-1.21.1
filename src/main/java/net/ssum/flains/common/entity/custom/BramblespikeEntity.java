package net.ssum.flains.common.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BramblespikeEntity extends Mob implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_DELAY = SynchedEntityData.defineId(BramblespikeEntity.class, EntityDataSerializers.INT);

    private int lifeTicks = 40;
    private int delayBeforeTakingDamage = 5;
    private boolean emergeAnim = false;
    private static final Vec3 STUCK = new Vec3(0.1, 0.1, 0.1);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation EMERGING = RawAnimation.begin()
            .thenPlay("emerging").thenLoop("emerged");

    public BramblespikeEntity(EntityType<? extends BramblespikeEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = this.level();

        if (getDelayTicks() > 0) {
            if (!level.isClientSide) {
                setDelayTicks(getDelayTicks() - 1);
            }
            return;
        }

        if (!level.isClientSide) {
            if (!this.emergeAnim) {
                this.emergeAnim = true;
                this.triggerAnim("actions", "emerge");
                this.playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1.0f, 1.0f);
            }

            lifeTicks--;
            if (lifeTicks < 0) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            this.getX(), this.getY() + 1, this.getZ(),
                            15,
                            0.5, 0.5, 0.5,
                            0.025
                    );
                }

                this.discard();
                return;
            }
        }

        if (delayBeforeTakingDamage > 0) {
            delayBeforeTakingDamage--;
            return;
        }

        AABB hitbox = this.getBoundingBox().inflate(0.2, 0.2, 0.2);
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class, hitbox, e -> e != this
        );

        for (LivingEntity entity : targets) {
            entity.makeStuckInBlock(entity.getBlockStateOn(), STUCK);

            if (!level.isClientSide) {
                entity.hurt(this.damageSources().indirectMagic(this, this), 3);
            }
        }
    }

    // attributes

    public static AttributeSupplier.Builder createAttribute() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150d)
                .add(Attributes.MOVEMENT_SPEED, 0d)
                .add(Attributes.FOLLOW_RANGE, 50d);
    }

    // behavior

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.isCreativePlayer();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    // geckolib

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this, "actions", 0, state -> PlayState.STOP)
                .triggerableAnim("emerge", EMERGING)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // nbt & data

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DELAY, 10);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("delayTicks"))
            setDelayTicks(compound.getInt("delayTicks"));

        if (compound.contains("lifeTicks"))
            this.lifeTicks = compound.getInt("lifeTicks");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("delayTicks", getDelayTicks());
        compound.putInt("lifeTicks", this.lifeTicks);
    }

    // methods

    public void setDelayTicks(int delayTicks) {
        this.entityData.set(DATA_DELAY, delayTicks);
    }

    public int getDelayTicks() {
        return this.entityData.get(DATA_DELAY);
    }

    public void setDelayBeforeTakingDamage(int delayBeforeTakingDamage) {
        this.delayBeforeTakingDamage = delayBeforeTakingDamage;
    }

    public boolean isWaiting() {
        return this.getDelayTicks() > 0;
    }
}
