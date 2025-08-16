package com.acikek.mannequin.util;

import com.acikek.mannequin.item.MannequinItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record MannequinLimbs(MannequinLimb leftLeg, MannequinLimb rightLeg, MannequinLimb leftArm, MannequinLimb rightArm, MannequinLimb torso) {

	public MannequinLimbs() {
		this(
			new MannequinLimb(LimbType.LEG, LimbOrientation.LEFT),
			new MannequinLimb(LimbType.LEG, LimbOrientation.RIGHT),
			new MannequinLimb(LimbType.ARM, LimbOrientation.LEFT),
			new MannequinLimb(LimbType.ARM, LimbOrientation.RIGHT),
			new MannequinLimb(LimbType.TORSO, LimbOrientation.NONE)
		);
	}

	public MannequinLimb resolve(LivingEntity entity, ItemStack stack, InteractionHand hand) {
		var right = hand == InteractionHand.MAIN_HAND;
		if (entity.getMainArm() == HumanoidArm.LEFT) {
			right = !right;
		}
		if (stack.is(MannequinItems.SEVERS_LEGS)) {
			return right ? leftLeg : rightLeg;
		}
		if (stack.is(MannequinItems.SEVERS_ARMS)) {
			return right ? leftArm : rightArm;
		}
		return null; // TODO
	}

	public MannequinLimb resolve(LimbType limbType, LimbOrientation limbOrientation) {
		return switch (limbType) {
			case LEG -> switch (limbOrientation) {
				case LEFT -> leftLeg;
				case RIGHT -> rightLeg;
				default -> throw new IllegalStateException();
			};
			case ARM -> switch (limbOrientation) {
				case LEFT -> leftArm;
				case RIGHT -> rightArm;
				default -> throw new IllegalStateException();
			};
			case TORSO -> torso;
		};
	}
}
