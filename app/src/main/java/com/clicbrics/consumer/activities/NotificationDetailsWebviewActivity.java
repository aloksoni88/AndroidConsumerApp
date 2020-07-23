package com.clicbrics.consumer.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

public class NotificationDetailsWebviewActivity extends BaseActivity {

    private static final String TAG = "NotificaitonDetailsWebview";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaiton_details_webview);

        initToolbar();
        initView();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.id_notification_detail_toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.id_notification_details_title);
        if(getIntent().hasExtra("Title") && !TextUtils.isEmpty(getIntent().getStringExtra("Title"))){
            toolbarTitle.setText(getIntent().getStringExtra("Title") +"");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);
    }

    private void initView(){
        try {
            WebView webView = (WebView) findViewById(R.id.id_notification_detail_webview);
            if(getIntent().hasExtra("URL") && !TextUtils.isEmpty(getIntent().getStringExtra("URL"))) {
                String pageURL = getIntent().getStringExtra("URL");
                Log.i(TAG, "initView: URL -> " + pageURL);
                if(pageURL.contains(getServerURL())) {
                    String userName = UtilityMethods.getStringInPref(this, Constants.AppConstants.USER_NAME_PREF_KEY, "");
                    String mobileNo = UtilityMethods.getStringInPref(this, Constants.AppConstants.MOBILE_PREF_KEY, "");
                    String parameterStr = "";
                    if(pageURL.contains("?")){
                        parameterStr = "&name=" + URLEncoder.encode(userName) + "&mobile=" + URLEncoder.encode(mobileNo);
                    }else{
                        parameterStr = "?name=" + URLEncoder.encode(userName) + "&mobile=" + URLEncoder.encode(mobileNo);
                    }
                    URL url = new URL(pageURL + parameterStr);
                    URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                    webView.loadUrl(uri.toString());
                }else{
                    URL url = new URL(pageURL);
                    URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                    webView.loadUrl(uri.toString());
                }
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);

                Log.i(TAG, "initView: webview loaded url -> " + webView.getUrl());
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
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

    private void sendData(URL urlObject){
        try {

            Log.i(TAG, "sendData: URL -> " + urlObject);
            final HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            Log.i(TAG, "Response code " + connection.getResponseMessage().toString());
                        }else{
                            Log.i(TAG, "Response error: "+ connection.getResponseCode() + ", "+connection.getResponseMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            /*final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost httpPost = new HttpPost(urlObject.toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        HttpResponse response = httpClient.execute(httpPost);
                        Log.i(TAG, "run: Response status line -> " + response.getStatusLine().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getServerURL(){
        if(BuildConfigConstants.configName.equalsIgnoreCase("housingTestServer-Config-App.png")){
            return "redrics-web-1.appspot.com/quiz";
        }else{
            return "clicbrics.com/quiz";
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.NotificationsDetailsScreen);

    }

}
