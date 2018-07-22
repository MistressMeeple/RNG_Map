package com.meeple.main.generate.dugeon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.meeple.Treetops;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.SPoint;
import com.meeple.lib.math.VPoint2D;
import com.meeple.main.generate.Printable;

public class Room extends Printable {

	Map<VPoint2D, Boolean> sideList;
	int roomSize = 100;

	Random random;

	public Room(Random random) {
		sideList = new HashMap<VPoint2D, Boolean>();
		this.random = random;
		generate();
	}

	void generate() {
		int walls = MathHelper.getRandomIntBetween(random, 3, 12);
		ArrayList<VPoint2D> list = new ArrayList<VPoint2D>(walls);
		Treetops.println(walls + " " + (360 / walls));
		for (int i = 0; i < walls; i++) {
			list.add(new VPoint2D(roomSize / 2, 0).rotate((360 / walls * i), new VPoint2D()));
		}
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
		for (VPoint2D point : list) {
			int x = (int) point.x;
			int y = (int) point.y;
			if (x < minX) {
				minX = x;
			}
			if (y < minY) {
				minY = y;
			}
		}
		list = VPoint2D.rotationSort(list, new VPoint2D());
		for (VPoint2D point : list) {
			sideList.put(point.add(-minX, -minY), random.nextBoolean());
		}

	}

	@Override
	public BufferedImage printImage(String filename) {
		Polygon p = new Polygon();
		int maxX = 0, maxY = 0;
		for (Entry<VPoint2D, Boolean> entry : sideList.entrySet()) {
			int x = (int) entry.getKey().x;
			int y = (int) entry.getKey().y;
			p.addPoint(x, y);
			if (x > maxX) {
				maxX = x;
			}
			if (y > maxY) {
				maxY = y;
			}

		}
		BufferedImage image = new BufferedImage(roomSize, roomSize, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.drawPolygon(p);
		tryWriteImage(image, filename);
		return image;
	}

}
