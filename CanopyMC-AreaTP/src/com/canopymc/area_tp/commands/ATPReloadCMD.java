package com.canopymc.area_tp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.canopymc.area_tp.ATMain;
import com.canopymc.area_tp.Settings;
import com.canopymc.area_tp.common.AreaData;

public class ATPReloadCMD implements CommandExecutor {

	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!cs.hasPermission("canopymc.areateleport.reload")) {
			cs.sendMessage(Settings.noPermission());
			return true;
		}
		Settings.updateSettings();
		AreaData.saveData();
		cs.sendMessage("§a§7[§a" + ATMain.getInstance().getDescription().getName() + "§7] §aConfig Reloaded");
		return true;
	}
}