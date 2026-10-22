package net.ssum.flains.common.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;

import java.util.EnumSet;

public class MoumoutonScaredGoal extends Goal {
    private final MoumoutonEntity mob;
    private final double speedModifier;

    public MoumoutonScaredGoal(MoumoutonEntity mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return mob.isScared() && mob.getTarget() == null && !mob.isAggressive();
    }

    @Override
    public void start() {
        if (mob.isSheared()) {
            // makes the mob run away if it cannot hide in its wool
            runAway();
        } else
            // prevents the mob from moving while it is hiding
            mob.getNavigation().stop();
    }

    private void runAway() {
        Player player = mob.level().getNearestPlayer(mob, 16.0);

        Vec3 from = player != null ? player.position() : mob.position();
        Vec3 target = DefaultRandomPos.getPosAway(mob, 16, 7, from);

        if (target != null) {
            mob.getNavigation().moveTo(target.x, target.y, target.z, this.speedModifier);
        }
    }
}
