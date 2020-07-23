package com.clicbrics.consumer.viewmodel;

import com.clicbrics.consumer.retrofit.pojoclass.SearchByName;

import retrofit2.Response;

public interface SearchActivityCallbacks {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onErrorSearchByName(String s);
    void onSuccessSearchByName(Response<SearchByName> response, String seq);
//    void onSuccess(Response<ProjectListResponse> response);
}
