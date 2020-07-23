package com.clicbrics.consumer.clicworth;

import android.text.TextUtils;
import android.util.Log;

import com.clicbrics.consumer.retrofit.ApiClassGetEstimate;
import com.clicbrics.consumer.retrofit.ApiClient;
import com.clicbrics.consumer.retrofit.ApiInterface;
import com.clicbrics.consumer.retrofit.pojoclass.SearchByName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClicworthViewmodel {
    
    private ClicWorthCallBacksInterface clicWorthCallBacksInterface;

    public ClicworthViewmodel(ClicWorthCallBacksInterface clicWorthCallBacksInterface)
    {
        this.clicWorthCallBacksInterface = clicWorthCallBacksInterface;
    }


    public void GetEstimateApi(String lati, String longi, String propertysize, String propertyTYpe, String propertyAddress, int roomValue, int floorValue) {
        if (!clicWorthCallBacksInterface.isInternetConnected()) {
            return;
        }
        clicWorthCallBacksInterface.showLoader();
        ApiInterface apiService = ApiClassGetEstimate.getClient().create(ApiInterface.class);

        final Call<GetEStimateModel> projectListResponse = apiService.getEstimateClicworth(lati,
                longi,propertysize,propertyTYpe,propertyAddress,roomValue,floorValue);
        Log.i("ClicWorth", "GetEstimate url: " + projectListResponse.request().url());
        projectListResponse.enqueue(new Callback<GetEStimateModel>() {
            @Override
            public void onResponse(Call<GetEStimateModel> call, Response<GetEStimateModel> response) {
                clicWorthCallBacksInterface.hideLoader();

                if(response != null && response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        Log.i("ClicWorth", "onResponse: "+response.body().getStatus());
                        clicWorthCallBacksInterface.onSuccess(response.body());

                    }else if(!TextUtils.isEmpty(response.body().getErrorMessage())){
                        clicWorthCallBacksInterface.onError(response.body().getErrorMessage());
                    }else{
                        clicWorthCallBacksInterface.onError("Error getting property,please retry.");
                    }
                }else{
                    clicWorthCallBacksInterface.onError("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<GetEStimateModel> call, Throwable t) {
                clicWorthCallBacksInterface.hideLoader();
                clicWorthCallBacksInterface.onError(t.toString());
            }
        });
    }


}
