package com.acikek.mannequin.util;

import com.acikek.mannequin.item.LimbItem;
import com.acikek.mannequin.item.MannequinItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
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

	@Environment(EnvType.CLIENT)
	public @Nullable PlayerSkin skin;

	public MannequinLimb(LimbType type, LimbOrientation orientation, boolean severed) {
		this.type = type;
		this.orientation = orientation;
		this.severed = severed;
	}

	public MannequinLimb(LimbType type, LimbOrientation orientation) {
		this(type, orientation, false);
	}

	public ItemStack getLimbItemStack(Player player) {
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

	@Environment(EnvType.CLIENT)
	public void setSkin(@Nullable ResolvableProfile profile) {
		if (profile == null) {
			skin = DefaultPlayerSkin.getDefaultSkin();
			return;
		}
		if (profile.isResolved()) {
			skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile.gameProfile(), null);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean isBaseVisible() {
		return !severed && skin == null;
	}
}
