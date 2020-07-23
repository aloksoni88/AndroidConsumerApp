package com.clicbrics.consumer.utils;

import android.util.Log;

import com.clicbrics.consumer.model.SearchProperty;

import java.util.Comparator;
import java.util.Map;

public class RelevanceComparator implements Comparator<Map.Entry<Long, SearchProperty>> {
    @Override
    public int compare(Map.Entry<Long, SearchProperty> p1, Map.Entry<Long, SearchProperty> p2) {

        int topScore1=p1.getValue().getOurPicksScore();
        int topScore2=p2.getValue().getOurPicksScore();
        Log.i("Pic score ",  "p1 = "+topScore1 + " p2=" + topScore2);
            return topScore2-topScore1;
//
//        double delta = (p1.getValue().getOurPicksScore()) - ((p2.getValue().getOurPicksScore()));
//        if (delta < 0) return 1;
//        if (delta > 0) return -1;
//        return 0;



    }
}


