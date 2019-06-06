package com.canopymc.area_tp.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.canopymc.area_tp.common.CustomClaimData;

public class AutoSaveDataTask extends BukkitRunnable {
	@Override
	public void run() {
		CustomClaimData.saveAll();
	}
}
