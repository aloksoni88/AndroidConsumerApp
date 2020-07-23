package com.clicbrics.consumer.utils;

/**
 * Created by Mehar on 21-12-2015.
 */

public class CityConfig {
    private Long id;

    private String welcomeUrl;
    private boolean welcomeUrlContentChange;
    private boolean forceUpdateApp;
    private String minAppVersion;
    private Long welcomeExpiry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWelcomeUrl() {
        return welcomeUrl;
    }

    public void setWelcomeUrl(String welcomeUrl) {
        this.welcomeUrl = welcomeUrl;
    }

    public boolean isWelcomeUrlContentChange() {
        return welcomeUrlContentChange;
    }

    public void setWelcomeUrlContentChange(boolean welcomeUrlContentChange) {
        this.welcomeUrlContentChange = welcomeUrlContentChange;
    }

    public boolean isForceUpdateApp() {
        return forceUpdateApp;
    }

    public void setForceUpdateApp(boolean forceUpdateApp) {
        this.forceUpdateApp = forceUpdateApp;
    }

    public String getMinAppVersion() {
        return minAppVersion;
    }

    public void setMinAppVersion(String minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    public Long getWelcomeExpiry() {
        return welcomeExpiry;
    }

    public void setWelcomeExpiry(Long welcomeExpiry) {
        this.welcomeExpiry = welcomeExpiry;
    }
}
