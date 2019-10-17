package edu.spbu.matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix {
  private int height, width;
  private double[][] matrix;
  private int hachCode;

  private DenseMatrix(int height, int width, double[][] matrix) {
    this.height = height;
    this.width = width;
    this.matrix = matrix;
    this.hachCode = Arrays.deepHashCode(this.matrix);
  }

  /**
   * загружает матрицу из файла
   *
   * @param fileName - path to the text file with matrix
   */
  public DenseMatrix(String fileName) {
    this.width = 0;
    this.height = 0;
    LinkedList<double[]> rows = new LinkedList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line = br.readLine();
      double[] matrixRow = Arrays.stream(line.split(" ")).mapToDouble(Double::parseDouble).toArray();
      this.width = matrixRow.length;
      this.height = 1;
      rows.add(matrixRow);

      while ((line = br.readLine()) != null) {
        matrixRow = Arrays.stream(line.split(" ")).mapToDouble(Double::parseDouble).toArray();

        rows.add(matrixRow);
        ++this.height;
      }

      this.matrix = new double[this.height][this.width];
      for (int i = 0; i < this.height; ++i) {
        this.matrix[i] = rows.get(i);
      }
      this.hachCode = Arrays.deepHashCode(this.matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * однопоточное умнджение матриц
   */
  @Override
  public Matrix mul(Matrix other) {
    if (other instanceof DenseMatrix && this.getWidth() == other.getHeight()) {
      DenseMatrix dm = (DenseMatrix) other;
      int newHeight = this.height, newWidth = dm.width;
      double[][] matrix = new double[newHeight][newWidth];


      matrix = new double[newHeight][newWidth];
      for (int i = 0; i < newHeight; ++i) {
        for (int j = 0; j < newWidth; ++j) {
          for (int k = 0; k < this.width; ++k) {
            matrix[i][j] += this.matrix[i][k] * dm.matrix[k][j];
          }
        }
      }
      return new DenseMatrix(newHeight, newWidth, matrix);
    }
    throw new IllegalArgumentException(String.format("Can't multiply matrices of size (%n, %n) and (%n, %n)", this.width, this.height, other.getHeight(), other.getWidth()));
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o - another matrix to multiply by
   * @return resulting matrix of the multiplication
   */
  @Override
  public Matrix dmul(Matrix o) {
    return null;
  }

  /**
   * спавнивает с обоими вариантами
   *
   * @param o - an object to compare against
   * @return true if equals
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof DenseMatrix) {
      DenseMatrix dm = (DenseMatrix) o;
      if (this.getHeight() != dm.getHeight() || this.getWidth() != dm.getWidth()) {
        return false;
      }

      if (this.hachCode != dm.hachCode) {
        return false;
      }

      for (int i = 0; i < this.getHeight(); ++i) {
        for (int j = 0; j < this.getWidth(); ++j) {
          if (this.matrix[i][j] != dm.matrix[i][j]) {
            return false;
          }
        }
      }
      return true;
    }

    if (o instanceof SparseMatrix) {
      return false;
    }

    return false;
  }

  @Override
  public double get(int i, int j) {
    if (i < this.getHeight() && j < this.getWidth()) {
      return this.matrix[i][j];
    }
    return 0;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public int getWidth() {
    return this.width;
  }
}