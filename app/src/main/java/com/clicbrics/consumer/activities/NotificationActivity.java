package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.fragment.NotificationActivityFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;

public class NotificationActivity extends BaseActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        UtilityMethods.setStatusBarColor(this,R.color.light_white);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        getSupportFragmentManager()
                .beginTransaction().add(R.id.notification_container, new NotificationActivityFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(getIntent().hasExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION)
                && getIntent().getBooleanExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,false)){
            Intent intent = new Intent(this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        UtilityMethods.saveBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, false);
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.NotificationScreen);

      /*  TrackAnalytics.trackEvent("NotificationScreen", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);*/
    }
}
