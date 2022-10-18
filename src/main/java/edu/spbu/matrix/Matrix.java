package edu.spbu.matrix;

/**
 *
 */
public interface Matrix
{

  double EPSILON = 10e-6;

  double getElement(int x, int y);

  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   * @param o
   * @return
   */
  Matrix mul(Matrix o);

  /**
   * многопоточное умножение матриц
   * @param o
   * @return
   */
  Matrix dmul(Matrix o);

}
