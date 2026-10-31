package net.ssum.flains.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.ssum.flains.Flains;
import net.ssum.flains.FlainsUtils;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.entity.custom.GudannaEntity;
import net.ssum.flains.common.entity.custom.HedgeHogEntity;
import net.ssum.flains.common.item.ModItems;
import net.ssum.flains.common.network.GudannaAttackPayload;
import net.ssum.flains.common.particle.ModParticles;
import net.ssum.flains.common.worldgen.ModConfiguredFeatures;
import net.ssum.flains.common.worldgen.ModPlacedFeatures;
import net.ssum.flains.common.worldgen.biome.ModBiomes;

import java.util.Set;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class ModCommonEvents {

    // gudanna right click
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (!(player.getVehicle() instanceof GudannaEntity gudanna)) return;
        if (!event.getItemStack().is(ModItems.GOLDEN_APPLE_ON_A_STICK)) return;

        if (!gudanna.isAbilityActive()) {
            if (gudanna.trySettingUpAbility()) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        } else {
            gudanna.stopAbility();
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    // gudanna payload
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(GudannaAttackPayload.TYPE, GudannaAttackPayload.CODEC,
                (payload, ctx) -> {
                    if (ctx.player().getVehicle() instanceof GudannaEntity gudanna) {
                        if (!gudanna.isAbilityActive()) {
                            gudanna.tryAttack();
                        } else {
                            gudanna.tryAbility(ctx.player());
                        }
                    }
                });
    }

    // hedgehog death
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (event.getSource().getEntity() instanceof Player player) {
            if (victim.getType() == ModEntities.HEDGEHOG.get()) {
                player.displayClientMessage(Component.translatable("flains.message.killed_hedgehog"), true);
            }
        }
    }

    // hedgehog scare logic
    @SubscribeEvent
    public static void onPunchingScaredHedgehog(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof HedgeHogEntity hedgehog)) return;
        Entity entity = event.getSource().getEntity();
        if (entity == null) return;

        if (hedgehog.isScared()) {
            if (entity.isAlive()) {
                entity.hurt(entity.level().damageSources().mobAttack(hedgehog), 2f);
            }
        } else {
            hedgehog.scare();
        }
    }

    // spawn butterfly particles
    static int butterflyTicks = 0;

    @SubscribeEvent
    public static void butterflyParticles(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide || level.isDay() || level.isRaining())
            return;

        butterflyTicks++;
        if (butterflyTicks % 128 != 0) return;

        RandomSource random = player.getRandom();
        BlockPos origin = player.blockPosition();

        if (level.getBrightness(LightLayer.SKY, origin) < 1 || !level.getBiome(origin).is(ModBiomes.BLOOM)) return;

        int radius = 20;

        for (int i = random.nextInt(5); i < 5; i++) {
            int dx = random.nextInt(2 * radius + 1) - radius;
            int dz = random.nextInt(2 * radius + 1) - radius;

            dx = FlainsUtils.clampToMinimum(dx, 5);
            dz = FlainsUtils.clampToMinimum(dz, 5);

            BlockPos candidate = origin.offset(
                    dx,
                    0,
                    dz
            );

            SimpleParticleType particle = ModParticles.YELLOW_BUTTERFLY.get();
            switch (random.nextInt(3)) {
                case 0 -> particle = ModParticles.YELLOW_BUTTERFLY.get();
                case 1 -> particle = ModParticles.BLUE_BUTTERFLY.get();
                case 2 -> particle = ModParticles.RED_BUTTERFLY.get();
            }

            level.addParticle(
                    particle,
                    candidate.getX(), candidate.getY(), candidate.getZ(),
                    0.1, 0.01, 0.1
            );
        }
    }

    // biome datagen
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(),
                new DatapackBuiltinEntriesProvider(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider(),
                        new RegistrySetBuilder()
                                .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
                                .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
                                .add(Registries.BIOME, ModBiomes::bootstrap),
                        Set.of(Flains.MOD_ID)));
    }
}
