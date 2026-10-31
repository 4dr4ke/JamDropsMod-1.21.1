package net.ssum.flains.common.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.ssum.flains.Flains;
import net.ssum.flains.client.FlainsClientHooks;
import net.ssum.flains.common.data.advancement.ModTriggers;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class GudannaEntity extends TamableAnimal implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation HEAD_DOWN = RawAnimation.begin().thenPlay("head_down").thenLoop("head_down_idle");
    private static final RawAnimation HEAD_UP = RawAnimation.begin().thenPlay("head_up");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation TALK = RawAnimation.begin().thenPlay("talk");

    private static final int TILL_RADIUS = 1;
    private static final int ATTACK_COOLDOWN = 20;
    private static final int ABILITY_COOLDOWN = 5;
    private static final int HIT_DELAY = 8;
    private int attackCooldown = 0;
    private int abilityCooldown = 0;
    private int pendingHit = -1;
    private static boolean hasHeadDown = false;

    public GudannaEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.STEP_HEIGHT, 1.0f)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75);
    }

    public void tryAttack() {
        if (this.level().isClientSide || attackCooldown > 0 || hasHeadDown) return;
        attackCooldown = ATTACK_COOLDOWN;
        pendingHit = HIT_DELAY;
        this.triggerAnim("actions", "attack");
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) return;

        if (this.tickCount % 2 == 0) breakFoliage();

        if (attackCooldown > 0) attackCooldown--;
        if (pendingHit >= 0) {
            if (pendingHit == 0) doAttack();
            pendingHit--;
        }

        if (abilityCooldown > 0) abilityCooldown--;
    }

    public boolean isAbilityActive() {
        return hasHeadDown;
    }

    public void stopAbility() {
        if (this.level().isClientSide || !isAbilityActive()) return;
        hasHeadDown = false;
        abilityCooldown = 0;
        this.triggerAnim("actions", "head_up");
    }

    private void breakFoliage() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return;

        AABB box = this.getBoundingBox().inflate(0.3, 0.0, 0.3);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = serverLevel.getBlockState(pos);
            if (state.is(BlockTags.LEAVES) || state.is(BlockTags.REPLACEABLE_BY_TREES)) {
                serverLevel.destroyBlock(pos, true, this);
            }
        }
    }

    public boolean tryAbility(Player player) {
        if (this.level().isClientSide || !hasHeadDown || abilityCooldown > 0) return false;
        if (!(this.level() instanceof ServerLevel serverLevel)) return false;

        Vec3 forward = Vec3.directionFromRotation(0, this.yBodyRot).normalize();
        BlockPos origin = BlockPos.containing(
                this.getX() + forward.x * 2f,
                this.getY() - 0.5,
                this.getZ() + forward.z * 2f);

        int tilled = 0;

        for (BlockPos p : BlockPos.betweenClosed(
                origin.offset(-TILL_RADIUS, 0, -TILL_RADIUS),
                origin.offset(TILL_RADIUS, 0, TILL_RADIUS))) {

            BlockPos pos = p.immutable();
            BlockState state = serverLevel.getBlockState(pos);

            if (!serverLevel.getBlockState(pos.above()).isAir()) continue;

            BlockState tilledState = state.getToolModifiedState(
                    new UseOnContext(serverLevel, null, InteractionHand.MAIN_HAND,
                            new ItemStack(Items.IRON_HOE),
                            new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)),
                    ItemAbilities.HOE_TILL, false);

            if (tilledState == null) continue;

            serverLevel.setBlock(pos, tilledState, Block.UPDATE_ALL_IMMEDIATE);
            serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(this, tilledState));
            tilled++;
        }

        if (tilled == 0) return false;

        abilityCooldown = ABILITY_COOLDOWN;
        this.playSound(SoundEvents.HOE_TILL, 1.0F, 1.0F);

        if (player instanceof ServerPlayer serverPlayer) {
            ModTriggers.BETTER_THAN_A_HOE.get().trigger(serverPlayer);
        }
        return true;
    }

    public boolean trySettingUpAbility() {
        if (this.level().isClientSide) return false;
        this.triggerAnim("actions", "head_down");
        hasHeadDown = true;
        return true;

    }

    private void doAttack() {
        Vec3 look = Vec3.directionFromRotation(0, this.getYRot()).normalize();
        AABB zone = this.getBoundingBox().move(look.scale(1.8)).inflate(1.0, 0.5, 1.0);
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

        List<LivingEntity> entities = this.level().getEntitiesOfClass(
                LivingEntity.class,
                zone,
                e -> e != this && e != this.getOwner() && !this.hasPassenger(e)
        );

        for (LivingEntity entity : entities) {
            if (entity.hurt(this.damageSources().mobAttack(this), damage)) {
                entity.knockback(0.8, -look.x, -look.z);
            }
        }

        this.playSound(SoundEvents.RAVAGER_ATTACK, 0.8f, 1.0f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "locomotion", 5, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(
                this, "actions", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK)
                .triggerableAnim("head_down", HEAD_DOWN)
                .triggerableAnim("head_up", HEAD_UP)
                .triggerableAnim("talk", TALK)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.GOLDEN_APPLE) || itemStack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty() && passenger instanceof Player player && this.isOwnedBy(player);
    }

    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.5f;
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float strafe = player.xxa * 0.7f;
        float forward = player.zza;
        if (forward <= 0.0f) forward *= 0.6f;
        return new Vec3(strafe, 0.0, forward);
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.3f);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        if (this.level().isClientSide && this.onGround() && FlainsClientHooks.isJumpPressed()) {
            this.jumpFromGround();
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!this.level().isClientSide && passenger instanceof Player) {
            stopAbility();
        }
    }

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

            if (stack.isEmpty() && !this.isBaby()) {
                if (!this.level().isClientSide) {
                    player.startRiding(this);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    public void playAmbientSound() {
        super.playAmbientSound();
        if (!this.level().isClientSide && !isAbilityActive()) {
            this.triggerAnim("actions", "talk");
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.15F, 1.0F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    1,
                    0.5, 0.5, 0.5,
                    0.01
            );
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ability") && compound.getBoolean("ability")) {
            trySettingUpAbility();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("ability", isAbilityActive());
    }
}
