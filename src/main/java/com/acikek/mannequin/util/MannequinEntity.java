package com.acikek.mannequin.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;

public interface MannequinEntity {

	MannequinLimbs mannequin$getLimbs();

	boolean mannequin$isSevering();

	void mannequin$setSevering(boolean severing);

	boolean mannequin$isDoll();

	@Nullable MannequinLimb mannequin$getSeveringLimb();

	void mannequin$setSeveringLimb(MannequinLimb limb);

	@Nullable InteractionHand mannequin$getSeveringHand();

	void mannequin$setSeveringHand(InteractionHand hand);

	int mannequin$getSeveringTicksRemaining();

	void mannequin$setSeveringTicksRemaining(int ticks);

	boolean mannequin$isSlim();

	void mannequin$setSlim(boolean slim);

	void mannequin$startSevering(MannequinLimb limbToSever, InteractionHand hand, int ticks);

	void mannequin$stopSevering();

	void mannequin$sever(MannequinLimb limb, InteractionHand hand);

	void mannequin$attach(MannequinLimb limb, @Nullable ResolvableProfile profile);
}
