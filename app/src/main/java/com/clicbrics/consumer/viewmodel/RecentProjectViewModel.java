package com.clicbrics.consumer.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.buy.housing.backend.propertyEndPoint.PropertyEndPoint;
import com.buy.housing.backend.propertyEndPoint.model.PropertySearchListResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.helper.RecentProjectResultCallback;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.model.RecentProject;
import com.clicbrics.consumer.model.SearchProperty;
import com.clicbrics.consumer.retrofit.ApiClient;
import com.clicbrics.consumer.retrofit.ApiInterface;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alok on 30-07-2018.
 */
public class RecentProjectViewModel  extends ViewModel{

    private RecentProjectResultCallback resultCallback;

    public RecentProjectViewModel(RecentProjectResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    public void getRecentProjectList(List<String> projectIdList){
        if(!resultCallback.isInternetConnected()){
            return;
        }
        resultCallback.showLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ProjectListResponse> recentProjects = apiService.getFavoriteProjectList(projectIdList.toArray(new String[projectIdList.size()]));
        recentProjects.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                Log.i("getProjectByIds", "Request URL -> " + call.request());
                resultCallback.hideLoader();
                if(response != null && response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        resultCallback.onSuccess(response);
                    }else if(!TextUtils.isEmpty(response.body().getErrorMessage())){
                        resultCallback.onError("Recent project not found");
                    }else{
                        resultCallback.onError("Recent project not found");
                    }
                }else{
                    resultCallback.onError("Recent project not found");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                Log.i("getProjectByIds", "Request URL -> " + call.request());
                resultCallback.hideLoader();
                resultCallback.onError("Recent project not found");
            }
        });
    }

}
