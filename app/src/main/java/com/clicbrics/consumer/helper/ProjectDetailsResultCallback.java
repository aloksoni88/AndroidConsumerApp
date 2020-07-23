package com.clicbrics.consumer.helper;

import com.buy.housing.backend.propertyEndPoint.model.GetProjectDetailResponse;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.UpdateUserStatResponse;

/**
 * Created by Alok on 16-08-2018.
 */
public interface ProjectDetailsResultCallback {
    boolean isInternetConnected(boolean showError);
    void showLoader();
    void hideLoader();
    void onSuccess(GetProjectDetailResponse response);
    void onError(String errMsg);
    void onSuccessUpdateStat(UpdateUserStatResponse updateStatResponse);
    void onErrorUpdateStat(String errMsg);
}
