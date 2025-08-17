package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.MannequinEntityData;
import com.acikek.mannequin.util.MannequinLimbs;
import com.acikek.mannequin.util.MannequinRenderState;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin implements MannequinRenderState {

	@Unique
	private @Nullable MannequinEntityData data;

	@Unique
	private @Nullable GameProfile profile;

	@Unique
	private float deltaTime;

	@Override
	public @Nullable MannequinEntityData mannequin$getData() {
		return data;
	}

	@Override
	public void mannequin$setData(MannequinEntityData data) {
		this.data = data;
	}

	@Override
	public @Nullable GameProfile mannequin$getProfile() {
		return profile;
	}

	@Override
	public void mannequin$setProfile(GameProfile profile) {
		this.profile = profile;
	}

	@Override
	public float mannequin$getDeltaTime() {
		return deltaTime;
	}

	@Override
	public void mannequin$setDeltaTime(float deltaTime) {
		this.deltaTime = deltaTime;
	}
}
