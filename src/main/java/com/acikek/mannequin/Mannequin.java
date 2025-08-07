package com.acikek.mannequin;

import com.acikek.mannequin.network.MannequinNetworking;
import net.fabricmc.api.ModInitializer;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mannequin implements ModInitializer {

	public static final String MOD_ID = "mannequin";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Item ARM_ITEM = Items.APPLE;
	public static final Item LEG_ITEM = Items.CARROT;

	public static final EntityDimensions LEGLESS_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.1F);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Mannequin...");
		MannequinNetworking.register();
		//Registry.register(BuiltInRegistries.ITEM, ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "arm")), ARM_ITEM);
		//Registry.register(BuiltInRegistries.ITEM, ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "leg")), LEG_ITEM);
	}
}
