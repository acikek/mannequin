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
		model.leftLeg.visible = limbs.leftLeg().isBaseVisible(mannequinRenderState.mannequin$getProfile());
		model.rightLeg.visible = limbs.rightLeg().isBaseVisible(mannequinRenderState.mannequin$getProfile());
		model.leftArm.visible = limbs.leftArm().isBaseVisible(mannequinRenderState.mannequin$getProfile());
		model.rightArm.visible = limbs.rightArm().isBaseVisible(mannequinRenderState.mannequin$getProfile());
		model.body.visible = limbs.torso().isBaseVisible(mannequinRenderState.mannequin$getProfile());
	}
}
