package com.acikek.mannequin.mixin.client;

import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerHeadSpecialRenderer.class)
public interface PlayerHeadSpecialRendererAccess {

	@Accessor
	SkullModelBase getModelBase();

	@Accessor
	PlayerHeadSpecialRenderer.PlayerHeadRenderInfo getDefaultPlayerHeadRenderInfo();
}
