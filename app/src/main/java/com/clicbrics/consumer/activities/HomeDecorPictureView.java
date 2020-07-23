package com.clicbrics.consumer.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.buy.housing.backend.decorEndPoint.model.Decor;
import com.buy.housing.backend.decorEndPoint.model.DecorItem;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.DecorPictureViewPagerAdapter;
import com.clicbrics.consumer.adapter.ImageGalleryPictureViewPagerAdapter;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.ImageObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeDecorPictureView extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = HomeDecorPictureView.class.getSimpleName();
    private TextView toolbarTitle;
    private DecorPictureViewPagerAdapter mPagerAdapter;
    private ArrayList<String> titles;
    ArrayList<DecorItem> decorItemList;
    int selectedImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_decor_picture_view);

        if(getIntent().hasExtra("DecorItemList")){
            String decorListStr = getIntent().getStringExtra("DecorItemList");
            Gson gson = new Gson();
            Type type = new TypeToken<List<DecorItem>>() {
            }.getType();
            decorItemList = gson.fromJson(decorListStr,type);
            if(decorItemList != null && !decorItemList.isEmpty()){
                titles = new ArrayList<>();
                for(int i=0; i<decorItemList.size(); i++){
                    DecorItem decorItem = decorItemList.get(i);
                    if(decorItem != null){
                        if(!TextUtils.isEmpty(decorItem.getTitle())){
                            titles.add(decorItem.getTitle());
                        }else{
                            titles.add("Decor View");
                        }
                    }
                }
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.decor_picture_view_toolbar);
        toolbarTitle = (TextView) findViewById(R.id.id_decor_picture_view_text);
        if(getIntent().hasExtra("Position")){
            selectedImageIndex = getIntent().getIntExtra("Position",0);
        }
        toolbarTitle.setText(titles.get(selectedImageIndex));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        ViewPager mViewPager = findViewById(R.id.id_decor_picture_viewpager);
        setupViewPager(mViewPager, decorItemList);
        mViewPager.setCurrentItem(selectedImageIndex);
        if(mViewPager != null) {
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.addOnPageChangeListener(this);
        }
    }

    private void setupViewPager(ViewPager viewPager, ArrayList<DecorItem> decorItems) {
        mPagerAdapter = new DecorPictureViewPagerAdapter(this,decorItems);
        viewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        toolbarTitle.setText(titles.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
        new EventAnalyticsHelper().logUsageStatsEvents(HomeDecorPictureView.this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.ImageGalleryZoomViewActivity);

       /* TrackAnalytics.trackEvent("ImageGalleryZoomViewActivity", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);*/
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
