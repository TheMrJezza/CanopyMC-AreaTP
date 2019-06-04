package com.canopymc.area_tp.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;

import com.canopymc.area_tp.ATMain;
import com.google.gson.reflect.TypeToken;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class AreaData {
	private static HashMap<Long, Location> areaDataCache = new HashMap<>();
	private static DataStore datastore = GriefPrevention.instance.dataStore;

	public static Location getAreaHome(long claimID) {
		Claim claim = datastore.getClaim(claimID);
		if (claim == null) {
			areaDataCache.remove(claimID);
			return null;
		}
		if (areaDataCache.containsKey(claimID)) {
			Location loc = areaDataCache.get(claimID);
			if (loc != null) {
				return loc;
			}
			areaDataCache.remove(claimID);
		}
		return getCentreOfClaim(claim);
	}

	public static boolean setAreaHome(long claimID, Location loc) {
		if (loc == null) {
			return false;
		}
		Claim claim = datastore.getClaim(claimID);
		if (claim == null) {
			areaDataCache.remove(claimID);
			return false;
		}
		if (!claim.contains(loc, true, false)) {
			return false;
		}
		areaDataCache.put(claimID, loc);
		return true;
	}

	private static Location getCentreOfClaim(Claim claim) {
		if (claim == null) {
			return null;
		}

		if (claim.parent != null) {
			claim = claim.parent;
		}

		Location greater = claim.getGreaterBoundaryCorner(), lesser = claim.getLesserBoundaryCorner();
		int x1 = greater.getBlockX(), x2 = lesser.getBlockX();
		int z1 = greater.getBlockZ(), z2 = lesser.getBlockZ();
		int x = (x1 - x2) / 2 + x2, z = (z1 - z2) / 2 + z2, y = lesser.getWorld().getHighestBlockYAt(x, z);

		return new Location(lesser.getWorld(), x + 0.5, y, z + 0.5);
	}

	public static void deleteData(long claimID) {
		areaDataCache.remove(claimID);
	}

	public static void saveData() {
		cleanupAll();
		ATMain.getInstance().getDataFolder().mkdirs();
		File file = new File(ATMain.getInstance().getDataFolder(), "AreaDataStore.json");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			ATMain.getGson().toJson(areaDataCache, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadData() {
		if (ATMain.getInstance().getDataFolder().isDirectory()) {
			File file = new File(ATMain.getInstance().getDataFolder(), "AreaDataStore.json");
			try {
				BufferedReader out = new BufferedReader(new FileReader(file));
				areaDataCache = ATMain.getGson().fromJson(out, new TypeToken<HashMap<Long, Location>>() {
				}.getType());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		cleanupAll();
	}

	private static void cleanupAll() {
		for (Entry<Long, Location> entry : areaDataCache.entrySet()) {
			if (entry.getValue() == null) {
				areaDataCache.remove(entry.getKey());
				continue;
			}
			Claim claim = datastore.getClaim(entry.getKey());
			if (claim == null) {
				areaDataCache.remove(entry.getKey());
				continue;
			}
			if (!claim.contains(entry.getValue(), true, false)) {
				areaDataCache.remove(entry.getKey());
			}
		}
	}
}