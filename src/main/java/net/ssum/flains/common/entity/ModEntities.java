package net.ssum.flains.common.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Flains.MOD_ID);

    public static final Supplier<EntityType<MoumoutonEntity>> MOUMOUTON =
            ENTITY_TYPES.register("moumouton", () -> EntityType.Builder.of(
                    MoumoutonEntity::new, MobCategory.CREATURE)
                    .sized(2f, 1.3f).build("moumouton")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
