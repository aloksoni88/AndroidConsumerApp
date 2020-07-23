package com.clicbrics.consumer.utils;

import android.util.Log;

import com.clicbrics.consumer.model.SearchProperty;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Alok on 08-08-2018.
 */
public class PriceComparator implements Comparator<Map.Entry<Long, SearchProperty>> {
    Constants.PriceSort sort;

    public PriceComparator(Constants.PriceSort sort) {
        this.sort = sort;
    }

    @Override
    public int compare(Map.Entry<Long, SearchProperty> lhs, Map.Entry<Long, SearchProperty> rhs) {

        if (sort == Constants.PriceSort.LowToHigh) {
            long leftValue = lhs.getValue().getMinPrice();
            long rightValue = rhs.getValue().getMinPrice();

            Log.d("price","lhs:"+leftValue+" rhs:"+rightValue);

                long delta = (leftValue) - (rightValue);
                if (delta > 0) return 1;
                if (delta < 0) return -1;
                return 0;


        /*    if (leftValue == rightValue) {
                return 0;
            }
            if (leftValue == 0) {
                return 1;
            }
            if (rightValue == 0) {
                return -1;
            }
            else return 0;*/


        } else {
            long delta = (lhs.getValue().getMinPrice()) - (rhs.getValue().getMinPrice());
            if (delta > 0) return -1;
            if (delta < 0) return 1;
            return 0;
        }

    }
}