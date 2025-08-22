package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.MannequinEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

	@Unique
	private Player player;

	@Unique
	private float deltaTime;

	@Inject(method = "renderHandsWithItems", at = @At("HEAD"))
	private void mannequin$capturePlayer(float f, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LocalPlayer localPlayer, int i, CallbackInfo ci) {
		player = localPlayer;
		deltaTime = f;
	}

	@Inject(method = "applyItemArmTransform", at = @At("TAIL"))
	private void mannequin$applySeveringAnimation(PoseStack poseStack, HumanoidArm humanoidArm, float f, CallbackInfo ci) {
		if (!(player instanceof MannequinEntity mannequinEntity) || !mannequinEntity.mannequin$getData().severing) {
			return;
		}
		var arm = mannequinEntity.mannequin$getData().severingHand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
		if (arm != humanoidArm) {
			return;
		}
		int right = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
		poseStack.translate(right * -0.25F, 0.12F, 0.2F);
		poseStack.mulPose(Axis.XP.rotationDegrees(-102.25F));
		poseStack.mulPose(Axis.YP.rotationDegrees(right * 13.365F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(right * 78.05F));
		float h = (10 - mannequinEntity.mannequin$getData().severingTicksElapsed) % 10;
		float i = h - deltaTime + 1.0F;
		float j = 1.0F - i / 10.0F;
		float motion = 0.1F * Mth.cos(j * 2.0F * (float) Math.PI);
		poseStack.translate(0.0F, motion, 0.0F);
	}

	@ModifyExpressionValue(method = "renderHandsWithItems", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer$HandRenderSelection;renderMainHand:Z"))
	private boolean mannequin$cancelMainHand(boolean original) {
		return original && (!(player instanceof MannequinEntity mannequinEntity) || !mannequinEntity.mannequin$getData().limbs.getArm(player.getMainArm()).severed);
	}

	@ModifyExpressionValue(method = "renderHandsWithItems", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer$HandRenderSelection;renderOffHand:Z"))
	private boolean mannequin$cancelOffHand(boolean original) {
		return original && (!(player instanceof MannequinEntity mannequinEntity) || !mannequinEntity.mannequin$getData().limbs.getArm(player.getMainArm().getOpposite()).severed);
	}
}
