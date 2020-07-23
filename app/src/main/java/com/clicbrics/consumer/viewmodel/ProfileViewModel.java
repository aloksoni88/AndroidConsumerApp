package com.clicbrics.consumer.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.buy.housing.backend.userEndPoint.model.UserProfileResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.helper.APIHelperResultCallback;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.SocketTimeoutException;

/**
 * Created by Alok on 16-09-2018.
 */
public class ProfileViewModel extends ViewModel {

    private MutableLiveData<UserProfileResponse> mutableLiveData;
    public MutableLiveData<UserProfileResponse> getUserProfileDetail(APIHelperResultCallback resultCallback,long userId){
        mutableLiveData = new MutableLiveData<>();
        loadUserProfileData(resultCallback,userId);
        return mutableLiveData;
    }


    private void loadUserProfileData(final APIHelperResultCallback resultCallback, final long userId){
        if(!resultCallback.isInternetConnected()){
            return;
        }
        resultCallback.showLoader();
        new Thread(new Runnable() {
            String errMsg = "";
            @Override
            public void run() {
                try {
                    final UserProfileResponse userProfileResponse = EndPointBuilder.getUserEndPoint()
                            .getUserProfileResponse(userId)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (userProfileResponse != null && userProfileResponse.getStatus()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                mutableLiveData.setValue(userProfileResponse);
                                resultCallback.hideLoader();
                            }
                        });
                    }else if(userProfileResponse != null && !TextUtils.isEmpty(userProfileResponse.getErrorMessage())){
                        errMsg = userProfileResponse.getErrorMessage();
                    }else{
                        errMsg = "Profile data not found";
                    }
                } catch (SocketTimeoutException e) {
                    errMsg = "You lost internet connectivity,please retry";
                    e.printStackTrace();
                }catch (Exception e) {
                    errMsg = "Could not find result,please retry";
                    e.printStackTrace();
                }
                if(!TextUtils.isEmpty(errMsg)){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            resultCallback.onError(errMsg);
                            resultCallback.hideLoader();
                        }
                    });
                }
            }
        }).start();
    }
}
