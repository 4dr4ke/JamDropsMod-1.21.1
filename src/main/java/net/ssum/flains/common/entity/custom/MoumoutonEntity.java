package net.ssum.flains.common.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.IShearable;
import net.ssum.flains.FlainsUtils;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.entity.ai.MoumoutonChargeGoal;
import net.ssum.flains.common.entity.ai.MoumoutonScaredGoal;
import net.ssum.flains.common.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MoumoutonEntity extends Animal implements GeoEntity, IShearable {

    private static final EntityDataAccessor<Boolean> DATA_SCARED = SynchedEntityData.defineId(MoumoutonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHEARED = SynchedEntityData.defineId(MoumoutonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> WALKING = SynchedEntityData.defineId(MoumoutonEntity.class, EntityDataSerializers.BOOLEAN);

    private final int SCARED_COOLDOWN = 10; // 0.5s
    private final int SHEARED_REGROW_TICKS = 2400; // 2 minutes
    private static final int WALK_CYCLE_TICKS = 14;
    private static final float WALK_START = 0.2f;
    private static final float WALK_END = 0.8f;

    // Animations geckolib

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation RANDOM_IDLE_ANIM1 = RawAnimation.begin().thenPlay("random_idle_anim1");
    private static final RawAnimation RANDOM_IDLE_ANIM2 = RawAnimation.begin().thenPlay("random_idle_anim2");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenPlayXTimes("run", 3);

    private static final RawAnimation SCARED = RawAnimation.begin()
            .thenPlay("scared")
            .thenLoop("scared_idle");

    private static final RawAnimation NO_LONGER_SCARED = RawAnimation.begin().thenPlay("no_longer_scared");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlay("death");
    private static final RawAnimation HURT = RawAnimation.begin().thenPlay("hurt");
    private static final RawAnimation HIT = RawAnimation.begin().thenPlay("hit");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("dash");

    // variables, constructor, attributes

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private UUID targetUUID;

    private int walkTicks = 0;
    private int scaredTicks = 0;
    private int scaredCooldownTicks = 0;
    private int nextRandomIdleTime = 0;
    private int shearedTicks = 0;

    public MoumoutonEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttribute() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14d)
                .add(Attributes.MOVEMENT_SPEED, 0.1d)
                .add(Attributes.FOLLOW_RANGE, 50d)
                .add(Attributes.STEP_HEIGHT, 1.0f)
                .add(Attributes.ATTACK_DAMAGE, 3d);
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDefaultDimensions(pose);
        return this.isBaby() ? dimensions.scale(0.5f) : dimensions;
    }

    // NBT

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SCARED, false);
        builder.define(SHEARED, false);
        builder.define(WALKING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putBoolean("sheared", this.isSheared());
        compound.putInt("randomIdle", this.nextRandomIdleTime);

        if (this.getTarget() != null) {
            compound.putUUID("target", this.getTarget().getUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("sheared"))
            this.setSheared(compound.getBoolean("sheared"));

        if (compound.contains("randomIdle"))
            this.nextRandomIdleTime = compound.getInt("randomIdle");

        if (compound.hasUUID("target"))
            this.targetUUID = compound.getUUID("target");
    }

    @Override
    public void tick() {
        super.tick();

        // remembers its target
        Level level = this.level();
        if (!level.isClientSide && this.targetUUID != null && this.getTarget() == null) {
            if (level instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(this.targetUUID);
                if (entity instanceof LivingEntity livingEntity) {
                    this.setTarget(livingEntity);
                }
            }
            this.targetUUID = null;
        }
    }

    // AI

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MoumoutonScaredGoal(this, 4));
        this.goalSelector.addGoal(2, new MoumoutonChargeGoal(this, 4.5, 10));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 3.5, true));
        this.goalSelector.addGoal(4, new BreedGoal(this, 3));
        this.goalSelector.addGoal(5, new TemptGoal(this, 2.5, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 2));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 2));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        this.tickRandomIdle();
        this.tickScared();
        this.tickSheared();
        this.tickWalkPulse();
    }

    private void tickRandomIdle() {
        this.nextRandomIdleTime--;
        if (this.nextRandomIdleTime > 0)
            return;

        this.nextRandomIdleTime = this.random.nextInt(300, 450);

        boolean notGood = this.isScared() || this.getTarget() != null || this.isAggressive() || !this.getNavigation().isDone();
        if (notGood)
            return;

        int i = this.random.nextInt(1, 3); // 1 or 2
        if (i == 2) {
            // eat grass animation
            this.setSheared(false);
        }
        this.triggerAnim("actions", "random_idle_anim" + i);
    }

    private void tickScared() {
        if (this.scaredCooldownTicks > 0)
            this.scaredCooldownTicks--;

        if (this.isScared()) {
            this.scaredTicks--;
            if (this.scaredTicks <= 0) {
                this.setScared(false);
                this.scaredCooldownTicks = this.SCARED_COOLDOWN;
            }
        }
    }

    private void tickSheared() {
        if (!this.isSheared())
            return;

        if (this.shearedTicks < SHEARED_REGROW_TICKS)
            this.shearedTicks++;
        else
            this.setSheared(false);
    }

    private void tickWalkPulse() {
        boolean walking = isPulseWalking();
        this.entityData.set(WALKING, walking);
        this.walkTicks = walking ? this.walkTicks + 1 : 0;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (!this.level().isClientSide && isPulseWalking()) {
            float phase = (this.walkTicks % WALK_CYCLE_TICKS) / (float) WALK_CYCLE_TICKS;
            float factor = (phase >= WALK_START && phase < WALK_END) ? 1f : 0f;
            travelVector = new Vec3(travelVector.x * factor, travelVector.y, travelVector.z * factor);
        }
        super.travel(travelVector);
    }

    // GeckoLib

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, state -> {
            if (this.isScared() && !this.isSheared()) {
                return state.setAndContinue(SCARED);
            }
            if (state.isMoving() || this.entityData.get(WALKING)) {
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
                .triggerableAnim("run", RUN)
                .triggerableAnim("no_longer_scared", NO_LONGER_SCARED)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // Behavior

    @Override
    public boolean isShearable(@Nullable Player player, ItemStack item, Level level, BlockPos pos) {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level level, BlockPos pos) {
        level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);

        if (!level.isClientSide) {
            this.setSheared(true);

            if (this.isScared())
                this.setScared(false);
        }

        List<ItemStack> toDrop = new ArrayList<>();
        RandomSource random = level.getRandom();

        if (random.nextFloat() > 0.65f) { // 35% chance
             toDrop.add(new ItemStack(ModItems.DURABLE_WOOL_FIBER.get()));
        }
        toDrop.add(
                new ItemStack(Items.WHITE_WOOL, random.nextInt(3) + 1)
        );

        return toDrop;
    }

    public boolean isSheared() {
        return this.entityData.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.shearedTicks = 0;
        this.entityData.set(SHEARED, sheared);
    }




    public boolean isScared() {
        return this.entityData.get(DATA_SCARED);
    }

    private void setScared(boolean scared, int durationInTicks) {
        Level level = this.level();

        if (!level.isClientSide && this.isScared() && !this.isSheared() && !scared) {
            this.triggerAnim("actions", "no_longer_scared");
            this.playSound(SoundEvents.SHULKER_OPEN, 0.8f, 1.0f - this.random.nextFloat() * 0.3f);
            this.playSound(SoundEvents.ARMADILLO_ROLL, 0.9f, 0.7f - this.random.nextFloat() * 0.2f);
        }

        this.scaredTicks = durationInTicks;
        this.entityData.set(DATA_SCARED, scared);
    }

    private void setScared(boolean scared) {
        this.setScared(scared, 100);
    }

    public void tryScare(int durationInTicks) {
        if (this.getTarget() != null || this.isAggressive()) {
            return;
        }

        if (this.scaredCooldownTicks >= 1)
            return;

        this.setScared(true, durationInTicks);

        if (!this.isSheared()) {
            this.playSound(SoundEvents.ARMADILLO_ROLL, 0.9f, 0.7f - this.random.nextFloat() * 0.2f);
            this.playSound(SoundEvents.SHULKER_CLOSE, 0.8f, 1.0f - this.random.nextFloat() * 0.3f);
        }
    }

    public boolean isPulseWalking() {
        return this.getNavigation().isInProgress() && !this.isAggressive() && !this.isScared();
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
        Level level = this.level();
        boolean damaged = super.hurt(source, amount);

        if (damaged && !level.isClientSide) {
            this.triggerAnim("actions", "hurt");
        }

        if (this.isBaby()) {
            this.setScared(true, 100);
            if (!level.isClientSide && source.getEntity() != null) {
                List<MoumoutonEntity> moumoutonsNearby = FlainsUtils.getEntitiesOfTypeNearbyEntity(
                        this,
                        MoumoutonEntity.class,
                        10,
                        entity -> entity.isAlive() && !entity.isBaby()
                );

                for (MoumoutonEntity moumouton : moumoutonsNearby) {
                    moumouton.setTarget((LivingEntity) source.getEntity());
                    moumouton.setAggressive(true);
                }
            }
        } else {
            if (this.isScared())
                this.setScared(false);
        }
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

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.HORSE_STEP, 0.15F, 1.0F);
    }
}