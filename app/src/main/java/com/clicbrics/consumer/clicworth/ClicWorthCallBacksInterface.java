package com.clicbrics.consumer.clicworth;

public interface ClicWorthCallBacksInterface {

    boolean isInternetConnected();
    void showLoader();
    void hideLoader();
    void onError(String s);

    void onSuccess(GetEStimateModel body);
}
