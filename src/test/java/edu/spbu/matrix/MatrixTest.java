package edu.spbu.matrix;

import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class MatrixTest
{
	@Test
	public void loadDenseMatrix() {
		DenseMatrix m = new DenseMatrix("dense_test/load_test.txt");
		double[][] expected_data = { {1.0, 2.0, 0}, {3.0, 4.0, 1.0}, {10.0, 8.0, 0} };
		DenseMatrix expected = new DenseMatrix(3, 3, expected_data);
		assertEquals(expected, m);
	}

	@Test
	public void loadEmptyDenseMatrix() {
		DenseMatrix m = new DenseMatrix("dense_test/empty.txt");
		DenseMatrix expected = new DenseMatrix(0, 0, new double[0][0]);
		assertEquals(expected, m);
	}

	@Test
	public void equalsDDTest() {
		double[][] data = { {1, 0, 0}, {0, 1, 0}, {0, 0, 1} };
		DenseMatrix m1 = new DenseMatrix(3, 3, data);
		DenseMatrix m2 = new DenseMatrix(3, 3, data);
		assertEquals(m1, m2);
	}

	@Test
	public void transposeDenseTest() {
		DenseMatrix m1 = new DenseMatrix("dense_test/2x4.txt");
		Matrix result = m1.transpose();
		DenseMatrix expected = new DenseMatrix("dense_test/2x4_transposed.txt");
		assertEquals(expected, result);
	}

	@Test
	public void transposeSparseTest() {
		SparseMatrix m1 = new SparseMatrix("sparse_test/5x3.txt");
		Matrix result = m1.transpose();
		SparseMatrix expected = new SparseMatrix("sparse_test/5x3_transposed.txt");
		assertEquals(expected, result);
	}

	@Test
	public void mulZeroSizedMatrix() {
		DenseMatrix m1 = new DenseMatrix(0, 0, new double[0][0]);
		DenseMatrix m2 = new DenseMatrix(0, 0, new double[0][0]);
		Matrix result = m1.mul(m2);
		DenseMatrix expected = new DenseMatrix(0, 0, new double[0][0]);
		assertEquals(expected, result);
	}

	@Test
	public void mulDV() {
		DenseMatrix m1 = new DenseMatrix("dense_test/2x4.txt");
		DenseMatrix v = new DenseMatrix("dense_test/vector.txt");
		Matrix result = m1.mul(v);
		DenseMatrix expected = new DenseMatrix("dense_test/m@v.txt");
		assertEquals(expected, result);
	}

	@Test
	public void mulDDSizeTest() {
		DenseMatrix m1 = new DenseMatrix("dense_test/4x3.txt");
		DenseMatrix m2 = new DenseMatrix("dense_test/2x4.txt");
		boolean catched = false;
		try {
			m1.mul(m2);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Unable to multiply matrices due to wrong sizes")) {
				catched = true;
			}
		}
		if (!catched) { fail("Multiplication with wrong sizes happened"); }
	}

	@Test
	public void mulSSSizeTest() {
		Matrix m1 = new SparseMatrix("sparse_test/5x3.txt");
		Matrix m2 = new SparseMatrix("sparse_test/4x5.txt");
		boolean catched = false;
		try {
			m1.mul(m2);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Unable to multiply matrices due to wrong sizes")) {
				catched = true;
			}
		}
		if (!catched) { fail("Multiplication with wrong sizes happened"); }
	}

	@Test
	public void mulSDSizeTest() {
		Matrix m1 = new SparseMatrix("sparse_test/5x3.txt");
		Matrix m2 = new DenseMatrix("dense_test/2x4.txt");
		boolean catched = false;
		try {
			m1.mul(m2);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Unable to multiply matrices due to wrong sizes")) {
				catched = true;
			}
		}
		if (!catched) { fail("Multiplication with wrong sizes happened"); }
	}

	@Test
	public void mulDSSizeTest() {
		Matrix m1 = new DenseMatrix("dense_test/2x4.txt");
		Matrix m2 = new SparseMatrix("sparse_test/5x3.txt");
		boolean catched = false;
		try {
			m1.mul(m2);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Unable to multiply matrices due to wrong sizes")) {
				catched = true;
			}
		}
		if (!catched) { fail("Multiplication with wrong sizes happened"); }
	}

	@Test
	public void mulDD1() {
		Matrix m1 = new DenseMatrix("dense_test/m1.txt");
		Matrix m2 = new DenseMatrix("dense_test/m2.txt");
		Matrix result = m1.mul(m2);
		Matrix expected = new DenseMatrix("dense_test/m1@m2.txt");
		assertEquals(expected, result);
	}

	@Test
	public void mulDD2() {
		Matrix m1 = new DenseMatrix("dense_test/m1.txt");
		Matrix m2 = new DenseMatrix("dense_test/m2.txt");
		Matrix result = m2.mul(m1);
		Matrix expected = new DenseMatrix("dense_test/m2@m1.txt");
		assertEquals(expected, result);
	}

	@Test
	public void mulDD3() {
		Matrix m1 = new DenseMatrix("dense_test/3x3.txt");
		Matrix m2 = new DenseMatrix("dense_test/3x3.txt");
		Matrix result = m2.mul(m1);
		Matrix expected = new DenseMatrix("dense_test/3x3_expected.txt");
		assertEquals(expected, result);
	}

	@Test
	public void equalsSSTest(){
		HashMap<Integer, HashMap<Integer, Double>> data = new HashMap<>();
		data.put(0, new HashMap<Integer, Double>());
		data.get(0).put(0, 2.0);
		data.get(0).put(2, -1.0);
		data.put(1, new HashMap<Integer, Double>());
		data.get(1).put(1, 5.0);
		Matrix m1 = new SparseMatrix(3, 3, data);
		Matrix m2 = new SparseMatrix(3, 3, data);

		assertEquals(m1, m2);
	}

	@Test
	public void loadSparseMatrix(){
		Matrix m = new SparseMatrix("sparse_test/load.txt");
		// System.out.println(((SparseMatrix) m).data.get(0));
		HashMap<Integer, HashMap<Integer, Double>> data = new HashMap<>();
		data.put(1, new HashMap<Integer, Double>());
		data.get(1).put(1, 5.0);
		data.put(2, new HashMap<Integer, Double>());
		data.get(2).put(2, -3.0);
		Matrix expected = new SparseMatrix(3, 3, data);

		assertEquals(expected, m);
	}

	@Test
	public void loadEmptySparseMatrix() {
		Matrix m = new SparseMatrix("dense_test/empty.txt");
		Matrix expected = new SparseMatrix(0, 0, null);
		assertEquals(expected, m);
	}

	@Test
	public void mulSS1() {
		Matrix m1 = new SparseMatrix("sparse_test/4x5.txt");
		Matrix m2 = new SparseMatrix("sparse_test/5x3.txt");
		Matrix result = m1.mul(m2);
		Matrix expected = new SparseMatrix("sparse_test/4x5@5x3.txt");

		assertEquals(expected, result);
	}

	@Test
	public void mulSV() {
		Matrix m = new SparseMatrix("sparse_test/4x5.txt");
		Matrix v = new DenseMatrix("sparse_test/vector.txt");
		Matrix result = m.mul(v);
		Matrix expected = new DenseMatrix("sparse_test/4x5@v.txt");
		assertEquals(expected, result);
	}

	@Test
	public void mulDS1() {
		Matrix m1 = new DenseMatrix("dense_test/2x4.txt");
		Matrix m2 = new SparseMatrix("sparse_test/4x5.txt");
		Matrix result = m1.mul(m2);
		Matrix expected = new SparseMatrix("sparse_test/2x4@4x5.txt");
		assertEquals(expected, result);
	}

	@Test
	public void mulSD1() {
		Matrix m1 = new SparseMatrix("sparse_test/5x3.txt");
		Matrix m2 = new DenseMatrix("dense_test/3x3.txt");
		Matrix result = m1.mul(m2);
		Matrix expected = new SparseMatrix("sparse_test/5x3@3x3.txt");
		assertEquals(expected, result);
	}

	@Test
	public void dmulDD1(){
		Matrix m1 = new DenseMatrix("dense_test/m1.txt");
		Matrix m2 = new DenseMatrix("dense_test/m2.txt");
		Matrix result = m2.dmul(m1);
		Matrix expected = new DenseMatrix("dense_test/m2@m1.txt");
		assertEquals(expected, result);
	}

	@Test
	public void dmulSS1() {
		Matrix m1 = new SparseMatrix("sparse_test/4x5.txt");
		Matrix m2 = new SparseMatrix("sparse_test/5x3.txt");
		Matrix result = m1.dmul(m2);
		Matrix expected = new SparseMatrix("sparse_test/4x5@5x3.txt");

		assertEquals(expected, result);
	}

	@Test
	public void dmulDDSizeTest() {
		DenseMatrix m1 = new DenseMatrix("dense_test/4x3.txt");
		DenseMatrix m2 = new DenseMatrix("dense_test/2x4.txt");
		boolean catched = false;
		try {
			m1.dmul(m2);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Unable to multiply matrices due to wrong sizes")) {
				catched = true;
			}
		}
		if (!catched) { fail("Multiplication with wrong sizes happened"); }
	}

	@Test
	public void dmulSSSizeTest() {
		Matrix m1 = new SparseMatrix("sparse_test/5x3.txt");
		Matrix m2 = new SparseMatrix("sparse_test/4x5.txt");
		boolean catched = false;
		try {
			m1.mul(m2);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Unable to multiply matrices due to wrong sizes")) {
				catched = true;
			}
		}
		if (!catched) { fail("Multiplication with wrong sizes happened"); }
	}

	@Test
	public void perfomance(){
		System.out.println("Starting loading sparse matrices");
		Matrix m1 = new SparseMatrix("m1.txt");
		System.out.println("1 loaded");
		Matrix m2 = new SparseMatrix("m2.txt");
		System.out.println("2 loaded");
		long start = 0;
		start = System.currentTimeMillis();
		SparseMatrix result1 = (SparseMatrix) m1.mul(m2);
		System.out.println("Mul Sparse Matrix time: " +(System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		SparseMatrix result2 = (SparseMatrix) m1.dmul(m2);
		System.out.println("Dmul Sparse Matrix time: " +(System.currentTimeMillis() - start));
		System.out.println(result1.equals(result2));
		System.out.println(result1);
		System.out.println(result2);
	}

	/*
	@Test
	public void mulDD() {
		Matrix m1 = new DenseMatrix("m1.txt");
		Matrix m2 = new DenseMatrix("m2.txt");
		Matrix expected = new DenseMatrix("result.txt");
		assertEquals(expected, m1.mul(m2));
	}
	 */
}
