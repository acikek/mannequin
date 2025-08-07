package com.acikek.mannequin.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum LimbOrientation implements StringRepresentable {

	NONE("none"),
	LEFT("left"),
	RIGHT("right");

	public static final Codec<LimbOrientation> CODEC = StringRepresentable.fromEnum(LimbOrientation::values);
	private static final IntFunction<LimbOrientation> BY_ID = ByIdMap.continuous(LimbOrientation::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
	public static final StreamCodec<ByteBuf, LimbOrientation> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, LimbOrientation::ordinal);

	public static final DataComponentType<LimbOrientation> DATA_COMPONENT_TYPE = DataComponentType.<LimbOrientation>builder()
		.persistent(CODEC)
		.networkSynchronized(STREAM_CODEC)
		.cacheEncoding()
		.build();

	public final String name;
	public final Component component;

	LimbOrientation(String name) {
		this.name = name;
		component = Component.translatable("limbOrientation.mannequin." + name);
	}

	@Override
	public @NotNull String getSerializedName() {
		return name;
	}
}
