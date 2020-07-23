package com.clicbrics.consumer.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import static com.clicbrics.consumer.R.string.error;

@SuppressWarnings("deprecation")
public class TermsAndConditionActivity extends BaseActivity {

    private static final String TAG = TermsAndConditionActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        /*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        UtilityMethods.setStatusBarColor(this,R.color.status_bar_color_dark);*/
        WebView webView = (WebView) findViewById(R.id.termAndConfitionWebview);
//        webView.loadUrl("file:///android_asset/TERMSANDCONDITIONS.html");
        webView.loadUrl(Constants.AppConstants.T_AND_C_URL);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                view.getSettings().setJavaScriptEnabled(true);
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.TermsAndConditionsActivity);

       /* TrackAnalytics.trackEvent("TermsAndConditionsActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);*/
    }

}
