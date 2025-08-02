package com.acikek.mannequin.mixin.client;

import com.acikek.mannequin.util.SeveredLimb;
import com.acikek.mannequin.util.SeveringRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin implements SeveringRenderState {

	@Unique
	private List<SeveredLimb> severedLimbs;

	@Override
	public List<SeveredLimb> mannequin$getSeveredLimbs() {
		return severedLimbs;
	}

	@Override
	public void mannequin$setSeveredLimbs(List<SeveredLimb> severedLimbs) {
		this.severedLimbs = severedLimbs;
	}
}
