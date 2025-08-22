package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.client.MannequinClient;
import com.acikek.mannequin.client.render.CustomLimbsLayer;
import com.acikek.mannequin.client.render.PlayerBloodLayer;
import com.acikek.mannequin.util.MannequinEntity;
import com.acikek.mannequin.util.MannequinRenderState;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void mannequin$addBloodRenderer(EntityRendererProvider.Context context, boolean bl, CallbackInfo ci) {
		((LivingEntityRendererAccessor<AbstractClientPlayer, PlayerRenderState, PlayerModel>) this).callAddLayer(new PlayerBloodLayer((PlayerRenderer) (Object) this));
		((LivingEntityRendererAccessor<AbstractClientPlayer, PlayerRenderState, PlayerModel>) this).callAddLayer(new CustomLimbsLayer((PlayerRenderer) (Object) this, context));
	}

	@Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
	private void mannequin$extractMannequinRenderState(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci) {
		if (abstractClientPlayer instanceof MannequinEntity mannequinEntity && playerRenderState instanceof MannequinRenderState mannequinRenderState) {
			mannequinRenderState.mannequin$setData(mannequinEntity.mannequin$getData());
			mannequinRenderState.mannequin$setProfile(abstractClientPlayer.getGameProfile());
			mannequinRenderState.mannequin$setDeltaTime(f);
			if (playerRenderState.nameTagAttachment == null) {
				return;
			}
			if (mannequinEntity.mannequin$getData().limbs.torso().severed) {
				playerRenderState.nameTagAttachment = playerRenderState.nameTagAttachment.add(0.0, 1.55 * playerRenderState.scale, 0.0);
			}
			else if (mannequinEntity.mannequin$getData().limbs.leftLeg().severed && mannequinEntity.mannequin$getData().limbs.rightLeg().severed) {
				playerRenderState.nameTagAttachment = playerRenderState.nameTagAttachment.add(0.0, 0.65 * playerRenderState.scale, 0.0);
			}
		}
	}

	@Inject(method = "getRenderOffset(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"))
	private void mannequin$captureRenderState(PlayerRenderState playerRenderState, CallbackInfoReturnable<Vec3> cir, @Share("renderState") LocalRef<PlayerRenderState> stateRef) {
		stateRef.set(playerRenderState);
	}

	@ModifyReturnValue(method = "getRenderOffset(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"))
	private Vec3 mannequin$shortenPlayer(Vec3 original, @Share("renderState") LocalRef<PlayerRenderState> stateRef) {
		var renderState = stateRef.get();
		if (!(renderState instanceof MannequinRenderState mannequinRenderState) || mannequinRenderState.mannequin$getData() == null) {
			return original;
		}
		var limbs = mannequinRenderState.mannequin$getData().limbs;
		if (limbs.torso().severed) {
			return original.add(0.0, -1.4 * renderState.scale, 0.0);
		}
		if (limbs.leftLeg().severed && limbs.rightLeg().severed) {
			return original.add(0.0, -0.7 * renderState.scale, 0.0);
		}
		return original;
	}
}
