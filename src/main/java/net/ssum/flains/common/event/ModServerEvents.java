package net.ssum.flains.common.event;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.FlainsUtils;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;
import net.ssum.flains.common.item.ModItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class ModServerEvents {

    @SubscribeEvent
    public static void onPlayerRunning(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide)
            return;

        if (!player.isSprinting() || player.isCreative() || player.isSpectator())
            return;

        List<MoumoutonEntity> entities = FlainsUtils.getEntitiesOfTypeNearbyEntity(
                player, MoumoutonEntity.class, 10,
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
