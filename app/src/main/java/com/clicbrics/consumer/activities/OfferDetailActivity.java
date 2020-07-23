package com.clicbrics.consumer.activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;


@SuppressWarnings("deprecation")
public class OfferDetailActivity extends BaseActivity {

    private static final String TAG = OfferDetailActivity.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.offer_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
        WebView webView = (WebView) findViewById(R.id.offerDetailWebview);
        if(getIntent().hasExtra("DetailLink") && !TextUtils.isEmpty(getIntent().getStringExtra("DetailLink"))){
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            String link = getIntent().getStringExtra("DetailLink");
            if(link.contains("http")|| link.contains("https")){
                webView.loadUrl(link);
            }else{
                webView.loadUrl("https://"+link);
            }
            Log.i(TAG, "onCreate: OfferDetail Link -> "+ link );
        }
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                findViewById(R.id.progress).setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
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
        TrackAnalytics.trackEvent("OfferDetailActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);
    }
}
