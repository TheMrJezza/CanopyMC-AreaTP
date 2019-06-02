package com.canopymc.area_tp.common;

import java.util.HashMap;
import java.util.UUID;

public class NamedData {
	private static HashMap<UUID, NamedData> namedData = new HashMap<>();
	
	private UUID owner;
	private HashMap<Long, HomeArea> claims = new HashMap<>();
	
	private NamedData(UUID playerID) {
		owner = playerID;
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
	
	public String getCustomClaimID(long claimID) {
		if (claims.containsKey(claimID)) {
			return claims.get(claimID).getName();
		}
		return null;
	}
	
	public void setCustomClaimID(long claimID, String name) {
		if (claims.containsKey(claimID)) {
			claims.get(claimID).setName(name);
			return;
		}
		// Fix This
	}
	
	public UUID getOwnerID() {
		return owner;
	}
}
