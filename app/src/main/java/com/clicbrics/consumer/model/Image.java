package com.clicbrics.consumer.model;


import com.google.gson.annotations.SerializedName;

/**
 * Created by mehar on 15-09-2016.
 */

public class Image {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    @SerializedName("sURL")
    private String sURL;

    public Image() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSURL() {
        return sURL;
    }

    public void setSURL(String sURL) {
        this.sURL = sURL;
    }
}
