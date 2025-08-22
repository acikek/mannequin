package com.acikek.mannequin.command;

import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import com.acikek.mannequin.util.MannequinEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import java.util.Collection;
import java.util.function.BiFunction;

public class MannequinCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		var mannequin = Commands.literal("mannequin").requires(Commands.hasPermission(2));
		var doll = Commands.literal("doll");
		executesSelfOrTargets(doll, MannequinCommand::doll);
		var sever = Commands.literal("sever");
		for (var limbType : LimbType.values()) {
			var limbNode = Commands.literal(limbType.name);
			if (limbType != LimbType.TORSO) {
				for (var limbOrientation : LimbOrientation.values()) {
					if (limbOrientation != LimbOrientation.NONE) {
						var orientationNode = Commands.literal(limbOrientation.name);
						executesSelfOrTargets(orientationNode, (source, entities) -> sever(source, limbType, limbOrientation, entities));
						limbNode.then(orientationNode);
					}
				}
			}
			else {
				executesSelfOrTargets(limbNode, (source, entities) -> sever(source, limbType, null, entities));
			}
			sever.then(limbNode);
		}
		dispatcher.register(mannequin.then(doll).then(sever));
	}

	public static <T extends ArgumentBuilder<CommandSourceStack, T>> void executesSelfOrTargets(ArgumentBuilder<CommandSourceStack, T> builder, BiFunction<CommandSourceStack, Collection<ServerPlayer>, Integer> executor) {
		builder
			.executes(context -> executor.apply(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException())))
			.then(Commands.argument("targets", EntityArgument.entities())
				.executes(context -> executor.apply(context.getSource(), EntityArgument.getPlayers(context, "targets"))));
	}

	public static int doll(CommandSourceStack source, Collection<ServerPlayer> players) {
		for (var player : players) {
			if (player instanceof MannequinEntity mannequinEntity) {
				mannequinEntity.mannequin$makeDoll();
			}
		}
		if (players.size() == 1) {
			source.sendSuccess(() -> Component.translatable("command.mannequin.doll.success.single", players.iterator().next().getDisplayName()), true);
		}
		else {
			source.sendSuccess(() -> Component.translatable("command.mannequin.doll.success.multiple", players.size()), true);
		}
		return players.size();
	}

	public static int sever(CommandSourceStack source, LimbType type, LimbOrientation orientation, Collection<ServerPlayer> players) {
		for (var player : players) {
			if (player instanceof MannequinEntity mannequinEntity) {
				var limbToSever = mannequinEntity.mannequin$getData().limbs.resolve(type, orientation);
				var severingHand = InteractionHand.MAIN_HAND;
				if (type == LimbType.ARM && player.getMainArm() == (orientation == LimbOrientation.RIGHT ? HumanoidArm.RIGHT : HumanoidArm.LEFT)) {
					severingHand = InteractionHand.OFF_HAND;
				}
				mannequinEntity.mannequin$sever(limbToSever, severingHand);
			}
		}
		var orientationDescription = orientation != null ? Component.translatable("command.mannequin.sever.limbOrientation." + orientation.name) : Component.empty();
		var limbDescription = Component.translatable("command.mannequin.sever.limbType." + type.name, orientationDescription);
		if (players.size() == 1) {
			source.sendSuccess(() -> Component.translatable("command.mannequin.sever.success.single", players.iterator().next().getDisplayName(), limbDescription), true);
		}
		else {
			source.sendSuccess(() -> Component.translatable("command.mannequin.sever.success.multiple", limbDescription, players.size()), true);
		}
		return players.size();
	}
}
