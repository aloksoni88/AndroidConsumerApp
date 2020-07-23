package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
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

import java.net.URI;
import java.net.URL;

public class WebviewActivity extends BaseActivity {

    private static final String TAG = WebviewActivity.class.getSimpleName();
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        /*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        UtilityMethods.setStatusBarColor(this,R.color.status_bar_color_dark);*/
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        initToolbar();
        initView();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.id_webview_activity_toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.id_webview_activity_title);
        if(getIntent().hasExtra("Title") && !TextUtils.isEmpty(getIntent().getStringExtra("Title"))){
            toolbarTitle.setText(getIntent().getStringExtra("Title") +"");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
    }

    private void initView(){
        try {
            webView = (WebView) findViewById(R.id.id_webview_activity_webview);
            if(getIntent().hasExtra("URL") && !TextUtils.isEmpty(getIntent().getStringExtra("URL"))) {
                String pageURL = getIntent().getStringExtra("URL");
                Log.i(TAG, "initView: URL -> " + pageURL);
                webView.loadUrl(pageURL);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.canGoBackOrForward(5);

                Log.i(TAG, "initView: webview loaded url -> " + webView.getUrl());
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.i(TAG, "shouldOverrideUrlLoading: URL -> " + url);
                        if (url.startsWith("tel:")) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                            startActivity(intent);
                            return true;
                        }else if (url.startsWith("mailto:")) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                            startActivity(intent);
                            return true;
                        }else if(url != null && url.startsWith("whatsapp://") || url.startsWith("https://wa.me")
                                || url.startsWith("https://api.whatsapp.com")){
                            try {
                                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                                if(intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                                return true;
                            } catch (Exception use) {
                                Log.e(TAG, use.getMessage());
                            }
                        }
                        view.loadUrl(url);
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
                        findViewById(R.id.progress).setVisibility(View.GONE);
                    }
                });
                //sendData(urlObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        } else if(UtilityMethods.getBooleanInPref(WebviewActivity.this,Constants.SharedPreferConstants.IS_USING_DL,false)){
            UtilityMethods.saveBooleanInPref(this, Constants.SharedPreferConstants.IS_USING_DL,false);
            Intent intent = new Intent(this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                if(UtilityMethods.getBooleanInPref(WebviewActivity.this,Constants.SharedPreferConstants.IS_USING_DL,false)){
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
}
