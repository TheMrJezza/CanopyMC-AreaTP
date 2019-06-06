package com.canopymc.area_tp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.canopymc.area_tp.ATMain;

public abstract class AbstractCommand implements CommandExecutor {
	private final String cmdName;
	private final String permission;
	private final boolean canConsoleUse;

	public AbstractCommand(String commandName, String permission, boolean canConsoleUse) {
		cmdName = commandName;
		this.permission = permission;
		this.canConsoleUse = canConsoleUse;
		ATMain.getInstance().getCommand(commandName).setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!cmd.getName().equalsIgnoreCase(cmdName)) return true;
		if (permission != null && !cs.hasPermission(permission)) {
			cs.sendMessage("You don't have permission for this.");
			return true;
		}
		if (!canConsoleUse && !(cs instanceof Player)) {
			cs.sendMessage("Only in-game players may use this command!");
			return true;
		}
		execute(cs, args, alias);
		return true;
	}
	
	public final static void registerCommands() {
	    new A_Home_CMD();
	}

	public abstract void execute(CommandSender cs, String[] args, String alias);
}