package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.MannequinLimbs;
import com.acikek.mannequin.util.MannequinRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin implements MannequinRenderState {

	@Unique
	private @Nullable MannequinLimbs limbs;

	@Override
	public @Nullable MannequinLimbs mannequin$getLimbs() {
		return limbs;
	}

	@Override
	public void mannequin$setLimbs(MannequinLimbs limbs) {
		this.limbs = limbs;
	}
}
