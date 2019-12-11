package edu.spbu.matrix;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.*;
import java.util.concurrent.*;

/**
 * Разряженная матрица
 */

public class SparseMatrix implements Matrix {
  public HashMap<Point, Double> val;
  public int height;
  public int width;


  public SparseMatrix(HashMap<Point, Double> val, int height, int width) {
    this.val = val;
    this.height = height;
    this.width = width;
  }

  /**
   * загружает матрицу из файла
   *
   * @param fileName
   */
  public SparseMatrix(String fileName) {
    try {
      FileReader fr = new FileReader(fileName);
      BufferedReader bufR = new BufferedReader(fr);
      val = new HashMap<>();
      String[] Currln;
      String line = bufR.readLine();
      int length = 0, height = 0;
      double element;
      while (line != null) {
        Currln = line.split(" ");
        length = Currln.length;
        for (int i = 0; i < length; i++) {
          if (!Currln[0].isEmpty()) {
            element = Double.parseDouble(Currln[i]);
            if (element != 0) {
              Point coord = new Point(height, i);
              val.put(coord, element);
            }
          }
        }
        height++;
        line = bufR.readLine();
      }
      fr.close();
      this.height = height;
      width = length;

    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  @Override
  public Matrix mul(Matrix o) {
      return UniversalMul.mul(this, o);
  }



  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public Matrix submatrix(int x1, int x2) {
    HashMap<Point, Double> result = new HashMap<>();
    for (Point k : this.val.keySet()){
      if (k.x<x2 && k.x>=x1) {
        result.put(k, this.val.get(k));
      }
    }
    return new SparseMatrix(result, x2-x1, this.getWidth());
  }

  public SparseMatrix transpose() {
    HashMap<Point, Double> transposedSMtx = new HashMap<>();
    Point p;
    for (Point k : val.keySet()) {
      p = new Point(k.y, k.x);
      transposedSMtx.put(p, val.get(k));
    }
    return new SparseMatrix(transposedSMtx, width, height);
  }




  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */

  @Override public Matrix dmul(Matrix o)
  {
    return UniversalMul.dmul(this, o);
  }

  /**
   * спавнивает с обоими вариантами
   *
  // * @param o
   * @return
   */

  @Override
  public String toString() {
    if (val == null) throw new RuntimeException("Встречена пустая матрица");
    StringBuilder resBuilder = new StringBuilder();
    //resBuilder.append('\n');
    for (int i = 0; i < height; i++) {
      resBuilder.append('[');
      for (int j = 0; j < width; j++) {
        Point p = new Point(i, j);
        if (val.containsKey(p)) {
          resBuilder.append(val.get(p));
        } else {
          resBuilder.append(0.0);
        }
        if (j < width - 1)
          resBuilder.append(" ");
      }
      resBuilder.append("]\n");
    }
    return resBuilder.toString();
  }

  //@Override
  public boolean equals(Object o) {
    if(o instanceof DenseMatrix)
    {
      DenseMatrix DMtx=(DenseMatrix) o;
      if (val == null || DMtx.matrix == null) return false;
      if (height == DMtx.height && width == DMtx.width) {
        int nonzeros=0;
        for(int i=0;i<DMtx.height;i++)
        {
          for(int j=0;j<DMtx.width;j++)
          {
            if(DMtx.matrix[i][j]!=0)
            {
              nonzeros++;
            }
          }
        }
        if(nonzeros!=val.size()) return false;
        for (Point k: val.keySet()) {
          if(DMtx.matrix[k.x][k.y]==0)
            return false;
          if (DMtx.matrix[k.x][k.y]!=val.get(k)) {
            return false;
          }
        }
        return true;
      }
    }
    if (o instanceof SparseMatrix) {
      SparseMatrix SMtx = (SparseMatrix) o;
      if (val == null || SMtx.val == null) return false;
      if (SMtx.val == val) return true;
      if (this.hashCode() != SMtx.hashCode()) return false;
      if (height != SMtx.height || width != SMtx.width) return false;
      if (val.size() != SMtx.val.size()) return false;
      for (Point p : val.keySet()) {
        if (val.get(p) - (SMtx.val.get(p)) != 0)
          return false;
      }
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hsh = Objects.hash(height, width);
    for (Point p : val.keySet()) {
      hsh += (val.get(p).hashCode() << 2) + 31;
    }

    return hsh;
  }
}


