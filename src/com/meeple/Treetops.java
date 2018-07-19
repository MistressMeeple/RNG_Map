package com.meeple;

import java.text.DecimalFormat;
import java.util.Random;

import com.meeple.main.generate.FractalLandscape;
import com.meeple.main.generate.Landmass;

public class Treetops {
	//Main classes
	static long start = System.currentTimeMillis();

	public static void println(Object s) {
		System.out.println(s + " (" + new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds from start)");
	}

	public Treetops() {
		Random random = new Random(0);

		println("Starting fractal");
		Landmass landmass = new Landmass(100, 100, random);
		println("Fin");

		//		Viewer3D v3d = new Viewer3D(fl);
	}

	long test(int w, int h) {
		long startA = System.currentTimeMillis();
		double lWidth = 100, lHeight = 100;
		FractalLandscape[][] landmass = new FractalLandscape[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				landmass[x][y] = new FractalLandscape(lWidth * x, lHeight * y, lWidth, lHeight, new Random(), 4);
			}
		}
		return (System.currentTimeMillis() - startA);
	}

	public static void main(String[] args) {
		new Treetops();
	}
}
