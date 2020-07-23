package com.clicbrics.consumer.helper;

import com.buy.housing.backend.propertyEndPoint.model.PropertySearchListResponse;
import com.clicbrics.consumer.model.ProjectListResponse;

import retrofit2.Response;

/**
 * Created by Alok on 30-07-2018.
 */
public interface RecentProjectResultCallback {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onSuccess(Response<ProjectListResponse> response);
    void onError(String errMsg);
}
