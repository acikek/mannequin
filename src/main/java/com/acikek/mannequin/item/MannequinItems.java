package com.acikek.mannequin.item;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class MannequinItems {

	public static final ResourceKey<Item> LEG_KEY = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "leg"));
	public static final LimbItem LEG = new LimbItem(LimbItem.PROPERTIES.setId(LEG_KEY), LimbType.LEG);

	public static final ResourceKey<Item> ARM_KEY = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "arm"));
	public static final LimbItem ARM = new LimbItem(new Item.Properties().setId(ARM_KEY), LimbType.ARM);

	public static void register() {
		Registry.register(BuiltInRegistries.ITEM, LEG_KEY, LEG);
		Registry.register(BuiltInRegistries.ITEM, ARM_KEY, ARM);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "limb_orientation"), LimbOrientation.DATA_COMPONENT_TYPE);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "limb_orientation"), LimbOrientation.Predicate.TYPE);
	}
}
