package edu.spbu.matrix;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix {
  public int height, width;
  public double[][] matrix;
  public int hachCode;

  public DenseMatrix(int height, int width, double[][] matrix) {
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


 @Override
 public Matrix mul(Matrix o) {
   return UniversalMul.mul(this, o);
 }


  /**
   * многопоточное умножение матриц
   *
   * @param o - another matrix to multiply by
   * @return resulting matrix of the multiplication
   */
  @Override
  public Matrix dmul(Matrix o) {
    return UniversalMul.dmul(this, o);
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

  //
  public DenseMatrix(double[][] input)
  {
    if (input.length > 0 )
    {
      matrix=input;
      height=input.length;
      width=input[0].length;
    }
  }


  public DenseMatrix transpose()
  {
    double[][] transposedDMtx=new double[width][height];
    for(int i=0;i<width;i++)
    {
      for(int j=0;j<height;j++)
      {
        transposedDMtx[i][j]=matrix[j][i];
      }
    }
    return new DenseMatrix(transposedDMtx);
  }

  @Override public String toString() {
    if(matrix==null) throw new RuntimeException("Встречена пустая матрица");
    StringBuilder resBuilder=new StringBuilder();
    resBuilder.append('\n');
    for(int i=0;i<height;i++) {
      resBuilder.append('[');
      for (int j = 0; j < width; j++) {
        resBuilder.append(matrix[i][j]);
        if (j < width - 1)
          resBuilder.append(" ");
      }
      resBuilder.append("]\n");

    }
    return resBuilder.toString();
  }

  public DenseMatrix submatrix(int x1, int x2) {
    double[][] out = new double[x2 - x1][];
    if (x2>this.getHeight()) { x2=this.getHeight();}
    for (int i = 0; i < x2 - x1; ++i) {
      out[i] = Arrays.copyOfRange(this.matrix[i + x1], 0, this.getWidth());
    }

    return new DenseMatrix(x2 - x1, this.getWidth(), out);
  }

}