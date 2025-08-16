package com.acikek.mannequin.network;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.client.MannequinClient;
import com.acikek.mannequin.util.MannequinEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.OptionalInt;

public class MannequinNetworking {

	public record StartSevering(OptionalInt entityId, boolean mainHand, boolean slim) implements CustomPacketPayload {

		public static final Type<StartSevering> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "start_severing"));

		public static final StreamCodec<FriendlyByteBuf, StartSevering> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.OPTIONAL_VAR_INT, StartSevering::entityId,
			ByteBufCodecs.BOOL, StartSevering::mainHand,
			ByteBufCodecs.BOOL, StartSevering::slim,
			StartSevering::new
		);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public record UpdateSeveringTicksRemaining(int ticksRemaining) implements CustomPacketPayload {

		public static final Type<UpdateSeveringTicksRemaining> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "update_severing_ticks_remaining"));

		public static final StreamCodec<FriendlyByteBuf, UpdateSeveringTicksRemaining> STREAM_CODEC = ByteBufCodecs.INT.map(UpdateSeveringTicksRemaining::new, UpdateSeveringTicksRemaining::ticksRemaining).cast();

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public record StopSevering(OptionalInt entityId) implements CustomPacketPayload {

		public static final Type<StopSevering> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, ""));

		public static final StreamCodec<FriendlyByteBuf, StopSevering> STREAM_CODEC = ByteBufCodecs.OPTIONAL_VAR_INT.map(StopSevering::new, StopSevering::entityId).cast();

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(StartSevering.TYPE, StartSevering.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(StartSevering.TYPE, StartSevering.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(UpdateSeveringTicksRemaining.TYPE, UpdateSeveringTicksRemaining.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(StopSevering.TYPE, StopSevering.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(StopSevering.TYPE, StopSevering.STREAM_CODEC);
		registerServer();
	}

	public static void registerServer() {
		ServerPlayNetworking.registerGlobalReceiver(StartSevering.TYPE, (payload, context) -> {
			if (!(context.player() instanceof MannequinEntity mannequinEntity)) {
				return;
			}
			int severingTicks = tryStartSevering(payload, context.player(), mannequinEntity);
			if (severingTicks >= 0) {
				context.responseSender().sendPacket(new UpdateSeveringTicksRemaining(severingTicks));
				var watcherPayload = new StartSevering(OptionalInt.of(context.player().getId()), payload.mainHand(), payload.slim());
				for (var watcher : PlayerLookup.tracking(context.player())) {
					ServerPlayNetworking.send(watcher, watcherPayload);
				}
			}
			else {
				s2cStopSevering(context, mannequinEntity, true);
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(StopSevering.TYPE, (payload, context) -> {
			if (context.player() instanceof MannequinEntity mannequinEntity) {
				s2cStopSevering(context, mannequinEntity, false);
			}
		});
	}

	@Environment(EnvType.CLIENT)
	public static void registerClient() {
		ClientPlayNetworking.registerGlobalReceiver(StartSevering.TYPE, (payload, context) -> {
			if (payload.entityId().isEmpty()) {
				return;
			}
			var entity = context.player().level().getEntity(payload.entityId().getAsInt());
			if (entity instanceof Player player && player instanceof MannequinEntity mannequinEntity) {
				if (tryStartSevering(payload, player, mannequinEntity) >= 0) {
					MannequinClient.playSeveringSound(player);
				}
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(UpdateSeveringTicksRemaining.TYPE, (payload, context) -> {
			if (context.player() instanceof MannequinEntity mannequinEntity) {
				mannequinEntity.mannequin$setSeveringTicksRemaining(payload.ticksRemaining());
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(StopSevering.TYPE, (payload, context) -> {
			var entity = payload.entityId().isPresent() ? context.player().level().getEntity(payload.entityId().getAsInt()) : context.player();
			if (entity instanceof MannequinEntity mannequinEntity) {
				mannequinEntity.mannequin$stopSevering();
			}
		});
	}

	public static int tryStartSevering(StartSevering payload, Player player, MannequinEntity mannequinEntity) {
		if (mannequinEntity.mannequin$isSevering()) {
			return -1;
		}
		var hand = payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		var stack = player.getItemInHand(hand);
		var limbToSever = mannequinEntity.mannequin$getLimbs().resolve(player, stack, hand);
		if (limbToSever != null && !limbToSever.severed) {
			int severingTicks = limbToSever.getSeveringTicks(stack);
			if (severingTicks >= 0) {
				mannequinEntity.mannequin$startSevering(limbToSever, hand, severingTicks);
				mannequinEntity.mannequin$setSlim(payload.slim());
			}
			return severingTicks;
		}
		return -1;
	}

	public static void s2cStopSevering(ServerPlayNetworking.Context context, MannequinEntity mannequinEntity, boolean force) {
		mannequinEntity.mannequin$stopSevering();
		if (force) {
			context.responseSender().sendPacket(new StopSevering(OptionalInt.empty()));
		}
		var watcherPayload = new StopSevering(OptionalInt.of(context.player().getId()));
		for (var watcher : PlayerLookup.tracking(context.player())) {
			ServerPlayNetworking.send(watcher, watcherPayload);
		}
	}
}
