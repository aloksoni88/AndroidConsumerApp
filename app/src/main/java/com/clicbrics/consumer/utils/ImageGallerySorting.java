package com.clicbrics.consumer.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Alok on 21-03-2017.
 */

public class ImageGallerySorting implements Comparator<String> {

    private ArrayList order = new ArrayList();

    public ImageGallerySorting()
    {
        List<String> sortingOrder = new ArrayList<>();
        sortingOrder.add("OVERVIEW");
        sortingOrder.add("VIDEO");
        sortingOrder.add("SITE PLAN");
        sortingOrder.add("Cluster Plan");
        sortingOrder.add("Layout Plan");
        sortingOrder.add("Amenities");
        sortingOrder.add("Nearby");
        sortingOrder.add("Construction Updates");
        sortingOrder.add("Other Images");

        /*if(desiredOrder == null){
            throw new IllegalArgumentException("desiredOrder cannot be null.");
        }*/
        for (String string : sortingOrder) {
            this.order.add(string.toLowerCase());
        }
    }

    @Override
    public int compare(String s1, String s2)
    {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        //get index for 1
        int indexOfS1 = order.indexOf(s1);
        if(indexOfS1 == -1){
            indexOfS1 = order.size();
        }

        //get index for 2
        int indexOfS2 = order.indexOf(s2);
        if(indexOfS2 == -1){
            indexOfS2 = order.size();
        }

        if(indexOfS1 == order.size() && indexOfS2 == order.size())
        {
            return s1.compareTo(s2);
        }else{
            return indexOfS1 - indexOfS2;
        }
    }

}
