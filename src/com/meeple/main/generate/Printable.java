package com.meeple.main.generate;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Printable {

	protected void tryWriteImage(BufferedImage image, String filename) {
		try {
			File outputfile = new File("out/" + filename + ".png");
			if (!outputfile.exists()) {
				outputfile.createNewFile();
			}
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage printImage(String filename) {
		BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		g.setColor(Color.black);
		g.drawString("Has not overriden printImage", 0, 0);

		tryWriteImage(image,filename);
		return image;
	}
}
