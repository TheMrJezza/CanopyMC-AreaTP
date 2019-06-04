package com.canopymc.area_tp.commands;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.canopymc.area_tp.ATMain;
import com.canopymc.area_tp.common.AreaDataOld;

public class AreaHomeCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!(cs instanceof Player)) {
			if (cs instanceof ConsoleCommandSender) {
				ATMain.getInstance().info("§cOnly ingame players can excute this command");
			} else
				cs.sendMessage("§cOnly ingame players can excute this command");
			return true;
		}
		Player player = (Player) cs;
		ArrayList<AreaDataOld> homes = AreaDataOld.getHomes(player.getUniqueId());
		if (homes.isEmpty()) {
			cs.sendMessage("§cYou don't have any homes yet!");
			return true;
		}
		if (args.length < 1) {
			if (cmd.getName().equalsIgnoreCase("areahomes") || homes.size() > 1) {
				StringBuilder sb = new StringBuilder("§eClaim Homes§7: §r");
				boolean started = false;
				for (AreaDataOld home : homes) {
					if (started)
						sb.append("§7, §r");
					else
						started = true;
					sb.append(home.getName());
				}
				sb.append("§7.");
				cs.sendMessage(sb.toString() /*+ "\n§7>> §r/" + alias + " <home>"*/);
				return true;
			}
			Location loc = homes.get(0).getHome();
			if (loc == null) {
				player.performCommand("ahomes");
				Pattern pat = Pattern.compile("");
				pat.matcher("").matches();
				return true;
			}
			player.teleport(loc);
			return true;
		}
		
		AreaDataOld nc = AreaDataOld.getData(player.getUniqueId(), args[0]);
		if (nc == null) {
			player.performCommand("ahomes");
			return true;
		}

		Location loc = nc.getHome();
		if (loc == null) {
			cs.sendMessage("§4Error§7: §cThat claim has either been deleted or Transferred!");
			return true;
		}
		
		player.teleport(loc);
		return true;
	}

}