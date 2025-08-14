package com.acikek.mannequin.sound;

import com.acikek.mannequin.Mannequin;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class MannequinSounds {

	public static final SoundEvent SEVERING = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "entity.severing"));
	public static final SoundEvent LIMB_SNAP = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "entity.limb_snap"));

	public static void register() {
		Registry.register(BuiltInRegistries.SOUND_EVENT, SEVERING.location(), SEVERING);
		Registry.register(BuiltInRegistries.SOUND_EVENT, LIMB_SNAP.location(), LIMB_SNAP);
	}
}
