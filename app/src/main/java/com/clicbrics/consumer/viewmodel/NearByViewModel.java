package com.clicbrics.consumer.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.buy.housing.backend.propertyEndPoint.PropertyEndPoint;
import com.buy.housing.backend.propertyEndPoint.model.NearbyResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.helper.NearByResultCallback;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.SocketTimeoutException;

/**
 * Created by Alok on 17-10-2018.
 */
public class NearByViewModel {

    NearByResultCallback mResultCallback;
    public NearByViewModel(NearByResultCallback nearByResultCallback) {
        this.mResultCallback = nearByResultCallback;
    }

    public final void getNearByAirport(final double latitude,final double longitude){
        mResultCallback.showLoader();
        new Thread(new Runnable() {
            String errMsg = "";
            @Override
            public void run() {
                try {
                    PropertyEndPoint.GetNearby nearByAirport = EndPointBuilder.getPropertyEndPoint().getNearby(latitude,longitude,"airport");
                    final NearbyResponse nearbyAirportResponse = nearByAirport.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if(nearbyAirportResponse != null){
                        if(nearbyAirportResponse.getStatus()){
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mResultCallback.onSuccess(nearbyAirportResponse);
                                    mResultCallback.hideLoader();
                                }
                            });
                        }else if(!TextUtils.isEmpty(nearbyAirportResponse.getErrorMessage())){
                            errMsg =  nearbyAirportResponse.getErrorMessage();
                        }else{
                            errMsg = "No airport found.";
                        }
                    }else{
                        errMsg = "No airport found.";
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    errMsg = "Seems you lost internet connectivity,please retry";
                } catch (Exception e) {
                    e.printStackTrace();
                    errMsg = "Error getting nearby airport,please retry.";;
                }
                if(!TextUtils.isEmpty(errMsg)){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mResultCallback.hideLoader();
                            mResultCallback.onError(errMsg);
                        }
                    });
                }
            }
        }).start();
    }
}
