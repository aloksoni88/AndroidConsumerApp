package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.buy.housing.backend.blogEndPoint.BlogEndPoint;
import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.blogEndPoint.model.BlogListResponse;
import com.buy.housing.backend.newsEndPoint.NewsEndPoint;
import com.buy.housing.backend.newsEndPoint.model.News;
import com.buy.housing.backend.newsEndPoint.model.NewsListResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.ArticlesPagerAdapter;
import com.clicbrics.consumer.adapter.NewsPagerAdapter;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.OnSwipeTouchListener;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.google.api.client.http.HttpHeaders;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BlogListActivity extends BaseActivity {

    private static final String TAG = BlogListActivity.class.getSimpleName();
    private ImageButton backImage, nextImage;
    private int backImageVisibility = View.GONE, nextImageVisibility=View.GONE;
    Animation hideAnimation;
    private BlogEndPoint mBlogEndPoint;
    private NewsEndPoint mNewsEndPoint;
    private int listSize = 0;
    private int position = 0;
    //private List<Blog> mBlogList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);

        initToolbar();
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        backImage = (ImageButton) findViewById(R.id.id_back_image);
        nextImage = (ImageButton) findViewById(R.id.id_next_image);
        hideAnimation = AnimationUtils.loadAnimation(this,R.anim.button_hide_alpha_animation);

        if(getIntent().hasExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION)
                && getIntent().getBooleanExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,false)){
            if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("news")){
                if(getIntent().hasExtra("NewsTitle") && !TextUtils.isEmpty(getIntent().getStringExtra("NewsTitle"))){
                    getNewsPosition(getIntent().getStringExtra("NewsTitle"),true);
                }else{
                    setNewsList();
                }
            }else {
                if(getIntent().hasExtra("BlogTitle") && !TextUtils.isEmpty(getIntent().getStringExtra("BlogTitle"))){
                    getArticlePosition(getIntent().getStringExtra("BlogTitle"),true);
                }else{
                    setBlogList();
                }
            }
        }else{
            if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("news")){
                position = getIntent().hasExtra("Position") ? getIntent().getIntExtra("Position",0):0;
                listSize = getIntent().hasExtra("TotalNewsCount") ? getIntent().getIntExtra("TotalNewsCount",0):0;
                if(getIntent().hasExtra("isFromShareActivity") && getIntent().getBooleanExtra("isFromShareActivity",false)){
                    getNewsPosition("",false);
                }else {
                    setNewsList();
                }
            }else {
                position = getIntent().hasExtra("Position") ? getIntent().getIntExtra("Position",0):0;
                listSize = getIntent().hasExtra("TotalBlogCount") ? getIntent().getIntExtra("TotalBlogCount",0):0;
                if(getIntent().hasExtra("isFromShareActivity") && getIntent().getBooleanExtra("isFromShareActivity",false)){
                    getArticlePosition("",false);
                }else {
                    setBlogList();
                }
            }
        }
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_blogdetails_toolbar);
        toolbar.setTitle("");
        //TextView toolbarTitle = (TextView) findViewById(R.id.id_blog_detail_title);

        setSupportActionBar(toolbar);

        /*if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("news")){
            toolbarTitle.setText("News");
        }else if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("blog")){
            toolbarTitle.setText("Articles");
        }*/
        //toolbarTitle.setText("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);
    }

    private void buildBlogService() {
        mBlogEndPoint = EndPointBuilder.getBlogEndPoint();
    }

    private void buildNewsService(){
        mNewsEndPoint = EndPointBuilder.getNewsEndPoint();
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_n_articles_menu,menu);
        return true;
    }*/

    private void setNewsList(){
        List<News> newsList =  new ArrayList<>();
        newsList.addAll(((HousingApplication)getApplicationContext()).getNewsList());
        if(newsList != null && !newsList.isEmpty()){
            if(!UtilityMethods.getBooleanInPref(this, Constants.AppConstants.IS_NEWS_HELP_SCREEN_SEEN,false)){
                showHelpScreen("news");
                UtilityMethods.saveBooleanInPref(this,Constants.AppConstants.IS_NEWS_HELP_SCREEN_SEEN,true);
            }else{
                findViewById(R.id.id_overlay_layout).setVisibility(View.GONE);
            }
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            deviceHeight = dm.heightPixels;
            deviceWidth = dm.widthPixels;

            /*int position = getIntent().getIntExtra("Position",0);
            final int newsCount = getIntent().hasExtra("TotalNewsCount") ? getIntent().getIntExtra("TotalNewsCount",0):0;*/

            final ViewPager newsPager = (ViewPager) findViewById(R.id.id_verticleViewPager);
            final NewsPagerAdapter adapter = new NewsPagerAdapter(this,newsList,listSize, findViewById(R.id.activity_blog_list),
                    newsPager);
            adapter.notifyDataSetChanged();
            newsPager.setAdapter(adapter);
            newsPager.setCurrentItem(position);
            newsPager.setOffscreenPageLimit(5);

            /* for very first time  */
            if(position == 0){
                setArrowImageVisibility(View.GONE,View.VISIBLE);
            }else if(adapter != null && (adapter.getCount()-1) == position){
                setArrowImageVisibility(View.GONE,View.GONE);
            }else{
                setArrowImageVisibility(View.VISIBLE,View.VISIBLE);
            }
            newsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if(position == 0){
                        setArrowImageVisibility(View.GONE,View.VISIBLE);
                    }else if(adapter != null && (adapter.getCount()-1) == position){
                        setArrowImageVisibility(View.GONE,View.GONE);
                    }else{
                        setArrowImageVisibility(View.VISIBLE,View.VISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if(state == 0) {
                        hideArrowButton(3000);
                    }
                }
            });


            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(newsPager != null){
                            newsPager.setCurrentItem(newsPager.getCurrentItem()-1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            nextImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(newsPager != null){
                            newsPager.setCurrentItem(newsPager.getCurrentItem()+1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            hideArrowButton(3000);

            newsPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });
        }
    }


    Timer backImageTimer = null;
    Timer nextImageTimer = null;
    private void hideArrowButton(long delay){
        delay = 2500;
        try {
            if(backImageVisibility == View.VISIBLE){
                if(backImageTimer != null){
                    backImageTimer.cancel();
                }
                backImageTimer = new Timer();
                TimerTask timerTask  = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                backImage.startAnimation(hideAnimation);
                                backImage.setVisibility(View.GONE);
                            }
                        });
                    }
                };
                backImageTimer.schedule(timerTask,delay);
            }

            if(nextImageVisibility == View.VISIBLE){
                if(nextImageTimer != null){
                    nextImageTimer.cancel();
                }
                nextImageTimer = new Timer();
                TimerTask timerTask  = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                nextImage.startAnimation(hideAnimation);
                                nextImage.setVisibility(View.GONE);
                            }
                        });
                    }
                };
                nextImageTimer.schedule(timerTask,delay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showButtonOnTouchView(){
        setArrowImageVisibility(backImageVisibility,nextImageVisibility);
        hideArrowButton(3000);
    }

    public void setArrowImageVisibility(int backImageVisibility, int nextImageVisibility){
        if(backImage != null){
            backImage.setVisibility(backImageVisibility);
        }
        if(nextImage != null){
            nextImage.setVisibility(nextImageVisibility);
        }
        this.backImageVisibility = backImageVisibility;
        this.nextImageVisibility = nextImageVisibility;
    }

    private void getNewsPosition(final String title, final boolean isNewsExistCheck){
        if(!UtilityMethods.isInternetConnected(this)){
            UtilityMethods.showErrorSnackBar(findViewById(R.id.activity_blog_list),getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            return;
        }
        buildNewsService();
        ((HousingApplication) getApplicationContext()).getNewsList().clear();
        showProgressBar();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                    httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                    final NewsListResponse newsResponse = mNewsEndPoint.getNewsList(1L,"1",0,50)
                            .setRequestHeaders(httpHeaders).execute();
                    if(newsResponse != null && newsResponse.getStatus() && newsResponse.getCount() != null
                            && newsResponse.getCount() != 0){
                        final List<News> newsList = newsResponse.getNews();
                        if(newsList != null && !newsList.isEmpty()) {
                            //((HousingApplication) getApplicationContext()).setNewsList(newsList);
                            listSize = newsResponse.getCount();
                            News sharedNews = null;
                            if(isNewsExistCheck){
                                for(int i=0; i< newsList.size(); i++){
                                    News news = newsList.get(i);
                                    if(news != null && !TextUtils.isEmpty(news.getSearchTitle())
                                            && news.getSearchTitle().equalsIgnoreCase(title)){
                                        position = i;
                                        sharedNews = news;
                                        break;
                                    }
                                }

                                if(position != 0) {
                                    newsList.remove(position);
                                    newsList.add(0, sharedNews);
                                    position = 0;
                                }
                                ((HousingApplication) getApplicationContext()).setNewsList(newsList);
                                final News finalSharedNews = sharedNews;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissProgressBar();
                                        if(finalSharedNews == null && !TextUtils.isEmpty(title)){
                                            Intent intent = new Intent(BlogListActivity.this,ShareActivity.class);
                                            intent.putExtra("TotalNewsCount",listSize);
                                            intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                                            intent.putExtra("ArticleType","news");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }else {
                                            setNewsList();
                                        }
                                    }
                                });
                            }else{
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((HousingApplication) getApplicationContext()).setNewsList(newsList);
                                        setNewsList();
                                        dismissProgressBar();
                                    }
                                });
                            }
                        }else{
                            errorMsg = "News list empty!";
                        }
                    }else{
                        if(newsResponse != null && !TextUtils.isEmpty(newsResponse.getErrorMessage())){
                            errorMsg = newsResponse.getErrorMessage();
                        }else{
                            errorMsg = "Something went wrong.Please try again!";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Something went wrong.Please try again!";
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            UtilityMethods.showSnackBar(findViewById(R.id.activity_blog_list),errorMsg, Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    private void getArticlePosition(final String title, final boolean isBlogExistingCheck){
        if(!UtilityMethods.isInternetConnected(this)){
            UtilityMethods.showErrorSnackBar(findViewById(R.id.activity_blog_list),getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            return;
        }
        showProgressBar();
        ((HousingApplication) getApplicationContext()).getBlogList().clear();
        buildBlogService();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                    httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                    final BlogListResponse blogResponse = mBlogEndPoint.getBlogs(1l,"1",0,50).setBlogStatus(Constants.BlogStatus.Publish.toString())
                            .setRequestHeaders(httpHeaders).execute();
                    if(blogResponse != null && blogResponse.getStatus() && blogResponse.getCount() != null
                            && blogResponse.getCount() != 0){
                        final List<Blog> blogList = blogResponse.getBlogs();
                        listSize = blogResponse.getCount();
                        if(blogList != null) {
                            Blog sharedBlog = null;
                            if(isBlogExistingCheck){
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
                                }
                                ((HousingApplication) getApplicationContext()).setBlogList(blogList);
                                final Blog finalSharedBlog = sharedBlog;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissProgressBar();
                                        if(finalSharedBlog == null && !TextUtils.isEmpty(title)){
                                            Intent intent = new Intent(BlogListActivity.this,ShareActivity.class);
                                            intent.putExtra("TotalBlogCount",listSize);
                                            intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                                            intent.putExtra("ArticleType","blog");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }else {
                                            setBlogList();
                                        }
                                    }
                                });
                            }else{
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((HousingApplication) getApplicationContext()).setBlogList(blogList);
                                        setBlogList();
                                        dismissProgressBar();

                                    }
                                });
                            }
                        }else{
                            errorMsg = "Blog list empty!";
                        }
                    }else{
                        if(blogResponse != null && !TextUtils.isEmpty(blogResponse.getErrorMessage())){
                            errorMsg = blogResponse.getErrorMessage();
                        }else{
                            errorMsg = "Something went wrong.Please try again!";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Something went wrong.Please try again!";
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            UtilityMethods.showErrorSnackBar(findViewById(R.id.activity_blog_list),errorMsg, Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    private void setBlogList(){
        List<Blog> blogList = new ArrayList<>();
        blogList.addAll(((HousingApplication)getApplicationContext()).getBlogList());
        if(blogList != null && !blogList.isEmpty()){
            if(!UtilityMethods.getBooleanInPref(this, Constants.AppConstants.IS_ARTICLE_HELP_SCREEN_SEEN,false)){
                showHelpScreen("blog");
                UtilityMethods.saveBooleanInPref(this,Constants.AppConstants.IS_ARTICLE_HELP_SCREEN_SEEN,true);
            }else{
                findViewById(R.id.id_overlay_layout).setVisibility(View.GONE);
            }
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            deviceHeight = dm.heightPixels;
            deviceWidth = dm.widthPixels;

            /*int position = getIntent().getIntExtra("Position",0);
            int blogCount = getIntent().hasExtra("TotalBlogCount") ? getIntent().getIntExtra("TotalBlogCount",0):0;*/

            /*verticalViewPager = (VerticalViewPager) findViewById(R.id.id_verticleViewPager);
            verticalViewPager.setAdapter(new VerticalPagerAdapter(this, blogList,null, "blog", deviceWidth, blogCount, findViewById(R.id.activity_blog_list)));
            verticalViewPager.setCurrentItem(positoin);*/
            Log.i(TAG, "getBlogList: " + blogList.size());
            final ViewPager articlesPager = (ViewPager) findViewById(R.id.id_verticleViewPager);
            final ArticlesPagerAdapter adapter = new ArticlesPagerAdapter(this, blogList, listSize, findViewById(R.id.activity_blog_list),
                        articlesPager);
            adapter.notifyDataSetChanged();
            articlesPager.setAdapter(adapter);
            articlesPager.setCurrentItem(position);
            articlesPager.setOffscreenPageLimit(5);
            Log.i(TAG, "adapter has been called");

            /* for very first time  */
            if(position == 0){
                setArrowImageVisibility(View.GONE,View.VISIBLE);
            }else if(adapter != null && (adapter.getCount()-1) == position){
                setArrowImageVisibility(View.GONE,View.GONE);
            }else{
                setArrowImageVisibility(View.VISIBLE,View.VISIBLE);
            }
            articlesPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if(position == 0){
                        setArrowImageVisibility(View.GONE,View.VISIBLE);
                    }else if(adapter != null && (adapter.getCount()-1) == position){
                        setArrowImageVisibility(View.GONE,View.GONE);
                    }else{
                        setArrowImageVisibility(View.VISIBLE,View.VISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if(state == 0) {
                        hideArrowButton(3000);
                    }
                }
            });


            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(articlesPager != null){
                            articlesPager.setCurrentItem(articlesPager.getCurrentItem()-1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            nextImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(articlesPager != null){
                            articlesPager.setCurrentItem(articlesPager.getCurrentItem()+1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            hideArrowButton(3000);
        }
    }

    private void showHelpScreen(String type){
        findViewById(R.id.id_overlay_layout).setVisibility(View.VISIBLE);
        final RelativeLayout overLayView = (RelativeLayout) findViewById(R.id.id_overlay_layout);
        ImageView swipeImage = (ImageView) findViewById(R.id.id_swipe_image);
        if(type.equalsIgnoreCase("news")){
            swipeImage.setImageResource(R.drawable.swipe_to_see_next_news);
        }else{
            swipeImage.setImageResource(R.drawable.swipe_to_see_next_article);
        }


        overLayView.setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            public void onClick() {
                super.onClick();
                overLayView.setVisibility(View.GONE);
            }

            public void onSwipeTop() {
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.TOP);
                TransitionManager.beginDelayedTransition(overLayView,slide);
                overLayView.setVisibility(View.GONE);
            }

            public void onSwipeRight() {
                Log.i(TAG, "onSwipeRight: ");
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.RIGHT);
                TransitionManager.beginDelayedTransition(overLayView,slide);
                overLayView.setVisibility(View.GONE);
            }

            public void onSwipeLeft() {
                Log.i(TAG, "onSwipeLeft: ");
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.LEFT);
                TransitionManager.beginDelayedTransition(overLayView,slide);
                overLayView.setVisibility(View.GONE);
            }

            public void onSwipeBottom() {
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.BOTTOM);
                TransitionManager.beginDelayedTransition(overLayView,slide);
                overLayView.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()){
                case android.R.id.home:{
                    if((getIntent().hasExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION)
                            && getIntent().getBooleanExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,false))
                            || (getIntent().hasExtra("isShared") && getIntent().getBooleanExtra("isShared",false))){
                        Intent intent = new Intent(this, HomeScreen.class);
                        intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,true);
                        /*if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("news")){
                            intent.putExtra(Constants.IntentKeyConstants.NOTIFICATION_TYPE,Constants.MessageType.News.toString());
                        }else if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("blog")){
                            intent.putExtra(Constants.IntentKeyConstants.NOTIFICATION_TYPE,Constants.MessageType.Articles.toString());
                        }*/
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else {
                        super.onBackPressed();
                    }
                }
                return true;
                /*case R.id.menu_scroll_to_top:{
                    Log.i(TAG, "onOptionsItemSelected: scroll to top" );
                }break;*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            if((getIntent().hasExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION)
                    && getIntent().getBooleanExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,false))
                    || (getIntent().hasExtra("isShared") && getIntent().getBooleanExtra("isShared",false))){
                Intent intent = new Intent(this, HomeScreen.class);
                intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION,true);
                if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("news")){
                    intent.putExtra(Constants.IntentKeyConstants.NOTIFICATION_TYPE,Constants.MessageType.News.toString());
                }else if(getIntent().hasExtra("ArticleType") && getIntent().getStringExtra("ArticleType").equalsIgnoreCase("blog")){
                    intent.putExtra(Constants.IntentKeyConstants.NOTIFICATION_TYPE,Constants.MessageType.Articles.toString());
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class GetNewsDetails extends AsyncTask<String , Void ,StringBuffer> {
        String newsTitle = "";
        GetNewsDetails(String title){
            this.newsTitle = title;
        }
        @Override
        protected StringBuffer doInBackground(String... strings) {
            String newsURL = BuildConfigConstants.SERVER_URL + "getnewsservlet?userToken=sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn&searchTitle=" + newsTitle;
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuffer result = new StringBuffer();
            try {
                url = new URL(newsURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(StringBuffer result) {
            super.onPostExecute(result);
            if(result != null) {
                Log.i(TAG, "Response " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if(jsonObject.get("status").toString().equalsIgnoreCase("true")){
                        if(jsonObject.toString().contains("newsObj") && jsonObject.get("newsObj") != null){
                            JSONObject newsStr =  jsonObject.getJSONObject("newsObj");
                            final News news = new News();
                            if(newsStr != null) {
                                if (newsStr.toString().contains("searchTitle")) {
                                    news.setSearchTitle(newsStr.getString("searchTitle"));
                                }
                                if (newsStr.toString().contains("title")) {
                                    news.setTitle(newsStr.getString("title"));
                                }
                                if (newsStr.toString().contains("image")) {
                                    news.setImage(newsStr.getString("image"));
                                }
                                if (newsStr.toString().contains("description")) {
                                    news.setDescription(newsStr.getString("description"));
                                }
                                if (newsStr.toString().contains("source")) {
                                    news.setSource(newsStr.getString("source"));
                                }
                                if (newsStr.toString().contains("clickURL")) {
                                    news.setClickURL(newsStr.getString("clickURL"));
                                }
                                if (newsStr.toString().contains("createTime")) {
                                    news.setCreateTime(Long.parseLong(newsStr.getString("createTime")));
                                }
                            }else{

                            }

                        }else{

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Log.i(TAG," Response is null");
            }
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
        new EventAnalyticsHelper().logUsageStatsEvents(BlogListActivity.this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.BlogListScreen);

    }
}
