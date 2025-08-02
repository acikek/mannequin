package com.acikek.mannequin;

import com.acikek.mannequin.network.MannequinNetworking;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mannequin implements ModInitializer {

	public static final String MOD_ID = "mannequin";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Mannequin...");
		MannequinNetworking.register();
	}
}
