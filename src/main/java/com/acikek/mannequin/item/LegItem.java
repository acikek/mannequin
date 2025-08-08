package com.acikek.mannequin.item;

import com.acikek.mannequin.util.LimbOrientation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.NotNull;

public class LegItem extends Item {

	public static final Properties PROPERTIES = new Properties().component(LimbOrientation.DATA_COMPONENT_TYPE, LimbOrientation.NONE);

	public LegItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull Component getName(ItemStack itemStack) {
		var profile = itemStack.get(DataComponents.PROFILE);
		var orientation = itemStack.getOrDefault(LimbOrientation.DATA_COMPONENT_TYPE, LimbOrientation.NONE);
		if (profile != null && profile.name().isPresent()) {
			return Component.translatable(descriptionId + ".named", profile.name().get(), orientation.component);
		}
		return Component.translatable("item.mannequin.leg", orientation.component);
	}

	@Override
	public void verifyComponentsAfterLoad(ItemStack itemStack) {
		var profile = itemStack.get(DataComponents.PROFILE);
		if (profile != null && !profile.isResolved()) {
			profile.resolve().thenAcceptAsync(resolved -> itemStack.set(DataComponents.PROFILE, resolved), SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
		}
	}
}
