package com.acikek.mannequin.mixin.client;

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
	private @Nullable MannequinLimbs limbs;

	@Unique
	private @Nullable GameProfile profile;

	@Override
	public @Nullable MannequinLimbs mannequin$getLimbs() {
		return limbs;
	}

	@Override
	public void mannequin$setLimbs(MannequinLimbs limbs) {
		this.limbs = limbs;
	}

	@Override
	public @Nullable GameProfile mannequin$getProfile() {
		return profile;
	}

	@Override
	public void mannequin$setProfile(GameProfile profile) {
		this.profile = profile;
	}
}
