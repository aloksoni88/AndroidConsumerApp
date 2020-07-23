package com.clicbrics.consumer.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import com.buy.housing.backend.propertyEndPoint.model.FooterLinkListResponse;
import com.buy.housing.backend.propertyEndPoint.model.ProjectCollection;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.helper.BuilderNProjectDataResuleCallback;
import com.clicbrics.consumer.helper.FavoriteProjectListResultCallback;
import com.clicbrics.consumer.helper.PopularProjectCallback;
import com.clicbrics.consumer.helper.ProjectListResultCallback;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.retrofit.ApiClient;
import com.clicbrics.consumer.retrofit.ApiInterface;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alok on 18-04-2019.
 */
public class ProjectListViewModel {
    private ProjectListResultCallback projectListResultCallback;
    private FavoriteProjectListResultCallback favListResultCallback;
    private BuilderNProjectDataResuleCallback builderNProjectResultCallback;
    private PopularProjectCallback popularProjectCallback;


    public ProjectListViewModel(ProjectListResultCallback projectListResultCallback) {
        this.projectListResultCallback = projectListResultCallback;
    }

    public ProjectListViewModel(FavoriteProjectListResultCallback favListResultCallback) {
        this.favListResultCallback = favListResultCallback;
    }

    /*   public ProjectListViewModel(BuilderNProjectDataResuleCallback builderNProjectResultCallback){
           this.builderNProjectResultCallback = builderNProjectResultCallback;
       }*/
    public ProjectListViewModel(PopularProjectCallback popularProjectCallback) {
        this.popularProjectCallback = popularProjectCallback;
    }


    /**
     * Method to get project list by city
     *
     * @param cityId
     * @param offset
     * @param limit
     * @param builderId
     * @param propertyType
     * @param bedType
     * @param projectStatus
     * @param minPrice
     * @param maxPrice
     * @param minArea
     * @param maxArea
     * @param orderBy
     */
    public void getProjectListByCity(String cityId, Integer offset, Integer limit, Long builderId, String propertyType,
                                     String bedType, String projectStatus, Integer minPrice, Integer maxPrice,
                                     Integer minArea, Integer maxArea, String orderBy
            , Long requestId, String appVersion, Long userId,
                                     String userEmail, String userPhone, String userName, Long virtualId,
                                     String leadsource, String browser, String browserVersion,
                                     String browserType, Boolean getSummary) {
        if (!projectListResultCallback.isInternetConnected()) {
            return;
        }
        projectListResultCallback.showLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ProjectListResponse> projectListResponse = apiService.getProjectListByCity(cityId, offset, limit,
                builderId, propertyType, bedType, projectStatus, minPrice, maxPrice, minArea, maxArea, orderBy
                , requestId, appVersion, userId
                , userEmail, userPhone, userName, virtualId, leadsource, browser, browserVersion, browserType, getSummary);
        Log.i("ProjectListViewModel", "getProjectListByCity: " + projectListResponse.request().url());
        projectListResponse.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                projectListResultCallback.hideLoader();
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus()) {
                        projectListResultCallback.onSuccessProjectByCity(response);
                    } else if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                        projectListResultCallback.onErrorProjectByCity(response.body().getErrorMessage());
                    } else {
                        projectListResultCallback.onErrorProjectByCity("Error getting property,please retry.");
                    }
                } else {
                    projectListResultCallback.onErrorProjectByCity("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                projectListResultCallback.hideLoader();
                projectListResultCallback.onErrorProjectByCity(t.toString());
            }
        });
    }


    /**
     * Method to get the project list by city
     * This method is for pagination, while user scroll down and load more project list by city
     *
     * @param cityId
     * @param offset
     * @param limit
     * @param builderId
     * @param propertyType
     * @param bedType
     * @param projectStatus
     * @param minPrice
     * @param maxPrice
     * @param minArea
     * @param maxArea
     * @param orderBy
     */
    public void getNextProjectListByCity(String cityId, Integer offset, Integer limit, Long builderId, String propertyType,
                                         String bedType, String projectStatus, Integer minPrice, Integer maxPrice,
                                         Integer minArea, Integer maxArea, String orderBy
            , Long requestId, String appVersion, Long userId,
                                         String userEmail, String userPhone, String userName, Long virtualId,
                                         String leadsource, String browser, String browserVersion,
                                         String browserType, Boolean getSummary) {
        if (!projectListResultCallback.isInternetConnected()) {
            return;
        }
        projectListResultCallback.showPagingLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ProjectListResponse> projectListResponse = apiService.getProjectListByCity(cityId, offset, limit,
                builderId, propertyType, bedType, projectStatus, minPrice, maxPrice, minArea, maxArea, orderBy
                , requestId, appVersion, userId
                , userEmail, userPhone, userName, virtualId, leadsource, browser, browserVersion, browserType, getSummary);
        projectListResponse.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                projectListResultCallback.hidePagingLoader();
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus()) {
                        projectListResultCallback.onSuccessNextProjectListByCity(response);
                    } else if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                        projectListResultCallback.onErrorNextProjectListByCity(response.body().getErrorMessage());
                    } else {
                        projectListResultCallback.onErrorNextProjectListByCity("Error getting property,please retry.");
                    }
                } else {
                    projectListResultCallback.onErrorNextProjectListByCity("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                projectListResultCallback.hidePagingLoader();
                projectListResultCallback.onErrorNextProjectListByCity(t.toString());
            }
        });
    }


    /**
     * To get the Project list of given offset and limit with given filter
     * This method gives the project list by using latitude and longitude - project by location
     *
     * @param latitude
     * @param longitude
     * @param cityId
     * @param offset
     * @param limit
     * @param propertyType
     * @param bedType
     * @param projectStatus
     * @param minPrice
     * @param maxPrice
     * @param minArea
     * @param maxArea
     * @param orderBy
     * @param requestId
     * @param appVersion
     * @param userId
     * @param userEmail
     * @param userPhone
     * @param userName
     * @param virtualId
     * @param leadsource
     * @param browser
     * @param browserVersion
     * @param browserType
     * @param city
     * @param searchTxt
     */
    public void getProjectListByDistance(double latitude, double longitude, String cityId, Integer offset, Integer limit,
                                         String propertyType, String bedType, String projectStatus, Integer minPrice,
                                         Integer maxPrice, Integer minArea, Integer maxArea, String orderBy, Long requestId, String appVersion, Long userId,
                                         String userEmail, String userPhone, String userName, Long virtualId,
                                         String leadsource, String browser, String browserVersion,
                                         String browserType, String city, String searchTxt) {
        if (!projectListResultCallback.isInternetConnected()) {
            return;
        }
        projectListResultCallback.showLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ProjectListResponse> projectListResponse = apiService.getProjectListByDistance(latitude, longitude, cityId, offset, limit,
                propertyType, bedType, projectStatus, minPrice, maxPrice, minArea, maxArea, orderBy, requestId, appVersion, userId
                , userEmail, userPhone, userName, virtualId, leadsource, browser, browserVersion, browserType, city, searchTxt);
        Log.i("ProjectListViewModel", "getProjectListByDistance: " + projectListResponse.request().url());
        projectListResponse.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                projectListResultCallback.hideLoader();
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus()) {
                        projectListResultCallback.onSuccessProjectByCity(response);
                    } else if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                        projectListResultCallback.onErrorProjectByCity(response.body().getErrorMessage());
                    } else {
                        projectListResultCallback.onErrorProjectByCity("Error getting property,please retry.");
                    }
                } else {
                    projectListResultCallback.onErrorProjectByCity("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                projectListResultCallback.hideLoader();
                projectListResultCallback.onErrorProjectByCity(t.toString());
            }
        });
    }

    /**
     * To get the Project list of given offset and limit with given filter
     * This method is used for pagination, if user scroll down more, so we call this method to get more project list
     * This method gives project list by location
     *
     * @param latitude
     * @param longitude
     * @param cityId
     * @param offset
     * @param limit
     * @param propertyType
     * @param bedType
     * @param projectStatus
     * @param minPrice
     * @param maxPrice
     * @param minArea
     * @param maxArea
     * @param orderBy
     */
    public void getNextProjectListByDistance(double latitude, double longitude, String cityId, Integer offset, Integer limit,
                                             String propertyType, String bedType, String projectStatus, Integer minPrice, Integer maxPrice,
                                             Integer minArea, Integer maxArea, String orderBy
            , Long requestId, String appVersion, Long userId,
                                             String userEmail, String userPhone, String userName, Long virtualId,
                                             String leadsource, String browser, String browserVersion,
                                             String browserType, String city, String searchTxt) {
        if (!projectListResultCallback.isInternetConnected()) {
            return;
        }
        projectListResultCallback.showPagingLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ProjectListResponse> projectListResponse = apiService.getProjectListByDistance(latitude, longitude, cityId, offset, limit,
                propertyType, bedType, projectStatus, minPrice, maxPrice, minArea, maxArea, orderBy, requestId, appVersion, userId
                , userEmail, userPhone, userName, virtualId, leadsource, browser, browserVersion, browserType, city, searchTxt);
        Log.i("ProjectListViewModel", "getProjectListByDistance: " + projectListResponse.request().url());
        projectListResponse.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                projectListResultCallback.hidePagingLoader();
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus()) {
                        projectListResultCallback.onSuccessNextProjectListByCity(response);
                    } else if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                        projectListResultCallback.onErrorNextProjectListByCity(response.body().getErrorMessage());
                    } else {
                        projectListResultCallback.onErrorNextProjectListByCity("Error getting property,please retry.");
                    }
                } else {
                    projectListResultCallback.onErrorNextProjectListByCity("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                projectListResultCallback.hidePagingLoader();
                projectListResultCallback.onErrorNextProjectListByCity(t.toString());
            }
        });
    }

    /**
     * To get the favorite project List by favorite project ids
     *
     * @param projectIdList - favorite project ids
     */
    public void getFavoriteProjectList(List<String> projectIdList) {
        if (!favListResultCallback.isInternetConnected()) {
            return;
        }
        favListResultCallback.showLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ProjectListResponse> favoriteProjects = apiService.getFavoriteProjectList(projectIdList.toArray(new String[projectIdList.size()]));
        favoriteProjects.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                Log.i("getProjectByIds", "Request URL -> " + call.request());
                favListResultCallback.hideLoader();
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus()) {
                        favListResultCallback.onSuccessFavoriteProjectList(response);
                    } else if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                        favListResultCallback.onErrorFavoriteProjectList("No project in wishlist");
                    } else {
                        favListResultCallback.onErrorFavoriteProjectList("No project in wishlist");
                    }
                } else {
                    favListResultCallback.onErrorFavoriteProjectList("No project in wishlist");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                Log.i("getProjectByIds", "Request URL -> " + call.request());
                favListResultCallback.hideLoader();
                favListResultCallback.onErrorFavoriteProjectList("No project in wishlist");
            }
        });
    }


    /**
     * Get Builder & Project List by city ID
     * This data we use for searching - while user search any property, so user can search by developer or by project or by location
     * so by developer and project search we need to have list of all builder and project list, so that's why we call this
     * api in background and store in the device and according to list we show the search by dev and project.
     *
     * @param cityId - city id of selected city
     */
    public void getBuilderAndProjectList(final long cityId) {
        new Thread(new Runnable() {
            String errorMsg = "";

            @Override
            public void run() {
                try {
                    ProjectCollection projectBuilderCollection = EndPointBuilder.getPropertyEndPoint().getProjectBuilderListCityWise(cityId)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (projectBuilderCollection != null) {
                        builderNProjectResultCallback.onSuccessProjectBuilderListAPI(projectBuilderCollection);
                    } else {
                        errorMsg = "Error getting project & builder list,please retry";
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    errorMsg = "You lost internet connectivity,please retry";
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Error getting project & builder list,please retry";
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    builderNProjectResultCallback.onErrorProjectBuilderListAPI(errorMsg);
                }
            }
        }).start();
    }

    public void getTopProjet(final long cityId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    FooterLinkListResponse projectBuilderCollection = EndPointBuilder.getPropertyEndPoint().getFooterLink(cityId)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
//                    Log.i("VIEWMOODEL", "run: ====="+cityId+":::::"+projectBuilderCollection.getTopLocalityList());
                    if (projectBuilderCollection != null && projectBuilderCollection.getStatus()) {
                        popularProjectCallback.onSuccessPopularProject(projectBuilderCollection);
                    } else {
                        popularProjectCallback.onErrorPopularProject(projectBuilderCollection.getErrorMessage());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    popularProjectCallback.onErrorPopularProject("Error getting top locality list,please retry");
                }

            }
        }).start();
    }
}
