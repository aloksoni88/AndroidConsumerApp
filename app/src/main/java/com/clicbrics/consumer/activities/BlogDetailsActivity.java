package com.clicbrics.consumer.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buy.housing.backend.blogEndPoint.BlogEndPoint;
import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.blogEndPoint.model.BlogResponse;
import com.buy.housing.backend.blogEndPoint.model.BlogTag;
import com.buy.housing.backend.newsEndPoint.model.News;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.ThreeTwoImageView;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlogDetailsActivity extends BaseActivity {

    private static final String TAG = BlogDetailsActivity.class.getSimpleName();

    private View mRootView;
    List<BlogTag> blogTags = null;
    int tagTextWidth = 0;

    private BlogEndPoint mBlogEndPoint;
    private long webViewFingerDownTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        initToolbar();
        setBlogValues();
        buildWebService();
    }

    private void initToolbar(){
        mRootView = findViewById(R.id.activity_blog_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_blogdetails_toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.id_blog_detail_title);
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


    private void buildWebService(){
        mBlogEndPoint = EndPointBuilder.getBlogEndPoint();
    }

    private void setBlogValues(){
        final TextView blogAuthor = (TextView) findViewById(R.id.id_blog_author);
        TextView blogDate = (TextView) findViewById(R.id.id_blog_date);
        TextView blogTitle = (TextView) findViewById(R.id.id_blog_title);
        WebView blogContent = (WebView) findViewById(R.id.id_blog_summery);
        final ThreeTwoImageView blogImage = (ThreeTwoImageView) findViewById(R.id.id_blog_image);

        if(getIntent().hasExtra("BlogImageURL") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogImageURL"))) {
            String blogImageURL = getIntent().getStringExtra("BlogImageURL") + "=h400";
            Picasso.get().load(blogImageURL)
                    .placeholder(R.drawable.placeholder)
                    .into(blogImage);
            blogImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else{
            blogImage.setImageResource(R.drawable.placeholder);
        }

        if(getIntent().hasExtra("BlogAuthor") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogAuthor"))) {
            blogAuthor.setText(getIntent().getStringExtra("BlogAuthor"));
        }else{
            blogAuthor.setVisibility(View.GONE);
        }
        if(getIntent().hasExtra("BlogDate") && getIntent().getLongExtra("BlogDate",0) != 0) {
            if(!TextUtils.isEmpty(blogAuthor.getText().toString())) {
                blogDate.setText(" | " + UtilityMethods.getDate(getIntent().getLongExtra("BlogDate",0), "dd MMM yyyy"));
            }else{
                blogDate.setText(UtilityMethods.getDate(getIntent().getLongExtra("BlogDate",0), "dd MMM yyyy"));
            }
        }else{
            blogDate.setVisibility(View.GONE);
        }
        if(getIntent().hasExtra("BlogTitle") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogTitle"))) {
            blogTitle.setText(getIntent().getStringExtra("BlogTitle"));
        }else{
            blogTitle.setVisibility(View.GONE);
        }

        if(getIntent().hasExtra("BlogTags") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogTags"))){
            Gson gson = new Gson();
            String json = getIntent().getStringExtra("BlogTags");
            if(!TextUtils.isEmpty(json)) {
                Type type = new TypeToken<List<BlogTag>>() {
                }.getType();
                blogTags = gson.fromJson(json, type);
                if(blogTags != null){
                    Log.i(TAG, "Blog Tag list size -> " + blogTags.size());
                }
            }
        }else{
            findViewById(R.id.id_tag_text).setVisibility(View.GONE);
            findViewById(R.id.id_tag_vertical_layout).setVisibility(View.GONE);
        }

        TypedValue tv = new TypedValue();
        int imageHeight = 0;
        int actionBarHeight = 0;
        try {
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            }
            imageHeight = (UtilityMethods.getScreenWidth(this) * 2 / 3)-actionBarHeight+UtilityMethods.dpToPx(11);
        }catch (Exception e){
            imageHeight = UtilityMethods.getScreenHeight(this)/3;
        }

        View fabButtonLayout = findViewById(R.id.id_share_menu);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fabButtonLayout.getLayoutParams();
        params.setMargins(0,imageHeight,0,0);
        fabButtonLayout.setLayoutParams(params);
        final FloatingActionMenu shareMenu = (FloatingActionMenu)findViewById(R.id.id_share_button);
        shareMenu.setIconAnimated(false);
        shareMenu.setClosedOnTouchOutside(true);
        final FloatingActionButton moreMenuButton = findViewById(R.id.id_more_menu);
        FloatingActionButton twitterMenuButton = findViewById(R.id.id_twitter_menu);
        FloatingActionButton facebookMenuButton = findViewById(R.id.id_facebook_menu);
        facebookMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Sharefb post");
                new EventAnalyticsHelper().ItemClickEvent(BlogDetailsActivity.this, Constants.AnaylticsClassName.BlogListScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.fbClick.toString());
                sharePost("facebook");
                if(shareMenu.isOpened()){

                    shareMenu.toggle(true);
                }
            }
        });
        twitterMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Twitter button");
                new EventAnalyticsHelper().ItemClickEvent(BlogDetailsActivity.this, Constants.AnaylticsClassName.BlogListScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.TwitterClick.toString());

                sharePost("twitter");
                if(shareMenu.isOpened()){
                    shareMenu.toggle(true);
                }
            }
        });
        moreMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: More button");
                new EventAnalyticsHelper().ItemClickEvent(BlogDetailsActivity.this, Constants.AnaylticsClassName.BlogListScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.moreClick.toString());
                sharePost("more");
                if(shareMenu.isOpened()){
                    shareMenu.toggle(true);
                }
            }
        });

        findViewById(R.id.id_container_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shareMenu.isOpened()){
                    shareMenu.toggle(true);
                }
            }
        });

        if(getIntent().hasExtra("BlogContent") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogContent"))) {
            String data = getIntent().getStringExtra("BlogContent");
            //blogContent.loadDataWithBaseURL(null,getHtmlData(getIntent().getStringExtra("BlogContent")),"text/html", "UTF-8",null);
            blogContent.loadDataWithBaseURL(null, formatWebviewContentImage(data), "text/html", "UTF-8", null);
            blogContent.getSettings().setJavaScriptEnabled(true);
            blogContent.getSettings().setDomStorageEnabled(true);

            blogContent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        webViewFingerDownTime = System.currentTimeMillis();
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if(webViewFingerDownTime != 0 && (System.currentTimeMillis() - webViewFingerDownTime) <= 1000){
                            if(shareMenu.isOpened()){
                                shareMenu.toggle(true);
                            }
                        }
                    }
                    return false;
                }
            });

            blogContent.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    try {
                        Log.i(TAG, "shouldOverrideUrlLoading: URL -> " + url);
                        Log.i(TAG, "shouldOverrideUrlLoading: URL -> " + url);
                        if(shareMenu.isOpened()){
                            shareMenu.toggle(true);
                            return true;
                        }
                        view.getSettings().setJavaScriptEnabled(true);
                        view.getSettings().setDomStorageEnabled(true);
                        if (url.contains("clicbrics") && url.contains("/project/")) {
                            String projectIdStr = UtilityMethods.getProjectIdFromURL(url);
                            String projectId = projectIdStr.substring(projectIdStr.lastIndexOf('-') + 1, projectIdStr.length());
                            intent = new Intent(BlogDetailsActivity.this, ProjectDetailsScreen.class);
                            intent.putExtra("ISDirectCall", true);
                            intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, Long.valueOf(projectId));
                            startActivity(intent);
                        }else if(url.contains("clicbrics") && url.contains("/city/")){
                            showAllCityProperty(url);
                        }else if(url.toLowerCase().startsWith("https://www.clicbrics.com/news-and-articles/post/")){
                            showOtherBlogDetails(url.replace("https://www.clicbrics.com/news-and-articles/post/",""));
                        } else{
                            if(!UtilityMethods.isInternetConnected(BlogDetailsActivity.this)){
                                UtilityMethods.showErrorSnackBar(mRootView,getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                            }else {
                                Intent blogContentActivity = new Intent(BlogDetailsActivity.this, BlogContentWebviewActivity.class);
                                if (getIntent().hasExtra("BlogTitle") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogTitle"))) {
                                    blogContentActivity.putExtra("BlogTitle", getIntent().getStringExtra("BlogTitle"));
                                }
                                if (!TextUtils.isEmpty(url)) {
                                    blogContentActivity.putExtra("contentURL", url.trim());
                                }
                                startActivity(blogContentActivity);
                            }
                        }
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        UtilityMethods.showSnackBar(mRootView,"Page not found!",Snackbar.LENGTH_LONG);
                    }catch (Exception e) {
                        startActivity(intent);
                        e.printStackTrace();
                    }
                    /********** To show details in browser ************/
                    /*view.getSettings().setJavaScriptEnabled(true);
                    view.getSettings().setDomStorageEnabled(true);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent,0);
                    if(resolveInfoList != null){
                        for(int i=0; i<resolveInfoList.size();i++){
                            if(resolveInfoList.get(i).activityInfo != null && resolveInfoList.get(i).activityInfo.packageName != null
                                    && resolveInfoList.get(i).activityInfo.packageName.equalsIgnoreCase("com.clicbrics.consumer")){
                                intent.setPackage("com.clicbrics.consumer");
                                break;
                            }
                        }
                    }
                    startActivity(intent);*/
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
                    TextView textView = (TextView) findViewById(R.id.id_tag_text);
                    if(blogTags != null && !blogTags.isEmpty()) {
                        textView.setVisibility(View.VISIBLE);
                        setTagLayout(blogTags);
                        /*GridView gridview = (GridView) findViewById(R.id.tag_gridview);
                        gridview.setVisibility(View.VISIBLE);
                        gridview.setAdapter(new TagGridAdapter(BlogDetailsActivity.this,blogTags));*/
                    }
                }
            });
        }else{
            blogContent.setVisibility(View.GONE);
        }

    }

    private void setTagLayout(List<BlogTag> blogTagList){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int deviceWidth = dm.widthPixels;
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.id_tag_vertical_layout);
        //LinearLayout horizontalLayout = new LinearLayout(verticalLayout.getContext());
        //horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        //verticalLayout.addView(horizontalLayout);
        List<LinearLayout> linearLayoutList = new ArrayList<>();
        int rowWidth = UtilityMethods.dpToPx(15);
        int rowNo = 0;
        for(int i=0; i<blogTagList.size(); i++){
            BlogTag blogTag = blogTagList.get(i);
            if(blogTag != null){
                LinearLayout horizontalLayout = new LinearLayout(verticalLayout.getContext());
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayoutList.add(horizontalLayout);
                final TextView tagNameText = new TextView(this);
                tagNameText.setText(blogTag.getName());
                UtilityMethods.setDrawableBackground(this,tagNameText,R.drawable.tag_background);
                UtilityMethods.setTextViewColor(this,tagNameText,R.color.text_grey);
                int left = UtilityMethods.dpToPx(15);
                int top = UtilityMethods.dpToPx(4);
                int bottom = UtilityMethods.dpToPx(6);
                tagNameText.setPadding(left,top,left,bottom);
                tagNameText.setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,left,0);
                tagNameText.setLayoutParams(params);
                tagNameText.measure(0,0);

                rowWidth = rowWidth + tagNameText.getMeasuredWidth()+ left+10;
                if(deviceWidth > rowWidth){
                    linearLayoutList.get(rowNo).addView(tagNameText);
                }else{
                    ++rowNo;
                    LinearLayout horizontalLayout2 = new LinearLayout(verticalLayout.getContext());
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(0,UtilityMethods.dpToPx(10),0,0);
                    horizontalLayout2.setOrientation(LinearLayout.HORIZONTAL);
                    //horizontalLayout2.addView(tagNameText);
                    rowWidth = tagNameText.getMeasuredWidth()+UtilityMethods.dpToPx(15)+20;
                    linearLayoutList.add(horizontalLayout2);
                    linearLayoutList.get(rowNo).setLayoutParams(params2);
                    linearLayoutList.get(rowNo).addView(tagNameText);
                }
            }
        }
        if(linearLayoutList != null){
            for(int i=0; i<linearLayoutList.size(); i++){
                verticalLayout.addView(linearLayoutList.get(i));
            }
        }

    }

    private String formatWebviewContentImage(String data){
        //return "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + data;
        return "<style>img{display: inline;height: auto;max-width: 100%;}@font-face {font-family: FiraSans-Light;src: url('file:///android_asset/fonts/FiraSans-Light.ttf');</style>" + data;
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
                /*String[] cityArr  = split[1].split("-");
                cityName = cityArr[0].trim();
                cityId = Long.parseLong(cityArr[1].trim());*/
                cityName  = split[1].substring(0,split[1].lastIndexOf("-"));
                cityId = Long.parseLong(split[1].substring((split[1].lastIndexOf("-")+1),split[1].length()));
            }
            Intent intent = new Intent(BlogDetailsActivity.this, HomeScreen.class);
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

    private void showOtherBlogDetails(final String blogName){
        if(!UtilityMethods.isInternetConnected(this)){
            UtilityMethods.showErrorSnackBar(mRootView,getResources().getString(R.string.no_internet_connection),Snackbar.LENGTH_LONG);
            return;
        }
        showProgressBar();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                    httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                    final BlogResponse blogResponse = mBlogEndPoint.getBlog(1l,"1",blogName,false)
                            .setRequestHeaders(httpHeaders).execute();
                    if(blogResponse != null){
                        if(blogResponse.getStatus()){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressBar();
                                    Blog blog = blogResponse.getBlog();
                                    if(blog != null) {
                                        Intent intent = new Intent(BlogDetailsActivity.this, BlogDetailsActivity.class);
                                        if (!TextUtils.isEmpty(blog.getTitle())) {
                                            intent.putExtra("BlogTitle", blog.getTitle());
                                        }
                                        if (!TextUtils.isEmpty(blog.getCreateByName())) {
                                            intent.putExtra("BlogAuthor", blog.getCreateByName());
                                        }
                                        if (blog.getCreateTime() != null) {
                                            intent.putExtra("BlogDate", blog.getCreateTime());
                                        }
                                        if (!TextUtils.isEmpty(blog.getSTitleImage())) {
                                            intent.putExtra("BlogImageURL", blog.getSTitleImage());
                                        }
                                        if (!TextUtils.isEmpty(blog.getContent())) {
                                            intent.putExtra("BlogContent", blog.getContent());
                                        }
                                        if (!TextUtils.isEmpty(blog.getSearchTitle())) {
                                            intent.putExtra("SearchTitle", blog.getSearchTitle());
                                        }
                                        if (!TextUtils.isEmpty(blog.getExcerpt())) {
                                            intent.putExtra("Description", blog.getExcerpt());
                                        }
                                        if (blog.getBlogTags() != null && !blog.getBlogTags().isEmpty()) {
                                            List<BlogTag> blogTags = blog.getBlogTags();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(blogTags);
                                            intent.putExtra("BlogTags", json);
                                        }
                                        intent.putExtra("ArticleType", "blog");
                                        startActivity(intent);
                                    }else{
                                        errorMsg = "Blog detail not found!";
                                    }
                                }
                            });
                        }else if(!TextUtils.isEmpty(blogResponse.getErrorMessage())){
                            errorMsg = blogResponse.getErrorMessage();
                        }else{
                            errorMsg = "Blog detail not found!";
                        }
                    }else{
                        errorMsg = "Blog detail not found!";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Something went wrong.Please retry!";
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            UtilityMethods.showSnackBar(mRootView,errorMsg,Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    private void sharePost(String shareType){
        String title = "",description="",imageURL = "",contentUrl="";
        if(getIntent().hasExtra("BlogTitle") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogTitle"))){
            title = getIntent().getStringExtra("BlogTitle");
        }
        if(getIntent().hasExtra("Description") && !TextUtils.isEmpty(getIntent().getStringExtra("Description"))){
            description = getIntent().getStringExtra("Description");
        }
        if(getIntent().hasExtra("BlogImageURL") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogImageURL"))){
            imageURL = getIntent().getStringExtra("BlogImageURL");
        }
        if(getIntent().hasExtra("SearchTitle") && !TextUtils.isEmpty(getIntent().getStringExtra("SearchTitle"))){
            contentUrl = BuildConfigConstants.WEB_URL + "post/"+ getIntent().getStringExtra("SearchTitle");
        }
        if(!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(this);
        }
        if(shareType.equalsIgnoreCase("facebook")) {
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(description)
                    .setContentUrl(Uri.parse(contentUrl))
                    .setImageUrl(Uri.parse(imageURL))
                    .build();
            ShareDialog.show(BlogDetailsActivity.this, shareLinkContent);
        }else if(shareType.equalsIgnoreCase("twitter")){
            Intent tweet = new Intent(Intent.ACTION_VIEW);
            //String message = title + "\n" + contentUrl;
            //tweet.setData(Uri.parse("http://twitter.com/?status=" + Uri.encode(message)));//where message is your string message
            String tweetUrl = "https://twitter.com/intent/tweet?text="+ title+ " &url=" + contentUrl;
            tweet.setData(Uri.parse(tweetUrl));
            startActivity(tweet);
        }else if(shareType.equalsIgnoreCase("more")){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String content = title + "\n" + contentUrl;
            intent.putExtra(Intent.EXTRA_TEXT, content);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "clicbrics");
            startActivity(Intent.createChooser(intent, "Share Article"));
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
        new EventAnalyticsHelper().logUsageStatsEvents(BlogDetailsActivity.this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.BlogDetailScreen);
    }
}
