package com.canopymc.area_tp;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import com.google.common.primitives.Ints;

public enum Sounds {
	ENDERMAN_TELEPORT(s("ENDERMAN_TELEPORT", 8), s("ENTITY_ENDERMAN_TELEPORT", 14)),
	VILLAGER_NO(s("VILLAGER_NO", 8), s("ENTITY_VILLAGER_NO", 14)),
	LEVEL_UP(s("LEVEL_UP", 8), s("ENTITY_PLAYER_LEVELUP", 14)),
	CLICK(s("WOOD_CLICK", 8), s("BLOCK_WOOD_BUTTON_CLICK_ON", 13), s("BLOCK_STONE_BUTTON_CLICK_ON", 14));

	static {
		Integer value = Ints.tryParse(Bukkit.getServer().getClass().getPackage().getName().split("_")[1]);
		version = value != null ? value.intValue() : -1;
	}

	private static int version;

	private CustomSound[] cusSounds;

	Sounds(CustomSound... sounds) {
		cusSounds = sounds;
	}

	public Sound getBukkitSound() {
		CustomSound result = null;
		for (CustomSound sound : cusSounds) {
			if (sound.highestTargetVersion() > version) {
				continue;
			}
			if (sound.highestTargetVersion() == version) {
				return Sound.valueOf(sound.getBukkitName());
			}
			if (result == null || result.highestTargetVersion() < sound.highestTargetVersion()) {
				result = sound;
			}
		}
		if (result != null) {
			return Sound.valueOf(result.getBukkitName());
		}
		return null;
	}

	private static CustomSound s(String bukkitName, int highestSupportedVersion) {
		return new CustomSound(bukkitName, highestSupportedVersion);
	}
}

class CustomSound {
	private int highestVersion;
	private String name;

	public CustomSound(String soundName, int highestVersion) {
		name = soundName;
		this.highestVersion = highestVersion;
	}

	public String getBukkitName() {
		return name;
	}

	public double highestTargetVersion() {
		return highestVersion;
	}
}