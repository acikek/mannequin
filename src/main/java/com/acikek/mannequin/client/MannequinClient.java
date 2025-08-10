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
	public static final ModelLayerLocation LEFT_ARM_SLIM_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "left_arm_slim"), "main");
	public static final ModelLayerLocation RIGHT_ARM_SLIM_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "right_arm_slim"), "main");

	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(LEFT_LEG_LAYER, () -> LimbModel.createLayer(LimbType.LEG, LimbOrientation.LEFT, false));
		EntityModelLayerRegistry.registerModelLayer(RIGHT_LEG_LAYER, () -> LimbModel.createLayer(LimbType.LEG, LimbOrientation.RIGHT, false));
		EntityModelLayerRegistry.registerModelLayer(LEFT_ARM_LAYER, () -> LimbModel.createLayer(LimbType.ARM, LimbOrientation.LEFT, false));
		EntityModelLayerRegistry.registerModelLayer(RIGHT_ARM_LAYER, () -> LimbModel.createLayer(LimbType.ARM, LimbOrientation.RIGHT, false));
		EntityModelLayerRegistry.registerModelLayer(LEFT_ARM_SLIM_LAYER, () -> LimbModel.createLayer(LimbType.ARM, LimbOrientation.LEFT, true));
		EntityModelLayerRegistry.registerModelLayer(RIGHT_ARM_SLIM_LAYER, () -> LimbModel.createLayer(LimbType.ARM, LimbOrientation.RIGHT, true));
		SpecialModelRenderers.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "limb"), LimbSpecialRenderer.Unbaked.MAP_CODEC);
	}

	/*public static TextureSheetParticle createBloodHang(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
		DripParticle.DripHangParticle dripHangParticle = new DripParticle.DripHangParticle(clientLevel, d, e, f, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
		dripHangParticle.gravity *= 0.01F;
		dripHangParticle.lifetime = 100;
		dripHangParticle.setColor(0.51171875F, 0.03125F, 0.890625F);
		return dripHangParticle;
	}

	static class DripLandParticle extends DripParticle {
		DripLandParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid) {
			super(clientLevel, d, e, f, fluid);
			this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		}
	}*/
}
