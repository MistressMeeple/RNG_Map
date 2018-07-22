package com.meeple.main.generate;

import java.util.Random;

import com.meeple.lib.math.VPoint2D;

public class Landmass {

	//default values
	int width = 10;
	int height = 10;
	double landscapeSize = 100;
	FractalLandscape[][] landmass;
	Random random;

	//TODO - change from storage in memory of ALL chunks to only within a set range
	//TODO - we can just generate the same fractal with the seed and location. 
	/**
	 * 
	 * 
	 * 
	 */

	public Landmass() {
		//default values: width = 10, height = 10;
		this(10, 10, new Random());
	}

	public Landmass(int width, int height, Random random) {
		this.width = width;
		this.height = height;
		landmass = new FractalLandscape[width][height];
		this.random = random;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				landmass[x][y] = new FractalLandscape(landscapeSize * x, landscapeSize * y, landscapeSize, landscapeSize, random, 4);
			}
		}
	}

	//range
	//central point
	//return "visible" fractalLandscapes
	/**
	 * 
	 * @param center central point to do checks from
	 * @param range radius from center of chunks to return
	 * @return FractalLandscape[][] which is all 
	 */
	public FractalLandscape[][] getChunksInRange(VPoint2D center, double range) {
		
		int chunkLoadRadius = (int) (range / landscapeSize);
		int startX = (int) center.x;
		int startY = (int) center.y;
		
		FractalLandscape[][] ret = new FractalLandscape[chunkLoadRadius*2][chunkLoadRadius*2];
		
		for (int x = 0 - chunkLoadRadius; x < startX + chunkLoadRadius; x++) {
			for (int y = 0 - chunkLoadRadius; y < startY + chunkLoadRadius; y++) {
				ret[x + chunkLoadRadius][y+ chunkLoadRadius] = new FractalLandscape(10,10,random);
			}
		}
		return ret;

	}

}
