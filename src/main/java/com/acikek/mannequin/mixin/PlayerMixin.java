package com.acikek.mannequin.mixin;

import com.acikek.mannequin.util.MannequinEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void mannequin$createLimbs(Level level, GameProfile gameProfile, CallbackInfo ci) {
		if (((Player) (Object) this) instanceof MannequinEntity mannequinEntity) {
			mannequinEntity.mannequin$getLimbs().setProfile(new ResolvableProfile(gameProfile));
		}
	}
}
