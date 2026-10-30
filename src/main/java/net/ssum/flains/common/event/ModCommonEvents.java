package net.ssum.flains.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.ssum.flains.Flains;
import net.ssum.flains.FlainsUtils;
import net.ssum.flains.client.model.entity.JavelinProjectileModel;
import net.ssum.flains.common.effect.ModEffects;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.entity.custom.*;
import net.ssum.flains.common.item.ModItems;
import net.ssum.flains.common.item.custom.JavelinItem;
import net.ssum.flains.common.network.LeftClickJavelinPayload;
import net.ssum.flains.common.particle.ModParticles;
import net.ssum.flains.common.worldgen.ModConfiguredFeatures;
import net.ssum.flains.common.worldgen.ModPlacedFeatures;
import net.ssum.flains.common.worldgen.biome.ModBiomes;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class ModCommonEvents {

    // javelin payload
    @SubscribeEvent // on the mod event bus
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Flains.MOD_ID);

        registrar.playToServer(
                LeftClickJavelinPayload.TYPE,
                LeftClickJavelinPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer serverPlayer) {
                        ItemStack stack = serverPlayer.getMainHandItem();
                        if (stack.getItem() instanceof JavelinItem javelinItem) {
                            javelinItem.triggerAnim(serverPlayer, GeoItem.getOrAssignId(stack, serverPlayer.serverLevel()), "actions", "attack");
                        }
                    }
                }
        ));
    }

    // cancel dragon breath damage if the player wears a gas mask
    @SubscribeEvent
    public static void onPlayerDamagedByDragonBreath(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getSource().is(DamageTypes.DRAGON_BREATH)) {
            ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!helmetStack.isEmpty() && helmetStack.is(ModItems.HORN_GAS_MASK.get())) {
                event.setCanceled(true);
            }
        }
    }

    // spawn butterfly particles
    static int butterflyTicks = 0;

    @SubscribeEvent
    public static void butterflyParticles(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide)
            return;

        butterflyTicks++;
        if (butterflyTicks % 128 != 0) return;

        RandomSource random = player.getRandom();
        BlockPos origin = player.blockPosition();

        if (!level.getBiome(origin).is(ModBiomes.BLOOM)) return;
        if (!level.isDay()) return;

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

    // prevent monsters from targeting player when he has the harmony effect
    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (event.getNewAboutToBeSetTarget() instanceof Player player && player.hasEffect(ModEffects.HARMONY)) {
            event.setCanceled(true);
        }
    }

    // make that the gas mask gives Poison Immunity effect
    static int gasMaskTicks = 0;
    @SubscribeEvent
    public static void onGasMaskWorn(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide)
            return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        if (helmet.isEmpty() || !helmet.is(ModItems.HORN_GAS_MASK.get()))
            return;

        gasMaskTicks++;
        if (gasMaskTicks % 4 == 0) {
            int level = 4;
            MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.POISON_IMMUNITY, 40, level - 1, true, false);
            player.addEffect(effectInstance);
        }
    }

    // reduce 70% of the damage the player takes when he has the Thorn Protection effect
    @SubscribeEvent
    public static void onPlayerDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.hasEffect(ModEffects.THORN_PROTECTION))
            return;

        DamageSource source = event.getSource();
        Entity direct = source.getDirectEntity();
        boolean bramblespike = direct != null && direct.getType() == ModEntities.BRAMBLESPIKE.get();

        if (source.is(DamageTypeTags.BYPASSES_ARMOR) && !bramblespike) {
            return;
        }

        // player only takes 30% of the incoming damage
        float factor = bramblespike ? 0.5f : 0.3f;
        event.setAmount(event.getAmount() * factor);
    }

    // make deers scared when the player is nearby and isn't discrete
    @SubscribeEvent
    public static void onPlayerAroundDeer(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide)
            return;

        if (player.isDiscrete() || player.isCreative() || player.isSpectator())
            return;

        List<DeerEntity> deers = FlainsUtils.getEntitiesOfTypeNearbyEntity(
                player, DeerEntity.class, 10,
                entity -> entity.isAlive() && !entity.isScared()
        );

        for (DeerEntity deer : deers) {
            deer.setRunning(true);
            deer.setScared(true);
        }
    }

    // make moumoutons scared when the player is running nearby
    @SubscribeEvent
    public static void onPlayerRunning(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide)
            return;

        if (!player.isSprinting() || player.isCreative() || player.isSpectator())
            return;

        List<MoumoutonEntity> entities = FlainsUtils.getEntitiesOfTypeNearbyEntity(
                player, MoumoutonEntity.class, 7,
                entity -> entity.isAlive() && !entity.isScared()
        );

        for (MoumoutonEntity moumouton : entities) {
            moumouton.tryScare(100);
        }
    }

    // make that moumoutons can drop their horns
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide)
            return;

        LivingEntity entity = event.getEntity();
        RandomSource random = level.getRandom();
        ItemStack toAdd = null;

        if (event.getEntity() instanceof MoumoutonEntity moumouton) {
            float chance = random.nextFloat();
            if (chance > 0.65f) // 35% chance
                toAdd = new ItemStack(ModItems.MOUMOUTON_HORN.get());
        }

        if (toAdd == null)
            return;

        ItemEntity itemEntity = new ItemEntity(
                level,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                toAdd
        );

        event.getDrops().add(itemEntity);
    }

    // entity attributes
    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(
                ModEntities.MOUMOUTON.get(),
                MoumoutonEntity.createAttribute().build()
        );

        event.put(
                ModEntities.DEER.get(),
                DeerEntity.createAttribute().build()
        );

        event.put(
                ModEntities.BRAMBLER.get(),
                BramblerEntity.createAttribute().build()
        );

        event.put(
                ModEntities.BRAMBLESPIKE.get(),
                BramblespikeEntity.createAttribute().build()
        );

        event.put(
                ModEntities.EXPLODING_BLOSSOM.get(),
                ExplodingBlossomEntity.createAttribute().build()
        );


    }

    // entity layers
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(JavelinProjectileModel.LAYER_LOCATION, JavelinProjectileModel::createBodyLayer);
    }

    // entity spawn placements
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.MOUMOUTON.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR
        );

        event.register(ModEntities.DEER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR
        );
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
