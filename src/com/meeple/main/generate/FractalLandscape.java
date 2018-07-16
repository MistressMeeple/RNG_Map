package com.meeple.main.generate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.meeple.Treetops;
import com.meeple.lib.math.VPoint3D;
import com.meeple.main.generate.fractal.SquareList;
import com.meeple.main.generate.fractal.SquareList.Square;

public class FractalLandscape {

	double startX, startY;
	double width, height;
	Random random;
	int iterations;
	double maxHeight = 100;
	double heightReduc = 2;

	SquareList squareList = new SquareList();

	public FractalLandscape(double startX, double startY, double width, double height, Random random, int iterations) {
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
		this.random = random;
		this.iterations = iterations;
		generate();
	}

	public FractalLandscape(double width, double height, Random random, int iterations) {
		this(0, 0, width, height, random, iterations);
	}

	public FractalLandscape(double width, double height, Random random) {
		this(0, 0, width, height, random, 50);
	}

	/**
	 * 
	 * @param array to subdivide
	 * @return subdivieded array
	 */
	SquareList subdivide(SquareList arr) {
		SquareList ret = new SquareList();
		for (Square s : arr.squares) {
			VPoint3D p1 = s.getP1();
			VPoint3D p2 = s.getP2();
			VPoint3D p3 = s.getP3();
			VPoint3D p4 = s.getP4();
			VPoint3D a = VPoint3D.midpoint(p1, p2);
			VPoint3D b = VPoint3D.midpoint(p2, p3);
			VPoint3D c = VPoint3D.midpoint(p3, p4);
			VPoint3D d = VPoint3D.midpoint(p4, p1);
			VPoint3D e = new VPoint3D((p1.x + p2.x + p3.x + p4.x) / 4, (p1.y + p2.y + p3.y + p4.y) / 4, (p1.z + p2.z + p3.z + p4.z) / 4);
			//add random height
			a = a.add(0, 0, random.nextDouble() * maxHeight);
			b = b.add(0, 0, random.nextDouble() * maxHeight);
			c = c.add(0, 0, random.nextDouble() * maxHeight);
			d = d.add(0, 0, random.nextDouble() * maxHeight);
			e = e.add(0, 0, random.nextDouble() * maxHeight);

			ret.addSquare(p1, a, e, d);
			ret.addSquare(a, p2, b, e);
			ret.addSquare(e, b, p3, c);
			ret.addSquare(d, e, c, p4);
		}
		maxHeight = maxHeight / heightReduc;

		return ret;
	}

	long getSeedForSquare(int dcp, VPoint3D... args) {
		String tot = "";
		for (VPoint3D a : args) {
			String x = a.x + "";
			String y = a.y + "";
			String z = a.z + "";
			x = x.replace(".", "").substring(0, dcp);
			y = y.replace(".", "").substring(0, dcp);
			z = z.replace(".", "").substring(0, dcp);
			tot += Integer.parseInt(x) + "" + Integer.parseInt(y) + "" + Integer.parseInt(z);
			//tot += a.x + "" + a.y + "" + a.z;
		}

		//		tot = tot;
		return Long.parseLong(tot);
	}

	public void generate() {
		squareList.addSquare(new VPoint3D(), new VPoint3D(width, 0, 0), new VPoint3D(width, height, 0), new VPoint3D(0, height, 0));
		//repeat for how many itterations we have
		for (int itt = 0; itt < iterations; itt++) {
			squareList = subdivide(squareList);
			Treetops.println("Itt: " + (itt + 1) + "/" + iterations + ", " + squareList.squares.size());
		}

	}

	public void print() {
		print("out/FractalLandscape");
	}

	public void print(String filename) {
		Treetops.println("Starting print for Fractal Landscape");
		Treetops.println("Points in squarelist: " + squareList.pointList.size());
		Treetops.println("Squares: " + squareList.squares.size());
		try {
			if (filename.contains(".")) {
				if (filename.split("\\.")[filename.split("\\.").length - 1] != "obj") {
					filename = filename.concat(".obj");
				}
			} else {
				filename = filename.concat(".obj");
			}

			File f = new File(filename);
			if (!f.exists()) {
				f.createNewFile();
			}

			squareList.sortPoints();

			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write("o poly" + System.lineSeparator());

			//Write all the vertices
			for (VPoint3D point : squareList.pointList) {
				String text = String.format("v %.6f %.6f %.6f", point.x, point.z, point.y) + System.lineSeparator();
				bw.write(text);
			}
			//Write the vertex normal
			bw.write("vn 0.0000 1.0000 0.0000" + System.lineSeparator());

			//write all the faces
			for (int i = 0; i < squareList.squares.size(); i++) {
				Square square = squareList.squares.get(i);

				bw.write(String.format("f %d %d %d %d %n", square.p1 + 1, square.p2 + 1, square.p3 + 1, square.p4 + 1));
			}
			bw.close();
			Treetops.println("finished printing");
		} catch (

		IOException e) {

		}

	}

	/**
	 * @return the squareList
	 */
	public SquareList getSquareList() {
		return squareList;
	}

	/**
	 * @param squareList the squareList to set
	 */
	public void setSquareList(SquareList squareList) {
		this.squareList = squareList;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}
	
}
