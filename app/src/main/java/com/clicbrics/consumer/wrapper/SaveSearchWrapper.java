package com.clicbrics.consumer.wrapper;

import com.buy.housing.backend.userEndPoint.model.LatLong;

import java.util.List;

/**
 * Created by root on 27/1/17.
 */
public class SaveSearchWrapper {
    public long id;
    public String name;
    public boolean filterApplied;
    public List<String> propertyTypeEnum;

    public List<Integer> bedList;

    public List<String> propertyStatusList;
    public boolean isSelected = false;

    public long maxCost;
    public long minCost;


    public List<LatLong> latLongList;
    public double latitude;
    public double longitude;

    public Long time;
    public Float zoomLevel;

    public Long cityId;
    public String cityName;

    public Long builderId;
    public String builderName;
    public String address;

}
