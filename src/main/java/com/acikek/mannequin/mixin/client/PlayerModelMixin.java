package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.SeveringRenderState;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
		if (!(playerRenderState instanceof SeveringRenderState severingRenderState) || severingRenderState.mannequin$getSeveredLimbs() == null) {
			return;
		}
		var model = ((HumanoidModel<?>) (Object) this);
		for (var limb : severingRenderState.mannequin$getSeveredLimbs()) {
			var part = switch (limb) {
				case LEFT_LEG -> model.leftLeg;
				case RIGHT_LEG -> model.rightLeg;
				case LEFT_ARM -> model.leftArm;
				case RIGHT_ARM -> model.rightArm;
				case TORSO -> model.body;
			};
			part.visible = false;
		}
	}
}
