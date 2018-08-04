package com.meeple.main.generate.world;

import java.awt.Color;
import java.time.temporal.ValueRange;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.meeple.Treetops;
import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.MathHelper.RandomCollection;

public class WorldHeights {
	public static ValueRange deepSeaLevel;//range of deep sea
	public static ValueRange seaLevel;//range of normal sea
	public static ValueRange shoreLevel;//range of the normal land
	public static ValueRange grassLevel;//range of hills
	public static ValueRange mountainLevel;//range of mountains
	public static ValueRange snowCapLevel;//range of mountains
	public static Map<ValueRange, Color> heightColor = new HashMap<ValueRange, Color>();

	static {
		Treetops.println("Setting up things in the WorldHeight class");
		//-
		//setup levels for heightmap detection
		//-
		deepSeaLevel = ValueRange.of(-20, 0);
		seaLevel = ValueRange.of(0, 0 + 20);
		shoreLevel = ValueRange.of(seaLevel.getMaximum(), seaLevel.getMaximum() + 5);
		grassLevel = ValueRange.of(shoreLevel.getMaximum(), shoreLevel.getMaximum() + 10);
		mountainLevel = ValueRange.of(grassLevel.getMaximum(), grassLevel.getMaximum() + 27);
		//we dont use max for this so the actual generation isnt affected
		snowCapLevel = ValueRange.of(mountainLevel.getMaximum(), mountainLevel.getMaximum() + 10);

		heightColor.put(seaLevel, Color.cyan);
		heightColor.put(shoreLevel, Color.yellow.darker());
		heightColor.put(grassLevel, Color.green.darker());
		heightColor.put(mountainLevel, Color.gray);
		heightColor.put(snowCapLevel, Color.white);

	}

	public static ValueRange getRange(double number) {
		if (seaLevel.isValidValue((int) number)) {
			return seaLevel;
		}
		if (shoreLevel.isValidValue((int) number)) {
			return shoreLevel;
		}
		if (grassLevel.isValidValue((int) number)) {
			return grassLevel;
		}
		if (mountainLevel.isValidValue((int) number)) {
			return mountainLevel;
		}
		//we actually want solid land above this to always be snowcapped, regardless of generate cap
		if (number >= snowCapLevel.getMinimum()) {
			return snowCapLevel;
		}
		return deepSeaLevel;
	}

	public static ValueRange getEntireWorldHeight() {
		return ValueRange.of(deepSeaLevel.getMinimum(), snowCapLevel.getMaximum());
	}

	public static double getRandomWeightedTowards(ValueRange weight, Random random) {
		MathHelper.RandomCollection<ValueRange> a = new RandomCollection<ValueRange>(random);
		a.add(1, seaLevel).add(1, shoreLevel).add(1, grassLevel).add(1, mountainLevel).add(1, snowCapLevel).add(4, weight);
		ValueRange ran = a.next();
		double mid = MathHelper.average(ran.getMinimum(), ran.getMaximum());
		double gen = MathHelper.getRandomDoubleBetween(random, getEntireWorldHeight());
		if (gen <= mid) {
			gen += (MathHelper.average(mid, gen));
		} else if (gen > mid) {
			gen -= (MathHelper.average(mid, gen));
		}
		return gen;

	}
}
