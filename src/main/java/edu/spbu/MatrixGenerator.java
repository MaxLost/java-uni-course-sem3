package edu.spbu;

import edu.spbu.matrix.*;
//import edu.spbu.matrix.DenseMatrix;
//import edu.spbu.matrix.Matrix;
//import edu.spbu.matrix.SparseMatrix;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class MatrixGenerator
{
  public static final int SEED1 = 1;
  public static final int SEED2 = 2;
  public static final int EMPTY_ROW_FRACTION = 10;

  public static final String MATRIX1_NAME = "m1.txt";
  public static final String MATRIX2_NAME = "m2.txt";
  public static final int SIZE = 100;

  private final int emptyRowFraction;
  private final int size;
  private final String emptyRow;
  private final Random rnd;
  private final String file;

  public MatrixGenerator(int seed, int emptyRowFraction, String file, int size)
  {
    this.emptyRowFraction = emptyRowFraction;
    this.size = size;
    this.file = file;
    rnd = new Random(seed);
    emptyRow = Collections.nCopies(size, "0").stream().collect(Collectors.joining(" "));
  }

  public static void main(String args[])
  {
    try
    {
      new MatrixGenerator(SEED1, EMPTY_ROW_FRACTION, MATRIX1_NAME, SIZE).generate();
      new MatrixGenerator(SEED2, EMPTY_ROW_FRACTION, MATRIX2_NAME, SIZE).generate();
      testPerformance();
    }
    catch (IOException e)
    {
      System.out.println("Fail to generate matrix file: " + e);
    }
  }

  private static void testPerformance()
  {
    // Uncomment the code to Test your library
    //*
//    System.out.println("Starting loading dense matrices");
//    Matrix m1 = new DenseMatrix(MATRIX1_NAME);
//    System.out.println("1 loaded");
//    Matrix m2 = new DenseMatrix(MATRIX2_NAME);
//    System.out.println("2 loaded");
//    long start = System.currentTimeMillis();
//    m1.mul(m2);
//    System.out.println("Dense Matrix time: " +(System.currentTimeMillis() - start));

    System.out.println("Starting loading sparse matrices");
    Matrix m1 = new SparseMatrix(MATRIX1_NAME);
    System.out.println("1 loaded");
    Matrix m2 = new SparseMatrix(MATRIX2_NAME);
    System.out.println("2 loaded");
    long start = 0;
    start = System.currentTimeMillis();
    Matrix result1 = m1.mul(m2);
    System.out.println("Mul Sparse Matrix time: " +(System.currentTimeMillis() - start));
    start = System.currentTimeMillis();
    Matrix result2 = m1.dmul(m2);
    System.out.println("Dmul Sparse Matrix time: " +(System.currentTimeMillis() - start));
    System.out.println(result1.equals(result2));
    System.out.println(result1);
    System.out.println(result2);

    //*/
  }

  public void generate() throws IOException
  {
    PrintWriter out = new PrintWriter(new FileWriter(file));
    for (int i = 0; i < size; i++)
    {
      // only 1/emptyRowFraction will have non 0 values
      if (rnd.nextInt(emptyRowFraction) == 0)
        out.println(generateRow());
      else
        out.println(emptyRow);
    }
    out.close();
  }

  private String generateRow()
  {
    return rnd.ints(0, emptyRowFraction).limit(size).mapToObj(r -> (r == 0) ? "" + rnd.nextInt(10000) : "0")
            .collect(Collectors.joining(" "));
  }

}
