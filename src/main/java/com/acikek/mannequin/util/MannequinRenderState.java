package com.acikek.mannequin.util;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;

public interface MannequinRenderState {

	@Nullable MannequinLimbs mannequin$getLimbs();

	void mannequin$setLimbs(MannequinLimbs limbs);

	@Nullable GameProfile mannequin$getProfile();

	void mannequin$setProfile(GameProfile profile);
}
