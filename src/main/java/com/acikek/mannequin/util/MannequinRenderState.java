package com.acikek.mannequin.util;

import org.jetbrains.annotations.Nullable;

public interface MannequinRenderState {

	@Nullable MannequinLimbs mannequin$getLimbs();

	void mannequin$setLimbs(MannequinLimbs limbs);
}
