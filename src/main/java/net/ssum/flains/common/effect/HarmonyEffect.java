package net.ssum.flains.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class HarmonyEffect extends MobEffect {

    public HarmonyEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            entity.level().getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(16.0D)).forEach(mob -> {
                if (mob.getTarget() == entity) {
                    mob.setTarget(null);
                }
            });
        }
        return super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}