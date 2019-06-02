package com.canopymc.area_tp.common;

import java.util.HashMap;
import java.util.UUID;

public class NamedData {
	private static HashMap<UUID, NamedData> namedData = new HashMap<>();
	
	private UUID owner;
	private HashMap<Long, String> claims = new HashMap<>();
	
	private NamedData(UUID playerID) {
		owner = playerID;
	}
	
	private String getCustomClaimID(long claimID) {
		return claims.get(claimID);
	}
	
	public static NamedData getNamedData(UUID playerID) {
		createNamedData(playerID);
		return namedData.get(playerID);
	}
	
	public static boolean createNamedData(UUID playerID) {
		if (namedData.containsKey(playerID)) return false;
		namedData.put(playerID, new NamedData(playerID));
		return true;
	}
}
