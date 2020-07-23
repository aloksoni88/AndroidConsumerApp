package com.clicbrics.consumer.framework.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.buy.housing.backend.blogEndPoint.BlogEndPoint;
import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.blogEndPoint.model.BlogListResponse;
import com.buy.housing.backend.blogEndPoint.model.BlogTag;
import com.buy.housing.backend.decorEndPoint.DecorEndPoint;
import com.buy.housing.backend.decorEndPoint.model.Decor;
import com.buy.housing.backend.decorEndPoint.model.DecorListResponse;
import com.buy.housing.backend.newsEndPoint.NewsEndPoint;
import com.buy.housing.backend.newsEndPoint.model.News;
import com.buy.housing.backend.newsEndPoint.model.NewsListResponse;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.UpdateUserStatResponse;
import com.clicbrics.consumer.BuildConfig;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.BlogListActivity;
import com.clicbrics.consumer.activities.DecorDetailViewActivity;
import com.clicbrics.consumer.activities.NoInternet;
import com.clicbrics.consumer.activities.PickCity;
import com.clicbrics.consumer.activities.ShareActivity;
import com.clicbrics.consumer.activities.WebviewActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.google.api.client.http.HttpHeaders;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;


public class SplashActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler mHandler;

    private UserEndPoint mUserEndPoint;
    private NewsEndPoint mNewsEndPoint;
    private BlogEndPoint mBlogEndPoint;
    private DecorEndPoint mDecorEndPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        /*if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }*/
        // to resume the activity at same stack if user press home button
        /*if (!isTaskRoot())
        {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }*/
        mHandler = new Handler();
        buildRegService();
        loadFavorites();
        //checkUpdateStatus();
        getLaunchingActivity();

    }

    //method to init VersionEndPoint
    protected void buildRegService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }

    private void buildNewsEndPointService(){
        mNewsEndPoint = EndPointBuilder.getNewsEndPoint();
    }

    private void buildBlogEndPointService(){
        mBlogEndPoint = EndPointBuilder.getBlogEndPoint();
    }

    private void buildDecorEndPointService(){
        mDecorEndPoint = EndPointBuilder.getDecorEndPoint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callUpdateStatAPI();
    }


    private void loadFavorites() {
        try {
            if (UtilityMethods.getLongInPref(this, Constants.ServerConstants.USER_ID, -1) != -1) {
                //     && UtilityMethods.isInternetConnected(this)) {
                if (UtilityMethods.favoriteIDs != null) {
                    UtilityMethods.favoriteIDs.clear();
                }

                UtilityMethods.favoriteIDs = UtilityMethods.getWishListFromPrefs(SplashActivity.this,
                        Constants.AppConstants.PROJECT_ID_SET);
                //getFavoritesList(UtilityMethods.getLongInPref(this, Constants.ServerConstants.USER_ID, -1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*private void getLaunchingActivity(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent =  null;
                    String action = getIntent().getAction();
                    Uri data = getIntent().getData();
                    String saveCity = UtilityMethods.getStringInPref(SplashActivity.this, Constants.AppConstants.SAVED_CITY, "");
                    boolean isFirstLaunch = UtilityMethods.getBooleanInPref(SplashActivity.this, Constants.SharedPreferConstants.FIRST_LAUNCH, false);
                    if(action != null && data != null) {
                        if (data != null && !TextUtils.isEmpty(data.toString())) {
                            UtilityMethods.saveBooleanInPref(SplashActivity.this, Constants.SharedPreferConstants.IS_USING_DL, true);
                        }
                        if(TextUtils.isEmpty(saveCity)){
                            if(UtilityMethods.isInternetConnected(SplashActivity.this)) {
                                intent = new Intent(SplashActivity.this, PickCity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, NoInternet.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else{
                            intent = new Intent(SplashActivity.this, WebviewActivity.class);
                            intent.putExtra("Title","Clicbrics");
                            intent.putExtra("URL",data.toString());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        Log.d(TAG, "firstLaunch->" + isFirstLaunch);
                        if (isFirstLaunch) {
                            if (UtilityMethods.isInternetConnected(SplashActivity.this)) {
                                intent = new Intent(SplashActivity.this, PickCity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, NoInternet.class);
                            }
                        }else if(TextUtils.isEmpty(saveCity)){
                            UtilityMethods.saveBooleanInPref(SplashActivity.this,Constants.SharedPreferConstants.FIRST_LAUNCH,true);
                            if (UtilityMethods.isInternetConnected(SplashActivity.this)) {
                                intent = new Intent(SplashActivity.this, PickCity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, NoInternet.class);
                            }
                        }else {
                            if (UtilityMethods.isInternetConnected(SplashActivity.this)) {
                                intent = new Intent(SplashActivity.this, HomeScreen.class);
                            } else {
                                intent = new Intent(SplashActivity.this, NoInternet.class);
                            }
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intent;
                    if(TextUtils.isEmpty(UtilityMethods.getStringInPref(SplashActivity.this,
                            Constants.AppConstants.SAVED_CITY, ""))) {
                        UtilityMethods.saveBooleanInPref(SplashActivity.this,Constants.SharedPreferConstants.FIRST_LAUNCH,true);
                        intent = new Intent(SplashActivity.this,PickCity.class);
                    }else{
                        intent = new Intent(SplashActivity.this, HomeScreen.class);
                    }
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }, 250);
    }*/

    private void getLaunchingActivity(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent =  null;
                    String action = getIntent().getAction();
                    Uri intentURI = getIntent().getData();
                    String saveCity = UtilityMethods.getStringInPref(SplashActivity.this, Constants.AppConstants.SAVED_CITY, "");
                    boolean isFirstLaunch = UtilityMethods.getBooleanInPref(SplashActivity.this, Constants.SharedPreferConstants.FIRST_LAUNCH, false);
                    if(action != null && intentURI != null) {
                        String data = intentURI.toString().replace("https://","").replace("http://","");
                        if (intentURI != null && !TextUtils.isEmpty(intentURI.toString())) {
                            UtilityMethods.saveBooleanInPref(SplashActivity.this, Constants.SharedPreferConstants.IS_USING_DL, true);
                        }

                        if(TextUtils.isEmpty(saveCity)){
                            if(UtilityMethods.isInternetConnected(SplashActivity.this)) {
                                intent = new Intent(SplashActivity.this, PickCity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, NoInternet.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else if((data.toString().startsWith("www.clicbrics.com/project/")
                                 || data.toString().startsWith("clicbrics.com/project/") )){
                            String projectIdStr = UtilityMethods.getProjectIdFromURL(intentURI.toString());
                            String projectId = projectIdStr.substring(projectIdStr.lastIndexOf('-')+1,projectIdStr.length());
                            intent = new Intent(SplashActivity.this, ProjectDetailsScreen.class);
                            intent.putExtra("ISDirectCall",true);
                            intent.putExtra(Constants.IntentKeyConstants.IS_DEEPLINK,true);
                            intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, Long.valueOf(projectId));
                            Log.i(TAG, "Deeplink ProjectDetailActivity - data -> " + intentURI.toString() );
                            Log.i(TAG, "Deeplink Full Project ID -> " + projectIdStr);
                            Log.i(TAG, "Deeplink Project ID -> " + projectId);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else if(data.toString().contains("www.clicbrics.com/news-and-articles/")){
                            if(data.toString().startsWith("www.clicbrics.com/news-and-articles/news-detail/")){
                                String url = data.toString();
                                String title = url.replace("www.clicbrics.com/news-and-articles/news-detail/","");
                                showNewsDetails(title,intentURI.toString());
                            }else if(data.toString().startsWith("www.clicbrics.com/news-and-articles/post/")){
                                String url = data.toString();
                                String title = url.replace("www.clicbrics.com/news-and-articles/post/","");
                                showBlogDetails(title,intentURI.toString());
                            }else if(data.toString().equals("www.clicbrics.com/news-and-articles/news")
                                    || data.toString().equals("www.clicbrics.com/news-and-articles/news/")){
                                intent = new Intent(SplashActivity.this, HomeScreen.class);
                                intent.putExtra("selectedTab","news");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else if(data.toString().equals("www.clicbrics.com/news-and-articles/")
                                    || data.toString().equals("www.clicbrics.com/news-and-articles/")){
                                intent = new Intent(SplashActivity.this, HomeScreen.class);
                                intent.putExtra("selectedTab","articles");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                intent = new Intent(SplashActivity.this, HomeScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }else if(data.toString().startsWith("www.clicbrics.com/home-decor-ideas")){
                            if(data.toString().startsWith("www.clicbrics.com/home-decor-ideas/post/")){
                                String url = data.toString();
                                String title = url.replace("www.clicbrics.com/home-decor-ideas/post/","");
                                showHomeDecorDetail(title,intentURI.toString());
                            }else if(data.toString().startsWith("www.clicbrics.com/home-decor-ideas/list/?title=")){
                                String url = data.toString();
                                String title = url.replace("www.clicbrics.com/home-decor-ideas/list/?title=","");
                                showHomeDecorDetail(title,intentURI.toString());
                            }else if(data.toString().equals("www.clicbrics.com/home-decor-ideas/list")
                                    || data.toString().equals("www.clicbrics.com/home-decor-ideas/list/")){
                                intent = new Intent(SplashActivity.this, HomeScreen.class);
                                intent.putExtra("selectedTab","homeDecor");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                intent = new Intent(SplashActivity.this, HomeScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }else if(data.toString().equalsIgnoreCase("www.clicbrics.com")
                                || data.toString().equalsIgnoreCase("www.clicbrics.com/")
                                || data.toString().equalsIgnoreCase("clicbrics.com")
                                || data.toString().equalsIgnoreCase("clicbrics.com/")){
                            Log.i(TAG, "Deeplink HomeActivity - data -> " + data.toString() );
                            intent = new Intent(SplashActivity.this, HomeScreen.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else{
                            Log.i(TAG, "run: need to open app on web view");
                            intent = new Intent(SplashActivity.this, WebviewActivity.class);
                            intent.putExtra("Title","Clicbrics");
                            intent.putExtra("URL",intentURI.toString());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        Log.d(TAG, "firstLaunch->" + isFirstLaunch);
                        if (isFirstLaunch) {
                            if (UtilityMethods.isInternetConnected(SplashActivity.this)) {
                                intent = new Intent(SplashActivity.this, PickCity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, NoInternet.class);
                            }
                        }else if(TextUtils.isEmpty(saveCity)){
                            UtilityMethods.saveBooleanInPref(SplashActivity.this,Constants.SharedPreferConstants.FIRST_LAUNCH,true);
                            if (UtilityMethods.isInternetConnected(SplashActivity.this)) {
                                intent = new Intent(SplashActivity.this, PickCity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, NoInternet.class);
                            }
                        }else {
                            if (UtilityMethods.isInternetConnected(SplashActivity.this)) {
                                intent = new Intent(SplashActivity.this, HomeScreen.class);
                            } else {
                                intent = new Intent(SplashActivity.this, NoInternet.class);
                            }
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intent;
                    if(TextUtils.isEmpty(UtilityMethods.getStringInPref(SplashActivity.this,
                            Constants.AppConstants.SAVED_CITY, ""))) {
                        UtilityMethods.saveBooleanInPref(SplashActivity.this,Constants.SharedPreferConstants.FIRST_LAUNCH,true);
                        intent = new Intent(SplashActivity.this,PickCity.class);
                    }else{
                        intent = new Intent(SplashActivity.this, HomeScreen.class);
                    }
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }, 250);
    }

    private void callUpdateStatAPI(){
        final int updateStatTime = UtilityMethods.getIntInPref(SplashActivity.this,Constants.AppConstants.UPDATE_STAT_TIME,-1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int currentDay = calendar.get(Calendar.DATE);
        Log.i(TAG, "Current date -> " + currentDay);

        if(updateStatTime == -1 || currentDay != updateStatTime) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final long userId = UtilityMethods.getLongInPref(SplashActivity.this,Constants.ServerConstants.USER_ID,-1);
                    final long virtualUID = UtilityMethods.getLongInPref(SplashActivity.this,Constants.AppConstants.VIRTUAL_UID,-1);
                    try {
                        UserEndPoint.UpdateStat updateStat = mUserEndPoint.updateStat(Constants.AppConstants.SOURCE);
                        if(userId != -1){
                            updateStat.setUId(userId);
                        }else if(virtualUID != -1){
                            updateStat.setUId(virtualUID);
                        }
                        UpdateUserStatResponse updateUserStatResponse = updateStat.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        if(updateUserStatResponse != null && updateUserStatResponse.getStatus()){
                            long virtualId = updateUserStatResponse.getUid();
                            UtilityMethods.saveLongInPref(SplashActivity.this,Constants.AppConstants.VIRTUAL_UID,virtualId);
                            UtilityMethods.saveIntInPref(SplashActivity.this,Constants.AppConstants.UPDATE_STAT_TIME,currentDay);
                            Log.i(TAG, "UpdateStat Response successfull... " + " Virtual ID -> " + virtualId);
                        }
                        else if(updateUserStatResponse != null && !updateUserStatResponse.getErrorMessage().isEmpty()){
                            Log.i(TAG, "UpdateStat Response Failed... " + " Error Message -> " + updateUserStatResponse.getErrorMessage());
                        }
                        else{
                            Log.i(TAG, "UpdateStat Response Failed... " + " Error Message null -> ");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
        TrackAnalytics.trackEvent("SplashActivity", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);
    }

    private News sharedNews = null;
    private int position = 0;
    private void showNewsDetails(final String title,final String url){
        try {
            List<News> newsList = ((HousingApplication) getApplicationContext()).getNewsList();
            if(newsList != null && !newsList.isEmpty()){
                for(int i=0; i< newsList.size(); i++){
                    News news = newsList.get(i);
                    if(news != null && !TextUtils.isEmpty(news.getSearchTitle())
                            && news.getSearchTitle().equalsIgnoreCase(title)){
                        position = i;
                        sharedNews = news;
                        break;
                    }
                }
                if(position != 0){
                    newsList.remove(position);
                    newsList.add(0,sharedNews);
                    position = 0;
                }else{
                    Intent intent = new Intent(SplashActivity.this, WebviewActivity.class);
                    intent.putExtra("Title","Clicbrics");
                    intent.putExtra("URL",url.toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }
                ((HousingApplication) getApplicationContext()).setNewsList(newsList);
            }
            if(sharedNews == null){
                if(!UtilityMethods.isInternetConnected(this)){
                    return;
                }
                buildNewsEndPointService();
                ((HousingApplication) getApplicationContext()).getNewsList().clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                            httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                            final NewsListResponse newsResponse = mNewsEndPoint.getNewsList(1L,"1",0,100)
                                    .setRequestHeaders(httpHeaders).execute();
                            if(newsResponse != null && newsResponse.getStatus() && newsResponse.getCount() != null
                                    && newsResponse.getCount() != 0) {
                                final List<News> newsList = newsResponse.getNews();
                                if (newsList != null && !newsList.isEmpty()) {
                                    //((HousingApplication) getApplicationContext()).setNewsList(newsList);
                                    for (int i = 0; i < newsList.size(); i++) {
                                        News news = newsList.get(i);
                                        if (news != null && !TextUtils.isEmpty(news.getSearchTitle())
                                                && news.getSearchTitle().equalsIgnoreCase(title)) {
                                            position = i;
                                            sharedNews = news;
                                            break;
                                        }
                                    }
                                    if(position != 0){
                                        newsList.remove(position);
                                        newsList.add(0,sharedNews);
                                        position = 0;
                                    }else{
                                        Intent intent = new Intent(SplashActivity.this, WebviewActivity.class);
                                        intent.putExtra("Title","Clicbrics");
                                        intent.putExtra("URL",url.toString());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    ((HousingApplication) getApplicationContext()).setNewsList(newsList);
                                    Intent intent = new Intent(SplashActivity.this,BlogListActivity.class);
                                    intent.putExtra("ArticleType","news");
                                    intent.putExtra("Position",position);
                                    intent.putExtra("TotalNewsCount",newsResponse.getCount());
                                    if(sharedNews != null){
                                        if(!TextUtils.isEmpty(sharedNews.getImage())){
                                            intent.putExtra("ImageURL",sharedNews.getImage());
                                        }
                                        if(!TextUtils.isEmpty(sharedNews.getSource())){
                                            intent.putExtra("NewsSource",sharedNews.getSource());
                                        }
                                        if(sharedNews.getCreateTime() != null && sharedNews.getCreateTime() != 0){
                                            intent.putExtra("CreationTime",sharedNews.getCreateTime());
                                        }
                                        if(!TextUtils.isEmpty(sharedNews.getTitle())){
                                            intent.putExtra("Title",sharedNews.getTitle());
                                        }
                                        if(!TextUtils.isEmpty(sharedNews.getDescription())){
                                            intent.putExtra("Description",sharedNews.getDescription());
                                        }
                                        if(!TextUtils.isEmpty(sharedNews.getSearchTitle())){
                                            intent.putExtra("SearchTitle",sharedNews.getSearchTitle());
                                        }
                                        if(!TextUtils.isEmpty(sharedNews.getClickURL())){
                                            intent.putExtra("NewsClickURL",sharedNews.getClickURL());
                                        }
                                    }
                                    intent.putExtra("isShared",true);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Intent intent;
                            if(TextUtils.isEmpty(UtilityMethods.getStringInPref(SplashActivity.this,
                                    Constants.AppConstants.SAVED_CITY, ""))) {
                                //intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                                intent = new Intent(SplashActivity.this, PickCity.class);
                            }else{
                                intent = new Intent(SplashActivity.this, HomeScreen.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).start();
            }else{
                Intent intent = new Intent(SplashActivity.this,BlogListActivity.class);
                intent.putExtra("ArticleType","news");
                intent.putExtra("Position",position);
                intent.putExtra("isShared",true);
                intent.putExtra("TotalNewsCount",newsList.size());
                if(sharedNews != null){
                    if(!TextUtils.isEmpty(sharedNews.getImage())){
                        intent.putExtra("ImageURL",sharedNews.getImage());
                    }
                    if(!TextUtils.isEmpty(sharedNews.getSource())){
                        intent.putExtra("NewsSource",sharedNews.getSource());
                    }
                    if(sharedNews.getCreateTime() != null && sharedNews.getCreateTime() != 0){
                        intent.putExtra("CreationTime",sharedNews.getCreateTime());
                    }
                    if(!TextUtils.isEmpty(sharedNews.getTitle())){
                        intent.putExtra("Title",sharedNews.getTitle());
                    }
                    if(!TextUtils.isEmpty(sharedNews.getDescription())){
                        intent.putExtra("Description",sharedNews.getDescription());
                    }
                    if(!TextUtils.isEmpty(sharedNews.getSearchTitle())){
                        intent.putExtra("SearchTitle",sharedNews.getSearchTitle());
                    }
                    if(!TextUtils.isEmpty(sharedNews.getClickURL())){
                        intent.putExtra("NewsClickURL",sharedNews.getClickURL());
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent;
            if(TextUtils.isEmpty(UtilityMethods.getStringInPref(SplashActivity.this,
                    Constants.AppConstants.SAVED_CITY, ""))) {
                //intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                intent = new Intent(SplashActivity.this, PickCity.class);
            }else{
                intent = new Intent(SplashActivity.this, HomeScreen.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private Blog sharedBlog;
    private void showBlogDetails(final String title, final String url){
        try {
            List<Blog> blogList = ((HousingApplication) getApplicationContext()).getBlogList();
            if(blogList != null && !blogList.isEmpty()){
                for(int i=0; i< blogList.size(); i++){
                    Blog blog = blogList.get(i);
                    if(blog != null && !TextUtils.isEmpty(blog.getSearchTitle())
                            && blog.getSearchTitle().equalsIgnoreCase(title)){
                        position = i;
                        sharedBlog = blog;
                        break;
                    }
                }
                if(position != 0){
                    blogList.remove(position);
                    blogList.add(0,sharedBlog);
                    position = 0;
                }else{
                    Intent intent = new Intent(SplashActivity.this, WebviewActivity.class);
                    intent.putExtra("Title","Clicbrics");
                    intent.putExtra("URL",url.toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }
                ((HousingApplication) getApplicationContext()).setBlogList(blogList);
            }
            if(sharedBlog == null){
                if(!UtilityMethods.isInternetConnected(this)){
                    return;
                }
                buildBlogEndPointService();
                ((HousingApplication) getApplicationContext()).getBlogList().clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                            httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                            final BlogListResponse blogResponse = mBlogEndPoint.getBlogs(1L,"1",0,100)
                                    .setRequestHeaders(httpHeaders).execute();
                            if(blogResponse != null && blogResponse.getStatus() && blogResponse.getCount() != null
                                    && blogResponse.getCount() != 0) {
                                final List<Blog> blogList = blogResponse.getBlogs();
                                if (blogList != null && !blogList.isEmpty()) {
                                    ((HousingApplication) getApplicationContext()).setBlogList(blogList);
                                    for (int i = 0; i < blogList.size(); i++) {
                                        Blog blog = blogList.get(i);
                                        if (blog != null && !TextUtils.isEmpty(blog.getSearchTitle())
                                                && blog.getSearchTitle().equalsIgnoreCase(title)) {
                                            position = i;
                                            sharedBlog = blog;
                                            break;
                                        }
                                    }
                                    if(position != 0){
                                        blogList.remove(position);
                                        blogList.add(0,sharedBlog);
                                        position = 0;
                                    }else{
                                        Intent intent = new Intent(SplashActivity.this, WebviewActivity.class);
                                        intent.putExtra("Title","Clicbrics");
                                        intent.putExtra("URL",url.toString());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    ((HousingApplication) getApplicationContext()).setBlogList(blogList);
                                    Intent intent = new Intent(SplashActivity.this,BlogListActivity.class);
                                    intent.putExtra("Position",position);
                                    intent.putExtra("TotalBlogCount",blogResponse.getCount());
                                    intent.putExtra("ArticleType","blog");
                                    if(sharedBlog != null){
                                        if(!TextUtils.isEmpty(sharedBlog.getSTitleImage())){
                                            intent.putExtra("ImageURL",sharedBlog.getSTitleImage());
                                        }
                                        if(!TextUtils.isEmpty(sharedBlog.getCreateByName())){
                                            intent.putExtra("Author",sharedBlog.getCreateByName());
                                        }
                                        if(sharedBlog.getCreateTime() != null && sharedBlog.getCreateTime() != 0){
                                            intent.putExtra("CreationTime",sharedBlog.getCreateTime());
                                        }
                                        if(!TextUtils.isEmpty(sharedBlog.getTitle())){
                                            intent.putExtra("Title",sharedBlog.getTitle());
                                        }
                                        if(!TextUtils.isEmpty(sharedBlog.getExcerpt())){
                                            intent.putExtra("Description",sharedBlog.getExcerpt());
                                        }
                                        if(!TextUtils.isEmpty(sharedBlog.getSearchTitle())){
                                            intent.putExtra("SearchTitle",sharedBlog.getSearchTitle());
                                        }
                                        if(!TextUtils.isEmpty(sharedBlog.getContent())){
                                            intent.putExtra("BlogContent",sharedBlog.getContent());
                                        }
                                        if (sharedBlog.getBlogTags() != null && !sharedBlog.getBlogTags().isEmpty()) {
                                            List<BlogTag> blogTags = sharedBlog.getBlogTags();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(blogTags);
                                            intent.putExtra("BlogTags", json);
                                        }
                                    }
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("isShared",true);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Intent intent;
                            if(TextUtils.isEmpty(UtilityMethods.getStringInPref(SplashActivity.this,
                                    Constants.AppConstants.SAVED_CITY, ""))) {
                                //intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                                intent = new Intent(SplashActivity.this, PickCity.class);
                            }else{
                                intent = new Intent(SplashActivity.this, HomeScreen.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).start();
            }else{
                Intent intent = new Intent(SplashActivity.this,BlogListActivity.class);
                intent.putExtra("Position",position);
                intent.putExtra("TotalBlogCount",blogList.size());
                intent.putExtra("ArticleType","blog");
                intent.putExtra("isShared",true);
                if(sharedBlog != null){
                    if(!TextUtils.isEmpty(sharedBlog.getSTitleImage())){
                        intent.putExtra("ImageURL",sharedBlog.getSTitleImage());
                    }
                    if(!TextUtils.isEmpty(sharedBlog.getCreateByName())){
                        intent.putExtra("Author",sharedBlog.getCreateByName());
                    }
                    if(sharedBlog.getCreateTime() != null && sharedBlog.getCreateTime() != 0){
                        intent.putExtra("CreationTime",sharedBlog.getCreateTime());
                    }
                    if(!TextUtils.isEmpty(sharedBlog.getTitle())){
                        intent.putExtra("Title",sharedBlog.getTitle());
                    }
                    if(!TextUtils.isEmpty(sharedBlog.getExcerpt())){
                        intent.putExtra("Description",sharedBlog.getExcerpt());
                    }
                    if(!TextUtils.isEmpty(sharedBlog.getSearchTitle())){
                        intent.putExtra("SearchTitle",sharedBlog.getSearchTitle());
                    }
                    if(!TextUtils.isEmpty(sharedBlog.getContent())){
                        intent.putExtra("BlogContent",sharedBlog.getContent());
                    }
                    if (sharedBlog.getBlogTags() != null && !sharedBlog.getBlogTags().isEmpty()) {
                        List<BlogTag> blogTags = sharedBlog.getBlogTags();
                        Gson gson = new Gson();
                        String json = gson.toJson(blogTags);
                        intent.putExtra("BlogTags", json);
                    }
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent;
            if(TextUtils.isEmpty(UtilityMethods.getStringInPref(SplashActivity.this,
                    Constants.AppConstants.SAVED_CITY, ""))) {
                //intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                intent = new Intent(SplashActivity.this, PickCity.class);
            }else{
                intent = new Intent(SplashActivity.this, HomeScreen.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private Decor sharedDecor;
    private void showHomeDecorDetail(final String title, final String url){
        try {
            if(!UtilityMethods.isInternetConnected(this)){
                List<Decor> decorList = ((HousingApplication) getApplicationContext()).getDecorList();
                if(decorList != null && !decorList.isEmpty()){
                    for(int i=0; i< decorList.size(); i++){
                        Decor decor = decorList.get(i);
                        if(decor != null && !TextUtils.isEmpty(decor.getSearchTitle())
                                && decor.getSearchTitle().equalsIgnoreCase(title)){
                            position = i;
                            sharedDecor = decor;
                            break;
                        }
                    }
                    if(position != 0){
                        decorList.remove(position);
                        decorList.add(0,sharedDecor);
                        position = 0;
                    }else{
                        Intent intent = new Intent(SplashActivity.this, WebviewActivity.class);
                        intent.putExtra("Title","Clicbrics");
                        intent.putExtra("URL",url.toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    ((HousingApplication) getApplicationContext()).setDecorList(decorList);
                }
                if(sharedDecor == null){
                    Intent intent = new Intent(SplashActivity.this,ShareActivity.class);
                    intent.putExtra("TotalDecorCount",((HousingApplication) getApplicationContext()).getDecorList().size());
                    intent.putExtra("ArticleType","homedecor");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("isShared",true);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this,DecorDetailViewActivity.class);
                    intent.putExtra("Position",position);
                    intent.putExtra("TotalDecorCount",((HousingApplication) getApplicationContext()).getDecorList().size());
                    intent.putExtra("ArticleType","homedecor");
                    intent.putExtra("isShared",true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }else{
                showProgressBar();
                buildDecorEndPointService();
                ((HousingApplication) getApplicationContext()).getDecorList().clear();
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
                                    && decorListResponse.getCount() != 0) {
                                final List<Decor> decorList = decorListResponse.getDecors();
                                if (decorList != null && !decorList.isEmpty()) {
                                    ((HousingApplication) getApplicationContext()).setDecorList(decorList);
                                    for (int i = 0; i < decorList.size(); i++) {
                                        Decor decor = decorList.get(i);
                                        if (decor != null && !TextUtils.isEmpty(decor.getSearchTitle())
                                                && decor.getSearchTitle().equalsIgnoreCase(title)) {
                                            position = i;
                                            sharedDecor = decor;
                                            break;
                                        }
                                    }
                                    if(position != 0){
                                        decorList.remove(position);
                                        decorList.add(0,sharedDecor);
                                        position = 0;
                                    }else{
                                        Intent intent = new Intent(SplashActivity.this, WebviewActivity.class);
                                        intent.putExtra("Title","Clicbrics");
                                        intent.putExtra("URL",url.toString());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    ((HousingApplication) getApplicationContext()).setDecorList(decorList);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissProgressBar();
                                            Intent intent = new Intent(SplashActivity.this,DecorDetailViewActivity.class);
                                            intent.putExtra("Position",position);
                                            intent.putExtra("TotalDecorCount",decorListResponse.getCount());
                                            intent.putExtra("ArticleType","homedecor");
                                            intent.putExtra("isShared",true);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }else{
                                    errorMsg = "Home Decor List is empty";
                                }
                            }else{
                                errorMsg = decorListResponse.getErrorMessage() != null ? decorListResponse.getErrorMessage() : "Home Decor List is empty";
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            errorMsg = "Home Decor list is empty";
                        }
                        if(!TextUtils.isEmpty(errorMsg)){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent;
                                    if(TextUtils.isEmpty(UtilityMethods.getStringInPref(SplashActivity.this,
                                            Constants.AppConstants.SAVED_CITY, ""))) {
                                        //intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                                        intent = new Intent(SplashActivity.this, PickCity.class);
                                    }else{
                                        intent = new Intent(SplashActivity.this, HomeScreen.class);
                                    }
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent;
            if(TextUtils.isEmpty(UtilityMethods.getStringInPref(SplashActivity.this,
                    Constants.AppConstants.SAVED_CITY, ""))) {
                //intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                intent = new Intent(SplashActivity.this, PickCity.class);
            }else{
                intent = new Intent(SplashActivity.this, HomeScreen.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}