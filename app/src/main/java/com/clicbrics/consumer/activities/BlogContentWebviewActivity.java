package com.clicbrics.consumer.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;

public class BlogContentWebviewActivity extends BaseActivity {

    private static final String TAG = "BlogContentWebview";

    private View mRootView;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_content_webview);

        initToolbar();
        loadContentWebview();
    }

    private void initToolbar(){
        mRootView = findViewById(R.id.activity_blog_content_webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_blog_content_toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.id_blog_content_webview_title);
        if(getIntent().hasExtra("BlogTitle") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogTitle"))){
            toolbarTitle.setText(getIntent().getStringExtra("BlogTitle"));
        }else{
            toolbarTitle.setText("NEWS & ARTICLES");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(webView.canGoBack()){
                webView.goBack();
            }else{
                super.onBackPressed();
            }
        }
        return true;
    }

    private void loadContentWebview(){
        webView = (WebView) findViewById(R.id.id_content);
        if(getIntent().hasExtra("contentURL") && !TextUtils.isEmpty(getIntent().getStringExtra("contentURL"))){
            String content = getIntent().getStringExtra("contentURL");
            Log.i(TAG, "loadContentWebview: contentURL -> "+ content);
            //webView.loadDataWithBaseURL(null, formatWebviewContentImage(content), "text/html", "UTF-8", null);
            webView.loadUrl(content);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    try {
                        Log.i(TAG, "shouldOverrideUrlLoading: URL -> " + url);
                        view.getSettings().setJavaScriptEnabled(true);
                        view.getSettings().setDomStorageEnabled(true);
                        boolean isAppInstalled = false;
                        if(UtilityMethods.appInstalledOrNot(BlogContentWebviewActivity.this,getPackageName())){
                            isAppInstalled = true;
                        }
                        if(isAppInstalled) {
                            if (url.contains("clicbrics") && url.contains("/project/")) {
                                String projectIdStr = UtilityMethods.getProjectIdFromURL(url);
                                String projectId = projectIdStr.substring(projectIdStr.lastIndexOf('-') + 1, projectIdStr.length());
                                intent = new Intent(BlogContentWebviewActivity.this, ProjectDetailsScreen.class);
                                intent.putExtra("ISDirectCall", true);
                                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, Long.valueOf(projectId));
                                startActivity(intent);
                            }else if(url.contains("clicbrics") && url.contains("/city/")){
                                showAllCityProperty(url);
                            }else{
                                view.loadUrl(url);
                            }
                        }else{
                            startActivity(intent);
                        }
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        UtilityMethods.showSnackBar(mRootView,"Page not found!",Snackbar.LENGTH_LONG);
                    }catch (Exception e) {
                        startActivity(intent);
                        e.printStackTrace();
                    }
                    return true;
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Log.i(TAG, "onReceivedError 1 - error -> " + error + " request -> " + request);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    Log.i(TAG, "onReceivedError 2 - description -> " + description + " failingUrl -> " + failingUrl);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    findViewById(R.id.blogcontent_webview_progress).setVisibility(View.GONE);
                }
            });
        }
    }


    private void showAllCityProperty(String url){
        try {
            if(!UtilityMethods.isInternetConnected(this)){
                UtilityMethods.showErrorSnackBar(mRootView,getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                return;
            }
            String regExp = "", cityName = "";
            long cityId=0;
            if(url.toString().trim().contains("https://www.clicbrics.com/city/")){
                regExp = "https://www.clicbrics.com/city/";
            }else if(url.toString().trim().contains("http://www.clicbrics.com/city/")){
                regExp = "http://www.clicbrics.com/city/";
            }else if(url.toString().trim().contains("https://clicbrics.com/city/")){
                regExp = "https://clicbrics.com/city/";
            }else if(url.toString().trim().contains("http://clicbrics.com/city/")){
                regExp = "http://clicbrics.com/city/";
            }
            String[] split = url.toString().split(regExp);
            if(split[1].contains("-")){
                String[] cityArr  = split[1].split("-");
                cityName = cityArr[0].trim();
                cityId = Long.parseLong(cityArr[1].trim());
            }
            Intent intent = new Intent(BlogContentWebviewActivity.this, HomeScreen.class);
            intent.putExtra(Constants.IntentKeyConstants.SHOW_PROPERTY_BY_CITY,true);
            if(cityId != -1 & cityId != 0){
                intent.putExtra("city_id",cityId);
                UtilityMethods.saveLongInPref(this,Constants.AppConstants.SAVED_CITY_ID,cityId);
            }
            if(!TextUtils.isEmpty(cityName)){
                UtilityMethods.saveStringInPref(this,Constants.AppConstants.SAVED_CITY,cityName);
                intent.putExtra("cityName",cityName);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
