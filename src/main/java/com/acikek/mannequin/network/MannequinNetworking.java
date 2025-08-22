package com.acikek.mannequin.network;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.client.MannequinClient;
import com.acikek.mannequin.util.LimbType;
import com.acikek.mannequin.util.MannequinEntity;
import com.acikek.mannequin.util.MannequinEntityData;
import com.acikek.mannequin.util.MannequinLimb;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

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

	public record UpdateLimb(OptionalInt entityId, boolean mainHand, MannequinLimb limb) implements CustomPacketPayload {

		public static final Type<UpdateLimb> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "update_limb"));

		public static final StreamCodec<FriendlyByteBuf, UpdateLimb> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.OPTIONAL_VAR_INT, UpdateLimb::entityId,
			ByteBufCodecs.BOOL, UpdateLimb::mainHand,
			MannequinLimb.STREAM_CODEC, UpdateLimb::limb,
			UpdateLimb::new
		);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public record UpdateDoll(OptionalInt entityId, boolean doll) implements CustomPacketPayload {

		public static final Type<UpdateDoll> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "update_doll"));

		public static final StreamCodec<FriendlyByteBuf, UpdateDoll> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.OPTIONAL_VAR_INT, UpdateDoll::entityId,
			ByteBufCodecs.BOOL, UpdateDoll::doll,
			UpdateDoll::new
		);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public record UpdateMannequinEntityData(OptionalInt entityId, MannequinEntityData data) implements CustomPacketPayload {

		public static final Type<UpdateMannequinEntityData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "update_mannequin_entity_data"));

		public static final StreamCodec<FriendlyByteBuf, UpdateMannequinEntityData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.OPTIONAL_VAR_INT, UpdateMannequinEntityData::entityId,
			MannequinEntityData.STREAM_CODEC, UpdateMannequinEntityData::data,
			UpdateMannequinEntityData::new
		);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public record RequestDataUpdate(int entityId) implements CustomPacketPayload {

		public static final Type<RequestDataUpdate> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "request_data_update"));

		public static final StreamCodec<FriendlyByteBuf, RequestDataUpdate> STREAM_CODEC = ByteBufCodecs.INT.map(RequestDataUpdate::new, RequestDataUpdate::entityId).cast();

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
		PayloadTypeRegistry.playS2C().register(UpdateDoll.TYPE, UpdateDoll.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(UpdateMannequinEntityData.TYPE, UpdateMannequinEntityData.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(RequestDataUpdate.TYPE, RequestDataUpdate.STREAM_CODEC);
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
			else if (result.severedLimb() == null) {
				stopSevering(context.player(), mannequinEntity, true);
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(StopSevering.TYPE, (payload, context) -> {
			if (context.player() instanceof MannequinEntity mannequinEntity) {
				stopSevering(context.player(), mannequinEntity, false);
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(RequestDataUpdate.TYPE, (payload, context) -> {
			if (context.player().level().getEntity(payload.entityId()) instanceof MannequinEntity mannequinEntity) {
				context.responseSender().sendPacket(new UpdateMannequinEntityData(OptionalInt.of(payload.entityId()), mannequinEntity.mannequin$getData()));
			}
		});
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof MannequinEntity) {
				entity.refreshDimensions();
			}
		});
	}

	public record StartSeveringResult(boolean active, int ticks, MannequinLimb severedLimb) {

		public static StartSeveringResult empty() {
			return new StartSeveringResult(false, 0, null);
		}
	}

	public static StartSeveringResult tryStartSevering(StartSevering payload, Player player, MannequinEntity mannequinEntity) {
		if (mannequinEntity.mannequin$getData().severing) {
			return StartSeveringResult.empty();
		}
		var hand = payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		var stack = player.getItemInHand(hand);
		var limbToSever = mannequinEntity.mannequin$getData().limbs.resolve(player, stack, hand);
		if (limbToSever != null && !limbToSever.severed) {
			mannequinEntity.mannequin$getData().slim = payload.slim();
			if (mannequinEntity.mannequin$getData().doll && limbToSever.type != LimbType.TORSO) {
				mannequinEntity.mannequin$sever(limbToSever, hand);
				return new StartSeveringResult(false, 0, limbToSever);
			}
			int severingTicks = limbToSever.getSeveringTicks(stack);
			if (severingTicks >= 0) {
				mannequinEntity.mannequin$startSevering(limbToSever, hand, severingTicks);
			}
			return new StartSeveringResult(true, severingTicks, null);
		}
		return StartSeveringResult.empty();
	}

	public static void stopSevering(ServerPlayer player, MannequinEntity mannequinEntity, boolean force) {
		mannequinEntity.mannequin$stopSevering();
		if (force) {
			ServerPlayNetworking.send(player, new StopSevering(OptionalInt.empty()));
		}
		var watcherPayload = new StopSevering(OptionalInt.of(player.getId()));
		for (var watcher : PlayerLookup.tracking(player)) {
			ServerPlayNetworking.send(watcher, watcherPayload);
		}
	}
}
