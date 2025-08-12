package com.acikek.mannequin.network;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.util.MannequinEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

public class MannequinNetworking {

	public record UpdateSevering(boolean active, boolean mainHand, boolean slim) implements CustomPacketPayload {

		public static final Type<UpdateSevering> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "update_severing"));

		public static final StreamCodec<FriendlyByteBuf, UpdateSevering> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, UpdateSevering::active,
			ByteBufCodecs.BOOL, UpdateSevering::mainHand,
			ByteBufCodecs.BOOL, UpdateSevering::slim,
			UpdateSevering::new
		);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public static UpdateSevering cancelled() {
			return new UpdateSevering(false, false, false);
		}
	}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(UpdateSevering.TYPE, UpdateSevering.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(UpdateSevering.TYPE, UpdateSevering.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdateSevering.TYPE, (payload, context) -> {
			if (!(context.player() instanceof MannequinEntity mannequinEntity)) {
				return;
			}
			if (!c2sTryStartSevering(payload, context, mannequinEntity)) {
				mannequinEntity.mannequin$stopSevering();
				context.responseSender().sendPacket(UpdateSevering.cancelled());
			}
		});
	}

	@Environment(EnvType.CLIENT)
	public static void registerClient() {
		ClientPlayNetworking.registerGlobalReceiver(UpdateSevering.TYPE, (payload, context) -> {
			if (!payload.active() && context.player() instanceof MannequinEntity mannequinEntity) {
				mannequinEntity.mannequin$stopSevering();
			}
		});
	}

	public static boolean c2sTryStartSevering(UpdateSevering payload, ServerPlayNetworking.Context context, MannequinEntity mannequinEntity) {
		if (!payload.active()) {
			return false;
		}
		if (mannequinEntity.mannequin$isSevering()) {
			return true;
		}
		var hand = payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		var limbToSever = mannequinEntity.mannequin$getLimbs().resolve(context.player(), context.player().getItemInHand(hand), hand);
		if (limbToSever != null && !limbToSever.severed) {
			mannequinEntity.mannequin$startSevering(limbToSever, hand, 20);
			mannequinEntity.mannequin$setSlim(payload.slim());
			return true;
		}
		return false;
	}
}
