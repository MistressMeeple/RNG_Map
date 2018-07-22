package com.meeple;

import java.text.DecimalFormat;
import java.util.Random;

import com.meeple.lib.math.RPoint;
import com.meeple.lib.math.VPoint2D;
import com.meeple.main.generate.dugeon.Room;

public class Treetops {
	//Main classes
	static long start = System.currentTimeMillis();

	public static void println(Object s) {
		System.out.println(s + " (" + new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds from start)");
	}

	public Treetops() {
		Random random = new Random(1);

		VPoint2D point = new VPoint2D(0, 5);
		println(new RPoint(point));
		point = new VPoint2D(5, 0);
		println(new RPoint(point));
		point = new VPoint2D(5, 5);
		println(new RPoint(point));
		new Room(random).printImage("Room");
	}

	public static void main(String[] args) {
		new Treetops();
	}
}
