package edu.spbu.matrix;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix
{
	private final HashMap<Integer, HashMap<Integer, Double>> data;
	public final int row_count;
	public final int col_count;
	private final int hashCode;

	/**
	 * Loads sparse matrix from file
	 * @param fileName - path to file with matrix data
	 */
	public SparseMatrix(String fileName) {
		Path file = Paths.get("src/resources/" + fileName);
		try (Scanner scanner = new Scanner(file)){
			ArrayList <String> rows = new ArrayList<>();

			while(scanner.hasNextLine()){
				rows.add(scanner.nextLine());
			}

			if (rows.size() == 0 || rows.get(0).split(" ").length == 0){
				this.row_count = 0;
				this.col_count = 0;
			}
			else {
				this.row_count = rows.size();
				this.col_count = rows.get(0).split(" ").length;
			}

			HashMap<Integer, HashMap<Integer, Double>> data = new HashMap<>();

			for (int i = 0; i < this.row_count; i++){
				String[] numbers = rows.get(i).split(" ");
				for (int j = 0; j < this.col_count; j++){
					data.computeIfAbsent(i, key -> new HashMap<Integer, Double>());
					double value = Double.parseDouble(numbers[j]);
					if (Math.abs(value) > EPSILON) {
						data.get(i).put(j, value);
					}
				}
			}

			this.data = data;
			this.hashCode = this.hashCode();

		} catch (IOException e) {
			throw new RuntimeException("Cannot open file", e);
		}
	}

	@Override public double getElement(int x, int y) {
		if (x >= this.col_count | y <= this.row_count){
			throw new RuntimeException("Invalid coordinates");
		} else {
			Double value = this.data.get(y).get(x);
			return  value == null ? 0 : value;
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

	@Override public int hashCode(){

		String caller = String.valueOf( (new Throwable().getStackTrace())[1] );
		if (caller.equals("SparseMatrix")) {

			if (this.row_count == 0 | this.col_count == 0) {
				return 0;
			}

			int a = 0, b = 0;
			for (int i = 0; i < this.col_count; i++){
				HashMap<Integer, Double> row = this.data.get(i);
				if (row != null) {

					if (row.values().iterator().hasNext()){
						a += row.values().iterator().next().intValue();
					}
					if (row.values().iterator().hasNext()){
						b += row.values().iterator().next().intValue();
					}
				}
			}
			return a ^ b;
		}
		return this.hashCode;
	}

	private boolean equalsDense(DenseMatrix other) {

		if (this.row_count != other.row_count | this.col_count != other.col_count) {
			return false;
		}

		if (this.row_count == 0 | this.col_count == 0) { return true; }

		for (int i = 0; i < this.row_count; i++){
			for (int j = 0; j < this.col_count; j++) {
				if (Math.abs(Math.abs(other.getElement(i, j)) - Math.abs(this.getElement(i, j))) > EPSILON) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean equalsSparse(SparseMatrix other) {

		if (this.row_count != other.row_count | this.col_count != other.col_count) {
			return false;
		}

		if (this.hashCode() == other.hashCode()) {

			if (this.row_count == 0 | this.col_count == 0) { return true; }

			for (int i = 0; i < this.row_count; i++){
				for (int j = 0; j < this.col_count; j++) {
					if (Math.abs(Math.abs(other.getElement(i, j)) - Math.abs(this.getElement(i, j))) > EPSILON) {
						return false;
					}
				}
			}

			return true;
		}
		return false;
	}

	@Override public boolean equals(Object o) {

		if (this == o) { return true; }

		if (o instanceof SparseMatrix) {
			return equalsSparse((SparseMatrix) o);
		}

		if (o instanceof DenseMatrix) {
			return equalsDense((DenseMatrix) o);
		}

		return false;
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
