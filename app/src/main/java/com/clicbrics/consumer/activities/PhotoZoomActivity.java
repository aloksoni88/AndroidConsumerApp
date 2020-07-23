package com.clicbrics.consumer.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.imagegestures.PhotoView;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoZoomActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_zoom);

        Toolbar toolbar = (Toolbar) findViewById(R.id.zoom_activity_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        //addBackButton();

        final PhotoView floorPlan = (PhotoView) findViewById(R.id.floor_plan);
        final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.id_imageloading_progressBar);
        String image = getIntent().getStringExtra(Constants.IntentKeyConstants.FLOOR_PLAN)+"=h1200";
        /*int screenWidth = UtilityMethods.getScreenWidth(this);
        Picasso.with(this).load(image)
                .placeholder(R.drawable.layout_placeholder_small)
                .resize(2*screenWidth, 0)
                .into(floorPlan);*/
        floorPlan.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(image)
                .placeholder(R.drawable.layout_unavailable)
                .into(floorPlan, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProgressBar.setVisibility(View.GONE);
                        floorPlan.setEnabled(true);
                    }

                    @Override
                    public void onError(Exception e) {
                        mProgressBar.setVisibility(View.GONE);
                        floorPlan.setEnabled(true);
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
        TrackAnalytics.trackEvent("PhotoZoomActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
