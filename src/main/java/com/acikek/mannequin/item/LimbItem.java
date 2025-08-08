package com.acikek.mannequin.item;

import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.NotNull;

public class LimbItem extends Item {

	public static final Properties PROPERTIES = new Properties().component(LimbOrientation.DATA_COMPONENT_TYPE, LimbOrientation.NONE);

	public LimbType limbType;

	public LimbItem(Properties properties, LimbType limbType) {
		super(properties);
		this.limbType = limbType;
	}

	@Override
	public @NotNull Component getName(ItemStack itemStack) {
		var profile = itemStack.get(DataComponents.PROFILE);
		var orientation = itemStack.getOrDefault(LimbOrientation.DATA_COMPONENT_TYPE, LimbOrientation.NONE);
		var componentBase = "item.mannequin." + limbType.name;
		if (profile != null && profile.name().isPresent()) {
			return Component.translatable( componentBase + ".named", profile.name().get(), orientation.component);
		}
		return Component.translatable(componentBase, orientation.component);
	}

	@Override
	public void verifyComponentsAfterLoad(ItemStack itemStack) {
		var profile = itemStack.get(DataComponents.PROFILE);
		if (profile != null && !profile.isResolved()) {
			profile.resolve().thenAcceptAsync(resolved -> itemStack.set(DataComponents.PROFILE, resolved), SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
		}
	}
}
