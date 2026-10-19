package net.ssum.flains.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
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
}
