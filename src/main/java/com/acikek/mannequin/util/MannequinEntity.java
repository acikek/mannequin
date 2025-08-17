package com.acikek.mannequin.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;

public interface MannequinEntity {

	MannequinEntityData mannequin$getData();

	void mannequin$setData(MannequinEntityData data);

	void mannequin$startSevering(MannequinLimb limbToSever, InteractionHand hand, int ticks);

	void mannequin$stopSevering();

	void mannequin$sever(MannequinLimb limb, InteractionHand hand);

	void mannequin$attach(MannequinLimb limb, @Nullable ResolvableProfile profile);

	void mannequin$makeDoll();

	ItemStack mannequin$getItemBySlot(EquipmentSlot slot);

	ItemStack mannequin$getItemInHand(InteractionHand hand);
}
