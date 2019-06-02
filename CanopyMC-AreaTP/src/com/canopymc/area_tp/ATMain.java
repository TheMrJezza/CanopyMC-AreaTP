package com.canopymc.area_tp;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.canopymc.area_tp.commands.ATPReloadCMD;
import com.canopymc.area_tp.commands.AreaTPCMD;

public class ATMain extends JavaPlugin implements Listener {
	private PluginManager pm = Bukkit.getPluginManager();
	private static ATMain instance;

	private static HashMap<UUID, BukkitTask> delays = new HashMap<>();

	public void onEnable() {
		if (!pm.isPluginEnabled("GriefPrevention")) {
			info("§cGriefPrevention hasn't loaded! (Is it installed?)");
			info("AreaTp will be Disabled.");
			pm.disablePlugin(this);
			return;
		}
		instance = this;
		Settings.updateSettings();
		getCommand("areateleport").setExecutor(new AreaTPCMD());
		getCommand("atpreload").setExecutor(new ATPReloadCMD());
		pm.registerEvents(this, this);
		new Metrics(this);
		info("Plugin Loaded Successfully!");
	}

	public void onDisable() {
		getCommand("areateleport").setExecutor(null);
		getCommand("atpreload").setExecutor(null);
		HandlerList.unregisterAll((Plugin) this);
		instance = null;
	}

	public void info(String input) {
		Bukkit.getConsoleSender().sendMessage(String.format("§7[§a%s§7] §e", getDescription().getName()) + input);
	}

	public static ATMain getInstance() {
		return instance;
	}

	public void playSound(Player player, Sound sound) {
		if (Settings.isSoundEnabled() && sound != null && player != null)
			player.playSound(player.getLocation(), sound, 10.0F, 1.1F);
	}

	public static HashMap<UUID, BukkitTask> getCountdowns() {
		return delays;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent evt) {
		if (!Settings.cancelOnMove() || evt.getFrom().distance(evt.getTo()) <= 0.1)
			return;
		BukkitTask task = delays.remove(evt.getPlayer().getUniqueId());
		if (task != null) {
			task.cancel();
			evt.getPlayer().sendMessage(Settings.teleportCancelled());
		}
	}
}
