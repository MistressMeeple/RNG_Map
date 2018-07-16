package com.meeple.main.generate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import javax.imageio.ImageIO;

import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.VPoint2D;

public class FractalLandscape2D {
	double width = 10;
	int iterations = 4;
	double heightReduction = 2;
	Random random;
	ArrayList<VPoint2D> pointList = new ArrayList<VPoint2D>();

	public FractalLandscape2D(double width, Random random) {
		this.width = width;
		this.random = random;
	}

	public FractalLandscape2D(double width, int iterations, Random random) {
		this.width = width;
		this.iterations = iterations;
		this.random = random;
	}

	public FractalLandscape2D(double width, int iterations, double heightReduc, Random random) {
		this.width = width;
		this.iterations = iterations;
		this.random = random;
		this.heightReduction = heightReduc;
	}

	public void generate() {
		double range = width / 2;// +- range to use with random
		pointList.clear();
		pointList.add(new VPoint2D(0, 0));
		pointList.add(new VPoint2D(width, 0));
		for (int i = 0; i < iterations; i++) {
			int size = pointList.size();
			int pointsToPlot = size - 1;// or Math.pow(2,i);
			ArrayList<VPoint2D> tempList = new ArrayList<VPoint2D>();
			for (int p = 0; p < pointsToPlot; p++) {
				VPoint2D mid = VPoint2D.midpoint(pointList.get(p), pointList.get(p + 1));
				tempList.add(new VPoint2D(mid.x, mid.y + MathHelper.getRandomDoubleBetween(random, -range, range)));
			}
			pointList.addAll(tempList);
			pointList.sort(new Comparator<VPoint2D>() {

				@Override
				public int compare(VPoint2D o1, VPoint2D o2) {
					return Double.compare(o1.x, o2.x);
				}
			});
			range = range / heightReduction;
		}
	}

	public Image print() {
		return print("out/FractalLandscape2D.png");
	}

	public Image print(String filename) {
		try {
			VPoint2D offset = new VPoint2D(0, width / 2);
			BufferedImage image = new BufferedImage((int) width, (int) (width), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.BLACK);
			for (int i = 0; i < pointList.size() - 1; i++) {
				double x1 = pointList.get(i).x + offset.x;
				double y1 = pointList.get(i).y + offset.y;
				double x2 = pointList.get(i + 1).x + offset.x;
				double y2 = pointList.get(i + 1).y + offset.y;
				g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			}
			File f = new File(filename);
			if (!f.exists())
				f.createNewFile();
			ImageIO.write(image, ".png", f);
			return image;
		} catch (Exception e) {
			return null;
		}
	}

}
