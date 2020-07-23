package com.clicbrics.consumer.view.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.Amenity;
import com.buy.housing.backend.propertyEndPoint.model.Bank;
import com.buy.housing.backend.propertyEndPoint.model.Builder;
import com.buy.housing.backend.propertyEndPoint.model.Document;
import com.buy.housing.backend.propertyEndPoint.model.DriveView;
import com.buy.housing.backend.propertyEndPoint.model.FloorPlan;
import com.buy.housing.backend.propertyEndPoint.model.GetProjectDetailResponse;
import com.buy.housing.backend.propertyEndPoint.model.Image;
import com.buy.housing.backend.propertyEndPoint.model.PaymentPlan;
import com.buy.housing.backend.propertyEndPoint.model.Project;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.buy.housing.backend.propertyEndPoint.model.Video;
import com.buy.housing.backend.userEndPoint.model.UpdateUserStatResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.EmiActivity;
import com.clicbrics.consumer.activities.ExploreNeighbourhood;
import com.clicbrics.consumer.activities.FilterActivity;
import com.clicbrics.consumer.activities.PaymentPlanActivity;
import com.clicbrics.consumer.activities.PropertyLayoutActivity;
import com.clicbrics.consumer.adapter.AmenitiesGridAdapter;
import com.clicbrics.consumer.adapter.FinanceAdapter;
import com.clicbrics.consumer.adapter.LayoutListAdapter;
import com.clicbrics.consumer.adapter.PaymentPlanListAdapter;
import com.clicbrics.consumer.adapter.ProjectDocumentsAdapter;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.animators.SlideInDownAnimator;
import com.clicbrics.consumer.customview.TextKnowMoreUtil;
import com.clicbrics.consumer.customview.TextReadMoreUtil;
import com.clicbrics.consumer.customview.imagestoryview.StoriesProgressView;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.helper.ProjectDetailsResultCallback;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.interfaces.OnFavoriteChangeListener;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.PropertyTypeSortUtil;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.viewmodel.ProjectDetailsViewModel;
import com.clicbrics.consumer.wrapper.ImageObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class ProjectDetailsScreen extends BaseActivity implements StoriesProgressView.StoriesListener,
        ViewPager.OnPageChangeListener, ProjectDetailsResultCallback, ProjectDetailImagePagerAdapter.OnImageTouchListener,
        LoginSuccessCallback, OnMapReadyCallback {

    private static final String TAG = "ProjectDetailsScreen";
    private final int STORAGE_PERMISSION = 105;
    private StoriesProgressView storiesProgressView;
    private ViewPager mViewPager;
    private int progressCounter = 0;
    private final long TIME_INTERVAL = 5000L;
    private boolean isNextOrPrev = false;
    private int imageCount = 0;
    private ProjectDetailsViewModel viewModel;
    //private ActivityProjectDetailsScreenBinding binding;
    private boolean isTantative = false, isCommercial= false, isOffer = false;
    private LoginFor loginFor;
    private LatLng mProjectLatlng;
    private String mProjectName;
    private long mProjectId;
    private long mCallUsMoreLastClicTime;
    private Project mProject;
    private List<String> coverImageList = new ArrayList<>();
    private LinkedHashMap<String, ArrayList<ImageObject>> galleryImageList = new LinkedHashMap<String, ArrayList<ImageObject>>();
    private LayoutListAdapter layoutListAdapter;
    private int bedIcon = -1;
    private ArrayList<Integer> areaList = new ArrayList<>();
    private ArrayList<Long> bspList = new ArrayList<>();
    private List<PropertyType> propertyTypeList = new ArrayList<>();
    private String priceRangeValue = "";
    private boolean isLand = false;
    private String projectStatus = "";
    private ImageView favoriteIconToolbar;
    private ImageView favoriteFAB,shareFAB;
    private ProjectDetailImagePagerAdapter mPagerAdapter;
    private GetProjectDetailResponse mDetailsResponse;
    private boolean isVirtualTourVisible = false;
    private List<String> propertyStatusList = new ArrayList<>();
    private boolean isAllPropertySoldOut = false;
    private DriveView mDriveViewObj, mDroneViewObj;
    private boolean isVirtualTourAvail = false;
    private List<Video> mVirtualTourVideoList;
    private enum VirtualTourType{
        indoorView, droneView, driveView
    }

    @Override
    public void isLoggedin() {
        if (loginFor.equals(LoginFor.FAVORITE_LISTING)) {
            TrackAnalytics.trackEvent("ProjectDetail_Mark_Fav", Constants.AppConstants.HOLD_TIME ,
                    1, this);
            favoriteFAB = findViewById(R.id.favorite_fab);
            favoriteIconToolbar = findViewById(R.id.favorite_collapsed);
            /*favoriteFAB.setImageDrawable(ContextCompat.getDrawable(ProjectDetailsScreen.this,
                    R.drawable.ic_favorite_white_24dp));
            favoriteIconToolbar.setImageDrawable(ContextCompat.getDrawable(ProjectDetailsScreen.this,
                    R.drawable.ic_favorite_white_24dp));*/
            favoriteFAB.setImageResource(R.drawable.ic_favorite_white_24dp);
            favoriteIconToolbar.setImageResource(R.drawable.heart_no_shadow_red);
            setTintColor(R.color.colorAccent);
            UtilityMethods.addFavoriteToServer(getRootView(), ProjectDetailsScreen.this, EndPointBuilder.getUserEndPoint(),
                    mProjectId);

            /**
             * We created 2 broadcast reciever for map and list, because if we are updating from list
             * view then we need to update map broadcast and if we updating from map then we need to update
             * list broadcast. Reason behind that if we do notifydatasetchange from list view then it refresh all
             * view which results a flickering view of list view
             */
            Intent mapIntent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE_MAP);
            mapIntent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,mProjectId);
            sendBroadcast(mapIntent);

            Intent listIntent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE);
            listIntent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,mProjectId);
            sendBroadcast(listIntent);

        } else if (loginFor.equals(LoginFor.CALL_ME_BACK)) {
            Intent intent = new Intent(ProjectDetailsScreen.this, FilterActivity.class);
            intent.putExtra(Constants.IntentKeyConstants.CALL_ME_BACK_REQUEST, true);
            intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, mProjectId);
            startActivity(intent);
        }
    }

    public void onClickOnShareBtn(View view) {
        if(mProject != null) {
            UtilityMethods.shareProject(this, mProjectName,
                    mProject.getSCoverImage() + "=w" + deviceWidth,
                    mProjectId);
        }
    }

    public void onClickOnFavoriteBtn(View view) {
        if(!UtilityMethods.isInternetConnected(this)){
            UtilityMethods.showErrorSnackbarOnTop(this);
            return;
        }
        favoriteFAB = findViewById(R.id.favorite_fab);
        favoriteIconToolbar = findViewById(R.id.favorite_collapsed);
        if (UtilityMethods.getLongInPref(this, Constants.ServerConstants.USER_ID, 0) != 0) {
            TrackAnalytics.trackEvent("ProjectDetail_Unmark_Fav", Constants.AppConstants.HOLD_TIME ,
                    1, this);
            Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(this, Constants.AppConstants.PROJECT_ID_SET);
            if ((favoriteList != null) && (!favoriteList.isEmpty())
                    && (favoriteList.contains(mProjectId+""))) {
                /*favoriteFAB.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.favorite_icon_default));
                favoriteIconToolbar.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.favorite_icon_default));*/
                favoriteFAB.setImageResource(R.drawable.ic_heart_white_with_shadow);
                favoriteIconToolbar.setImageResource(R.drawable.heart_no_shadow_gray);
                //favoriteIconToolbar.setColorFilter(ContextCompat.getColor(this, R.color.text_color_login_reg), android.graphics.PorterDuff.Mode.MULTIPLY);
                setTintColor(R.color.text_color_login_reg);
                UtilityMethods.removeFavoriteFromServer(getRootView(), this, EndPointBuilder.getUserEndPoint(),
                        mProjectId);
            } else {
                TrackAnalytics.trackEvent("ProjectDetail_Mark_Fav", Constants.AppConstants.HOLD_TIME ,
                        1, this);
                /*favoriteFAB.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.ic_favorite_white_24dp));
                favoriteIconToolbar.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.ic_favorite_white_24dp));*/
                favoriteFAB.setImageResource(R.drawable.ic_heart_red_filled_with_shadow);
                favoriteIconToolbar.setImageResource(R.drawable.heart_no_shadow_red);
                //favoriteIconToolbar.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                setTintColor(R.color.colorAccent);
                UtilityMethods.addFavoriteToServer(getRootView(), this, EndPointBuilder.getUserEndPoint(),
                        mProjectId);
            }
            /**
             * We created 2 broadcast reciever for map and list, because if we are updating from list
             * view then we need to update map broadcast and if we updating from map then we need to update
             * list broadcast. Reason behind that if we do notifydatasetchange from list view then it refresh all
             * view which results a flickering view of list view
             */
            Intent mapIntent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE_MAP);
            mapIntent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,mProjectId);
            sendBroadcast(mapIntent);

            Intent listIntent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE);
            listIntent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,mProjectId);
            sendBroadcast(listIntent);
            UtilityMethods.saveBooleanInPref(ProjectDetailsScreen.this,Constants.MORE_FRAGMENT_UPDATE,true);
        } else {
            HousingApplication.mLoginSuccessCallback = this;
            loginFor = LoginFor.FAVORITE_LISTING;
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private enum LoginFor {
        FAVORITE_LISTING, CALL_ME_BACK, BOOK_A_VISIT
    }

    private View getRootView(){
        return findViewById(R.id.detail_parent_layout);
    }

    private OnFavoriteChangeListener onFavoriteChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_project_details_screen);

        UtilityMethods.setStatusBarColor(this,R.color.overlay_color2);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //binding = DataBindingUtil.setContentView(this,R.layout.activity_project_details_screen);
        viewModel = new ProjectDetailsViewModel(this);
        initView();
        ViewCompat.setNestedScrollingEnabled(findViewById(R.id.id_container_layout), true);
        //final ImageView scrollViewIcon = findViewById(R.id.id_scroll_view_icon);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<10; i++){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewIcon.animate().translationY(40)
                                    .alpha(1)
                                    .setDuration(500)
                                    .setInterpolator(new OvershootInterpolator(0.5f))
                                    .start();
                        }
                    });
                }
            }
        }).start();*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            //    case R.id.menu_done:
            //        filter();
            //        return true;
            case android.R.id.home:
                if(getIntent() != null && getIntent().hasExtra(Constants.IntentKeyConstants.IS_DEEPLINK)
                        && getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_DEEPLINK,false)){
                    UtilityMethods.saveBooleanInPref(this,Constants.SharedPreferConstants.IS_USING_DL,false);
                    Intent intent = new Intent(this, HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    super.onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        favoriteIconToolbar = findViewById(R.id.favorite_collapsed);
        favoriteFAB = findViewById(R.id.favorite_fab);
        shareFAB = findViewById(R.id.share_fab);
        Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(this, Constants.AppConstants.PROJECT_ID_SET);
        if ((favoriteList != null) && (!favoriteList.isEmpty())) {
            if (favoriteList.contains(mProjectId+"")) {
                Log.d(TAG, "favoriteID->" + mProjectId);
                /*favoriteFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
                favoriteIconToolbar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));*/
                favoriteFAB.setImageResource(R.drawable.ic_heart_red_filled_with_shadow);
                favoriteIconToolbar.setImageResource(R.drawable.heart_no_shadow_red);
                //favoriteIconToolbar.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                setTintColor(R.color.colorAccent);
            } else {
                /*favoriteFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_default));
                favoriteIconToolbar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_default));*/
                favoriteFAB.setImageResource(R.drawable.ic_heart_white_with_shadow);
                favoriteIconToolbar.setImageResource(R.drawable.heart_no_shadow_gray);
                //favoriteIconToolbar.setColorFilter(ContextCompat.getColor(this, R.color.text_color_login_reg), android.graphics.PorterDuff.Mode.MULTIPLY);
                setTintColor(R.color.text_color_login_reg);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getIntent() != null && getIntent().hasExtra(Constants.IntentKeyConstants.IS_DEEPLINK)
                && getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_DEEPLINK,false)){
            UtilityMethods.saveBooleanInPref(this,Constants.SharedPreferConstants.IS_USING_DL,false);
            Intent intent = new Intent(this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            super.onBackPressed();
        }
    }

    private void initView(){
        setCollapsingToolbar();
        loadInitialView();
        Button callMeBackBtn = findViewById(R.id.contact_btn);
        callMeBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: of callMeBack Button");
                handleRequestLayoutPlan();
            }
        });

        TextView callUsForMore = findViewById(R.id.call_us_for_more);
        callUsForMore.setText(getString(R.string.call_us_for_more_info, UtilityMethods
                .getStringInPref(ProjectDetailsScreen.this,
                        Constants.AppConfigConstants.CONTACT_NUMBER, Constants.AppConstants.DEFAULT_NUMBER)));
        callUsForMore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i(TAG, "onTouch: Call Us For More Info");
                if (SystemClock.elapsedRealtime() - mCallUsMoreLastClicTime < 2000) {
                    return false;
                }else{
                    callUpdateStatAPI();
                }
                mCallUsMoreLastClicTime = SystemClock.elapsedRealtime();
                return false;
            }
        });

        if(getIntent().hasExtra(Constants.IntentKeyConstants.PROJECT_ID)){
            Log.i(TAG, "initView: ProjectId====="+getIntent().getLongExtra(Constants.IntentKeyConstants.PROJECT_ID,-1));
            long projectId = getIntent().getLongExtra(Constants.IntentKeyConstants.PROJECT_ID,-1);
            long userId = getIntent().getLongExtra(Constants.ServerConstants.USER_ID,-1);
            viewModel.getProjectDetails(projectId,userId);
        }
    }

    private void setCollapsingToolbar(){
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        final ImageView scrollViewIcon = findViewById(R.id.id_scroll_view_icon);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(TAG, "verticalOffset->" + verticalOffset);

                if (verticalOffset == 0) {
                    return;
                }
                scrollViewIcon.setVisibility(View.GONE);
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    findViewById(R.id.toolbar_collapsed_layout).setVisibility(View.VISIBLE);
                    UtilityMethods.setStatusBarColor(ProjectDetailsScreen.this,R.color.light_white);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
                    isShow = true;
                } else if (isShow) {
                    findViewById(R.id.toolbar_collapsed_layout).setVisibility(View.INVISIBLE);
                    UtilityMethods.setStatusBarColor(ProjectDetailsScreen.this,R.color.overlay_color2);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);
                    isShow = false;
                }
            }
        });

        favoriteIconToolbar = findViewById(R.id.favorite_collapsed);
        favoriteFAB = findViewById(R.id.favorite_fab);
        shareFAB = findViewById(R.id.share_fab);
        //favoriteFAB.hide();
        //shareFAB.hide();
        favoriteIconToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOnFavoriteBtn(favoriteIconToolbar);
            }
        });
        favoriteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.favouriteClick.toString());
                onClickOnFavoriteBtn(favoriteFAB);
            }
        });

        final ImageView shareToolbar = findViewById(R.id.share_collapsed);
        final ImageView shareFAB = findViewById(R.id.share_fab);
        shareToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOnShareBtn(shareToolbar);
            }
        });
        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.shareclick.toString());
                onClickOnShareBtn(shareFAB);
            }
        });
    }

    private void loadInitialView(){
        if ( getIntent().hasExtra(Constants.IntentKeyConstants.IMAGE_LIST)
                && getIntent().getStringArrayListExtra(Constants.IntentKeyConstants.IMAGE_LIST) != null
                && !getIntent().getStringArrayListExtra(Constants.IntentKeyConstants.IMAGE_LIST).isEmpty()) {
            List<String> imageList = getIntent().getStringArrayListExtra(Constants.IntentKeyConstants.IMAGE_LIST);
            if(imageList.size() > 10){
                coverImageList.addAll(imageList.subList(0,11));
            }else {
                coverImageList.addAll(imageList);
            }
            setCoverImageList();
        }
        if(getIntent().hasExtra(Constants.IntentKeyConstants.PROJECT_ID)){
            mProjectId = getIntent().getLongExtra(Constants.IntentKeyConstants.PROJECT_ID,-1);
        }
        if(getIntent().hasExtra(Constants.IntentKeyConstants.PROJECT_NAME)
            && !getIntent().getStringExtra(Constants.IntentKeyConstants.PROJECT_NAME).isEmpty()) {
            ((TextView)findViewById(R.id.id_project_name)).setText(getIntent().getStringExtra(Constants.IntentKeyConstants.PROJECT_NAME));
        }
        if(getIntent().hasExtra(Constants.IntentKeyConstants.BED_LIST)
                && !getIntent().getStringExtra(Constants.IntentKeyConstants.BED_LIST).isEmpty()) {
            ((TextView)findViewById(R.id.id_bedroom)).setText(getIntent().getStringExtra(Constants.IntentKeyConstants.BED_LIST));
        }
        if(getIntent().hasExtra(Constants.IntentKeyConstants.ADDRESS)
                && !getIntent().getStringExtra(Constants.IntentKeyConstants.ADDRESS).isEmpty()) {
            ((TextView)findViewById(R.id.id_address)).setText(getIntent().getStringExtra(Constants.IntentKeyConstants.ADDRESS));
        }
        if(getIntent().hasExtra(Constants.IntentKeyConstants.OFFER)
                && getIntent().getBooleanExtra(Constants.IntentKeyConstants.OFFER,false)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation scaleAnimation = AnimationUtils.loadAnimation(ProjectDetailsScreen.this, R.anim.fab_scale);
                    findViewById(R.id.offer_image).setVisibility(View.VISIBLE);
                    findViewById(R.id.offer_image).setAnimation(scaleAnimation);
                }
            }, 100);
        }
        if(getIntent().hasExtra(Constants.IntentKeyConstants.IS_COMMERCIAL)
                && getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_COMMERCIAL,false)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation scaleAnimation = AnimationUtils.loadAnimation(ProjectDetailsScreen.this, R.anim.fab_scale);
                    findViewById(R.id.commercial_tag).setVisibility(View.VISIBLE);
                    findViewById(R.id.commercial_tag).setAnimation(scaleAnimation);
                }
            }, 100);
        }
        String areaRange = "", status = "", priceRange = "",bspRange = "";
        if(getIntent().hasExtra(Constants.IntentKeyConstants.AREA_RANGE)
                && !getIntent().getStringExtra(Constants.IntentKeyConstants.AREA_RANGE).isEmpty()) {
            areaRange = getIntent().getStringExtra(Constants.IntentKeyConstants.AREA_RANGE);
            ((TextView)findViewById(R.id.id_area)).setText(areaRange);
        }
        if(getIntent().hasExtra(Constants.IntentKeyConstants.PROJECT_STATUS)
                && !getIntent().getStringExtra(Constants.IntentKeyConstants.PROJECT_STATUS).isEmpty()) {
            status = getIntent().getStringExtra(Constants.IntentKeyConstants.PROJECT_STATUS);
            if(!TextUtils.isEmpty(areaRange)){
                ((TextView)findViewById(R.id.id_project_status)).setText(" | "+status);
            }else{
                ((TextView)findViewById(R.id.id_project_status)).setText(" | "+status);
            }
        }
        if(getIntent().hasExtra(Constants.IntentKeyConstants.PRICE_RANGE)
                && !getIntent().getStringExtra(Constants.IntentKeyConstants.PRICE_RANGE).isEmpty()) {
            priceRange = getIntent().getStringExtra(Constants.IntentKeyConstants.PRICE_RANGE);
        }
        if(getIntent().hasExtra(Constants.IntentKeyConstants.PROJECT_BSP_RANGE)
                && !getIntent().getStringExtra(Constants.IntentKeyConstants.PROJECT_BSP_RANGE).isEmpty()) {
            bspRange = getIntent().getStringExtra(Constants.IntentKeyConstants.PROJECT_BSP_RANGE);
        }
        /*if(!areaRange.isEmpty() && !status.isEmpty()){
            ((TextView)findViewById(R.id.id_area)).setText(areaRange);
            ((TextView)findViewById(R.id.id_project_status)).setText( "  |  " + UtilityMethods.getPropertyStatus(status));
        }else if(!areaRange.isEmpty()){
            ((TextView)findViewById(R.id.id_area)).setText(areaRange);
        }else if(!status.isEmpty()){
            ((TextView)findViewById(R.id.id_project_status)).setText(UtilityMethods.getPropertyStatus(status));
        }*/

        if(priceRange.equalsIgnoreCase(getString(R.string.price_on_request))){
            setPriceOnRequestUI();
        }
        if(!priceRange.isEmpty() && !bspRange.isEmpty()){
            ((TextView)findViewById(R.id.id_price_n_bsp)).setText(priceRange + " | " + bspRange);
        }else if(!priceRange.isEmpty()){
            ((TextView)findViewById(R.id.id_price_n_bsp)).setText(priceRange);
        }else if(!bspRange.isEmpty()){
            ((TextView)findViewById(R.id.id_price_n_bsp)).setText(bspRange);
        }

        if(priceRange.equalsIgnoreCase(getString(R.string.price_on_request))){
            findViewById(R.id.id_emi_layout).setVisibility(View.GONE);
        }
    }

    private void callUpdateStatAPI(){
        final long userId = UtilityMethods.getLongInPref(ProjectDetailsScreen.this, Constants.ServerConstants.USER_ID, -1);
        final long virtualUID = UtilityMethods.getLongInPref(ProjectDetailsScreen.this, Constants.AppConstants.VIRTUAL_UID, -1);
        viewModel = new ProjectDetailsViewModel(this);
        viewModel.callUpdateStatAPI(userId,virtualUID);
    }

    private void setCoverImageList(){
        if(!coverImageList.isEmpty()){
            mViewPager = findViewById(R.id.id_image_viewpager);
            setupViewPager(coverImageList);
            mViewPager.setCurrentItem(0,false);
            if(mViewPager != null) {
                mViewPager.setOffscreenPageLimit(1);
                mViewPager.addOnPageChangeListener(this);
            }
            //mViewPager.setPageTransformer(false,new FadeViewPagerTransformer());

            storiesProgressView = findViewById(R.id.stories);
            imageCount = coverImageList.size();
            storiesProgressView.setStoriesCount(imageCount);
            storiesProgressView.setStoryDuration(TIME_INTERVAL);

            storiesProgressView.setStoriesListener(this);
            storiesProgressView.startStories();

            /*mViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            pressTime = System.currentTimeMillis();
                            storiesProgressView.pause();
                            return false;
                        case MotionEvent.ACTION_UP:
                            long now = System.currentTimeMillis();
                            storiesProgressView.resume();
                            return TIME_INTERVAL < now - pressTime;
                        case MotionEvent.ACTION_CANCEL:
                            long now2 = System.currentTimeMillis();
                            storiesProgressView.resume();
                            return TIME_INTERVAL < now2 - pressTime;
                    }
                    return false;
                }
            });*/
        }
    }

    private void setupViewPager(List<String> imageList) {
        mPagerAdapter = new ProjectDetailImagePagerAdapter(this,imageList);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onNext() {
        Log.i(TAG, "onNext: ");
        isNextOrPrev = true;
        mViewPager.setCurrentItem(++progressCounter,false);
    }

    @Override
    public void onPrev() {
        Log.i(TAG, "onPrev: ");
        if ((progressCounter - 1) < 0){
            return;
        }
        isNextOrPrev = true;
        mViewPager.setCurrentItem(--progressCounter,false);
    }

    @Override
    public void onComplete() {
        resetStoryProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(storiesProgressView != null) {
            storiesProgressView.destroy();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.i(TAG, "onPageSelected: position " + position);
        if(!isNextOrPrev){
            if(position > progressCounter){
                storiesProgressView.skip();
            }else{
                storiesProgressView.reverse();
            }
        }
        isNextOrPrev = false;
    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void resetStoryProgress(){
        storiesProgressView.resetStory();
        isNextOrPrev = false;
        progressCounter =0;
        mViewPager.setCurrentItem(0,false);
        storiesProgressView.setStoriesCount(imageCount);
        storiesProgressView.setStoryDuration(TIME_INTERVAL);
        storiesProgressView.startStories();
    }

    @Override
    public boolean isInternetConnected(boolean showError) {
        if(UtilityMethods.isInternetConnected(this)){
           return true;
        }else{
            if(showError) {
                UtilityMethods.showErrorSnackbarOnTop(this);
            }
            return false;
        }
    }
    @Override
    public void showLoader() {
        showProgressBar();
        //findViewById(R.id.indeterminate_progress).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        dismissProgressBar();
        //findViewById(R.id.indeterminate_progress).setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(GetProjectDetailResponse response) {
        Log.i(TAG, "onSuccess: " + response);
        //viewModel = new ProjectDetailsViewModel(response);
        //binding.setProjectDetailsModel(viewModel);
        //setCoverImageList();
        new EventAnalyticsHelper().logAPICallEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                null, Constants.ApiName.getProjectDetails.toString(),Constants.AnalyticsEvents.SUCCESS,
                null);
        setValues(response);
    }

    @Override
    public void onError(String errMsg) {
        new EventAnalyticsHelper().logAPICallEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                null, Constants.ApiName.getProjectDetails.toString(),Constants.AnalyticsEvents.FAILED,
                errMsg);
        if(UtilityMethods.getBooleanInPref(this,Constants.SharedPreferConstants.IS_USING_DL,false)){
            UtilityMethods.saveBooleanInPref(this,Constants.SharedPreferConstants.IS_USING_DL,false);
            Intent intent = new Intent(this, HomeScreen.class);
            intent.putExtra("InvalidProjectError",errMsg);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            //UtilityMethods.showSnackbarOnTop(this,"Error",errMsg,true,Constants.AppConstants.ALERTOR_LENGTH_LONG);
            showSnackBar(errMsg);
        }
    }

    @Override
    public void onSuccessUpdateStat(UpdateUserStatResponse response) {
        if(response != null && response.getUid() != null) {
            new EventAnalyticsHelper().logAPICallEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                    null, Constants.ApiName.updateStat.toString(),Constants.AnalyticsEvents.SUCCESS,
                    null);
            UtilityMethods.saveLongInPref(ProjectDetailsScreen.this, Constants.AppConstants.VIRTUAL_UID, response.getUid());
        }
    }

    int updateStatRetryCount = 0;
    @Override
    public void onErrorUpdateStat(String errMsg) {
        new EventAnalyticsHelper().logAPICallEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                null, Constants.ApiName.updateStat.toString(),Constants.AnalyticsEvents.FAILED,
                errMsg);
        if(updateStatRetryCount <= 1){
            updateStatRetryCount++;
            final long userId = UtilityMethods.getLongInPref(ProjectDetailsScreen.this, Constants.ServerConstants.USER_ID, -1);
            final long virtualUID = UtilityMethods.getLongInPref(ProjectDetailsScreen.this, Constants.AppConstants.VIRTUAL_UID, -1);
            viewModel.callUpdateStatAPI(userId,virtualUID);
        }
    }

    @Override
    public void onActionDown() {
        storiesProgressView.pause();
    }

    @Override
    public void onActionUp() {
        storiesProgressView.resume();
    }

    @Override
    public void onActionCancel() {
        storiesProgressView.resume();
    }

    private void setValues(GetProjectDetailResponse response){

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation scaleAnimation = AnimationUtils.loadAnimation(ProjectDetailsScreen.this, R.anim.fab_scale);
                favoriteFAB.show();
                favoriteFAB.setAnimation(scaleAnimation);
            }
        }, 1);*/


        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation scaleAnimation = AnimationUtils.loadAnimation(ProjectDetailsScreen.this, R.anim.fab_scale);
                shareFAB.show();
                shareFAB.setAnimation(scaleAnimation);

            }
        }, 250);*/

        if(response != null) {
            mDetailsResponse = response;
            if(response.getProject() != null){
                mProject = response.getProject();
                mProjectId = mProject.getId();
                mProjectName = mProject.getName();
                mProjectLatlng = new LatLng(mProject.getLat(),mProject.getLng());

            }

            ((TextView)findViewById(R.id.id_project_name)).setText(mProjectName);
            //ImageView reraIcon = findViewById(R.id.id_rera_icon);
            TextView reraId = findViewById(R.id.id_rera_id);
            if(mProject != null && !TextUtils.isEmpty(mProject.getReraId())){
                reraId.setVisibility(View.VISIBLE);
                reraId.setText("RERA ID: "+mProject.getReraId());
                /*reraIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View.OnClickListener positiveListener = new View.OnClickListener() {

                            @Override
                            public void onClick(View dialog) {
                                if (mCustomDialog != null && !isFinishing() && !isDestroyed())
                                    mCustomDialog.dismiss();
                            }
                        };
                        mCustomDialog = UtilityMethods.showAlert(ProjectDetailsScreen.this, R.string.rera_id, mProject.getReraId(), true, R.string.ok, positiveListener, -1, null, null);
                    }
                });*/
            }else{
                reraId.setVisibility(View.GONE);
            }
            setAddress(mProject);

            final TextView collapsed_title = findViewById(R.id.toolbar_collapsed_title);
            collapsed_title.setText(mProjectName);

            String status = mProject.getProjectStatus() != null ? mProject.getProjectStatus() : "";
            projectStatus = UtilityMethods.getPropertyStatus(status);

            if(response.getPropertyTypeList() != null){
                propertyTypeList = response.getPropertyTypeList();
                isAllPropertySoldOut = getPropertySoldOutStatus();
                PropertyTypeSortUtil sortUtil = new PropertyTypeSortUtil();
                propertyTypeList = sortUtil.getSortedProperty(propertyTypeList);
                ((HousingApplication)getApplicationContext()).setPropertyTypeList(propertyTypeList);
                setBedAreaPriceNBSPValue(propertyTypeList);
                setEMIValue(propertyTypeList,mProject);
                createVirtualTourList();
                if(isAllPropertySoldOut) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation scaleAnimation = AnimationUtils.loadAnimation(ProjectDetailsScreen.this, R.anim.fab_scale);
                            findViewById(R.id.id_soldout_tag).setVisibility(View.VISIBLE);
                            findViewById(R.id.id_soldout_tag).setAnimation(scaleAnimation);
                        }
                    }, 1);
                }
            }

            if(mProject.getDriveView() != null){
                mDriveViewObj = mProject.getDriveView();
            }

            if(mProject.getDroneView() != null){
                mDroneViewObj = mProject.getDroneView();
            }

            if(isVirtualTourVisible || mDriveViewObj != null || mDroneViewObj != null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation scaleAnimation = AnimationUtils.loadAnimation(ProjectDetailsScreen.this, R.anim.fab_scale);
                        findViewById(R.id.id_virtual_tour_icon).setVisibility(View.VISIBLE);
                        findViewById(R.id.id_virtual_tour_icon).setAnimation(scaleAnimation);
                    }
                }, 1);
                findViewById(R.id.id_virtual_tour_icon).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(true){
                            return;
                        }
                        final AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
                        final NestedScrollView nestedScrollView = findViewById(R.id.id_project_detail_scrollview);
                        final LinearLayout virtualTourLayout = findViewById(R.id.id_virtual_tour_layout);
                        final int scrollTo = virtualTourLayout.getTop();
                        //Log.i(TAG, "onClick: scrollTo " + scrollTo);
                        appBarLayout.setExpanded(false,true);
                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            @Override
                            public void onOffsetChanged(AppBarLayout appBar, int verticalOffset) {
                                if((appBar.getTotalScrollRange()+verticalOffset) == 0 ){
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.i(TAG, "onClick: scrollTo " + scrollTo);
                                            nestedScrollView.smoothScrollTo(0, scrollTo);
                                        }
                                    },100);
                                    appBarLayout.removeOnOffsetChangedListener(this);
                                }
                            }
                        });
                    }
                });
            }

            if (coverImageList.isEmpty()) {
                if(mProject != null && !TextUtils.isEmpty(mProject.getSCoverImage())){
                    coverImageList.add(mProject.getSCoverImage());
                }
                addCoverImageList(propertyTypeList,mProject);
                setCoverImageList();
            }
            /*if ( getIntent().hasExtra(Constants.IntentKeyConstants.IMAGE_LIST)
                    && !getIntent().getStringArrayListExtra(Constants.IntentKeyConstants.IMAGE_LIST).isEmpty()) {
                coverImageList.addAll(getIntent().getStringArrayListExtra(Constants.IntentKeyConstants.IMAGE_LIST));
            }else{
                if(mProject != null && !TextUtils.isEmpty(mProject.getSCoverImage())){
                    coverImageList.add(mProject.getSCoverImage());
                }
                addCoverImageList(propertyTypeList,mProject);
            }
            setCoverImageList();*/


            if(mProject != null){
                setOfferView(mProject);
            }
            getCommericalView(propertyTypeList);

            if(isOffer){
                findViewById(R.id.offer_image).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.offer_image).setVisibility(View.GONE);
            }

            if(isCommercial){
                findViewById(R.id.commercial_tag).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.commercial_tag).setVisibility(View.GONE);
            }
            //findViewById(R.id.offer_image).setVisibility(isOffer ? View.VISIBLE:View.GONE);
            //findViewById(R.id.commercial_tag).setVisibility(isCommercial ? View.VISIBLE:View.GONE);

            if(mProject != null && mProject.getTentative() != null) {
                isTantative = mProject.getTentative();
            }

            set3DView();

            if(mProject != null && mProject.getProjectSummary() != null) {
                setAboutProject(mProject.getProjectSummary());
            }

            if(response.getBuilder() != null){
                setDeveloperView(response.getBuilder());
            }
            else
            {
                LinearLayout developerViewLayout = findViewById(R.id.developer_layout);
                developerViewLayout.setVisibility(View.GONE);
            }

            if(mProject != null){
                setAmenityList(mProject.getAmenities());
            }

            setLayoutList(propertyTypeList, mProjectId);

            if(mProjectName != null && mProjectLatlng != null) {
                setExploreArea();
            }

            if(mProject != null) {
                setFinancingList(mProject.getBankLoan(), propertyTypeList);
            }

            if(mProject != null) {
                setPaymentPlanList((ArrayList<PaymentPlan>) mProject.getPaymentPlanList());
            }

            setPropertyScores(mProject);

            if(mProject != null && mProject.getSCoverImage() != null) {
                setProjectDocumentList(mProject.getSCoverImage(),mProject.getProjectbrowcherPdfURL(), mProject.getDocuments());
            }
            addToRecentView(response);
            setGalleryImageList(response);
        }

    }

    private void addCoverImageList(List<PropertyType> propertyTypeList,Project project){
        if(project != null){
            if(project.getImages() != null && !project.getImages().isEmpty()){
                if (project.getImages() != null && !project.getImages().isEmpty()) {
                    for (int k = 0; k < project.getImages().size(); k++) {
                        Image image = project.getImages().get(k);
                        if (image != null && !TextUtils.isEmpty(image.getSURL())) {
                            coverImageList.add(image.getSURL());
                        }
                    }
                }
            }
            if(project.getConstructionImages() != null && !project.getConstructionImages().isEmpty()){
                for(int i=0; i<project.getConstructionImages().size(); i++){
                    Image image = project.getConstructionImages().get(i);
                    if (image != null && !TextUtils.isEmpty(image.getSURL())) {
                        coverImageList.add(image.getSURL());
                    }
                }
            }
            if(project.getAmenityImages() != null && !project.getAmenityImages().isEmpty()){
                for(int i=0; i<project.getAmenityImages().size(); i++){
                    Image image = project.getAmenityImages().get(i);
                    if (image != null && !TextUtils.isEmpty(image.getSURL())) {
                        coverImageList.add(image.getSURL());
                    }
                }
            }
            if(project.getNeighbourImages() != null && !project.getNeighbourImages().isEmpty()){
                for(int i=0; i<project.getNeighbourImages().size(); i++){
                    Image image = project.getNeighbourImages().get(i);
                    if (image != null && !TextUtils.isEmpty(image.getSURL())) {
                        coverImageList.add(image.getSURL());
                    }
                }
            }
            if(project.getSitePlan() != null){
                if(!TextUtils.isEmpty(project.getSitePlan().getSurlImage())){
                    coverImageList.add(project.getSitePlan().getSurlImage());
                }
                if(!TextUtils.isEmpty(project.getSitePlan().getSurl3d())){
                    coverImageList.add(project.getSitePlan().getSurl3d());
                }
                if(!TextUtils.isEmpty(project.getSitePlan().getSurl2d())){
                    coverImageList.add(project.getSitePlan().getSurl2d());
                }
            }
            if(project.getLocationPlan() != null){
                if(!TextUtils.isEmpty(project.getLocationPlan().getSurlImage())){
                    coverImageList.add(project.getLocationPlan().getSurlImage());
                }
                if(!TextUtils.isEmpty(project.getLocationPlan().getSurl3d())){
                    coverImageList.add(project.getLocationPlan().getSurl3d());
                }
                if(!TextUtils.isEmpty(project.getLocationPlan().getSurl2d())){
                    coverImageList.add(project.getLocationPlan().getSurl2d());
                }
            }
            if(project.getClusterPlan() != null){
                for(int i=0; i<project.getClusterPlan().size(); i++){
                    FloorPlan floorPlan = project.getClusterPlan().get(i);
                    if(floorPlan != null){
                        if(!TextUtils.isEmpty(floorPlan.getSurlImage())){
                            coverImageList.add(floorPlan.getSurlImage());
                        }
                        if(!TextUtils.isEmpty(floorPlan.getSurl3d())){
                            coverImageList.add(floorPlan.getSurl3d());
                        }
                        if(!TextUtils.isEmpty(floorPlan.getSurl2d())){
                            coverImageList.add(floorPlan.getSurl2d());
                        }
                    }
                }
            }
        }
        if(propertyTypeList != null && !propertyTypeList.isEmpty()){
            for(int i=0; i<propertyTypeList.size(); i++){
                PropertyType propertyType = propertyTypeList.get(i);
                if(propertyType != null) {
                    if (propertyType.getImages() != null && !propertyType.getImages().isEmpty()) {
                        for (int k = 0; k < propertyType.getImages().size(); k++) {
                            Image image = propertyType.getImages().get(k);
                            if (image != null && !TextUtils.isEmpty(image.getSURL())) {
                                coverImageList.add(image.getSURL());
                            }
                        }
                    }

                    if(propertyType.getFloorPlan() != null){
                        if(!TextUtils.isEmpty(propertyType.getFloorPlan().getSurlImage())){
                            coverImageList.add(propertyType.getFloorPlan().getSurlImage());
                        }
                        if(!TextUtils.isEmpty(propertyType.getFloorPlan().getSurl3d())){
                            coverImageList.add(propertyType.getFloorPlan().getSurl3d());
                        }
                        if(!TextUtils.isEmpty(propertyType.getFloorPlan().getSurl2d())){
                            coverImageList.add(propertyType.getFloorPlan().getSurl2d());
                        }
                    }
                }
            }
        }
        if(coverImageList.size() > 10){
            coverImageList = coverImageList.subList(0,11);
        }
    }

    private String setBedAreaPriceNBSPValue(List<PropertyType> propertyTypeList) {
        String bedNPropertyType = "";
        if(propertyTypeList != null){
            int listSize = propertyTypeList.size();
            boolean hasApartment = false, hasVilla = false, hasFloor = false, hasPlot = false, hasCommercial = false, hasCommercialLand = false;
            Set<Integer> bedRoomSet = new TreeSet<>();
            Set<Integer> areaSet = new TreeSet<>();
            Set<Long> priceSet = new TreeSet<>();
            Set<Long> bspSet = new TreeSet<>();
            List<String> typeList = new ArrayList<>();
            String commercialTypeStr = "";

            for(int i=0; i<listSize ; i++){
                PropertyType propertyType = propertyTypeList.get(i);
                if(propertyType != null){
                    boolean isPropertySold = false;
                    if(propertyType.getSoldStatus() != null && propertyType.getSoldStatus() && !isAllPropertySoldOut){
                        isPropertySold = true;
                    }
                    if(!isPropertySold) {
                        if (propertyType.getNumberOfBedrooms() != null && propertyType.getNumberOfBedrooms() != 0) {
                            bedRoomSet.add(propertyType.getNumberOfBedrooms());
                        }
                        if (propertyType.getSuperArea() != null) {
                            areaSet.add(propertyType.getSuperArea());
                        }
                        if (propertyType.getSuperArea() != null && propertyType.getBsp() != null) {
                            priceSet.add(propertyType.getBsp() * propertyType.getSuperArea());
                        }
                        if (propertyType.getBsp() != null) {
                            bspSet.add(propertyType.getBsp());
                        }
                        if (!TextUtils.isEmpty(propertyType.getStatus())) {
                            propertyStatusList.add(propertyType.getStatus());
                        }

                        //This is for recent list, for details we have to show vt irrespective of property unit sold out but in list we have to show
                        //only for those property unit which have not sold out.
                        if(propertyType.getVirtualTour() != null){
                            isVirtualTourAvail = true;
                        }
                        if (UtilityMethods.hasApartment(propertyType.getType())) {
                            hasApartment = true;
                        } else if (UtilityMethods.hasVilla(propertyType.getType())) {
                            hasVilla = true;
                        } else if (propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentFloor.toString())) {
                            hasFloor = true;
                        } else if (propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.Land.toString())) {
                            hasPlot = true;
                        } else if (UtilityMethods.isCommercial(propertyType.getType())) {
                            //commercialTypeStr = UtilityMethods.getCummercialTypeName(propertyType.getType());
                            typeList.add(propertyType.getType());
                            if (UtilityMethods.isCommercialLand(propertyType.getType())) {
                                hasCommercialLand = true;
                            } else {
                                hasCommercial = true;
                            }
                        }
                    }
                }
            }
            String propertyTypeStr ="";
            if(hasApartment){
                propertyTypeStr = Constants.AppConstants.PropertyType.Apartment.toString();
            }else if(hasVilla){
                propertyTypeStr = Constants.AppConstants.PropertyType.Villa.toString();
            }else if(hasFloor){
                propertyTypeStr = "Independent Floor";
            }else if(hasPlot){
                propertyTypeStr = "Plot";
                isLand = true;
            }else if(hasCommercial || hasCommercialLand){
                if(typeList != null){
                    commercialTypeStr = UtilityMethods.getCommercialTypeName(typeList);
                }
                if(hasCommercialLand){
                    isLand = true;
                }
                propertyTypeStr = commercialTypeStr;
            }

            if(bedRoomSet != null && !bedRoomSet.isEmpty()){
                String bedStr = "";
                int count = bedRoomSet.size();
                for(Integer bed : bedRoomSet){
                    count--;
                    if(count != 0){
                        bedStr = bedStr + bed + ", ";
                    }else{
                        bedStr = bedStr + bed + " BHK";
                    }
                }
                if(!TextUtils.isEmpty(bedStr)) {
                    bedStr = bedStr + " " + propertyTypeStr;
                }
                bedNPropertyType =  bedStr;
            }else{
                bedNPropertyType = propertyTypeStr;
            }

            if(priceSet != null && !priceSet.isEmpty()){
                String price = "";
                ArrayList<Long> priceList = new ArrayList<>(priceSet);
                if(priceList.size() == 1){
                    if(priceList.get(0) != 0){
                        price = UtilityMethods.getPriceWord(priceList.get(0));
                    }else{
                        price = getString(R.string.price_on_request);
                    }
                }else{
                    price = "\u20B9" + " " + UtilityMethods.getPriceWordWithoutSymbol(priceList.get(0)) + " - "
                            + UtilityMethods.getPriceWordWithoutSymbol(priceList.get(priceList.size() - 1));
                }
                priceRangeValue = price;
            }
            if(areaSet != null && !areaSet.isEmpty()){
                areaList = new ArrayList<>(areaSet);
                setAreaRange();
            }
            if(bspSet != null && !bspSet.isEmpty()){
                bspList =  new ArrayList<>(bspSet);
                setBSPRange();
            }

            if(hasApartment && (hasVilla || hasPlot || hasCommercial)){
                bedIcon = R.drawable.bed_generic_icon;
            }else if(hasPlot && !hasApartment && !hasVilla){
                bedIcon = R.drawable.plot_detail_icon;
            }else if(hasCommercial && !hasApartment && !hasVilla && !hasPlot){
                bedIcon = R.drawable.ic_shop_selected;
            }else{
                bedIcon = R.drawable.bed_icon;
            }
        }
        ((TextView)findViewById(R.id.id_bedroom)).setText(bedNPropertyType);
        ((TextView)findViewById(R.id.id_bedroom)).setCompoundDrawablesWithIntrinsicBounds(bedIcon, 0, 0, 0);
        return bedNPropertyType;
    }

    private void setAreaRange(){
        String area = "";
        if(!areaList.isEmpty()) {
            if (areaList.size() == 1) {
                if(areaList.get(0) != 0){
                    if (isLand) {
                        area = UtilityMethods.getArea(this, areaList.get(0), true)
                                + " " + UtilityMethods.getUnit(this, true);
                    } else {
                        area = UtilityMethods.getArea(this, areaList.get(0), false)
                                + " " + UtilityMethods.getUnit(this, false);
                    }
                }
            } else {
                int minArea = areaList.get(0);
                int maxArea = areaList.get(areaList.size() - 1);
                if (isLand) {
                    area = UtilityMethods.getArea(this, minArea, true)
                            + " - " + UtilityMethods.getArea(this, maxArea, true)
                            + " " + UtilityMethods.getUnit(this, true);
                } else {
                    area = UtilityMethods.getArea(this, minArea, false)
                            + " - " + UtilityMethods.getArea(this, maxArea, false)
                            + " " + UtilityMethods.getUnit(this, false);
                }
            }
        }
        LinearLayout layout = findViewById(R.id.id_area_n_status_layout);
        TextView areaTextview = layout.findViewById(R.id.id_area);
        TextView statusText = layout.findViewById(R.id.id_project_status);
        areaTextview.measure(0,0);
        if(!area.isEmpty() && !projectStatus.isEmpty()){
            layout.setVisibility(View.VISIBLE);
            areaTextview.setText(area+"");
            statusText.setText("  |  " + UtilityMethods.getPropertyStatus(projectStatus));
        }else if(!area.isEmpty()){
            layout.setVisibility(View.VISIBLE);
            areaTextview.setText(area);
        }else if(!projectStatus.isEmpty()){
            layout.setVisibility(View.VISIBLE);
            statusText.setText(" "+UtilityMethods.getPropertyStatus(projectStatus));
        }
        final String finalArea = area;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(finalArea)) {
                    showUnitPopupWindow();
                }
            }
        });

    }

    private void setBSPRange(){
        String bsp = "";
        if(!bspList.isEmpty()) {
            if (bspList.size() == 1) {
                if (bspList.get(0) != 0) {
                    if (isLand) {
                        String bspValue = UtilityMethods.getBSP(this, bspList.get(0), true);
                        String unit = UtilityMethods.getUnit(this, true);
                        bsp = "BSP \u20B9" + bspValue + unit;
                    } else {
                        String bspValue = UtilityMethods.getBSP(this, bspList.get(0), false);
                        String unit = UtilityMethods.getUnit(this, false);
                        bsp = "BSP \u20B9" + bspValue + unit;
                    }
                }
            } else {
                if (isLand) {
                    String bspMin = UtilityMethods.getBSP(this, bspList.get(0), true);
                    String bspMax = UtilityMethods.getBSP(this, bspList.get(bspList.size() - 1), true);
                    String unit = UtilityMethods.getUnit(this, true);
                    bsp = "BSP \u20B9" + bspMin + " - " + bspMax + unit;
                } else {
                    String bspMin = UtilityMethods.getBSP(this, bspList.get(0), true);
                    String bspMax = UtilityMethods.getBSP(this, bspList.get(bspList.size() - 1), true);
                    String unit = UtilityMethods.getUnit(this, true);
                    bsp = "BSP \u20B9" + bspMin + " - " + bspMax + unit;
                }
            }
        }
        TextView priceNBSPText = findViewById(R.id.id_price_n_bsp);
        if(bsp.isEmpty()){
            priceNBSPText.setText(getString(R.string.price_on_request));
            setPriceOnRequestUI();
        }else{
            priceNBSPText.setText(priceRangeValue + "  |   " + bsp);
        }

    }

    private void setAddress(Project project){
        if(project == null){
            return;
        }
        String address = "";
        if(project.getAddress() != null){
            address = project.getAddress();
        }
        if(project.getCity() != null){
            address = address + ", " + project.getCity();
        }
        ((TextView)findViewById(R.id.id_address)).setText(address);
    }

    private String setEMIValue(final List<PropertyType> propertyList, Project project){
        String emiStr = "";
        float defaultInterestRate = UtilityMethods.getFloatInPref(this, Constants.AppConfigConstants.DEFAULT_INTEREST_RATE, 8.60f);
        Bank bank = null;
        if(project != null && project.getBankLoan() != null && !project.getBankLoan().isEmpty()){
            ArrayList<Float> interestList = new ArrayList<>();
            ArrayList<Bank> bankLoanList = new ArrayList<>();
            bankLoanList.addAll(project.getBankLoan());
            for (int i = 0; i < project.getBankLoan().size(); i++) {
                if (project.getBankLoan().get(i) != null && project.getBankLoan().get(0).getInterest()!= null) {
                    float interestRate = project.getBankLoan().get(0).getInterest();
                    interestList.add(interestRate);
                }
            }
            Collections.sort(interestList);
            Collections.sort(bankLoanList, new Comparator<Bank>() {
                @Override
                public int compare(Bank bank1, Bank bank2) {
                    if(bank1.getInterest() != null && bank2.getInterest() != null) {
                        return Float.compare(bank1.getInterest(), bank2.getInterest());
                    }else{
                        return 0;
                    }
                }
            });
            bank = bankLoanList.get(0);
            defaultInterestRate = interestList.get(0);
        }
        com.clicbrics.consumer.wrapper.Bank bankWrapper= null;
        if(bank != null){
            if(bank.getInterest() != null && bank.getInterest() <= defaultInterestRate){
                defaultInterestRate = bank.getInterest();
                bankWrapper = new com.clicbrics.consumer.wrapper.Bank();
                if(bank.getId() != null){
                    bankWrapper.setId(bank.getId());
                }
                if(bank.getOffersList() != null){
                    bankWrapper.setOffersList(bank.getOffersList());
                }
                if(bank.getInterest() != null){
                    bankWrapper.setInterest(bank.getInterest());
                }
                if(bank.getName() != null){
                    bankWrapper.setName(bank.getName());
                }
                if(bank.getLogo() != null){
                    bankWrapper.setLogo(bank.getLogo());
                }
                if(bank.getInterestType() != null){
                    bankWrapper.setInterestType(bank.getInterestType());
                }
                if(bank.getContactList() != null){
                    bankWrapper.setContactList(bank.getContactList());
                }
            }
        }
        final float finalInterestRate = defaultInterestRate;
        final ArrayList<Double> emiList = new ArrayList();
        if(propertyList != null) {
            for (int i = 0; i < propertyList.size(); i++) {
                boolean isPropertySold = false;
                PropertyType propertyType = propertyList.get(i);
                if(propertyType != null) {
                    if (!isAllPropertySoldOut && propertyType.getSoldStatus() != null
                            && propertyType.getSoldStatus()) {
                        isPropertySold = true;
                    }
                    if (!isPropertySold && ((propertyType.getSuperArea() != null) && (propertyType.getSuperArea() != 0)) &&
                            ((propertyType.getBsp() != null) && (propertyType.getBsp() != 0))) {
                        double emiAmount = calculateEmi(propertyType.getType(),
                                propertyType.getSuperArea() * propertyType.getBsp(), finalInterestRate);
                        if (emiAmount != 0) {
                            emiList.add(emiAmount);
                        }
                    }
                }
            }
        }

        if ((emiList != null) && (!emiList.isEmpty())) {
            Collections.sort(emiList);
            emiStr = "EMI starts from " + UtilityMethods.getPriceWordTillThousand(emiList.get(0)) + " per month";
        }
        LinearLayout emiLayout = findViewById(R.id.id_emi_layout);
        if(!TextUtils.isEmpty(emiStr)) {
            ((TextView) findViewById(R.id.id_emistr)).setText(emiStr);
            emiLayout.setVisibility(View.VISIBLE);
        }else{
            emiLayout.setVisibility(View.GONE);
        }
        final com.clicbrics.consumer.wrapper.Bank finalBankWrapper = bankWrapper;
        emiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmiActivity(propertyList,emiList, finalInterestRate, finalBankWrapper);
            }
        });
        return emiStr;
    }

    private static double calculateEmi(String type, long totalAmount, float interestRate) {
        double downPayment;
        if (type.equalsIgnoreCase(Constants.AppConstants.PropertyType.Land.toString())) {
            downPayment = (totalAmount) * (0.40);
        } else {
            downPayment = (totalAmount) * (0.20);
        }
        double principleAmount = totalAmount - downPayment;
        double rate = interestRate / (12 * 100); /*one month interest*/
        double time = 20 * 12; /*one month period*/
        return (principleAmount * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);
    }

    private void showEmiActivity(List<PropertyType> propertyTypeList, ArrayList<Double> emiList,
                                        float finalInterestRate, com.clicbrics.consumer.wrapper.Bank bankWrapper){
        if(propertyTypeList != null && propertyTypeList.size() > 0){
            if(propertyTypeList.size() == 1){
                Intent intent = new Intent(this, EmiActivity.class);
                long housePrice = propertyTypeList.get(0).getBsp() * propertyTypeList.get(0).getSuperArea();
                intent.putExtra(Constants.IntentKeyConstants.IS_FROM_DETAIL_LINK, true);      //flag to evaluate from where we are calling this activity.
                if(bankWrapper != null){
                    Gson gson = new Gson();
                    String json = gson.toJson(bankWrapper);
                    intent.putExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL, true);
                    intent.putExtra(Constants.IntentKeyConstants.BANK_DETAILS, json);
                }else{
                    intent.putExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL, false);
                }
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_VALUE, housePrice);
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_ID, propertyTypeList.get(0).getId());
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, propertyTypeList.get(0).getProjectId());
                intent.putExtra(Constants.IntentKeyConstants.TYPE, propertyTypeList.get(0).getType());
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_SIZE, propertyTypeList.get(0).getSuperArea());
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_TYPE, propertyTypeList.get(0).getNumberOfBedrooms().intValue());
                startActivity(intent);
            }else{
                for(int i=0; i< propertyTypeList.size(); i++){
                    double emiAmount = calculateEmi(propertyTypeList.get(i).getType(),
                            propertyTypeList.get(i).getSuperArea() * propertyTypeList.get(i).getBsp(), finalInterestRate);
                    if(emiAmount == emiList.get(0)){
                        Intent intent = new Intent(this, EmiActivity.class);
                        long housePrice = propertyTypeList.get(i).getBsp() * propertyTypeList.get(i).getSuperArea();
                        intent.putExtra(Constants.IntentKeyConstants.IS_FROM_DETAIL_LINK, true);      //flag to evaluate from where we are calling this activity.
                        if(bankWrapper != null){
                            Gson gson = new Gson();
                            String json = gson.toJson(bankWrapper);
                            intent.putExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL, true);
                            intent.putExtra(Constants.IntentKeyConstants.BANK_DETAILS, json);
                        }else{
                            intent.putExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL, false);
                        }
                        intent.putExtra(Constants.IntentKeyConstants.PROPERTY_VALUE, housePrice);
                        intent.putExtra(Constants.IntentKeyConstants.PROPERTY_ID, propertyTypeList.get(i).getId());
                        intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, propertyTypeList.get(i).getProjectId());
                        intent.putExtra(Constants.IntentKeyConstants.TYPE, propertyTypeList.get(i).getType());
                        intent.putExtra(Constants.IntentKeyConstants.PROPERTY_SIZE, propertyTypeList.get(i).getSuperArea());
                        intent.putExtra(Constants.IntentKeyConstants.PROPERTY_TYPE, propertyTypeList.get(i).getNumberOfBedrooms().intValue());
                        startActivity(intent);
                        break;
                    }else{
                        continue;
                    }
                }
            }
        }
    }

    private void set3DView(){
        if(isVirtualTourVisible || mDriveViewObj != null || mDroneViewObj != null){
            findViewById(R.id.id_virtual_tour_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.id_virtual_tour_sep).setVisibility(View.VISIBLE);
            //findViewById(R.id.ic_virtual_tour_scrollview).setPadding(UtilityMethods.dpToPx(16), 0, UtilityMethods.dpToPx(16), 0);
            if(mVirtualTourVideoList != null && mVirtualTourVideoList.size() > 0){
                Video video = mVirtualTourVideoList.get(0);
                if(video != null){
                    if(!TextUtils.isEmpty(video.getThumb())) {
                        Picasso.get().load(video.getThumb())
                                .config(Bitmap.Config.ARGB_8888)
                                .placeholder(R.drawable.indoor_view_placeholder)
                                .into((ImageView)findViewById(R.id.id_3d_view_thumb_img));
                    }
                    findViewById(R.id.id_3d_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mVirtualTourVideoList.size() == 1){
                                if(!UtilityMethods.isInternetConnected(ProjectDetailsScreen.this)){
                                    UtilityMethods.showErrorSnackbarOnTop(ProjectDetailsScreen.this);
                                    return;
                                }
                                Intent intent = new Intent(ProjectDetailsScreen.this,VirtualTourViewActivity.class);
                                intent.putExtra("URL",mVirtualTourVideoList.get(0).getUrl().trim());
                                intent.putExtra("Title",mVirtualTourVideoList.get(0).getName().trim());
                                startActivity(intent);
                            }else{
                                showVirtualTourViewListDialog(mVirtualTourVideoList);
                            }
                        }
                    });
                }
            }else{
                findViewById(R.id.id_3d_view).setVisibility(View.GONE);
            }

            if(mDroneViewObj != null){
                if(!TextUtils.isEmpty(mDroneViewObj.getThumb())){
                    Picasso.get().load(mDroneViewObj.getThumb())
                            .config(Bitmap.Config.ARGB_8888)
                            .placeholder(R.drawable.aerial_view_placeholder)
                            .into((ImageView)findViewById(R.id.id_drone_view_thumb_img));
                }
                findViewById(R.id.id_drone_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String videoURL = mDroneViewObj.getVideoURL();
                        if(!TextUtils.isEmpty(videoURL)){
                            if(!UtilityMethods.isInternetConnected(ProjectDetailsScreen.this)){
                                UtilityMethods.showErrorSnackbarOnTop(ProjectDetailsScreen.this);
                                return;
                            }
                            handleVideoPlatform(videoURL,VirtualTourType.droneView.toString());
                        }else{
                            UtilityMethods.showSnackbarOnTop(ProjectDetailsScreen.this,"Error","Aerial View URL is empty",true, Snackbar.LENGTH_LONG);
                        }
                    }
                });
            }else{
                findViewById(R.id.id_drone_view).setVisibility(View.GONE);
            }

            if(mDriveViewObj != null){
                if(!TextUtils.isEmpty(mDriveViewObj.getThumb())){
                    Picasso.get().load(mDriveViewObj.getThumb())
                            .config(Bitmap.Config.ARGB_8888)
                            .placeholder(R.drawable.drive_view_placeholder)
                            .into((ImageView)findViewById(R.id.id_drive_view_thumb_img));
                }
                findViewById(R.id.id_drive_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!TextUtils.isEmpty(mDriveViewObj.getVideoURL())){
                            if(!UtilityMethods.isInternetConnected(ProjectDetailsScreen.this)){
                                UtilityMethods.showErrorSnackbarOnTop(ProjectDetailsScreen.this);
                                return;
                            }
                            handleVideoPlatform(mDriveViewObj.getVideoURL(),VirtualTourType.driveView.toString());
                        }else{
                            UtilityMethods.showSnackbarOnTop(ProjectDetailsScreen.this,"Error","Drive View URL is empty",true, Snackbar.LENGTH_LONG);
                        }
                    }
                });
            }else{
                findViewById(R.id.id_drive_view).setVisibility(View.GONE);
            }
        }else{
            findViewById(R.id.id_virtual_tour_layout).setVisibility(View.GONE);
            findViewById(R.id.id_virtual_tour_sep).setVisibility(View.GONE);
        }
    }

    private void setOfferView(Project project){
        TextView offerDetails = findViewById(R.id.offer_detail_txt);
        View seperatore = findViewById(R.id.id_offer_sep);
        if(isOffer(project)){
            findViewById(R.id.offer_layout).setVisibility(View.VISIBLE);
            seperatore.setVisibility(View.VISIBLE);
            isOffer = true;
            if(!TextUtils.isEmpty(project.getOfferLink())) {
                new TextKnowMoreUtil(offerDetails,"Know More",project.getOffer(),
                        project.getOfferLink());
            }

        }else {
            isOffer = false;
            seperatore.setVisibility(View.GONE);
            findViewById(R.id.offer_layout).setVisibility(View.GONE);
        }
    }

    private boolean isOffer(Project project){
        long currentDate = System.currentTimeMillis();
        boolean isOffer = false;
        //Button offerImage = findViewById(R.id.offer_image);
        if(project != null && project.getStartOfferDate() != null
                && project.getEndOfferDate() != null){
            long offerStartdate = project.getStartOfferDate();
            long offerEndDate = project.getEndOfferDate();
            if (currentDate >= offerStartdate && currentDate <= offerEndDate
                    && (!TextUtils.isEmpty(project.getOffer())) ) {
                isOffer = true;
            } else {
                isOffer = false;
            }
        }else{
            if (project != null && !TextUtils.isEmpty(project.getOffer())) {
                isOffer = true;
            } else {
                isOffer = false;
            }
        }
        return isOffer;
    }

    private void getCommericalView(List<PropertyType> propertyTypeList){
        if(checkCommercial(propertyTypeList)){
            isCommercial = true;
        }else{
            isCommercial = false;
        }
    }

    private boolean checkCommercial(List<PropertyType> propertyTypeList){
        boolean isCommercial=false;
        if(propertyTypeList != null && !propertyTypeList.isEmpty()){
            isCommercial = true;
            for(int i=0; i<propertyTypeList.size(); i++){
                PropertyType propertyType = propertyTypeList.get(i);
                boolean isSoldOut = false;
                if(propertyType != null){
                    if(propertyType.getSoldStatus() != null && propertyType.getSoldStatus()){
                        isSoldOut = true;
                    }
                    if(!UtilityMethods.isCommercial(propertyType.getType())){
                        if(isAllPropertySoldOut || !isSoldOut) {
                            isCommercial = false;
                            break;
                        }
                    }else if(isAllPropertySoldOut){
                        isCommercial = true;
                        break;
                    }
                }
            }
        }
        return isCommercial;
    }

    private void setAboutProject(String projectSummery){
        TextView aboutProject = findViewById(R.id.about_project_txt);
        aboutProject.setText(Html.fromHtml(projectSummery));
        if(Html.fromHtml(projectSummery).length() > 50) {
            new TextReadMoreUtil(aboutProject, 3, "Read More", true, projectSummery.toString().trim());
        }
    }

    private void setDeveloperView(Builder builder){
        final ProgressBar mDevLogoPB = findViewById(R.id.id_devlogo_pb);
        ImageView developerLogo = findViewById(R.id.img_builder_logo);
        final FrameLayout id_dev_image_panel = findViewById(R.id.id_dev_image_panel);
        TextView developerName =findViewById(R.id.txt_builder_name);
        TextView totalProjects = findViewById(R.id.total_count);
        TextView ongoingProjects = findViewById(R.id.ongoing_count);
        TextView devSummary = findViewById(R.id.summary);
        if(builder != null && !builder.isEmpty()) {
            // If developer flag is true then we need to show developer layout else not
            if(builder.getHide() != null && builder.getHide()) {
                LinearLayout developerViewLayout = findViewById(R.id.developer_layout);
                developerViewLayout.setVisibility(View.GONE);
                return;
            }
            String logoURL =null;
            if(builder.getSlogoURL()!=null)
            {
                logoURL = builder.getSlogoURL();
                id_dev_image_panel.setVisibility(View.VISIBLE);

            }
            else
            {
                id_dev_image_panel.setVisibility(View.GONE);
            }
            String name = builder.getName();
            Long establishDate = builder.getEstablishDate();
            Integer projectStarted = builder.getProjectStarted();
            Integer projectInProgress = builder.getProjectInProgress();
            String summary = builder.getSummary();
            if(!TextUtils.isEmpty(logoURL)){
                mDevLogoPB.setVisibility(View.VISIBLE);

                Picasso.get().load(logoURL).into(developerLogo, new Callback() {
                    @Override
                    public void onSuccess() {
                        mDevLogoPB.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        mDevLogoPB.setVisibility(View.GONE);
                        id_dev_image_panel.setVisibility(View.GONE);
                    }
                });
            }
            else
            {
                id_dev_image_panel.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(name)) {
                developerName.setText(name);
            }

            if (establishDate != null && establishDate != 0) {
                setDevloperExperince(establishDate);
            }

            if (projectStarted != null) {
                totalProjects.setText(projectStarted + " total projects");
            }
            if (projectInProgress != null) {
                ongoingProjects.setText(projectInProgress + " ongoing projects");
            }

            if (!TextUtils.isEmpty(summary)) {
                Log.i(TAG, "setDeveloperView: Summery -> " + summary);
                devSummary.setText(Html.fromHtml(summary));
                if(Html.fromHtml(summary).length() > 15) {
                    new TextReadMoreUtil(devSummary,3,"Read More",true,summary);
                }
            }
        }
    }

    private void setDevloperExperince(long startDate){
        TextView experience = findViewById(R.id.experience);
        TextView year_s = findViewById(R.id.year_s);
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String inputString1 = myFormat.format(new Date(startDate));
        String inputString2 = myFormat.format(new Date(System.currentTimeMillis()));

        try {
            Date date1 = myFormat.parse(inputString1);
            Date date2 = myFormat.parse(inputString2);
            long diff = date2.getTime() - date1.getTime();
            long noOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            Log.i(TAG, "getDevloperExperince: No OF days -> " + noOfDays);
            int d;
            d=(int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            int years = d/365;
            int dayCount = d%365;
            int noOfLeapYear = years/4;
            dayCount = dayCount - noOfLeapYear;
            if(years!=0 && dayCount > 0){
                experience.setText(years+"+ years experience ");
                year_s.setText("+ years ");
            }else if(years == 0 && dayCount > 0){
                experience.setText(1+" year experience ");
                year_s.setText(" year ");
            }else if(d > 0){
                experience.setText(1+" year experience ");
                year_s.setText(" year ");
            }else{
                experience.setText(1+" year experience ");
                year_s.setText(" year ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Calendar currentDate = Calendar.getInstance();
            currentDate.setTime(new Date(System.currentTimeMillis()));
            Calendar start = Calendar.getInstance();
            start.setTime(new Date(startDate));
            int diffYear = currentDate.get(Calendar.YEAR) - start.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + currentDate.get(Calendar.MONTH) - start.get(Calendar.MONTH);
            int totalExp = diffMonth/12;
            if(diffMonth > 0 && totalExp == 0){
                experience.setText(1+" year experience");
                year_s.setText(" year ");
            }else if(totalExp > 0){
                experience.setText(totalExp+"");
                year_s.setText("+ years experience");
            }
        }
    }

    private boolean expand = false;
    private void setAmenityList(final List<Amenity> amenityList) {

        if ((amenityList == null) || (amenityList.isEmpty())) {
            findViewById(R.id.amenities_layout).setVisibility(View.GONE);
            return;
        }
        final TextView moreAmenities = findViewById(R.id.amenities_more_less);
        final RecyclerView amenities = findViewById(R.id.amenities_list);
        final TextView amenityText = findViewById(R.id.amenities_txt);
        if(isTantative){
            amenityText.setText("Amenities (Tentative)");
        }

        amenities.setNestedScrollingEnabled(false);
        final AmenitiesGridAdapter amenitiesGridAdapter = new AmenitiesGridAdapter(this, amenityList);
        amenities.setLayoutManager(new GridLayoutManager(this,
                getResources().getInteger(R.integer.ameities_column_count)));
        amenities.setItemAnimator(new SlideInDownAnimator(new OvershootInterpolator(0.5f)));

        amenities.setAdapter(amenitiesGridAdapter);
        if (amenityList.size() > getResources().getInteger(R.integer.ameities_column_count)) {
            moreAmenities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expand) {
                        moreAmenities.setText(getResources().getString(R.string.view_all));
                        amenitiesGridAdapter.itemCount = getResources().getInteger(R.integer.ameities_column_count);
                        amenitiesGridAdapter.notifyItemRangeRemoved(getResources().getInteger(R.integer.ameities_column_count),
                                amenityList.size() - getResources().getInteger(R.integer.ameities_column_count));
                    } else {
                        moreAmenities.setText(getResources().getString(R.string.less));
                        amenitiesGridAdapter.itemCount = amenityList.size();
                        amenitiesGridAdapter.notifyItemRangeInserted(getResources().getInteger(R.integer.ameities_column_count),
                                amenityList.size() - getResources().getInteger(R.integer.ameities_column_count));
                    }
                    expand = !expand;
                    //amenitiesGridAdapter.notifyDataSetChanged();
                }
            });
        } else {
            moreAmenities.setVisibility(View.GONE);
        }
    }

    private void setLayoutList(List<PropertyType> propertyList,final Long projectId){
        if(propertyList == null || propertyList.isEmpty()){
            findViewById(R.id.id_area_n_status_layout).setVisibility(View.GONE);
            findViewById(R.id.layout_detail_layout).setVisibility(View.GONE);
            findViewById(R.id.tantative_layout_view).setVisibility(View.VISIBLE);
            Button requestLayoutPlanBtn = findViewById(R.id.id_layout_req_btn);
            requestLayoutPlanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleRequestLayoutPlan();
                }
            });
            return;
        }


        //((HousingApplication)getApplicationContext()).setPropertyTypeList(propertyTypeList);
        TextView viewAll = (TextView) findViewById(R.id.view_all_layouts);
        RecyclerView layoutList = (RecyclerView) findViewById(R.id.layout_list);
        layoutList.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutList.setLayoutManager(llm);
        layoutListAdapter = new LayoutListAdapter(this, propertyTypeList, projectId);
        layoutList.setAdapter(layoutListAdapter);
        if (propertyTypeList.size() == 1) {
            viewAll.setVisibility(View.GONE);

            if(propertyTypeList.get(0) != null && propertyTypeList.get(0).getSuperArea() != null
                    && propertyTypeList.get(0).getSuperArea() == 0){
                findViewById(R.id.layout_detail_layout).setVisibility(View.GONE);
                findViewById(R.id.tantative_layout_view).setVisibility(View.VISIBLE);
                Button requestLayoutPlanBtn = findViewById(R.id.id_layout_req_btn);
                requestLayoutPlanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleRequestLayoutPlan();
                    }
                });
                return;
            }
        }
        String layoutImageURL = "";
        boolean is2DPicture = true;
        try {
            if((propertyTypeList.get(0).getFloorPlan()!=null)
                    && (!propertyTypeList.get(0).getFloorPlan().isEmpty())) {
                if (!TextUtils.isEmpty(propertyTypeList.get(0).getFloorPlan().getSurl2d())) {
                    layoutImageURL = propertyTypeList.get(0).getFloorPlan().getSurl2d() + "=h350";
                    is2DPicture = true;
                } else if (!TextUtils.isEmpty(propertyTypeList.get(0).getFloorPlan().getSurl3d())) {
                    layoutImageURL = propertyTypeList.get(0).getFloorPlan().getSurl3d() + "=h350";
                    is2DPicture = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String customImageURL = layoutImageURL ;
        final boolean is2DPic = is2DPicture;
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UtilityMethods.isInternetConnected(ProjectDetailsScreen.this)){
                    UtilityMethods.showErrorSnackbarOnTop(ProjectDetailsScreen.this);
                    return;
                }
                try {
                    if(!TextUtils.isEmpty(customImageURL)){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap imageBitmap = Picasso.get().load(customImageURL).get();
                                    ((HousingApplication) getApplicationContext()).setLayoutImageBitmap(imageBitmap,
                                            propertyTypeList.get(0).getId(), is2DPic);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ProjectDetailsScreen.this, PropertyLayoutActivity.class);
                intent.putExtra("isViewAll",true);
                intent.putExtra("FromLayoutPage",true);
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, projectId);
                startActivity(intent);
            }
        });
    }

    private void setExploreArea() {
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.explore_map_lite);

        if (mapFragment != null) {
            mapFragment.getMapAsync(ProjectDetailsScreen.this);
        }

        findViewById(R.id.explore_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.exploreClick.toString());
                openMapExploreActivity();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady called...");
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.id_explore_map_light_PB);
        progressBar.setVisibility(View.VISIBLE);
        GoogleMap mMap = googleMap;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.i(TAG, "onMapLoaded: called...");
                progressBar.setVisibility(View.GONE);
            }
        });
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.redbrics_mapstyle);
        mMap.setMapStyle(mapStyleOptions);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        addMarker(mProjectLatlng,mMap);
        showLocation(null, mProjectLatlng,mMap);
        LinearLayout exploreAreaLayout = (LinearLayout) findViewById(R.id.id_explore_area_layout);

        exploreAreaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.exploreClick.toString());
                openMapExploreActivity();
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                openMapExploreActivity();
            }
        });
    }

    private void openMapExploreActivity(){
        if(!UtilityMethods.isInternetConnected(ProjectDetailsScreen.this)){
            UtilityMethods.showErrorSnackbarOnTop(ProjectDetailsScreen.this);
            return;
        }
        Intent intent = new Intent(ProjectDetailsScreen.this, ExploreNeighbourhood.class);
        Bundle location = new Bundle();
        location.putParcelable("LatLng", mProjectLatlng);
        intent.putExtra(Constants.AppConstants.LOCATION, location);
        intent.putExtra(Constants.AppConstants.HOME, mProjectName);
        startActivity(intent);
    }

    private void addMarker(LatLng position, GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(position))
                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_explore_lite));//BitmapDescriptorFactory.fromResource(R.drawable.explore_area_marker));
    }

    public void showLocation(View v, LatLng latLng,GoogleMap mMap) {
        // Wait until map is ready
        if (mMap == null) {
            return;
        }
        //   mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .tilt(80)
                .zoom(14.0f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setFinancingList(List<Bank> bankLoan, final List<PropertyType> propertyList) {
        if(isTantative || bankLoan == null || bankLoan.isEmpty()){
            findViewById(R.id.financing_layout).setVisibility(View.GONE);
            findViewById(R.id.tantative_finance_view).setVisibility(View.VISIBLE);
            Button requestFinancePlanBtn = findViewById(R.id.id_finance_req_btn);
            requestFinancePlanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EventAnalyticsHelper().ItemClickEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.finacialClick.toString());
                    handleRequestLayoutPlan();
                }
            });
            return;
        }
        boolean isPriceOnRequest = false;
        if(propertyList == null || propertyList.isEmpty() || propertyList.get(0).getBsp() == 0){
            isPriceOnRequest = true;
        }
        if ((bankLoan != null) && !bankLoan.isEmpty()) {
            ((HousingApplication) getApplicationContext()).setBankLoanList(bankLoan);
            RecyclerView financeList = (RecyclerView) findViewById(R.id.financing_list);
            financeList.setNestedScrollingEnabled(false);
            LinearLayoutManager llm = new LinearLayoutManager(ProjectDetailsScreen.this);
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
            financeList.setLayoutManager(llm);
            financeList.setAdapter(new FinanceAdapter(ProjectDetailsScreen.this,
                    findViewById(R.id.financing_layout),bankLoan, isPriceOnRequest));
        } else {
            findViewById(R.id.financing_layout).setVisibility(View.GONE);
        }
    }

    private void setPaymentPlanList(ArrayList<PaymentPlan> paymentPlanList) {
        if (isTantative || (paymentPlanList == null) || (paymentPlanList.isEmpty())) {
            findViewById(R.id.payment_plan_layout).setVisibility(View.GONE);
            findViewById(R.id.tantative_payment_plan_view).setVisibility(View.VISIBLE);
            Button requestPaymentPlanBtn = findViewById(R.id.id_request_payment_plan_btn);
            requestPaymentPlanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EventAnalyticsHelper().ItemClickEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.paymentClick.toString());
                    handleRequestLayoutPlan();
                }
            });
            return;
        }else{
            /**
             * To check if all payment plan get hidden by getHide() flag then we need to hide all
             * layout and need to show request payment plan layout
             */
            ArrayList<PaymentPlan> paymentPlansArrayList = new ArrayList<>();
            for(int i=0; i<paymentPlanList.size(); i++){
                PaymentPlan paymentPlan = paymentPlanList.get(i);
                if(paymentPlan != null && ( paymentPlan.getHide() == null || !paymentPlan.getHide()) ){
                    paymentPlansArrayList.add(paymentPlan);
                }
            }
            if(paymentPlansArrayList.isEmpty()){
                findViewById(R.id.payment_plan_layout).setVisibility(View.GONE);
                findViewById(R.id.tantative_payment_plan_view).setVisibility(View.VISIBLE);
                Button requestPaymentPlanBtn = findViewById(R.id.id_request_payment_plan_btn);
                requestPaymentPlanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new EventAnalyticsHelper().ItemClickEvent(ProjectDetailsScreen.this, Constants.AnaylticsClassName.ProjectDetailsScreen,
                                null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.paymentClick.toString());
                        handleRequestLayoutPlan();
                        }
                });
                return;
            }
            ((HousingApplication)getApplicationContext()).setmPaymentPlanList(paymentPlansArrayList);
            RecyclerView paymentPlans = findViewById(R.id.payment_plan_list);
            paymentPlans.setNestedScrollingEnabled(false);
            TextView viewAllPaymentPlan = findViewById(R.id.view_all_payyment_plans);

            LinearLayoutManager llm = new LinearLayoutManager(ProjectDetailsScreen.this);
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
            paymentPlans.setLayoutManager(llm);
            if ((paymentPlansArrayList != null) && (!paymentPlansArrayList.isEmpty())) {
                paymentPlans.setAdapter(new PaymentPlanListAdapter(ProjectDetailsScreen.this, paymentPlansArrayList));
            }

            if (paymentPlansArrayList.size() == 1) {
                viewAllPaymentPlan.setVisibility(View.GONE);
            }
            viewAllPaymentPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProjectDetailsScreen.this, PaymentPlanActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setPropertyScores(Project project) {
        Float livability= null, connectivity = null, lifeStyle = null, valueForMoney = null;
        if(project == null || isCommercial || isTantative){
            findViewById(R.id.value_layout).setVisibility(View.GONE);
            return;
        }
        if(project != null){
            livability = project.getLivabilityScore();
            connectivity = project.getConnectivityScore();
            lifeStyle = project.getLifeStyleScore();
            valueForMoney = project.getValueForMoneyScore();
            if(livability == null || livability == 0
                    || connectivity == null || connectivity == 0
                    || lifeStyle == null || lifeStyle == 0
                    || valueForMoney == null || valueForMoney == 0){
                findViewById(R.id.value_layout).setVisibility(View.GONE);
                return;
            }
        }
        int livabilityScore = 0, connectivityScore =0, lifeStyleScore =0, valueForMoneyScore =0;
        String livabilityScoreStr = "", connectivityScoreStr = "", lifeStyleScoreStr = "", valueForMoneyScoreStr = "";
        if(livability != null){
            livabilityScore =  (int)livability.floatValue();
            livabilityScoreStr = String.format("%.1f",livability.floatValue());
        }
        if(connectivity != null){
            connectivityScore =  (int)connectivity.floatValue();
            connectivityScoreStr = String.format("%.1f",connectivity.floatValue());
        }
        if(lifeStyle != null){
            lifeStyleScore =  (int)lifeStyle.floatValue();
            lifeStyleScoreStr = String.format("%.1f",lifeStyle.floatValue());
        }
        if(valueForMoney != null){
            valueForMoneyScore =  (int)valueForMoney.floatValue();
            valueForMoneyScoreStr = String.format("%.1f",valueForMoney.floatValue());
        }
        if(lifeStyleScore == 0 || connectivityScore == 0 || lifeStyleScore == 0 || valueForMoneyScore == 0){
            findViewById(R.id.value_layout).setVisibility(View.GONE);
            return;
        }
        TextView livabilityScoreText = findViewById(R.id.livability_score);
        TextView connectivityScoreText = findViewById(R.id.connectivity_score);
        TextView lifestyleScoreText = findViewById(R.id.lifestyle_score);
        TextView valueForMoneyScoreText = findViewById(R.id.value_score);

        ProgressBar livabilityPB = findViewById(R.id.livability_progress);
        ProgressBar connectivityPB = findViewById(R.id.connectivity_progress);
        ProgressBar lifestylePB = findViewById(R.id.lifestyle_progress);
        ProgressBar valueForMoneyPB = findViewById(R.id.value_progress);
        try {
            if(!TextUtils.isEmpty(livabilityScoreStr)){
                Float liveScore = Float.parseFloat(livabilityScoreStr);
                if(liveScore == liveScore.intValue()){
                    livabilityScoreText.setText(liveScore.intValue() + "");
                }else{
                    livabilityScoreText.setText(livabilityScoreStr + "");
                }
                livabilityPB.setProgress(Math.round(liveScore));
            }

            if(!TextUtils.isEmpty(connectivityScoreStr)){
                Float connectScore = Float.parseFloat(connectivityScoreStr);
                if(connectScore == connectScore.intValue()){
                    connectivityScoreText.setText(connectScore.intValue() + "");
                }else{
                    connectivityScoreText.setText(connectivityScoreStr + "");
                }
                connectivityPB.setProgress(Math.round(connectScore));
            }

            if(!TextUtils.isEmpty(lifeStyleScoreStr)){
                Float lifeScore = Float.parseFloat(lifeStyleScoreStr);
                if(lifeScore == lifeScore.intValue()){
                    lifestyleScoreText.setText(lifeScore.intValue() + "");
                }else{
                    lifestyleScoreText.setText(lifeStyleScoreStr + "");
                }
                lifestylePB.setProgress(Math.round(lifeScore));
            }

            if(!TextUtils.isEmpty(valueForMoneyScoreStr)){
                Float moneyScore = Float.parseFloat(valueForMoneyScoreStr);
                if(moneyScore == moneyScore.intValue()){
                    valueForMoneyScoreText.setText(moneyScore.intValue() + "");
                }else{
                    valueForMoneyScoreText.setText(valueForMoneyScoreStr + "");
                }
                valueForMoneyPB.setProgress(Math.round(moneyScore));
            }
        } catch (Exception e) {
            e.printStackTrace();
            lifestyleScoreText.setText(livabilityScoreStr + "");
            connectivityScoreText.setText(connectivityScoreStr + "");
            lifestyleScoreText.setText(lifeStyleScoreStr + "");
            valueForMoneyScoreText.setText(valueForMoneyScoreStr + "");
        }
        if(livabilityScore >= 0 && livabilityScore < 4){
            UtilityMethods.setProgressBarBackground(this,livabilityPB,R.drawable.property_score_pb_red_bg);
            UtilityMethods.setTextViewColor(this,livabilityScoreText,R.color.property_score_red_color);
        }else if(livabilityScore >= 4 && livabilityScore < 8){
            UtilityMethods.setProgressBarBackground(this,livabilityPB,R.drawable.property_score_pb_yellow_bg);
            UtilityMethods.setTextViewColor(this,livabilityScoreText,R.color.property_score_yellow_color);
        }else if(livabilityScore >= 8 && livabilityScore <= 10){
            UtilityMethods.setProgressBarBackground(this,livabilityPB,R.drawable.property_score_pb_green_bg);
            UtilityMethods.setTextViewColor(this,livabilityScoreText,R.color.property_score_green_color);
        }

        if(connectivityScore >= 0 && connectivityScore < 4){
            UtilityMethods.setProgressBarBackground(this,connectivityPB,R.drawable.property_score_pb_red_bg);
            UtilityMethods.setTextViewColor(this,connectivityScoreText,R.color.property_score_red_color);
        }else if(connectivityScore >= 4 && connectivityScore < 8){
            UtilityMethods.setProgressBarBackground(this,connectivityPB,R.drawable.property_score_pb_yellow_bg);
            UtilityMethods.setTextViewColor(this,connectivityScoreText,R.color.property_score_yellow_color);
        }else if(connectivityScore >= 8 && connectivityScore <= 10){
            UtilityMethods.setProgressBarBackground(this,connectivityPB,R.drawable.property_score_pb_green_bg);
            UtilityMethods.setTextViewColor(this,connectivityScoreText,R.color.property_score_green_color);
        }

        if(valueForMoneyScore >= 0 && valueForMoneyScore < 4){
            UtilityMethods.setProgressBarBackground(this,valueForMoneyPB,R.drawable.property_score_pb_red_bg);
            UtilityMethods.setTextViewColor(this,valueForMoneyScoreText,R.color.property_score_red_color);
        }else if(valueForMoneyScore >= 4 && valueForMoneyScore < 8){
            UtilityMethods.setProgressBarBackground(this,valueForMoneyPB,R.drawable.property_score_pb_yellow_bg);
            UtilityMethods.setTextViewColor(this,valueForMoneyScoreText,R.color.property_score_yellow_color);
        }else if(valueForMoneyScore >= 8 && valueForMoneyScore <= 10){
            UtilityMethods.setProgressBarBackground(this,valueForMoneyPB,R.drawable.property_score_pb_green_bg);
            UtilityMethods.setTextViewColor(this,valueForMoneyScoreText,R.color.property_score_green_color);
        }

        if(lifeStyleScore >= 0 && lifeStyleScore < 4){
            UtilityMethods.setProgressBarBackground(this,lifestylePB,R.drawable.property_score_pb_red_bg);
            UtilityMethods.setTextViewColor(this,lifestyleScoreText,R.color.property_score_red_color);
        }else if(lifeStyleScore >= 4 && lifeStyleScore < 8){
            UtilityMethods.setProgressBarBackground(this,lifestylePB,R.drawable.property_score_pb_yellow_bg);
            UtilityMethods.setTextViewColor(this,lifestyleScoreText,R.color.property_score_yellow_color);
        }else if(lifeStyleScore >= 8 && lifeStyleScore <= 10){
            UtilityMethods.setProgressBarBackground(this,lifestylePB,R.drawable.property_score_pb_green_bg);
            UtilityMethods.setTextViewColor(this,lifestyleScoreText,R.color.property_score_green_color);
        }

        findViewById(R.id.id_livability_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoresDialog(getResources().getString(R.string.livability),
                        getResources().getString(R.string.livability_detail));
            }
        });

        findViewById(R.id.id_connectivity_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoresDialog(getResources().getString(R.string.connectivity),
                        getResources().getString(R.string.connectivity_detail));
            }
        });

        findViewById(R.id.id_lifestyle_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoresDialog(getResources().getString(R.string.lifestyle),
                        getResources().getString(R.string.lifestyle_detail));
            }
        });

        findViewById(R.id.id_valueformoney_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoresDialog(getResources().getString(R.string.value_for_money),
                        getResources().getString(R.string.value_for_money_detail));
            }
        });
    }

    public void showScoresDialog(String title, String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProjectDetailsScreen.this);
        builder.setTitle(title);
        builder.setMessage(description);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
        alertDialog.show();
    }

    private void setProjectDocumentList(final String coverImageURL,final String browcherURL, List<Document> documentList) {
        if(!TextUtils.isEmpty(browcherURL)){
            Document document = new Document();
            document.setName("Brochure");
            document.setUrl(browcherURL);
            document.setSecUrl(coverImageURL);
            if(documentList != null && !documentList.isEmpty()) {
                documentList.add(0, document);
            }else{
                documentList = new ArrayList<>();
                documentList.add(document);
            }
        }else{
            if(documentList != null && !documentList.isEmpty()) {
                Document document = documentList.get(0);
                document.setSecUrl(coverImageURL);
            }
        }
        if ((documentList != null) && !documentList.isEmpty()) {
            //new ProjectDocumentTask(documentList).execute();
            RecyclerView financeList = (RecyclerView) findViewById(R.id.project_document_list);
            financeList.setNestedScrollingEnabled(false);
            LinearLayoutManager llm = new LinearLayoutManager(ProjectDetailsScreen.this);
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
            financeList.setLayoutManager(llm);
            financeList.setAdapter(new ProjectDocumentsAdapter(ProjectDetailsScreen.this, documentList));
        } else {
            findViewById(R.id.project_document_layout).setVisibility(View.GONE);
        }
    }

    private void addToRecentView(GetProjectDetailResponse result) {
        if(result != null && result.getProject() != null){
            com.clicbrics.consumer.model.Project project = new com.clicbrics.consumer.model.Project();
            project.setId(mProjectId);
            project.setName(mProjectName);
            if(!TextUtils.isEmpty(result.getProject().getBuilderName())){
                project.setBuilderName(result.getProject().getBuilderName());
            }else if(getIntent().hasExtra(Constants.IntentKeyConstants.BUILDER_NAME)){
                project.setBuilderName(getIntent().getStringExtra(Constants.IntentKeyConstants.BUILDER_NAME));
            }
            if(!TextUtils.isEmpty(result.getProject().getPriceRange())){
                project.setPriceRange(result.getProject().getPriceRange());
            }else if(getIntent().hasExtra(Constants.IntentKeyConstants.PRICE_RANGE)){
                project.setPriceRange(getIntent().getStringExtra(Constants.IntentKeyConstants.PRICE_RANGE));
            }
            if(!TextUtils.isEmpty(result.getProject().getBspRange())){
                project.setBspRange(result.getProject().getBspRange());
            }else if(getIntent().hasExtra(Constants.IntentKeyConstants.PROJECT_BSP_RANGE)){
                project.setBspRange(getIntent().getStringExtra(Constants.IntentKeyConstants.PROJECT_BSP_RANGE));
            }
            if(!TextUtils.isEmpty(result.getProject().getPropertyTypeRange())){
                project.setPropertyTypeRange(result.getProject().getPropertyTypeRange());
            }else if(getIntent().hasExtra(Constants.IntentKeyConstants.BED_LIST)){
                project.setPropertyTypeRange(getIntent().getStringExtra(Constants.IntentKeyConstants.BED_LIST));
            }
            if(!TextUtils.isEmpty(result.getProject().getSizeRange())){
                project.setSizeRange(result.getProject().getSizeRange());
            }else if(getIntent().hasExtra(Constants.IntentKeyConstants.AREA_RANGE)){
                project.setSizeRange(getIntent().getStringExtra(Constants.IntentKeyConstants.AREA_RANGE));
            }
            if(!TextUtils.isEmpty(result.getProject().getAddress())){
                project.setAddress(result.getProject().getAddress());
            }else if(getIntent().hasExtra(Constants.IntentKeyConstants.ADDRESS)){
                project.setAddress(getIntent().getStringExtra(Constants.IntentKeyConstants.ADDRESS));
            }

            project.setVirtualTour(isVirtualTourAvail);
            project.setSold(isAllPropertySoldOut);
            project.setCommercial(isCommercial);

            if(result.getProject().getOfferAvailable() != null){
                project.setOfferAvailable(result.getProject().getOfferAvailable());
            }else if(getIntent().hasExtra(Constants.IntentKeyConstants.OFFER)){
                project.setOfferAvailable(getIntent().getBooleanExtra(Constants.IntentKeyConstants.OFFER,false));
            }
            if(!TextUtils.isEmpty(result.getProject().getSCoverImage())){
                project.setCoverImage(result.getProject().getSCoverImage());
            }else if(getIntent().hasExtra(Constants.IntentKeyConstants.PROJECT_COVER_IMAGE)){
                project.setCoverImage(getIntent().getStringExtra(Constants.IntentKeyConstants.PROJECT_COVER_IMAGE));
            }

            if(!TextUtils.isEmpty(result.getProject().getProjectStatus())) {
                project.setProjectStatus(Constants.AppConstants.PropertyStatus.valueOf(result.getProject().getProjectStatus()));
            }
            project.setTimeStamp(System.currentTimeMillis());
            project.setCityId(result.getProject().getCityId());
            project.setBuilderId(result.getProject().getBuilderId());

            if(result.getProject().getImages() != null){
                List<com.clicbrics.consumer.model.Image> imageList = new ArrayList<>();
                List<Image> images = result.getProject().getImages();
                if(images != null){
                    for(int i=0; i<images.size(); i++){
                        Image image = images.get(i);
                        if(image != null && !TextUtils.isEmpty(image.getSURL()) && !imageList.contains(image.getSURL())){
                            com.clicbrics.consumer.model.Image img = new com.clicbrics.consumer.model.Image();
                            img.setSURL(image.getSURL());
                            imageList.add(img);
                        }
                    }
                }
                if(!imageList.isEmpty()) {
                    project.setImages(imageList);
                }
            }
            else if(getIntent().hasExtra(Constants.IntentKeyConstants.IMAGE_LIST)) {
                List<com.clicbrics.consumer.model.Image> imageList = new ArrayList<>();
                List<String> images = getIntent().getStringArrayListExtra(Constants.IntentKeyConstants.IMAGE_LIST);
                if(images != null){
                    for(int i=0; i<images.size(); i++){
                        String img = images.get(i);
                        if(!TextUtils.isEmpty(img) && !imageList.contains(img)){
                            com.clicbrics.consumer.model.Image image = new com.clicbrics.consumer.model.Image();
                            image.setSURL(img);
                            imageList.add(image);
                        }
                    }
                }
                project.setImages(imageList);
            }

            /*SearchProperty recentProject = new SearchProperty(mProjectId, mProjectName, developerName, minPrice,
                    maxPrice, bedSet, type, typeList, minArea, maxArea, getAddress(result.getProject()),
                    projectStatus, cityId, isCommercial, isOffer, coverImage, coverImageList,
                    System.currentTimeMillis(),bhkType,isVirtualTourAvail,propertyStatusList,
                    isAllPropertySoldOut,isDriveView,isDroneView);*/

            UtilityMethods.addToRecentProjectList(this, mProjectId, project);
        }
    }

    private String getAddress(Project project){
        if(project == null){
            return "";
        }
        String address = "";
        if(project.getAddress() != null){
            address = project.getAddress();
        }
        if(project.getCity() != null){
            address = address + ", " + project.getCity();
        }
        return address;
    }

    private void handleRequestLayoutPlan(){
        if (!UtilityMethods.isInternetConnected(ProjectDetailsScreen.this)) {
            UtilityMethods.showErrorSnackbarOnTop(ProjectDetailsScreen.this);
            return;
        }
        if (UtilityMethods.getLongInPref(ProjectDetailsScreen.this, Constants.ServerConstants.USER_ID, -1) == -1) {
            startActivity(new Intent(ProjectDetailsScreen.this, LoginActivity.class));
            HousingApplication.mLoginSuccessCallback = ProjectDetailsScreen.this;
            loginFor = LoginFor.CALL_ME_BACK;
        } else {
            Intent intent = new Intent(ProjectDetailsScreen.this, FilterActivity.class);
            intent.putExtra(Constants.IntentKeyConstants.CALL_ME_BACK_REQUEST, true);
            intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, mProjectId);
            startActivity(intent);
        }
    }

    private void setPriceOnRequestUI(){
        LinearLayout priceNBSPLayout = findViewById(R.id.id_price_n_bsp_layout);
        TextView priceOnRequestText = findViewById(R.id.id_price_n_bsp);
        priceOnRequestText.setTextSize(14);
        priceOnRequestText.setPadding(10,5,10,5);
        UtilityMethods.setDrawableBackground(this,priceOnRequestText,R.drawable.button_red_border);
        priceNBSPLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Price on request");
                handleRequestLayoutPlan();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case STORAGE_PERMISSION:{
                boolean isPermissionGranted = false;
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        isPermissionGranted = true;
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        isPermissionGranted = false;
                        UtilityMethods.showSnackbarOnTop(this,"Error",getResources().getString(R.string.storage_permission_denied),true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private PopupWindow popupWindow;
    private void showUnitPopupWindow(){
        try {
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.drop_down_layout, (ViewGroup) findViewById(R.id.id_drop_down_layout));
            TextView sqftTextview = layout.findViewById(R.id.id_unit_sqft);
            TextView sqydTextview = layout.findViewById(R.id.id_unit_sqyd);
            TextView sqmTextview = layout.findViewById(R.id.id_unit_sqm);

            sqftTextview.setOnClickListener(OnUnitChangeListener);
            sqydTextview.setOnClickListener(OnUnitChangeListener);
            sqmTextview.setOnClickListener(OnUnitChangeListener);

            TextView areaRange = findViewById(R.id.id_area);
            if(areaRange != null){
                if(areaRange.getText().toString().toLowerCase().contains("sq.ft")){
                    //sqftTextview.setSelected(true);
                    UtilityMethods.setColorBackground(ProjectDetailsScreen.this,sqftTextview,R.color.colorAccent);
                    UtilityMethods.setTextViewColor(ProjectDetailsScreen.this,sqftTextview,R.color.white);
                    UtilityMethods.setColorBackground(ProjectDetailsScreen.this,sqydTextview,R.color.white);
                    UtilityMethods.setTextViewColor(ProjectDetailsScreen.this,sqydTextview,R.color.black);
                    UtilityMethods.setColorBackground(ProjectDetailsScreen.this,sqmTextview,R.color.white);
                    UtilityMethods.setTextViewColor(ProjectDetailsScreen.this,sqmTextview,R.color.black);
                }else if(areaRange.getText().toString().toLowerCase().contains("sq.yd")){
                    UtilityMethods.setColorBackground(ProjectDetailsScreen.this,sqydTextview,R.color.colorAccent);
                    UtilityMethods.setTextViewColor(ProjectDetailsScreen.this,sqydTextview,R.color.white);
                    UtilityMethods.setColorBackground(ProjectDetailsScreen.this,sqftTextview,R.color.white);
                    UtilityMethods.setTextViewColor(ProjectDetailsScreen.this,sqftTextview,R.color.black);
                    UtilityMethods.setColorBackground(ProjectDetailsScreen.this,sqmTextview,R.color.white);
                    UtilityMethods.setTextViewColor(ProjectDetailsScreen.this,sqmTextview,R.color.black);
                }else if(areaRange.getText().toString().toLowerCase().contains("sq.m")){
                    UtilityMethods.setColorBackground(ProjectDetailsScreen.this,sqmTextview,R.color.colorAccent);
                    UtilityMethods.setTextViewColor(ProjectDetailsScreen.this,sqmTextview,R.color.white);
                    UtilityMethods.setColorBackground(ProjectDetailsScreen.this,sqftTextview,R.color.white);
                    UtilityMethods.setTextViewColor(ProjectDetailsScreen.this,sqftTextview,R.color.black);
                    UtilityMethods.setColorBackground(ProjectDetailsScreen.this,sqydTextview,R.color.white);
                    UtilityMethods.setTextViewColor(ProjectDetailsScreen.this,sqydTextview,R.color.black);
                }
            }
            popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            areaRange.measure(0,0);
            int width = areaRange.getMeasuredWidth();
            if(width != 0) {
                popupWindow.setWidth(width);
            }else{
                popupWindow.setWidth(180);
            }
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

            popupWindow.setTouchInterceptor(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popupWindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });

            popupWindow.setContentView(layout);
            popupWindow.showAsDropDown(areaRange);



         /*   int[] point = new int[2];
            areaRange.getLocationOnScreen(point);
            int x = point[0];
            int y = point[1];
            popupWindow.setContentView(layout);
//            popupWindow.showAtLocation(areaRange, Gravity.TOP,x, y);
            popupWindow.showAtLocation(areaRange, Gravity.BOTTOM, 0,
                    areaRange.getBottom() - 60);
            popupWindow.showAsDropDown(areaRange);*/
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                popupWindow.showAsDropDown(areaRange, x, y, Gravity.TOP);
            } else {
                popupWindow.showAsDropDown(areaRange, areaRange.getWidth() - popupWindow.getWidth(), y);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener OnUnitChangeListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onClick(View view) {
            if(popupWindow !=null && popupWindow.isShowing() && !isDestroyed()) {
                try {
                    String unit = "";
                    if(view.getId() == R.id.id_unit_sqft){
                        Log.i(TAG, "onClick: Selected Unit SQFT");
                        unit = Constants.PropertyUnit.SQFT.toString();
                    }else if(view.getId() == R.id.id_unit_sqyd){
                        Log.i(TAG, "onClick: Selected Unit SQYD");
                        unit = Constants.PropertyUnit.SQYD.toString();
                    }else if(view.getId() == R.id.id_unit_sqm){
                        Log.i(TAG, "onClick: Selected Unit SQM");
                        unit = Constants.PropertyUnit.SQM.toString();
                    }
                    popupWindow.dismiss();
                    popupWindow = null;
                    if(unit.equalsIgnoreCase(UtilityMethods.getSelectedUnit(ProjectDetailsScreen.this))){
                        return;
                    }
                    UtilityMethods.saveStringInPref(ProjectDetailsScreen.this,Constants.AppConstants.SELECTED_PROPERTY_UNIT,unit);
                    if(layoutListAdapter != null){
                        layoutListAdapter.notifyDataSetChanged();
                    }
                    changeValueOnPropertyChange();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }   catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void changeValueOnPropertyChange(){
        setBSPRange();
        setAreaRange();
        setGalleryImageList(mDetailsResponse);
        createVirtualTourList();
        Intent intent = new Intent(Constants.BroadCastConstants.ON_UNIT_CHANGE);
        sendBroadcast(intent);
    }

    private void setGalleryImageList(GetProjectDetailResponse result) {
        if(result == null){
            return;
        }
        if(galleryImageList != null || !galleryImageList.isEmpty()){
            galleryImageList.clear();
        }
        ArrayList<ImageObject> imageList = null;
        if (result.getProject() != null && !TextUtils.isEmpty(result.getProject().getSCoverImage())) {
            imageList = new ArrayList<>();
            ImageObject imageObject = new ImageObject();
            imageObject.isVideo = false;
            imageObject.image_url = result.getProject().getSCoverImage();
            imageList.add(imageObject);
        }

        if ((result.getProject() != null) && (!result.getProject().isEmpty()) &&
                (result.getProject().getImages() != null) && (!result.getProject().getImages().isEmpty())) {
            List<Image> plotImages = result.getProject().getImages();
            //imageList = new ArrayList<>();
            if(imageList == null){
                imageList = new ArrayList<>();
            }

            for (Image image : plotImages) {
                if(image != null && !image.isEmpty() && image.getSURL() != null && !image.getSURL().isEmpty()) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = false;
                    imageObject.image_url = image.getSURL();
                    imageList.add(imageObject);
                }
            }

            if ((imageList != null) && (!imageList.isEmpty())) {
                Log.d(TAG, "imageListSize->" + imageList.size() + " plotImageSize->" + plotImages.size());
                //mImageList.put("OVERVIEW", imageList);
            }
        }

        if(imageList != null && !imageList.isEmpty()) {
            galleryImageList.put("OVERVIEW", imageList);
        }

        imageList = new ArrayList<>();
        if (result.getProject() != null && result.getProject().getVideos() != null && result.getProject().getVideos().size() != 0) {
            for (Video video : result.getProject().getVideos()) {
                if(video != null && !video.isEmpty() && video.getUrl() != null && !video.getUrl().isEmpty()) {
                    if (video.getUrl().contains("youtube")) {
                        ImageObject imageObject = new ImageObject();
                        imageObject.isVideo = true;
                        imageObject.image_url = video.getUrl();
                        imageList.add(imageObject);
                    } else {
                        ImageObject imageObject = new ImageObject();
                        imageObject.isVideo = true;
                        imageObject.image_url = BuildConfigConstants.storage_path + video.getUrl();
                        imageList.add(imageObject);
                    }
                }
            }
        }

        if(result.getProject()!= null && !result.getProject().isEmpty()
                && result.getProject().getWalkThroughVideoURL() != null
                && !result.getProject().getWalkThroughVideoURL().isEmpty()){
            if(imageList == null){
                imageList = new ArrayList<>();
            }
            if(result.getProject().getWalkThroughVideoURL() != null && !result.getProject().getWalkThroughVideoURL().isEmpty()) {
                ImageObject imageObject = new ImageObject();
                imageObject.image_url = result.getProject().getWalkThroughVideoURL();
                imageObject.isVideo = true;
                imageList.add(imageObject);
            }
        }
        if(imageList != null && !imageList.isEmpty()) {
            galleryImageList.put("VIDEO", imageList);
        }

        imageList = new ArrayList<>();
        if ((result.getProject() != null) && !result.getProject().isEmpty() &&
                result.getProject().getLocationPlan() != null && !result.getProject().getLocationPlan().isEmpty()) {
            FloorPlan floorPlan = result.getProject().getLocationPlan();
            if(floorPlan != null && !floorPlan.isEmpty()){
                if(floorPlan.getSurl2d() != null && !floorPlan.getSurl2d().isEmpty()) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = false;
                    imageObject.image_url = floorPlan.getSurl2d();
                    imageList.add(imageObject);
                }
                if(floorPlan.getSurl3d() != null && !floorPlan.getSurl3d().isEmpty()){
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = false;
                    imageObject.image_url = floorPlan.getSurl3d();
                    imageList.add(imageObject);
                }
                if(floorPlan.getSurlImage() != null && !floorPlan.getSurlImage().isEmpty()){
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = false;
                    imageObject.image_url = floorPlan.getSurlImage();
                    imageList.add(imageObject);
                }
                /*if(floorPlan.getUrlKML() != null && !floorPlan.getUrlKML().isEmpty()){
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = false;
                    imageObject.image_url = floorPlan.getUrlKML()+"=h400";
                    imageList.add(imageObject);
                }*/
                if(floorPlan.getUrlVideo() != null && !floorPlan.getUrlVideo().isEmpty()){
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = true;
                    imageObject.image_url = floorPlan.getUrlVideo();
                    imageList.add(imageObject);
                }
            }
        }
        if(imageList != null && !imageList.isEmpty()) {
            galleryImageList.put("LOCATION PLAN", imageList);
        }


        imageList = new ArrayList<>();
        if ((result.getProject() != null) && (!result.getProject().isEmpty()) &&
                (result.getProject().getSitePlan() != null) && (!result.getProject().getSitePlan().isEmpty())) {
            if(imageList == null) {
                imageList = new ArrayList<>();
            }
            FloorPlan sitePlan = result.getProject().getSitePlan();
            if(sitePlan != null && !sitePlan.isEmpty()){
                if(sitePlan.getSurl2d() != null && !sitePlan.getSurl2d().isEmpty()) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = false;
                    imageObject.image_url = sitePlan.getSurl2d();
                    imageList.add(imageObject);
                }
                if(sitePlan.getSurl3d() != null && !sitePlan.getSurl3d().isEmpty()) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = false;
                    imageObject.image_url = sitePlan.getSurl3d();
                    imageList.add(imageObject);
                }
                if(sitePlan.getSurlImage() != null && !sitePlan.getSurlImage().isEmpty()){
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = false;
                    imageObject.image_url = sitePlan.getSurlImage();
                    imageList.add(imageObject);
                }
                /*if(sitePlan.getUrlKML() != null && !sitePlan.getUrlKML().isEmpty()){
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = false;
                    imageObject.image_url = sitePlan.getUrlKML()+"=h400";
                    imageList.add(imageObject);
                }*/
                if(sitePlan.getUrlVideo() != null && !sitePlan.getUrlVideo().isEmpty()){
                    ImageObject imageObject = new ImageObject();
                    imageObject.isVideo = true;
                    imageObject.image_url = sitePlan.getUrlVideo();
                    imageList.add(imageObject);
                }

            }
        }
        if ((imageList != null) && (!imageList.isEmpty())) {
            galleryImageList.put("SITE PLAN", imageList);
        }

        imageList = new ArrayList<>();
        if ((result.getProject().getClusterPlan() != null) && (!result.getProject().getClusterPlan().isEmpty())) {

            List<FloorPlan> clusterPlanList = result.getProject().getClusterPlan();
            if(clusterPlanList != null && !clusterPlanList.isEmpty()) {
                for (FloorPlan clusterPlan : clusterPlanList) {
                    if(clusterPlan != null && !clusterPlan.isEmpty()){
                        if(clusterPlan.getSurl2d() != null && !clusterPlan.getSurl2d().isEmpty()) {
                            ImageObject imageObject = new ImageObject();
                            imageObject.isVideo = false;
                            imageObject.image_url = clusterPlan.getSurl2d();
                            imageList.add(imageObject);
                        }
                        if(clusterPlan.getSurl3d() != null && !clusterPlan.getSurl3d().isEmpty()){
                            ImageObject imageObject = new ImageObject();
                            imageObject.isVideo = false;
                            imageObject.image_url = clusterPlan.getSurl3d();
                            imageList.add(imageObject);
                        }
                        if(clusterPlan.getSurlImage() != null && !clusterPlan.getSurlImage().isEmpty()){
                            ImageObject imageObject = new ImageObject();
                            imageObject.isVideo = false;
                            imageObject.image_url = clusterPlan.getSurlImage();
                            imageList.add(imageObject);
                        }
                        /*if(clusterPlan.getUrlKML() != null && !clusterPlan.getUrlKML().isEmpty()){
                            ImageObject imageObject = new ImageObject();
                            imageObject.isVideo = false;
                            imageObject.image_url = clusterPlan.getUrlKML()+"=h400";
                            imageList.add(imageObject);
                        }*/
                        if(clusterPlan.getUrlVideo() != null && !clusterPlan.getUrlVideo().isEmpty()){
                            ImageObject imageObject = new ImageObject();
                            imageObject.isVideo = true;
                            imageObject.image_url = clusterPlan.getUrlVideo();
                            imageList.add(imageObject);
                        }
                    }
                }
            }
        }
        if(imageList != null && !imageList.isEmpty()){
            galleryImageList.put("CLUSTER PLAN", imageList);
        }

        //imageList = new ArrayList<>();
        if ((result.getPropertyTypeList() != null) && (!result.getPropertyTypeList().isEmpty())) {
            for (PropertyType property : result.getPropertyTypeList()) {
                imageList = new ArrayList<>();
                if(property != null && !property.isEmpty()) {
                    if ((property.getImages() != null) && (!property.getImages().isEmpty())) {
                        for (int i = 0; i < property.getImages().size(); i++) {
                            if ((property.getImages().get(i).getSURL() != null) && (!property.getImages().get(i).getSURL().isEmpty())) {
                                ImageObject imageObject = new ImageObject();
                                imageObject.image_url = property.getImages().get(i).getSURL();
                                imageObject.isVideo = false;
                                imageList.add(imageObject);
                            }
                        }
                    }

                    if (property.getFloorPlan() != null && (!property.getFloorPlan().isEmpty())) {
                        FloorPlan floorPlan = property.getFloorPlan();
                        if ((floorPlan.getSurl2d() != null) && (!floorPlan.getSurl2d().isEmpty())) {
                            String url2d = floorPlan.getSurl2d();
                            ImageObject imageObject = new ImageObject();
                            imageObject.image_url = url2d;
                            imageObject.isVideo = false;
                            imageList.add(imageObject);
                        }

                        if ((floorPlan.getSurl3d() != null) && (!floorPlan.getSurl3d().isEmpty())) {
                            String url3d = floorPlan.getSurl3d();
                            ImageObject imageObject = new ImageObject();
                            imageObject.image_url = url3d;
                            imageObject.isVideo = false;
                            imageList.add(imageObject);
                        }

                        if ((floorPlan.getSurlImage() != null) && (!floorPlan.getSurlImage().isEmpty())) {
                            String surlImage = floorPlan.getSurlImage();
                            ImageObject imageObject = new ImageObject();
                            imageObject.image_url = surlImage;
                            imageObject.isVideo = false;
                            imageList.add(imageObject);
                        }

                        /*if ((floorPlan.getUrlKML() != null) && (!floorPlan.getUrlKML().isEmpty())) {
                            String urlKML = floorPlan.getUrlKML();
                            ImageObject imageObject = new ImageObject();
                            imageObject.image_url = urlKML+"=h400";
                            imageObject.isVideo = false;
                            imageList.add(imageObject);
                        }*/

                        if ((floorPlan.getUrlVideo() != null) && (!floorPlan.getUrlVideo().isEmpty())) {
                            ImageObject imageObject = new ImageObject();
                            imageObject.isVideo = true;
                            imageObject.image_url = floorPlan.getUrlVideo();
                            imageList.add(imageObject);
                        }
                        if(property.getVideos() != null && !property.getVideos().isEmpty()){
                            List<Video> videos = property.getVideos();
                            if(videos != null && !videos.isEmpty()){
                                for(int i=0; i<videos.size(); i++){
                                    Video video = videos.get(i);
                                    if(video.getUrl() != null && !video.getUrl().isEmpty()) {
                                        ImageObject imageObject = new ImageObject();
                                        imageObject.isVideo = true;
                                        imageObject.image_url = video.getUrl();
                                        imageList.add(imageObject);
                                    }
                                }
                            }
                        }
                    }
                    if ((imageList != null) && (!imageList.isEmpty())) {
                        int superArea = property.getSuperArea();
                        if (property.getType().equalsIgnoreCase("LAND")) {
                            String area = UtilityMethods.getArea(this,superArea,true);
                            String unit = UtilityMethods.getUnit(this,true);
                            galleryImageList.put("PLOT (" + area + unit.toUpperCase()+")", imageList);
                        }else if (property.getType().equalsIgnoreCase("Studio")) {
                            String area = UtilityMethods.getArea(this,superArea,false);
                            String unit = UtilityMethods.getUnit(this,false);
                            galleryImageList.put("1RK Studio (" + area + unit.toUpperCase()+")", imageList);
                        }else if (UtilityMethods.isCommercial(property.getType())) {
                            if(UtilityMethods.isCommercialLand(property.getType())){
                                String area = UtilityMethods.getArea(this,superArea,true);
                                String unit = UtilityMethods.getUnit(this,true);
                                galleryImageList.put(UtilityMethods.getCommercialTypeName(property.getType())+" (" + area + unit.toUpperCase()+")", imageList);
                            }else{
                                String area = UtilityMethods.getArea(this,superArea,false);
                                String unit = UtilityMethods.getUnit(this,false);
                                galleryImageList.put(UtilityMethods.getCommercialTypeName(property.getType())+" (" + area+unit.toUpperCase() + ")", imageList);
                            }
                        } else {
                            String area = UtilityMethods.getArea(this,superArea,false);
                            String unit = UtilityMethods.getUnit(this,false);
                            galleryImageList.put(property.getNumberOfBedrooms() + "BHK (" + area+unit.toUpperCase() + ")", imageList);
                            //Log.d(TAG, property.getNumberOfBedrooms() + "BHK (" + property.getArea() + " SQFT.) + size-> " + imageList.size());
                        }
                    }
                }
            }
        }

        if ((result.getProject().getAmenityImages() != null) && (!result.getProject().getAmenityImages().isEmpty())) {

            List<Image> amenityImages = result.getProject().getAmenityImages();
            imageList = new ArrayList<>();
            for (Image amenityImage : amenityImages) {
                if(amenityImage != null && !amenityImage.isEmpty()
                        && amenityImage.getSURL()!= null && !amenityImage.getSURL().isEmpty()) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.image_url = amenityImage.getSURL();
                    imageObject.isVideo = false;
                    imageList.add(imageObject);
                }
            }
            if(imageList != null && !imageList.isEmpty()) {
                galleryImageList.put("AMENITIES", imageList);
            }
        }

        if ((result.getProject().getNeighbourImages() != null) && (!result.getProject().getNeighbourImages().isEmpty())) {
            List<Image> neighbourImages = result.getProject().getNeighbourImages();
            imageList = new ArrayList<>();
            for (Image neighbourImage : neighbourImages) {
                if(neighbourImage != null && !neighbourImage.isEmpty()
                        && neighbourImage.getSURL() != null && !neighbourImage.getSURL().isEmpty()) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.image_url = neighbourImage.getSURL();
                    imageObject.isVideo = false;
                    imageList.add(imageObject);
                }
            }
            if(imageList != null && !imageList.isEmpty()) {
                galleryImageList.put("NEARBY", imageList);
            }
        }

        if (result.getProject() != null && (result.getProject().getConstructionImages() != null)
                && (!result.getProject().getConstructionImages().isEmpty())) {
            List<Image> constructionImages = result.getProject().getConstructionImages();
            imageList = new ArrayList<>();
            for (Image constructionImage : constructionImages) {
                if(constructionImage != null && !constructionImage.isEmpty()
                        && constructionImage.getSURL() != null && !constructionImage.getSURL().isEmpty()) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.image_url = constructionImage.getSURL();
                    imageObject.isVideo = false;
                    imageList.add(imageObject);
                }
            }
            if(imageList != null && !imageList.isEmpty()) {
                galleryImageList.put("CONSTRUCTION UPDATES", imageList);
            }
        }

        if (result.getProject() != null && (result.getProject().getOtherImages() != null)
                && (!result.getProject().getOtherImages().isEmpty())) {
            List<Image> otherImages = result.getProject().getOtherImages();
            imageList = new ArrayList<>();
            for (Image otherImage : otherImages) {
                if(otherImage != null && !otherImage.isEmpty()
                        && otherImage.getSURL() != null && !otherImage.getSURL().isEmpty()) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.image_url = otherImage.getSURL();
                    imageObject.isVideo = false;
                    imageList.add(imageObject);
                }
            }
            if(imageList != null && !imageList.isEmpty()) {
                galleryImageList.put("OTHERS", imageList);
            }
        }
        mPagerAdapter.setGalleryImageList(galleryImageList,mProjectId);
    }

    private void setVirtualTourIcon(final List<Video> virtualTourVideoList){
        if(virtualTourVideoList != null && !virtualTourVideoList.isEmpty()){
            mVirtualTourVideoList = virtualTourVideoList;
            isVirtualTourVisible = true;
        }

        /*final LinearLayout iconLayout = findViewById(R.id.id_icon_layout);
        ImageView virtualViewIcon = findViewById(R.id.id_virtual_tour_view);
        ImageView driveViewIcon = findViewById(R.id.id_drive_view);
        if(virtualTourVideoList != null && !virtualTourVideoList.isEmpty()){
            isVirtualTourVisible = true;
        }
        if(isVirtualTourVisible){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation scaleAnimation = AnimationUtils.loadAnimation(ProjectDetailsScreen.this, R.anim.fab_scale);
                    iconLayout.setVisibility(View.VISIBLE);
                    iconLayout.setAnimation(scaleAnimation);
                }
            }, 1);
        }else{
            iconLayout.setVisibility(View.GONE);
        }
        virtualViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(virtualTourVideoList.size() == 1){
                    if(!UtilityMethods.isInternetConnected(ProjectDetailsScreen.this)){
                        UtilityMethods.showErrorSnackbarOnTop(ProjectDetailsScreen.this);
                        return;
                    }
                    Intent intent = new Intent(ProjectDetailsScreen.this,VirtualTourViewActivity.class);
                    intent.putExtra("URL",virtualTourVideoList.get(0).getUrl().trim());
                    intent.putExtra("Title",virtualTourVideoList.get(0).getName().trim());
                    startActivity(intent);
                }else{
                    showVirtualTourViewListDialog(virtualTourVideoList);
                }

            }
        });
        driveViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UtilityMethods.isInternetConnected(ProjectDetailsScreen.this)){
                    UtilityMethods.showErrorSnackbarOnTop(ProjectDetailsScreen.this);
                    return;
                }
                Intent driveViewIntent = new Intent(ProjectDetailsScreen.this,DriveViewActivity.class);
                startActivity(driveViewIntent);
            }
        });
        */
    }

    Dialog virtualTourListDialog;
    private void showVirtualTourViewListDialog(final List<Video> virtualTourVideoList){
        if(virtualTourListDialog != null && virtualTourListDialog.isShowing()){
            virtualTourListDialog.dismiss();
        }
        virtualTourListDialog = new Dialog(this);
        virtualTourListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        virtualTourListDialog.setContentView(R.layout.dialog_virtual_tour_list);
        virtualTourListDialog.setCancelable(true);
        //builderSingle.setIcon(R.mipmap.ic_launcher);
        //builderSingle.setTitle("Select Virtual Tour");

        ListView listView = virtualTourListDialog.findViewById(R.id.id_list);
        TextView cancelText = virtualTourListDialog.findViewById(R.id.id_cancel_btn);

        List<String> nameList = new ArrayList<>();
        for(int i=0; i<virtualTourVideoList.size(); i++){
            nameList.add(virtualTourVideoList.get(i).getName());
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.dialog_listitem_layout,R.id.id_list_item_txt);
        arrayAdapter.addAll(nameList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Video video = virtualTourVideoList.get(position);
                if(virtualTourListDialog != null && virtualTourListDialog.isShowing() && !isFinishing() && !isDestroyed()) {
                    virtualTourListDialog.dismiss();
                }
                Intent intent = new Intent(ProjectDetailsScreen.this,VirtualTourViewActivity.class);
                intent.putExtra("URL",video.getUrl().trim());
                intent.putExtra("Title",video.getName().trim());
                startActivity(intent);
            }
        });

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(virtualTourListDialog != null && virtualTourListDialog.isShowing() && !isFinishing() &&
                        !isDestroyed()) {
                    virtualTourListDialog.dismiss();
                }
            }
        });
        if(!isFinishing() && !isDestroyed()) {
            virtualTourListDialog.show();
        }
        /*builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Video video = virtualTourVideoList.get(which);
                dialog.dismiss();
                Intent intent = new Intent(ProjectDetailsScreen.this,VirtualTourViewActivity.class);
                intent.putExtra("URL",video.getUrl().trim());
                intent.putExtra("Title",video.getName().trim());
                startActivity(intent);
            }
        });*/
    }

    private void createVirtualTourList(){
        List<Video> virtualTourViewList = new ArrayList<>();
        if(propertyTypeList != null && !propertyTypeList.isEmpty()) {
            for(int i=0; i<propertyTypeList.size(); i++){
                PropertyType propertyType = propertyTypeList.get(i);
                if (propertyType.getVirtualTour() != null && !propertyType.getVirtualTour().isEmpty()) {
                    Video video = propertyType.getVirtualTour();
                    if (video != null && !TextUtils.isEmpty(video.getUrl())) {
                        try {
                            if (propertyType.getType().equalsIgnoreCase("Land")) {
                                String type = "PLOT";
                                String area = UtilityMethods.getArea(this, propertyType.getSuperArea(), true)
                                        + UtilityMethods.getUnit(this, true);
                                if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(area)) {
                                    video.setName(type + " - " + area);
                                }
                            } else {
                                String type = "";
                                if (UtilityMethods.hasVilla(propertyType.getType())) {
                                    type = "Villa";
                                } else if (propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentFloor.toString())) {
                                    type = "Independent Floor";
                                } else if (UtilityMethods.isCommercial(propertyType.getType())) {
                                    type = UtilityMethods.getArea(this, propertyType.getSuperArea(), true)
                                            + UtilityMethods.getUnit(this, true);
                                } else if (propertyType.getType().equalsIgnoreCase("Studio")) {
                                    type = "Studio";
                                } else {
                                    type = propertyType.getType();
                                }
                                String typeName = "";
                                if (UtilityMethods.isCommercial(propertyType.getType())) {
                                    typeName = UtilityMethods.getCommercialTypeName(propertyType.getType());
                                } else if (propertyType.getType().equalsIgnoreCase("Studio")) {
                                    typeName = "1RK";
                                } else {
                                    typeName = propertyType.getNumberOfBedrooms() + "BHK";
                                }
                                if (!TextUtils.isEmpty(typeName) && !TextUtils.isEmpty(type)) {
                                    video.setName(typeName + " " + type);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        virtualTourViewList.add(video);
                    }
                }
            }
        }
        setVirtualTourIcon(virtualTourViewList);
    }

    private boolean getPropertySoldOutStatus(){
        boolean isSoldOut = false;
        if(propertyTypeList != null){
            for(int i=0; i<propertyTypeList.size(); i++){
                PropertyType propertyType = propertyTypeList.get(i);
                if(propertyType != null && propertyType.getSoldStatus()){
                    isSoldOut = true;
                }else{
                    isSoldOut = false;
                    break;
                }
            }
        }
        return isSoldOut;
    }

    private void handleVideoPlatform(String videoURL, String type){
        Log.i(TAG, "handleVideoPlatform: VideoURL " + videoURL);
        if(videoURL.toLowerCase().contains("youtube")){
            if(!videoURL.contains("http")) {
                videoURL = BuildConfigConstants.storage_path + videoURL;
            }
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoURL));
            youtubeIntent.putExtra("force_fullscreen",true);
            startActivity(youtubeIntent);
        }else {
            if(type.equalsIgnoreCase(VirtualTourType.droneView.toString())){
                Intent intent = new Intent(ProjectDetailsScreen.this, DroneViewActivity.class);
                intent.putExtra("URL", videoURL.trim());
                //intent.putExtra("Title",mVirtualTourVideoList.get(0).getName().trim());
                startActivity(intent);
            }else if(type.equalsIgnoreCase(VirtualTourType.driveView.toString())){
                Intent intent = new Intent(ProjectDetailsScreen.this,DriveViewScreen.class);
                intent.putExtra("URL",videoURL.trim());
                startActivity(intent);
            }
        }
    }

    private void setTintColor(int color){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            favoriteIconToolbar.setColorFilter(ContextCompat.getColor(this, color), android.graphics.PorterDuff.Mode.MULTIPLY);
        }else{
            favoriteIconToolbar.setColorFilter(getResources().getColor( color), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    private void showSnackBar(String errorMsg){
        try {
            final Snackbar snackbar = Snackbar
                    .make(getRootView(), errorMsg, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                            long projectId = getIntent().getLongExtra(Constants.IntentKeyConstants.PROJECT_ID,-1);
                            long userId = getIntent().getLongExtra(Constants.ServerConstants.USER_ID,-1);
                            if(viewModel != null) {
                                viewModel.getProjectDetails(projectId, userId);
                            }
                        }
                    });

            snackbar.setActionTextColor(Color.WHITE);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.uber_red));
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.ProjectDetailsScreen);


    }

}
