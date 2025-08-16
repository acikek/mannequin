package com.acikek.mannequin.mixin;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.sound.MannequinSounds;
import com.acikek.mannequin.util.MannequinLimb;
import com.acikek.mannequin.util.MannequinLimbs;
import com.acikek.mannequin.util.MannequinEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements MannequinEntity {

	@Shadow
	public abstract ItemStack getItemInHand(InteractionHand interactionHand);

	@Shadow
	@Nullable
	public abstract AttributeInstance getAttribute(Holder<Attribute> holder);

	@Shadow
	public abstract void makeSound(@Nullable SoundEvent soundEvent);

	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> holder);

	@Shadow
	public abstract HumanoidArm getMainArm();

	@Shadow
	@Final
	protected EntityEquipment equipment;

	@Shadow
	@Nullable
	protected abstract ItemEntity createItemStackToDrop(ItemStack itemStack, boolean bl, boolean bl2);

	@Shadow
	public abstract void setItemInHand(InteractionHand interactionHand, ItemStack itemStack);

	@Unique
	private final MannequinLimbs limbs = new MannequinLimbs();

	@Unique
	private boolean severing;

	@Unique
	private boolean doll;

	@Unique
	private @Nullable MannequinLimb severingLimb;

	@Unique
	private @Nullable InteractionHand severingHand;

	@Unique
	private int severingTicksRemaining;

	@Unique
	private int damageTicksElapsed;

	@Unique
	private int ticksToBleed;

	@Unique
	private int totalBleedingTicks;

	@Unique
	private boolean slim;

	@Inject(method = "tick", at = @At("HEAD"))
	private void mannequin$tick(CallbackInfo ci) {
		mannequin$tickDamage();
		if (severing) {
			severingTicksRemaining--;
			if (severingTicksRemaining <= 0) {
				mannequin$sever();
			}
		}
	}

	@Unique
	private void mannequin$tickDamage() {
		if (doll || (!severing && ticksToBleed == 0)) {
			return;
		}
		if (!severing && (ticksToBleed > 0 && damageTicksElapsed >= ticksToBleed)) {
			damageTicksElapsed = 0;
			ticksToBleed = 0;
			return;
		}
		damageTicksElapsed++;
		if (ticksToBleed > 0) {
			totalBleedingTicks++;
			System.out.println(totalBleedingTicks);
			if (totalBleedingTicks >= 20) {
				mannequin$makeDoll();
				System.out.println("doll");
				return;
			}
		}
		if ((severing || !hasEffect(MobEffects.SLOW_FALLING)) && damageTicksElapsed % (severing ? 10 : 20) == 0 && ((LivingEntity) (Object) this).level() instanceof ServerLevel serverLevel) {
			var damageSource = ((LivingEntity) (Object) this).level().damageSources().source(Mannequin.BLEEDING_DAMAGE_TYPE);
			var velocity = ((LivingEntity) (Object) this).getDeltaMovement();
			((LivingEntity) (Object) this).hurtServer(serverLevel, damageSource, severing ? 2.0F : 1.0F);
			((LivingEntity) (Object) this).setDeltaMovement(velocity.multiply(0.3, 0.3, 0.3));
		}
	}

	@Unique
	private void mannequin$sever() {
		if (severingLimb == null || severingHand == null) {
			return;
		}
		ticksToBleed = damageTicksElapsed * 2;
		mannequin$sever(severingLimb, severingHand);
	}

	@Unique
	private void mannequin$makeDoll() {
		damageTicksElapsed = 0;
		totalBleedingTicks = 0;
		ticksToBleed = 0;
		doll = true;
		makeSound(SoundEvents.WITHER_SKELETON_AMBIENT);
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

	@Inject(method = "getItemInHand", at = @At("HEAD"), cancellable = true)
	private void mannequin$getItemInHand(InteractionHand interactionHand, CallbackInfoReturnable<ItemStack> cir) {
		var arm = interactionHand == InteractionHand.MAIN_HAND ? getMainArm() : getMainArm().getOpposite();
		if (limbs.getArm(arm).severed) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}

	@Inject(method = "canUseSlot", at = @At("HEAD"), cancellable = true)
	private void mannequin$canUseSlot(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Boolean> cir) {
		if (mannequin$isSlotSevered(equipmentSlot)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "getItemBySlot", at = @At("HEAD"), cancellable = true)
	private void mannequin$getItemBySlot(EquipmentSlot equipmentSlot, CallbackInfoReturnable<ItemStack> cir) {
		if (mannequin$isSlotSevered(equipmentSlot)) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}

	@Unique
	private boolean mannequin$isSlotSevered(EquipmentSlot equipmentSlot) {
		return (equipmentSlot == EquipmentSlot.MAINHAND && limbs.getArm(getMainArm()).severed)
			|| (equipmentSlot == EquipmentSlot.OFFHAND && limbs.getArm(getMainArm().getOpposite()).severed);
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
	public boolean mannequin$isDoll() {
		return doll;
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
		if (attribute != null && !attribute.hasModifier(Mannequin.SEVERING_SLOWNESS.id())) {
			attribute.addTransientModifier(Mannequin.SEVERING_SLOWNESS);
		}
	}

	@Override
	public void mannequin$stopSevering() {
		severing = false;
		severingLimb = null;
		severingHand = null;
		severingTicksRemaining = 0;
		damageTicksElapsed = 0;
		var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
		if (attribute != null) {
			attribute.removeModifier(Mannequin.SEVERING_SLOWNESS);
		}
	}

	@Override
	public void mannequin$sever(MannequinLimb limb, InteractionHand hand) {
		limb.severed = true;
		if (((LivingEntity) (Object) this) instanceof Player player) {
			var limbStack = limb.getLimbItemStack(player);
			if (!limbStack.isEmpty()) {
				player.addItem(limbStack);
			}
			var severedHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
			var heldStack = mannequin$getItemInHand(severedHand);
			if (!heldStack.isEmpty()) {
				var heldDrop = createItemStackToDrop(heldStack, true, false);
				if (heldDrop != null) {
					player.level().addFreshEntity(heldDrop);
				}
			}
			setItemInHand(severedHand, ItemStack.EMPTY);
		}
		getItemInHand(hand).hurtAndBreak(5, (LivingEntity) (Object) this, hand);
		mannequin$stopSevering();
		((LivingEntity) (Object) this).refreshDimensions();
		makeSound(MannequinSounds.LIMB_SNAP);
	}

	@Override
	public void mannequin$attach(MannequinLimb limb, @Nullable ResolvableProfile profile) {
		limb.severed = false;
		((LivingEntity) (Object) this).refreshDimensions();
		makeSound(SoundEvents.WOOD_PLACE);
		if (((LivingEntity) (Object) this).level().isClientSide() && (profile == null || !(((LivingEntity) (Object) this) instanceof Player player) || !profile.gameProfile().getId().equals(player.getUUID()))) {
			limb.setSkin(profile);
		}
	}

	@Override
	public ItemStack mannequin$getItemBySlot(EquipmentSlot slot) {
		return equipment.get(slot);
	}

	@Override
	public ItemStack mannequin$getItemInHand(InteractionHand hand) {
		return mannequin$getItemBySlot( hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
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
