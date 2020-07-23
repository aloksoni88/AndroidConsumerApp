package com.clicbrics.consumer.wrapper;

import java.io.Serializable;

/**
 * Created by Alok on 04-02-2017.
 */

public class Notificaiton implements Serializable {

    private String clickUrl;
    private Long id;
    private String text;
    private Long time;
    private Long startTime;
    private Long endTime;
    private String type;
    private Long uid;
    private String url;
    private String welcomeUrl;

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getWelcomeUrl() {
        return welcomeUrl;
    }

    public void setWelcomeUrl(String welcomeUrl) {
        this.welcomeUrl = welcomeUrl;
    }
}
