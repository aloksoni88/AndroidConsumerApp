package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;


public class NoInternet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        Button tryAgain = (Button) findViewById(R.id.try_again);
        UtilityMethods.setStatusBarColor(this,R.color.gray_300);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityMethods.isInternetConnected(NoInternet.this)) {
                    if (UtilityMethods.getBooleanInPref(NoInternet.this, Constants.SharedPreferConstants.FIRST_LAUNCH, false)) {
                        startActivity(new Intent(NoInternet.this, PickCity.class));
                        finish();
                        return;
                    }

                    startActivity(new Intent(NoInternet.this, HomeScreen.class));
                    finish();
                } else {
                    startActivity(new Intent(NoInternet.this, NoInternet.class));
                    finish();
                }
            }
        });
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
        TrackAnalytics.trackEvent("NoInternetScreen", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);
    }
}
