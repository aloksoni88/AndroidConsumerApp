package com.clicbrics.consumer.Presenter;

import com.buy.housing.backend.newsEndPoint.model.NewsListResponse;

/**
 * Created by Alok on 24-07-2018.
 */
public class NewsPresenterImpl implements NewsPresenterContract.Presenter
            ,NewsPresenterContract.NewsViewInteractor.OnFinishedListerner{

    NewsPresenterContract.View view;
    NewsPresenterContract.NewsViewInteractor interactor;
    public NewsPresenterImpl(NewsPresenterContract.View view, NewsPresenterContract.NewsViewInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }


    @Override
    public void onSuccess(NewsListResponse response) {
        if(view != null){
            view.hideProgress();
            view.setNewsList(response);
        }
    }

    @Override
    public void onError(String message) {
        if(view != null){
            view.hideProgress();
            view.setErrorView(message);
        }
    }

    @Override
    public void onNextPageLoadSuccess(NewsListResponse response) {
        if(view != null){
            view.hidePagingLoader();
            view.setNextNewsList(response);
        }
    }

    @Override
    public void onNextPageLoadError(String message) {
        if(view != null){
            view.hidePagingLoader();
            view.setOnNextPageLoadErrorView(message);
        }
    }

    @Override
    public void getView() {
        if(view != null && view.isInternetConnect()){
            view.showProgress();
            interactor.getNewsList(this);
        }
    }

    @Override
    public void loadNextPage(int offset, int limit) {
        if(view != null && view.isInternetConnect()){
            view.showPagingLoader();
            interactor.getNextNewsList(this,offset,limit);
        }
    }

    @Override
    public void onDestroy() {

    }
}
