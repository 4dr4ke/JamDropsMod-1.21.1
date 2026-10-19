package net.ssum.flains.common.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;

import java.util.EnumSet;

public class MoumoutonScaredGoal extends Goal {
    private final MoumoutonEntity mob;

    public MoumoutonScaredGoal(MoumoutonEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return mob.isScared() && mob.getTarget() == null && !mob.isAggressive();
    }

    @Override
    public void start() {
        // prevents the mob from moving as long as it's scared
        mob.getNavigation().stop();
    }
}
