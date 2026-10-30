package net.ssum.flains.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.ssum.flains.client.model.item.JavelinItemModel;
import net.ssum.flains.common.item.custom.JavelinItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class JavelinItemRenderer extends GeoItemRenderer<JavelinItem> {
    public JavelinItemRenderer() {
        super(new JavelinItemModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        boolean isThirdPerson = transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;

        Minecraft mc = Minecraft.getInstance();
        boolean isBeingUsed = mc.player != null && mc.player.isUsingItem() && mc.player.getUseItem() == stack;

        if (isThirdPerson && isBeingUsed) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.translate(-0.5, -1, -0.5);
            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
}
