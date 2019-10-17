package edu.spbu.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixTest
{
  /**
   * ожидается 4 таких теста
   */
  @Test
  public void mulDD() {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test(expected=RuntimeException.class)
  public void mulDD2() {
    Matrix m1 = new DenseMatrix("m3.txt");
    Matrix m2 = new DenseMatrix("m1.txt");
    m1.mul(m2);
  }
  @Test
  public void mulDD3() {
    Matrix m4 = new DenseMatrix("m4.txt");
    Matrix m5 = new DenseMatrix("m5.txt");
    Matrix expected = new DenseMatrix("result45.txt");
    assertEquals(expected, m4.mul(m5));
  }
}
