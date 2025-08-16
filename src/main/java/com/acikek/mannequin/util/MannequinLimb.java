package com.acikek.mannequin.util;

import com.acikek.mannequin.item.LimbItem;
import com.acikek.mannequin.item.MannequinItems;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;

public class MannequinLimb {

	public final LimbType type;
	public final LimbOrientation orientation;

	public boolean severed;
	public ResolvableProfile profile;

	public MannequinLimb(LimbType type, LimbOrientation orientation, boolean severed, ResolvableProfile profile) {
		this.type = type;
		this.orientation = orientation;
		this.severed = severed;
		this.profile = profile;
	}

	public MannequinLimb(LimbType type, LimbOrientation orientation) {
		this(type, orientation, false, null);
	}

	public ItemStack getLimbItemStack(Player player) {
		if (type == LimbType.TORSO) {
			return ItemStack.EMPTY;
		}
		var stack = (type == LimbType.LEG ? MannequinItems.LEG : MannequinItems.ARM).getDefaultInstance();
		stack.set(LimbOrientation.DATA_COMPONENT_TYPE, orientation);
		stack.set(DataComponents.PROFILE, profile != null ? profile : new ResolvableProfile(player.getGameProfile()));
		if (player instanceof MannequinEntity mannequinEntity) {
			stack.set(LimbItem.SLIM_COMPONENT_TYPE, mannequinEntity.mannequin$isSlim());
		}
		return stack;
	}

	public int getSeveringTicks(ItemStack stack) {
		boolean leg = type == LimbType.LEG;
		if (stack.is(MannequinItems.SEVER_TIER_1)) {
			return leg ? 160 : 120;
		}
		if (stack.is(MannequinItems.SEVER_TIER_2)) {
			return leg ? 100 : 80;
		}
		if (stack.is(MannequinItems.SEVER_TIER_3)) {
			return 50;
		}
		return -1;
	}

	public boolean isBaseVisible(GameProfile profile) {
		if (severed) {
			System.out.println("severed " + orientation + " " + type);
		}
		return !severed && this.profile.gameProfile() == profile;
	}
}
