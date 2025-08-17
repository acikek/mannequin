package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.client.MannequinClient;
import com.acikek.mannequin.network.MannequinNetworking;
import com.acikek.mannequin.util.MannequinEntity;
import com.acikek.mannequin.util.MannequinLimb;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow
	@Final
	public Options options;

	@Shadow
	@Nullable
	public LocalPlayer player;

	@Shadow
	@Nullable
	public MultiPlayerGameMode gameMode;

	@Shadow
	@Nullable
	public ClientLevel level;

	@Unique
	private @Nullable MannequinLimb limbToSever;

	@Unique
	private @Nullable InteractionHand severingHand;

	@Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 14))
	private void mannequin$inputSever(CallbackInfo ci) {
		if (!(player instanceof MannequinEntity mannequinEntity)) {
			return;
		}
		if (options.keyAttack.isDown() && options.keyUse.isDown() && !mannequinEntity.mannequin$getData().severing) {
			mannequin$querySevering(player);
		}
		if ((!options.keyAttack.isDown() || !options.keyUse.isDown()) && mannequinEntity.mannequin$getData().severing) {
			mannequinEntity.mannequin$stopSevering();
			ClientPlayNetworking.send(new MannequinNetworking.StopSevering(OptionalInt.empty()));
		}
	}

	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void mannequin$cancelStartAttack(CallbackInfoReturnable<Boolean> cir) {
		if (player instanceof MannequinEntity mannequinEntity && (mannequinEntity.mannequin$getData().severing || limbToSever != null || severingHand != null)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 16))
	private void mannequin$tryStartSevering(CallbackInfo ci) {
		if (player instanceof MannequinEntity mannequinEntity && !player.isUsingItem() && limbToSever != null && severingHand != null) {
			boolean slim = player.getSkin().model() == PlayerSkin.Model.SLIM;
			mannequinEntity.mannequin$getData().slim = slim;
			if (mannequinEntity.mannequin$getData().doll) {
				mannequinEntity.mannequin$sever(limbToSever, severingHand);
				player.swing(severingHand);
			}
			else {
				mannequinEntity.mannequin$startSevering(limbToSever, severingHand, Integer.MAX_VALUE);
				MannequinClient.playSeveringSound(player);
			}
			ClientPlayNetworking.send(new MannequinNetworking.StartSevering(OptionalInt.empty(), severingHand == InteractionHand.MAIN_HAND, slim));
		}
	}

	@Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
	private void mannequin$cancelContinueAttack(boolean bl, CallbackInfo ci) {
		if (player instanceof MannequinEntity mannequinEntity && mannequinEntity.mannequin$getData().severing) {
			ci.cancel();
		}
	}

	@Inject(method = "handleKeybinds", at = @At("TAIL"))
	private void mannequin$clearQueryData(CallbackInfo ci) {
		limbToSever = null;
		severingHand = null;
	}

	@ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z", ordinal = 1))
	private boolean mannequin$cancelSwap(boolean original) {
		return original || (player instanceof MannequinEntity mannequinEntity && mannequinEntity.mannequin$getData().limbs.getArm(player.getMainArm().getOpposite()).severed);
	}

	@Unique
	private void mannequin$querySevering(LocalPlayer player) {
		if (!(player instanceof MannequinEntity mannequinEntity)) {
			return;
		}
		if (gameMode == null || gameMode.isDestroying() || player.isHandsBusy() || player.isUsingItem()) {
			return;
		}
		for (var hand : InteractionHand.values()) {
			var stack = player.getItemInHand(hand);
			if (level == null || !stack.isItemEnabled(level.enabledFeatures())) {
				continue;
			}
			var limbCandidate = mannequinEntity.mannequin$getData().limbs.resolve(player, stack, hand);
			if (limbCandidate != null && !limbCandidate.severed) {
				limbToSever = limbCandidate;
				severingHand = hand;
				return;
			}
		}
	}
}
