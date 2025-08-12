package com.acikek.mannequin.util;

import com.acikek.mannequin.item.LimbItem;
import com.acikek.mannequin.item.MannequinItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;

public class MannequinLimb {

	public final LimbType type;
	public final LimbOrientation orientation;

	public boolean severed;
	public @Nullable PlayerSkin skin;

	public MannequinLimb(LimbType type, LimbOrientation orientation, boolean severed, @Nullable PlayerSkin skin) {
		this.type = type;
		this.orientation = orientation;
		this.severed = severed;
		this.skin = skin;
	}

	public MannequinLimb(LimbType type, LimbOrientation orientation) {
		this(type, orientation, false, null);
	}

	public ItemStack getItemStack(Player player) {
		if (type == LimbType.TORSO) {
			return ItemStack.EMPTY;
		}
		var stack = (type == LimbType.LEG ? MannequinItems.LEG : MannequinItems.ARM).getDefaultInstance();
		stack.set(LimbOrientation.DATA_COMPONENT_TYPE, orientation);
		stack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
		if (player instanceof MannequinEntity mannequinEntity) {
			stack.set(LimbItem.SLIM_COMPONENT_TYPE, mannequinEntity.mannequin$isSlim());
		}
		return stack;
	}

	public int getSeveringTicks(Player player) {
		int damageOffset = type == LimbType.LEG ? 6 : 3;
		int maxTime = type == LimbType.LEG ? 230 : 120;
		return (int) (maxTime / (player.getAttributeValue(Attributes.ATTACK_DAMAGE) - damageOffset));
	}
}
