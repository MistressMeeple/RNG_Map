package com.meeple.main.generate.dugeon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.meeple.lib.math.Directional;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.VPoint2D;
import com.meeple.main.generate.Printable;

public class TileBasedRoom extends Printable {



	static class RoomConnection {
		//flag system for NESW
		static final int NORTH = 1;
		static final int EAST = 2;
		static final int SOUTH = 4;
		static final int WEST = 8;
		int flags = 0;
		//singlular roomconnections
		static RoomConnection OnlyNorth = new RoomConnection(true, false, false, false);
		static RoomConnection OnlyEast = new RoomConnection(false, true, false, false);
		static RoomConnection OnlySouth = new RoomConnection(false, false, true, false);
		static RoomConnection OnlyWest = new RoomConnection(false, false, false, true);

		public RoomConnection() {
		}

		public RoomConnection(boolean n, boolean e, boolean s, boolean w) {
			if (n) {
				flags += NORTH;
			}
			if (e) {
				flags += EAST;
			}
			if (s) {
				flags += SOUTH;
			}
			if (w) {
				flags += WEST;
			}
		}

		public Directional[] getDirectional() {
			ArrayList<Directional> ret = new ArrayList<Directional>(4);
			if ((flags & NORTH) == NORTH) {
				ret.add(Directional.NORTH);
			}

			if ((flags & EAST) == EAST) {
				ret.add(Directional.EAST);
			}

			if ((flags & SOUTH) == SOUTH) {
				ret.add(Directional.SOUTH);
			}

			if ((flags & WEST) == WEST) {
				ret.add(Directional.WEST);
			}
			return (Directional[]) ret.toArray();
		}

		public void addDirectional(Directional d) {
			if ((flags & d.getFlag()) != d.getFlag()) {
				flags += d.getFlag();
			}

		}
		public static ArrayList<Directional> AllDirectionals(){
			ArrayList<Directional> ret = new ArrayList<Directional>(4);
			ret.add(Directional.NORTH);
			ret.add(Directional.EAST);
			ret.add(Directional.SOUTH);
			ret.add(Directional.WEST);
			return ret;
		}
	}

	Random random;
	Map<VPoint2D, RoomConnection> tiles;

	public TileBasedRoom(Random random) {
		tiles = new HashMap<VPoint2D, RoomConnection>();
		this.random = random;
	}

	public void generate() {
		//create an inital room with a number of doors
		//while there are open doors, create rooms

		//this is the list to store the "to generate" rooms
		//holds the new room location and where it was entered from
		Map<VPoint2D, Directional> pointsToGenerate = new HashMap<VPoint2D, Directional>();
		if (tiles.size() <= 0) {

			VPoint2D start = new VPoint2D();
			RoomConnection roomStart = new RoomConnection();
			int doorCount = MathHelper.getRandomIntBetween(random, 1, 4);
			ArrayList<Directional> dirClone = new ArrayList<Directional>(Directional.AllDirectionals());
			for (int i = 0; i < doorCount; i++) {
				Directional a = MathHelper.getRandomElementFromList(random, dirClone);
				dirClone.remove(a);
				//room.addDirectional(a);
				//pointsToGenerate.put(point.add(a.dir), a);
			}

		}
		while (pointsToGenerate.size() != 0) {
			VPoint2D point = pointsToGenerate.keySet().iterator().next();
			pointsToGenerate.remove(point);
			RoomConnection room = new RoomConnection();
			//door count = max or 4 connectors, with 1 already existing because of entry
			int doorcount = MathHelper.getRandomIntBetween(random, 0, 3);
			ArrayList<Directional> dirClone = new ArrayList<Directional>(Directional.AllDirectionals());
			for (int i = 0; i < doorcount; i++) {
				Directional a = MathHelper.getRandomElementFromList(random, dirClone);
				dirClone.remove(a);
				room.addDirectional(a);
				pointsToGenerate.put(point.add(a.getDirection()), a);
			}

		}
	}

	@Override
	public BufferedImage printImage(String filename) {
		BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		g.setColor(Color.black);
		VPoint2D offset = new VPoint2D(image.getWidth() / 2, image.getHeight() / 2);
		for (Entry<VPoint2D, RoomConnection> tile : tiles.entrySet()) {
			VPoint2D o = tile.getKey().add(offset);
			System.out.println(o);
			g.drawRect((int) o.x - 5, (int) o.y - 5, 10, 10);
		}
		tryWriteImage(image, filename);
		return image;
	}
}
