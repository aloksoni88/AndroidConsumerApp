package com.clicbrics.consumer.helper;

import com.buy.housing.backend.propertyEndPoint.model.NearbyResponse;

/**
 * Created by Alok on 17-10-2018.
 */
public interface NearByResultCallback {
    void showLoader();
    void hideLoader();
    void onSuccess(NearbyResponse nearbyResponse);
    void onError(String errMsg);
}
