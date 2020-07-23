package com.clicbrics.consumer.model;


/**
 * Created by mehar on 15-09-2016.
 */

public class FloorPlan {

    private Long id;
    private String name;
    private String urlImage;
    private String url2d;
    private String url3d;
    private String url360;
    private String urlVideo;
    private String urlKML;
    private String surlImage;
    private String surl2d;
    private String surl3d;

    public FloorPlan() {
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

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrl2d() {
        return url2d;
    }

    public void setUrl2d(String url2d) {
        this.url2d = url2d;
    }

    public String getUrl3d() {
        return url3d;
    }

    public void setUrl3d(String url3d) {
        this.url3d = url3d;
    }

    public String getUrl360() {
        return url360;
    }

    public void setUrl360(String url360) {
        this.url360 = url360;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public String getUrlKML() {
        return urlKML;
    }

    public void setUrlKML(String urlKML) {
        this.urlKML = urlKML;
    }

    public String getSurlImage() {
        return surlImage;
    }

    public void setSurlImage(String surlImage) {
        this.surlImage = surlImage;
    }

    public String getSurl2d() {
        return surl2d;
    }

    public void setSurl2d(String surl2d) {
        this.surl2d = surl2d;
    }

    public String getSurl3d() {
        return surl3d;
    }

    public void setSurl3d(String surl3d) {
        this.surl3d = surl3d;
    }
}
