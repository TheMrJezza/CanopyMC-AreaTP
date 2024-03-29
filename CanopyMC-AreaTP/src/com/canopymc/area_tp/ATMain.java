package com.canopymc.area_tp;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.canopymc.area_tp.commands.ATPReloadCMD;
import com.canopymc.area_tp.commands.AbstractCommand;
import com.canopymc.area_tp.commands.AreaSethomeCMD;
import com.canopymc.area_tp.commands.AreaTPCMD;
import com.canopymc.area_tp.common.CustomClaimData;
import com.canopymc.area_tp.common.LocationAdapter;
import com.canopymc.area_tp.tasks.AutoSaveDataTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ATMain extends JavaPlugin {
	private PluginManager pm = Bukkit.getPluginManager();
	private static ATMain instance;

	private static Gson gson;

	private static HashMap<UUID, BukkitTask> delays = new HashMap<>();
	private static AutoSaveDataTask saveDataTask = new AutoSaveDataTask();

	public void onEnable() {
		if (!pm.isPluginEnabled("GriefPrevention")) {
			info("�cGriefPrevention hasn't loaded! (Is it installed?)");
			info("AreaTp will be Disabled.");
			pm.disablePlugin(this);
			return;
		}
		instance = this;
		Settings.updateSettings();
		AbstractCommand.registerCommands();
		getCommand("areateleport").setExecutor(new AreaTPCMD());
		getCommand("atpreload").setExecutor(new ATPReloadCMD());
		getCommand("areasethome").setExecutor(new AreaSethomeCMD());
		pm.registerEvents(new Listeners(), this);
		new Metrics(this);
		gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).setPrettyPrinting()
				.serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
		new BukkitRunnable() {
			public void run() {
				CustomClaimData.loadAll();
			}
		}.runTaskAsynchronously(this);
		saveDataTask.runTaskTimerAsynchronously(this, 60, 60);
		info("Plugin Loaded Successfully!");
	}

	public void onDisable() {
		getCommand("areateleport").setExecutor(null);
		getCommand("atpreload").setExecutor(null);
		getCommand("areahomes").setExecutor(null);
		HandlerList.unregisterAll((Plugin) this);
		saveDataTask.cancel();
		new BukkitRunnable() {
			public void run() {
				CustomClaimData.saveAll();
			}
		}.runTaskAsynchronously(this);
		gson = null;
		instance = null;
	}

	public void info(String input) {
		Bukkit.getConsoleSender().sendMessage(String.format("�7[�a%s�7] �e", getDescription().getName()) + input);
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

	public static Gson getGson() {
		return gson;
	}
}
