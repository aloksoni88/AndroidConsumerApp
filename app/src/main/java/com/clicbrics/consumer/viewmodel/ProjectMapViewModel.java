package com.clicbrics.consumer.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import com.clicbrics.consumer.helper.ProjectMapResultCallback;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.retrofit.ApiClient;
import com.clicbrics.consumer.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alok on 26-04-2019.
 */
public class ProjectMapViewModel {
    private ProjectMapResultCallback projectMapResultCallback;

    public ProjectMapViewModel(ProjectMapResultCallback projectMapResultCallback) {
        this.projectMapResultCallback = projectMapResultCallback;
    }

    public void getProjectListByCity(String cityId,Integer offset, Integer limit, Long builderId, String propertyType,
                                     String bedType, String projectStatus, Integer minPrice, Integer maxPrice,
                                     Integer minArea, Integer maxArea, String orderBy
            , Long requestId, String appVersion, Long userId,
                                     String userEmail, String userPhone, String userName, Long virtualId,
                                     String leadsource, String browser, String browserVersion,
                                     String browserType, Boolean getSummary){
        if(!projectMapResultCallback.isInternetConnected()){
            return;
        }
        projectMapResultCallback.showLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ProjectListResponse> projectListResponse = apiService.getProjectListByCity(cityId,offset,limit,
                builderId,propertyType,bedType,projectStatus,minPrice,maxPrice,minArea,maxArea,orderBy
                , requestId, appVersion, userId
                , userEmail, userPhone, userName, virtualId, leadsource, browser, browserVersion, browserType, getSummary);
        Log.i("ProjectMapViewModel", "getProjectListByCity: " + projectListResponse.request().url());

        projectListResponse.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                projectMapResultCallback.hideLoader();
                if(response != null && response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        projectMapResultCallback.onSuccessProjectList(response);
                    }else if(!TextUtils.isEmpty(response.body().getErrorMessage())){
                        projectMapResultCallback.onErrorProjectList(response.body().getErrorMessage());
                    }else{
                        projectMapResultCallback.onErrorProjectList("Error getting property,please retry.");
                    }
                }else{
                    projectMapResultCallback.onErrorProjectList("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                projectMapResultCallback.hideLoader();
                projectMapResultCallback.onErrorProjectList(t.toString());
            }
        });
    }


    public void getProjectListByDistance(double latitude, double longitude, String cityId,Integer offset, Integer limit,
                                         String propertyType, String bedType, String projectStatus, Integer minPrice,
                                         Integer maxPrice, Integer minArea, Integer maxArea, String orderBy
            , Long requestId, String appVersion, Long userId,
                                         String userEmail, String userPhone, String userName, Long virtualId,
                                         String leadsource, String browser, String browserVersion,
                                         String browserType, String city, String searchTxt){
        if(!projectMapResultCallback.isInternetConnected()){
            return;
        }
        projectMapResultCallback.showLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ProjectListResponse> projectListResponse = apiService.getProjectListByDistance(latitude,longitude,cityId,offset,limit,
                propertyType,bedType,projectStatus,minPrice,maxPrice,minArea,maxArea,orderBy, requestId, appVersion, userId
                , userEmail, userPhone, userName, virtualId, leadsource, browser, browserVersion, browserType, city, searchTxt);

        Log.i("ProjectMapViewModel", "getProjectListByCity: " + projectListResponse.request().url());
        projectListResponse.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                projectMapResultCallback.hideLoader();
                if(response != null && response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        projectMapResultCallback.onSuccessProjectList(response);
                    }else if(!TextUtils.isEmpty(response.body().getErrorMessage())){
                        projectMapResultCallback.onErrorProjectList(response.body().getErrorMessage());
                    }else{
                        projectMapResultCallback.onErrorProjectList("Error getting property,please retry.");
                    }
                }else{
                    projectMapResultCallback.onErrorProjectList("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                projectMapResultCallback.hideLoader();
                projectMapResultCallback.onErrorProjectList(t.toString());
            }
        });
    }
}
