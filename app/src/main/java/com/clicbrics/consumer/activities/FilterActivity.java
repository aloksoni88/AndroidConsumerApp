package com.clicbrics.consumer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.fragment.FilterFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

public class FilterActivity extends BaseActivity {
    public boolean isFilterChanged;
    private final int FILTER_REQUEST_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        //UtilityMethods.setStatusBarColor(this,R.color.status_bar_color_light);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.filter_container, new FilterFragment()).commit();
        }
    }



    @Override
    public void onBackPressed() {
        if(isFilterChanged) {
            setResult(RESULT_OK);
            finishActivity(FILTER_REQUEST_CODE);
            finish();
        }else{
            super.onBackPressed();
        }
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.FilterScreen);
        /*TrackAnalytics.trackEvent("FilterActivity", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);*/
    }
}
