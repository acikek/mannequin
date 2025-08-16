package com.acikek.mannequin.util;

import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;

public class MannequinEntityData {

	public MannequinLimbs limbs = new MannequinLimbs();
	public boolean severing;
	public boolean doll;
	public @Nullable MannequinLimb severingLimb;
	public @Nullable InteractionHand severingHand;
	public int severingTicksRemaining;
	public int damageTicksElapsed;
	public int ticksToBleed;
	public int totalBleedingTicks;
	public boolean slim;
}
