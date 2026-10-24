package net.ssum.flains.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ParalysiaEffect extends MobEffect {

    public ParalysiaEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Conserve uniquement la vitesse verticale (gravité) et bloque X et Z
        entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
        return super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}