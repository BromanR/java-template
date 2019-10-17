package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
    public static void QuickSort(int[] array, int low, int high) {
        if (array.length == 0)
            return;

        if (low >= high)
            return;

        int middle = low + (high - low) / 2;
        int opora = array[middle];

        int i = low;
        int j = high;
        while (i <= j) {
            while (array[i] < opora) i++;

            while (array[j] > opora) j--;

            if (i<=j) {
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                ++i;
                --j;
            }
        }
        if (low < j)
            QuickSort(array, low, j);

        if (high > i)
            QuickSort(array, i, high);
    }

    public static void sort (int[] array){
        QuickSort(array, 0, array.length-1);
    }

    public static void sort(List<Integer> list) {
        Collections.sort(list);
    }
}