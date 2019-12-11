package edu.spbu.matrix;

import java.awt.*;
import java.util.HashMap;

class MatrixStacker {
  static Matrix hstack(Matrix m1, Matrix m2) {
    if (m1.getHeight() != m2.getHeight()) {
      return null;
    }

    if (m1 instanceof DenseMatrix) {
      if (m2 instanceof DenseMatrix) {
        return hstack((DenseMatrix) m1, (DenseMatrix) m2);
      }
    }
    if (m1 instanceof SparseMatrix) {
      if (m2 instanceof SparseMatrix) {
        return hstack((SparseMatrix) m1, (SparseMatrix) m2);
      }
    }
    return null;
  }

  static Matrix vstack(Matrix m1, Matrix m2) {
    if (m1.getWidth() != m2.getWidth()) {
      return null;
    }

    if (m1 instanceof DenseMatrix) {
      if (m2 instanceof DenseMatrix) {
        return vstack((DenseMatrix) m1, (DenseMatrix) m2);
      }
    }
    if (m1 instanceof SparseMatrix) {
      if (m2 instanceof SparseMatrix) {
        return vstack((SparseMatrix) m1, (SparseMatrix) m2);
      }
    }
    return null;
  }

  private static DenseMatrix hstack(DenseMatrix m1, DenseMatrix m2) {
    int newHeight = m1.getHeight(), newWidth = m1.getWidth() + m2.getWidth();
    double[][] out = new double[newHeight][newWidth];

    for (int i = 0; i < newHeight; ++i) {
      System.arraycopy(m1.matrix[i], 0, out[i], 0, m1.matrix[i].length);
      System.arraycopy(m2.matrix[i], 0, out[i], m1.matrix[i].length, m2.matrix[i].length);
    }

    return new DenseMatrix(newHeight, newWidth, out);
  }

  private static SparseMatrix hstack(SparseMatrix m1, SparseMatrix m2) {
    int newHeight = m1.getHeight(), newWidth = m1.getWidth() + m2.getWidth();
    for (Point p: m2.val.keySet()){
      Point k = new Point(p.x , p.y +m2.width);
      m1.val.put(k, m2.val.get(p));
    }


    return new SparseMatrix(m1.val, newHeight, newWidth);
  }

  private static DenseMatrix vstack(DenseMatrix m1, DenseMatrix m2) {
    int newHeight = m1.getHeight() + m2.getHeight(), newWidth = m1.getWidth();

    double[][] out = new double[newHeight][newWidth];
    System.arraycopy(m1.matrix, 0, out, 0, m1.matrix.length);
    System.arraycopy(m2.matrix, 0, out, m1.matrix.length, m2.matrix.length);

    return new DenseMatrix(newHeight, newWidth, out);
  }

  private static SparseMatrix vstack(SparseMatrix m1, SparseMatrix m2) {
    int newHeight = m1.getHeight() + m2.getHeight(), newWidth = m1.getWidth();

    for (Point p: m2.val.keySet()){
      Point k = new Point(p.x, p.y);
      m1.val.put(k, m2.val.get(p));
    }


    SparseMatrix actual =  new SparseMatrix(m1.val, newHeight, newWidth);
   // System.out.println("now:" + ((SparseMatrix)actual).toString());
    return actual;
  }
}