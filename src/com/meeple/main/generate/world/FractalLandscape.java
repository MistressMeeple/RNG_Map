package com.meeple.main.generate.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.meeple.Treetops;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.VPoint2D;
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

	SquareList squareList;// = new SquareList();

	
	
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
			a = a.add(0, 0, MathHelper.getRandomDoubleBetween(random, 0, maxHeight));
			b = b.add(0, 0, MathHelper.getRandomDoubleBetween(random, 0, maxHeight));
			c = c.add(0, 0, MathHelper.getRandomDoubleBetween(random, 0, maxHeight));
			d = d.add(0, 0, MathHelper.getRandomDoubleBetween(random, 0, maxHeight));
			e = e.add(0, 0, MathHelper.getRandomDoubleBetween(random, 0, maxHeight));

			ret.addSquare(p1, a, e, d);
			ret.addSquare(a, p2, b, e);
			ret.addSquare(e, b, p3, c);
			ret.addSquare(d, e, c, p4);
		}
		maxHeight = maxHeight / heightReduc;

		return ret;
	}

	double[][] circleHeightMap() {
		int w = (int) (width + 1), h = (int) (height + 1);
		double[][] ret = new double[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {

				double dist = (new VPoint2D(x, y).distanceTo(new VPoint2D(w / 2, h / 2)));
				//finds the distance between xy and mid. divides by max dist. mults by -1 then adds 1
				double mths = ((((dist + (((w + h) / 2) / 50)) / (MathHelper.pythag(0, w / 2, 0, h / 2))) * -1) + 1);

				if (dist < ((w + h) / 2) / 2) {
					ret[x][y] = (mths * 1.5);
				} else {
					ret[x][y] /= 2;
				}
			}
		}
		return ret;
	}

	public void generate() {
		squareList = new SquareList();
		squareList.addSquare(new VPoint3D(startX, startY, 0), new VPoint3D(width + startX, startY, 0), new VPoint3D(width + startX, height + startY, 0), new VPoint3D(startX, height + startY, 0));
		//repeat for how many itterations we have
		for (int itt = 0; itt < iterations; itt++) {
			squareList = subdivide(squareList);
		}

	}

	public void combineWithCircle() {
		double[][] arr = circleHeightMap();
		for (VPoint3D point : squareList.pointList) {
			point.z = point.z * arr[(int) point.x][(int) point.y];
		}
	}

	public void smooth() {
		ArrayList<VPoint3D> tempList = new ArrayList<VPoint3D>(squareList.pointList);
		//readonly
		double[][] heightMap = new double[(int) (width + 1)][(int) (height + 1)];
		for (VPoint3D p : tempList) {
			heightMap[(int) p.x][(int) p.y] = p.z;
		}
		//writable
		double[][] tempArr = new double[(int) (width + 1)][(int) (height + 1)];
		for (int x = 1; x < width; x++) {
			for (int y = 1; y < height; y++) {
				double averageHeight = MathHelper.average(/*heightMap[x-1][y-1],*/ heightMap[x][y - 1], /*heightMap[x + 1][y-1],*/
						heightMap[x - 1][y], heightMap[x][y], heightMap[x + 1][y], /*heightMap[x-1][y + 1],*/ heightMap[x][y + 1]/*,		heightMap[x + 1][y + 1]*/);
				tempArr[x - 1][y - 1] = averageHeight;
				tempArr[x - 1][y] = averageHeight;
				tempArr[x - 1][y + 1] = averageHeight;
				tempArr[x][y + 1] = averageHeight;
				tempArr[x][y] = averageHeight;
				tempArr[x][y - 1] = averageHeight;
				tempArr[x + 1][y - 1] = averageHeight;
				tempArr[x + 1][y] = averageHeight;
				tempArr[x + 1][y + 1] = averageHeight;
			}
		}
		for (VPoint3D p : tempList) {
			p.z = heightMap[(int) p.x][(int) p.y];
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
