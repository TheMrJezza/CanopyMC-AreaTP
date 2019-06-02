package com.canopymc.area_tp.common;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class LocationAdapter extends TypeAdapter<Location> {
	@Override
	public Location read(JsonReader reader) throws IOException {
		World world = null;
		Double x = null, y = null, z = null;
		Float yaw = null, pitch = null;
		String fieldname = null;
		reader.beginObject();
		while (reader.hasNext()) {
			JsonToken token = reader.peek();

			if (token.equals(JsonToken.NAME)) {
				fieldname = reader.nextName();
			}

			if ("world".equalsIgnoreCase(fieldname)) {
				token = reader.peek();
				world = Bukkit.getWorld(reader.nextString());
			}

			if ("x".equalsIgnoreCase(fieldname)) {
				token = reader.peek();
				x = reader.nextDouble();
				continue;
			}

			if ("y".equalsIgnoreCase(fieldname)) {
				token = reader.peek();
				y = reader.nextDouble();
				continue;
			}

			if ("z".equalsIgnoreCase(fieldname)) {
				token = reader.peek();
				z = reader.nextDouble();
				continue;
			}

			if ("yaw".equalsIgnoreCase(fieldname)) {
				token = reader.peek();
				yaw = (float) reader.nextDouble();
				continue;
			}

			if ("pitch".equalsIgnoreCase(fieldname)) {
				token = reader.peek();
				pitch = (float) reader.nextDouble();
				continue;
			}
			reader.peek();
		}
		reader.endObject();
		if (world == null || x == null || y == null || z == null || yaw == null || pitch == null) {
			return null;
		}
		return new Location(world, x, y, z, yaw, pitch);
	}

	@Override
	public void write(JsonWriter writer, Location location) throws IOException {
		writer.beginObject();
		writer.name("world");
		writer.value(location.getWorld().getName());
		writer.name("x");
		writer.value(location.getX());
		writer.name("y");
		writer.value(location.getY());
		writer.name("z");
		writer.value(location.getZ());
		writer.name("yaw");
		writer.value(location.getYaw());
		writer.name("pitch");
		writer.value(location.getPitch());
		writer.endObject();
	}
}