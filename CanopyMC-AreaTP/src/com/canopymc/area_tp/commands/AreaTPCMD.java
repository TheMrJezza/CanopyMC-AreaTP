package com.canopymc.area_tp.commands;

import java.util.UUID;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.canopymc.area_tp.ATMain;
import com.canopymc.area_tp.Settings;
import com.canopymc.area_tp.Sounds;
import com.google.common.primitives.Ints;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

public class AreaTPCMD implements CommandExecutor {

	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!(cs instanceof Player)) {
			if (cs instanceof ConsoleCommandSender) {
				ATMain.getInstance().info("§cOnly ingame players can excute this command");
			} else
				cs.sendMessage("§cOnly ingame players can excute this command");
			return true;
		}
		Player player = (Player) cs;
		if (!cs.hasPermission("canopymc.areateleport.use")) {
			cs.sendMessage(Settings.noPermission());
			ATMain.getInstance().playSound(player, Sounds.VILLAGER_NO.getBukkitSound());
			return true;
		}
		if (args.length < 1) {
			listData(player, player.getUniqueId(), alias);
			return true;
		}
		UUID targetUUID = getTargetUUID(args[0], player.getUniqueId());
		if (cs.hasPermission("canopymc.areateleport.other")
				|| (targetUUID != null && targetUUID.equals(player.getUniqueId()))) {
			if (Ints.tryParse(args[0]) == null) {
				if (targetUUID != null) {
					// cs.sendMessage(Settings.invalidPlayer());
					// ATMain.getInstance().playSound(player, Sounds.VILLAGER_NO.getBukkitSound());
					// return true;
					if (args.length > 1) {
						process(player, args[1], targetUUID);
						return true;
					}
					listData(player, targetUUID, alias);
					return true;
				}
			}
		}
		process(player, args[0], null);
		return true;
	}

	private void process(Player player, String arg, UUID override) {
		String message = Settings.invalidPlayer();
		if (override == null) {
			override = player.getUniqueId();
			message = Settings.noClaimData();
		}
		PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(override);
		Vector<Claim> claims = data.getClaims();
		if (claims.isEmpty()) {
			player.sendMessage(message);
			return;
		}
		Integer claimNum = Ints.tryParse(arg);
		if (claimNum == null) {
			player.sendMessage(Settings.invalidArgument());
			ATMain.getInstance().playSound(player, Sounds.VILLAGER_NO.getBukkitSound());
			return;
		}
		if (claimNum < 0 || claimNum >= claims.size()) {
			player.sendMessage(Settings.indexOutOfBounds());
			ATMain.getInstance().playSound(player, Sounds.VILLAGER_NO.getBukkitSound());
			return;
		}

		teleport(player, claims.get(claimNum));
	}

	private Location getCentreOfClaim(Claim claim) {
		Location greater = claim.getGreaterBoundaryCorner(), lesser = claim.getLesserBoundaryCorner();
		int x1 = greater.getBlockX(), x2 = lesser.getBlockX();
		int z1 = greater.getBlockZ(), z2 = lesser.getBlockZ();
		int x = (x1 - x2) / 2 + x2, z = (z1 - z2) / 2 + z2, y = lesser.getWorld().getHighestBlockYAt(x, z);

		return new Location(lesser.getWorld(), x + 0.5, y, z + 0.5);
	}

	public void teleport(Player player, Claim claim) {
		BukkitTask current = ATMain.getCountdowns().remove(player.getUniqueId());
		if (current != null) {
			current.cancel();
		}

		Location centre = getCentreOfClaim(claim);

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
					centre.getChunk(); // Load the chunk
					player.teleport(centre);
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

	private void listData(Player reciever, UUID dataOwner, String alias) {
		PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(dataOwner);
		Vector<Claim> claims = data.getClaims();
		if (claims.isEmpty()) {
			reciever.sendMessage(Settings.noClaimData());
			return;
		}
		StringBuilder builder = new StringBuilder("§7==§6ID§7======[§6Available Claims§7]==========\n \n");

		for (int i = 0; i < claims.size(); i++) {
			Location centre = getCentreOfClaim(claims.get(i));
			builder.append(String.format(" §7- §6%s §7(near X: §6%S §7Z: §6%s §7World: §6%s§7)\n", i,
					centre.getBlockX(), centre.getBlockZ(), centre.getWorld().getName()));
		}

		builder.append(" \nUse §6/" + alias + " <ID> §7 to teleport to a claim.\n ");
		reciever.sendMessage(builder.toString());
		ATMain.getInstance().playSound(reciever, Sounds.LEVEL_UP.getBukkitSound());
	}

	private UUID getTargetUUID(String input, UUID ignore) {
		UUID last = null;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getDisplayName().toLowerCase().contains(input.toLowerCase())) {
				if (!player.getUniqueId().equals(ignore)) {
					return player.getUniqueId();
				}
				last = ignore;
			}
		}
		if (last != null) {
			return last;
		}

		for (OfflinePlayer offPlayer : Bukkit.getOfflinePlayers()) {
			if (offPlayer.getName().equalsIgnoreCase(input)) {
				return offPlayer.getUniqueId();
			}
		}
		return null;
	}
}