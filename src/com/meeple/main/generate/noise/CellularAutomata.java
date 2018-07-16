package com.meeple.main.generate.noise;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import com.meeple.Treetops;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.VPoint2D;

public class CellularAutomata {

	/**
	 * Map values
	 * 0 is edge
	 * 0 - RENAMEME is water
	 * RENAMEME + is ground
	 * 
	 * */
	protected int seed = new Random().nextInt(99999);
	protected int width = 100;
	protected int height = 100;
	protected Random random;
	protected int smoothingLevel = 5;
	protected int margin = 5;
	protected float RENAMEME = 0.5f;
	Map<VPoint2D, Float> map;
	protected ArrayList<VPoint2D> openTileList = new ArrayList<VPoint2D>();

	public CellularAutomata(int width, int height, Random random) {
		map = new HashMap<VPoint2D, Float>();
		this.width = width;
		this.height = height;
		this.random = random;
		// createMap();
	}

	public CellularAutomata(int width, int height, Random random, int margin) {
		this(width, height, random);
		this.margin = margin;
	}

	public CellularAutomata createMap() {
		long start = System.currentTimeMillis();
		Treetops.println("Starting Cellular Automata");
		ValueRange xLowerMargin = ValueRange.of(0, margin);
		ValueRange xUpperMargin = ValueRange.of(width - margin, width);

		ValueRange yLowerMargin = ValueRange.of(0, margin);
		ValueRange yUpperMargin = ValueRange.of(height - margin, height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				//sets a border around 
				//if border set false, otherwise make random

				boolean test = (xLowerMargin.isValidValue(x) || xUpperMargin.isValidValue(x) || yUpperMargin.isValidValue(y) || yLowerMargin.isValidValue(y));
				map.put(new VPoint2D(x, y), test ? 0 : (MathHelper.getRandomFloatBetween(random, 0, 1)));
			}
		}
		Treetops.println("Ended pre-gen: " + new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds. ");
		Treetops.println("Starting smoothing: " + new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds. ");
		//smoothingLevel = 3;
		smoothMap();
		Treetops.println("Ended Smoothing: " + new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds. ");
		//		createOpenList();
		//		Treetops.println("Generating open list: " + new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds. ");
		return this;

	}

	private void smoothMap() {
		for (int smooth = 0; smooth < smoothingLevel; smooth++) {
			for (int x = 1; x < width - 1; x++) {
				for (int y = 1; y < height - 1; y++) {
					int neighbourWallTiles = getSurroundingWallCount(x, y);
					float temp = map.get(new VPoint2D(x, y));
					if (neighbourWallTiles > 4)
						map.put(new VPoint2D(x, y), temp - MathHelper.getRandomFloatBetween(random, 0, RENAMEME));
					else if (neighbourWallTiles < 4)
						map.put(new VPoint2D(x, y), temp + MathHelper.getRandomFloatBetween(random, 0, RENAMEME));
				}
			}
		}
	}

	/*
		public void createOpenList() {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					try {
						if (map.get(new VPoint2D(x, y)).doubleValue() > RENAMEME) {
							openTileList.add(new VPoint2D(x, y));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}*/

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean inMap(VPoint2D p) {
		return inMap(p.x, p.y);
	}

	public boolean inMap(double x, double y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	ArrayList<ArrayList<VPoint2D>> getRegions() {
		ArrayList<ArrayList<VPoint2D>> regions = new ArrayList<ArrayList<VPoint2D>>();
		boolean[][] lookedAtTile = new boolean[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (!(lookedAtTile[x][y] == true) && (map.get(new VPoint2D(x, y)).doubleValue() > RENAMEME)) {
					ArrayList<VPoint2D> newReg = getRegion(x, y);
					regions.add(newReg);
					for (VPoint2D p : newReg) {
						lookedAtTile[(int) p.x][(int) p.y] = true;
					}
				}
			}

		}

		return regions;
	}

	public ArrayList<VPoint2D> getRegion(VPoint2D p) {
		return getRegion((int) p.x, (int) p.y);
	}

	public ArrayList<VPoint2D> getRegion(int startX, int startY) {
		ArrayList<VPoint2D> region = new ArrayList<VPoint2D>();
		// ArrayList<VPoint2D> allOpen = new ArrayList<VPoint2D>(openTileList);
		boolean[][] lookedAtTile = new boolean[width][height];
		ArrayList<VPoint2D> toCheckList = new ArrayList<VPoint2D>();
		toCheckList.add(new VPoint2D(startX, startY));
		region.add(new VPoint2D(startX, startY));
		lookedAtTile[startX][startY] = true;
		while (!toCheckList.isEmpty()) {
			VPoint2D currCheck = toCheckList.get(0);
			toCheckList.remove(currCheck);
			for (int x1 = (int) currCheck.x - 1; x1 <= currCheck.x + 1; x1++) {
				for (int y1 = (int) currCheck.y - 1; y1 <= currCheck.y + 1; y1++) {
					if (inMap(x1, y1) && (x1 == currCheck.x || y1 == currCheck.y)) {
						if (map.get(new VPoint2D(x1, y1)).doubleValue() > RENAMEME) {

							if (!(lookedAtTile[x1][y1] == true)) {
								lookedAtTile[x1][y1] = true;
								toCheckList.add(new VPoint2D(x1, y1));
								region.add(new VPoint2D(x1, y1));
							}
						}
					}
				}
			}
		}
		region.sort(new Comparator<VPoint2D>() {

			@Override
			public int compare(VPoint2D a, VPoint2D b) {
				if (a.x < b.x) {
					return -1;
				} else if (a.x > b.x) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return region;

	}

	public int getSurroundingWallCount(int gridX, int gridY) {
		int wallCount = 0;
		for (int neighbourX = gridX - 1; neighbourX <= gridX + 1; neighbourX++) {
			for (int neighbourY = gridY - 1; neighbourY <= gridY + 1; neighbourY++) {
				if (inMap(neighbourX, neighbourY)) {
					if (neighbourX != gridX || neighbourY != gridY) {
						wallCount += (map.get(new VPoint2D(neighbourX, neighbourY)).doubleValue() < RENAMEME) ? 1 : 0;
					}
				} else {
					wallCount++;
				}
			}
		}
		return wallCount;
	}

	public int getExtendedSurroundingWallCount(int gridX, int gridY) {
		int wallCount = 0;
		for (int neighbourX = gridX - 2; neighbourX <= gridX + 2; neighbourX++) {
			for (int neighbourY = gridY - 2; neighbourY <= gridY + 2; neighbourY++) {
				if (inMap(neighbourX, neighbourY)) {
					if (neighbourX != gridX || neighbourY != gridY) {
						// wallCount += map[neighbourX][neighbourY];
						wallCount += (map.get(new VPoint2D(neighbourX, neighbourY)).doubleValue() < RENAMEME) ? 1 : 0;
					}
				} else {
					wallCount++;
				}
			}
		}
		return wallCount;
	}

	public void print(double scale, String filename) {
		BufferedImage image = new BufferedImage((int) (width * scale), (int) (height * scale), BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		g.setColor(Color.red);
		for (Map.Entry<VPoint2D, Float> entry : map.entrySet()) {
			VPoint2D key = entry.getKey();
			Float value = entry.getValue();
			if (value > 1)
				value = 1f;
			if (value < 0)
				value = 0f;
			g.setColor(value > RENAMEME ? new Color(value, 0, 0) : new Color(0, 0, value));
			g.fillRect((int) (key.x * scale), (int) (key.y * scale), (int) (1 * scale), (int) (1 * scale));
		}
		try {
			File outputfile = new File(filename);
			if (!outputfile.exists()) {
				outputfile.createNewFile();
			}
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<VPoint2D> getOpenTiles() {
		return openTileList;
	}

	public Map<VPoint2D, Float> getMap() {
		return map;
	}

	/**
	 * @return the margin
	 */
	public int getMargin() {
		return margin;
	}

	/**
	 * @param margin the margin to set
	 */
	public void setMargin(int margin) {
		this.margin = margin;
	}

	@Override
	public String toString() {
		return "Map Size: [" + width + "," + height + "]. Seed: " + seed + ". " + "Regions: " + getRegions().size() + "\n ";
	}

}
