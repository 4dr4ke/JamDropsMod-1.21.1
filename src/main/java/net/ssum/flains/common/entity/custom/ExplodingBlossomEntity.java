package net.ssum.flains.common.entity.custom;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.ssum.flains.FlainsUtils;
import net.ssum.flains.common.effect.ModEffects;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class ExplodingBlossomEntity extends Mob implements GeoEntity {
    private enum State {
        BEATING,
        EXPLODING,
        CLOUD
    }

    private static final int EXPLOSION_ANIM_TICKS = 10; // explode animation duration
    private static final double CLOUD_RADIUS = 7;

    private State state = State.BEATING;
    private int beatingTicks = 80;      // 4s
    private int explosionTicks = 0;
    private int cloudLifeTicks = 140;   // 7s
    private boolean spawned = false;
    private Holder<MobEffect> effect;

    // geckolib animations

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("deploy").thenLoop("beating");

    private static final RawAnimation EXPLOSION = RawAnimation.begin().thenPlay("explode");

    // constructor & behavior

    public ExplodingBlossomEntity(EntityType<? extends ExplodingBlossomEntity> entityType, Level level) {
        super(entityType, level);
        this.setInvisible(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (!(this.level() instanceof ServerLevel serverLevel))
            return;

        switch (this.state) {
            case BEATING -> tickBeating();
            case EXPLODING -> tickExploding(serverLevel);
            case CLOUD -> tickCloud(serverLevel);
        }
    }

    private void tickBeating() {
        if (!this.spawned) {
            if (this.tickCount < 2) return;

            this.spawned = true;
            this.setInvisible(false);
            this.triggerAnim("actions", "spawn");
            this.playSound(SoundEvents.SPORE_BLOSSOM_PLACE, 1.0f, 1.0f);
        }

        if (--this.beatingTicks <= 0) {
            this.state = State.EXPLODING;
            this.triggerAnim("actions", "explode");
            this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, 1.2f, 0.7f);
        }

        float progress = 1f - (this.beatingTicks / 80f);
        if (this.beatingTicks % 16 == 0) {
            float pitch = 0.8f + progress * 0.7f;
            this.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0f, pitch);
        }
    }

    private void tickExploding(ServerLevel level) {
        if (this.explosionTicks == 0) {
            this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, 1.2f, 0.6f);
        }

        if (++this.explosionTicks >= EXPLOSION_ANIM_TICKS) {
            level.sendParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.getX(), this.getY() + 1, this.getZ(),
                    15,
                    0.5, 0.5, 0.5,
                    0.025
            );
            this.setInvisible(true);
            this.state = State.CLOUD;
            this.effect = random.nextFloat() > 0.8 ? ModEffects.PARALYSIA : MobEffects.POISON;
        }
    }

    private void tickCloud(ServerLevel level) {
        if (--this.cloudLifeTicks < 0) {
            this.discard();
            return;
        }

        if (this.cloudLifeTicks % 4 != 0)
            return;

        SimpleParticleType particleType;

        if (this.effect == MobEffects.POISON) {
            particleType = ParticleTypes.SPORE_BLOSSOM_AIR;

            if (this.cloudLifeTicks % 20 == 0) {
                this.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, 0.45f, 0.7f);
            }
        } else {
            particleType = ParticleTypes.DRAGON_BREATH;
            this.cloudLifeTicks = (int)(this.cloudLifeTicks * 0.6);
        }

        level.sendParticles(
                particleType,
                this.getX(), this.getY() + 1, this.getZ(),
                15,
                3.0, 1.0, 3.0,
                0
        );

        AABB hitbox = this.getBoundingBox().inflate(CLOUD_RADIUS);
        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                hitbox,
                entity -> entity != this &&
                        entity.distanceToSqr(this) <= CLOUD_RADIUS * CLOUD_RADIUS &&
                        !FlainsUtils.isBoxedIn(entity)
        );

        for (LivingEntity entity : entities) {
            entity.addEffect(new MobEffectInstance(this.effect, 100, 0, true, true));
        }
    }

    // attributes

    public static AttributeSupplier.Builder createAttribute() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0d)
                .add(Attributes.FOLLOW_RANGE, 50d);
    }

    // behavior
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
                .triggerableAnim("spawn", SPAWN)
                .triggerableAnim("explode", EXPLOSION)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // nbt

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("phase", this.state.ordinal());
        compound.putInt("beatingTicks", this.beatingTicks);
        compound.putInt("explosionTicks", this.explosionTicks);
        compound.putInt("cloudLifeTicks", this.cloudLifeTicks);
        compound.putBoolean("spawned", this.spawned);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("phase")) {
            int i = Mth.clamp(compound.getInt("phase"), 0, State.values().length - 1);
            this.state = State.values()[i];
        }
        if (compound.contains("beatingTicks")) this.beatingTicks = compound.getInt("beatingTicks");
        if (compound.contains("explosionTicks")) this.explosionTicks = compound.getInt("explosionTicks");
        if (compound.contains("cloudLifeTicks")) this.cloudLifeTicks = compound.getInt("cloudLifeTicks");
        if (compound.contains("spawned")) this.spawned = compound.getBoolean("spawned");
    }
}
