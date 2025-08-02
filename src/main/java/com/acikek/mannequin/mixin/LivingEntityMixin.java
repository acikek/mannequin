package com.acikek.mannequin.mixin;

import com.acikek.mannequin.util.SeveringEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements SeveringEntity {

	@Shadow
	public abstract boolean isUsingItem();

	@Shadow
	public abstract void releaseUsingItem();

	@Unique
	private boolean canSever;

	@Unique
	private boolean severing;

	@Unique
	private int severingTicksRemaining;

	@Inject(method = "updatingUsingItem", at = @At("HEAD"))
	private void mannequin$_s(CallbackInfo ci) {
		if (!isUsingItem() || !severing) {
			return;
		}
		severingTicksRemaining--;
		System.out.println(severingTicksRemaining);
		if (severingTicksRemaining <= 0) {
			severing = false;
			severingTicksRemaining = 0;
			releaseUsingItem();
			System.out.println("severed");
		}
	}

	@Inject(method = "stopUsingItem", at = @At("HEAD"))
	private void mannequin$_stop(CallbackInfo ci) {
		mannequin$stopSevering();
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
}
