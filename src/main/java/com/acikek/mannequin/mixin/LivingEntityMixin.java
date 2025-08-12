package com.acikek.mannequin.mixin;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.util.MannequinLimb;
import com.acikek.mannequin.util.MannequinLimbs;
import com.acikek.mannequin.util.MannequinEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
	public abstract ItemStack getItemInHand(InteractionHand interactionHand);

	@Shadow
	@Nullable
	public abstract AttributeInstance getAttribute(Holder<Attribute> holder);

	@Unique
	private final MannequinLimbs limbs = new MannequinLimbs();

	@Unique
	private boolean severing;

	@Unique
	private @Nullable MannequinLimb severingLimb;

	@Unique
	private @Nullable InteractionHand severingHand;

	@Unique
	private int severingTicksRemaining;

	@Unique
	private boolean slim;

	@Inject(method = "tick", at = @At("HEAD"))
	private void mannequin$tickSevering(CallbackInfo ci) {
		if (!severing) {
			return;
		}
		severingTicksRemaining--;
		if (severingTicksRemaining <= 0) {
			mannequin$sever();
		}
	}

	@Unique
	private void mannequin$sever() {
		if (severingLimb == null) {
			return;
		}
		severingLimb.severed = true;
		if (((LivingEntity) (Object) this) instanceof Player player) {
			var stack = severingLimb.getItemStack(player);
			if (!stack.isEmpty()) {
				player.addItem(stack);
			}
		}
		if (severingHand != null) {
			getItemInHand(severingHand).hurtAndBreak(5, (LivingEntity) (Object) this, severingHand);
		}
		mannequin$stopSevering();
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
	public boolean mannequin$isSevering() {
		return severing;
	}

	@Override
	public void mannequin$setSevering(boolean severing) {
		this.severing = severing;
	}

	@Override
	public MannequinLimb mannequin$getSeveringLimb() {
		return severingLimb;
	}

	@Override
	public void mannequin$setSeveringLimb(MannequinLimb limb) {
		severingLimb = limb;
	}

	@Override
	public @Nullable InteractionHand mannequin$getSeveringHand() {
		return severingHand;
	}

	@Override
	public void mannequin$setSeveringHand(InteractionHand hand) {
		severingHand = hand;
	}

	@Override
	public int mannequin$getSeveringTicksRemaining() {
		return severingTicksRemaining;
	}

	@Override
	public void mannequin$setSeveringTicksRemaining(int ticks) {
		severingTicksRemaining = ticks;
	}

	@Override
	public boolean mannequin$isSlim() {
		return slim;
	}

	@Override
	public void mannequin$setSlim(boolean slim) {
		this.slim = slim;
	}

	@Override
	public void mannequin$startSevering(MannequinLimb limbToSever, InteractionHand hand, int ticks) {
		severing = true;
		severingLimb = limbToSever;
		severingHand = hand;
		severingTicksRemaining = ticks;
		var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
		if (attribute != null) {
			attribute.addTransientModifier(Mannequin.SEVERING_SLOWNESS);
		}
	}

	@Override
	public void mannequin$stopSevering() {
		severing = false;
		severingLimb = null;
		severingHand = null;
		severingTicksRemaining = 0;
		var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
		if (attribute != null) {
			attribute.removeModifier(Mannequin.SEVERING_SLOWNESS);
		}
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
