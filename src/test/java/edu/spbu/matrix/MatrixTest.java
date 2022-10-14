package edu.spbu.matrix;

import org.junit.Test;

import static org.junit.Assert.*;

public class MatrixTest
{
	/**
	 * ожидается 4 таких теста
	 */

	@Test
	public void loadDenseMatrixTest() {
		DenseMatrix m = new DenseMatrix("dense_test/load_test.txt");
		double[][] expected_data = { {1.0, 2.0, 0}, {3.0, 4.0, 1.0}, {10.0, 8.0, 0} };
		DenseMatrix expected = new DenseMatrix(3, 3, expected_data);
		assertEquals(m, expected);
	}

	@Test
	public void loadEmptyMatrix() {
		DenseMatrix m = new DenseMatrix("dense_test/empty.txt");
		DenseMatrix expected = new DenseMatrix(0, 0, new double[0][0]);
		assertEquals(m, expected);
	}

	@Test
	public void denseEqualsTest() {
		double[][] data = { {1, 0, 0}, {0, 1, 0}, {0, 0, 1} };
		DenseMatrix m1 = new DenseMatrix(3, 3, data);
		DenseMatrix m2 = new DenseMatrix(3, 3, data);
		assertEquals(m1, m2);
	}

	@Test
	public void transposeTest() {
		DenseMatrix m1 = new DenseMatrix("dense_test/2x4.txt");
		Matrix result = m1.transpose();
		DenseMatrix expected = new DenseMatrix("dense_test/2x4_transposed.txt");
		assertEquals(result, expected);
	}

	@Test
	public void mulZeroSizedMatrix() {
		DenseMatrix m1 = new DenseMatrix(0, 0, new double[0][0]);
		DenseMatrix m2 = new DenseMatrix(0, 0, new double[0][0]);
		Matrix result = m1.mul(m2);
		DenseMatrix expected = new DenseMatrix(0, 0, new double[0][0]);
		assertEquals(result, expected);
	}

	@Test
	public void mulVector() {
		DenseMatrix m1 = new DenseMatrix("dense_test/2x4.txt");
		DenseMatrix v = new DenseMatrix("dense_test/vector.txt");
		Matrix result = m1.mul(v);
		DenseMatrix expected = new DenseMatrix("dense_test/m@v.txt");
		assertEquals(result, expected);
	}

	@Test
	public void mulSizeTest() {
		DenseMatrix m1 = new DenseMatrix("dense_test/2x4.txt");
		DenseMatrix m2 = new DenseMatrix("dense_test/4x3.txt");
		boolean catched = false;
		try {
			m2.mul(m1);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Unable to multiply matrices due to their sizes")) {
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
		assertEquals(result, expected);
	}

	@Test
	public void mulDD2() {
		Matrix m1 = new DenseMatrix("dense_test/m1.txt");
		Matrix m2 = new DenseMatrix("dense_test/m2.txt");
		Matrix result = m2.mul(m1);
		Matrix expected = new DenseMatrix("dense_test/m2@m1.txt");
		assertEquals(result, expected);
	}

	@Test
	public void mulDD3() {
		Matrix m1 = new DenseMatrix("dense_test/3x3.txt");
		Matrix m2 = new DenseMatrix("dense_test/3x3.txt");
		Matrix result = m2.mul(m1);
		Matrix expected = new DenseMatrix("dense_test/3x3_expected.txt");
		assertEquals(result, expected);
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
