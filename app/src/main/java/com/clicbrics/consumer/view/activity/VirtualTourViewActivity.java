package com.clicbrics.consumer.view.activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.VideoView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.concurrent.TimeUnit;

/**
 * Created by Alok on 09-10-2018.
 */
public class VirtualTourViewActivity extends BaseActivity {
    private static final String TAG = "VirtualTourView";
    private WebView webView;
    private boolean isErrorShown = false;
    private String pageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_tour_view);
        /*if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
       */
        initView();
        //UtilityMethods.setStatusBarColor(this,R.color.overlay_color3);
        //initToolbar();
        /*findViewById(R.id.id_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/
    }

    /*private void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.id_matterport_toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.id_matterport_view_title);
        if(getIntent().hasExtra("Title") && !TextUtils.isEmpty(getIntent().getStringExtra("Title"))){
            toolbarTitle.setText(getIntent().getStringExtra("Title") +"");
        }
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
    }*/

    private void initView(){
        try {
            webView = findViewById(R.id.id_virtualtour_view);
            if(getIntent().hasExtra("URL") && !TextUtils.isEmpty(getIntent().getStringExtra("URL"))) {
                pageURL = getIntent().getStringExtra("URL")+"&title=0";
                //pageURL = createIFrameURL(pageURL);
                //webView.loadData(pageURL,"text/html",null);
                Log.i(TAG, "initView: URL -> " + pageURL);
                loadWebview();
                //sendData(urlObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWebview(){
        if(!UtilityMethods.isInternetConnected(this)){
            showErrorSnackbar();
            return;
        }
        isErrorShown = false;
        webView.loadUrl(pageURL.replaceAll(" ","%20"));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        Log.i(TAG, "initView: webview loaded url -> " + webView.getUrl());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(TAG, "onPageStarted: " + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadData(url,"text/html",null);
                Log.i(TAG, "shouldOverrideUrlLoading: " + url);
                if(!UtilityMethods.isInternetConnected(VirtualTourViewActivity.this)){
                    showErrorSnackbar();
                    return false;
                }else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if(!isErrorShown){
                    showErrorSnackbar();
                }
                Log.i(TAG, "onReceivedError 1 - error code -> " + error.getErrorCode()  + " error desc " + error.getDescription() + " request -> " + request);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if(!isErrorShown){ // net::ERR_CONNECTION_ABORTED i.e internet connection issue
                    showErrorSnackbar();
                }
                Log.i(TAG, "onReceivedError 2 - description -> " + description + " Error code " + errorCode + " failingUrl -> " + failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    private String createIFrameURL(String url){
        String html = "";
        html += "<html><body>";
        html += "<iframe width=\"100%\" height=\"100%\" src=\"https://my.matterport.com/show/?m=4JEczUXyzbj&play=1\" allowvr=\"allowvr\" allowfullscreen=\"allowfullscreen\"></iframe>";
        html += "</body></html>";
        return html;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(webView != null){
            webView.removeAllViews();
            webView.clearHistory();
            webView.clearCache(true);
            webView.loadUrl("about:blank");
            webView.onPause();
            webView.removeAllViews();
            webView.destroyDrawingCache();
            webView.destroy();
            webView = null;
        }
    }

    private Snackbar snackbar;
    private long snackBarShowingTime;
    private void showErrorSnackbar(){
        isErrorShown = true;
        Log.i(TAG, "showErrorSnackbar: " + (TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - snackBarShowingTime)));
        if(TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - snackBarShowingTime) < 1000){
            snackBarShowingTime = System.currentTimeMillis();
            return;
        }
        if(snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
        }
        try {
            snackbar = Snackbar.make(findViewById(R.id.id_root_view), "Seems you lost internet connectivity.", Snackbar.LENGTH_INDEFINITE);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.uber_red));
            snackbar.setAction(R.string.try_again_btn, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadWebview();
                }
            });
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
