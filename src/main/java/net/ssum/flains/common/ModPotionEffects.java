package net.ssum.flains.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.PoisonImmunityEffect;

public class ModPotionEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Flains.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> POISON_IMMUNITY = MOB_EFFECTS.register("poison_immunity",
            () -> new PoisonImmunityEffect(MobEffectCategory.BENEFICIAL, 0x4E9F3D)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}