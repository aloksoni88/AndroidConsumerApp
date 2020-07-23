package com.clicbrics.consumer.framework.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Alok on 26-09-2016.
 */

@SuppressWarnings("deprecation")
public class GCMRegistration {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    private final String SENDER_ID = BuildConfigConstants.GCM_SENDER_ID;

    /**
     * Tag used on log messages.
     */
    static final String TAG = GCMRegistration.class.getName();

    //GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context mContext;

    public GCMRegistration(Context context){
        mContext = context;
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            //gcm = GoogleCloudMessaging.getInstance(mContext);
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(mContext);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog((Activity)mContext, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    public void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGcmPreferences(mContext);
        int appVersion = getAppVersion(mContext);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public String getRegistrationId() {
        final SharedPreferences prefs = getGcmPreferences(mContext);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(mContext);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public void resetRegistrationId()
    {
        final SharedPreferences prefs = getGcmPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.apply();
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */

    public  String registerGCM() throws IOException
    {
        String msg = "";
//        try {
        /*if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(mContext);
        }
        String  regid = gcm.register(SENDER_ID);
        msg = "Device registered, registration ID=" + regid;
        Log.i(TAG, "msg: "+msg);

        // For this demo: we don't need to send it because the device will send
        // upstream messages to a server that echo back the message using the
        // 'from' address in the message.

        // Persist the regID - no need to register again.
        storeRegistrationId(regid);
        return regid;*/
        return "";
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public  int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException(context.getString(R.string.could_not_get_package_name) + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(GCMRegistration.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
}
