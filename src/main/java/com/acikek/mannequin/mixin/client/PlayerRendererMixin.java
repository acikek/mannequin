package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.MannequinEntity;
import com.acikek.mannequin.util.MannequinRenderState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
	private void mannequin$_a(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci) {
		if (abstractClientPlayer instanceof MannequinEntity mannequinEntity && playerRenderState instanceof MannequinRenderState severingRenderState) {
			severingRenderState.mannequin$setLimbs(mannequinEntity.mannequin$getLimbs());
		}
	}
}
