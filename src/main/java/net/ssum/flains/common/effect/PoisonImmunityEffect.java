package net.ssum.flains.common.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PoisonImmunityEffect extends MobEffect {

    public PoisonImmunityEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        int level = amplifier + 1;

        if (level >= 1) removeIfPresent(entity, MobEffects.POISON);
        if (level >= 2) removeIfPresent(entity, MobEffects.CONFUSION);
        if (level >= 3) removeIfPresent(entity, MobEffects.WEAKNESS);
        if (level >= 4) removeIfPresent(entity, MobEffects.MOVEMENT_SLOWDOWN);
        if (level >= 5) removeIfPresent(entity, MobEffects.WITHER);

        return super.applyEffectTick(entity, amplifier);
    }

    private void removeIfPresent(LivingEntity entity, Holder<MobEffect> effect) {
        if (entity.hasEffect(effect)) {
            entity.removeEffect(effect);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}