package com.acikek.mannequin.util;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;

public interface MannequinRenderState {

	@Nullable MannequinEntityData mannequin$getData();

	void mannequin$setData(MannequinEntityData data);

	@Nullable GameProfile mannequin$getProfile();

	void mannequin$setProfile(GameProfile profile);

	float mannequin$getDeltaTime();

	void mannequin$setDeltaTime(float deltaTime);
}
