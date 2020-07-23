package com.clicbrics.consumer.clicworth;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.buy.housing.backend.estimateEndPoint.EstimateEndPoint;
import com.buy.housing.backend.estimateEndPoint.model.CommonResponse;
import com.buy.housing.backend.loanEndPoint.LoanEndPoint;
import com.buy.housing.backend.loanEndPoint.model.LoanLead;
import com.buy.housing.backend.userEndPoint.model.UserProfileResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.helper.APIHelperResultCallback;
import com.clicbrics.consumer.helper.ProjectListResultCallback;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.retrofit.ApiClient;
import com.clicbrics.consumer.retrofit.ApiInterface;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstimatedPropertyViewModel extends ViewModel {

    private EstimatedPropertyModel estimatedPropertyModel;
    private EstimateCallback callback;

    public EstimatedPropertyViewModel(EstimateCallback callback,EstimatedPropertyModel estimatedPropertymodel) {
        this.estimatedPropertyModel = estimatedPropertymodel;
        this.callback = callback;

    }

    public EstimatedPropertyModel getEstimatedPropertyModel() {
        return estimatedPropertyModel;
    }

    public void setEstimatedPropertyModel(EstimatedPropertyModel estimatedPropertyModel) {
        this.estimatedPropertyModel = estimatedPropertyModel;
    }


    public String getPropertyAddress() {
        return estimatedPropertyModel.getPropertyAddress();
    }

    public void setPropertyAddress(String propertyAddress) {
        estimatedPropertyModel.setPropertyAddress(propertyAddress);
    }

    public String getPropertyPrice() {
        return estimatedPropertyModel.getPropertyPrice();
    }

    public void setPropertyPrice(String propertyPrice) {
        estimatedPropertyModel.setPropertyPrice(propertyPrice);
    }

    public String getPropertyType() {
        return estimatedPropertyModel.getPropertyType();
    }

    public void setPropertyType(String propertyType) {
        estimatedPropertyModel.setPropertyType(propertyType);
    }

    public String getPropertyPriceRange() {
        return estimatedPropertyModel.getPropertyPriceRange();
    }

    public void setPropertyPriceRange(String propertyPriceRange) {
        estimatedPropertyModel.setPropertyPriceRange(propertyPriceRange);
    }

    public String getPropertyBsp() {
        return estimatedPropertyModel.getPropertyBsp();
    }

    public void setPropertyBsp(String propertyBsp) {
        estimatedPropertyModel.setPropertyBsp(propertyBsp);
    }

    public String getEstimatedAccuracy() {
        return estimatedPropertyModel.getEstimatedAccuracy();
    }

    public void setEstimatedAccuracy(String estimatedAccuracy) {
        estimatedPropertyModel.setEstimatedAccuracy(estimatedAccuracy);
    }

    public String getNoOfRoom() {
        return estimatedPropertyModel.getNoOfRoom();
    }

    public void setNoOfRoom(String noOfRoom) {
        estimatedPropertyModel.setNoOfRoom(noOfRoom);
    }

    public String getSelectedFloor() {
        return estimatedPropertyModel.getSelectedFloor();
    }

    public void setSelectedFloor(String selectedFloor) {
        estimatedPropertyModel.setSelectedFloor(selectedFloor);
    }

    public String getPropertySize() {
        return estimatedPropertyModel.getPropertySize();
    }

    public void setPropertySize(String propertySize) {
        estimatedPropertyModel.setPropertySize(propertySize);
    }


    public String getEstimatePropertyType() {
        return estimatedPropertyModel.getEstimatePropertyType();
    }

    public void setEstimatePropertyType(String estimatePropertyType) {
        estimatedPropertyModel.setEstimatePropertyType(estimatePropertyType);
    }


    public int viewVisiblity() {
        if (estimatedPropertyModel.getEstimatedAccuracy().equalsIgnoreCase("0.0%")) {
            return 0;
        }
        return 1;
    }

    public void sendFeedBack(final String feedback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                EstimateEndPoint estimateEndPoint = EndPointBuilder.getEstimateEndPoint();
                try {
                    CommonResponse commonResponse;
                    if(estimatedPropertyModel.getPropertyType().equalsIgnoreCase("Land")||
                            estimatedPropertyModel.getPropertyType().equalsIgnoreCase("Plot")){
                         commonResponse = estimateEndPoint.estimationFeedback(estimatedPropertyModel.getLatitude(),
                                estimatedPropertyModel.getLongitude(), estimatedPropertyModel.getSizeValue(),
                                estimatedPropertyModel.getPropertyType(), feedback)
                                .setAddress(estimatedPropertyModel.getPropertyAddress()).setBsp(estimatedPropertyModel.getBspValue())
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    }else if(estimatedPropertyModel.getPropertyType().equalsIgnoreCase("Villa")){

                         commonResponse = estimateEndPoint.estimationFeedback(estimatedPropertyModel.getLatitude(),
                                estimatedPropertyModel.getLongitude(),estimatedPropertyModel.getSizeValue(),
                                estimatedPropertyModel.getPropertyType(),feedback)
                                .setAddress(estimatedPropertyModel.getPropertyAddress()).setBsp(estimatedPropertyModel.getBspValue()).
                                        setBed(estimatedPropertyModel.getBed())
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    }else {
                         commonResponse = estimateEndPoint.estimationFeedback(estimatedPropertyModel.getLatitude(),
                                estimatedPropertyModel.getLongitude(), estimatedPropertyModel.getSizeValue(),
                                estimatedPropertyModel.getPropertyType(), feedback)
                                .setAddress(estimatedPropertyModel.getPropertyAddress()).setBsp(estimatedPropertyModel.getBspValue()).
                                        setBed(estimatedPropertyModel.getBed()).setFloor(estimatedPropertyModel.getFloor())
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    }


                    if(commonResponse!=null&&commonResponse.getStatus()){
                        callback.sendSuccess("Thanks for your valuable feedback");
                    }else {
                        callback.sendError();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    callback.sendError();
                }

            }
        }).start();
    }


    public void NotifyPriceChange(final long userId){

        new Thread(new Runnable() {
            @Override
            public void run() {
                EstimateEndPoint estimateEndPoint = EndPointBuilder.getEstimateEndPoint();
                try {
                    Log.i("VIEWMOODEL", "run: =====");
                    CommonResponse commonResponse
                            = estimateEndPoint.notifyPriceChange(userId,estimatedPropertyModel.getLatitude(),
                                estimatedPropertyModel.getLongitude(), estimatedPropertyModel.getSizeValue(),
                                estimatedPropertyModel.getPropertyType())
//                                .setAddress(estimatedPropertyModel.getPropertyAddress())
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();


                    if(commonResponse!=null&&commonResponse.getStatus()){
                        callback.sendSuccess("Thanks for subscribing. We will notify you on price update.");
                    }else {
                        callback.sendError();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    callback.sendError();
                }

            }
        }).start();
    }


    public void requestforloan(final LoanLead loanLead){

        new Thread(new Runnable() {
            @Override
            public void run() {
                LoanEndPoint loanEndPoint = EndPointBuilder.getLoanEndPoint();
                try {

                    com.buy.housing.backend.loanEndPoint.model.CommonResponse commonResponse
                            = loanEndPoint.applyForLoan(loanLead)
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();



                    if(commonResponse!=null&&commonResponse.getStatus()){
                        callback.sendSuccess("We have received your enquiry. Our executive will contact you soon.");
                    }else {
                        callback.sendError();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    callback.sendError();
                }

            }
        }).start();
    }



    /**
     * To get the Project list of given offset and limit with given filter
     * This method gives the project list by using latitude and longitude - project by location
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
    public void getProjectListByDistance(double latitude, double longitude, String cityId,Integer offset, Integer limit,
                                         String propertyType, String bedType, String projectStatus, Integer minPrice,
                                         Integer maxPrice, Integer minArea, Integer maxArea, String orderBy
            , Long requestId, String appVersion, Long userId,
                                         String userEmail, String userPhone, String userName, Long virtualId,
                                         String leadsource, String browser, String browserVersion,
                                         String browserType, String city, String searchTxt){
        if(!callback.isInternetConnected()){
            return;
        }
        callback.showLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ProjectListResponse> projectListResponse = apiService.getProjectListByDistance(latitude,longitude,cityId,offset,limit,
                propertyType,bedType,projectStatus,minPrice,maxPrice,minArea,maxArea,orderBy
                , requestId, appVersion, userId
                , userEmail, userPhone, userName, virtualId, leadsource, browser, browserVersion, browserType, city, searchTxt);
        projectListResponse.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                callback.hideLoader();
                if(response != null && response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        callback.onSuccessProjectByCity(response);
                    }else if(!TextUtils.isEmpty(response.body().getErrorMessage())){
                        callback.onErrorProjectByCity(response.body().getErrorMessage());
                    }else{
                        callback.onErrorProjectByCity("Error getting property,please retry.");
                    }
                }else{
                    callback.onErrorProjectByCity("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                callback.hideLoader();
                callback.onErrorProjectByCity(t.toString());
            }
        });
    }


    /**
     * To get the Project list of given offset and limit with given filter
     * This method is used for pagination, if user scroll down more, so we call this method to get more project list
     * This method gives project list by location
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
   /* public void getNextProjectListByDistance(double latitude, double longitude, String cityId,Integer offset, Integer limit,
                                             String propertyType, String bedType, String projectStatus, Integer minPrice, Integer maxPrice,
                                             Integer minArea, Integer maxArea, String orderBy){
        if(!callback.isInternetConnected()){
            return;
        }
        callback.showPagingLoader();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ProjectListResponse> projectListResponse = apiService.getProjectListByDistance(latitude,longitude,cityId,offset,limit,
                propertyType,bedType,projectStatus,minPrice,maxPrice,minArea,maxArea,orderBy);
        projectListResponse.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                callback.hidePagingLoader();
                if(response != null && response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        callback.onSuccessNextProjectListByCity(response);
                    }else if(!TextUtils.isEmpty(response.body().getErrorMessage())){
                        callback.onErrorNextProjectListByCity(response.body().getErrorMessage());
                    }else{
                        callback.onErrorNextProjectListByCity("Error getting property,please retry.");
                    }
                }else{
                    callback.onErrorNextProjectListByCity("Error getting property,please retry.");
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                callback.hidePagingLoader();
                callback.onErrorNextProjectListByCity(t.toString());
            }
        });
    }*/

}
