package com.clicbrics.consumer.wrapper;

/**
 * Created by Paras on 16-11-2016.
 */

public class NotificationWrapper
{
    private Long startTime;

    private Long id;

    private String welcomeUrl;

    private String minVersion;

    private Long endTime;

    private String appType;

    public Long getStartTime ()
    {
        return startTime;
    }

    public void setStartTime (Long startTime)
    {
        this.startTime = startTime;
    }

    public Long getId ()
    {
        return id;
    }

    public void setId (Long id)
    {
        this.id = id;
    }

    public String getWelcomeUrl ()
    {
        return welcomeUrl;
    }

    public void setWelcomeUrl (String welcomeUrl)
    {
        this.welcomeUrl = welcomeUrl;
    }

    public String getMinVersion ()
    {
        return minVersion;
    }

    public void setMinVersion (String minVersion)
    {
        this.minVersion = minVersion;
    }

    public Long getEndTime ()
    {
        return endTime;
    }

    public void setEndTime (Long endTime)
    {
        this.endTime = endTime;
    }

    public String getAppType ()
    {
        return appType;
    }

    public void setAppType (String appType)
    {
        this.appType = appType;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [startTime = "+startTime+", id = "+id+", welcomeUrl = "+welcomeUrl+", minVersion = "+minVersion+", endTime = "+endTime+", appType = "+appType+"]";
    }
}

