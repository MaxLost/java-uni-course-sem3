package edu.spbu.sort;

import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {

  public static void quickSort(int[] array, int begin, int end) {

      if (begin >= end) {
          return;
      }

      if (end - begin < 3) {
          if (array[begin] > array[end - 1]) {
              int temp = array[begin];
              array[begin] = array[end - 1];
              array[end - 1] = temp;
          }
          return;
      }

      int mid = begin + end / 2;
      int pivot = array[begin] + array[mid] + array[end - 1];

      if (pivot == array[begin]) {
          int temp = array[end - 1];
          array[end - 1] = array[begin];
          array[begin] = temp;
      }
      else if (pivot == array[begin + end / 2]) {
          int temp = array[end - 1];
          array[end - 1] = array[mid];
          array[mid] = temp;
      }

      int edge = begin;
      for (int i = begin; i < end - 1; i++) {
          if (array[i] < pivot) {
              int temp = array[edge];
              array[edge] = array[i];
              array[i] = temp;
              edge++;
          }
      }

      int temp = array[edge];
      array[edge] = array[end - 1];
      array[end - 1] = temp;

      quickSort(array, begin, edge);
      quickSort(array, edge + 1, end);
  }
  public static void sort (int array[]) {
      quickSort(array, 0, array.length);
  }

  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}
