package com.acikek.mannequin.item;

import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.NotNull;

public class LimbItem extends Item {

	public static final DataComponentType<Boolean> SLIM_COMPONENT_TYPE = DataComponentType.<Boolean>builder()
		.persistent(Codec.BOOL)
		.networkSynchronized(ByteBufCodecs.BOOL)
		.cacheEncoding()
		.build();

	public static final Properties PROPERTIES = new Properties()
		.component(LimbOrientation.DATA_COMPONENT_TYPE, LimbOrientation.NONE)
		.component(SLIM_COMPONENT_TYPE, false);

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

	public record SlimPredicate(boolean slim) implements SingleComponentItemPredicate<Boolean> {

		public static final Codec<SlimPredicate> CODEC = Codec.BOOL.xmap(SlimPredicate::new, SlimPredicate::slim);
		public static final Type<SlimPredicate> TYPE = new Type<>(CODEC);

		@Override
		public @NotNull DataComponentType<Boolean> componentType() {
			return SLIM_COMPONENT_TYPE;
		}

		@Override
		public boolean matches(Boolean object) {
			return slim == object;
		}
	}
}
