package com.acikek.mannequin.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ArmItem extends Item {

	public ArmItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull Component getName(ItemStack itemStack) {
		return super.getName(itemStack);
	}
}
