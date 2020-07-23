package com.clicbrics.consumer.helper;

import com.buy.housing.backend.propertyEndPoint.model.FooterLinkListResponse;
import com.buy.housing.backend.propertyEndPoint.model.ProjectCollection;

public interface PopularProjectCallback {
    boolean isInternetConnected();
    void onSuccessPopularProject(FooterLinkListResponse projectBuilderCollection);
    void onErrorPopularProject(String errorMsg);
}
