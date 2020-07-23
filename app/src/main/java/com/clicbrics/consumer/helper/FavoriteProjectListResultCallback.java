package com.clicbrics.consumer.helper;

import com.clicbrics.consumer.model.ProjectListResponse;

import retrofit2.Response;

/**
 * Created by Alok on 25-04-2019.
 */
public interface FavoriteProjectListResultCallback {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onSuccessFavoriteProjectList(Response<ProjectListResponse> projectListResponse);
    void onErrorFavoriteProjectList(String errMsg);
}
