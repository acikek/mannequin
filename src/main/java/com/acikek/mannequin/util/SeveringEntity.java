package com.acikek.mannequin.util;

import java.util.List;

public interface SeveringEntity {

	boolean mannequin$canSever();

	void mannequin$setCanSever(boolean canSever);

	boolean mannequin$isSevering();

	void mannequin$setSevering(boolean severing);

	int mannequin$getSeveringTicksRemaining();

	void mannequin$setSeveringTicksRemaining(int ticks);

	List<SeveredLimb> mannequin$getSeveredLimbs();

	default void mannequin$startSevering(int ticks) {
		mannequin$setSevering(true);
		mannequin$setSeveringTicksRemaining(ticks);
	}

	default void mannequin$stopSevering() {
		mannequin$setCanSever(false);
		mannequin$setSevering(false);
		mannequin$setSeveringTicksRemaining(0);
	}
}
