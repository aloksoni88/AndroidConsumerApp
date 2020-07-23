package com.clicbrics.consumer.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;

import com.buy.housing.backend.loanEndPoint.model.SearchCriteria;
import com.clicbrics.consumer.helper.RecentProjectResultCallback;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.retrofit.ApiClient;
import com.clicbrics.consumer.retrofit.ApiInterface;
import com.clicbrics.consumer.retrofit.pojoclass.SearchByName;
import com.clicbrics.consumer.viewmodel.SearchActivityCallbacks;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivityViewModel extends ViewModel {

    private SearchActivityCallbacks searchActivityCallbacks;

    public SearchActivityViewModel(SearchActivityCallbacks searchActivityCallbacks) {
        this.searchActivityCallbacks = searchActivityCallbacks;
    }


    public void GetSearchByName(String searchkeyword, long cityId, String prefix, Long time) {
        if (!searchActivityCallbacks.isInternetConnected()) {
            return;
        }
        searchActivityCallbacks.showLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<SearchByName> projectListResponse = apiService.getSearchByName(searchkeyword,cityId,prefix,time);
        Log.i("projectListResponse", "GetSearchByName: " + projectListResponse.request().url());
        projectListResponse.enqueue(new Callback<SearchByName>() {
            @Override
            public void onResponse(Call<SearchByName> call, Response<SearchByName> response) {
                searchActivityCallbacks.hideLoader();

                if(response != null && response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        Log.i("SearchActivityModel", "onResponse: "+response.body().getStatus());
                        searchActivityCallbacks.onSuccessSearchByName(response,response.body().getSeq());

                    }else if(!TextUtils.isEmpty(response.body().getErrorMessage())){
                        searchActivityCallbacks.onErrorSearchByName(response.body().getErrorMessage());
                    }else{
                        searchActivityCallbacks.onErrorSearchByName("Error getting property,please retry.");
                    }
                }else{
                    searchActivityCallbacks.onErrorSearchByName("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<SearchByName> call, Throwable t) {
                searchActivityCallbacks.hideLoader();
                searchActivityCallbacks.onErrorSearchByName(t.toString());
            }
        });
    }

}
