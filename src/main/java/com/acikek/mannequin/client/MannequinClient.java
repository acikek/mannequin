package com.acikek.mannequin.client;

import com.acikek.mannequin.Mannequin;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;

public class MannequinClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		SpecialModelRenderers.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "leg"), LegSpecialRenderer.Unbaked.MAP_CODEC);
	}
}
