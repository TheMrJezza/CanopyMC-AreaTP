package com.canopymc.area_tp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.canopymc.area_tp.ATMain;
import com.canopymc.area_tp.Settings;
import com.canopymc.area_tp.common.AreaData;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class AreaSethomeCMD implements CommandExecutor {

	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!(cs instanceof Player)) {
			if (cs instanceof ConsoleCommandSender) {
				ATMain.getInstance().info("§cOnly ingame players can excute this command");
			} else
				cs.sendMessage("§cOnly ingame players can excute this command");
			return true;
		}
		Player player = (Player) cs;
		Claim claim = getValidClaim(player);
		if (claim != null) {
			AreaData original = AreaData.getData(player.getUniqueId(), claim.getID());

			if (args.length < 1) {
				if (original != null) {
					original.setHome(player.getLocation());
					cs.sendMessage("Claim Home Updated!");
					return true;
				}
				cs.sendMessage("§cYou need to specify a name for this Claim Home.\n§7>> §r/" + alias + " <name>");
				return true;
			}

			String name = args[0];

			AreaData nc = AreaData.getData(player.getUniqueId(), name);

			if (original == null && nc == null) {
				AreaData.createData(claim, name, player);
				cs.sendMessage(String.format(
						"§aClaim home set to your location!\n§7>> §eUse §7/home %s §eto return to this location.",
						name));
				return true;
			}

			if (nc == null) {
				original.setName(name);
				original.setHome(player.getLocation());
				cs.sendMessage("§aHome location and name have been updated!");
				return true;
			}

			if (original == null || !original.equals(nc)) {
				cs.sendMessage("§cYou already have another home using that name!");
				return true;
			}

			original.setHome(player.getLocation());
			cs.sendMessage(String.format(
					"§aClaim home set to your location!\n§7>> §eUse §7/home %s §eto return to this location.",
					original.getName()));
		}
		return true;
	}

	private Claim getValidClaim(Player player) {
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
		if (claim == null) {
			// Not Standing in a claim.
			player.sendMessage(Settings.notInClaim());
			return null;
		}
		if (claim.parent != null) {
			claim = claim.parent;
		}
		if (claim.isAdminClaim() || claim.ownerID.equals(player.getUniqueId())) {
			// You cannot control this claim.
			player.sendMessage(Settings.noClaimAccess());
			return null;
		}
		if (claim.getArea() < Settings.minimumAreaThreshold()) {
			// Claim too small
			player.sendMessage(Settings.areaBelowThreshold());
			return null;
		}
		return claim;
	}
}