package com.meeple.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

public class JuliaSet {
	private final int maxIter = 300;
	private final double zoom;
	private double cY, cX;
	private final Random r;

	public JuliaSet(double zoom) {
		this.zoom=zoom;
		r = new Random();
		cX = -0.7*(r.nextDouble()-0.5);
		cY = 0.27015*(r.nextDouble()-0.5);
	}

	public void drawJuliaSet(Graphics g,int w,int h) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		
		double moveX = 0, moveY = 0;
		double zx, zy;

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				zx = 1.5 * (x - w / 2) / (0.5 * zoom * w) + moveX;
				zy = (y - h / 2) / (0.5 * zoom * h) + moveY;
				float i = maxIter;
				while (zx * zx + zy * zy < 4 && i > 0) {
					double tmp = zx * zx - zy * zy + cX;
					zy = 2.0 * zx * zy + cY;
					zx = tmp;
					i--;
				}
				int c = Color.HSBtoRGB((maxIter / i) % 1, 1, i > 0 ? 1 : 0);
				image.setRGB(x, y, c);
			}
		}
		g2.drawImage(image, 0, 0, null);
		g2.setColor(Color.black);
		g2.drawString(String.format("cX: %f, cY: %f",cX,cY), 0, h+10);
	}

	
}
