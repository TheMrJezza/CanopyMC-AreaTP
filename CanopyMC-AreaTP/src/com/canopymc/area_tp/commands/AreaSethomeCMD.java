package com.canopymc.area_tp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.canopymc.area_tp.ATMain;
import com.canopymc.area_tp.Settings;
import com.canopymc.area_tp.common.CustomClaimData;

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
			if (CustomClaimData.setLocation(player.getUniqueId(), claim, player.getLocation())) {
				player.sendMessage("Claim Home Changed Successfully!");
			} else
				player.sendMessage("You must be standing in the claim to set it's home!");
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
		if (claim.isAdminClaim() || !claim.ownerID.equals(player.getUniqueId())) {
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