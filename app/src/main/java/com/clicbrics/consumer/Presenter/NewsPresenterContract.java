package com.clicbrics.consumer.Presenter;

import com.buy.housing.backend.newsEndPoint.model.NewsListResponse;

/**
 * Created by Alok on 24-07-2018.
 */
public interface NewsPresenterContract {

    interface View{
        boolean isInternetConnect();
        void showProgress();
        void hideProgress();
        void setNewsList(NewsListResponse response);
        void showPagingLoader();
        void hidePagingLoader();
        void setNextNewsList(NewsListResponse response);
        void setErrorView(String message);
        void setOnNextPageLoadErrorView(String message);
    }

    interface NewsViewInteractor{
        interface OnFinishedListerner{
            void onSuccess(NewsListResponse response);
            void onError(String message);
            void onNextPageLoadSuccess(NewsListResponse response);
            void onNextPageLoadError(String message);
        }

        void getNewsList(OnFinishedListerner listerner);
        void getNextNewsList(OnFinishedListerner listerner,int offset, int limit);
    }

    interface Presenter{
        void getView();
        void loadNextPage(int offset, int limit);
        void onDestroy();
    }
}
