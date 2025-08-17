package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.client.MannequinClient;
import com.acikek.mannequin.util.MannequinRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends HumanoidRenderState> {

	@Shadow
	@Final
	public ModelPart rightArm;

	@Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
	private void mannequin$poseRightArm(T humanoidRenderState, HumanoidModel.ArmPose armPose, CallbackInfo ci) {
		if (!(humanoidRenderState instanceof MannequinRenderState mannequinRenderState) || mannequinRenderState.mannequin$getData() == null) {
			return;
		}
		var data = mannequinRenderState.mannequin$getData();
		var hand = humanoidRenderState.mainArm == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		if (data.severing && data.severingHand == hand) {
			MannequinClient.test(mannequinRenderState, rightArm);
			ci.cancel();
		}
	}
}
