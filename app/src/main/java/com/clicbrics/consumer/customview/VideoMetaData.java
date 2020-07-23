package com.clicbrics.consumer.customview;

/**
 * Created by Alok on 31-10-2018.
 */
public class VideoMetaData{
    float distanceLeft;
    double speed;
    String location;
    float upcomingAreaDistance;
    String upcomingArea;
    String arrowDir;
    int duration;
    float etaTime;
    double latitude;
    double longitude;

    public float getDistanceLeft() {
        return distanceLeft;
    }

    public void setDistanceLeft(float distanceLeft) {
        this.distanceLeft = distanceLeft;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getUpcomingAreaDistance() {
        return upcomingAreaDistance;
    }

    public void setUpcomingAreaDistance(float upcomingAreaDistance) {
        this.upcomingAreaDistance = upcomingAreaDistance;
    }

    public String getUpcomingArea() {
        return upcomingArea;
    }

    public void setUpcomingArea(String upcomingArea) {
        this.upcomingArea = upcomingArea;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getEtaTime() {
        return etaTime;
    }

    public void setEtaTime(float etaTime) {
        this.etaTime = etaTime;
    }

    public String getArrowDir() {
        return arrowDir;
    }

    public void setArrowDir(String arrowDir) {
        this.arrowDir = arrowDir;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return duration + "," + distanceLeft + "," + location +"," + upcomingArea + "," + upcomingAreaDistance + ","
                + speed + "," + arrowDir + ", " + etaTime + "," + latitude + "," + longitude;
    }
}
