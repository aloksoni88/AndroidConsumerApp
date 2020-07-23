package com.clicbrics.consumer.utils;

import com.buy.housing.backend.userEndPoint.model.NotificationInfo;

import java.util.Comparator;

/**
 * Created by Alok on 13-08-2018.
 */
public class NotificationTimeComparator implements Comparator<NotificationInfo>{
    @Override
    public int compare(NotificationInfo o1, NotificationInfo o2) {
        if (o1.getStartTime() > o2.getStartTime()) return -1;
        if (o1.getStartTime() < o2.getStartTime()) return 1;
        return 0;
    }
}
