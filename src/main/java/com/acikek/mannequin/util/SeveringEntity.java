package com.acikek.mannequin.util;

public interface SeveringEntity {

	boolean mannequin$isSevering();

	void mannequin$setSevering(boolean severing);

	int mannequin$getSeveringTicksRemaining();

	void mannequin$setSeveringTicksRemaining(int ticks);

	default void mannequin$startSevering(int ticks) {
		mannequin$setSevering(true);
		mannequin$setSeveringTicksRemaining(ticks);
	}

	default void mannequin$stopSevering() {
		mannequin$setSevering(false);
		mannequin$setSeveringTicksRemaining(0);
	}
}
