package com.meeple.main.generate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import com.meeple.lib.math.VPoint3D;

public class FlatMap {
	double width, height;
	double resolution;
	Random random;
	Set<VPoint3D> points = new TreeSet<VPoint3D>(new Comparator<VPoint3D>() {
		@Override
		public int compare(VPoint3D o1, VPoint3D o2) {
			return o1.equals(o2) ? 0 : o1.hashCode() - o2.hashCode();
		}
	});

	public FlatMap(double w, double h, double res, Random r) {
		this.width = w;
		this.height = h;
		this.random = r;
		this.resolution = res;
	}

	void generate() {
		for (double x = 0; x < width; x += resolution) {
			for (double y = 0; y < height; y += resolution) {
				points.add(new VPoint3D(x, y, random.nextDouble()));
			}
		}
	}
	public void writeToPNG(String fileLoc) throws IOException{
		if (fileLoc.contains(".")) {
			if (fileLoc.split("\\.")[fileLoc.split("\\.").length - 1] != "obj") {
				fileLoc = fileLoc.concat(".obj");
			}
		} else {
			fileLoc = fileLoc.concat(".obj");
		}

		File f = new File(fileLoc);
		if (!f.exists()) {
			f.createNewFile();
		}
		
		BufferedImage bi = new BufferedImage((int)width,(int)height,BufferedImage.TYPE_INT_ARGB);
		for(VPoint3D point:points){
			
		}
		
		
		try {
		    // retrieve image
//		    BufferedImage bi = getMyImage();
		    File outputfile = new File("saved.png");
		    ImageIO.write(bi, "png", outputfile);
		} catch (IOException e) {

		}
	}
}
