package com.clicbrics.consumer.helper;

import com.buy.housing.backend.propertyEndPoint.model.ProjectCollection;
import com.buy.housing.backend.propertyEndPoint.model.PropertySearchResponse;
import com.buy.housing.backend.propertyEndPoint.model.SearchProperty;

import java.util.List;

/**
 * Created by Alok on 01-08-2018.
 */
public interface SearchPropertyResultCallback {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onSuccessSearchProeprtyByCity(List<SearchProperty> searchPropertyList);
    void onSuccess(PropertySearchResponse response);
    void onError(String errMsg);
    void onSuccessProjectBuilderListAPI(ProjectCollection projectCollection);
    void onErrorProjectBuilderListAPI(String errMsg);
}
