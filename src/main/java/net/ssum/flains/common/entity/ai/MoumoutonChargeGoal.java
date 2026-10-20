package net.ssum.flains.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;

import java.util.EnumSet;

public class MoumoutonChargeGoal extends Goal {
    private static final int PREPARATION_TICKS = 10; // waits 0.5s before charging the player
    private static final int MAX_CHARGE_TICKS = 30; // duration of the charge
    private static final int RECOVER_TICKS = 20; // stun duration after a charge
    private static final int COOLDOWN_TICKS = 15; // delay before the next attack
    private static final double OVERSHOOT = 10.0; // goes further than the target's position when charging it
    private static final float KNOCKBACK = 1.5f; // the amount of knockback the target takes when hit by the charge
    private static final int STEP_INTERVAL = 4; //

    private final MoumoutonEntity mob;
    private final double chargeSpeed;
    private final double triggerChargeRange;
    private final double minRangeAllowed;

    private State state = State.PREPARATION; // default state
    private int stateTicks;
    private long nextUseTicks;
    private boolean hasHit;
    private boolean done;
    private Vec3 chargeEnd;
    private boolean preparationSoundPlayed = false;

    public MoumoutonChargeGoal(MoumoutonEntity mob, double chargeSpeed, double triggerRange) {
        this.mob = mob;
        this.chargeSpeed = chargeSpeed;
        this.triggerChargeRange = triggerRange * triggerRange;
        this.minRangeAllowed = 9.0; //3.0*3.0
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();

        if (target == null || !target.isAlive() || !isValidTarget(target))
            return false;

        if (this.mob.isBaby() || this.mob.isScared() || this.mob.level().getGameTime() < this.nextUseTicks)
            return false;

        double dist = this.mob.distanceToSqr(target);
        return dist <= this.triggerChargeRange && dist >= this.minRangeAllowed && this.mob.getSensing().hasLineOfSight(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        return !this.done && !this.mob.isScared() && target != null && target.isAlive() && isValidTarget(target) && this.mob.isWithinRestriction(target.blockPosition());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.state = State.PREPARATION;
        this.stateTicks = 0;
        this.hasHit = false;
        this.done = false;
        this.chargeEnd = null;

        this.mob.getNavigation().stop();
        this.mob.setAggressive(true);
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return;
        }

        if (this.state == State.PREPARATION)
            tickPreparation(target);
        else if (this.state == State.CHARGE)
            tickCharge(target);
        else if (this.state == State.RECOVER)
            tickRecover();
    }

    private void tickPreparation(LivingEntity target) {
        // stop the mob and make it look at its target
        this.mob.getNavigation().stop();
        this.mob.getLookControl().setLookAt(target, 30.0f, 30.0f);

        if (!this.preparationSoundPlayed) {
            this.preparationSoundPlayed = true;
            this.mob.playSound(SoundEvents.SHEEP_HURT, 1.0f, 0.8f);
        }

        this.stateTicks++;
        if (this.stateTicks >= PREPARATION_TICKS) {
            Vec3 start = this.mob.position();

            // get the direction between the mob and the target, regardless of the Y axis
            // and normalize the direction vector
            Vec3 direction = target.position().subtract(start);
            direction = direction.multiply(1, 0, 1).normalize();

            // if the mob and its target are too close, it uses the mob's look angle instead of the direction vector
            if (direction.lengthSqr() < 1.0E-6) {
                direction = this.mob.getLookAngle().multiply(1, 0, 1).normalize();
            }

            // set the end position of the charge to the target's position + the overshoot offset
            this.chargeEnd = target.position().add(direction.scale(OVERSHOOT));

            // start the charge
            this.mob.getNavigation().moveTo(this.chargeEnd.x, this.chargeEnd.y, this.chargeEnd.z, this.chargeSpeed);
            this.mob.triggerAnim("actions", "attack");

            this.state = State.CHARGE;
            this.stateTicks = 0;
            this.preparationSoundPlayed = false;
        }
    }

    private void tickCharge(LivingEntity target) {
        Level level = this.mob.level();
        RandomSource random = level.getRandom();

        this.stateTicks++;

        if (this.stateTicks % STEP_INTERVAL == 0) {
            float pitch = 1.0f - random.nextFloat() * 0.3f;
            this.mob.playSound(SoundEvents.RAVAGER_STEP, 0.3f, pitch);
            this.mob.playSound(SoundEvents.HORSE_STEP, 1.0f, pitch);

            BlockPos currentPos = this.mob.blockPosition();
            BlockState currentBlock = level.getBlockState(currentPos);
            BlockPos below = currentPos.below();
            BlockState blockBelow = level.getBlockState(below);

            if (currentBlock.is(BlockTags.FLOWERS) || currentBlock.is(BlockTags.REPLACEABLE_BY_TREES) || currentBlock.is(BlockTags.SMALL_FLOWERS))
                level.destroyBlock(currentPos, true);

            if (blockBelow.is(BlockTags.FLOWERS) || blockBelow.is(BlockTags.REPLACEABLE_BY_TREES) || blockBelow.is(BlockTags.SMALL_FLOWERS))
                level.destroyBlock(below, true);

            if (blockBelow.is(BlockTags.DIRT)) {
                level.setBlock(below, Blocks.DIRT.defaultBlockState(), Block.UPDATE_ALL);
                this.mob.playSound(SoundEvents.GRASS_BREAK, 0.7f, pitch);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ(),
                    1,
                    0.5, 0.5, 0.5,
                    0.01
            );
        }

        if (!this.hasHit && this.mob.getBoundingBox().inflate(0.4).intersects(target.getBoundingBox())) {
            this.hasHit = true;
            this.mob.doHurtTarget(target);

            double dx = target.getX() - this.mob.getX();
            double dz = target.getZ() - this.mob.getZ();
            target.knockback(KNOCKBACK, -dx, -dz);

            startRecover();
            return;
        }

        if (this.mob.getNavigation().isDone() || this.stateTicks >= MAX_CHARGE_TICKS) {
            startRecover();
        }
    }

    private void startRecover() {
        this.mob.getNavigation().stop();
        this.state = State.RECOVER;
        this.stateTicks = 0;
    }

    private void tickRecover() {
        this.mob.getNavigation().stop();

        this.stateTicks++;
        if (this.stateTicks >= RECOVER_TICKS) {
            this.done = true;
        }
    }

    @Override
    public void stop() {
        LivingEntity target = this.mob.getTarget();
        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
        this.nextUseTicks = this.mob.level().getGameTime() + COOLDOWN_TICKS;
    }

    private static boolean isValidTarget(LivingEntity target) {
        return !(target instanceof Player player) || (!player.isSpectator() && !player.isCreative());
    }

    private enum State {
        PREPARATION,
        CHARGE,
        RECOVER;
    }

}
