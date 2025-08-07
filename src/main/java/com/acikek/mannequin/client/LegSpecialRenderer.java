package com.acikek.mannequin.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.SkinManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegSpecialRenderer extends PlayerHeadSpecialRenderer {

	public LegSpecialRenderer(SkinManager skinManager, SkullModelBase skullModelBase, PlayerHeadRenderInfo playerHeadRenderInfo) {
		super(skinManager, skullModelBase, playerHeadRenderInfo);
	}

	public record Unbaked() implements SpecialModelRenderer.Unbaked {

		public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

		@Override
		public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public @Nullable SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
			return null;
		}
	}
}
