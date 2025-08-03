package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.MannequinRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)V", at = @At("TAIL"))
	private void mannequin$hideParts(PlayerRenderState playerRenderState, CallbackInfo ci) {
		if (!(playerRenderState instanceof MannequinRenderState mannequinRenderState)) {
			return;
		}
		var limbs = mannequinRenderState.mannequin$getLimbs();
		if (limbs == null) {
			return;
		}
		var model = ((HumanoidModel<?>) (Object) this);
		model.leftLeg.visible = !limbs.leftLeg().severed;
		model.rightLeg.visible = !limbs.rightLeg().severed;
		model.leftArm.visible = !limbs.leftArm().severed;
		model.rightArm.visible = !limbs.rightArm().severed;
		model.body.visible = !limbs.torso().severed;
	}
}
