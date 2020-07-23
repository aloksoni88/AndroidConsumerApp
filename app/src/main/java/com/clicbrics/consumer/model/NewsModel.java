package com.clicbrics.consumer.model;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.buy.housing.backend.newsEndPoint.NewsEndPoint;
import com.buy.housing.backend.newsEndPoint.model.NewsListResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.Presenter.NewsPresenterContract;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.SocketTimeoutException;


/**
 * Created by Alok on 24-07-2018.
 */
public class NewsModel implements NewsPresenterContract.NewsViewInteractor{

    Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public void getNewsList(final OnFinishedListerner listerner) {
        final NewsEndPoint newsEndPoint = EndPointBuilder.getNewsEndPoint();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    final NewsListResponse newsResponse = newsEndPoint.getNewsList(1l,"1",0,15)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listerner.onSuccess(newsResponse);
                        }
                    });
                } catch (final SocketTimeoutException e) {
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
                            listerner.onError(errorMsg);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void getNextNewsList(final OnFinishedListerner listerner, final int offset, final int limit) {
        final NewsEndPoint newsEndPoint = EndPointBuilder.getNewsEndPoint();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    final NewsListResponse newsResponse = newsEndPoint.getNewsList(1l,"1",offset,limit)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listerner.onNextPageLoadSuccess(newsResponse);
                        }
                    });
                } catch (final SocketTimeoutException e) {
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
                            listerner.onNextPageLoadError(errorMsg);
                        }
                    });
                }
            }
        }).start();
    }
}
