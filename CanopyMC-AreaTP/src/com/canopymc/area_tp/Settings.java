package com.canopymc.area_tp;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	private static File file;
	private static YamlConfiguration settings;

	// Doubles
	private static double COUNTDOWN_DELAY;

	// Booleans
	private static boolean ENABLE_SOUND, CANCEL_ON_MOVE;

	// Messages
	private static String TELEPORT_SUCCESS, COUNTDOWN_INITIALIZED, COUNTDOWN_CANCELLED, INVALID_ARGUMENT,
			INDEX_OUT_OF_BOUNDS, INVALID_PLAYER, NO_PERMISSION, NO_CLAIM_DATA;

	public static void updateSettings() {
		file = new File(ATMain.getInstance().getDataFolder(), "Settings.yml");
		if (!file.exists()) {
			ATMain.getInstance().saveResource("Settings.yml", true);
		}
		
		settings = YamlConfiguration.loadConfiguration(file);
		
		COUNTDOWN_DELAY = settings.getDouble("TeleportDelay.TimeInSeconds", 0);
		ENABLE_SOUND = settings.getBoolean("EnableSound", true);
		CANCEL_ON_MOVE = settings.getBoolean("TeleportDelay.CancelOnMove", COUNTDOWN_DELAY > 0);
		
		TELEPORT_SUCCESS = getMessage("TeleportSuccess", "§aTeleporting...");
		COUNTDOWN_INITIALIZED = getMessage("AboutToTeleport", "§eStand still for §6" + COUNTDOWN_DELAY + " §eseconds, and you will be teleported.");
		COUNTDOWN_CANCELLED = getMessage("PlayerMoved", "§cClaim Teleport has been cancelled because you moved.");
		INVALID_ARGUMENT = getMessage("InvalidInput", "§cPlease use a numeric value such as §7'§60§7'§c.");
		INDEX_OUT_OF_BOUNDS = getMessage("InvalidClaim", "§cInvalid Choice.");
		INVALID_PLAYER = getMessage("InvalidPlayer", "§cCouldn't find any claim data for that player.");
		NO_PERMISSION = getMessage("NoPermission", "§cYou do not have access to that!");
		NO_CLAIM_DATA = getMessage("NoClaimData", "§7You haven't made any claims yet.");
	}

	private static String getMessage(String key, String defaultValue) {
		return ChatColor.translateAlternateColorCodes('&', settings.getString("Messages." + key, defaultValue))
				.replace("{DELAY}", COUNTDOWN_DELAY + "");
	}

	public static boolean isSoundEnabled() {
		return ENABLE_SOUND;
	}

	public static String noPermission() {
		return NO_PERMISSION;
	}

	public static boolean cancelOnMove() {
		return CANCEL_ON_MOVE;
	}

	public static String noClaimData() {
		return NO_CLAIM_DATA;
	}

	public static String invalidPlayer() {
		return INVALID_PLAYER;
	}

	public static String invalidArgument() {
		return INVALID_ARGUMENT;
	}

	public static String indexOutOfBounds() {
		return INDEX_OUT_OF_BOUNDS;
	}

	public static double getTeleportDelay() {
		return COUNTDOWN_DELAY;
	}

	public static String teleportCancelled() {
		return COUNTDOWN_CANCELLED;
	}

	public static String teleportSuccess() {
		return TELEPORT_SUCCESS;
	}

	public static String countdownStarted() {
		return COUNTDOWN_INITIALIZED;
	}
}