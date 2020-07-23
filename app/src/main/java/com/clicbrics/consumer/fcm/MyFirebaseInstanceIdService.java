package com.clicbrics.consumer.fcm;

import android.text.TextUtils;
import android.util.Log;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.UnknownHostException;

/**
 * Created by Alok on 24-02-2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Firebase Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {
        if(!UtilityMethods.isInternetConnected(this)){
            return;
        }
        buildRegService();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    final long userId = UtilityMethods.getLongInPref(MyFirebaseInstanceIdService.this, Constants.ServerConstants.USER_ID, -1);
                    UserEndPoint.UpdateFCMKey updateFCMKey = mUserEndPoint.updateFCMKey(userId,token);
                    updateFCMKey.setOSType(Constants.AppConstants.OSType.ANDROID.toString());

                    final CommonResponse response = updateFCMKey.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (response != null && response.getStatus()) {
                    } else {
                        errorMsg = "No target found";
                    }
                } catch (UnknownHostException e) {
                    errorMsg = getString(R.string.network_error_msg);
                    e.printStackTrace();
                } catch (Exception e) {
                    errorMsg = getString(R.string.something_went_wrong);
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;
                }
            }
        }).start();
    }


    UserEndPoint mUserEndPoint;
    //method to init VersionEndPoint
    private void buildRegService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }

}
