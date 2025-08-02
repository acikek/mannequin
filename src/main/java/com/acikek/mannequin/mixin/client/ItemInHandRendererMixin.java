package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.client.MannequinClient;
import com.acikek.mannequin.util.SeveringEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

	@Inject(method = "itemUsed", at = @At("HEAD"), cancellable = true)
	private void mannequin$cancelItemUsedAnimation(InteractionHand interactionHand, CallbackInfo ci) {
		if (Minecraft.getInstance().player instanceof SeveringEntity severingEntity && severingEntity.mannequin$canSever()) {
			ci.cancel();
		}
	}
}
