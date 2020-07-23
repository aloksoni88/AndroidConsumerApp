package com.clicbrics.consumer.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.FurnishingAdapterFull;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.AmenityObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class FurnishingListActivity extends BaseActivity {

    private final String TAG = FurnishingListActivity.class.getSimpleName();
    ArrayList<AmenityObject> flooringList, fittingList,wallsList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furnishing_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.furnishing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        if(getIntent().hasExtra("flooring_list")){
            Gson gson = new Gson();
            String json = getIntent().getStringExtra("flooring_list");
            Type type = new TypeToken<ArrayList<AmenityObject>>() {}.getType();
            flooringList = gson.fromJson(json, type);
            Log.d(TAG,"flooringSize->"+flooringList.size());

            RecyclerView flooring = (RecyclerView) findViewById(R.id.flooring_list);
            flooring.setNestedScrollingEnabled(false);
            flooring.setLayoutManager(new LinearLayoutManager(this));
            flooring.setAdapter(new FurnishingAdapterFull(this, flooringList));
        }

        if(getIntent().hasExtra("fitting_list")){
            Gson gson = new Gson();
            String json = getIntent().getStringExtra("fitting_list");
            Type type = new TypeToken<ArrayList<AmenityObject>>() {}.getType();

            fittingList = gson.fromJson(json, type);
            Log.d(TAG,"fittingSize->"+fittingList.size());

            RecyclerView fitting = (RecyclerView) findViewById(R.id.fitting_list);
            fitting.setNestedScrollingEnabled(false);
            fitting.setLayoutManager(new LinearLayoutManager(this));
            fitting.setAdapter(new FurnishingAdapterFull(this, fittingList));
        }

        if(getIntent().hasExtra("walls_list")){
            Gson gson = new Gson();
            String json = getIntent().getStringExtra("walls_list");
            Type type = new TypeToken<ArrayList<AmenityObject>>() {}.getType();

            wallsList = gson.fromJson(json, type);
            Log.d(TAG,"wallsListSize->"+wallsList.size());

            RecyclerView walls = (RecyclerView) findViewById(R.id.walls_list);
            walls.setNestedScrollingEnabled(false);
            walls.setLayoutManager(new LinearLayoutManager(this));
            walls.setAdapter(new FurnishingAdapterFull(this, wallsList));
        }

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        UtilityMethods.setStatusBarColor(this,R.color.gray_300);
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
        TrackAnalytics.trackEvent("FurnishListActivity", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);
    }
}
