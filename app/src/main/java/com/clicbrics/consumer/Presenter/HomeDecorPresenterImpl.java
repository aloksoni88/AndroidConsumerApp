package com.clicbrics.consumer.Presenter;

import com.buy.housing.backend.decorEndPoint.model.DecorListResponse;
import com.clicbrics.consumer.utils.UtilityMethods;

/**
 * Created by Alok on 13-07-2018.
 */
public class HomeDecorPresenterImpl implements HomeDecorPresenterContract.Presenter,
        HomeDecorPresenterContract.GetDecorViewInteractor.OnFinishedListerner {

    private HomeDecorPresenterContract.View view;
    private HomeDecorPresenterContract.GetDecorViewInteractor getDecorViewInteractor;
    public HomeDecorPresenterImpl(HomeDecorPresenterContract.View view,
                                  HomeDecorPresenterContract.GetDecorViewInteractor getDecorViewInteractor) {
        this.view = view;
        this.getDecorViewInteractor = getDecorViewInteractor;
    }

    @Override
    public void getView() {
        if(view != null && view.isInternetConnected()){
            view.showProgress();
            getDecorViewInteractor.getDecorView(this);
        }
    }

    @Override
    public void loadNextPage(int offset, int limit) {
        if(view != null && view.isInternetConnected()){
            view.showPagingLoader();
            getDecorViewInteractor.getNextDecorList(this,offset,limit);
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onSuccess(DecorListResponse decorListResponse) {
        if(view != null){
            view.setDecorView(decorListResponse);
            view.hideProgress();
        }
    }


    @Override
    public void onError(String errMsg) {
        if(view != null){
            view.hideProgress();
            view.setErrorView(errMsg);
        }
    }

    @Override
    public void onNextPageLoadSuccess(DecorListResponse decorListResponse) {
        if(view != null){
            view.setNextDecorList(decorListResponse);
            view.hidePagingLoader();
        }
    }

    @Override
    public void onNextPageLoadError(String message) {
        if(view != null){
            view.hidePagingLoader();
            view.setNextPageErrorView(message);
        }
    }
}
