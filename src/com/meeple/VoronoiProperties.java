package com.meeple;
public class VoronoiProperties {
	public int size;
	public int cells;
	public long seed;

	public VoronoiProperties() {
		this.size = 1000;
		this.cells = 100;
		this.seed = System.currentTimeMillis();
	}

	public VoronoiProperties(int size, int cells, long seed) {
		this();
		this.size = size;
		this.cells = cells;
		this.seed = seed;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getCells() {
		return cells;
	}

	public void setCells(int cells) {
		this.cells = cells;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

}
