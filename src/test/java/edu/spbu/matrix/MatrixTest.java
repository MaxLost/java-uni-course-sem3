package edu.spbu.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
				assertEquals(expected[i][j], m.getElement(i, j), 0);
			}
		}
	}

	@Test
	public void mulDD() {
		Matrix m1 = new DenseMatrix("m1.txt");
		Matrix m2 = new DenseMatrix("m2.txt");
		Matrix expected = new DenseMatrix("result.txt");
		assertEquals(expected, m1.mul(m2));
	}
}
