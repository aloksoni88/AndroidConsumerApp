package com.clicbrics.consumer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.clicbrics.consumer.utils.UtilityMethods;

/**
 * Created by Alok on 25-09-2017.
 */

public class InternetBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "InternetBroadcast";
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if(UtilityMethods.isInternetConnected(context)){
                UtilityMethods.sendBroadCast(context);
            }else{

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
