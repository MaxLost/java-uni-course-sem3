package edu.spbu.matrix;

public class MulTaskManager {

	private int currentRow = -1;
	private final int rows;

	public MulTaskManager(int n) {
		this.rows = n;
	}

	synchronized public Integer next() {
		this.currentRow++;
		return ((this.currentRow < this.rows) ? this.currentRow : null);
	}
}
