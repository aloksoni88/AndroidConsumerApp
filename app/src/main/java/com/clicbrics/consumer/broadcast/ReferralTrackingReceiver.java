package com.clicbrics.consumer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;



/**
 * Created by Alok on 06-06-2017.
 */

/*
    Broadcast receiver class to track referral id
 */
public class ReferralTrackingReceiver extends BroadcastReceiver {
    private static final String TAG = ReferralTrackingReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        TrackAnalytics.trackEvent("RefTrackReceiver", Constants.AppConstants.HOLD_TIME , 1, context);
        Log.i(TAG, "onReceive: Called...");
        try {
            if(intent != null && intent.getExtras() != null) {
                for (String key : intent.getExtras().keySet()) {
                    try {
                        Log.i(TAG, "onReceive: Value " + String.valueOf(intent.getExtras().get(key)));
                        if(intent.getExtras().get(key) != null) {
                            TrackAnalytics.trackEvent("RefTrackReceiver", intent.getExtras().get(key).toString(), context);
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "onReceive: Exception cought ");
                    }
                }
            }else{
                Log.i(TAG, "onReceive: Called...but intent is null");
            }
        } catch (Exception e) {
            Log.i(TAG, "onReceive: Exception");
            e.printStackTrace();
        }
    }
}
