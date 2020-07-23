package com.clicbrics.consumer.customview;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.UtilityMethods;

/**
 * Created by Paras on 16-11-2016.
 */
public class CustomWebDialog extends Dialog{

    public CustomWebDialog(Context context) {
        super(context);

    }

    Button positive,negative;
    TextView titleMsgView,msgView;
    int mTitleId=-1;
    String msg = null;
    int positivetextId=-1;
    View.OnClickListener positiveListener;
    int negativeTextId =-1;
    View.OnClickListener negetiveListener;
    WebView webView;
    String mWebViewURL;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_web);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Typeface mFont = Typeface.createFromAsset(this.getContext().getAssets(),
                "fonts/FiraSans-Light.ttf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        UtilityMethods.setAppFont(mContainer, mFont, false);
        titleMsgView = (TextView)findViewById(R.id.txt_title);
        msgView = (TextView)findViewById(R.id.txt_msg);
        int width = (int)(getContext().getResources().getDisplayMetrics().widthPixels*0.75);
        msgView.getLayoutParams().width=width;
        positive = (Button)findViewById(R.id.btn_positive);
        negative = (Button)findViewById(R.id.btn_negative);
        if(mTitleId!=-1){
            titleMsgView.setText(mTitleId);
        }
        if(msg!=null){
            msgView.setText(msg);
        }

        if(positivetextId!=-1){
            positive.setText(positivetextId);
            positive.setOnClickListener(positiveListener);
            final int version = Build.VERSION.SDK_INT;
            positive.setBackgroundResource(R.drawable.btn_transparent_selector);
            positive.setVisibility(View.VISIBLE);
        }
        if(negativeTextId!=-1){
            negative.setText(negativeTextId);
            negative.setOnClickListener(negetiveListener);
            negative.setBackgroundResource(R.drawable.btn_transparent_selector);
            negative.setVisibility(View.VISIBLE);
        }
        LinearLayout normal_view_layout = (LinearLayout)findViewById(R.id.normal_view_layout);
        RelativeLayout web_view_layout = (RelativeLayout)findViewById(R.id.web_view_layout);
        if(!TextUtils.isEmpty(mWebViewURL)){

            normal_view_layout.setVisibility(View.GONE);
            web_view_layout.setVisibility(View.VISIBLE);
            ImageButton imageButton = (ImageButton)findViewById(R.id.web_view_cross);
            imageButton.setOnClickListener(positiveListener);
            WebView webView = (WebView)findViewById(R.id.web_view);
            String url =mWebViewURL;
            if(!url.startsWith("http")){
                url ="http://"+mWebViewURL;
            }
            webView.loadUrl(url);
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
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
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    findViewById(R.id.progress).setVisibility(View.GONE);
                }
            });
            //enable Javascript
            webView.getSettings().setJavaScriptEnabled(true);
        } else {
            normal_view_layout.setVisibility(View.VISIBLE);
            web_view_layout.setVisibility(View.GONE);
        }
    }

    public void setMessage(String msg){
        this.msg = msg;
    }

    public void setTitle(int id){
        mTitleId = id;
    }

    public void setPositiveButton(int textId,View.OnClickListener positiveListener){
        positivetextId = textId;
        this.positiveListener = positiveListener;
    }

    public void setNegativeButton(int textId,View.OnClickListener positiveListener){
        this.negetiveListener = positiveListener;
        this.negativeTextId = textId;

    }


    public void setmWebViewURL(String mWebViewURL) {
        this.mWebViewURL = mWebViewURL;
    }
}

