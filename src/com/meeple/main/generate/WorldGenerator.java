package com.meeple.main.generate;

import java.util.HashMap;
import java.util.Random;

import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.VPoint2D;
import com.meeple.main.generate.world.FractalLandscape;
import com.meeple.main.generate.world.WorldHeights;

public class WorldGenerator {
	//Map to store the corners of chunks
	HashMap<VPoint2D, Double> chunkCornerHeightMap = new HashMap<VPoint2D, Double>();
	HashMap<VPoint2D, FractalLandscape> chunkMap = new HashMap<VPoint2D, FractalLandscape>();

	Random random;

	public WorldGenerator(Random random) {
		this.random = random;
		//seed the inital "chunk"
		chunkCornerHeightMap.put(new VPoint2D(), WorldHeights.getRandomWeightedTowards(WorldHeights.grassLevel, random));
		generateCorner(new VPoint2D().north(), WorldHeights.getRandomWeightedTowards(WorldHeights.grassLevel, random));
		generateCorner(new VPoint2D().east(), WorldHeights.getRandomWeightedTowards(WorldHeights.grassLevel, random));
		generateCorner(new VPoint2D().north().east(), WorldHeights.getRandomWeightedTowards(WorldHeights.grassLevel, random));

	}

	void generateCorner(VPoint2D generate, double maxHeightVariation) {
		chunkCornerHeightMap.put(generate, MathHelper.getRandomDoubleBetween(random, -maxHeightVariation, maxHeightVariation));
	}

	void generateChunkAt(VPoint2D point) {

		FractalLandscape fl = new FractalLandscape(0, 0, 1, 1, random, 5);
	}

	double getHeightAt(VPoint2D point) {
		if (!chunkCornerHeightMap.containsKey(point)) {
			chunkCornerHeightMap.put(point, WorldHeights.getRandomWeightedTowards(WorldHeights.grassLevel, random));
		}
		return chunkCornerHeightMap.get(point);
	}

}
