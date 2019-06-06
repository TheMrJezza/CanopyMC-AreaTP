/*
 * So... This is slightly less of a cluster fu*k than the old Data System... Still a cluster tho...
 */

package com.canopymc.area_tp.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import com.canopymc.area_tp.ATMain;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.Ints;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class CustomClaimData {
	private static File dataBase = new File(ATMain.getInstance().getDataFolder(), "AreaData");
	private static Table<Long, UUID, CustomClaimData> dataCache = HashBasedTable.create();
	private static DataStore datastore = GriefPrevention.instance.dataStore;
	private boolean modified = true;
	@Expose
	private Location claimLocation = null;
	@Expose
	private long claimID;
	@Expose
	private UUID dataOwner = null;
	@Expose
	private String customName = null;

	private CustomClaimData(UUID player, @Nonnull Claim claim) {
		if (claim == null) {
			return;
		}
		claimID = claim.getID();
		dataOwner = player;
		dataCache.put(claimID, dataOwner, this);
	}

	private void save() {
		if (!modified)
			return;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(dataBase + File.separator + claimID,
					dataOwner != null ? dataOwner.toString() : "default" + ".json")));
			ATMain.getGson().toJson(this, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		modified = false;
	}

	public static void saveAll() {
		HashSet<Long> deleteThese = new HashSet<>();
		Iterator<CustomClaimData> itt = dataCache.values().iterator();
		while (itt.hasNext()) {
			CustomClaimData data = itt.next();
			if (datastore.getClaim(data.claimID) == null && !deleteThese.contains(data.claimID)) {
				deleteThese.add(data.claimID);
				for (UUID uuid : dataCache.columnKeySet()) {
					dataCache.remove(data.claimID, uuid);
				}
				continue;
			}
			data.save();
		}
		for (long claimID : deleteThese) {
			File file = new File(dataBase + File.separator + claimID);
			if (file.exists()) {
				try {
					FileUtils.deleteDirectory(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Location getLocation(UUID playerID, @Nonnull Claim claim) {
		if (claim != null && dataCache.contains(claim.getID(), playerID)) {
			Location location = dataCache.get(claim.getID(), playerID).claimLocation;
			if (location != null)
				return location;
		}
		return getCentreOfClaim(claim);
	}

	public static String getName(UUID playerID, @Nonnull Claim claim) {
		if (claim != null && dataCache.contains(claim.getID(), playerID)) {
			String name = dataCache.get(claim.getID(), playerID).customName;
			if (name != null) {
				return name;
			}
		}
		return "Claim#" + claim.getID();
	}

	public static boolean isNameSet(UUID playerID, @Nonnull Claim claim) {
		if (claim != null && dataCache.contains(claim.getID(), playerID)) {
			return dataCache.get(claim.getID(), playerID).customName != null;
		}
		return false;
	}

	public static Set<CustomClaimData> getDataFromOwner(UUID uuid) {
		HashSet<CustomClaimData> result = new HashSet<>();
		for (CustomClaimData data : dataCache.values()) {
			if (data.dataOwner.equals(uuid)) {
				result.add(data);
			}
		}
		return result;
	}

	/**
	 * Sets the {@link Location} of the specified {@link Claim} for the specified
	 * {@link Player} {@link UUID}. The claim cannot be null. The playerID and
	 * location can be null.
	 * 
	 * @param playerID The UUID of the player that this data belongs to.
	 * @param claim    The Claim that is data belongs to. Cannot be NULL.
	 * @param location The Location to change this data's location to. Must be
	 *                 inside the claim.
	 * @return False if the claim is null or the location isn't inside of the claim.
	 *         Otherwise True because the claim location was updated successfully.
	 */
	public static boolean setLocation(UUID playerID, @Nonnull Claim claim, Location location) {
		if (claim == null || location != null && !claim.contains(location, true, false)) {
			return false;
		}
		if (!dataCache.contains(claim.getID(), playerID)) {
			new CustomClaimData(playerID, claim);
		}
		CustomClaimData data = dataCache.get(claim.getID(), playerID);
		data.claimLocation = location;
		return data.modified = true;
	}

	/**
	 * Sets the Name {@link String} of the specified {@link Claim} for the specified
	 * {@link Player} {@link UUID}. The claim cannot be null. The playerID and name
	 * can be null.
	 * 
	 * @param playerID The UUID of the player that this data belongs to.
	 * @param claim    The Claim that is data belongs to. Cannot be NULL.
	 * @param name     The Name to change this data's name to.
	 * @return False if the claim is null. Otherwise True because the claim name was
	 *         updated successfully.
	 */
	public static boolean setName(UUID playerID, @Nonnull Claim claim, String name) {
		if (claim == null) {
			return false;
		}
		if (!dataCache.contains(claim.getID(), playerID)) {
			new CustomClaimData(playerID, claim);
		}
		CustomClaimData data = dataCache.get(claim.getID(), playerID);
		if (name == null) {
			data.customName = null;
		} else
			data.customName = name.trim().replace(" ", "_");
		return data.modified = true;
	}

	private static Location getCentreOfClaim(Claim claim) {
		if (claim.parent != null) {
			claim = claim.parent;
		}

		Location greater = claim.getGreaterBoundaryCorner(), lesser = claim.getLesserBoundaryCorner();
		int x1 = greater.getBlockX(), x2 = lesser.getBlockX();
		int z1 = greater.getBlockZ(), z2 = lesser.getBlockZ();
		int x = (x1 - x2) / 2 + x2, z = (z1 - z2) / 2 + z2, y = lesser.getWorld().getHighestBlockYAt(x, z);
		return new Location(lesser.getWorld(), x + 0.5, y, z + 0.5);
	}

	public static void loadAll() {
		for (File file : dataBase.listFiles()) {
			Integer claimID = Ints.tryParse(file.getName());
			if (!file.isDirectory() || claimID == null)
				continue;
			for (File inner : file.listFiles()) {
				String name = inner.getName();
				if (!inner.isFile() || !name.endsWith(".json"))
					continue;
				try {
					BufferedReader in = new BufferedReader(new FileReader(inner));
					CustomClaimData data = ATMain.getGson().fromJson(in, new TypeToken<CustomClaimData>() {
					}.getType());
					data.insert();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void insert() {
		if (datastore.getClaim(claimID) == null || claimLocation == null && customName == null) {
			File file = new File(dataBase + File.separator + claimID);
			if (file.exists()) {
				try {
					FileUtils.deleteDirectory(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return;
		}
		dataCache.put(claimID, dataOwner, this);
	}

	public boolean hasName() {
		return customName != null;
	}

	public long getClaimID() {
		return claimID;
	}

	public Location getClaimLocation() {
		if (claimLocation != null) {
			return claimLocation;
		}
		return getCentreOfClaim(datastore.getClaim(claimID));
	}

	public String getName() {
		return customName;
	}
}