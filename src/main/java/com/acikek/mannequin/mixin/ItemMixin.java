package com.acikek.mannequin.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void mannequin$_a(ItemStack itemStack, LivingEntity livingEntity, CallbackInfoReturnable<Integer> cir) {
		if (((Item) (Object) this) instanceof AxeItem) {
			cir.setReturnValue(72000);
		}
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void mannequin$_b(ItemStack itemStack, CallbackInfoReturnable<ItemUseAnimation> cir) {
		if (((Item) (Object) this) instanceof AxeItem) {
			cir.setReturnValue(ItemUseAnimation.NONE);
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void mannequin$_c(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
		if (((Item) (Object) this) instanceof AxeItem) {
			player.startUsingItem(interactionHand);
			cir.setReturnValue(InteractionResult.SUCCESS);
		}
	}
}
