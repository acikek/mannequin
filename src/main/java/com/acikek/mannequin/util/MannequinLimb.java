package com.acikek.mannequin.util;

import com.acikek.mannequin.item.LegItem;
import com.acikek.mannequin.item.MannequinItems;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
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
		if (type == LimbType.LEG) {
			var stack = MannequinItems.LEG.getDefaultInstance();
			stack.set(LimbOrientation.DATA_COMPONENT_TYPE, orientation);
			stack.set(LegItem.SOURCE_COMPONENT_TYPE, player.getName());
			stack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
			return stack;
		}
		if (type == LimbType.ARM) {
			return MannequinItems.ARM.getDefaultInstance();
		}
		return ItemStack.EMPTY;
	}
}
