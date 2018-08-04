package com.meeple.main.generate.world;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.meeple.Treetops;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.Polygon3D;
import com.meeple.lib.math.SPoint;
import com.meeple.lib.math.VPoint3D;
import com.meeple.lib.math.MathHelper.RandomCollection;
import com.meeple.main.generate.world.Island.IslandSize;

public class SphereRNG {
	VPoint3D origin = new VPoint3D();
	Set<SPoint> points = new TreeSet<SPoint>(new Comparator<SPoint>() {

		@Override
		public int compare(SPoint o1, SPoint o2) {
			return o1.equals(o2) ? 0 : o1.hashCode() - o2.hashCode();
		}

	});
	//resolution
	final double radius;
	final double pointsPerRadius;
	final double pitchIncrement;
	Random random;
	final int seed;
	double maxHeightVariation = 10;
	double minHeightVariation = -10;

	//threads
	Thread generatorThread;

	public SphereRNG() {
		//points = new Polygon3D();
		this.radius = 10;
		this.seed = 1;
		this.random = new Random(seed);
		pointsPerRadius = 360 / getScale();
		pitchIncrement = 360 / getScale();
		generate();
	}

	public SphereRNG(double radius, Random random) {
		this.origin = new VPoint3D();
		this.radius = radius;
		this.seed = 1;
		this.random = random;
		pointsPerRadius = 360 / getScale();
		pitchIncrement = 360 / getScale();
		generate();
	}

	public SphereRNG(double radius) {
		//points = new Polygon3D();
		this.radius = radius;
		this.seed = 1;
		this.random = new Random(this.seed);
		pointsPerRadius = 360 / getScale();
		pitchIncrement = 360 / getScale();
		generate();
	}

	public SphereRNG(VPoint3D or, double radius, Random random) {
		this.origin = or;
		this.radius = radius;
		this.seed = 1;
		this.random = random;
		pointsPerRadius = 360 / getScale();
		pitchIncrement = 360 / getScale();
		generate();
	}

	public SphereRNG(int seed) {
		//points = new Polygon3D();
		this.radius = 10;
		this.seed = seed;
		this.random = new Random(seed);
		pointsPerRadius = 360 / getScale();
		pitchIncrement = 360 / getScale();
		generate();
	}

	public SphereRNG(double radius, Random random, int seed) {
		this.origin = new VPoint3D();
		this.radius = radius;
		this.seed = seed;
		this.random = random;
		pointsPerRadius = 360 / getScale();
		pitchIncrement = 360 / getScale();
		generate();
	}

	public SphereRNG(double radius, int seed) {
		//points = new Polygon3D();
		this.radius = radius;
		this.seed = seed;
		this.random = new Random(this.seed);
		pointsPerRadius = 360 / getScale();
		pitchIncrement = 360 / getScale();
		generate();
	}

	public SphereRNG(VPoint3D or, double radius, Random random, int seed) {
		this.origin = or;
		this.radius = radius;
		this.seed = seed;
		this.random = random;
		pointsPerRadius = 360 / getScale();
		pitchIncrement = 360 / getScale();
		generate();
	}

	public void generate() {
		Treetops.println("Starting generation with radius of " + radius);
		long start = System.currentTimeMillis();

		generatorThread = new Thread(() -> {
			points.clear();
			int count = 0;
			double third = 180 - ((+0.5) * pitchIncrement);
			double third2 = 0 + ((2 + 0.5) * pitchIncrement);

			//TODO sometimes skips rotation of 10 for some reason...??
				for (double pitch = 0; pitch < 180; pitch += pitchIncrement) {
					//if pitch is first, second, third, last, second to last or third to last iteration

					for (double rot = 0; rot <= 360; rot += (MathHelper.isBetween(third, pitch, 180) || (MathHelper.isBetween(0, pitch, third2))) ? pointsPerRadius * 2 : pointsPerRadius) {

						count += 1;
						SPoint a = new SPoint(rot, pitch, radius);
						points.add(a);
					}
					//Treetops.println("pitch: " + pitch + ", called rot: " + call);

				}
				Treetops.println("Ending generation, Created " + points.size() + " points and called " + count + " times. Took: "
						+ new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds. ");

			});
		generatorThread.start();
	}

	public SphereRNG generateIslands() {
		return generateIslands(2, 30);
	}

	/**
	 * privately generates a random set using a seed (made by combining rot,pitch and world seed) to garuntee its the same each time its called for same location
	 * uses a randomCollection to allow a wieghted variation in world leaning towards flat
	 * */
	public double getHeightAt(double rot, double pitch) {
		//get island at location and have the island generate the height

		Random rand = new Random(Long.parseLong(rot + "" + pitch + "" + seed));
		RandomCollection<double[]> rc = new RandomCollection<>(rand);
		rc.add(5, new double[] { minHeightVariation / 10, maxHeightVariation / 10 });
		rc.add(3, new double[] { minHeightVariation / 5, maxHeightVariation / 5 });
		rc.add(1, new double[] { minHeightVariation / 2, maxHeightVariation / 2 });
		rc.add(1, new double[] { minHeightVariation, maxHeightVariation });
		double[] temp = rc.next();
		return MathHelper.getRandomDoubleBetween(rand, temp[0], temp[1]);
	}

	public SphereRNG generateIslands(int min, int max) {
		Treetops.println("Starting island generation");
		int islandCount = MathHelper.getRandomIntBetween(random, min, max);
		ArrayList<Island> islands = new ArrayList<Island>();
		RandomCollection<IslandSize> rc = new RandomCollection<>(random);
		rc.add(2, IslandSize.TINY).add(4, IslandSize.SMALL).add(5, IslandSize.MEDIUM).add(3, IslandSize.BIG).add(1, IslandSize.HUGE);

		for (int i = 0; i < islandCount; i++) {
			islands.add(new Island(random, rc.next()));
			islands.get(islands.size() - 1).generate();
		}
		Treetops.println("Finished island generation");

		//TODO assign random place on the planet for island

		return this;
	}

	public SphereRNG randomiseDistances(double lowerLimit, double upperLimit) {
		for (SPoint point : points) {
			point.addDistance(point.getDistance() * MathHelper.getRandomDoubleBetween(random, lowerLimit, upperLimit));
		}
		return this;
	}

	public SphereRNG randomiseRotations(double lowerLimit, double upperLimit) {
		for (SPoint point : points) {
			point.addRotation(point.getRotation() * MathHelper.getRandomDoubleBetween(random, lowerLimit, upperLimit));
		}
		return this;
	}

	public SphereRNG randomisePitches(double lowerLimit, double upperLimit) {
		for (SPoint point : points) {
			point.addPitch(point.getPitch() * MathHelper.getRandomDoubleBetween(random, lowerLimit, upperLimit));
		}
		return this;
	}

	//--
	//Getters and setters
	//--
	public VPoint3D getOrigin() {
		return origin;
	}

	public void setOrigin(VPoint3D origin) {
		this.origin = origin;
	}

	public double getRadius() {
		return radius;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	//--
	//export the points to a .obj file
	//--
	public void print(String filename) {
		print(filename, 1);
	}

	public void print(String filename, double scale) {
		new Thread(() -> {
			try {
				Treetops.println("Starting export to .obj file. ");
				if (generatorThread.isAlive()) {
					Treetops.println("Waiting for generator thread to finish. ");
				}
				generatorThread.join();
				Polygon3D p = new Polygon3D();
				for (SPoint point : points) {
					//Treetops.println(point + " " + point.convertToCaresian());
				p.addPoint(point.convertToCaresian());

			}
			try {
				p.writeToOBJFile(filename, scale);
			} catch (IOException e) {
				Treetops.println("cannot export");
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	).start();

	}

	double getScale() {
		return 36;
	}
}
