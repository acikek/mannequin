package com.acikek.mannequin.item;

import com.acikek.mannequin.util.LimbOrientation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LegItem extends Item {

	public static final DataComponentType<Component> SOURCE_COMPONENT_TYPE = DataComponentType.<Component>builder()
		.persistent(ComponentSerialization.CODEC)
		.networkSynchronized(ComponentSerialization.STREAM_CODEC)
		.cacheEncoding()
		.build();

	public static final Properties PROPERTIES = new Properties().component(LimbOrientation.DATA_COMPONENT_TYPE, LimbOrientation.NONE);

	public LegItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull Component getName(ItemStack itemStack) {
		var orientation = itemStack.getOrDefault(LimbOrientation.DATA_COMPONENT_TYPE, LimbOrientation.NONE);
		if (itemStack.has(SOURCE_COMPONENT_TYPE)) {
			return Component.translatable("item.mannequin.leg.named", itemStack.get(SOURCE_COMPONENT_TYPE), orientation.component);
		}
		return Component.translatable("item.mannequin.leg", orientation.component);
	}
}
