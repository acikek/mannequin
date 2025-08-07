package com.acikek.mannequin.mixin;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.util.MannequinLimb;
import com.acikek.mannequin.util.MannequinLimbs;
import com.acikek.mannequin.util.MannequinEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements MannequinEntity {

	@Shadow
	public abstract boolean isUsingItem();

	@Shadow
	public abstract void releaseUsingItem();

	@Shadow
	public abstract ItemStack getUseItem();

	@Shadow
	public abstract InteractionHand getUsedItemHand();

	@Unique
	private final MannequinLimbs limbs = new MannequinLimbs();

	@Unique
	private boolean canSever;

	@Unique
	private @Nullable MannequinLimb limbToSever;

	@Unique
	private boolean severing;

	@Unique
	private int severingTicksRemaining;

	@Inject(method = "updatingUsingItem", at = @At("HEAD"))
	private void mannequin$tickSevering(CallbackInfo ci) {
		if (!isUsingItem() || !severing) {
			return;
		}
		severingTicksRemaining--;
		if (severingTicksRemaining <= 0) {
			mannequin$sever();
		}
	}

	@Unique
	private void mannequin$sever() {
		if (limbToSever == null) {
			return;
		}
		limbToSever.severed = true;
		var stack = limbs.createLimbStack(limbToSever);
		if (!stack.isEmpty() && ((LivingEntity) (Object) this) instanceof Player player) {
			player.addItem(stack);
		}
		getUseItem().hurtAndBreak(5, (LivingEntity) (Object) this, getUsedItemHand());
		releaseUsingItem();
		((LivingEntity) (Object) this).refreshDimensions();
	}

	@Inject(method = "stopUsingItem", at = @At("HEAD"))
	private void mannequin$cancelSevering(CallbackInfo ci) {
		mannequin$stopSevering();
	}

	@ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
	private EntityDimensions mannequin$resize(EntityDimensions original) {
		if (limbs.leftLeg().severed && limbs.rightLeg().severed) {
			return Mannequin.LEGLESS_DIMENSIONS;
		}
		return original;
	}

	@Override
	public MannequinLimbs mannequin$getLimbs() {
		return limbs;
	}

	@Override
	public boolean mannequin$canSever() {
		return canSever;
	}

	@Override
	public void mannequin$setCanSever(boolean canSever) {
		this.canSever = canSever;
	}

	@Override
	public MannequinLimb mannequin$getLimbToSever() {
		return limbToSever;
	}

	@Override
	public void mannequin$setLimbToSever(MannequinLimb limb) {
		limbToSever = limb;
	}

	@Override
	public boolean mannequin$isSevering() {
		return severing;
	}

	@Override
	public void mannequin$setSevering(boolean severing) {
		this.severing = severing;
	}

	@Override
	public int mannequin$getSeveringTicksRemaining() {
		return severingTicksRemaining;
	}

	@Override
	public void mannequin$setSeveringTicksRemaining(int ticks) {
		severingTicksRemaining = ticks;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void mannequin$addSaveData(ValueOutput valueOutput, CallbackInfo ci) {
		//valueOutput.storeNullable("mannequin$severed_limbs", SeveredLimb.LIST_CODEC, severedLimbs);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void mannequin$readSaveData(ValueInput valueInput, CallbackInfo ci) {
		/*severedLimbs = valueInput.read("mannequin$severed_limbs", SeveredLimb.LIST_CODEC).orElse(null);
		if (severedLimbs != null) {
			severedLimbs = new ArrayList<>(severedLimbs);
		}*/
	}
}
