package com.acikek.mannequin;

import com.acikek.mannequin.item.MannequinItems;
import com.acikek.mannequin.network.MannequinNetworking;
import net.fabricmc.api.ModInitializer;

import net.minecraft.world.entity.EntityDimensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mannequin implements ModInitializer {

	public static final String MOD_ID = "mannequin";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityDimensions LEGLESS_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.1F);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Mannequin...");
		MannequinItems.register();
		MannequinNetworking.register();
	}
}
