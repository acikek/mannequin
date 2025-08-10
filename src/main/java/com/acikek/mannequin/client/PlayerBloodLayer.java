package com.acikek.mannequin.client;

import com.acikek.mannequin.util.MannequinRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.core.particles.ParticleTypes;

public class PlayerBloodLayer extends RenderLayer<PlayerRenderState, PlayerModel> {

	public PlayerBloodLayer(RenderLayerParent<PlayerRenderState, PlayerModel> renderLayerParent) {
		super(renderLayerParent);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState entityRenderState, float f, float g) {
		if (entityRenderState.isInvisible || !(entityRenderState instanceof MannequinRenderState mannequinRenderState)) {
			return;
		}
		Minecraft.getInstance().levelRenderer.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, entityRenderState.x, entityRenderState.y, entityRenderState.z, 0.0, 0.0, 0.0);
	}
}
