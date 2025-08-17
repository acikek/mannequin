package com.acikek.mannequin.command;

import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import com.acikek.mannequin.util.MannequinEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import java.util.Collection;

public class MannequinCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			Commands.literal("mannequin")
				.requires(Commands.hasPermission(2))
				.then(Commands.literal("sever"))
				.then(Commands.literal("doll")
					.executes(context -> doll(context.getSource(), ImmutableList.of(context.getSource().getEntityOrException())))
					.then(Commands.argument("targets", EntityArgument.entities())
						.executes(context -> doll(context.getSource(), EntityArgument.getEntities(context, "targets")))))
		);
	}

	public static int sever(CommandSourceStack source, Collection<? extends Entity> entities, LimbType type, LimbOrientation orientation) {
		return 0;
	}

	public static int doll(CommandSourceStack source, Collection<? extends Entity> entities) {
		for (var entity : entities) {
			if (entity instanceof MannequinEntity mannequinEntity) {
				mannequinEntity.mannequin$makeDoll();
			}
		}
		return entities.size();
	}
}
