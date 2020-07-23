package com.clicbrics.consumer.helper;

import com.buy.housing.backend.userEndPoint.model.UserProfileResponse;

/**
 * Created by Alok on 16-09-2018.
 */
public interface ProfileResultCallback {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onSuccess(UserProfileResponse response);
    void onError(String errMsg);
}
