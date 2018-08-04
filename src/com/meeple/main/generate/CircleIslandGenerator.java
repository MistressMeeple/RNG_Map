package com.meeple.main.generate;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Random;

import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.RPoint;
import com.meeple.lib.math.VPoint2D;

public class CircleIslandGenerator extends Printable {
	Polygon island;
	ArrayList<Polygon> test = new ArrayList<Polygon>();
	int width = 500, height = 500;
	ValueRange vr = ValueRange.of((long) (width * 0.1), (long) (width * 0.9));
	Random random;

	public CircleIslandGenerator(Random random) {
		this.random = random;
		island = new Polygon();

		test.add(addRandomCircle());
		System.out.println(vr);

	}

	Polygon addRandomCircle() {
		return addCircle(MathHelper.getRandomDoubleBetween(random, 0, 10), new VPoint2D(MathHelper.getRandomDoubleBetween(random, vr), MathHelper.getRandomDoubleBetween(random, vr)));
	}

	Polygon addCircle(double radius, VPoint2D center) {
		Polygon circle = new Polygon();
		int points = 16;
		VPoint2D curr = center.add(0, radius);
		for (int i = 0; i < 360; i += (360 / points)) {
			RPoint a = new RPoint(curr);
			VPoint2D temp = curr.rotate(i, center);
			circle.addPoint((int) temp.x, (int) temp.y);
		}
		return circle;
	}

	@Override
	public BufferedImage printImage(String filename) {
		BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage image2 = new BufferedImage((int) (image.getWidth() * 1.2), (int) (image.getHeight() * 1.2), BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = image.createGraphics();
		g.setColor(new Color(0, 100, 50, 10));
		for (int i = 0; i < image.getWidth() / 5; i++) {
			VPoint2D v = new VPoint2D(MathHelper.getRandomDoubleBetween(random, vr), MathHelper.getRandomDoubleBetween(random, vr));
			double distToEdge = image.getWidth();
			double edgeX = v.x, edgeY = v.y;
			//find nearest edge, if
			double XdistToZero = v.x;
			double XdistToWidth = image.getWidth() - v.x;

			double YdistToZero = v.y;
			double YdistToHeight = image.getHeight() - v.y;
			if (v.x >= v.y) {
				if (v.x < image.getWidth() / 2) {
					edgeX = 0;
				} else {
					edgeX = image.getWidth();
				}
			} else {
				if (v.y < image.getHeight() / 2) {
					edgeY = 0;
				} else {
					edgeY = image.getHeight();
				}
			}
			double maxRad = MathHelper.pythag(v.x, edgeX, v.y, edgeY);
			double rad = MathHelper.getRandomDoubleBetween(random, ValueRange.of((long) (0), (long) (maxRad)));
			g.fillOval((int) (v.x - rad), (int) (v.y - rad), (int) rad * 2, (int) rad * 2);
		}
		g.setColor(Color.black);
		for (Polygon p : test) {
			//g.drawPolygon(p);
		}
		image2.createGraphics().drawImage(image, image.getWidth() / 10, image.getHeight() / 10, null);
		tryWriteImage(image2, filename);
		return image2;
	}
}
