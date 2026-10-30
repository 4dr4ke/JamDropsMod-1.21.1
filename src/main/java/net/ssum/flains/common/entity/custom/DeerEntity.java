package net.ssum.flains.common.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ssum.flains.client.FlainsClientHooks;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.entity.ai.DeerScaredGoal;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DeerEntity extends TamableAnimal implements GeoEntity {
    public static final float MAX_STAMINA = 100f;
    private static final EntityDataAccessor<Float> STAMINA = SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> SCARED = SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.BOOLEAN);;
    private static final EntityDataAccessor<Boolean> EXHAUSTED = SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.BOOLEAN);;

    private float staminaRegenDelay = 0;

    public DeerEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    // geckolib
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SIT = RawAnimation.begin().thenPlay("sit").thenLoop("sit_idle");
    private static final RawAnimation STAND_UP = RawAnimation.begin().thenPlay("stand_up");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlay("death");
    private static final RawAnimation RANDOM_IDLE_ANIM1 = RawAnimation.begin().thenPlay("random_idle1");
    private static final RawAnimation RANDOM_IDLE_ANIM2 = RawAnimation.begin().thenPlay("random_idle2");
    private static final RawAnimation RANDOM_IDLE_ANIM3 = RawAnimation.begin().thenPlay("random_idle3");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(this.entityData.get(RUNNING) ? RUN : WALK);
            }
            return state.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(
                this, "actions", 0, state -> PlayState.STOP)
                .triggerableAnim("random_idle1", RANDOM_IDLE_ANIM1)
                .triggerableAnim("random_idle2", RANDOM_IDLE_ANIM2)
                .triggerableAnim("random_idle3", RANDOM_IDLE_ANIM3)
                .triggerableAnim("death", DEATH)
                .triggerableAnim("sit", SIT)
                .triggerableAnim("stand_up", STAND_UP)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // AI

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new DeerScaredGoal(this, 10, 3.8d));
        this.goalSelector.addGoal(3, new PanicGoal(this, 2d));
        this.goalSelector.addGoal(4, new BreedGoal(this, 3));
        this.goalSelector.addGoal(5, new TemptGoal(this, 2.5, Ingredient.of(Items.SHORT_GRASS), false));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 2));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 2));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    // attributes

    public static AttributeSupplier.Builder createAttribute() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10d)
                .add(Attributes.MOVEMENT_SPEED, 0.12d)
                .add(Attributes.JUMP_STRENGTH, 0.42)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.65)
                .add(Attributes.FOLLOW_RANGE, 40d);
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDefaultDimensions(pose);
        return this.isBaby() ? dimensions.scale(0.5f) : dimensions;
    }

    // behavior

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!this.isTame() && this.isFood(stack) && !level().isClientSide) {
            if (!this.level().isClientSide) {
                stack.consume(1, player);
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.isOwnedBy(player)) {
            if (this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
                if (!this.level().isClientSide) {
                    this.heal(4.0f);
                    stack.consume(1, player);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

            if (player.isSecondaryUseActive()) {
                if (!this.level().isClientSide) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    String animName = this.isOrderedToSit() ? "sit" : "stand_up";
                    this.triggerAnim("actions", animName);
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

            if (stack.isEmpty() && !this.isBaby()) {
                if (!this.level().isClientSide) {
                    this.setOrderedToSit(false);
                    player.startRiding(this);
                    this.triggerAnim("actions", "stand_up");
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty() && passenger instanceof Player player && this.isOwnedBy(player);
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float strafe = player.xxa * 0.8f;
        float forward = player.zza;
        if (forward <= 0.0f) forward *= 0.7f;
        return new Vec3(strafe, 0.0, forward);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        float base = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.2F;
        if (isBoosting()) base *= 1.3F;
        if (this.isInWater()) base *= 1.15F;
        return base;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        if (this.level().isClientSide && this.onGround() && FlainsClientHooks.isJumpPressed()) {
            this.jumpFromGround();
        }

        if (this.level().isClientSide && this.isInWater() && FlainsClientHooks.isJumpPressed()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.04, 0.0));
        }
    }
    public boolean isExhausted() {
        return this.entityData.get(EXHAUSTED);
    }

    public boolean isBoosting() {
        return this.getControllingPassenger() instanceof Player p && p.isSprinting() && !isExhausted() && getStamina() > 0;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) return;

        boolean movingForward = this.getControllingPassenger() instanceof Player p && p.zza > 0;

        if (!movingForward && this.entityData.get(RUNNING)) {
            setRunning(false);
        } else {
            setRunning(true);
        }

        if (isBoosting() && movingForward) {
            setStamina(getStamina() - 0.5f);
            staminaRegenDelay += 0.05f;
            if (getStamina() <= 0) {
                this.entityData.set(EXHAUSTED, true);
            }
        } else {
            if (staminaRegenDelay > 0) {
                staminaRegenDelay = Math.max(0, staminaRegenDelay - 0.2f);
            } else {
                setStamina(getStamina() + 0.3f);
            }

            if (isExhausted() && getStamina() >= 20.0f) {
                this.entityData.set(EXHAUSTED, false);
            }
        }

        if (this.isInWater() && this.getControllingPassenger() != null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.02, 0.0));
        }
    }

    public void setScared(boolean scared) {
        this.entityData.set(SCARED, scared);
    }

    public boolean isScared() {
        return this.entityData.get(SCARED);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.SHORT_GRASS);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.DEER.get().create(level());
    }

    public void setRunning(boolean running) {
        this.entityData.set(RUNNING, running);
    }

    public float getStamina() {
        return this.entityData.get(STAMINA);
    }

    public void setStamina(float stamina) {
        this.entityData.set(STAMINA, Mth.clamp(stamina, 0f, MAX_STAMINA));
    }

    public float getStaminaRatio() {
        return getStamina() / MAX_STAMINA;
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.HORSE_STEP, 0.15F, 1.0F);
    }

    // data
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SCARED, false);
        builder.define(STAMINA, MAX_STAMINA);
        builder.define(EXHAUSTED, false);
        builder.define(RUNNING, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("scared")) this.setScared(compound.contains("scared"));
        if (compound.contains("stamina")) setStamina(compound.getFloat("stamina"));
        if (compound.contains("exhausted")) entityData.set(EXHAUSTED, compound.getBoolean("exhausted"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("scared", this.isScared());
        compound.putFloat("stamina", getStamina());
        compound.putBoolean("exhausted", entityData.get(EXHAUSTED));
    }
}
