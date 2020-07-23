package com.clicbrics.consumer.Presenter;

import com.buy.housing.backend.decorEndPoint.model.DecorListResponse;

/**
 * Created by Alok on 13-07-2018.
 */
public interface HomeDecorPresenterContract {

    interface View{
        boolean isInternetConnected();
        void showProgress();
        void hideProgress();
        void setDecorView(DecorListResponse response);
        void setErrorView(String message);
        void showPagingLoader();
        void hidePagingLoader();
        void setNextDecorList(DecorListResponse response);
        void setNextPageErrorView(String message);
    }

    interface GetDecorViewInteractor{
        interface OnFinishedListerner{
            void onSuccess(DecorListResponse decorListResponse);
            void onError(String message);
            void onNextPageLoadSuccess(DecorListResponse decorListResponse);
            void onNextPageLoadError(String message);
        }

        void getDecorView(OnFinishedListerner onFinishedListerner);
        void  getNextDecorList(OnFinishedListerner onFinishedListerner,int offset, int limit);
    }

    interface Presenter{
        void getView();
        void loadNextPage(int offset, int limit);
        void onDestroy();
    }
}
