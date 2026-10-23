package net.ssum.flains.common.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.FlainsUtils;
import net.ssum.flains.common.effect.ModEffects;
import net.ssum.flains.common.entity.ModEntities;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;
import net.ssum.flains.common.item.ModItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class ModServerEvents {

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

        // player only takes 30% of the incoming damage if he has the thorn protection effect
        float factor = bramblespike ? 0.5f : 0.3f;
        event.setAmount(event.getAmount() * factor);
    }

    @SubscribeEvent
    public static void onPlayerRunning(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide)
            return;

        if (!player.isSprinting() || player.isCreative() || player.isSpectator())
            return;

        List<MoumoutonEntity> entities = FlainsUtils.getEntitiesOfTypeNearbyEntity(
                player, MoumoutonEntity.class, 7,
                entity -> entity.isAlive() && !entity.isScared()
        );

        for (MoumoutonEntity moumouton : entities) {
            moumouton.tryScare(100);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide)
            return;

        LivingEntity entity = event.getEntity();
        RandomSource random = level.getRandom();
        ItemStack toAdd = null;

        if (event.getEntity() instanceof MoumoutonEntity moumouton) {
            float chance = random.nextFloat();
            if (chance > 0.65f) // 35% chance
                toAdd = new ItemStack(ModItems.MOUMOUTON_HORN.get());
        }

        if (toAdd == null)
            return;

        ItemEntity itemEntity = new ItemEntity(
                level,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                toAdd
        );

        event.getDrops().add(itemEntity);
    }
}
