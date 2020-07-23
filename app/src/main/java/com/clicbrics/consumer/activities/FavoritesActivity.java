package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.fragment.Favorite_Fragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

public class FavoritesActivity extends BaseActivity {

    private static final String TAG = "FavoritesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //UtilityMethods.setStatusBarColor(this,R.color.light_white);
        /*FrameLayout containerParent = (FrameLayout) findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_favorites, containerParent);*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.favorite_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        /*ImageView drawerButton = (ImageView) findViewById(R.id.drawer_button);
        if(getIntent().hasExtra("isFromProfile") && getIntent().getBooleanExtra("isFromProfile",false)){
            drawerButton.setImageResource(R.drawable.ic_back_button_white_icon);
            drawerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FavoritesActivity.super.onBackPressed();
                }
            });
        }else{
            drawerButton.setImageResource(R.drawable.ic_menu);
            drawerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }*/

        getSupportFragmentManager().beginTransaction().add(R.id.favorites_layout,
                                                                    new Favorite_Fragment(),Constants.AppConstants.TAG_FAVORITE_FRAGMENT).commit();
    }

    long onStartTime;
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        onStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        TrackAnalytics.trackEvent("FavoritesActivity", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);
    }


    /**
     * Refresh the list while user coming from details screen
     * @param requestCode - request code for resultant activity
     * @param resultCode - result code
     * @param data- intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.ActivityRequestCode.FAVORITE_ACT_RESULT_CODE){
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_FAVORITE_FRAGMENT);
            if(fragment != null && fragment instanceof Favorite_Fragment){
                Favorite_Fragment favoriteFragment = (Favorite_Fragment) fragment;
                favoriteFragment.refreshListOnRestart();
            }
        }
    }
}
