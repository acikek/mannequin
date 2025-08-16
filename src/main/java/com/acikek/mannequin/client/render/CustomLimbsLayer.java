package com.acikek.mannequin.client.render;

import com.acikek.mannequin.util.MannequinRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;

public class CustomLimbsLayer extends RenderLayer<PlayerRenderState, PlayerModel> {

	public PlayerModel wide;
	public PlayerModel slim;

	public CustomLimbsLayer(RenderLayerParent<PlayerRenderState, PlayerModel> renderLayerParent, EntityRendererProvider.Context context) {
		super(renderLayerParent);
		wide = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
		slim = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState entityRenderState, float f, float g) {
		if (!(entityRenderState instanceof MannequinRenderState mannequinRenderState) || mannequinRenderState.mannequin$getLimbs() == null) {
			return;
		}
		var limbs = mannequinRenderState.mannequin$getLimbs();
		mannequinRenderState.mannequin$setLimbs(null);
		wide.setupAnim(entityRenderState);
		slim.setupAnim(entityRenderState);
		mannequinRenderState.mannequin$setLimbs(limbs);
		for (var limb : mannequinRenderState.mannequin$getLimbs().getParts()) {
			if (limb.severed) {
				continue;
			}
			var skin = limb.profile.isPresent() && limb.profile.get().isResolved()
				? Minecraft.getInstance().getSkinManager().getInsecureSkin(limb.profile.get().gameProfile(), null)
				: DefaultPlayerSkin.getDefaultSkin();
			if (skin == null) {
				continue;
			}
			var model = skin.model() == PlayerSkin.Model.WIDE ? wide : slim;
			var part = switch (limb.type) {
				case LEG -> switch (limb.orientation) {
					case LEFT -> model.leftLeg;
					case RIGHT -> model.rightLeg;
					default -> null;
				};
				case ARM -> switch (limb.orientation) {
					case LEFT -> model.leftArm;
					case RIGHT -> model.rightArm;
					default -> null;
				};
				default -> null;
			};
			if (part == null) {
				continue;
			}
			var renderType = RenderType.entityTranslucent(skin.texture());
			var vertexConsumer = multiBufferSource.getBuffer(renderType);
			part.render(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
		}
	}
}
