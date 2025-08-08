package com.acikek.mannequin.client;

import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LimbSpecialRenderer implements SpecialModelRenderer<LimbSpecialRenderer.Argument> {

	private final Map<ResolvableProfile, Argument> updatedProfiles = new HashMap<>();
	private final SkinManager skinManager;
	private final LimbModel model;
	private final Argument defaultArgument;

	public LimbSpecialRenderer(SkinManager skinManager, LimbModel model, Argument argument) {
		this.skinManager = skinManager;
		this.model = model;
		defaultArgument = argument;
	}

	@Override
	public @Nullable Argument extractArgument(ItemStack itemStack) {
		var profile = itemStack.get(DataComponents.PROFILE);
		if (profile == null) {
			return null;
		}
		var argument = updatedProfiles.get(profile);
		if (argument != null) {
			return argument;
		}
		var resolved = profile.pollResolve();
		return resolved != null ? this.createAndCacheIfTextureIsUnpacked(resolved) : null;
	}

	@Nullable
	private Argument createAndCacheIfTextureIsUnpacked(ResolvableProfile profile) {
		var skin = skinManager.getInsecureSkin(profile.gameProfile(), null);
		if (skin == null) {
			return null;
		}
		var argument = new Argument(RenderType.entityTranslucent(skin.texture()));
		updatedProfiles.put(profile, argument);
		return argument;
	}

	@Override
	public void render(@Nullable Argument argument, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, boolean bl) {
		var arg = Objects.requireNonNullElse(argument, defaultArgument);
		if (itemDisplayContext == ItemDisplayContext.GROUND) {
			poseStack.pushPose();
			poseStack.translate(0.2F, -0.8F, -0.2F); // FIXME these values make no sense
			poseStack.mulPose(new Quaternionf().rotateXYZ(65F * (float) (Math.PI / 180.0), -15F * (float) (Math.PI / 180.0), 0F));
		}
		poseStack.pushPose();
		poseStack.translate(0.5F, 1.5F, 0.5F);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		VertexConsumer vertexConsumer = multiBufferSource.getBuffer(arg.renderType());
		model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		if (itemDisplayContext == ItemDisplayContext.GROUND) {
			poseStack.popPose();
		}
	}

	@Override
	public void getExtents(Set<Vector3f> set) {

	}

	public record Argument(RenderType renderType) { }

	public record Unbaked(LimbType limbType, LimbOrientation limbOrientation) implements SpecialModelRenderer.Unbaked {

		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
				LimbType.CODEC.fieldOf("limb_type").forGetter(Unbaked::limbType),
				LimbOrientation.CODEC.fieldOf("limb_orientation").forGetter(Unbaked::limbOrientation)
			).apply(instance, Unbaked::new)
		);

		@Override
		public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public @NotNull SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
			var layer = limbType == LimbType.LEG
				? limbOrientation == LimbOrientation.LEFT ? MannequinClient.LEFT_LEG_LAYER : MannequinClient.RIGHT_LEG_LAYER
				: limbOrientation == LimbOrientation.LEFT ? MannequinClient.LEFT_ARM_LAYER : MannequinClient.RIGHT_ARM_LAYER;
			var model = new LimbModel(entityModelSet.bakeLayer(layer));
			return new LimbSpecialRenderer(Minecraft.getInstance().getSkinManager(), model, new Argument(RenderType.entityTranslucent(DefaultPlayerSkin.getDefaultTexture())));
		}
	}
}
