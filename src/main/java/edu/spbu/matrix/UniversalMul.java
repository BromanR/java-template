package edu.spbu.matrix;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

class UniversalMul {
  private static class MultiplierTask implements Callable<Matrix> {
    private final Matrix m1, m2;

    MultiplierTask(Matrix m1, Matrix m2) {
      this.m1 = m1;
      this.m2 = m2;
    }

    public Matrix call() {
      return UniversalMul.mul(m1, m2);
    }
  }

  static Matrix mul(Matrix m1, Matrix m2) {
    if (m1.getWidth() != m2.getHeight()) {
      return null;
    }

    if (m1 instanceof DenseMatrix) {
      if (m2 instanceof DenseMatrix) {
        return mul((DenseMatrix) m1, (DenseMatrix) m2);
      } else if (m2 instanceof SparseMatrix) {
        return mul((DenseMatrix) m1, (SparseMatrix) m2);
      }
    } else if (m1 instanceof SparseMatrix) {
      if (m2 instanceof DenseMatrix) {
        return mul((SparseMatrix) m1, (DenseMatrix) m2);
      } else if (m2 instanceof SparseMatrix) {
        return mul((SparseMatrix) m1, (SparseMatrix) m2);
      }
    }
    return null;
  }

  private static DenseMatrix mul(DenseMatrix m1, DenseMatrix m2) {
    int newHeight = m1.getHeight(), newWidth = m2.getWidth();
    m2 = m2.transpose();

    double[][] out = new double[newHeight][newWidth];
    for (int i = 0; i < newHeight; ++i) {
      for (int j = 0; j < newWidth; ++j) {
        for (int k = 0; k < m1.getWidth(); ++k) {
          out[i][j] += m1.matrix[i][k] * m2.matrix[j][k];
        }
      }
    }
    return new DenseMatrix(newHeight, newWidth, out);
  }

  private static DenseMatrix mul(DenseMatrix m1, SparseMatrix m2) {
    return mul(m2.transpose(), m1.transpose()).transpose();
  }

  private static DenseMatrix mul(SparseMatrix m1, DenseMatrix DMtx) {
    if (m1.width == DMtx.height && m1.val != null && DMtx.matrix != null) {
      int newHeight = m1.getHeight(), newWidth = DMtx.getWidth();
      double[][] res = new double[m1.height][DMtx.width];
      DenseMatrix tDMtx = DMtx.transpose();
      for (Point p : m1.val.keySet()) {
        for (int j = 0; j < tDMtx.height; j++) {
          for (int k = 0; k < m1.width; k++) {
            if (p.y == k) {
              res[p.x][j] += m1.val.get(p) * tDMtx.matrix[j][k];
            }
          }
        }
      }
      return new DenseMatrix(newHeight, newWidth, res);
    } else throw new RuntimeException("Размеры матриц не отвечают матричному уможению.");
  }

  private static SparseMatrix mul(SparseMatrix m1, SparseMatrix m2) {
    if (m1.width == 0 || m2.height == 0 || m1.val == null || m2.val == null) return null;
    if (m1.width == m2.height) {
      HashMap<Point, Double> result = new HashMap<>();
      SparseMatrix tSMtx = m2.transpose();
      for (Point k : m1.val.keySet()) {
        for (int i = 0; i < tSMtx.height; i++) {
          Point p1 = new Point(i, k.y);
          if (tSMtx.val.containsKey(p1)) {
            Point p2 = new Point(k.x, i);
            {
              double buf;
              if (result.containsKey(p2)) {
                buf = result.get(p2) + m1.val.get(k) * tSMtx.val.get(p1);
                if (buf == 0) result.remove(p2);
                else result.put(p2, buf);
              } else {
                buf = m1.val.get(k) * tSMtx.val.get(p1);
                result.put(p2, buf);
              }
            }
          }
        }
      }
      return new SparseMatrix(result, m1.height, m2.width);
    } else throw new RuntimeException("Размеры матриц не верны");
  }

  static Matrix dmul(Matrix m1, Matrix m2) {
    if (m1.getWidth() != m2.getHeight()) {
      return null;
    }

    //final int threadCount = 4;
    int threadCount = Runtime.getRuntime().availableProcessors();
    int parts = threadCount;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    Matrix[] Submatrices = new Matrix[parts];

    int height = m1.getHeight();
    int SubmatrixSize = (int) Math.floor((double) height / parts);
    int residue = height % parts;
    int k = 1; //k = 1 if residue > 0
    int currentLine = 0;

    for (int i = 0; i < parts; ++i) {
      if (residue < 1) {
        k = 0;
      }
      Submatrices[i] = m1.submatrix(currentLine, currentLine + SubmatrixSize + k);
      currentLine += SubmatrixSize + k;
      residue--;
    }

    ArrayList<Future<Matrix>> results = new ArrayList<>(parts * m2.getWidth());
    for (int i = 0; i < parts; ++i) {
      results.add(executor.submit(new MultiplierTask(Submatrices[i], m2)));
    }

    try {
      Matrix current = null;
      for (int j = 0; j < parts; ++j) {
        if (current == null) {
          current = results.get(j).get();
        } else {
          current = MatrixStacker.vstack(current, results.get(j).get());
        }
      }
      executor.shutdown();
      return current;
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    executor.shutdown();
    return null;
  }
}