package com.acikek.mannequin.item;

import com.acikek.mannequin.Mannequin;
import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MannequinItems {

	public static final ResourceKey<Item> LEG_KEY = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "leg"));
	public static final LimbItem LEG = new LimbItem(LimbItem.PROPERTIES.setId(LEG_KEY), LimbType.LEG);

	public static final ResourceKey<Item> ARM_KEY = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "arm"));
	public static final LimbItem ARM = new LimbItem(new Item.Properties().setId(ARM_KEY), LimbType.ARM);

	public static final TagKey<Item> SEVERS_LEGS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "severs_legs"));
	public static final TagKey<Item> SEVERS_ARMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "severs_arms"));
	public static final TagKey<Item> SEVER_TIER_1 = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "sever_tier_1"));
	public static final TagKey<Item> SEVER_TIER_2 = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "sever_tier_2"));
	public static final TagKey<Item> SEVER_TIER_3 = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "sever_tier_3"));

	public static void register() {
		Registry.register(BuiltInRegistries.ITEM, LEG_KEY, LEG);
		Registry.register(BuiltInRegistries.ITEM, ARM_KEY, ARM);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "limb_orientation"), LimbOrientation.DATA_COMPONENT_TYPE);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "limb_orientation"), LimbOrientation.Predicate.TYPE);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "limb_slim"), LimbItem.SLIM_COMPONENT_TYPE);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE, ResourceLocation.fromNamespaceAndPath(Mannequin.MOD_ID, "limb_slim"), LimbItem.SlimPredicate.TYPE);
	}
}
