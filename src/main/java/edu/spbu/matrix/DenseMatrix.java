package edu.spbu.matrix;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


public class DenseMatrix implements Matrix
{

	private final double[][] data;
	public final int row_count;
	public final int col_count;
	private final int hashCode;

	/**
	 * Loads dense matrix from file
	 * @param fileName - name of file with matrix data
	 */
	public DenseMatrix(String fileName) {
		Path file = Paths.get("src/resources/" + fileName);
		try ( Scanner scanner = new Scanner(file) ) {
			ArrayList <String> rows = new ArrayList<>();

			while(scanner.hasNextLine()) {
				rows.add(scanner.nextLine());
			}

			if (rows.size() == 0 || rows.get(0).split(" ").length == 0) {
				this.row_count = 0;
				this.col_count = 0;
				this.data = new double[0][0];
			}
			else {
				this.row_count = rows.size();
				this.col_count = rows.get(0).split(" ").length;
				double[][] data = new double[this.row_count][this.col_count];

				for (int i = 0; i < this.row_count; i++) {
					String[] line = rows.get(i).split(" ");
					for (int j = 0; j < this.col_count; j++) {
						data[i][j] = Double.parseDouble(line[j]);
					}
				}

				this.data = data;

			}

			this.hashCode = this.hashCode();

		} catch (IOException e) {
			throw new RuntimeException("Cannot open file", e);
		}
	}

	public DenseMatrix(int row_count, int col_count, double[][] data) {
		if (row_count <= 0 | col_count <= 0) {
			this.row_count = 0;
			this.col_count = 0;
			this.data = new double[0][0];
			this.hashCode = this.hashCode();
		}
		else if (row_count == data.length & col_count == data[0].length) {
			this.row_count = row_count;
			this.col_count = col_count;
			this.data = data;
			this.hashCode = this.hashCode();
		}
		else {
			throw new RuntimeException("Size arguments didn't match data array size");
		}
	}

	@Override public double getElement(int x, int y) {
		if (y >= this.row_count | x >= this.col_count) {
			throw new RuntimeException("Invalid coordinates");
		}
		else {
			return data[y][x];
		}
	}

	/**
	 * Single-thread matrix multiplication
	 * <p>
	 * (1) A@B = C
	 *
	 * @param o - B matrix in (1)
	 * @return - result of matrix multiplication, C matrix in (1)
	 */
	@Override public Matrix mul(Matrix o){

		if (o instanceof DenseMatrix) {
			return mulDense((DenseMatrix) o);
		}
		else if (o instanceof SparseMatrix) {
			return mulSparse((SparseMatrix) o);
		}

		return null;
	}

	private Matrix mulDense(DenseMatrix m){

		if (this.col_count == m.row_count) {

			if (this.row_count == 0 | m.col_count == 0) {
				return new DenseMatrix(0, 0, new double[0][0]);
			}

			double[][] result = new double[this.row_count][m.col_count];
			for (int i = 0; i < this.row_count; i++) {
				for (int j = 0; j < m.col_count; j++) {
					for (int k = 0; k < this.col_count; k++) {
						result[i][j] += this.getElement(k, i) * m.getElement(j, k);
					}
				}
			}
			return new DenseMatrix(this.row_count, m.col_count, result);
		}
		else {
			throw new RuntimeException("Unable to multiply matrices due to wrong sizes");
		}
	}

	private Matrix mulSparse(SparseMatrix m) {
		if (this.col_count == m.row_count){
			if (this.row_count == 0 | m.col_count == 0) {
				return new SparseMatrix(0, 0, null);
			}

			HashMap<Integer, HashMap<Integer, Double>> data = new HashMap<>();

			SparseMatrix x = (SparseMatrix) m.transpose();
			DenseMatrix y = (DenseMatrix) this.transpose();

			return ((SparseMatrix) x.mul(y)).transpose();
		} else {
			throw new RuntimeException("Unable to multiply matrices due to wrong sizes");
		}
	}

	/**
	 * Multi-thread matrix multiplication
	 * <p>
	 * (1) A@B = C
	 *
	 * @param o - B matrix in (1)
	 * @return - result of matrix multiplication, C matrix in (1)
	 */
	@Override public Matrix dmul(Matrix o) {

		if (o instanceof DenseMatrix) {
			DenseMatrix m = (DenseMatrix) o;
			DenseMatrix n = this;

			if (this.col_count == m.row_count) {
				if (this.row_count == 0 | m.col_count == 0) {
					return new DenseMatrix(0, 0, null);
				}

				MulTaskManager task_manager = new MulTaskManager(n.row_count);
				double[][] data = new double[n.row_count][m.col_count];

				class Multiplicator implements Runnable {
					@Override
					public void run() {
						Integer row;
						while ((row = task_manager.next()) != null){
							double[] result = new double[m.col_count];

							for (int i = 0; i < m.col_count; i++) {
								for (int j = 0; j < n.col_count; j++) {
									result[i] += n.getElement(j, row) * m.getElement(i, j);
								}
							}
							data[row] = result;
						}
					}
				}

				Thread[] threads = new Thread[4];
				for (int i = 0; i < threads.length; i++) {
					threads[i] = new Thread(new Multiplicator());
					threads[i].start();
				}
				try {
					for (Thread thread : threads) {
						thread.join();
					}
				} catch (InterruptedException e) {
					throw new RuntimeException("Multiplication failed! Try again!", e);
				}

				return new DenseMatrix(n.row_count, m.col_count, data);
			}
			else {
				throw new RuntimeException("Unable to multiply matrices due to wrong sizes");
			}
		} else {
			return this.mul(o);
		}
	}

	public Matrix transpose() {
		if (this.row_count == 0 | this.col_count == 0) {
			return this;
		}
		else {
			double[][] result = new double[this.col_count][this.row_count];
			for (int i = 0; i < this.row_count; i++) {
				for (int j = 0; j < this.col_count; j++) {
					result[j][i] = this.getElement(j, i);
				}
			}
			return new DenseMatrix(this.col_count, this.row_count, result);
		}
	}

	@Override public int hashCode() {

		String caller = String.valueOf( (new Throwable().getStackTrace())[1] );
		if (caller.equals("DenseMatrix")) {

			if (this.row_count == 0 | this.col_count == 0) {
				return 0;
			}
			int a = 0, b = 0;
			for (int i = 0; i < Math.min(this.col_count, this.row_count); i++) {
				a += this.getElement(i, i);
				b += this.getElement(this.col_count - i - 1, i);
			}

			return a ^ b;
		}
		else {
			return this.hashCode;
		}
	}

	/**
	 * Compares various types of matrices
	 * @param o - Object with which this matrix will be compared
	 * @return - true if objects equals, false if not
	 */
	@Override public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		else if (o instanceof DenseMatrix) {
			return equalsDense((DenseMatrix) o);
		}
		else if (o instanceof SparseMatrix) {
			return equalsSparse((SparseMatrix) o);
		}

		return false;
	}

	private boolean equalsDense(DenseMatrix other){

		if (this.row_count != other.row_count | this.col_count != other.col_count) {
			return false;
		}

		if (this.hashCode() == other.hashCode()) {

			if (this.col_count == 0 | this.row_count == 0) {
				return true;
			}

			for (int i = 0; i < this.col_count; i++) {
				for (int j = 0; j < this.row_count; j++) {
					if (Math.abs(Math.abs(other.getElement(i, j)) - Math.abs(this.getElement(i, j))) > EPSILON) {
						return false;
					}
				}

			}
			return true;
		}
		return false;
	}

	private boolean equalsSparse(SparseMatrix other) {
		if (this.row_count != other.row_count | this.col_count != other.col_count) {
			return false;
		}

		if (this.col_count == 0 | this.row_count == 0) {
			return true;
		}

		for (int i = 0; i < this.col_count; i++) {
			for (int j = 0; j < this.row_count; j++) {
				if (Math.abs(Math.abs(other.getElement(i, j)) - Math.abs(this.getElement(i, j))) > EPSILON) {
					return false;
				}
			}
		}
		return true;
	}

	@Override public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < this.row_count; i++) {
			for (int j = 0; j < this.col_count; j++) {
				str.append(this.getElement(j, i)).append(" ");
			}
			str.append("\n");
		}
		return str.toString();
	}

}
