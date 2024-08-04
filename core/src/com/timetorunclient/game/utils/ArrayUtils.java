package com.timetorunclient.game.utils;

import java.util.ArrayList;
import java.util.Objects;

public class ArrayUtils {
    public static int interceptSorted(ArrayList<Integer> a, ArrayList<Integer> b){
        int i = 0, j = 0;
        if(a == null || b == null){
            return -1;
        }
        while (i < a.size() && j < b.size() && !Objects.equals(a.get(i), b.get(j))){
            while (i < a.size() && a.get(i) < b.get(j)){
                i++;
            }
            while (i < a.size() && j < b.size() && b.get(j) < a.get(i)){
                j++;
            }
        }
        if(i >= a.size() || j >= b.size()) return -1;
        else return i;
    }
}
