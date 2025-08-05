package com.acikek.mannequin.util;

import com.acikek.mannequin.Mannequin;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record MannequinLimbs(MannequinLimb leftLeg, MannequinLimb rightLeg, MannequinLimb leftArm, MannequinLimb rightArm, MannequinLimb torso) {

	public MannequinLimbs() {
		this(new MannequinLimb(), new MannequinLimb(), new MannequinLimb(), new MannequinLimb(), new MannequinLimb());
	}

	public MannequinLimb resolve(LivingEntity entity, ItemStack stack, InteractionHand hand) {
		var right = hand == InteractionHand.MAIN_HAND;
		if (entity.getMainArm() == HumanoidArm.LEFT) {
			right = !right;
		}
		if (stack.is(ItemTags.AXES)) {
			return right ? leftLeg : rightLeg;
		}
		if (stack.is(ItemTags.SWORDS)) {
			return right ? leftArm : rightArm;
		}
		return torso; // TODO
	}

	// TODO
	public ItemStack createLimbStack(MannequinLimb limb) {
		if (limb == leftLeg || limb == rightLeg) {
			return Mannequin.LEG_ITEM.getDefaultInstance();
		}
		if (limb == leftArm || limb == rightArm) {
			return Mannequin.ARM_ITEM.getDefaultInstance();
		}
		return ItemStack.EMPTY;
	}
}
