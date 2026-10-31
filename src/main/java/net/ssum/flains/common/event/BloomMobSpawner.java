package net.ssum.flains.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.ModEntities;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class BloomMobSpawner {

    private static final ResourceLocation BLOOM =
            ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "bloom");

    private static final int INTERVAL_TICKS = 200;  // 10 s
    private static final int MAX_MOBS_AROUND = 14;  // plafond dans un rayon de 64 blocs
    private static final int MIN_DIST = 24;
    private static final int MAX_DIST = 48;
    private static final int ATTEMPTS = 10;

    private record Entry(Supplier<? extends EntityType<? extends Mob>> type,
                         int weight, int min, int max) {}

    private static final List<Entry> ENTRIES = List.of(
            new Entry(ModEntities.MOUMOUTON, 12, 2, 4),
            new Entry(ModEntities.DEER, 9, 1, 3),
            new Entry(ModEntities.HEDGEHOG, 9, 2, 4),
            new Entry(ModEntities.GUDANNA, 7, 1, 1)
    );

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isSpectator() || player.tickCount % INTERVAL_TICKS != 0) return;

        ServerLevel level = player.serverLevel();
        if (!level.getBiome(player.blockPosition()).is(BLOOM)) return;

        AABB box = player.getBoundingBox().inflate(64);
        int existing = 0;
        for (Entry e : ENTRIES) {
            existing += level.getEntitiesOfClass(Mob.class, box,
                    m -> m.getType() == e.type().get()).size();
        }
        if (existing >= MAX_MOBS_AROUND) return;

        RandomSource random = level.random;
        Entry entry = pickWeighted(random);
        EntityType<? extends Mob> type = entry.type().get();

        for (int i = 0; i < ATTEMPTS; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = MIN_DIST + random.nextInt(MAX_DIST - MIN_DIST + 1);
            int x = player.blockPosition().getX() + (int) (Math.cos(angle) * dist);
            int z = player.blockPosition().getZ() + (int) (Math.sin(angle) * dist);

            if (!level.hasChunkAt(new BlockPos(x, 0, z))) continue;

            BlockPos pos = level.getHeightmapPos(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));

            if (!isValidSpot(level, type, pos)) continue;

            int count = entry.min() + random.nextInt(entry.max() - entry.min() + 1);
            for (int n = 0; n < count; n++) {
                BlockPos p = pos.offset(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
                p = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p);
                if (isValidSpot(level, type, p)) {
                    type.spawn(level, p, MobSpawnType.NATURAL);
                }
            }
            return;
        }
    }

    private static boolean isValidSpot(ServerLevel level, EntityType<? extends Mob> type, BlockPos pos) {
        if (!level.getBiome(pos).is(BLOOM)) return false;
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)
                && level.getFluidState(pos).isEmpty()
                && level.getFluidState(below).isEmpty()
                && level.noCollision(type.getSpawnAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
    }

    private static Entry pickWeighted(RandomSource random) {
        int total = ENTRIES.stream().mapToInt(Entry::weight).sum();
        int roll = random.nextInt(total);
        for (Entry e : ENTRIES) {
            roll -= e.weight();
            if (roll < 0) return e;
        }
        return ENTRIES.get(0);
    }
}