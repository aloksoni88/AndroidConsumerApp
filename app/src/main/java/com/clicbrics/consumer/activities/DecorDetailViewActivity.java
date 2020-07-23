package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.buy.housing.backend.blogEndPoint.BlogEndPoint;
import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.decorEndPoint.DecorEndPoint;
import com.buy.housing.backend.decorEndPoint.model.Decor;
import com.buy.housing.backend.decorEndPoint.model.DecorItem;
import com.buy.housing.backend.decorEndPoint.model.DecorListResponse;
import com.buy.housing.backend.decorEndPoint.model.DecorResponse;
import com.buy.housing.backend.newsEndPoint.NewsEndPoint;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.ArticlesPagerAdapter;
import com.clicbrics.consumer.adapter.DecorPagerAdapter;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.OnSwipeTouchListener;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DecorDetailViewActivity extends BaseActivity {

    private static final String TAG = "DecorDetailView";
    private ImageButton backImage, nextImage;
    private int backImageVisibility = View.GONE, nextImageVisibility=View.GONE;
    Animation hideAnimation;
    private DecorEndPoint mDecorEndPoint;
    private int listSize = 0;
    private int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decor_detail_view);

        initToolbar();
        initView();
    }

    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.id_decordetails_toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);
    }

    private void initView(){
        try {
            backImage = findViewById(R.id.id_back_image);
            nextImage = findViewById(R.id.id_next_image);
            hideAnimation = AnimationUtils.loadAnimation(this,R.anim.button_hide_alpha_animation);
            if(getIntent().hasExtra("Position")){
                position = getIntent().getIntExtra("Position",0);
            }
            if(getIntent().hasExtra("TotalDecorCount")){
                listSize = getIntent().getIntExtra("TotalDecorCount",0);
            }
            if(getIntent().hasExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION)
                    && getIntent().getBooleanExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,false)){
                if(getIntent().hasExtra("ArticleType")
                        && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("homedecor")){
                    getDecorPosition(getIntent().getStringExtra("HomeDecorTitle"),true);
                }else{
                    setDecorList();
                }
            }
            else{
                setDecorList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildWebService(){
        mDecorEndPoint = EndPointBuilder.getDecorEndPoint();
    }

    private void getDecorPosition(final String title, final boolean isDecorExistCheck){
        if(!UtilityMethods.isInternetConnected(this)){
            new EventAnalyticsHelper().logAPICallEvent(DecorDetailViewActivity.this, Constants.AnaylticsClassName.HomeDecorDetailScreen,
                    null, Constants.ApiName.getDecorList.toString(),Constants.AnalyticsEvents.FAILED,getResources().getString(R.string.no_internet_connection));
            UtilityMethods.showErrorSnackBar(findViewById(R.id.activity_decor_details),getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            return;
        }
        buildWebService();
        ((HousingApplication) getApplicationContext()).getDecorList().clear();
        showProgressBar();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                    httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                    final DecorListResponse decorListResponse = mDecorEndPoint.getDecorList(0,50)
                            .setRequestHeaders(httpHeaders).execute();
                    if(decorListResponse != null && decorListResponse.getStatus() && decorListResponse.getCount() != null
                            && decorListResponse.getCount() != 0){
                        final List<Decor> decorList = decorListResponse.getDecors();
                        if(decorList != null && !decorList.isEmpty()) {
                            new EventAnalyticsHelper().logAPICallEvent(DecorDetailViewActivity.this, Constants.AnaylticsClassName.HomeDecorDetailScreen,
                                    null, Constants.ApiName.getDecorList.toString(),Constants.AnalyticsEvents.SUCCESS,null);
                            listSize = decorListResponse.getCount();
                            Decor shareDecor = null;
                            if(isDecorExistCheck){
                                for(int i=0; i< decorList.size(); i++){
                                    Decor decor = decorList.get(i);
                                    if(decor != null && !TextUtils.isEmpty(decor.getSearchTitle())
                                            && decor.getSearchTitle().equalsIgnoreCase(title)){
                                        position = i;
                                        shareDecor = decor;
                                        break;
                                    }
                                }

                                if(position != 0) {
                                    decorList.remove(position);
                                    decorList.add(0, shareDecor);
                                    position = 0;
                                }
                                ((HousingApplication) getApplicationContext()).setDecorList(decorList);
                                final Decor finalSharedDecor = shareDecor;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissProgressBar();
                                        if(finalSharedDecor == null && !TextUtils.isEmpty(title)){
                                            Intent intent = new Intent(DecorDetailViewActivity.this, ShareActivity.class);
                                            intent.putExtra("TotalDecorCount", listSize);
                                            intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                                            intent.putExtra("ArticleType", "homedecor");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }else {
                                            setDecorList();
                                        }
                                    }
                                });
                            }else{
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((HousingApplication) getApplicationContext()).setDecorList(decorList);
                                        setDecorList();
                                        dismissProgressBar();
                                    }
                                });
                            }
                        }else{
                            errorMsg = "Decor View list empty!";
                        }
                    }else{
                        if(decorListResponse != null && !TextUtils.isEmpty(decorListResponse.getErrorMessage())){
                            errorMsg = decorListResponse.getErrorMessage();
                        }else{
                            errorMsg = "Something went wrong.Please try again!";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Something went wrong.Please try again!";
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            new EventAnalyticsHelper().logAPICallEvent(DecorDetailViewActivity.this, Constants.AnaylticsClassName.HomeDecorDetailScreen,
                                    null, Constants.ApiName.getDecorList.toString(),Constants.AnalyticsEvents.FAILED,errorMsg);
                            UtilityMethods.showSnackBar(findViewById(R.id.activity_decor_details),errorMsg, Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }


    private void setDecorList(){
        List<Decor> decorList = new ArrayList<>();
        decorList.addAll(((HousingApplication)getApplicationContext()).getDecorList());
        if(decorList != null && !decorList.isEmpty()){
            if(!UtilityMethods.getBooleanInPref(this, Constants.AppConstants.IS_DECOR_HELP_SCREEN_SEEN,false)){
                showHelpScreen();
                UtilityMethods.saveBooleanInPref(this,Constants.AppConstants.IS_DECOR_HELP_SCREEN_SEEN,true);
            }else{
                findViewById(R.id.id_overlay_layout).setVisibility(View.GONE);
            }
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            deviceHeight = dm.heightPixels;
            deviceWidth = dm.widthPixels;

            Log.i(TAG, "getBlogList: " + decorList.size());
            listSize = (listSize != 0) ? listSize : decorList.size();
            final ViewPager decorPager = (ViewPager) findViewById(R.id.id_decorviewpager);
            final DecorPagerAdapter adapter = new DecorPagerAdapter(this, decorList, listSize,
                    findViewById(R.id.activity_decor_details), decorPager);
            adapter.notifyDataSetChanged();
            decorPager.setAdapter(adapter);
            decorPager.setCurrentItem(position);
            decorPager.setOffscreenPageLimit(5);
            Log.i(TAG, "adapter has been called");

            /* for very first time  */
            if(position == 0){
                setArrowImageVisibility(View.GONE,View.VISIBLE);
            }else if(adapter != null && (adapter.getCount()-1) == position){
                setArrowImageVisibility(View.GONE,View.GONE);
            }else{
                setArrowImageVisibility(View.VISIBLE,View.VISIBLE);
            }
            decorPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if(position == 0){
                        setArrowImageVisibility(View.GONE,View.VISIBLE);
                    }else if(adapter != null && (adapter.getCount()-1) == position){
                        setArrowImageVisibility(View.GONE,View.GONE);
                    }else{
                        setArrowImageVisibility(View.VISIBLE,View.VISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if(state == 0) {
                        hideArrowButton(3000);
                    }
                }
            });


            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(decorPager != null){
                            decorPager.setCurrentItem(decorPager.getCurrentItem()-1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            nextImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(decorPager != null){
                            decorPager.setCurrentItem(decorPager.getCurrentItem()+1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            hideArrowButton(3000);
        }
    }

    private void showHelpScreen(){
        findViewById(R.id.id_overlay_layout).setVisibility(View.VISIBLE);
        final RelativeLayout overLayView = (RelativeLayout) findViewById(R.id.id_overlay_layout);
        overLayView.setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            public void onClick() {
                super.onClick();
                overLayView.setVisibility(View.GONE);
            }

            public void onSwipeTop() {
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.TOP);
                TransitionManager.beginDelayedTransition(overLayView,slide);
                overLayView.setVisibility(View.GONE);
            }

            public void onSwipeRight() {
                Log.i(TAG, "onSwipeRight: ");
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.RIGHT);
                TransitionManager.beginDelayedTransition(overLayView,slide);
                overLayView.setVisibility(View.GONE);
            }

            public void onSwipeLeft() {
                Log.i(TAG, "onSwipeLeft: ");
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.LEFT);
                TransitionManager.beginDelayedTransition(overLayView,slide);
                overLayView.setVisibility(View.GONE);
            }

            public void onSwipeBottom() {
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.BOTTOM);
                TransitionManager.beginDelayedTransition(overLayView,slide);
                overLayView.setVisibility(View.GONE);
            }
        });
    }

    public void setArrowImageVisibility(int backImageVisibility, int nextImageVisibility){
        if(backImage != null){
            backImage.setVisibility(backImageVisibility);
        }
        if(nextImage != null){
            nextImage.setVisibility(nextImageVisibility);
        }
        this.backImageVisibility = backImageVisibility;
        this.nextImageVisibility = nextImageVisibility;
    }

    Timer backImageTimer = null;
    Timer nextImageTimer = null;
    private void hideArrowButton(long delay){
        delay = 2500;
        try {
            if(backImageVisibility == View.VISIBLE){
                if(backImageTimer != null){
                    backImageTimer.cancel();
                }
                backImageTimer = new Timer();
                TimerTask timerTask  = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                backImage.startAnimation(hideAnimation);
                                backImage.setVisibility(View.GONE);
                            }
                        });
                    }
                };
                backImageTimer.schedule(timerTask,delay);
            }

            if(nextImageVisibility == View.VISIBLE){
                if(nextImageTimer != null){
                    nextImageTimer.cancel();
                }
                nextImageTimer = new Timer();
                TimerTask timerTask  = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                nextImage.startAnimation(hideAnimation);
                                nextImage.setVisibility(View.GONE);
                            }
                        });
                    }
                };
                nextImageTimer.schedule(timerTask,delay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showButtonOnTouchView(){
        setArrowImageVisibility(backImageVisibility,nextImageVisibility);
        hideArrowButton(3000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()){
                case android.R.id.home:{
                    if((getIntent().hasExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION)
                            && getIntent().getBooleanExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,false))
                            || (getIntent().hasExtra("isShared") && getIntent().getBooleanExtra("isShared",false))){
                        Intent intent = new Intent(this, HomeScreen.class);
                        intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,true);
                        intent.putExtra(Constants.IntentKeyConstants.NOTIFICATION_TYPE,Constants.MessageType.HomeDecor.toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else {
                        super.onBackPressed();
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            if((getIntent().hasExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION)
                    && getIntent().getBooleanExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,false))
                    || (getIntent().hasExtra("isShared") && getIntent().getBooleanExtra("isShared",false))){
                Intent intent = new Intent(this, HomeScreen.class);
                /*intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,true);
                intent.putExtra(Constants.IntentKeyConstants.NOTIFICATION_TYPE,Constants.MessageType.HomeDecor.toString());*/
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }else {
                super.onBackPressed();
            }
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.HomeDecorDetailScreen);

    }
}
