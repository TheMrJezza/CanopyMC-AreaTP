package com.canopymc.area_tp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.canopymc.area_tp.ATMain;
import com.canopymc.area_tp.common.AreaData;

public class AreaDelhomeCMD implements CommandExecutor {
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!(cs instanceof Player)) {
			if (cs instanceof ConsoleCommandSender) {
				ATMain.getInstance().info("§cOnly ingame players can excute this command");
			} else
				cs.sendMessage("§cOnly ingame players can excute this command");
			return true;
		}	
		Player player = (Player) cs;
		if (args.length < 1) {
			cs.sendMessage("§cYou must specify a home to delete!\n§7>> §r/" + alias + " <claim_name>");
			return true;
		}
		String name = args[0];

		AreaData current = AreaData.getData(player.getUniqueId(), name);
		if (current != null) {
			AreaData.remove(current);
			cs.sendMessage("§aClaim Home Deleted!");
			return true;
		}
		cs.sendMessage("§cThat Claim Home doesn't exist!");
		return true;
	}
}
