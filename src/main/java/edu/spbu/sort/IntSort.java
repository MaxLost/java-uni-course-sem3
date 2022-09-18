package edu.spbu.sort;

import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {

    public static int avg(int[] array){

        assert(array.length == 3);
        for (int i = 0; i < array.length; i++){
            for (int j = 0; j < array.length; j++) {
                if (array[i] > array[j]) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
        return array[array.length / 2];
    }

    public static void quickSort(int[] array, int begin, int end) {

        if (begin < end) {

            if (begin == end - 1) {
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

            int mid = begin + (end - begin) / 2;
            int[] pivot_candidates = {array[begin], array[mid], array[end - 1]};
            int pivot = avg(pivot_candidates);

            if (pivot == array[begin]) {
                int temp = array[end - 1];
                array[end - 1] = array[begin];
                array[begin] = temp;
            }
            else if (pivot == array[mid]) {
                int temp = array[end - 1];
                array[end - 1] = array[mid];
                array[mid] = temp;
            }

            int edge = begin;
            for (int i = begin; i < (end - 1); i++) {
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
    }

    public static void sort (int array[]) {
        quickSort(array, 0, array.length);
    }

    public static void sort (List<Integer> list) {
        Collections.sort(list);
    }
}
