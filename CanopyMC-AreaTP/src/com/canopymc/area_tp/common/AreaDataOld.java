/*
 * Believe me, I totally agree with anyone who calls this an absolute Cluster F*ck.
 * 
 * It is what it is. But any improvements are most welcome. ;)
 */

package com.canopymc.area_tp.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.canopymc.area_tp.ATMain;
import com.canopymc.area_tp.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class AreaDataOld {
	private static DataStore datastore;
	private static List<AreaDataOld> cache = new ArrayList<>();

	private UUID playerID;
	private long baseID;
	private String name;
	private Location location;
	
	private static Gson gson;

	static {
		datastore = GriefPrevention.instance.dataStore;
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Location.class, new LocationAdapter());
		builder.setPrettyPrinting();
		gson = builder.create();
	}

	private AreaDataOld(Claim claim, String name, Player player) {
		this.playerID = player.getUniqueId();
		baseID = claim.getID();
		setHome(player.getLocation());
		setName(name);
	}

	public Location getHome() {
		Claim claim = GriefPrevention.instance.dataStore.getClaim(baseID);
		if (claim == null) {
			cache.remove(this);
			return null;
		}
		return location;
	}

	public void setHome(Location loc) {
		location = loc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		name = name.trim().toLowerCase().replace(" ", "_");
		this.name = name;
	}

	public static AreaDataOld getData(UUID playerID, long claimID) {
		for (AreaDataOld nc : getHomes(playerID)) {
			if (nc.baseID == claimID) {
				return nc;
			}
		}
		return null;
	}

	public static AreaDataOld getData(UUID playerID, String claimName) {
		for (AreaDataOld nc : getHomes(playerID)) {
			if (nc.name.trim().equalsIgnoreCase(claimName.trim())) {
				return nc;
			}
		}
		return null;
	}
	
	public static void remove(AreaDataOld data) {
		cache.remove(data);
	}

	public static ArrayList<AreaDataOld> getHomes(UUID playerID) {
		cleanup(playerID);
		ArrayList<AreaDataOld> result = new ArrayList<>();
		for (AreaDataOld nc : cache) {
			if (!nc.playerID.equals(playerID))
				continue;
			result.add(nc);
		}
		return result;
	}

	public static void saveData() {
		cleanupAll();
		ATMain.getInstance().getDataFolder().mkdirs();
		File file = new File(ATMain.getInstance().getDataFolder(), "DataStore.json");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			gson.toJson(cache, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadData() {
		if (ATMain.getInstance().getDataFolder().isDirectory()) {
			File file = new File(ATMain.getInstance().getDataFolder(), "DataStore.json");
			if (!file.isFile() || !file.getName().endsWith(".json"))
				return;
			try {
				BufferedReader out = new BufferedReader(new FileReader(file));
				cache = gson.fromJson(out, new TypeToken<ArrayList<AreaDataOld>>() {
				}.getType());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		cleanupAll();
	}

	private static void cleanupAll() {
		Iterator<AreaDataOld> iter = cache.iterator();
		while (iter.hasNext()) {
			AreaDataOld nc = iter.next();
			Claim claim = datastore.getClaim(nc.baseID);
			if (claim == null || claim.isAdminClaim() || !claim.ownerID.equals(nc.playerID)
					|| claim.getArea() < Settings.minimumAreaThreshold()) {
				cache.remove(nc);
			}
		}
	}

	private static void cleanup(UUID playerID) {
		Iterator<AreaDataOld> iter = cache.stream().filter(n -> n.playerID.equals(playerID)).iterator();
		while (iter.hasNext()) {
			AreaDataOld named = iter.next();
			Claim claim = datastore.getClaim(named.baseID);
			if (claim == null || claim.isAdminClaim() || !claim.ownerID.equals(named.playerID)
					|| claim.getArea() < 300) {
				cache.remove(named);
			}
		}
	}

	public static AreaDataOld createData(Claim claim, String name, Player player) {
		AreaDataOld data = new AreaDataOld(claim, name, player);
		cache.add(data);
		return data;
	}
}