package com.acikek.mannequin.util;

import com.acikek.mannequin.item.LimbItem;
import com.acikek.mannequin.item.MannequinItems;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.Optional;

public class MannequinLimb {

	public static final Codec<MannequinLimb> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			LimbType.CODEC.fieldOf("type").forGetter(limb -> limb.type),
			LimbOrientation.CODEC.fieldOf("orientation").forGetter(limb -> limb.orientation),
			Codec.BOOL.fieldOf("severed").forGetter(limb -> limb.severed),
			ResolvableProfile.CODEC.optionalFieldOf("profile").forGetter(limb -> limb.profile)
		).apply(instance, MannequinLimb::new)
	);

	public static final StreamCodec<FriendlyByteBuf, MannequinLimb> STREAM_CODEC = StreamCodec.composite(
		LimbType.STREAM_CODEC, limb -> limb.type,
		LimbOrientation.STREAM_CODEC, limb -> limb.orientation,
		ByteBufCodecs.BOOL, limb -> limb.severed,
		ByteBufCodecs.optional(ResolvableProfile.STREAM_CODEC), limb -> limb.profile,
		MannequinLimb::new
	);

	public final LimbType type;
	public final LimbOrientation orientation;

	public boolean severed;
	public Optional<ResolvableProfile> profile;

	public MannequinLimb(LimbType type, LimbOrientation orientation, boolean severed, Optional<ResolvableProfile> profile) {
		this.type = type;
		this.orientation = orientation;
		this.severed = severed;
		this.profile = profile;
	}

	public MannequinLimb(LimbType type, LimbOrientation orientation) {
		this(type, orientation, false, Optional.empty());
	}

	public ItemStack getLimbItemStack(Player player) {
		if (type == LimbType.TORSO) {
			return ItemStack.EMPTY;
		}
		var stack = (type == LimbType.LEG ? MannequinItems.LEG : MannequinItems.ARM).getDefaultInstance();
		stack.set(LimbOrientation.DATA_COMPONENT_TYPE, orientation);
		stack.set(DataComponents.PROFILE, profile.orElseGet(() -> new ResolvableProfile(player.getGameProfile())));
		if (player instanceof MannequinEntity mannequinEntity) {
			stack.set(LimbItem.SLIM_COMPONENT_TYPE, mannequinEntity.mannequin$getData().slim);
		}
		return stack;
	}

	public int getSeveringTicks(ItemStack stack) {
		boolean leg = type == LimbType.LEG;
		if (stack.is(MannequinItems.SEVER_TIER_1)) {
			return leg ? 160 : 120;
		}
		if (stack.is(MannequinItems.SEVER_TIER_2)) {
			return leg ? 100 : 80;
		}
		if (stack.is(MannequinItems.SEVER_TIER_3)) {
			return 50;
		}
		return -1;
	}

	public boolean isBaseVisible(GameProfile profile) {
		return !severed && (this.profile.isEmpty() || this.profile.get().gameProfile().equals(profile));
	}

	@Override
	public String toString() {
		return "MannequinLimb{" +
			"type=" + type +
			", orientation=" + orientation +
			", severed=" + severed +
			", profile=" + profile.flatMap(ResolvableProfile::name).orElse("<none>") +
			'}';
	}
}
