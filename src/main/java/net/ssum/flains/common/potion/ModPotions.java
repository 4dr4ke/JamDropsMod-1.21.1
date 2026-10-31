package net.ssum.flains.common.potion;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;
import net.ssum.flains.common.effect.ModEffects;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, Flains.MOD_ID);

    public static final Holder<Potion> HARMONY_POTION = POTIONS.register("harmony_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.HARMONY, 600, 0)));

    public static final Holder<Potion> THORN_PROTECTION_POTION = POTIONS.register("thorn_protection_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.THORN_PROTECTION, 1000, 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
