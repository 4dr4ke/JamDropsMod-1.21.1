package net.ssum.flains.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.ssum.flains.Flains;
import net.ssum.flains.common.entity.custom.MoumoutonEntity;

import java.util.List;

@EventBusSubscriber(modid = Flains.MOD_ID)
public class ModCommonEvents {

    @SubscribeEvent
    public static void onPlayerRunning(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide)
            return;
        if (!player.isSprinting())
            return;

        AABB searchBox = player.getBoundingBox().inflate(10);

        List<MoumoutonEntity> entities = level.getEntitiesOfClass(
                MoumoutonEntity.class,
                searchBox,
                entity -> entity.isAlive() && !entity.isScared()
        );

        for (MoumoutonEntity moumouton : entities) {
            moumouton.tryScare();
        }
    }
}
