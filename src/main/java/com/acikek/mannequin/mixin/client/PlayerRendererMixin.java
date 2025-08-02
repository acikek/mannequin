package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.SeveringEntity;
import com.acikek.mannequin.util.SeveringRenderState;
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
		if (abstractClientPlayer instanceof SeveringEntity severingEntity && playerRenderState instanceof SeveringRenderState severingRenderState) {
			severingRenderState.mannequin$setSeveredLimbs(severingEntity.mannequin$getSeveredLimbs());
		}
	}
}
