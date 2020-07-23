package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.StickyHeaderLayoutManager;
import com.clicbrics.consumer.adapter.ImageGalleryAdapter;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.ImageObject;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viven.imagezoom.ImageZoomHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


public class ImageGalleryActivity extends BaseActivity implements LoginSuccessCallback {

    RecyclerView imageList;
    LinkedHashMap<String, ArrayList<ImageObject>> images;
    ImageZoomHelper imageZoomHelper;
    FloatingActionButton favoriteFAB;
    long projectId;
    CoordinatorLayout rootLayout;
    UserEndPoint mUserEndPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildRegService();

        UtilityMethods.setStatusBarColor(this,R.color.profile_bg_color);

        setContentView(R.layout.activity_image_gallery);

        ImageView backButton =  findViewById(R.id.ic_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.image_list_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        addBackButton();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);*/

        imageZoomHelper = new ImageZoomHelper(this);

        imageList = (RecyclerView) findViewById(R.id.image_gallery);
        imageList.setLayoutManager(new StickyHeaderLayoutManager());

        rootLayout = (CoordinatorLayout) findViewById(R.id.image_gallery_parent_layout);

        String str = getIntent().getStringExtra("Images");
        Gson gson = new Gson();
        Type entityType = new TypeToken<LinkedHashMap<String, ArrayList<ImageObject>>>() {
        }.getType();
        images = gson.fromJson(str, entityType);

        if ((images != null) && (!images.isEmpty())) {
            imageList.setAdapter(new ImageGalleryAdapter(ImageGalleryActivity.this, images));
        }


        projectId = getIntent().getLongExtra(Constants.IntentKeyConstants.PROJECT_ID, -1);

        favoriteFAB = (FloatingActionButton) findViewById(R.id.gallery_favorite);

        //final List<Long> favoriteList = UtilityMethods.getSetValuesFromPrefs(ImageGalleryActivity.this, Constants.AppConstants.PROJECT_ID_SET);
        final Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(ImageGalleryActivity.this, Constants.AppConstants.PROJECT_ID_SET);
        if ((favoriteList != null) && (!favoriteList.isEmpty())) {
            if(favoriteList.contains(projectId+"")){
                favoriteFAB.setImageDrawable(ContextCompat.getDrawable(ImageGalleryActivity.this, R.drawable.ic_favorite_white_24dp));
            }
        }

        favoriteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UtilityMethods.isInternetConnected(ImageGalleryActivity.this)){
                    showNetworkErrorSnackBar();
                    return;
                }
                if (UtilityMethods.getLongInPref(ImageGalleryActivity.this, Constants.ServerConstants.USER_ID, 0) != 0) {
                    TrackAnalytics.trackEvent("ImgGalPage_Unmark_Fav", Constants.AppConstants.HOLD_TIME ,
                            1, ImageGalleryActivity.this);
                    //List<Long> favoriteList = UtilityMethods.getSetValuesFromPrefs(ImageGalleryActivity.this, Constants.AppConstants.PROJECT_ID_SET);
                    final Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(ImageGalleryActivity.this, Constants.AppConstants.PROJECT_ID_SET);
                    if ((favoriteList!=null) && (!favoriteList.isEmpty())
                            && (favoriteList.contains(projectId+""))) {
                        favoriteFAB.setImageDrawable(ContextCompat.getDrawable(ImageGalleryActivity.this,
                                R.drawable.favorite_icon_default));
                        /*UtilityMethods.removeFromFavorites(v, ImageGalleryActivity.this, mUserEndPoint,
                                projectId);*/
                        UtilityMethods.removeFavoriteFromServer(v,ImageGalleryActivity.this,mUserEndPoint,projectId);
                    } else {
                        TrackAnalytics.trackEvent("ImgGalPage_Mark_Fav", Constants.AppConstants.HOLD_TIME ,
                                1, ImageGalleryActivity.this);
                        favoriteFAB.setImageDrawable(ContextCompat.getDrawable(ImageGalleryActivity.this,
                                R.drawable.ic_favorite_white_24dp));
                        /*UtilityMethods.addToFavorites(v, ImageGalleryActivity.this, mUserEndPoint,
                                projectId);*/
                        UtilityMethods.addFavoriteToServer(v, ImageGalleryActivity.this, mUserEndPoint,
                                projectId);
                    }
                    /**
                     * We created 2 broadcast reciever for map and list, because if we are updating from list
                     * view then we need to update map broadcast and if we updating from map then we need to update
                     * list broadcast. Reason behind that if we do notifydatasetchange from list view then it refresh all
                     * view which results a flickering view of list view
                     */
                    Intent mapIntent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE_MAP);
                    mapIntent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,projectId);
                    sendBroadcast(mapIntent);

                    Intent listIntent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE);
                    listIntent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,projectId);
                    sendBroadcast(listIntent);
                    UtilityMethods.saveBooleanInPref(ImageGalleryActivity.this,Constants.MORE_FRAGMENT_UPDATE,true);
                } else {
                    HousingApplication.mLoginSuccessCallback = ImageGalleryActivity.this;
                    startActivity(new Intent(ImageGalleryActivity.this, LoginActivity.class));
                }
            }
        });

        if(!UtilityMethods.isInternetConnected(this)){
            showNetworkErrorSnackBar();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return imageZoomHelper.onDispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public void isLoggedin() {
        TrackAnalytics.trackEvent("ImageGalleryActivity_Fav_click", Constants.AppConstants.HOLD_TIME ,
                1, this);
        favoriteFAB.setImageDrawable(ContextCompat.getDrawable(ImageGalleryActivity.this,
                R.drawable.ic_favorite_white_24dp));

        //UtilityMethods.addToFavorites(rootLayout, ImageGalleryActivity.this, mUserEndPoint, projectId);
        UtilityMethods.addFavoriteToServer(rootLayout, ImageGalleryActivity.this, EndPointBuilder.getUserEndPoint(), projectId);

        /**
         * We created 2 broadcast reciever for map and list, because if we are updating from list
         * view then we need to update map broadcast and if we updating from map then we need to update
         * list broadcast. Reason behind that if we do notifydatasetchange from list view then it refresh all
         * view which results a flickering view of list view
         */
        Intent mapIntent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE_MAP);
        mapIntent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,projectId);
        sendBroadcast(mapIntent);

        Intent listIntent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE);
        listIntent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,projectId);
        sendBroadcast(listIntent);
    }

    private void buildRegService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }

    public void showNetworkErrorSnackBar() {
        UtilityMethods.showErrorSnackbarOnTop(this);
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
        TrackAnalytics.trackEvent("ImageGalleryActivity", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
