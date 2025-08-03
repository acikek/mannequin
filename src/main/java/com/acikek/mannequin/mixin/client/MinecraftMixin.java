package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.network.MannequinNetworking;
import com.acikek.mannequin.util.MannequinEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

	@Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 11))
	private void mannequin$inputSever(CallbackInfo ci) {
		if (!(player instanceof MannequinEntity mannequinEntity)) {
			return;
		}
		if (options.keyAttack.isDown() && !mannequinEntity.mannequin$isSevering() && mannequinEntity.mannequin$getLimbToSever() != null) {
			mannequinEntity.mannequin$startSevering(20);
			ClientPlayNetworking.send(new MannequinNetworking.UpdateSevering(true));
		}
		if (!options.keyAttack.isDown() && mannequinEntity.mannequin$isSevering()) {
			player.releaseUsingItem();
			ClientPlayNetworking.send(new MannequinNetworking.UpdateSevering(false));
		}
	}
}
