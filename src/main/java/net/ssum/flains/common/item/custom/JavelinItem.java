package net.ssum.flains.common.item.custom;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.ssum.flains.client.renderer.item.JavelinItemRenderer;
import net.ssum.flains.common.entity.custom.JavelinProjectileEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class JavelinItem extends ProjectileWeaponItem implements GeoItem {
    private boolean used = false;

    public JavelinItem(Properties properties) {
        super(properties);
    }

    // geckolib
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this, "actions", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private JavelinItemRenderer renderer;

            @Override
            public @Nullable BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new JavelinItemRenderer();
                return this.renderer;
            }
        });
    }

    // item

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        used = true;
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles(ItemStack stack) {
        return stack1 -> stack1.getItem() == this;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!(livingEntity instanceof Player player))
            return;

        used = false;

        int charge = this.getUseDuration(stack, livingEntity) - timeCharged;
        float power = BowItem.getPowerForTime(charge);

        if (power > 0.1f && !level.isClientSide) {
            JavelinProjectileEntity projectile = new JavelinProjectileEntity(player, level);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
            level.addFreshEntity(projectile);

            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return null;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 0;
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {

    }

    public boolean isBeingUsed() {
        return this.used;
    }
}
