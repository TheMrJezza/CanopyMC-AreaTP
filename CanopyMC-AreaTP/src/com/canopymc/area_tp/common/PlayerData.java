package com.canopymc.area_tp.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.UUID;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class PlayerData {
	private static HashMap<UUID, PlayerData> playerDataCache = new HashMap<>();
	private HashMap<Long, String> customNames = new HashMap<>();

	public static void remove(long claimID) {
		for (PlayerData data : playerDataCache.values()) {
			data.customNames.remove(claimID);
		}
	}

	public static void filter() {
		HashSet<Long> nullClaims = new HashSet<>();
		for (Entry<UUID, PlayerData> data : playerDataCache.entrySet()) {
			for (long id : data.getValue().customNames.keySet()) {
				if (nullClaims.contains(id)) {
					continue;
				}
				Claim claim = GriefPrevention.instance.dataStore.getClaim(id);
				if (claim == null) {
					nullClaims.add(id);
					continue;
				}
				if (!claim.ownerID.equals(data.getKey()))
					data.getValue().customNames.remove(id);
			}
		}
		for (long claimID : nullClaims) {
			remove(claimID);
		}
	}
}