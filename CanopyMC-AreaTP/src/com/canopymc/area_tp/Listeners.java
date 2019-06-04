package com.canopymc.area_tp;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import com.canopymc.area_tp.common.AreaData;
import com.canopymc.area_tp.common.PlayerData;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimModifiedEvent;

public class Listeners implements Listener {
	@EventHandler
	private void onClaimDelete(ClaimDeletedEvent evt) {
		Claim claim = evt.getClaim();
		if (claim == null) {
			return; // You never know...
		}
		AreaData.deleteData(claim.getID());
		PlayerData.remove(claim.getID());
	}

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
	
	@EventHandler
	public void onClaimChange(ClaimModifiedEvent evt) {
		Bukkit.broadcastMessage("A Claim was changed!");
	}
}
