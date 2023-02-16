package edu.spbu.matrix;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class DenseMatrix implements Matrix
{

	private final double[][] data;
	public final int rowCount;
	public final int colCount;
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
				this.rowCount = 0;
				this.colCount = 0;
				this.data = new double[0][0];
			}
			else {
				this.rowCount = rows.size();
				this.colCount = rows.get(0).split(" ").length;
				double[][] data = new double[this.rowCount][this.colCount];

				for (int i = 0; i < this.rowCount; i++) {
					String[] line = rows.get(i).split(" ");
					for (int j = 0; j < this.colCount; j++) {
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

	public DenseMatrix(int rowCount, int colCount, double[][] data) {
		if (rowCount <= 0 | colCount <= 0) {
			this.rowCount = 0;
			this.colCount = 0;
			this.data = new double[0][0];
			this.hashCode = this.hashCode();
		}
		else if (rowCount == data.length & colCount == data[0].length) {
			this.rowCount = rowCount;
			this.colCount = colCount;
			this.data = data;
			this.hashCode = this.hashCode();
		}
		else {
			throw new RuntimeException("Size arguments didn't match data array size");
		}
	}

	@Override public double getElement(int x, int y) {
		if (y >= this.rowCount | x >= this.colCount) {
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

		if (this.colCount == m.rowCount) {

			if (this.rowCount == 0 | m.colCount == 0) {
				return new DenseMatrix(0, 0, new double[0][0]);
			}

			double[][] result = new double[this.rowCount][m.colCount];
			for (int i = 0; i < this.rowCount; i++) {
				for (int j = 0; j < m.colCount; j++) {
					for (int k = 0; k < this.colCount; k++) {
						result[i][j] += this.getElement(k, i) * m.getElement(j, k);
					}
				}
			}
			return new DenseMatrix(this.rowCount, m.colCount, result);
		}
		else {
			throw new RuntimeException("Unable to multiply matrices due to wrong sizes");
		}
	}

	private Matrix mulSparse(SparseMatrix m) {
		if (this.colCount == m.row_count){
			if (this.rowCount == 0 | m.col_count == 0) {
				return new SparseMatrix(0, 0, null);
			}

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

			if (this.colCount == m.rowCount) {
				if (this.rowCount == 0 | m.colCount == 0) {
					return new DenseMatrix(0, 0, null);
				}

				MulTaskManager task_manager = new MulTaskManager(n.rowCount);
				double[][] data = new double[n.rowCount][m.colCount];

				class Multiplicator implements Runnable {
					@Override
					public void run() {
						Integer row;
						while ((row = task_manager.next()) != null){
							double[] result = new double[m.colCount];

							for (int i = 0; i < m.colCount; i++) {
								for (int j = 0; j < n.colCount; j++) {
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

				return new DenseMatrix(n.rowCount, m.colCount, data);
			}
			else {
				throw new RuntimeException("Unable to multiply matrices due to wrong sizes");
			}
		} else {
			return this.mul(o);
		}
	}

	public Matrix transpose() {
		if (this.rowCount == 0 | this.colCount == 0) {
			return this;
		}
		else {
			double[][] result = new double[this.colCount][this.rowCount];
			for (int i = 0; i < this.rowCount; i++) {
				for (int j = 0; j < this.colCount; j++) {
					result[j][i] = this.getElement(j, i);
				}
			}
			return new DenseMatrix(this.colCount, this.rowCount, result);
		}
	}

	@Override public int hashCode() {

		String caller = String.valueOf( (new Throwable().getStackTrace())[1] );
		if (caller.equals("DenseMatrix")) {

			if (this.rowCount == 0 | this.colCount == 0) {
				return 0;
			}
			int a = 0, b = 0;
			for (int i = 0; i < Math.min(this.colCount, this.rowCount); i++) {
				a += this.getElement(i, i);
				b += this.getElement(this.colCount - i - 1, i);
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

		if (this.rowCount != other.rowCount | this.colCount != other.colCount) {
			return false;
		}

		if (this.hashCode() == other.hashCode()) {

			if (this.colCount == 0 | this.rowCount == 0) {
				return true;
			}

			for (int i = 0; i < this.colCount; i++) {
				for (int j = 0; j < this.rowCount; j++) {
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
		if (this.rowCount != other.row_count | this.colCount != other.col_count) {
			return false;
		}

		if (this.colCount == 0 | this.rowCount == 0) {
			return true;
		}

		for (int i = 0; i < this.colCount; i++) {
			for (int j = 0; j < this.rowCount; j++) {
				if (Math.abs(Math.abs(other.getElement(i, j)) - Math.abs(this.getElement(i, j))) > EPSILON) {
					return false;
				}
			}
		}
		return true;
	}

	@Override public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < this.rowCount; i++) {
			for (int j = 0; j < this.colCount; j++) {
				str.append(this.getElement(j, i)).append(" ");
			}
			str.append("\n");
		}
		return str.toString();
	}

}
