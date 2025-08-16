package com.acikek.mannequin.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum LimbType implements StringRepresentable {

	LEG("leg"),
	ARM("arm"),
	TORSO("torso");

	public static final Codec<LimbType> CODEC = StringRepresentable.fromEnum(LimbType::values);
	private static final IntFunction<LimbType> BY_ID = ByIdMap.continuous(LimbType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
	public static final StreamCodec<ByteBuf, LimbType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, LimbType::ordinal);

	public final String name;

	LimbType(String name) {
		this.name = name;
	}

	@Override
	public @NotNull String getSerializedName() {
		return name;
	}
}
