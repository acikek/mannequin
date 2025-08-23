package com.acikek.mannequin.client;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.client.render.LimbModel;
import com.acikek.mannequin.client.render.LimbSpecialRenderer;
import com.acikek.mannequin.mixin.client.MinecraftAccessor;
import com.acikek.mannequin.network.MannequinNetworking;
import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import com.acikek.mannequin.util.MannequinEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

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
		registerNetworking();
	}

	public static void playSeveringSound(Player player) {
		Minecraft.getInstance().getSoundManager().play(new SeveringSoundInstance(player));
	}

	public static void registerNetworking() {
		ClientPlayNetworking.registerGlobalReceiver(MannequinNetworking.StartSevering.TYPE, (payload, context) -> {
			if (payload.entityId().isEmpty()) {
				return;
			}
			var entity = context.player().level().getEntity(payload.entityId().getAsInt());
			if (entity instanceof Player player && player instanceof MannequinEntity mannequinEntity) {
				if (MannequinNetworking.tryStartSevering(payload, player, mannequinEntity).active()) {
					MannequinClient.playSeveringSound(player);
				}
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(MannequinNetworking.UpdateSeveringTicksRemaining.TYPE, (payload, context) -> {
			if (context.player() instanceof MannequinEntity mannequinEntity) {
				mannequinEntity.mannequin$getData().severingTicksRemaining = payload.ticksRemaining();
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(MannequinNetworking.StopSevering.TYPE, (payload, context) -> {
			var entity = payload.entityId().isPresent() ? context.player().level().getEntity(payload.entityId().getAsInt()) : context.player();
			if (entity instanceof MannequinEntity mannequinEntity) {
				mannequinEntity.mannequin$stopSevering();
			}
			if (payload.entityId().isEmpty()) {
				((MinecraftAccessor) Minecraft.getInstance()).setRightClickDelay(4);
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(MannequinNetworking.UpdateLimb.TYPE, (payload, context) -> {
			var entity = payload.entityId().isPresent() ? context.player().level().getEntity(payload.entityId().getAsInt()) : context.player();
			if (entity instanceof MannequinEntity mannequinEntity) {
				var limb = mannequinEntity.mannequin$getData().limbs.resolve(payload.limb().type, payload.limb().orientation);
				if (limb == null) {
					return;
				}
				if (payload.limb().severed) {
					var hand = payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
					mannequinEntity.mannequin$sever(limb, hand);
					if (entity instanceof Player player) {
						player.swing(hand);
					}
				}
				else if (payload.limb().profile.isPresent()) {
					limb.profile = payload.limb().profile;
					limb.slim = payload.limb().slim;
					mannequinEntity.mannequin$attach(limb);
				}
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(MannequinNetworking.UpdateDoll.TYPE, (payload, context) -> {
			var entity = payload.entityId().isPresent() ? context.player().level().getEntity(payload.entityId().getAsInt()) : context.player();
			if (entity instanceof MannequinEntity mannequinEntity) {
				if (payload.doll()) {
					mannequinEntity.mannequin$makeDoll();
				}
				else {
					mannequinEntity.mannequin$getData().doll = false;
				}
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(MannequinNetworking.UpdateMannequinEntityData.TYPE, (payload, context) -> {
			var entity = payload.entityId().isPresent() ? context.player().level().getEntity(payload.entityId().getAsInt()) : context.player();
			if (entity instanceof MannequinEntity mannequinEntity) {
				mannequinEntity.mannequin$setData(payload.data());
				entity.refreshDimensions();
			}
		});
		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof Player && entity instanceof MannequinEntity mannequinEntity) {
				if (entity == Minecraft.getInstance().player) {
					boolean slim = Minecraft.getInstance().player.getSkin().model() == PlayerSkin.Model.SLIM;
					mannequinEntity.mannequin$getData().slim = slim;
					ClientPlayNetworking.send(new MannequinNetworking.UpdateSlim(slim));
				}
				ClientPlayNetworking.send(new MannequinNetworking.RequestDataUpdate(entity.getId()));
			}
		});
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
