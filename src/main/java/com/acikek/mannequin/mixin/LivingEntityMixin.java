package com.acikek.mannequin.mixin;

import com.acikek.mannequin.util.SeveredLimb;
import com.acikek.mannequin.util.SeveringEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements SeveringEntity {

	@Shadow
	public abstract boolean isUsingItem();

	@Shadow
	public abstract void releaseUsingItem();

	@Unique
	private boolean canSever;

	@Unique
	private SeveredLimb severingLimb;

	@Unique
	private boolean severing;

	@Unique
	private int severingTicksRemaining;

	@Unique
	private List<SeveredLimb> severedLimbs;

	@Inject(method = "updatingUsingItem", at = @At("HEAD"))
	private void mannequin$_s(CallbackInfo ci) {
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
		if (severedLimbs == null) {
			severedLimbs = new ArrayList<>();
		}
		severedLimbs.add(severingLimb);
		releaseUsingItem();
		System.out.println(severedLimbs);
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
	public SeveredLimb mannequin$getSeveringLimb() {
		return severingLimb;
	}

	@Override
	public void mannequin$setSeveringLimb(SeveredLimb severingLimb) {
		this.severingLimb = severingLimb;
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

	@Override
	public List<SeveredLimb> mannequin$getSeveredLimbs() {
		return severedLimbs;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void mannequin$addSaveData(ValueOutput valueOutput, CallbackInfo ci) {
		valueOutput.storeNullable("mannequin$severed_limbs", SeveredLimb.LIST_CODEC, severedLimbs);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void mannequin$readSaveData(ValueInput valueInput, CallbackInfo ci) {
		severedLimbs = valueInput.read("mannequin$severed_limbs", SeveredLimb.LIST_CODEC).orElse(null);
		if (severedLimbs != null) {
			severedLimbs = new ArrayList<>(severedLimbs);
		}
	}
}
