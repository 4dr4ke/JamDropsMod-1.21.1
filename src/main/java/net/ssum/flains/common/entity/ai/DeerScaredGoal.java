package net.ssum.flains.common.entity.ai;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.ssum.flains.common.entity.custom.DeerEntity;

import java.util.EnumSet;

public class DeerScaredGoal extends Goal {
    private final DeerEntity deer;
    private final double detectRadius;
    private final double speedModifier;
    private Path path;

    public DeerScaredGoal(DeerEntity deer, double detectRadius, double speedModifier) {
        this.deer = deer;
        this.detectRadius = detectRadius;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Player player = deer.level().getNearestPlayer(
                deer.getX(), deer.getY(), deer.getZ(), detectRadius,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(entity -> !entity.isDiscrete())
        );
        if (player == null) return false;
        if (deer.isOwnedBy(player)) return false;

        Vec3 away = DefaultRandomPos.getPosAway(deer, 16, 7, player.position());
        if (away == null) return false;

        this.path = deer.getNavigation().createPath(away.x, away.y, away.z, 0);
        return this.path != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !deer.getNavigation().isDone();
    }

    @Override
    public void start() {
        deer.getNavigation().moveTo(this.path, speedModifier);
    }

    @Override
    public void stop() {
        deer.getNavigation().stop();
        deer.setScared(false);
        deer.setRunning(false);
    }
}
