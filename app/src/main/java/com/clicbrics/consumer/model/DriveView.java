package com.clicbrics.consumer.model;


/**
 * Created by Ranjeet on 12-11-2018.
 */

public class DriveView {

    private Long id;
    private String name;
    private String thumb;
    private String videoURL;
    private String latLongUrl;
    private String nearbyUrl;
    private double startLat;
    private double startLong;
    private double endLat;
    private double endLong;
    private Image projectLogo;
    private String kml;

    public DriveView() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getLatLongUrl() {
        return latLongUrl;
    }

    public void setLatLongUrl(String latLongUrl) {
        this.latLongUrl = latLongUrl;
    }

    public String getNearbyUrl() {
        return nearbyUrl;
    }

    public void setNearbyUrl(String nearbyUrl) {
        this.nearbyUrl = nearbyUrl;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    public Image getProjectLogo() {
        return projectLogo;
    }

    public void setProjectLogo(Image projectLogo) {
        this.projectLogo = projectLogo;
    }

    public String getKml() {
        return kml;
    }

    public void setKml(String kml) {
        this.kml = kml;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
