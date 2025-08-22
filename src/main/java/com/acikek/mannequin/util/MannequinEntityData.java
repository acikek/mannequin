package com.acikek.mannequin.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MannequinEntityData {

	public MannequinLimbs limbs;
	public boolean severing;
	public boolean doll;
	public @Nullable MannequinLimb severingLimb;
	public @Nullable InteractionHand severingHand;
	public int severingTicksRemaining;
	public int severingTicksElapsed;
	public int damageTicksElapsed;
	public int ticksToBleed;
	public int totalBleedingTicks;
	public boolean slim;

	public MannequinEntityData(MannequinLimbs limbs, boolean severing, boolean doll, Optional<Integer> severingLimbIndex, Optional<Boolean> severingHandMain, int severingTicksRemaining, int severingTicksElapsed, int damageTicksElapsed, int ticksToBleed, int totalBleedingTicks, boolean slim) {
		this.limbs = limbs;
		this.severing = severing;
		this.doll = doll;
		severingLimb = severingLimbIndex.map(index -> limbs.getParts().get(index)).orElse(null);
		severingHand = severingHandMain.map(main -> main ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND).orElse(null);
		this.severingTicksRemaining = severingTicksRemaining;
		this.damageTicksElapsed = damageTicksElapsed;
		this.ticksToBleed = ticksToBleed;
		this.totalBleedingTicks = totalBleedingTicks;
		this.slim = slim;
	}

	public MannequinEntityData() {
		this(new MannequinLimbs(), false, false, Optional.empty(), Optional.empty(), 0, 0, 0, 0, 0, false);
	}

	public Optional<Integer> getSeveringLimbIndex() {
		return Optional.ofNullable(severingLimb).map(limb -> limbs.getParts().indexOf(limb));
	}

	public Optional<Boolean> getSeveringHandMain() {
		return Optional.ofNullable(severingHand).map(hand -> hand == InteractionHand.MAIN_HAND);
	}

	public static final Codec<MannequinEntityData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			MannequinLimbs.CODEC.fieldOf("limbs").forGetter(data -> data.limbs),
			Codec.BOOL.fieldOf("severing").forGetter(data -> data.severing),
			Codec.BOOL.fieldOf("doll").forGetter(data -> data.doll),
			Codec.INT.optionalFieldOf("severing_limb_index").forGetter(MannequinEntityData::getSeveringLimbIndex),
			Codec.BOOL.optionalFieldOf("severing_hand_main").forGetter(MannequinEntityData::getSeveringHandMain),
			Codec.INT.fieldOf("severing_ticks_remaining").forGetter(data -> data.severingTicksRemaining),
			Codec.INT.fieldOf("severing_ticks_elapsed").forGetter(data -> data.severingTicksElapsed),
			Codec.INT.fieldOf("damage_ticks_remaining").forGetter(data -> data.damageTicksElapsed),
			Codec.INT.fieldOf("ticks_to_bleed").forGetter(data -> data.ticksToBleed),
			Codec.INT.fieldOf("total_bleeding_ticks").forGetter(data -> data.totalBleedingTicks),
			Codec.BOOL.fieldOf("slim").forGetter(data -> data.slim)
		).apply(instance, MannequinEntityData::new)
	);

	public static final StreamCodec<FriendlyByteBuf, MannequinEntityData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC).cast();

	@Override
	public String toString() {
		return "MannequinEntityData{" +
			"limbs=" + limbs +
			", severing=" + severing +
			", doll=" + doll +
			", severingLimb=" + severingLimb +
			", severingHand=" + severingHand +
			", severingTicksRemaining=" + severingTicksRemaining +
			", severingTicksElapsed=" + severingTicksElapsed +
			", damageTicksElapsed=" + damageTicksElapsed +
			", ticksToBleed=" + ticksToBleed +
			", totalBleedingTicks=" + totalBleedingTicks +
			", slim=" + slim +
			'}';
	}
}
