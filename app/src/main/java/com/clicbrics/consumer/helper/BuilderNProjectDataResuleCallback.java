package com.clicbrics.consumer.helper;

import com.buy.housing.backend.propertyEndPoint.model.ProjectCollection;

/**
 * Created by Alok on 03-05-2019.
 */
public interface BuilderNProjectDataResuleCallback {
    boolean isInternetConnected();
    void onSuccessProjectBuilderListAPI(ProjectCollection projectCollection);
    void onErrorProjectBuilderListAPI(String errorMsg);
}
