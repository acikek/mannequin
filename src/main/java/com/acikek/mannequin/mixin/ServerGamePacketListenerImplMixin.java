package com.acikek.mannequin.mixin;

import com.acikek.mannequin.util.MannequinEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

	@Shadow
	public ServerPlayer player;

	@ModifyExpressionValue(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z", ordinal = 0))
	private boolean mannequin$cancelSwap(boolean original) {
		return original || (player instanceof MannequinEntity mannequinEntity && mannequinEntity.mannequin$getData().limbs.getArm(player.getMainArm().getOpposite()).severed);
	}

	@ModifyExpressionValue(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
	private ItemStack mannequin$overrideOffHand(ItemStack original) {
		return player instanceof MannequinEntity mannequinEntity ? mannequinEntity.mannequin$getItemInHand(InteractionHand.OFF_HAND) : original;
	}

	@ModifyExpressionValue(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 1))
	private ItemStack mannequin$overrideMainHand(ItemStack original) {
		return player instanceof MannequinEntity mannequinEntity ? mannequinEntity.mannequin$getItemInHand(InteractionHand.MAIN_HAND) : original;
	}
}
