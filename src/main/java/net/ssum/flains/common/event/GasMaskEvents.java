package net.ssum.flains.common.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.common.effect.ModEffects;
import net.ssum.flains.common.item.ModItems;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class GasMaskEvents {
    // cancel dragon breath damage if the player wears a gas mask
    @SubscribeEvent
    public static void onPlayerDamagedByDragonBreath(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getSource().is(DamageTypes.DRAGON_BREATH)) {
            ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!helmetStack.isEmpty() && helmetStack.is(ModItems.HORN_GAS_MASK.get())) {
                event.setCanceled(true);
            }
        }
    }

    // make that the gas mask gives Poison Immunity effect
    static int gasMaskTicks = 0;
    @SubscribeEvent
    public static void onGasMaskWorn(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide)
            return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        if (helmet.isEmpty() || !helmet.is(ModItems.HORN_GAS_MASK.get()))
            return;

        gasMaskTicks++;
        if (gasMaskTicks % 4 == 0) {
            int level = 4;
            MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.POISON_IMMUNITY, 40, level - 1, true, false);
            player.addEffect(effectInstance);
        }
    }
}
