package com.clicbrics.consumer.framework.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
//import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by Alok on 05-09-2016.
 */
public class TrackAnalytics {

    static FirebaseAnalytics mFirebaseAnalytics;
    public static void trackException(String TAG,String msg,Exception e) {
        /*FirebaseCrash.logcat(Log.INFO, TAG, msg);
        FirebaseCrash.logcat(android.util.Log.ERROR, TAG, msg);
        FirebaseCrash.report(e);*/
        try {
            Crashlytics.logException(e);
            if(msg != null) {
                Crashlytics.log(5, TAG, msg);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void trackEvent( String action, String value,Context context) {
        try {
            if(mFirebaseAnalytics == null) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            }
            Bundle bundle = new Bundle();
//        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, 1);
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "asdasd");
            bundle.putString(FirebaseAnalytics.Param.VALUE,value );
            //Logs an app event.
            mFirebaseAnalytics.logEvent(action, bundle);
            //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
            mFirebaseAnalytics.setMinimumSessionDuration(20000);

            //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
            mFirebaseAnalytics.setSessionTimeoutDuration(500);
            Crashlytics.log(10,action,value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void trackScreenEvent(Activity activity, String title, String message){
        if(mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        }
        //firebaseAnalytics.setCurrentScreen(activity,title,message);
    }

    public static void trackEvent( String action, String label,long holdTime, Context contex) {
        try {
            if(mFirebaseAnalytics == null){
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(contex);
            }
            mFirebaseAnalytics.setUserProperty("REDBRICS_EVENT","EVENT");
            Bundle bundle = new Bundle();
            bundle.putString("Screen",action);
            bundle.putString("Event",label);
            bundle.putLong("value",holdTime);

            mFirebaseAnalytics.logEvent(action.trim().replace(" ",""), bundle);
            Crashlytics.log(10,action,label+": " + holdTime);
            Log.i(action,label + " " +holdTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
