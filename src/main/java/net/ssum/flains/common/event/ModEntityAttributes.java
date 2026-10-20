package net.ssum.flains.common.event;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class ModEntityAttributes {

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(
                ModEntities.MOUMOUTON.get(),
                MoumoutonEntity.createAttribute().build()
        );
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.MOUMOUTON.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR);
    }
}
