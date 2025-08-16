package com.acikek.mannequin.network;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.client.MannequinClient;
import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import com.acikek.mannequin.util.MannequinEntity;
import com.acikek.mannequin.util.MannequinLimb;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
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

		public static final Type<StopSevering> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "stop_severing"));

		public static final StreamCodec<FriendlyByteBuf, StopSevering> STREAM_CODEC = ByteBufCodecs.OPTIONAL_VAR_INT.map(StopSevering::new, StopSevering::entityId).cast();

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public record UpdateLimb(int entityId, boolean mainHand, LimbType limbType, LimbOrientation limbOrientation, boolean severed, Optional<ResolvableProfile> profile) implements CustomPacketPayload {

		public static final Type<UpdateLimb> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "update_limb"));

		public static final StreamCodec<FriendlyByteBuf, UpdateLimb> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, UpdateLimb::entityId,
			ByteBufCodecs.BOOL, UpdateLimb::mainHand,
			LimbType.STREAM_CODEC, UpdateLimb::limbType,
			LimbOrientation.STREAM_CODEC, UpdateLimb::limbOrientation,
			ByteBufCodecs.BOOL, UpdateLimb::severed,
			ByteBufCodecs.optional(ResolvableProfile.STREAM_CODEC), UpdateLimb::profile,
			UpdateLimb::new
		);

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
		PayloadTypeRegistry.playS2C().register(UpdateLimb.TYPE, UpdateLimb.STREAM_CODEC);
		registerServer();
	}

	public static void registerServer() {
		ServerPlayNetworking.registerGlobalReceiver(StartSevering.TYPE, (payload, context) -> {
			if (!(context.player() instanceof MannequinEntity mannequinEntity)) {
				return;
			}
			var result = tryStartSevering(payload, context.player(), mannequinEntity);
			if (result.active()) {
				context.responseSender().sendPacket(new UpdateSeveringTicksRemaining(result.ticks()));
				var watcherPayload = new StartSevering(OptionalInt.of(context.player().getId()), payload.mainHand(), payload.slim());
				for (var watcher : PlayerLookup.tracking(context.player())) {
					ServerPlayNetworking.send(watcher, watcherPayload);
				}
			}
			else if (result.severedLimb() != null) {
				var watcherPayload = new UpdateLimb(context.player().getId(), payload.mainHand(), result.severedLimb().type, result.severedLimb().orientation, true, Optional.empty());
				for (var watcher : PlayerLookup.tracking(context.player())) {
					ServerPlayNetworking.send(watcher, watcherPayload);
				}
			}
			else {
				stopSevering(context, mannequinEntity, true);
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(StopSevering.TYPE, (payload, context) -> {
			if (context.player() instanceof MannequinEntity mannequinEntity) {
				stopSevering(context, mannequinEntity, false);
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
				if (tryStartSevering(payload, player, mannequinEntity).active()) {
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
		ClientPlayNetworking.registerGlobalReceiver(UpdateLimb.TYPE, (payload, context) -> {
			var entity = context.player().level().getEntity(payload.entityId());
			if (entity instanceof MannequinEntity mannequinEntity) {
				var limb = mannequinEntity.mannequin$getLimbs().resolve(payload.limbType(), payload.limbOrientation());
				if (payload.severed()) {
					var hand = payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
					mannequinEntity.mannequin$sever(limb, hand);
					if (entity instanceof LocalPlayer player) {
						player.swing(hand);
					}
				}
				else if (payload.profile().isPresent()) {
					mannequinEntity.mannequin$attach(limb, payload.profile().get());
				}
			}
		});
	}

	public record StartSeveringResult(boolean active, int ticks, MannequinLimb severedLimb) {

		public static StartSeveringResult empty() {
			return new StartSeveringResult(false, 0, null);
		}
	}

	public static StartSeveringResult tryStartSevering(StartSevering payload, Player player, MannequinEntity mannequinEntity) {
		if (mannequinEntity.mannequin$isSevering()) {
			return StartSeveringResult.empty();
		}
		var hand = payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		var stack = player.getItemInHand(hand);
		var limbToSever = mannequinEntity.mannequin$getLimbs().resolve(player, stack, hand);
		if (limbToSever != null && !limbToSever.severed) {
			if (mannequinEntity.mannequin$isDoll()) {
				mannequinEntity.mannequin$sever(limbToSever, hand);
				return new StartSeveringResult(false, 0, limbToSever);
			}
			int severingTicks = limbToSever.getSeveringTicks(stack);
			if (severingTicks >= 0) {
				mannequinEntity.mannequin$startSevering(limbToSever, hand, severingTicks);
				mannequinEntity.mannequin$setSlim(payload.slim());
			}
			return new StartSeveringResult(true, severingTicks, null);
		}
		return StartSeveringResult.empty();
	}

	public static void stopSevering(ServerPlayNetworking.Context context, MannequinEntity mannequinEntity, boolean force) {
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
