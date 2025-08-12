package com.acikek.mannequin.util;

import org.jetbrains.annotations.Nullable;

public interface MannequinEntity {

	MannequinLimbs mannequin$getLimbs();

	boolean mannequin$canSever();

	void mannequin$setCanSever(boolean canSever);

	@Nullable MannequinLimb mannequin$getLimbToSever();

	void mannequin$setLimbToSever(MannequinLimb limb);

	boolean mannequin$isSevering();

	void mannequin$setSevering(boolean severing);

	int mannequin$getSeveringTicksRemaining();

	void mannequin$setSeveringTicksRemaining(int ticks);

	boolean mannequin$isSlim();

	void mannequin$setSlim(boolean slim);

	default void mannequin$startSevering(MannequinLimb limbToSever, int ticks) {
		mannequin$setSevering(true);
		mannequin$setLimbToSever(limbToSever);
		mannequin$setSeveringTicksRemaining(ticks);
	}

	default void mannequin$stopSevering() {
		mannequin$setCanSever(false);
		mannequin$setLimbToSever(null);
		mannequin$setSevering(false);
		mannequin$setSeveringTicksRemaining(0);
	}
}
