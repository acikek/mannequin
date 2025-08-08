package com.acikek.mannequin.client;

import com.acikek.mannequin.Mannequin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;

public class MannequinClient implements ClientModInitializer {

	public static final ModelLayerLocation LEG_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "leg"), "main");

	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(LEG_LAYER, LegModel::createLayer);
		SpecialModelRenderers.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "leg"), LegSpecialRenderer.Unbaked.MAP_CODEC);
	}
}
