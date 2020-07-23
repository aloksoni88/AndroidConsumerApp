package com.clicbrics.consumer.viewmodel;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.buy.housing.backend.propertyEndPoint.PropertyEndPoint;
import com.buy.housing.backend.propertyEndPoint.model.Builder;
import com.buy.housing.backend.propertyEndPoint.model.GetProjectDetailResponse;
import com.buy.housing.backend.propertyEndPoint.model.Project;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.UpdateUserStatResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.helper.ProjectDetailsResultCallback;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Alok on 13-08-2018.
 */
public class ProjectDetailsViewModel {

    private ProjectDetailsResultCallback mResultCallback;
    private GetProjectDetailResponse projectDetailResponse;

    public ProjectDetailsViewModel(GetProjectDetailResponse projectDetailResponse){
        this.projectDetailResponse = projectDetailResponse;
    }

    public ProjectDetailsViewModel(ProjectDetailsResultCallback resultCallback) {
        this.mResultCallback = resultCallback;
    }

    public Project getProject(){
        return projectDetailResponse.getProject();
    }

    public void setProject(Project project){
        projectDetailResponse.setProject(project);
    }

    public List<PropertyType> getPropertyTypeList(){
        return projectDetailResponse.getPropertyTypeList();
    }

    public void getPropertyTypeList(List<PropertyType> propertyTypeList){
        projectDetailResponse.setPropertyTypeList(propertyTypeList);
    }

    public Builder getBuilder(){
        return projectDetailResponse.getBuilder();
    }

    public void setBuilder(Builder builder){
        projectDetailResponse.setBuilder(builder);
    }

    public final void getProjectDetails(final long projectId, final long userId){
        if(!mResultCallback.isInternetConnected(true)){
            return;
        }
        mResultCallback.showLoader();
        new Thread(new Runnable() {
            String errMsg = "";
            @Override
            public void run() {
                try {
                    final PropertyEndPoint.GetProjectDetails getProjectDetails = EndPointBuilder.getPropertyEndPoint().getProjectDetails(projectId);
                    if(userId != -1 && userId != 0){
                        getProjectDetails.setUserId(userId);
                    }
                    getProjectDetails.setRequestHeaders(UtilityMethods.getHttpHeaders());
                    final GetProjectDetailResponse projectDetailResponse = getProjectDetails.execute();
                    if(projectDetailResponse != null){
                        if(projectDetailResponse.getStatus()){
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mResultCallback.onSuccess(projectDetailResponse);
                                    mResultCallback.hideLoader();
                                }
                            });
                        }else if(!TextUtils.isEmpty(projectDetailResponse.getErrorMessage())){
                            errMsg = projectDetailResponse.getErrorMessage();
                        }else{
                            errMsg = "Error getting project details,please retry.";
                        }
                    }else{
                        errMsg = "Error getting project details,please retry.";
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    errMsg = "Internet connecivity lost,please retry";
                } catch (Exception e) {
                    e.printStackTrace();
                    errMsg = "Error getting project details,please retry.";;
                }
                if(!TextUtils.isEmpty(errMsg)){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mResultCallback.hideLoader();
                            mResultCallback.onError(errMsg);
                        }
                    });
                }
            }
        }).start();
    }


    public void callUpdateStatAPI(final long userId, final long virtualUID){
        if(mResultCallback != null && !mResultCallback.isInternetConnected(false)){
            return;
        }
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    final UserEndPoint.UpdateStat updateStat = EndPointBuilder.getUserEndPoint().updateStat(Constants.AppConstants.SOURCE);
                    if (userId != -1) {
                        updateStat.setUId(userId);
                    } else if (virtualUID != -1) {
                        updateStat.setUId(virtualUID);
                    }
                    updateStat.setCall(true);
                    final UpdateUserStatResponse updateUserStatResponse = updateStat.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (updateUserStatResponse != null && updateUserStatResponse.getStatus()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if(mResultCallback != null) {
                                    mResultCallback.onSuccessUpdateStat(updateUserStatResponse);
                                }
                            }
                        });
                    } else if (updateUserStatResponse != null && !updateUserStatResponse.getErrorMessage().isEmpty()) {
                        errorMsg = updateUserStatResponse.getErrorMessage();
                    } else {
                        errorMsg = "Error getting update stat,please retry";
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    errorMsg = "You lost internet connectivity,please retry";
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Error getting update stat,please retry";
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(mResultCallback != null) {
                                mResultCallback.onErrorUpdateStat(errorMsg);
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
