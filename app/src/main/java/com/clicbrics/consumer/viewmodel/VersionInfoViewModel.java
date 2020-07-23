package com.clicbrics.consumer.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.VersionResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.helper.VersionInfoResultCallback;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Alok on 13-08-2018.
 */
public class VersionInfoViewModel extends ViewModel {

    private VersionInfoResultCallback mResultCallback;
    public VersionInfoViewModel(VersionInfoResultCallback resultCallback) {
        this.mResultCallback = resultCallback;
    }

    public void checkUpdateStatus(final long userId, final long notificationTimestamp) {
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                int errorCode = 0;
                try {
                    UserEndPoint mUserEndPoint = EndPointBuilder.getUserEndPoint();
                    VersionResponse versionResponse;
                    if (notificationTimestamp != 0) {
                        if (userId == -1) {
                            versionResponse = mUserEndPoint.getVersionInfo(Constants.AppConstants.OSType.ANDROID.toString(),
                                    Constants.AppConstants.AppType.Consumer.toString())
                                    .setTime(notificationTimestamp)
                                    .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        } else {
                            versionResponse = mUserEndPoint.getVersionInfo(Constants.AppConstants.OSType.ANDROID.toString(),
                                    Constants.AppConstants.AppType.Consumer.toString())
                                    .setUserId(userId)
                                    .setTime(notificationTimestamp)
                                    .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        }
                    } else {
                        if (userId == -1) {
                            versionResponse = mUserEndPoint.getVersionInfo(Constants.AppConstants.OSType.ANDROID.toString(),
                                    Constants.AppConstants.AppType.Consumer.toString())
                                    .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        } else {
                            versionResponse = mUserEndPoint.getVersionInfo(Constants.AppConstants.OSType.ANDROID.toString(),
                                    Constants.AppConstants.AppType.Consumer.toString())
                                    .setUserId(userId)
                                    .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        }
                    }
                    if (versionResponse != null) {
                        mResultCallback.onSuccessVerisonInfoAPI(versionResponse);
                    } else {
                        errorMsg = "Something went wrong, please retry";
                    }
                }catch (UnknownHostException e) {
                    errorMsg = "Something went wrong, please retry";
                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    errorMsg = "You lost internet connectivity, please retry";
                }catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Something went wrong, please retry";
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    mResultCallback.onErrorVersionInfoAPI(errorMsg);
                }
            }
        }).start();
    }
}
