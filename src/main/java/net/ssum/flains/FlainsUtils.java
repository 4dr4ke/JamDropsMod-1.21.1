package net.ssum.flains;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class FlainsUtils {
    /**
     * This method searches for entities of the given class that are located around the given entity, within the given radius.
     * @param entity The entity around which entities of the given class will be searched
     * @param tClass The entity class that will be searched within the given radius
     * @return List entities belonging to the given class
     */
    public static <T extends Entity> List<T> getEntitiesOfTypeNearbyEntity(Entity entity, Class<T> tClass, int radius, Predicate<? super T> filter) {
        Level level = entity.level();
        AABB searchBox = entity.getBoundingBox().inflate(radius);

        return level.getEntitiesOfClass(
                tClass,
                searchBox,
                filter
        );
    }

    public static Entity spawnEntityAt(EntityType<? extends Entity> entityType, Level level, double x, double y, double z ) {
        Entity entity = entityType.create(level);
        if (entity == null) return null;

        entity.moveTo(x, y, z, 0f, 0f);
        level.addFreshEntity(entity);

        return entity;
    }

    public static Entity spawnEntityAt(EntityType<? extends Entity> entityType, Level level, Vec3 pos) {
        return spawnEntityAt(entityType, level, pos.x, pos.y, pos.z);
    }

    public static int clampToMinimum(int value, int threshold) {
        if (value > 0 && value < threshold) return threshold;
        if (value < 0 && value < -threshold) return -threshold;
        return value;
    }

    public static boolean isBoxedIn(Entity entity) {
        AABB box = entity.getBoundingBox();
        double d = 0.1;

        for (Direction dir : Direction.values()) {
            AABB probe = box.move(dir.getStepX() * d, dir.getStepY() * d, dir.getStepZ() * d);
            boolean blocked = entity.level().getBlockCollisions(entity, probe).iterator().hasNext();
            if (!blocked) {
                return false;
            }
        }
        return true;
    }
}
