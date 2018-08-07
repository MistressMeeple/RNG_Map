package com.meeple.main.generate.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.meeple.Treetops;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.VPoint2D;
import com.meeple.lib.math.VPoint3D;
import com.meeple.main.generate.fractal.SquareList;
import com.meeple.main.generate.fractal.SquareList.Square;

public class FractalLandscape {

	double maxHeight;
	double heightReduc;
	String seed;
	int subDivides;
	VPoint3D p1, p2, p3, p4;
	SquareList squareList;// = new SquareList();

	public FractalLandscape(VPoint3D pointA, VPoint3D pointB, VPoint3D pointC, VPoint3D pointD, String seed, int iterations, double maxHeight, double heightReduc) {
		this.p1 = pointA;
		this.p2 = pointB;
		this.p3 = pointC;
		this.p4 = pointD;
		this.maxHeight = maxHeight;
		this.heightReduc = heightReduc;
		this.seed = seed;
		this.subDivides = iterations;
		generate();
	}

	void generate() {
		squareList = new SquareList();
		squareList.addSquare(p1, p2, p3, p4);

		for (int itt = 0; itt < this.subDivides; itt++) {
			squareList = subdivide(squareList);
		}
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
			a = new VPoint3D(a.x, a.y, a.z * getPointAt(new VPoint2D(a.x, a.y)));
			b = new VPoint3D(b.x, b.y, b.z * getPointAt(new VPoint2D(b.x, b.y)));
			c = new VPoint3D(c.x, c.y, c.z * getPointAt(new VPoint2D(c.x, c.y)));
			d = new VPoint3D(d.x, d.y, d.z * getPointAt(new VPoint2D(d.x, d.y)));
			e = new VPoint3D(e.x, e.y, e.z * getPointAt(new VPoint2D(e.x, e.y)));

			ret.addSquare(p1, a, e, d);
			ret.addSquare(a, p2, b, e);
			ret.addSquare(e, b, p3, c);
			ret.addSquare(d, e, c, p4);
		}
		maxHeight = maxHeight / heightReduc;

		return ret;
	}

	private double getPointAt(VPoint2D p) {
		int rad = 3;
		int size = ((rad * rad) + 1) * ((rad * rad) + 1);
		double[] avg = new double[size];
		int index = 0;

		for (int x = -rad; x < rad; x++) {
			for (int y = -rad; y < rad; y++) {
				avg[index] = WorldHeights.getRandomWeightedTowards(WorldHeights.grassLevel, new Random(seedFromLocation(p.add(x * 10, y * 10)).hashCode()));
				if (WorldHeights.getRange(avg[index]) == WorldHeights.mountainLevel || WorldHeights.getRange(avg[index]) == WorldHeights.snowCapLevel) {
					double min = WorldHeights.mountainLevel.getMinimum();
					double remainder = avg[index] % min;
					double range = WorldHeights.snowCapLevel.getMaximum() - WorldHeights.mountainLevel.getMinimum();

					remainder = remainder * (1 - (remainder / (range + 1)));
					avg[index] = WorldHeights.mountainLevel.getMinimum() + remainder;
				}
				index += 1;
			}
		}
		return MathHelper.average(avg) ;
	}

	private String seedFromLocation(VPoint2D a) {
		return (seed + (a.x + "" + a.y));
	}

	public double getHeightAt(VPoint2D point) {
		return squareList.hashmap.get(point);
	}

	//TODO
	public void smooth(double scale) {
		/*for (VPoint3D point : squareList.pointList) {
			double average = 0;
			for (int x = -1; x < 1; x++) {
				for (int y = -1; y < 1; y++) {
					average += WorldHeights.getRandomWeightedTowards(WorldHeights.grassLevel, new Random(seedFromLocation(point.add(x * scale, y * scale, 0)).hashCode()));
				}
			}
			average = average / 9;
			point.z =average;
		}*/
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

			bw.write(writeVertices());
			bw.write(writeFaces(0));
			bw.close();
			Treetops.println("finished printing");
		} catch (IOException e) {

		}

	}

	public String writeVertices() {
		String ret = "";
		//Write all the vertices
		//Write the vertex normal

		for (VPoint3D point : squareList.pointList) {
			ret += (String.format("v %.6f %.6f %.6f", point.x, point.z, point.y) + System.lineSeparator());

		}
		ret.concat("vn 0.0000 1.0000 0.0000" + System.lineSeparator());
		return ret;
	}

	public String writeFaces(int offset) {
		String ret = "";
		//write all the faces
		for (int i = 0; i < squareList.squares.size(); i++) {
			Square square = squareList.squares.get(i);

			ret += (String.format("f %d %d %d %d %n", square.p1 + 1 + offset, square.p2 + 1 + offset, square.p3 + 1 + offset, square.p4 + 1 + offset));
		}
		return ret;
	}

	/**
	 * @return SquareList
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

	public void setSubDivisions(int subDivides) {
		this.subDivides = subDivides;
		generate();
	}

	public int getSubDivisions() {
		return subDivides;
	}
}
