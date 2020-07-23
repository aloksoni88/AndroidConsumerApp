package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.fragment.PickCityFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.PermissionManager;
import com.clicbrics.consumer.utils.UtilityMethods;

public class PickCity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = PickCity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_CODE = 4;
    private static final int LOCATION_SETTING_CHANGE_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_city);

        //UtilityMethods.setStatusBarColor(this,R.color.status_bar_color_light);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.pick_city_activity, new PickCityFragment(),Constants.AppConstants.TAG_PICKCITY_FRAGMENT).commit();
    }

    long onStartTime;
    @Override
    protected void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.PickCity);

        /*TrackAnalytics.trackEvent("CitySelectionActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_SETTING_CHANGE_CODE) {
            PickCityFragment pickCityFragment = (PickCityFragment) getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_PICKCITY_FRAGMENT);
            if(pickCityFragment != null) {
                pickCityFragment.onActivityResult(requestCode, resultCode, data);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                PickCityFragment pickCityFragment = (PickCityFragment) getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_PICKCITY_FRAGMENT);
                if(pickCityFragment != null) {
                    pickCityFragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
                }
                break;
            default:
                Log.d(TAG, "code->" + requestCode);
                break;
        }
    }
}
