package edu.spbu.matrix;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix
{
	private final double[][] data;
	public final int row_count;
	public final int col_count;

	private int hashCode = 0;
	/**
	 * Loads dense matrix from file
	 * @param fileName - name of file with matrix data
	 */
	public DenseMatrix(String fileName) {
		Path file = Paths.get("src\\resources\\" + fileName);
		try ( Scanner scanner = new Scanner(file) ) {

			int row_count = 0;
			try ( Scanner counter = new Scanner(file) ) {
				String line = "";
				while (counter.hasNextLine()) {
					row_count++;
					line = counter.nextLine();
				}
				this.row_count = row_count;
				this.col_count = line.split(" ").length;
			}

			this.data = new double[this.row_count][this.col_count];

			for (int i = 0; i < this.row_count; i++) {
				String[] numbers = scanner.nextLine().split(" ");
				for (int j = 0; j < this.col_count; j++) {
					this.data[i][j] = Double.parseDouble(numbers[j]);
				}
			}

		} catch (IOException e) {
			throw new RuntimeException("Cannot open file", e);
		}
	}

	public DenseMatrix(int row_count, int col_count, double[][] data) {
		if (row_count == data.length & col_count == data[0].length) {
			this.row_count = row_count;
			this.col_count = col_count;
			this.data = data;
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
	 * должно поддерживаться для всех 4-х вариантов
	 * <p>
	 * (1) A@B = C
	 *
	 * @param o - B matrix in (1)
	 * @return - result of matrix multiplication, C matrix in (1)
	 */
	@Override public Matrix mul(Matrix o) {

		if (o instanceof DenseMatrix) {

			DenseMatrix b = (DenseMatrix) o;
			if (this.col_count == b.row_count) {

				double[][] result = new double[this.row_count][b.col_count];
				for (int i = 0; i < this.row_count; i++) {
					for (int j = 0; j < b.col_count; j++) {
						for (int k = 0; k < this.col_count; k++) {
							result[i][j] += this.getElement(k, i) * b.getElement(j, k);
						}
					}
				}
				return new DenseMatrix(this.row_count, b.col_count, result);
			}
			else {
				throw new RuntimeException("Unable to multiply matrices due to their sizes");
			}
		}

		return null;
	}

	/**
	 * Multi-thread matrix multiplication
	 * <p>
	 * (1) A@B = C
	 *
	 * @param o - B matrix in (1)
	 * @return - result of matrix multiplication, C matrix in (1)
	 */
	@Override public Matrix dmul(Matrix o)
	{
		return null;
	}

	public int hashCode() {
		if (this.hashCode == 0) {
			int a = 0, b = 0;
			for (int i = 0; i < Math.min(this.col_count, this.row_count); i++) {
				a += this.getElement(i, i);
				b += this.getElement(this.col_count - i - 1, i);
			}
			this.hashCode = a ^ b;
		}
		return this.hashCode;
	}

	/**
	 * спавнивает с обоими вариантами
	 * @param o - Object with which DenseMatrix comparing
	 * @return - true if objects equals, false if not
	 */
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		else if (o instanceof DenseMatrix) {
			if (this.row_count != ((DenseMatrix) o).row_count | this.col_count != ((DenseMatrix) o).col_count) {
				return false;
			}
			if (this.hashCode() == o.hashCode()) {
				for (int i = 0; i < this.col_count; i++) {
					for (int j = 0; j < this.row_count; j++) {
						if (Math.abs(((DenseMatrix) o).getElement(i, j)) - Math.abs(this.getElement(i, j)) > 0.0001 |
								Math.abs(((DenseMatrix) o).getElement(i, j)) - Math.abs(this.getElement(i, j)) < -0.0001) {
							return false;
						}
					}

				}
				return true;
			}
		}


		return false;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < this.row_count; i++) {
			for (double x : this.data[i]) {
				str.append(x).append(" ");
			}
			str.append("\n");
		}
		return str.toString();
	}

}
