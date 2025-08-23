package com.acikek.mannequin.util;

import com.acikek.mannequin.item.MannequinItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.List;
import java.util.Optional;

public record MannequinLimbs(MannequinLimb leftLeg, MannequinLimb rightLeg, MannequinLimb leftArm, MannequinLimb rightArm, MannequinLimb torso) {

	public static final Codec<MannequinLimbs> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			MannequinLimb.CODEC.fieldOf("leftLeg").forGetter(MannequinLimbs::leftLeg),
			MannequinLimb.CODEC.fieldOf("rightLeg").forGetter(MannequinLimbs::rightLeg),
			MannequinLimb.CODEC.fieldOf("leftArm").forGetter(MannequinLimbs::leftArm),
			MannequinLimb.CODEC.fieldOf("rightArm").forGetter(MannequinLimbs::rightArm),
			MannequinLimb.CODEC.fieldOf("torso").forGetter(MannequinLimbs::torso)
		).apply(instance, MannequinLimbs::new)
	);

	public static final StreamCodec<FriendlyByteBuf, MannequinLimbs> STREAM_CODEC = StreamCodec.composite(
		MannequinLimb.STREAM_CODEC, MannequinLimbs::leftLeg,
		MannequinLimb.STREAM_CODEC, MannequinLimbs::rightLeg,
		MannequinLimb.STREAM_CODEC, MannequinLimbs::leftArm,
		MannequinLimb.STREAM_CODEC, MannequinLimbs::rightArm,
		MannequinLimb.STREAM_CODEC, MannequinLimbs::torso,
		MannequinLimbs::new
	);

	public MannequinLimbs() {
		this(
			new MannequinLimb(LimbType.LEG, LimbOrientation.LEFT),
			new MannequinLimb(LimbType.LEG, LimbOrientation.RIGHT),
			new MannequinLimb(LimbType.ARM, LimbOrientation.LEFT),
			new MannequinLimb(LimbType.ARM, LimbOrientation.RIGHT),
			new MannequinLimb(LimbType.TORSO, LimbOrientation.NONE)
		);
	}

	public void setProfile(Optional<ResolvableProfile> profile) {
		leftLeg.profile = profile;
		rightLeg.profile = profile;
		leftArm.profile = profile;
		rightArm.profile = profile;
		torso.profile = profile;
	}

	public List<MannequinLimb> getParts() {
		return List.of(leftLeg, rightLeg, leftArm, rightArm, torso);
	}

	public MannequinLimb resolve(LivingEntity entity, ItemStack stack, InteractionHand hand) {
		var right = hand == InteractionHand.MAIN_HAND;
		if (entity.getMainArm() == HumanoidArm.LEFT) {
			right = !right;
		}
		if (stack.is(MannequinItems.SEVERS_HEADS) && leftLeg.severed && rightLeg.severed
				&& entity instanceof MannequinEntity mannequinEntity
				&& mannequinEntity.mannequin$getData() != null && mannequinEntity.mannequin$getData().doll) {
			return torso;
		}
		if (stack.is(MannequinItems.SEVERS_LEGS)) {
			return right ? leftLeg : rightLeg;
		}
		if (stack.is(MannequinItems.SEVERS_ARMS)) {
			return right ? leftArm : rightArm;
		}
		return null;
	}

	public MannequinLimb resolve(LimbType limbType, LimbOrientation limbOrientation) {
		return switch (limbType) {
			case LEG -> switch (limbOrientation) {
				case LEFT -> leftLeg;
				case RIGHT -> rightLeg;
				default -> null;
			};
			case ARM -> switch (limbOrientation) {
				case LEFT -> leftArm;
				case RIGHT -> rightArm;
				default -> null;
			};
			case TORSO -> torso;
		};
	}

	public MannequinLimb getArm(HumanoidArm arm) {
		return arm == HumanoidArm.RIGHT ? rightArm : leftArm;
	}
}
