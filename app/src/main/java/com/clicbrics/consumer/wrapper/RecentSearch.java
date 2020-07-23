package com.clicbrics.consumer.wrapper;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by root on 10/1/17.
 */
public class RecentSearch{
    public String primaryText;
    public String secondaryText;
    public LatLng latLng;
    public long timestamp;
    public String searchType;
    public long projectId;
    public long builderId;
    public String builderName;
    public long cityId;
    public String cityName;
    public String state;

    public RecentSearch( String primaryText, String secondaryText, LatLng latLng, long timestamp,
                         String searchType,long projectId, long builderId, String builderName, long cityId,
                         String cityName){
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
        this.latLng = latLng;
        this.timestamp = timestamp;
        this.searchType = searchType;
        this.projectId = projectId;
        this.builderId = builderId;
        this.builderName = builderName;
        this.cityId = cityId;
        this.cityName = cityName;
    }
}