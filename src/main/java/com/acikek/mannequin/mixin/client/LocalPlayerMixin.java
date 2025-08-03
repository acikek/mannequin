package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.MannequinEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

	@ModifyExpressionValue(method = "modifyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
	private boolean mannequin$cancelUsingSlowness(boolean original) {
		return mannequin$isUsingItem(original);
	}

	@ModifyExpressionValue(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
	private boolean mannequin$allowUsingSprinting(boolean original) {
		return mannequin$isUsingItem(original);
	}

	@Unique
	private boolean mannequin$isUsingItem(boolean original) {
		if (!(((LocalPlayer) (Object) this) instanceof MannequinEntity mannequinEntity)) {
			return original;
		}
		return mannequinEntity.mannequin$isSevering() || (original && !mannequinEntity.mannequin$canSever());
	}
}
