package com.clicbrics.consumer.helper;


import com.buy.housing.backend.userEndPoint.model.PropertySearchResponse;
import com.buy.housing.backend.userEndPoint.model.SearchProperty;

import java.util.List;

/**
 * Created by Alok on 12-09-2018.
 */
public interface WishlistResultCallback {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    //void onSuccess(PropertySearchResponse response);
    void onSuccess(List<SearchProperty> wishList);
    void onError(String errMsg);
    void showPagingLoader();
    void hidePagingLoader();
    void onNextPageLoadSuccess(PropertySearchResponse response);
    void onNextPageLoadError(String errMsg);
}
