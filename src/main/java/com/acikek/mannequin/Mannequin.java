package com.acikek.mannequin;

import com.acikek.mannequin.command.MannequinCommand;
import com.acikek.mannequin.item.MannequinItems;
import com.acikek.mannequin.network.MannequinNetworking;
import com.acikek.mannequin.sound.MannequinSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mannequin implements ModInitializer {

	public static final String MOD_ID = "mannequin";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityDimensions LEGLESS_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.1F);
	public static final EntityDimensions HEAD_ONLY_DIMENSIONS = EntityDimensions.scalable(0.6F, 0.3F);
	public static final AttributeModifier SEVERING_SLOWNESS = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "severing_slowness"), -0.65, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	public static final ResourceKey<DamageType> BLEEDING_DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "bleeding"));

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Mannequin...");
		MannequinItems.register();
		MannequinSounds.register();
		MannequinNetworking.register();
		CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
			MannequinCommand.register(commandDispatcher);
		});
	}
}
