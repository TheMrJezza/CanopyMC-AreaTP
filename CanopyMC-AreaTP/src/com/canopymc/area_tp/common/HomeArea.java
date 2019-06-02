package com.canopymc.area_tp.common;

import org.bukkit.Location;

public class HomeArea {
	private String name;
	private Location location;
	
	public HomeArea(String name, Location loc) {
		setName(name);
		setLocation(loc);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLocation(Location loc) {
		location = loc;
	}
	
	public Location getLocation() {
		return location;
	}
}