package com.clicbrics.consumer.utils;

import android.util.Log;

import com.clicbrics.consumer.model.SearchProperty;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Alok on 08-08-2018.
 */
public class DistanceFromLocationComparator implements Comparator<Map.Entry<Long, SearchProperty>> {
    @Override
    public int compare(Map.Entry<Long, SearchProperty> p1, Map.Entry<Long, SearchProperty> p2) {
        double delta = (p1.getValue().getDistanceInKM()) - ((p2.getValue().getDistanceInKM()));
        if (delta > 0) return 1;
        if (delta < 0) return -1;
        return 0;


//        float topScore1=p1.getValue().getDistanceInKM();
//        float topScore2=p2.getValue().getDistanceInKM();
//        Log.i("Pic score ",  "p1 = "+topScore1 + " p2=" + topScore2);
//        return (int) (topScore1-topScore2);

    }
}
