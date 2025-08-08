package com.acikek.mannequin.client;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;

public class MannequinClient implements ClientModInitializer {

	public static final ModelLayerLocation LEFT_LEG_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "left_leg"), "main");
	public static final ModelLayerLocation RIGHT_LEG_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "right_leg"), "main");
	public static final ModelLayerLocation LEFT_ARM_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "left_arm"), "main");
	public static final ModelLayerLocation RIGHT_ARM_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "right_arm"), "main");

	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(LEFT_LEG_LAYER, () -> LimbModel.createLayer(LimbType.LEG, LimbOrientation.LEFT));
		EntityModelLayerRegistry.registerModelLayer(RIGHT_LEG_LAYER, () -> LimbModel.createLayer(LimbType.LEG, LimbOrientation.RIGHT));
		EntityModelLayerRegistry.registerModelLayer(LEFT_ARM_LAYER, () -> LimbModel.createLayer(LimbType.ARM, LimbOrientation.LEFT));
		EntityModelLayerRegistry.registerModelLayer(RIGHT_ARM_LAYER, () -> LimbModel.createLayer(LimbType.ARM, LimbOrientation.RIGHT));
		SpecialModelRenderers.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "limb"), LimbSpecialRenderer.Unbaked.MAP_CODEC);
	}
}
