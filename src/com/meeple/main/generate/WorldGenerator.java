package com.meeple.main.generate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import com.meeple.Treetops;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.VPoint2D;
import com.meeple.lib.math.VPoint3D;
import com.meeple.main.generate.world.FractalLandscape;
import com.meeple.main.generate.world.WorldHeights;

public class WorldGenerator {
	//Map to store the corners of chunks
	HashMap<VPoint2D, Double> chunkCornerHeightMap = new HashMap<VPoint2D, Double>();
	HashMap<VPoint2D, FractalLandscape> chunkMap = new HashMap<VPoint2D, FractalLandscape>();
	public final double scale;
	String seed;
	int itts = 4;

	public WorldGenerator(String seed, double scale) {

		this.scale = scale;
		this.seed = seed;
		Random random = new Random(seed.hashCode());
		WorldHeights.create(scale);

		//seed the inital "chunk"
		chunkCornerHeightMap.put(new VPoint2D(), WorldHeights.getRandomWeightedTowards(WorldHeights.grassLevel, random));
		getChunkAt(new VPoint2D(), itts);

	}

	public VPoint2D clamp(VPoint2D input) {
		return new VPoint2D((int) (Math.round(input.x / scale) * scale), (int) (Math.round(input.y / scale) * scale));
	}

	private String seedFromLocation(VPoint2D a) {
		return (seed + (a.x + "" + a.y));
	}

	public FractalLandscape getChunkAt(VPoint2D point) {
		return getChunkAt(point, itts);
	}

	public FractalLandscape getChunkAt(VPoint2D point, int subDivides) {
		point = clamp(point);
		if (chunkMap.containsKey(point)) {
			if (chunkMap.get(point).getSubDivisions() == subDivides) {
				return chunkMap.get(point);
			}
		}

		VPoint3D p1 = new VPoint3D(point.x, point.y, getChunkHeightAt(point));
		VPoint3D p2 = new VPoint3D(point.north(scale).x, point.north(scale).y, getChunkHeightAt(point.north(scale)));
		VPoint3D p3 = new VPoint3D(point.north(scale).east(scale).x, point.north(scale).east(scale).y, getChunkHeightAt(point.north(scale).east(scale)));
		VPoint3D p4 = new VPoint3D(point.east(scale).x, point.east(scale).y, getChunkHeightAt(point.east(scale)));

		FractalLandscape fl = new FractalLandscape(p1, p2, p3, p4, seed, subDivides, 2.5 * scale, 2);
		fl.smooth(scale);
		chunkMap.put(point, fl);

		return chunkMap.get(point);
	}

	double getChunkHeightAt(VPoint2D point) {
		if (!chunkCornerHeightMap.containsKey(point)) {
			double height = WorldHeights.getRandomWeightedTowards(WorldHeights.snowCapLevel, new Random(seedFromLocation(point).hashCode()));
			height = new Random(seedFromLocation(point).hashCode()).nextDouble() * WorldHeights.getEntireWorldHeight().getMaximum();
			int rad = 1;
			int size = ((rad * rad) + 1) * ((rad * rad) + 1);
			double[] avg = new double[size - 1];
			int index = 0;

			for (int x = -rad; x < rad; x++) {
				for (int y = -rad; y < rad; y++) {
					if (x != 0 && y != 0) {
						avg[index] = new Random(seedFromLocation(point.add(x * scale, y * scale)).hashCode()).nextDouble() * WorldHeights.getEntireWorldHeight().getMaximum();
						index += 1;
					}
				}
			}
			height = height - MathHelper.average(avg);
			chunkCornerHeightMap.put(point, height);
		}
		return chunkCornerHeightMap.get(point);
	}

	public void print() {
		print("out/wgPoints.obj");
	}
	public void print(String fname) {
		Treetops.println("Starting print for Fractal Landscape");
		try {

			File f = new File(fname);
			if (!f.exists()) {
				f.createNewFile();
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			int verticesWritten = 0;
			//			bw.write("o all");
			for (Entry<VPoint2D, FractalLandscape> entry : chunkMap.entrySet()) {
				bw.write("o chunk_" + entry.getKey().x + "_" + entry.getKey().y + System.lineSeparator());
				bw.write(entry.getValue().writeVertices());

				//				bw.write("s 1");

				bw.write(entry.getValue().writeFaces(verticesWritten));
				verticesWritten += entry.getValue().getSquareList().pointList.size();
			}

			bw.close();
			Treetops.println("finished printing");
		} catch (IOException e) {

		}

	}

	public double getScale() {
		return scale;
	}
}
