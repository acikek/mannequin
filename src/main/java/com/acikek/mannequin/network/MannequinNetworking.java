package com.acikek.mannequin.network;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.util.MannequinEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MannequinNetworking {

	public record UpdateSevering(boolean active) implements CustomPacketPayload {

		public static final Type<UpdateSevering> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "update_severing"));

		public static final StreamCodec<FriendlyByteBuf, UpdateSevering> STREAM_CODEC = ByteBufCodecs.BOOL.map(UpdateSevering::new, UpdateSevering::active).cast();

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(UpdateSevering.TYPE, UpdateSevering.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdateSevering.TYPE, (payload, context) -> {
			if (context.player().isUsingItem() && context.player() instanceof MannequinEntity mannequinEntity) {
				if (payload.active()) {
					if (!mannequinEntity.mannequin$isSevering() && mannequinEntity.mannequin$getLimbToSever() != null) {
						mannequinEntity.mannequin$startSevering(20);
					}
				}
				else {
					context.player().releaseUsingItem();
				}
			}
		});
	}
}
