package com.acikek.mannequin.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum LimbType implements StringRepresentable {

	LEG("leg"),
	ARM("arm"),
	TORSO("torso");

	public static final Codec<LimbType> CODEC = StringRepresentable.fromEnum(LimbType::values);

	private final String name;

	LimbType(String name) {
		this.name = name;
	}

	@Override
	public @NotNull String getSerializedName() {
		return name;
	}
}
