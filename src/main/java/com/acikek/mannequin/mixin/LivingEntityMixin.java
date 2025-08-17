package com.acikek.mannequin.mixin;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.network.MannequinNetworking;
import com.acikek.mannequin.sound.MannequinSounds;
import com.acikek.mannequin.util.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

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

	@Shadow
	public abstract void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack);

	@Unique
	private MannequinEntityData data = new MannequinEntityData();

	@Unique
	private boolean dirty;

	@Inject(method = "tick", at = @At("HEAD"))
	private void mannequin$tick(CallbackInfo ci) {
		//mannequin$tickData();
		mannequin$tickDamage();
		if (data.severing) {
			data.severingTicksRemaining--;
			if (data.severingTicksRemaining <= 0) {
				mannequin$sever();
			}
		}
	}

	private void mannequin$tickData() {
		if (!dirty || !(((LivingEntity) (Object) this) instanceof ServerPlayer serverPlayer)) {
			return;
		}
		ServerPlayNetworking.send(serverPlayer, new MannequinNetworking.UpdateMannequinEntityData(OptionalInt.empty(), data));
		var watcherPayload = new MannequinNetworking.UpdateMannequinEntityData(OptionalInt.of(serverPlayer.getId()), data);
		for (var watcher : PlayerLookup.tracking(serverPlayer)) {
			ServerPlayNetworking.send(watcher, watcherPayload);
		}
		dirty = false;
	}

	@Unique
	private void mannequin$tickDamage() {
		if (data.doll || (!data.severing && data.ticksToBleed == 0)) {
			return;
		}
		if (!data.severing && (data.ticksToBleed > 0 && data.damageTicksElapsed >= data.ticksToBleed)) {
			data.damageTicksElapsed = 0;
			data.ticksToBleed = 0;
			return;
		}
		data.damageTicksElapsed++;
		if (data.ticksToBleed > 0) {
			data.totalBleedingTicks++;
			if (data.totalBleedingTicks >= 600) {
				mannequin$makeDoll();
				return;
			}
		}
		if ((data.severing || !hasEffect(MobEffects.SLOW_FALLING)) && data.damageTicksElapsed % (data.severing ? 10 : 20) == 0 && ((LivingEntity) (Object) this).level() instanceof ServerLevel serverLevel) {
			var damageSource = ((LivingEntity) (Object) this).level().damageSources().source(Mannequin.BLEEDING_DAMAGE_TYPE);
			var velocity = ((LivingEntity) (Object) this).getDeltaMovement();
			((LivingEntity) (Object) this).hurtServer(serverLevel, damageSource, data.severing ? 2.0F : 1.0F);
			((LivingEntity) (Object) this).setDeltaMovement(velocity.multiply(0.3, 0.3, 0.3));
		}
	}

	@Unique
	private void mannequin$sever() {
		if (data.severingLimb == null || data.severingHand == null) {
			return;
		}
		data.ticksToBleed = data.damageTicksElapsed * 2;
		mannequin$sever(data.severingLimb, data.severingHand);
	}

	@ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
	private EntityDimensions mannequin$resize(EntityDimensions original) {
		if (data.limbs.torso().severed) {
			return Mannequin.HEAD_ONLY_DIMENSIONS;
		}
		if (data.limbs.leftLeg().severed && data.limbs.rightLeg().severed) {
			return Mannequin.LEGLESS_DIMENSIONS;
		}
		return original;
	}

	@Inject(method = "getItemInHand", at = @At("HEAD"), cancellable = true)
	private void mannequin$getItemInHand(InteractionHand interactionHand, CallbackInfoReturnable<ItemStack> cir) {
		var arm = interactionHand == InteractionHand.MAIN_HAND ? getMainArm() : getMainArm().getOpposite();
		if (data.limbs.getArm(arm).severed) {
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
		if (data.limbs.torso().severed) {
			return equipmentSlot != EquipmentSlot.HEAD;
		}
		return ((equipmentSlot == EquipmentSlot.FEET || equipmentSlot == EquipmentSlot.LEGS) && data.limbs.leftLeg().severed && data.limbs.rightLeg().severed)
			|| (equipmentSlot == EquipmentSlot.MAINHAND && data.limbs.getArm(getMainArm()).severed)
			|| (equipmentSlot == EquipmentSlot.OFFHAND && data.limbs.getArm(getMainArm().getOpposite()).severed);
	}

	@Override
	public MannequinEntityData mannequin$getData() {
		return data;
	}

	@Override
	public void mannequin$setData(MannequinEntityData data) {
		this.data = data;
	}

	@Override
	public void mannequin$startSevering(MannequinLimb limbToSever, InteractionHand hand, int ticks) {
		data.severing = true;
		data.severingLimb = limbToSever;
		data.severingHand = hand;
		data.severingTicksRemaining = ticks;
		var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
		if (attribute != null && !attribute.hasModifier(Mannequin.SEVERING_SLOWNESS.id())) {
			attribute.addTransientModifier(Mannequin.SEVERING_SLOWNESS);
		}
	}

	@Override
	public void mannequin$stopSevering() {
		data.severing = false;
		data.severingLimb = null;
		data.severingHand = null;
		data.severingTicksRemaining = 0;
		data.damageTicksElapsed = 0;
		var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
		if (attribute != null) {
			attribute.removeModifier(Mannequin.SEVERING_SLOWNESS);
		}
	}

	@Override
	public void mannequin$sever(MannequinLimb limb, InteractionHand hand) {
		if (!(((LivingEntity) (Object) this) instanceof Player player)) {
			return;
		}
		limb.severed = true;
		var severedHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		List<ItemStack> drop = new ArrayList<>();
		if (limb.type == LimbType.ARM) {
			drop.add(mannequin$getItemInHand(severedHand));
			setItemInHand(severedHand, ItemStack.EMPTY);
		}
		if (limb.type == LimbType.LEG && data.limbs.leftLeg().severed && data.limbs.rightLeg().severed) {
			drop.add(mannequin$getItemBySlot(EquipmentSlot.FEET));
			drop.add(mannequin$getItemBySlot(EquipmentSlot.LEGS));
			setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
			setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
		}
		else if (limb.type == LimbType.TORSO) {
			if (!data.limbs.leftArm().severed) {
				mannequin$severTorsoArm(player, data.limbs.leftArm(), drop);
			}
			if (!data.limbs.rightArm().severed) {
				mannequin$severTorsoArm(player, data.limbs.rightArm(), drop);
			}
			drop.add(mannequin$getItemBySlot(EquipmentSlot.CHEST));
			setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
		}
		for (var stack : drop) {
			if (!stack.isEmpty()) {
				var entity = createItemStackToDrop(stack, false, false);
				if (entity != null) {
					player.level().addFreshEntity(entity);
				}
			}
		}
		var limbStack = limb.getLimbItemStack(player);
		if (!limbStack.isEmpty()) {
			player.addItem(limbStack);
		}
		getItemInHand(hand).hurtAndBreak(5, (LivingEntity) (Object) this, hand);
		mannequin$stopSevering();
		((LivingEntity) (Object) this).refreshDimensions();
		makeSound(MannequinSounds.LIMB_SNAP);
	}

	@Unique
	private void mannequin$severTorsoArm(Player player, MannequinLimb armLimb, List<ItemStack> drop) {
		armLimb.severed = true;
		var hand = getMainArm() == (armLimb.orientation == LimbOrientation.RIGHT ? HumanoidArm.RIGHT : HumanoidArm.LEFT) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		drop.add(armLimb.getLimbItemStack(player));
		drop.add(mannequin$getItemInHand(hand));
		setItemInHand(hand, ItemStack.EMPTY);
	}

	@Override
	public void mannequin$attach(MannequinLimb limb, @Nullable ResolvableProfile profile) {
		limb.severed = false;
		limb.profile = Optional.ofNullable(profile);
		((LivingEntity) (Object) this).refreshDimensions();
		makeSound(SoundEvents.WOOD_PLACE);
		if (((LivingEntity) (Object) this) instanceof ServerPlayer serverPlayer) {
			var watcherPayload = new MannequinNetworking.UpdateLimb(serverPlayer.getId(), false, limb);
			for (var watcher : PlayerLookup.tracking(serverPlayer)) {
				ServerPlayNetworking.send(watcher, watcherPayload);
			}
		}
	}

	@Override
	public void mannequin$makeDoll() {
		data.damageTicksElapsed = 0;
		data.totalBleedingTicks = 0;
		data.ticksToBleed = 0;
		data.doll = true;
		makeSound(SoundEvents.WITHER_SKELETON_AMBIENT);
		if (((LivingEntity) (Object) this) instanceof ServerPlayer serverPlayer) {
			ServerPlayNetworking.send(serverPlayer, new MannequinNetworking.UpdateDoll(OptionalInt.empty(), true));
			var watcherPayload = new MannequinNetworking.UpdateDoll(OptionalInt.of(serverPlayer.getId()), true);
			for (var watcher : PlayerLookup.tracking(serverPlayer)) {
				ServerPlayNetworking.send(watcher, watcherPayload);
			}
		}
	}

	@Override
	public ItemStack mannequin$getItemBySlot(EquipmentSlot slot) {
		return equipment.get(slot);
	}

	@Override
	public ItemStack mannequin$getItemInHand(InteractionHand hand) {
		return mannequin$getItemBySlot(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void mannequin$addSaveData(ValueOutput valueOutput, CallbackInfo ci) {
		if (((LivingEntity) (Object) this) instanceof Player) {
			valueOutput.store("mannequin$entity_data", MannequinEntityData.CODEC, data);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void mannequin$readSaveData(ValueInput valueInput, CallbackInfo ci) {
		if (((LivingEntity) (Object) this) instanceof Player) {
			valueInput.read("mannequin$entity_data", MannequinEntityData.CODEC).ifPresent(data -> {
				this.data = data;
				dirty = true;
			});
		}
	}
}
