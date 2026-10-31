package net.ssum.flains.common.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.common.effect.ModEffects;
import net.ssum.flains.common.entity.ModEntities;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class ModEffectsEvents {

    // reduce 70% of the damage the player takes when he has the Thorn Protection effect
    @SubscribeEvent
    public static void onPlayerDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.hasEffect(ModEffects.THORN_PROTECTION))
            return;

        DamageSource source = event.getSource();
        Entity direct = source.getDirectEntity();
        boolean bramblespike = direct != null && direct.getType() == ModEntities.BRAMBLESPIKE.get();

        if (source.is(DamageTypeTags.BYPASSES_ARMOR) && !bramblespike) {
            return;
        }

        // player only takes 30% of the incoming damage
        float factor = bramblespike ? 0.5f : 0.3f;
        event.setAmount(event.getAmount() * factor);
    }

    // prevent monsters from targeting player when he has the harmony effect
    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (event.getNewAboutToBeSetTarget() instanceof Player player && player.hasEffect(ModEffects.HARMONY)) {
            event.setCanceled(true);
        }
    }
}
