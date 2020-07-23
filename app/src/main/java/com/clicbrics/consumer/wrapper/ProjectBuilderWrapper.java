package com.clicbrics.consumer.wrapper;

/**
 * Created by Alok on 17-05-2017.
 */

public class ProjectBuilderWrapper {

    public String projectName;
    public String address;
    public long builderId;
    public String builderName;
    public long projectId;
    public long cityId;
    public String cityName;
    public String state;

    @Override
    public String toString() {
        return "ProjectName: " + projectName + ", " + "CityName: " + cityName + ", " + "Builder Name: " + builderName;
    }
}
