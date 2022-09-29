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
		DenseMatrix m = new DenseMatrix("load_test.txt");
		double[][] expected = { {1.0, 2.0, 0}, {3.0, 4.0, 1.0}, {10.0, 8.0, 0} };
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++){
				assertEquals(expected[j][i], m.getElement(i, j), 0);
			}
		}
	}

	@Test
	public void mulIdentityMatrix() {
		DenseMatrix m = new DenseMatrix("load_test.txt");
		double[][] e_data = new double[m.row_count][m.row_count];
		for(int i = 0; i < m.row_count; i++) {
			e_data[i][i] = 1;
		}
		DenseMatrix e = new DenseMatrix(m.row_count, m.row_count, e_data);
		DenseMatrix result = (DenseMatrix) m.mul(e);
		assertEquals(m, result);
	}

	@Test
	public void mulSizeTest() {
		DenseMatrix m1 = new DenseMatrix("2x4_test.txt");
		DenseMatrix m2 = new DenseMatrix("4x3_test.txt");
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
	public void mullDD1() {
		Matrix m1 = new DenseMatrix("dense_mul_test_m1.txt");
		Matrix m2 = new DenseMatrix("dense_mul_test_m2.txt");
		Matrix result = m1.mul(m2);
		Matrix expected = new DenseMatrix("dense_mul_test_m1xm2.txt");
		assertEquals(result, expected);
	}

	@Test
	public void mullDD2() {
		Matrix m1 = new DenseMatrix("dense_mul_test_m1.txt");
		Matrix m2 = new DenseMatrix("dense_mul_test_m2.txt");
		Matrix result = m2.mul(m1);
		Matrix expected = new DenseMatrix("dense_mul_test_m2xm1.txt");
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
