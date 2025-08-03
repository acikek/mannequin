package com.acikek.mannequin.util;

import net.minecraft.client.resources.PlayerSkin;
import org.jetbrains.annotations.Nullable;

public class MannequinLimb {

	public boolean severed;
	public @Nullable PlayerSkin skin;

	public MannequinLimb(boolean severed, @Nullable PlayerSkin skin) {
		this.severed = severed;
		this.skin = skin;
	}

	public MannequinLimb() {
		this(false, null);
	}
}
