package com.clicbrics.consumer.utils;

import android.util.Log;

import com.clicbrics.consumer.model.SearchProperty;

import java.util.Comparator;
import java.util.Map;

public class PriceOnReqestComparator implements Comparator<Map.Entry<Long, SearchProperty>> {

    @Override
    public int compare(Map.Entry<Long, SearchProperty> lhs, Map.Entry<Long, SearchProperty> rhs) {
        long leftValue = lhs.getValue().getMinPrice();
        long rightValue = rhs.getValue().getMinPrice();

        if (leftValue == rightValue) {
            return 0;
        }
        if (leftValue == 0) {
            return 1;
        }
        if (rightValue == 0) {
            return -1;
        } else return 0;


    }

}
