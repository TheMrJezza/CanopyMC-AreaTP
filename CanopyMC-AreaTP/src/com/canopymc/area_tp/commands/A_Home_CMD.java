package com.canopymc.area_tp.commands;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.canopymc.area_tp.ATMain;
import com.canopymc.area_tp.Settings;
import com.canopymc.area_tp.Sounds;
import com.canopymc.area_tp.common.CustomClaimData;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class A_Home_CMD extends AbstractCommand {

	public A_Home_CMD() {
		super("areahome", "canopymc.areatp.command.areahome", false);
	}

	@Override
	public void execute(CommandSender cs, String[] args, String alias) {
		Player player = (Player) cs;
		Set<CustomClaimData> dataSet = CustomClaimData.getDataFromOwner(player.getUniqueId());
		Iterator<CustomClaimData> it = dataSet.iterator();
		while (it.hasNext()) {
			CustomClaimData data = it.next();
			Claim claim = GriefPrevention.instance.dataStore.getClaim(data.getClaimID());
			if (claim == null || !data.hasName() || claim.getArea() < Settings.minimumAreaThreshold()
					|| !claim.ownerID.equals(player.getUniqueId())) {
				dataSet.remove(data);
			}
		}
		if (dataSet.isEmpty()) {
			cs.sendMessage("You don't have any available Claim Homes.");
			return;
		}
		if (args.length < 1) {
			if (dataSet.size() == 1) {
				for (CustomClaimData data : dataSet) {
					teleport(player, data.getClaimLocation());
					return;
				}
			}
			player.performCommand("areahomes");
			return;
		}
		for (CustomClaimData data : dataSet) {
			if (data.getName().trim().equalsIgnoreCase(args[0])) {
				teleport(player, data.getClaimLocation());
				return;
			}
		}
		cs.sendMessage("You don't have any claims by that name!");
	}

	public void teleport(Player player, Location home) {
		BukkitTask current = ATMain.getCountdowns().remove(player.getUniqueId());
		if (current != null) {
			current.cancel();
		}

		int delay = (int) (20 * Settings.getTeleportDelay());
		if (delay > 0) {
			player.sendMessage(Settings.countdownStarted());
		}
		ATMain.getCountdowns().put(player.getUniqueId(), new BukkitRunnable() {
			private long count = 0;

			public void run() {
				if (count % 20 == 0) {
					ATMain.getInstance().playSound(player, Sounds.CLICK.getBukkitSound());
				}
				count++;
				if (count >= delay) {
					home.getChunk(); // Load the chunk
					player.teleport(home);
					player.sendMessage(Settings.teleportSuccess());
					ATMain.getCountdowns().remove(player.getUniqueId());
					new BukkitRunnable() {
						public void run() {
							ATMain.getInstance().playSound(player, Sounds.ENDERMAN_TELEPORT.getBukkitSound());
						}
					}.runTaskLater(ATMain.getInstance(), 2l);
					cancel();
				}
			}
		}.runTaskTimer(ATMain.getInstance(), 0, 0));
	}
}