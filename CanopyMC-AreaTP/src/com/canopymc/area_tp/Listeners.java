package com.canopymc.area_tp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

public class Listeners implements Listener {


	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent evt) {
		if (!Settings.cancelOnMove() || evt.getFrom().distance(evt.getTo()) <= 0.1)
			return;
		BukkitTask task = ATMain.getCountdowns().remove(evt.getPlayer().getUniqueId());
		if (task != null) {
			task.cancel();
			evt.getPlayer().sendMessage(Settings.teleportCancelled());
		}
	}
}
