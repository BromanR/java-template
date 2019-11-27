package edu.spbu.matrix;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

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
        // System.out.println(line);
        Currln = line.split(" ");
        //String intArrayString = Arrays.toString(Currln);
        //System.out.println(intArrayString);
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
    if (o instanceof SparseMatrix) {
      return mul((SparseMatrix) o);
    } else if (o instanceof DenseMatrix) {
      return mul((DenseMatrix) o);
    } else throw new RuntimeException("Ошибка");
  }

  @Override
  public Matrix dmul(Matrix o) {
    return null;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public int getWidth() {
    return 0;
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


  public SparseMatrix mul(SparseMatrix SMtx) {
    if (width == 0 || SMtx.height == 0 || val == null || SMtx.val == null) return null;
    if (width == SMtx.height) {
      HashMap<Point, Double> result = new HashMap<>();
      SparseMatrix tSMtx = SMtx.transpose();
      for (Point k : val.keySet()) {
        for (int i = 0; i < tSMtx.height; i++) {
          Point p1 = new Point(i, k.y);
          if (tSMtx.val.containsKey(p1)) {
            Point p2 = new Point(k.x, i);
            {
              double buf;
              if (result.containsKey(p2)) {
                buf = result.get(p2) + val.get(k) * tSMtx.val.get(p1);
                if (buf == 0) result.remove(p2);
                else result.put(p2, buf);
              } else {
                buf = val.get(k) * tSMtx.val.get(p1);
                result.put(p2, buf);
              }
            }
          }
        }
      }
      return new SparseMatrix(result, height, SMtx.width);
    } else throw new RuntimeException("Размеры матриц не верны");
  }

  public DenseMatrix mul(DenseMatrix DMtx) {
    if (width == DMtx.height && val != null && DMtx.matrix != null) {
      double[][] res = new double[height][DMtx.width];
      DenseMatrix tDMtx = DMtx.transpose();
      for (Point p : val.keySet()) {
        for (int j = 0; j < tDMtx.height; j++) {
          for (int k = 0; k < width; k++) {
            if (p.y == k) {
              res[p.x][j] += val.get(p) * tDMtx.matrix[j][k];
            }
          }
        }
      }
      return new DenseMatrix(res);
    } else throw new RuntimeException("Размеры матриц не отвечают матричному уможению.");
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */


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
    resBuilder.append('\n');
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

  @Override
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


