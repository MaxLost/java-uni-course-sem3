package edu.spbu.matrix;

public class MulTaskManager {

	private int current_row = -1;
	private final int rows;

	public MulTaskManager(int n) {
		this.rows = n;
	}

	synchronized public Integer next() {
		this.current_row++;
		return ((this.current_row < this.rows) ? this.current_row : null);
	}
}
