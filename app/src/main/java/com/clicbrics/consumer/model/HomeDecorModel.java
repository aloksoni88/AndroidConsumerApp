package com.clicbrics.consumer.model;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.buy.housing.backend.decorEndPoint.DecorEndPoint;
import com.buy.housing.backend.decorEndPoint.model.DecorListResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.Presenter.HomeDecorPresenterContract;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.SocketTimeoutException;

/**
 * Created by Alok on 13-07-2018.
 */
public class HomeDecorModel implements HomeDecorPresenterContract.GetDecorViewInteractor {

    private DecorEndPoint mDecorEndPoint;
    private Handler handler = new Handler(Looper.getMainLooper());

    private void buildWebService(){
        mDecorEndPoint = EndPointBuilder.getDecorEndPoint();
    }

    @Override
    public void getDecorView(final OnFinishedListerner onFinishedListerner) {
        buildWebService();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try{
                    final DecorListResponse decorListResponse = mDecorEndPoint.getDecorList(0,15)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onFinishedListerner.onSuccess(decorListResponse);
                        }
                    });
                }catch (final SocketTimeoutException e) {
                    e.printStackTrace();
                    errorMsg = "Internet connectivity lost,please retry.";
                }catch (final Exception e) {
                    e.printStackTrace();
                    errorMsg = "Error getting news list,please retry.";;
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onFinishedListerner.onError(errorMsg);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void getNextDecorList(final OnFinishedListerner onFinishedListerner, final int offset, final int limit) {
        buildWebService();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try{
                    final DecorListResponse decorListResponse = mDecorEndPoint.getDecorList(offset,limit)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onFinishedListerner.onNextPageLoadSuccess(decorListResponse);
                        }
                    });
                }catch (final SocketTimeoutException e) {
                    e.printStackTrace();
                    errorMsg = "Internet connectivity lost,please retry.";
                }catch (final Exception e) {
                    e.printStackTrace();
                    errorMsg = "Error getting news list,please retry.";;
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onFinishedListerner.onNextPageLoadError(errorMsg);
                        }
                    });
                }
            }
        }).start();
    }
}
