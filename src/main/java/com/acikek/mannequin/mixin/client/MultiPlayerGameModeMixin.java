package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.MannequinEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mannequin$cancelDestroyBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (mannequin$isMainArmSevered()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mannequin$cancelContinueDestroyBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (mannequin$isMainArmSevered()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	private void mannequin$cancelAttack(Player player, Entity entity, CallbackInfo ci) {
		if (mannequin$isMainArmSevered()) {
			ci.cancel();
		}
	}

	@Unique
	private boolean mannequin$isMainArmSevered() {
		return minecraft.player instanceof MannequinEntity mannequinEntity && mannequinEntity.mannequin$getLimbs().getArm(minecraft.player.getMainArm()).severed;
	}
}
