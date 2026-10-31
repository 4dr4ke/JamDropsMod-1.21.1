package net.ssum.flains.common.effect;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.ssum.flains.common.item.ModItems;

public class PoisonImmunityEffect extends MobEffect {
    private final int HURT_COOLDOWN = 10;
    private int cooldown = 0;

    public PoisonImmunityEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        cooldown++;

        int level = amplifier + 1;

        if (level >= 1) removeIfPresent(entity, MobEffects.POISON);
        if (level >= 2) removeIfPresent(entity, MobEffects.CONFUSION);
        if (level >= 3) removeIfPresent(entity, MobEffects.WEAKNESS);
        if (level >= 4) removeIfPresent(entity, ModEffects.PARALYSIA);
        if (level >= 5) removeIfPresent(entity, MobEffects.WITHER);

        return super.applyEffectTick(entity, amplifier);
    }

    private void removeIfPresent(LivingEntity entity, Holder<MobEffect> effect) {
        if (entity.hasEffect(effect)) {
            entity.removeEffect(effect);

            if (cooldown > HURT_COOLDOWN && entity instanceof ServerPlayer player && player.level() instanceof ServerLevel serverLevel) {
                ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);
                if (!helmetStack.isEmpty() && helmetStack.is(ModItems.HORN_GAS_MASK.get())) {
                    helmetStack.hurtAndBreak(
                            1,
                            serverLevel,
                            player,
                            item -> player.onEquippedItemBroken(item, EquipmentSlot.MAINHAND)
                    );
                }
                cooldown = 0;
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}