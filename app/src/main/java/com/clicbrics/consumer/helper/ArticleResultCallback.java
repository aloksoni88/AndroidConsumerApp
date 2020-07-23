package com.clicbrics.consumer.helper;

import com.buy.housing.backend.blogEndPoint.model.BlogListResponse;

/**
 * Created by Alok on 02-08-2018.
 */
public interface ArticleResultCallback {
    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onSuccess(BlogListResponse response);
    void onError(String errMsg);
    void showPagingLoader();
    void hidePagingLoader();
    void onNextPageLoadSuccess(BlogListResponse response);
    void onNextPageLoadError(String errMsg);
}
