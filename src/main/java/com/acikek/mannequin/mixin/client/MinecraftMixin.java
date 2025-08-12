package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.network.MannequinNetworking;
import com.acikek.mannequin.util.MannequinEntity;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
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

	@Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 14))
	private void mannequin$inputSever(CallbackInfo ci, @Share("canSever") LocalBooleanRef canSever) {
		if (!(player instanceof MannequinEntity mannequinEntity)) {
			return;
		}
		if (options.keyAttack.isDown() && options.keyUse.isDown() && !mannequinEntity.mannequin$isSevering()) {
			canSever.set(true);
		}
		if ((!options.keyAttack.isDown() || !options.keyUse.isDown()) && mannequinEntity.mannequin$isSevering()) {
			mannequinEntity.mannequin$stopSevering();
			ClientPlayNetworking.send(new MannequinNetworking.UpdateSevering(false, false));
		}
	}

	@Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 16))
	private void mannequin$tryStartSevering(CallbackInfo ci, @Local(name = "bl3") boolean isAttacking, @Share("canSever") LocalBooleanRef canSever) {
		if (canSever.get()) {
			mannequin$tryStartSevering(player);
		}
	}

	@Unique
	private boolean mannequin$tryStartSevering(LocalPlayer player) {
		if (!(player instanceof MannequinEntity mannequinEntity)) {
			return false;
		}
		if (gameMode == null || gameMode.isDestroying() || player.isHandsBusy() || player.isUsingItem()) {
			return false;
		}
		for (var hand : InteractionHand.values()) {
			var stack = player.getItemInHand(hand);
			if (level == null || !stack.isItemEnabled(level.enabledFeatures())) {
				continue;
			}
			var limbToSever = mannequinEntity.mannequin$getLimbs().resolve(player, stack, hand);
			if (limbToSever != null) {
				boolean slim = player.getSkin().model() == PlayerSkin.Model.SLIM;
				mannequinEntity.mannequin$startSevering(limbToSever, 20);
				mannequinEntity.mannequin$setSlim(slim);
				ClientPlayNetworking.send(new MannequinNetworking.UpdateSevering(true, slim));
				return true;
			}
		}
		return false;
	}
}
