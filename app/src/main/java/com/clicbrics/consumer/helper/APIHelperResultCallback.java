package com.clicbrics.consumer.helper;

/**
 * Created by Alok on 23-05-2019.
 */
public interface APIHelperResultCallback {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onError(String errMsg);
}
