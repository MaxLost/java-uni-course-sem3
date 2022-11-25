package edu.spbu.matrix;

import java.util.Iterator;

public class MulTaskManager implements Iterator {

	private int current_row = -1;
	private final int rows;

	public MulTaskManager(int n){
		this.rows = n;
	}

	/**
	 *  Dummy method
	 **/
	@Override
	synchronized public boolean hasNext() {
		return false;
	}

	@Override
	synchronized public Integer next() {
		this.current_row++;
		return (this.current_row < this.rows) ? this.current_row : null;
	}
}
