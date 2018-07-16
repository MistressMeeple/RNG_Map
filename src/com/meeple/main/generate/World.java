package com.meeple.main.generate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.imageio.ImageIO;

import com.meeple.Treetops;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.Polygon3D;
import com.meeple.lib.math.SPoint;
import com.meeple.lib.math.VPoint2D;
import com.meeple.lib.math.VPoint3D;
import com.meeple.main.generate.Island.IslandSize;

public class World {

	Random random;
	double radius;
	//"Island" and the top and bottom for the poles
	Island topPole;
	Island bottomPole;

	ArrayList<Island> islandList;

	ArrayList<SPoint> map;

	public World(Random random, double radius) {
		this.random = random;
		this.radius = radius;
		map = new ArrayList<SPoint>();
		topPole = new Island(random, IslandSize.HUGE);
		bottomPole = new Island(random, IslandSize.HUGE);

	}

	public void addIsland(Island add) {
		//ignoring the poles
		int mapWidth, mapHeight;
		mapHeight = (int) (radius * 1.8);
		mapWidth = (int) (2 * Math.PI * radius);

		for (Entry<VPoint2D, Double> entry : add.map.entrySet()) {
			VPoint2D point = entry.getKey();
			double height = entry.getValue();
			System.out.println(point + " " + height);
			SPoint sp = map2DToSphere(point.x, point.y);
			sp.addDistance(height);
			map.add(sp);
		}
	}

	SPoint map2DToSphere(double x, double y) {
		double longitude = x / radius;
		double latitude = 2 * Math.atan(Math.exp(y / radius)) - Math.PI / 2;

		VPoint3D P = new VPoint3D();
		P.x = radius * Math.cos(latitude) * Math.cos(longitude);
		P.y = radius * Math.cos(latitude) * Math.sin(longitude);
		P.z = radius * Math.sin(latitude);

		return P.convertToSpherical();
	}

	void createMap() {

	}
/*
	void run() {

		Map<Island, VPoint2D> hugeIslands = new HashMap<Island, VPoint2D>();
		Map<Island, VPoint2D> bigIslands = new HashMap<Island, VPoint2D>();
		Map<Island, VPoint2D> mediumIslands = new HashMap<Island, VPoint2D>();
		Map<Island, VPoint2D> smallIslands = new HashMap<Island, VPoint2D>();
		Map<Island, VPoint2D> tinyIslands = new HashMap<Island, VPoint2D>();

		for (Island i : islandList) {
			switch (i.getSize()) {
			case TINY:
				tinyIslands.put(i, new VPoint2D());
				break;
			case SMALL:
				smallIslands.put(i, new VPoint2D());
				break;
			case MEDIUM:
				mediumIslands.put(i, new VPoint2D());
				break;
			case BIG:
				bigIslands.put(i, new VPoint2D());
				break;
			case HUGE:
				hugeIslands.put(i, new VPoint2D());
				break;

			}
		}
		//HUGE islands. generate a random point that isnt already occupied
		for (Entry<Island, VPoint2D> entry : hugeIslands.entrySet()) {

			Island i = entry.getKey();
			VPoint2D point = new VPoint2D();
			boolean foundPoint = false;
			long start = System.currentTimeMillis();
			long maxTimeSpent = 10 * 1000;//10 seconds
			while (!foundPoint) {
				point = new VPoint2D(MathHelper.getRandomDoubleBetween(random, 0 + i.getSize().getDiameter(), worldW - (i.getSize().getDiameter() * 2)), MathHelper.getRandomDoubleBetween(random, 0 + i
						.getSize().getDiameter(), worldH - (i.getSize().getDiameter() * 2)));
				for (Entry<Island, VPoint2D> temp : hugeIslands.entrySet()) {
					if (temp.getKey().getName() != i.getName()) {
						foundPoint = (MathHelper.inRange(point.x, temp.getValue().x - (i.getSize().getDiameter() * 1.5), temp.getValue().x + (i.getSize().getDiameter() * 1.5)) && MathHelper.inRange(
								point.y, temp.getValue().y - (i.getSize().getDiameter() * 1.5), temp.getValue().y + (i.getSize().getDiameter() * 1.5)));
						Treetops.println("Island: " + temp + " X1Y1: " + temp.getValue() + " X2Y2: " + point);
					}
					if (foundPoint) {
						break;
					}
					if (System.currentTimeMillis() - start < maxTimeSpent) {
						break;
					}
				}
				if (System.currentTimeMillis() - start < maxTimeSpent) {
					break;
				}
			}
			if (foundPoint) {
				hugeIslands.put(i, point);
				BufferedImage image = i.print();
				g2.drawImage(image, (int) point.x, (int) point.y, (int) (i.getSize().getDiameter() * 2), (int) (i.getSize().getDiameter() * 2), null);
			}
		}
		//BIG islands
		for (Entry<Island, VPoint2D> entry : bigIslands.entrySet()) {
			Island i = entry.getKey();
			VPoint2D point;

			point = new VPoint2D(MathHelper.getRandomDoubleBetween(random, 0 + i.getSize().getDiameter(), worldW - (i.getSize().getDiameter() * 2)), MathHelper.getRandomDoubleBetween(random, 0 + i
					.getSize().getDiameter(), worldH - (i.getSize().getDiameter() * 2)));

			BufferedImage image = i.print();
			g2.drawImage(image, (int) point.x, (int) point.y, (int) (i.getSize().getDiameter() * 2), (int) (i.getSize().getDiameter() * 2), null);
		}

		try {
			File outputfile = new File("world.png");
			if (!outputfile.exists()) {
				outputfile.createNewFile();
			}
			ImageIO.write(world, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//*/
	public void print(String filename, double scale) {
		Treetops.println("Starting export to .obj file. ");
		Polygon3D p = new Polygon3D();
		for (SPoint point : map) {
			p.addPoint(point.convertToCaresian());
		}
		try {
			p.writeToOBJFile(filename, scale);
		} catch (IOException e) {
			Treetops.println("cannot export");
			e.printStackTrace();
		}
	}

}
