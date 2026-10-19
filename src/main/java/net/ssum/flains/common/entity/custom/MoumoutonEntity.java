package net.ssum.flains.common.entity.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.entity.ai.MoumoutonChargeGoal;
import net.ssum.flains.common.entity.ai.MoumoutonScaredGoal;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MoumoutonEntity extends Animal implements GeoEntity {
    private static final EntityDataAccessor<Boolean> DATA_SCARED = SynchedEntityData.defineId(MoumoutonEntity.class, EntityDataSerializers.BOOLEAN);
    private final int SCARED_DURATION = 100; // 5s
    private final int SCARED_COOLDOWN = 10; // 0.5s

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int scaredTicks = 0;
    private int scaredCooldownTicks = 0;
    private int nextRandomIdleTime = 0;

    public MoumoutonEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SCARED, false);
    }

    // Animations
    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("idle");

    private static final RawAnimation RANDOM_IDLE_ANIM1 =
            RawAnimation.begin().thenPlay("random_idle_anim1");

    private static final RawAnimation RANDOM_IDLE_ANIM2 =
            RawAnimation.begin().thenPlay("random_idle_anim2");

    private static final RawAnimation WALK =
            RawAnimation.begin().thenLoop("walk");

    private static final RawAnimation SCARED = RawAnimation.begin()
            .thenPlay("scared")
            .thenLoop("scared_idle");

    private static final RawAnimation NO_LONGER_SCARED = RawAnimation.begin()
            .thenPlay("no_longer_scared");

    private static final RawAnimation DEATH =
            RawAnimation.begin().thenPlay("death");

    private static final RawAnimation HURT =
            RawAnimation.begin().thenPlay("hurt");

    private static final RawAnimation HIT =
            RawAnimation.begin().thenPlay("hit");

    private static final RawAnimation ATTACK =
            RawAnimation.begin().thenPlay("dash");

    // Attributes

    public static AttributeSupplier.Builder createAttribute() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14d)
                .add(Attributes.MOVEMENT_SPEED, 0.12d)
                .add(Attributes.FOLLOW_RANGE, 50d)
                .add(Attributes.ATTACK_DAMAGE, 3d);
    }

    // AI

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MoumoutonScaredGoal(this));
        this.goalSelector.addGoal(2, new MoumoutonChargeGoal(this, 4, 10));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 2, true));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.2));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.2, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.2));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.2));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        nextRandomIdleTime--;
        if (nextRandomIdleTime <= 0 && !this.level().isClientSide) {
            nextRandomIdleTime = random.nextInt(300, 450);

            boolean notGood = this.isScared() || this.getTarget() != null || this.isAggressive() || !this.getNavigation().isDone();

            if (!notGood) {
                int i = random.nextInt(1, 3); // 1 or 2
                String animation = "random_idle_anim" + i;
                triggerAnim("actions", animation);
            }
        }

        if (this.scaredCooldownTicks > 0)
            this.scaredCooldownTicks--;

        this.scaredTicks--;
        if (this.scaredTicks <= 0) {
            this.setScared(false);
            this.scaredCooldownTicks = this.SCARED_COOLDOWN;
        }
    }

    // GeckoLib

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, state -> {
            if (this.isScared()) {
                return state.setAndContinue(SCARED);
            }
            if (state.isMoving()) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(
                this, "actions", 0, state -> PlayState.STOP)
                .triggerableAnim("hurt", HURT)
                .triggerableAnim("random_idle_anim1", RANDOM_IDLE_ANIM1)
                .triggerableAnim("random_idle_anim2", RANDOM_IDLE_ANIM2)
                .triggerableAnim("attack", ATTACK)
                .triggerableAnim("hit", HIT)
                .triggerableAnim("death", DEATH)
                .triggerableAnim("scared", SCARED)
                .triggerableAnim("no_longer_scared", NO_LONGER_SCARED));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // Behavior

    public boolean isScared() {
        return this.entityData.get(DATA_SCARED);
    }

    private void setScared(boolean scared) {
        Level level = this.level();

        if (!level.isClientSide && this.isScared() && !scared) {
            this.triggerAnim("actions", "no_longer_scared");
            this.playSound(SoundEvents.SHULKER_OPEN, 0.8f, 1.0f - this.random.nextFloat() * 0.3f);
            this.playSound(SoundEvents.ARMADILLO_ROLL, 0.9f, 0.7f - this.random.nextFloat() * 0.2f);
        }
        this.entityData.set(DATA_SCARED, scared);
        this.scaredTicks = SCARED_DURATION;
    }

    public void tryScare() {
        if (this.getTarget() != null || this.isAggressive()) {
            return;
        }

        if (this.scaredCooldownTicks >= 1)
            return;

        this.scaredTicks = SCARED_DURATION;
        this.setScared(true);

        this.playSound(SoundEvents.ARMADILLO_ROLL, 0.9f, 0.7f - this.random.nextFloat() * 0.2f);
        this.playSound(SoundEvents.SHULKER_CLOSE, 0.8f, 1.0f - this.random.nextFloat() * 0.3f);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide) {
            this.triggerAnim("actions", "hit");
        }
        return super.doHurtTarget(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);

        if (damaged && !this.level().isClientSide) {
            this.triggerAnim("actions", "hurt");
        }

        if (this.isScared())
            this.setScared(false);

        return damaged;
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide) {
            this.triggerAnim("actions", "death");
        }
        super.die(damageSource);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.WHEAT);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.MOUMOUTON.get().create(serverLevel);
    }

    // Sounds

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }
}