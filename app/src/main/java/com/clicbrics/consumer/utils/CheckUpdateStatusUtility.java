package com.clicbrics.consumer.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.buy.housing.backend.userEndPoint.model.VersionResponse;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.helper.VersionInfoResultCallback;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.viewmodel.VersionInfoViewModel;
import com.google.gson.Gson;

import java.util.Collections;

/**
 * Created by Alok on 13-08-2018.
 */
public class CheckUpdateStatusUtility implements VersionInfoResultCallback{

    private static final String TAG = "CheckUpdateStatus";
    private Handler handler;
    private Context context;
    public CheckUpdateStatusUtility(Context context) {
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
    }

    public void checkUpdateStatus(){
        VersionInfoViewModel viewModel = new VersionInfoViewModel(this);
        long userId = UtilityMethods.getLongInPref(context, Constants.ServerConstants.USER_ID, -1);
        long notificationTimestamp = UtilityMethods.getLongInPref(context, Constants.SharedPreferConstants.TIME_STAMP,0);
        if(UtilityMethods.isInternetConnected(context)) {
            viewModel.checkUpdateStatus(userId, notificationTimestamp);
        }
    }

    @Override
    public void onSuccessVerisonInfoAPI(VersionResponse versionResponse) {
        if (versionResponse != null) {
            UtilityMethods.saveLongInPref(context, Constants.SharedPreferConstants.NOTIFICATION_REFRESH_TIME, System.currentTimeMillis());
            Log.d(TAG, "vr NotificationCount->" + versionResponse.getUserNotificationCount());
            if (versionResponse.getAppConfig() != null) {
                float interestRate = versionResponse.getAppConfig().getInterestRate();
                UtilityMethods.saveFloatInPref(context, Constants.AppConfigConstants.DEFAULT_INTEREST_RATE, interestRate);

                String phone = versionResponse.getAppConfig().getContactNumber();
                if ((phone != null) && (!phone.isEmpty())) {
                    UtilityMethods.saveStringInPref(context, Constants.AppConfigConstants.CONTACT_NUMBER, phone);
                }
            }
            if(versionResponse.getUserNotificationCount() != null) {
                if (versionResponse.getUserNotificationCount() > 0) {
                    Log.i(TAG, "Notification Count -> " + versionResponse.getUserNotificationCount());
                    TrackAnalytics.trackEvent("notificaiton_count","homescreen",context);
                    UtilityMethods.saveBooleanInPref(context, Constants.AppConstants.IS_NEW_NOTIFICATION, true);
                } else {
                    UtilityMethods.saveBooleanInPref(context, Constants.AppConstants.IS_NEW_NOTIFICATION, false);
                }

                if (versionResponse.getNotificationInfoList() != null) {
                    Collections.sort(versionResponse.getNotificationInfoList(), new NotificationTimeComparator());
                    SystemConfig.getInstanceSystemConfig(context).resetConfig();
                    Gson gson = new Gson();
                    String notificationStr = gson.toJson(versionResponse.getNotificationInfoList());
                    if (!TextUtils.isEmpty(notificationStr))
                        SystemConfig.getInstanceSystemConfig(context).writeConfig(notificationStr);
                }
            }
            if(versionResponse.getAppConfig() != null){
                if(versionResponse.getAppConfig().getMapProjectCount() != null){
                    UtilityMethods.saveIntInPref(context,Constants.AppConfigConstants.MAP_PROJECT_COUNT,versionResponse.getAppConfig().getMapProjectCount());
                }
                if(versionResponse.getAppConfig().getMapPrjRefreshDist() != null){
                    UtilityMethods.saveIntInPref(context,Constants.AppConfigConstants.MAP_PROJECT_REF_DISTANCE,versionResponse.getAppConfig().getMapPrjRefreshDist());
                }
            }
            if (versionResponse.getVersionInfo() != null) {
                final String version = versionResponse.getVersionInfo().getVersionName();
                String appVersion = UtilityMethods.getVersionName((HomeScreen) context);
                boolean isUpdate = false;
                int com = UtilityMethods.isUpdate(version, appVersion);
                UtilityMethods.saveBooleanInPref(context, Constants.AppConfigConstants.PARAM_UPDATE_APP, true);
                if (com == 1) { // return -1 (a<b)return 0 (a=b)   return 1 (a>b)
                    isUpdate = true;
                }
                final boolean isUpd = isUpdate;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isUpd) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            // set title
                            alertDialogBuilder.setTitle(context.getString(R.string.force_update_title));
                            // set dialog message
                            alertDialogBuilder
                                    .setMessage(context.getString(R.string.force_update_msg))
                                    .setCancelable(false)
                                    .setPositiveButton(context.getString(R.string.force_update_btn_text), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            final String appPackageName = context.getPackageName();
                                            try {
                                                UtilityMethods.saveStringInPref(context, "AppVersion", version);
                                                ((HomeScreen)context).startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)), 101);
                                            } catch (ActivityNotFoundException anfe) {
                                                UtilityMethods.saveStringInPref(context, "AppVersion", version);
                                                ((HomeScreen)context).startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), 101);
                                            }
                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            // show it
                            alertDialog.show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onErrorVersionInfoAPI(String errorMsg) {
        Log.i(TAG, "onErrorVersionInfoAPI: " + errorMsg);
    }
}
