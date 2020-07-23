package com.clicbrics.consumer.clicworth;

import com.clicbrics.consumer.model.ProjectListResponse;

import retrofit2.Response;

interface EstimateCallback {
    public void sendSuccess(String notify);
    public void sendError();
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
