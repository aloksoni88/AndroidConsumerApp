package com.clicbrics.consumer.helper;

import com.clicbrics.consumer.model.ProjectListResponse;

import retrofit2.Response;

/**
 * Created by Alok on 18-04-2019.
 */
public interface ProjectMapResultCallback {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onSuccessProjectList(Response<ProjectListResponse> projectListResponse);
    void onErrorProjectList(String errMsg);
}
