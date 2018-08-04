package com.meeple;

import java.text.DecimalFormat;
import java.util.Random;

import com.meeple.main.generate.CircleIslandGenerator;
import com.meeple.main.generate.WorldGenerator;
import com.meeple.main.generate.dugeon.TileBasedRoom;

public class Treetops {
	//Main classes
	static long start = System.currentTimeMillis();

	public static void println(Object s) {
		System.out.println(s + " (" + new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds from start)");
	}

	public Treetops() {
		Random random = new Random(1);
		//TileBasedRoom a = new TileBasedRoom(random);
		//a.generate();
		//a.printImage("TBR");
		
		//CircleIslandGenerator cig = new CircleIslandGenerator(random);
		//cig.printImage("cig");
		WorldGenerator wg = new WorldGenerator(random);
		
	}

	public static void main(String[] args) {
		new Treetops();
	}
}
