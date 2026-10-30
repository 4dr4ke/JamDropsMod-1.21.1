package net.ssum.flains.common.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ssum.flains.FlainsUtils;
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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class BramblerEntity extends Mob implements GeoEntity {
    private enum State {
        ATTACK,
        SPAWN,
        IDLE
    }

    private State state = State.IDLE;
    private int attackTicks = 0;
    private int attackDelay = 50;
    private int numberOfAttacks = 3;
    private boolean attackAnimation = false;
    private boolean brambleAttack;
    private final Deque<Vec3> pendingSpikes = new ArrayDeque<>();
    private Player attackTarget;


    public BramblerEntity(EntityType<? extends BramblerEntity> entityType, Level level) {
        super(entityType, level);
    }

    // geckolib
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ATTACK_RIGHT = RawAnimation.begin().thenPlay("attack_right");
    private static final RawAnimation ATTACK_LEFT = RawAnimation.begin().thenPlay("attack_left");
    private static final RawAnimation ATTACK_BOTH = RawAnimation.begin().thenPlay("attack_both");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, state -> state.setAndContinue(IDLE)));

        controllers.add(new AnimationController<>(
                this, "actions", 0, state -> PlayState.STOP)
                .triggerableAnim("attack_right", ATTACK_RIGHT)
                .triggerableAnim("attack_left", ATTACK_LEFT)
                .triggerableAnim("attack_both", ATTACK_BOTH)
                //.triggerableAnim("death", DEATH)
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
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (!level().isClientSide && !pendingSpikes.isEmpty() && tickCount % 2 == 0) {
            FlainsUtils.spawnEntityAt(ModEntities.BRAMBLESPIKE.get(), level(), pendingSpikes.poll());
        }

        tickAttack();
    }

    private void tickAttack() {
        attackTicks++;

        if (!attackAnimation) {
            brambleAttack = random.nextFloat() > 0.5;
        }

        List<Player> players = FlainsUtils.getEntitiesOfTypeNearbyEntity(
                this, Player.class, 15,
                player -> player.isAlive() && !player.isCreative() && !player.isSpectator()
                        && (brambleAttack ? this.hasLineOfSight(player) : !FlainsUtils.isBoxedIn(player))
        );

        Player nearest = null;
        double bestDist = Double.MAX_VALUE;
        for (Player player : players) {
            double dist = this.distanceToSqr(player);
            if (dist < bestDist) {
                nearest = player;
                bestDist = dist;
            }
        }

        if (nearest == null) return;

        if (!attackAnimation) {
            double diff = nearest.getX() - this.getX();
            String animation = diff > 5 ? "attack_left" : diff < -5 ? "attack_right" : "attack_both";

            attackAnimation = true;
            attackDelay = 50;
            attackTarget = nearest;
            this.triggerAnim("actions", animation);

            this.lookAt(nearest, 30.0f, 30.0f);

            return;
        }

        if (attackTarget != null && attackTarget.isAlive()) {
            this.getLookControl().setLookAt(attackTarget, 30.0f, 30.0f);
            this.lookAt(attackTarget, 30.0f, 30.0f);

            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
        }

        if (--attackDelay > 0) return;

        if (brambleAttack) {
            spawnSpikeLine(nearest);
        } else {
            FlainsUtils.spawnEntityAt(ModEntities.EXPLODING_BLOSSOM.get(), level(), nearest.position());
        }

        attackAnimation = false;
        attackTarget = null;
    }

    // attributes

    public static AttributeSupplier.Builder createAttribute() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 28d)
                .add(Attributes.MOVEMENT_SPEED, 0.12d)
                .add(Attributes.FOLLOW_RANGE, 40d);
    }

    // behavior

    public boolean isAttacking() {
        return true;
    }

    private void spawnSpikeLine(Player target) {
        Vec3 start = this.position();
        Vec3 dir = new Vec3(target.getX() - start.x, 0, target.getZ() - start.z);

        double length = dir.length();
        if (length < 0.001) return;

        dir = dir.normalize();

        double spacing = 2.5;
        for (double d = spacing; d <= length + spacing; d += spacing) {
            Vec3 pos = findGround(start.add(dir.scale(d)));
            pendingSpikes.add(pos);

            //FlainsUtils.spawnEntityAt(ModEntities.BRAMBLESPIKE.get(), level(), pos);
        }
    }

    private Vec3 findGround(Vec3 pos) {
        BlockPos.MutableBlockPos p = BlockPos.containing(pos).mutable();
        for (int i = 0; i < 3 && level().getBlockState(p).blocksMotion(); i++) {
            p.move(Direction.UP);
        }
        for (int i = 0; i < 4 && !level().getBlockState(p.below()).blocksMotion(); i++) {
            p.move(Direction.DOWN);
        }
        return new Vec3(pos.x, p.getY(), pos.z);
    }

    // data

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }
}
