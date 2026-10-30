package net.ssum.flains.common.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.custom.*;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Flains.MOD_ID);

    public static final Supplier<EntityType<JavelinProjectileEntity>> JAVELIN_PROJECTILE =
            ENTITY_TYPES.register("javelin_projectile", () -> EntityType.Builder.<JavelinProjectileEntity>of(
                    JavelinProjectileEntity::new, MobCategory.MISC)
                    .sized(1.5f, 0.5f).build("javelin_projectile")
            );

    public static final Supplier<EntityType<MoumoutonEntity>> MOUMOUTON =
            ENTITY_TYPES.register("moumouton", () -> EntityType.Builder.of(
                    MoumoutonEntity::new, MobCategory.CREATURE)
                    .sized(1.5f, 1.5f).build("moumouton")
            );

    public static final Supplier<EntityType<DeerEntity>> DEER =
            ENTITY_TYPES.register("deer", () -> EntityType.Builder.of(
                            DeerEntity::new, MobCategory.CREATURE)
                    .sized(0.8f, 1.3f).passengerAttachments(0.8f).build("deer")
            );

    public static final Supplier<EntityType<BramblespikeEntity>> BRAMBLESPIKE =
            ENTITY_TYPES.register("bramblespike", () -> EntityType.Builder.of(
                    BramblespikeEntity::new, MobCategory.MONSTER)
                    .sized(1f, 1.5f).build("bramblespike")
            );

    public static final Supplier<EntityType<ExplodingBlossomEntity>> EXPLODING_BLOSSOM =
            ENTITY_TYPES.register("exploding_blossom", () -> EntityType.Builder.of(
                            ExplodingBlossomEntity::new, MobCategory.MONSTER)
                    .sized(1f, 0.5f).build("exploding_blossom")
            );

    public static final Supplier<EntityType<BramblerEntity>> BRAMBLER =
            ENTITY_TYPES.register("brambler", () -> EntityType.Builder.of(
                            BramblerEntity::new, MobCategory.MONSTER)
                    .sized(0.8f, 2.2f).build("brambler")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
