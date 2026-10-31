package net.ssum.flains.common.data.advancement;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ssum.flains.Flains;

public class ModTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, Flains.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, BetterThanAHoeAdvancementTrigger> BETTER_THAN_A_HOE =
            TRIGGERS.register("better_than_a_hoe", BetterThanAHoeAdvancementTrigger::new);

    public static void register(IEventBus modEventBus) {
        TRIGGERS.register(modEventBus);
    }
}
