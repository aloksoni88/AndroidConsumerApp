package com.clicbrics.consumer.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.blogEndPoint.model.BlogTag;
import com.buy.housing.backend.newsEndPoint.model.News;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.ThreeTwoImageView;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

public class ShareActivity extends BaseActivity {

    private static final String TAG = ShareActivity.class.getSimpleName();
    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        //initToolbar();
        initView();
    }

    /*private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_share_activity_toolbar);
        TextView toolbarTitle = findViewById(R.id.id_share_activity_toolbar_title);
        setSupportActionBar(toolbar);
        if(getIntent().hasExtra("Type")){
            if(getIntent().getStringExtra("Type").equalsIgnoreCase("news")) {
                toolbarTitle.setText("News");
            }else if(getIntent().getStringExtra("Type").equalsIgnoreCase("blog")){
                toolbarTitle.setText("Articles");
            }else{
                toolbarTitle.setText("Clicbrics");
            }
        }else {
            toolbarTitle.setText("Clicbrics");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }*/

    private void initView(){
        TextView messageText = findViewById(R.id.id_message_text);
        TextView readAllText = findViewById(R.id.id_read_all_txt);
        LinearLayout readAllLayout = findViewById(R.id.id_read_all_layout);

        if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("news")){
            messageText.setText("OOPs,this news is not available at this time.");
            readAllText.setText("Read All News");
            readAllLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShareActivity.this,BlogListActivity.class);
                    intent.putExtra("Position",0);
                    if(getIntent().hasExtra("TotalNewsCount") && getIntent().getIntExtra("TotalNewsCount",0) != 0) {
                        intent.putExtra("TotalNewsCount", getIntent().getIntExtra("TotalNewsCount",0));
                    }else{
                        intent.putExtra("TotalNewsCount", 0);
                        intent.putExtra("isFromShareActivity",true);
                    }
                    //intent.putExtra("isShared",true);
                    intent.putExtra("ArticleType","news");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }else if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("blog")){
            messageText.setText("OOPs,this article is not available at this time.");
            readAllText.setText("Read All Articles");
            readAllLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShareActivity.this,BlogListActivity.class);
                    intent.putExtra("Position",0);
                    if(getIntent().hasExtra("TotalBlogCount") && getIntent().getIntExtra("TotalBlogCount",0) != 0) {
                        intent.putExtra("TotalBlogCount", getIntent().getIntExtra("TotalBlogCount",0));
                    }else{
                        intent.putExtra("TotalBlogCount", 0);
                        intent.putExtra("isFromShareActivity","ShareActivity");
                    }
                    //intent.putExtra("isShared",true);
                    intent.putExtra("ArticleType","blog");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }else if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("homedecor")){
            messageText.setText("OOPs,this Decor Idea is not available at this time.");
            readAllText.setText("Read All Home Decor Ideas");
            readAllLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShareActivity.this,DecorDetailViewActivity.class);
                    intent.putExtra("Position",0);
                    if(getIntent().hasExtra("TotalDecorCount") && getIntent().getIntExtra("TotalDecorCount",0) != 0) {
                        intent.putExtra("TotalDecorCount", getIntent().getIntExtra("TotalDecorCount",0));
                    }else{
                        intent.putExtra("TotalDecorCount", 0);
                        intent.putExtra("isFromShareActivity","ShareActivity");
                    }
                    intent.putExtra("ArticleType","homedecor");
                    //intent.putExtra("isShared",true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }else{
            messageText.setText("OOPs,the current message is not available at this time.");
            readAllText.setText("Back");
            readAllLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShareActivity.this, HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()){
                case android.R.id.home:{
                    if(getIntent().hasExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION)
                            && getIntent().getBooleanExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,false)){
                        Intent intent = new Intent(this, HomeScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else {
                        finish();
                    }
                }break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            if(getIntent().hasExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION)
                    && getIntent().getBooleanExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,false)){
                Intent intent = new Intent(this, HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }else {
                finish();
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
        new EventAnalyticsHelper().logUsageStatsEvents(ShareActivity.this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.ShareActivity);

    }
}
