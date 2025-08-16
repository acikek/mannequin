package com.acikek.mannequin.mixin;

import com.acikek.mannequin.util.MannequinEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

	@Shadow
	@Final
	protected ServerPlayer player;

	@ModifyExpressionValue(method = "handleBlockBreakAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;mayInteract(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)Z"))
	private boolean mannequin$cancelDestroyBlock(boolean original) {
		return original && (!(player instanceof MannequinEntity mannequinEntity) || !mannequinEntity.mannequin$getLimbs().getArm(player.getMainArm()).severed);
	}
}
