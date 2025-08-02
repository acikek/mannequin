package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.SeveringEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

	@ModifyExpressionValue(method = "modifyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
	private boolean mannequin$_b(boolean original) {
		return mannequin$isUsingItem(original);
	}

	@ModifyExpressionValue(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
	private boolean mannequin$_a(boolean original) {
		return mannequin$isUsingItem(original);
	}

	@Unique
	private boolean mannequin$isUsingItem(boolean original) {
		if (!(((LocalPlayer) (Object) this) instanceof SeveringEntity severingEntity)) {
			return original;
		}
		return severingEntity.mannequin$isSevering() || (original && !severingEntity.mannequin$canSever());
	}
}
