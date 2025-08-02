package com.acikek.mannequin.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum SeveredLimb implements StringRepresentable {

	LEFT_LEG("left_leg"),
	RIGHT_LEG("right_leg"),
	LEFT_ARM("left_arm"),
	RIGHT_ARM("right_arm"),
	TORSO("torso");

	private final String name;

	public static final Codec<SeveredLimb> CODEC = StringRepresentable.fromValues(SeveredLimb::values);
	public static final Codec<List<SeveredLimb>> LIST_CODEC = Codec.list(CODEC);

	SeveredLimb(String name) {
		this.name = name;
	}

	@Override
	public @NotNull String getSerializedName() {
		return name;
	}

	public static SeveredLimb resolve(LivingEntity entity, ItemStack stack, InteractionHand hand) {
		var item = stack.getItem();
		var right = hand == InteractionHand.MAIN_HAND;
		if (entity.getMainArm() == HumanoidArm.LEFT) {
			right = !right;
		}
		if (item instanceof AxeItem) {
			return right ? SeveredLimb.LEFT_LEG : SeveredLimb.RIGHT_LEG;
		}
		if (/*item is sword*/ true) {
			return right ? SeveredLimb.LEFT_ARM : SeveredLimb.RIGHT_ARM;
		}
		// TODO: torso
		return null;
	}
}
