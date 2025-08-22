package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.client.MannequinClient;
import com.acikek.mannequin.util.LimbType;
import com.acikek.mannequin.util.MannequinRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends HumanoidRenderState> {

	@Shadow
	@Final
	public ModelPart rightArm;

	@Shadow
	@Final
	public ModelPart leftArm;

	@Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
	private void mannequin$poseRightArm(T humanoidRenderState, HumanoidModel.ArmPose armPose, CallbackInfo ci) {
		if (mannequin$tryPoseArm(humanoidRenderState, true)) {
			ci.cancel();
		}
	}

	@Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
	private void mannequin$poseLeftArm(T humanoidRenderState, HumanoidModel.ArmPose armPose, CallbackInfo ci) {
		if (mannequin$tryPoseArm(humanoidRenderState, false)) {
			ci.cancel();
		}
	}

	@Unique
	private boolean mannequin$tryPoseArm(T humanoidRenderState, boolean right) {
		if (!(humanoidRenderState instanceof MannequinRenderState mannequinRenderState) || mannequinRenderState.mannequin$getData() == null) {
			return false;
		}
		var data = mannequinRenderState.mannequin$getData();
		var hand = humanoidRenderState.mainArm == (right ? HumanoidArm.RIGHT : HumanoidArm.LEFT) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		if (!data.severing || data.severingHand != hand) {
			return false;
		}
		var arm = right ? rightArm : leftArm;
		boolean leg = mannequinRenderState.mannequin$getData().severingLimb.type == LimbType.LEG;
		float h = (10 - mannequinRenderState.mannequin$getData().severingTicksElapsed) % 10;
		float i = h - mannequinRenderState.mannequin$getDeltaTime() + 1.0F;
		float j = 1.0F - i / 10.0F;
		arm.xRot = Mth.cos(j * 2.0F * (float) Math.PI) * 0.3F;
		float xRot = right ? (float) (-Math.PI / 2.5F) : (float) (-Math.PI + Math.PI / 2.5F);
		arm.xRot = arm.xRot * 0.5F + xRot;
		float zRot = leg ? (right ? (float) -Math.PI / 3 : (float) (-Math.PI + Math.PI / 3.0F)) : (float) (-Math.PI / 2);
		arm.zRot = arm.zRot * 0.5F + zRot;
		if (mannequinRenderState.mannequin$getData().severingLimb.type != LimbType.TORSO) {
			arm.yRot = leg ? (float) (Math.PI / 4) : (float) (-Math.PI / 10);
		}
		if (!right) {
			arm.y = 3.0F;
		}
		return true;
	}
}
