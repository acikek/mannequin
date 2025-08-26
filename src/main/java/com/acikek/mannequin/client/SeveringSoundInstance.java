package com.acikek.mannequin.client;

import com.acikek.mannequin.sound.MannequinSounds;
import com.acikek.mannequin.util.MannequinEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class SeveringSoundInstance extends AbstractTickableSoundInstance {

	public Player player;

	protected SeveringSoundInstance(Player player) {
		super(MannequinSounds.SEVERING, SoundSource.PLAYERS, RandomSource.create());
		this.player = player;
		attenuation = Attenuation.LINEAR;
		looping = true;
		delay = 0;
	}

	@Override
	public void tick() {
		if (!player.isAlive() || !(player instanceof MannequinEntity mannequinEntity) || !mannequinEntity.mannequin$getData().severing) {
			stop();
			return;
		}
		x = player.getX();
		y = player.getY();
		z = player.getZ();
		volume = 0.7F;
		pitch = 1.0F;
	}
}
