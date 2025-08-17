package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.client.MannequinClient;
import com.acikek.mannequin.util.LimbType;
import com.acikek.mannequin.util.MannequinRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin<S extends ArmedEntityRenderState> {

	@Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
	private void mannequin$adjustSeveringItem(S armedEntityRenderState, ItemStackRenderState itemStackRenderState, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
		if (!(armedEntityRenderState instanceof MannequinRenderState mannequinRenderState)) {
			return;
		}
		var data = mannequinRenderState.mannequin$getData();
		var arm = data.severingHand == InteractionHand.MAIN_HAND ? armedEntityRenderState.mainArm : armedEntityRenderState.mainArm.getOpposite();
		if (!data.severing || arm != humanoidArm) {
			return;
		}
		boolean right = humanoidArm == HumanoidArm.RIGHT;
		boolean leg = mannequinRenderState.mannequin$getData().severingLimb.type == LimbType.LEG;
		poseStack.translate((right ? -0.025F : -0.09F), 0.0F, -1.0F);
		if (leg) {
			poseStack.translate(-0.3675F, 0.0F, 0.2F);
		}
		poseStack.mulPose(Axis.YP.rotationDegrees(leg ? 230.0F : 180.0F));
		if (right) {
			poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		}
	}
}
