package com.meeple;

import java.text.DecimalFormat;

import com.meeple.lib.math.VPoint2D;
import com.meeple.main.generate.WorldGenerator;

public class Treetops {
	//Main classes
	static long start = System.currentTimeMillis();

	public static void println(Object s) {
		System.out.println(s + " (" + new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds from start)");
	}

	public Treetops() {
		String seed = "wabajack";
		//TileBasedRoom a = new TileBasedRoom(random);
		//a.generate();
		//a.printImage("TBR");

		//CircleIslandGenerator cig = new CircleIslandGenerator(random);
		//cig.printImage("cig");

		WorldGenerator wg = new WorldGenerator(seed, 10);
		for (int x = (int) -wg.scale; x < 10 * wg.scale; x += wg.scale) {
			for (int y = (int) -wg.scale; y < 10 * wg.scale; y += wg.scale) {
				wg.getChunkAt(new VPoint2D(x, y));
			}
		}
		wg.getChunkAt(new VPoint2D(), 6);
		wg.print();
	}

	public static void main(String[] args) {
		new Treetops();
	}
}
