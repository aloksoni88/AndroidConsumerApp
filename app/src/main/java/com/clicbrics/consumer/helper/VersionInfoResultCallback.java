package com.clicbrics.consumer.helper;

import com.buy.housing.backend.userEndPoint.model.VersionResponse;

/**
 * Created by Alok on 13-08-2018.
 */
public interface VersionInfoResultCallback {
    void onSuccessVerisonInfoAPI(VersionResponse versionResponse);
    void onErrorVersionInfoAPI(String errorMsg);
}
