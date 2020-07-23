package com.clicbrics.consumer.helper;

import com.clicbrics.consumer.model.ProjectListResponse;

import retrofit2.Response;

/**
 * Created by Alok on 18-04-2019.
 */
public interface ProjectListResultCallback {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onSuccessProjectByCity(Response<ProjectListResponse> projectListResponse);
    void onErrorProjectByCity(String errMsg);
    void showPagingLoader();
    void hidePagingLoader();
    void onSuccessNextProjectListByCity(Response<ProjectListResponse> projectListResponse);
    void onErrorNextProjectListByCity(String errMsg);
}
