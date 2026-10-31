package net.ssum.flains.common.event;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredItem;
import net.ssum.flains.Flains;
import net.ssum.flains.client.model.entity.JavelinProjectileModel;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.entity.custom.*;
import net.ssum.flains.common.item.ModItems;
import net.ssum.flains.common.network.GudannaAttackPayload;
import net.ssum.flains.common.potion.ModPotions;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class ModEventBusEvents {
    // entity attributes
    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MOUMOUTON.get(), MoumoutonEntity.createAttribute().build());
        event.put(ModEntities.DEER.get(), DeerEntity.createAttribute().build());
        event.put(ModEntities.BRAMBLESPIKE.get(), BramblespikeEntity.createAttribute().build());
        event.put(ModEntities.GUDANNA.get(), GudannaEntity.createAttributes().build());
        event.put(ModEntities.HEDGEHOG.get(), HedgeHogEntity.createAttributes().build());
    }

    // entity layers
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(JavelinProjectileModel.LAYER_LOCATION, JavelinProjectileModel::createBodyLayer);
    }

    // entity spawn placements
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        System.out.println("[FLAINS] RegisterSpawnPlacementsEvent appelé !");

        event.register(ModEntities.MOUMOUTON.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> {
                    System.out.println("[MOUMOUTON] check " + pos + " reason=" + reason
                            + " below=" + level.getBlockState(pos.below()));
                    return true;
                },
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.DEER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> {
                    System.out.println("[DEER] check " + pos + " reason=" + reason
                            + " below=" + level.getBlockState(pos.below()));
                    return true;
                },
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.HEDGEHOG.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> {
                    System.out.println("[HEDGEHOG] check " + pos + " reason=" + reason
                            + " below=" + level.getBlockState(pos.below()));
                    return true;
                },
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.GUDANNA.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> {
                    System.out.println("[GUDANNA] check " + pos + " reason=" + reason
                            + " below=" + level.getBlockState(pos.below()));
                    return true;
                },
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    // add mod items to vanilla tabs
    @SubscribeEvent
    public static void addToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            ModItems.MOD_SPAWN_EGGS.forEach(event::accept);
        }
    }

    // potion recipes
    public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();
        builder.addMix(Potions.WATER, ModItems.HARMONY_FLOWER.get(), ModPotions.HARMONY_POTION);
        builder.addMix(Potions.WATER, ModItems.ROSE.get(), ModPotions.THORN_PROTECTION_POTION);
    }
}
