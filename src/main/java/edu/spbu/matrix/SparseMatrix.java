package edu.spbu.matrix;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


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
				this.data = null;
			}
			else {
				this.row_count = rows.size();
				this.col_count = rows.get(0).split(" ").length;


				HashMap<Integer, HashMap<Integer, Double>> data = new HashMap<>();

				for (int i = 0; i < this.row_count; i++) {
					String[] numbers = rows.get(i).split(" ");
					for (int j = 0; j < this.col_count; j++) {
						data.computeIfAbsent(i, t -> new HashMap<Integer, Double>());
						double value = Double.parseDouble(numbers[j]);
						if (Math.abs(value) > EPSILON) {
							data.get(i).put(j, value);
						}
					}
				}

				this.data = data;
			}
			this.hashCode = this.hashCode();

		} catch (IOException e) {
			throw new RuntimeException("Cannot open file", e);
		}
	}

	public SparseMatrix (int row_count, int col_count, HashMap<Integer, HashMap<Integer, Double>> data){
		this.data = data;
		this.row_count = row_count;
		this.col_count = col_count;
		this.hashCode = hashCode();
	}

	@Override public double getElement(int x, int y) {
		if (x >= this.col_count | y >= this.row_count){
			throw new RuntimeException("Invalid coordinates");
		} else {
			HashMap<Integer, Double> row = this.data.get(y);
			if (row == null) { return 0; }
			Double value = row.get(x);
			return  value == null ? 0 : value;
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
	@Override public Matrix mul(Matrix o) {
		if (o instanceof SparseMatrix) {
			return mulSparse((SparseMatrix) o);
		}
		else if (o instanceof DenseMatrix){
			return mulDense((DenseMatrix) o);
		}

		return null;
	}

	private Matrix mulDense(DenseMatrix m){

		if (this.col_count == m.row_count){
			if (this.row_count == 0 | m.col_count == 0) {
				return new SparseMatrix(0, 0, null);
			}

			HashMap<Integer, HashMap<Integer, Double>> data = new HashMap<>();

			for (Integer row : this.data.keySet()){
				for (int column = 0; column < m.col_count; column++){
					double sum = 0;
					for (Integer element : this.data.get(row).keySet()) {
						sum += this.getElement(element, row) * m.getElement(column, element);
					}
					if (Math.abs(sum) > EPSILON){
						data.computeIfAbsent(row, t -> new HashMap<>());
						data.get(row).put(column, sum);
					}
				}
			}

			return new SparseMatrix(this.row_count, m.col_count, data);
		} else {
			throw new RuntimeException("Unable to multiply matrices due to wrong sizes");
		}
	}

	private Matrix mulSparse(SparseMatrix m){

		if (this.col_count == m.row_count){
			if (this.row_count == 0 | m.col_count == 0) {
				return new SparseMatrix(0, 0, null);
			}
			HashMap<Integer, HashMap<Integer, Double>> data = new HashMap<>();

			SparseMatrix x = (SparseMatrix) m.transpose();

			for (Integer row : this.data.keySet()){
				for (Integer column : x.data.keySet()){
					double sum = 0;
					for (Integer element : this.data.get(row).keySet()) {
						sum += this.getElement(element, row) * x.getElement(element, column);
					}
					if (Math.abs(sum) > EPSILON){
						data.computeIfAbsent(row, t -> new HashMap<>());
						data.get(row).put(column, sum);
					}
				}
			}
			return new SparseMatrix(this.row_count, m.col_count, data);

		}
		else {
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
		return null;
	}

	public Matrix transpose() {
		if (this.row_count == 0 | this.col_count == 0) {
			return this;
		}
		else {
			HashMap<Integer, HashMap<Integer, Double>> data = new HashMap<>();
			for (Integer i : this.data.keySet()){
				for (Integer j : this.data.get(i).keySet()){
					data.computeIfAbsent(j, t -> new HashMap<Integer, Double>());
					data.get(j).put(i, this.getElement(j, i));
				}
			}
			return new SparseMatrix(this.col_count, this.row_count, data);
		}
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

	private boolean equalsDense(DenseMatrix o) {

		if (this.row_count != o.row_count | this.col_count != o.col_count) {
			return false;
		}

		if (this.row_count == 0 | this.col_count == 0) { return true; }

		for (int i = 0; i < this.row_count; i++){
			for (int j = 0; j < this.col_count; j++) {
				if (Math.abs(Math.abs(o.getElement(j, i)) - Math.abs(this.getElement(j, i))) > EPSILON) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean equalsSparse(SparseMatrix o) {

		if (this.row_count != o.row_count | this.col_count != o.col_count) {
			return false;
		}

		if (this.hashCode() == o.hashCode()) {

			if (this.row_count == 0 | this.col_count == 0) { return true; }

			for (int i = 0; i < this.row_count; i++){
				for (int j = 0; j < this.col_count; j++) {
					if (Math.abs(Math.abs(o.getElement(j, i)) - Math.abs(this.getElement(j, i))) > EPSILON) {
						return false;
					}
				}
			}

			return true;
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
