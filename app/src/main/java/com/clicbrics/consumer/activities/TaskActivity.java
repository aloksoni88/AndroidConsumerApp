package com.clicbrics.consumer.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.fragment.TaskFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class TaskActivity extends BaseActivity {

    public static Long agentId;
    public static ArrayList<String> timeline;
    public static ArrayList<String> timeStamp;
    public static String timelineStatus;

    public TextView mToolbarText;
    public AppBarLayout mToolbarLayout;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarText = (TextView) findViewById(R.id.id_task_toolbar_text);
        mToolbarLayout = (AppBarLayout) findViewById(R.id.toolbar_container_layout);
        if (getIntent().hasExtra("project")) {
            mToolbarText.setText(getIntent().getStringExtra("project"));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
        if (getIntent().hasExtra("agentId")) {
            agentId = getIntent().getLongExtra("agentId", 0);
        }

        if (getIntent().hasExtra("timeline")) {
            timeline = getIntent().getStringArrayListExtra("timeline");
        }

        if (getIntent().hasExtra("timelineStatus")) {
            timelineStatus = getIntent().getStringExtra("timelineStatus");
        }

        if (getIntent().hasExtra("timeStamp")) {
            timeStamp = getIntent().getStringArrayListExtra("timeStamp");
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction().add(R.id.task_fragment, new TaskFragment())
                    .commit();
        }

        /*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        UtilityMethods.setStatusBarColor(this,R.color.status_bar_color_dark);*/
    }

    @Override
    public void onDestroy() {
        if (timeline != null && !timeline.isEmpty()) {
            timeline.clear();
        }
        if (timeStamp != null && !timeStamp.isEmpty()) {
            timeStamp.clear();
        }
        if (agentId != null && agentId != 0) {
            agentId = null;
        }
        super.onDestroy();
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
        TrackAnalytics.trackEvent("SiteVisitDetailActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);
    }
}
