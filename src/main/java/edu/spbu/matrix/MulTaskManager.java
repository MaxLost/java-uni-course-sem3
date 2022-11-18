package edu.spbu.matrix;

import java.util.Iterator;

public class MulTaskManager implements Iterator {

	private int current_row = -1;
	private final int rows;

	public MulTaskManager(int n){
		this.rows = n;
	}

	/**
	 *  Don't use this method alone with threads
	 **/
	@Override
	synchronized public boolean hasNext() {
		return this.current_row < this.rows;
	}

	@Override
	synchronized public Integer next() {
		this.current_row++;
		return this.hasNext() ? this.current_row : null;
	}
}
