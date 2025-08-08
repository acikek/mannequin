package com.acikek.mannequin.client;

import com.acikek.mannequin.mixin.client.PlayerHeadSpecialRendererAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LegSpecialRenderer extends PlayerHeadSpecialRenderer {

	public LegSpecialRenderer(SkinManager skinManager, SkullModelBase skullModelBase, PlayerHeadRenderInfo playerHeadRenderInfo) {
		super(skinManager, skullModelBase, playerHeadRenderInfo);
	}

	@Override
	public void render(@Nullable PlayerHeadSpecialRenderer.PlayerHeadRenderInfo playerHeadRenderInfo, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, boolean bl) {
		PlayerHeadSpecialRenderer.PlayerHeadRenderInfo playerHeadRenderInfo2 = Objects.requireNonNullElse(playerHeadRenderInfo, ((PlayerHeadSpecialRendererAccess) this).getDefaultPlayerHeadRenderInfo());
		RenderType renderType = playerHeadRenderInfo2.renderType();
		poseStack.pushPose();
		poseStack.translate(0.5F, 1.5F, 0.5F);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
		((PlayerHeadSpecialRendererAccess) this).getModelBase().renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
	}

	public record Unbaked() implements SpecialModelRenderer.Unbaked {

		public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

		@Override
		public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public @NotNull SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
			var model = new LegModel(entityModelSet.bakeLayer(MannequinClient.LEG_LAYER));
			return new LegSpecialRenderer(Minecraft.getInstance().getSkinManager(), model, new PlayerHeadSpecialRenderer.PlayerHeadRenderInfo(SkullBlockRenderer.getPlayerSkinRenderType(DefaultPlayerSkin.getDefaultTexture())));
		}
	}
}
