package com.clicbrics.consumer.utils;

import java.util.Comparator;

/**
 * Created by Alok on 08-08-2018.
 */
public class BedroomComparator implements Comparator<Integer>{
    @Override
    public int compare(Integer lhs, Integer rhs) {
        return lhs - rhs;
    }
}
