package com.meeple.main.generate.world;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;

import javax.imageio.ImageIO;

import com.meeple.Treetops;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.MathHelper.RandomCollection;
import com.meeple.lib.math.SPoint;
import com.meeple.lib.math.VPoint2D;
import com.meeple.main.generate.noise.Noise;

public class Island {

	public enum IslandSize {
		TINY, SMALL, MEDIUM, BIG, HUGE;
		double maxRadius;

		private IslandSize() {
			maxRadius = ((this.ordinal() + 1) * 10);
		}

		public double getMaxRadius() {
			return maxRadius;
		}

		public double getDiameter() {
			return maxRadius * 2;
		}

		@Override
		public String toString() {
			String n = this.name().toLowerCase();
			n = n.substring(0, 1).toUpperCase() + n.substring(1);
			return n + ": " + getMaxRadius();
		}
	}

	SPoint location;//we only are interested in rot and pitch, not distance. hopefully the world has a uniform radius
	final Random random;
	final IslandSize size;
	final String name;
	int margin = 10;
	Map<VPoint2D, Double> map = new HashMap<VPoint2D, Double>();
	//-
	//things that are only needed in this class
	//-
	static Queue<String> islandNames;//List of names that islands can be

	static RandomCollection<IslandSize> rc;

	static {
		rc = new RandomCollection<>();
		rc.add(1, IslandSize.TINY).add(3, IslandSize.SMALL).add(5, IslandSize.MEDIUM).add(2, IslandSize.BIG).add(1, IslandSize.HUGE);
	}

	void setup() {
		//-
		//sets up the island names
		//loads from the file, shuffles the collection then puts it into the name list
		//-
		ArrayList<String> list = new ArrayList<String>();
		File f = new File("IslandNames.txt");
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(f);

			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			Treetops.println("Unable to open file '" + f + "'");
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			Treetops.println("Error reading file '" + f + "'");
		}

		islandNames = new ArrayDeque<String>();
		while (list.size() != 0) {
			int index = MathHelper.getRandomIntBetween(random, 0, list.size() - 1);
			String a = list.get(index);
			list.remove(index);
			islandNames.add(a);
		}
	}

	public Island(Random r) {
		this(r, rc.setRandom(r).next());
	}

	public Island(Random r, IslandSize size) {
		this.random = r;
		this.size = size;
		if (islandNames == null) {
			setup();
		}
		//sets the name to be a random 
		String s = "island " + MathHelper.getRandomDoubleBetween(r, 0, 100);
		try {
			s = islandNames.remove();
		} catch (NoSuchElementException e) {
		}
		this.name = s;
		this.location = new SPoint();
	}

	float[][] randomShapeHeightMap() {
		int s = (int) (size.maxRadius * 20);
		int points = MathHelper.getRandomIntBetween(random, (int) (size.getMaxRadius()), (int) (size.getMaxRadius() * 2));
		int offset = 10;
		float[][] ret = new float[s][s];
		ArrayList<VPoint2D> plist = new ArrayList<VPoint2D>();
		for (int i = 0; i < points; i++) {
			plist.add(new VPoint2D(MathHelper.getRandomIntBetween(random, offset, s - offset), MathHelper.getRandomIntBetween(random, offset, s - offset)));
		}
		VPoint2D.rotationSort(plist, new VPoint2D(s / 2, s / 2));

		Polygon poly = new Polygon();
		for (int i = 0; i < plist.size(); i++) {
			poly.addPoint((int) plist.get(i).x, (int) plist.get(i).y);
		}

		for (int x = 0; x < s; x++) {
			for (int y = 0; y < s; y++) {

				float dist = (float) (new VPoint2D(x, y).distanceTo(new VPoint2D(s / 2, s / 2)));
				//finds the distance between xy and mid. divides by max dist. mults by -1 then adds 1
				float mths = (float) ((((dist + (s / 50)) / (MathHelper.pythag(0, s / 2, 0, s / 2))) * -1) + 1);

				if (poly.contains(x, y)) {
					ret[x][y] = (float) (mths * 1.5);
				} else {
					ret[x][y] /= 2;
				}
			}
		}
		return ret;
	}

	float[][] circleHeightMap() {
		int s = (int) (size.maxRadius * 20);
		float[][] ret = new float[s][s];
		for (int x = 0; x < s; x++) {
			for (int y = 0; y < s; y++) {

				float dist = (float) (new VPoint2D(x, y).distanceTo(new VPoint2D(s / 2, s / 2)));
				//finds the distance between xy and mid. divides by max dist. mults by -1 then adds 1
				float mths = (float) ((((dist + (s / 50)) / (MathHelper.pythag(0, s / 2, 0, s / 2))) * -1) + 1);

				if (dist < s / 2) {
					ret[x][y] = (float) (mths * 1.5);
				} else {
					ret[x][y] /= 2;
				}
			}
		}
		return ret;
	}

	public Island generate() {
		double radius = size.maxRadius * 10;
		float[][] noise = Noise.generateSimplexNoise((float) (size.maxRadius / MathHelper.getRandomDoubleBetween(random, 7, 10)), (int) (radius * 2), (int) (radius * 2), (int) (random.nextInt(
				(int) radius) * radius), (int) (random.nextInt((int) radius) * radius));
		float[][] circle = circleHeightMap();
		Treetops.println("Starting generate");

		for (int x = 0; x < (radius * 2); x++) {
			for (int y = 0; y < (radius * 2); y++) {
				//float dist = (float) (new VPoint2D(x, y).distanceTo(new VPoint2D(radius, radius)));

				double value1 = noise[x][y] / 1.5;
				double value2 = noise[noise.length - x - 1][noise[0].length - y - 1] / 3;
				double value3 = noise[noise.length - y - 1][noise[0].length - x - 1] / 3;
				double value4 = noise[y][x] / 1.5;
				double tot = value1 + (value2 + value3 + value4) / 3;
				double mult = tot * circle[x][y] * WorldHeights.mountainLevel.getMaximum();
				map.put(new VPoint2D(x, y), mult);

			}
		}
		Treetops.println("Finish island gen");
		return this;
	}

	public IslandSize getSize() {
		return size;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the location
	 */
	public SPoint getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(SPoint location) {
		this.location = location;
	}

	/**
	 * @return the map
	 */
	public Map<VPoint2D, Double> getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(Map<VPoint2D, Double> map) {
		this.map = map;
	}

	public BufferedImage print() {
		Treetops.println("Island: Writing island " + toString() + " to file");
		double radius = size.maxRadius * 10;
		int offset = (int) size.maxRadius;
		BufferedImage image = new BufferedImage((int) (radius * 2) + (offset * 2), (int) (radius * 2) + (offset * 2), BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		g.setColor(WorldHeights.heightColor.get(WorldHeights.getRange(0)));
		//g.fillRect(0, 0, (int) (radius * 2) + (offset * 2), (int) (radius * 2) + (offset * 2));
		for (Entry<VPoint2D, Double> entry : map.entrySet()) {
			VPoint2D key = entry.getKey();
			double value = entry.getValue();
			g.setColor(WorldHeights.heightColor.get(WorldHeights.getRange(value)));
			if (g.getColor() != WorldHeights.heightColor.get(WorldHeights.getRange(0))) {
				g.fillRect((int) key.x + offset, (int) key.y + offset, 1, 1);
			}

		}
		try {
			File outputfile = new File("islands/" + this.name + " (" + this.size.name() + ").png");
			if (!outputfile.exists()) {
				outputfile.createNewFile();
			}
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	@Override
	public String toString() {
		String n = name.substring(0, 1).toUpperCase() + name.substring(1);
		return "Island: " + n + ", " + size;
	}

}
