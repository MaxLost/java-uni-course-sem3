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
	private final double[][] matrix;
	private final int row_count;
	private final int col_count;
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

			this.matrix = new double[this.row_count][this.col_count];

			for (int i = 0; i < this.row_count; i++) {
				for (int j = 0; j < this.col_count; j++) {
					this.matrix[i][j] = Double.parseDouble(scanner.next());
				}
			}

		} catch (IOException e) {
			throw new RuntimeException("Cannot open file", e);
		}
	}

	public double getElement(int x, int y) {
		if (y >= row_count | x >= col_count) {
			throw new RuntimeException("Invalid coordinates");
		}
		else {
			return matrix[x][y];
		}
	}

	/**
	 * однопоточное умнджение матриц
	 * должно поддерживаться для всех 4-х вариантов
	 *
	 * @param o
	 * @return
	 */
	@Override public Matrix mul(Matrix o)
	{
		return null;
	}

	/**
	 * многопоточное умножение матриц
	 *
	 * @param o
	 * @return
	 */
	@Override public Matrix dmul(Matrix o)
	{
		return null;
	}

	/**
	 * спавнивает с обоими вариантами
	 * @param o
	 * @return
	 */
	@Override public boolean equals(Object o) {
		return false;
	}

}
