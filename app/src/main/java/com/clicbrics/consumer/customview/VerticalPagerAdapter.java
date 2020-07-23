package com.clicbrics.consumer.customview;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buy.housing.backend.blogEndPoint.BlogEndPoint;
import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.blogEndPoint.model.BlogListResponse;
import com.buy.housing.backend.blogEndPoint.model.BlogTag;
import com.buy.housing.backend.newsEndPoint.NewsEndPoint;
import com.buy.housing.backend.newsEndPoint.model.News;
import com.buy.housing.backend.newsEndPoint.model.NewsListResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.BlogDetailsActivity;
import com.clicbrics.consumer.activities.BlogListActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpHeaders;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Alok on 19-09-2017.
 */

@SuppressWarnings("deprecation")
public class VerticalPagerAdapter extends PagerAdapter {

    private static final String TAG = VerticalPagerAdapter.class.getSimpleName();

    private NewsEndPoint mNewsEndPoint;
    private BlogEndPoint mBlogEndPoint;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Blog> mBlogList;
    private List<News> mNewsList;
    private int mDeviceWidth;
    private int mTotalBlogCount;
    private View mRootView;
    private boolean isLoadingDataViewEnable = false;
    private String articleType = "";
    private Handler mHandler;
    private View toastView;
    private static final int TOAST_POSITION = 5;

    public VerticalPagerAdapter(Context context, List<Blog> blogList, List<News> newsList,String articleType ,int deviceWidth, int blogCount, View rootView) {
        try {
            mContext = context;
            mBlogList = ((HousingApplication) mContext.getApplicationContext()).getBlogList();
            mNewsList = ((HousingApplication) mContext.getApplicationContext()).getNewsList();
            this.articleType = articleType;
            mDeviceWidth = deviceWidth;
            mTotalBlogCount = blogCount;
            mRootView = rootView;
            mHandler = new Handler();
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            buildWebService();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.clicbrics.internetconnection");
            context.registerReceiver(internetReceiver, intentFilter);
            //notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildWebService() {
        if(articleType.equalsIgnoreCase("blog")) {
            mBlogEndPoint = EndPointBuilder.getBlogEndPoint();
        }

        if(articleType.equalsIgnoreCase("news")) {
            mNewsEndPoint = EndPointBuilder.getNewsEndPoint();
        }
    }

    @Override
    public int getCount() {
        try {
            if(articleType.equalsIgnoreCase("news") && mNewsList != null && !mNewsList.isEmpty()){
                Log.i(TAG, "getCount: News " + mNewsList.size());
                return mNewsList.size() +1;
            }
            else if (articleType.equalsIgnoreCase("blog") && mBlogList != null && !mBlogList.isEmpty()) {
                Log.i(TAG, "getCount: Articles " + mBlogList.size());
                return mBlogList.size() + 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View itemView = null;
        try {
            Log.i(TAG, "instantiateItem: " + position);
            itemView = null;
            if(toastView == null) {
                toastView = mLayoutInflater.inflate(R.layout.custom_toast_layout,container,false);
            }
            if ((mTotalBlogCount == (getCount() - 1)) && (mTotalBlogCount == position)) {
                if (isLoadingDataViewEnable) {
                    itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, container, false);
                    TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                    if(articleType.equalsIgnoreCase("news")){
                        loadingTextMsg.setText("Loading News...");
                    }else {
                        loadingTextMsg.setText("Loading Articles...");
                    }
                } else {
                    itemView = mLayoutInflater.inflate(R.layout.content_all_blog_read_layout, container, false);
                    TextView allListReadMsg  = itemView.findViewById(R.id.id_all_list_read_msg);
                    if(articleType.equalsIgnoreCase("news")){
                        allListReadMsg.setText(mContext.getString(R.string.read_all_news_msg));
                    }else {
                        allListReadMsg.setText(mContext.getString(R.string.read_all_blog_msg));
                    }
                }
            } else if (isLoadingDataViewEnable && ((getCount() - 1) == position)) {
                itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, container, false);
                TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                if(articleType.equalsIgnoreCase("news")){
                    loadingTextMsg.setText("Loading News...");
                }else {
                    loadingTextMsg.setText("Loading Articles...");
                }
            } else {
                if (mTotalBlogCount > (getCount() - 1) && ((getCount() - 1) == position)) {
                    itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, container, false);
                    TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                    if(articleType.equalsIgnoreCase("news")){
                        loadingTextMsg.setText("Loading News...");
                    }else {
                        loadingTextMsg.setText("Loading Articles...");
                    }
                } else {
                    itemView = mLayoutInflater.inflate(R.layout.content_blog_list, container, false);
                    final ThreeTwoImageView blogImage = (ThreeTwoImageView) itemView.findViewById(R.id.id_blog_image);
                    final ImageView newsImage = (ImageView) itemView.findViewById(R.id.id_news_image);
                    final ImageView newsBlurImage = (ImageView) itemView.findViewById(R.id.id_news_blurr_image);
                    TextView blogAuthor = (TextView) itemView.findViewById(R.id.id_blog_author);
                    TextView blogDate = (TextView) itemView.findViewById(R.id.id_blog_date);
                    final TextView blogTitle = (TextView) itemView.findViewById(R.id.id_blog_title);
                    LinearLayout blogAuthorLayout = itemView.findViewById(R.id.id_blog_author_layout);
                    final TextView blogSummer = (TextView) itemView.findViewById(R.id.id_blog_summery);
                    LinearLayout readMoreTextLayout = (LinearLayout) itemView.findViewById(R.id.id_read_more_txt_layout);
                    //TextView slidePositionText = (TextView) itemView.findViewById(R.id.id_slide_position);
                    final TextView toastMessageText = (TextView) toastView.findViewById(R.id.custom_toast_message);
                    if (articleType.equalsIgnoreCase("news")
                            && mNewsList != null && !mNewsList.isEmpty() && mNewsList.size() > position) {
                        if(position % TOAST_POSITION == 0){
                            if(mTotalBlogCount > position) {
                                showToast(mContext,toastMessageText,toastView,(mTotalBlogCount-position) + " unread news below");
                                //Toast.makeText(mContext, (mTotalBlogCount-position) + " unread news below", Toast.LENGTH_SHORT).show();
                            }else if(getCount() > position){
                                //Toast.makeText(mContext, (getCount()-position) + " unread news below", Toast.LENGTH_SHORT).show();
                                showToast(mContext,toastMessageText,toastView,(getCount()-position) + " unread news below");
                            }
                        }
                        try {
                            newsBlurImage.setVisibility(View.VISIBLE);
                            newsImage.setVisibility(View.VISIBLE);
                            blogImage.setVisibility(View.GONE);
                            final News news = mNewsList.get(position);
                            if (news != null) {
                                if (!TextUtils.isEmpty(news.getImage())) {
                                    final String newsImageURL = news.getImage();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String decodedURL = URLDecoder.decode(newsImageURL,"UTF-8");
                                                URL url = new URL(decodedURL);
                                                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                                                final Bitmap bitmap = BitmapFactory.decodeStream(uri.toURL().openConnection().getInputStream());
                                                if(bitmap != null) {
                                                    mHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                int minWidth = UtilityMethods.dpToPx(220);
                                                                int minHeight = UtilityMethods.dpToPx(180);
                                                                float scale = minWidth / bitmap.getWidth();
                                                                int newHeight = Math.round(bitmap.getHeight() * scale);
                                                                if (bitmap.getWidth() < minWidth) {
                                                                    if (newHeight > minHeight) {
                                                                        float scale2 = minHeight / bitmap.getHeight();
                                                                        int newWidth = Math.round(bitmap.getWidth() * scale2);
                                                                        newsImage.getLayoutParams().height = minHeight;
                                                                        newsImage.getLayoutParams().width = newWidth;
                                                                    } else {
                                                                        newsImage.getLayoutParams().height = newHeight;
                                                                        newsImage.getLayoutParams().width = minWidth;
                                                                    }
                                                                    newsImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                                                    newsBlurImage.setImageBitmap(getBlurImageBitmap(bitmap, 2f));
                                                                } else {
                                                                    newsImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                                                    newsBlurImage.setImageBitmap(getBlurImageBitmap(bitmap, 20f));
                                                                }
                                                                newsImage.setImageBitmap(bitmap);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            String decodedURL = URLDecoder.decode(newsImageURL,"UTF-8");
                                                            URL url = new URL(decodedURL);
                                                            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                                                            Picasso.get().load(uri.toString())
                                                                .placeholder(R.drawable.placeholder)
                                                                .into(new Target() {
                                                                    @Override
                                                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                        if(bitmap != null) {
                                                                            int minWidth = UtilityMethods.dpToPx(220);
                                                                            int minHeight = UtilityMethods.dpToPx(180);
                                                                            float scale = minWidth / bitmap.getWidth();
                                                                            int newHeight = Math.round(bitmap.getHeight() * scale);
                                                                            if (bitmap.getWidth() < minWidth) {
                                                                                if (newHeight > minHeight) {
                                                                                    float scale2 = minHeight / bitmap.getHeight();
                                                                                    int newWidth = Math.round(bitmap.getWidth() * scale2);
                                                                                    newsImage.getLayoutParams().height = minHeight;
                                                                                    newsImage.getLayoutParams().width = newWidth;
                                                                                } else {
                                                                                    newsImage.getLayoutParams().height = newHeight;
                                                                                    newsImage.getLayoutParams().width = minWidth;
                                                                                }
                                                                                newsImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                                                                newsBlurImage.setImageBitmap(getBlurImageBitmap(bitmap, 2f));
                                                                            } else {
                                                                                newsImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                                                                newsBlurImage.setImageBitmap(getBlurImageBitmap(bitmap, 20f));
                                                                            }
                                                                            newsImage.setImageBitmap(bitmap);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onBitmapFailed(Exception e,Drawable errorDrawable) {
                                                                        Log.i(TAG, "onBitmapFailed: position "+ position);
                                                                    }

                                                                    @Override
                                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                                        Log.i(TAG, "onPrepareLoad: position " + position);
                                                                    }
                                                                });
                                                        } catch (Exception e1) {
                                                            e1.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }).start();

                                }else{
                                    newsImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder));
                                }
                                View fabButtonLayout = itemView.findViewById(R.id.id_share_menu);
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fabButtonLayout.getLayoutParams();
                                params.setMargins(0, UtilityMethods.dpToPx(191), 0, 0);
                                fabButtonLayout.setLayoutParams(params);

                                final FloatingActionMenu shareMenu = (FloatingActionMenu)itemView.findViewById(R.id.id_share_button);
                                shareMenu.setIconAnimated(false);
                                shareMenu.setClosedOnTouchOutside(true);
                                FloatingActionButton moreMenuButton = (FloatingActionButton) itemView.findViewById(R.id.id_more_menu);
                                FloatingActionButton twitterMenuButton = (FloatingActionButton)itemView.findViewById(R.id.id_twitter_menu);
                                FloatingActionButton facebookMenuButton = (FloatingActionButton)itemView.findViewById(R.id.id_facebook_menu);

                                facebookMenuButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i(TAG, "onClick: Sharefb post");
                                        shareNewsOnFacebook(news);
                                        if(shareMenu.isOpened()){
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                twitterMenuButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i(TAG, "onClick: Twitter button");
                                        String contentURL = "";
                                        if(!TextUtils.isEmpty(news.getSearchTitle())){
                                            contentURL = BuildConfigConstants.WEB_URL + "news-detail/" + news.getSearchTitle();
                                        }
                                        sharePostOnTwitter(news.getTitle(),contentURL,"Share News");
                                        if(shareMenu.isOpened()){
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                moreMenuButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i(TAG, "onClick: More button");
                                        String contentURL = "";
                                        if(!TextUtils.isEmpty(news.getSearchTitle())){
                                            contentURL = BuildConfigConstants.WEB_URL + "news-detail/" + news.getSearchTitle();
                                        }
                                        shareContent(news.getTitle(),contentURL,"Share News");
                                        if(shareMenu.isOpened()){
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });

                                if (!TextUtils.isEmpty(news.getSource())) {
                                    blogAuthor.setVisibility(View.VISIBLE);
                                    blogAuthor.setText(news.getSource());
                                }else{
                                    blogAuthor.setVisibility(View.GONE);
                                }
                                if (news.getCreateTime() != null) {
                                    blogDate.setVisibility(View.VISIBLE);
                                    if (!TextUtils.isEmpty(news.getSource())) {
                                        //newsDate.setText(" | " + UtilityMethods.getDate(news.getCreateTime(), "dd MMM yyyy"));
                                        blogDate.setText(" | " + UtilityMethods.getDate(news.getCreateTime(), "dd MMM yyyy"));
                                    } else {
                                        //newsDate.setText(UtilityMethods.getDate(news.getCreateTime(), "dd MMM yyyy"));
                                        blogDate.setText(UtilityMethods.getDate(news.getCreateTime(), "dd MMM yyyy"));
                                    }
                                }else{
                                    blogDate.setVisibility(View.GONE);
                                }
                                if (!TextUtils.isEmpty(news.getTitle())) {
                                    blogTitle.setVisibility(View.VISIBLE);
                                    blogTitle.setText(news.getTitle());
                                }else{
                                    blogTitle.setVisibility(View.GONE);
                                }
                                if (!TextUtils.isEmpty(news.getDescription())) {
                                    blogSummer.setVisibility(View.VISIBLE);
                                    blogSummer.setText(news.getDescription());
                                    ViewTreeObserver observer = blogSummer.getViewTreeObserver();
                                    observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            int maxLines = (int) blogSummer.getHeight()
                                                    / blogSummer.getLineHeight();
                                            blogSummer.setMaxLines(maxLines);
                                            blogSummer.setEllipsize(TextUtils.TruncateAt.END);
                                            blogSummer.getViewTreeObserver().removeGlobalOnLayoutListener(
                                                    this);
                                        }
                                    });
                                }else{
                                    blogSummer.setVisibility(View.GONE);
                                }
                                /*itemView.setOnTouchListener(new OnSwipeTouchListener(mContext){
                                    public void onSwipeRight() {
                                    }

                                    public void onSwipeLeft() {
                                    }

                                    public void onSwipeTop() {
                                    }

                                    public void onSwipeBottom() {
                                        if(position == 0) {
                                            showToast(mContext, toastMessageText, toastView, "Already showing first articles");
                                        }
                                    }
                                });*/
                                /*newsBlurImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showNewsDetails(news);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                newsImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showNewsDetails(news);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                blogAuthor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showNewsDetails(news);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                blogDate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showNewsDetails(news);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                blogTitle.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showNewsDetails(news);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                blogSummer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showNewsDetails(news);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });*/
                                itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(shareMenu.isOpened()) {
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                readMoreTextLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showNewsDetails(news);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else if (articleType.equalsIgnoreCase("blog") &&
                            mBlogList != null && !mBlogList.isEmpty() && mBlogList.size() > position) {
                        if(position % TOAST_POSITION == 0){
                            if(mTotalBlogCount > position) {
                                //Toast.makeText(mContext, (mTotalBlogCount-position) + " unread articles below", Toast.LENGTH_SHORT).show();
                                showToast(mContext,toastMessageText,toastView,(mTotalBlogCount-position) + " unread articles below");
                            }else if(getCount() > position){
                                //Toast.makeText(mContext, (getCount()-position) + " unread articles below", Toast.LENGTH_SHORT).show();
                                showToast(mContext,toastMessageText,toastView,(getCount()-position) + " unread articles below");
                            }
                        }
                        try {
                            newsBlurImage.setVisibility(View.GONE);
                            newsImage.setVisibility(View.GONE);
                            blogImage.setVisibility(View.VISIBLE);
                            final Blog blog = mBlogList.get(position);
                            if (blog != null) {
                                if (!TextUtils.isEmpty(blog.getSTitleImage())) {
                                    String blogImageURL = mBlogList.get(position).getSTitleImage()+"=h300";
                                    URL url = new URL(blogImageURL);
                                    //Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    Picasso.get().load(blogImageURL)
                                            .placeholder(R.drawable.placeholder)
                                            .into(blogImage);
                                    //blogImage.setImageBitmap(bitmap);
                                    blogImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                }else{
                                    blogImage.setImageResource(R.drawable.placeholder);
                                }

                                if (!TextUtils.isEmpty(blog.getCreateByName())) {
                                    blogAuthor.setVisibility(View.VISIBLE);
                                    blogAuthor.setText(blog.getCreateByName());
                                }else{
                                    blogAuthor.setVisibility(View.GONE);
                                }
                                if (blog.getCreateTime() != null) {
                                    blogDate.setVisibility(View.VISIBLE);
                                    if (!TextUtils.isEmpty(blog.getCreateByName())) {
                                        blogDate.setText(" | " + UtilityMethods.getDate(blog.getCreateTime(), "dd MMM yyyy"));
                                    } else {
                                        blogDate.setText(UtilityMethods.getDate(blog.getCreateTime(), "dd MMM yyyy"));
                                    }
                                }else{
                                    blogDate.setVisibility(View.GONE);
                                }
                                if (!TextUtils.isEmpty(blog.getTitle())) {
                                    blogTitle.setVisibility(View.VISIBLE);
                                    blogTitle.setText(blog.getTitle());
                                }else{
                                    blogTitle.setVisibility(View.GONE);
                                }
                                if (!TextUtils.isEmpty(blog.getExcerpt())) {
                                    blogSummer.setVisibility(View.VISIBLE);
                                    blogSummer.setText(blog.getExcerpt());
                                    ViewTreeObserver observer = blogSummer.getViewTreeObserver();
                                    observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            int maxLines = (int) blogSummer.getHeight()
                                                    / blogSummer.getLineHeight();
                                            blogSummer.setMaxLines(maxLines);
                                            blogSummer.setEllipsize(TextUtils.TruncateAt.END);
                                            blogSummer.getViewTreeObserver().removeGlobalOnLayoutListener(
                                                    this);
                                        }
                                    });
                                }else{
                                    blogSummer.setVisibility(View.GONE);
                                }

                                int imageHeight = 0;
                                try {
                                    imageHeight = (UtilityMethods.getScreenWidth(mContext) * 2 / 3) + UtilityMethods.dpToPx(11);
                                }catch (Exception e){
                                    imageHeight = (UtilityMethods.getScreenHeight(mContext)/3) + 50;
                                }
                                View fabButtonLayout = itemView.findViewById(R.id.id_share_menu);
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fabButtonLayout.getLayoutParams();
                                params.setMargins(0,imageHeight,0,0);
                                fabButtonLayout.setLayoutParams(params);
                                final FloatingActionMenu shareMenu = (FloatingActionMenu)itemView.findViewById(R.id.id_share_button);
                                shareMenu.setIconAnimated(false);
                                shareMenu.setClosedOnTouchOutside(true);
                                FloatingActionButton moreMenuButton = itemView.findViewById(R.id.id_more_menu);
                                FloatingActionButton twitterMenuButton = itemView.findViewById(R.id.id_twitter_menu);
                                FloatingActionButton facebookMenuButton = itemView.findViewById(R.id.id_facebook_menu);
                                facebookMenuButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i(TAG, "onClick: Sharefb post");
                                        shareBlogOnFacebook(blog);
                                        if(shareMenu.isOpened()){
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                twitterMenuButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i(TAG, "onClick: Twitter button");
                                        String contentURL = "";
                                        if(!TextUtils.isEmpty(blog.getSearchTitle())){
                                            contentURL = BuildConfigConstants.WEB_URL + "post/" + blog.getSearchTitle();
                                        }
                                        sharePostOnTwitter(blog.getTitle(),contentURL,"Share Article");
                                        if(shareMenu.isOpened()){
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                moreMenuButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i(TAG, "onClick: More button");
                                        String contentURL = "";
                                        if(!TextUtils.isEmpty(blog.getSearchTitle())){
                                            contentURL = BuildConfigConstants.WEB_URL + "post/" + blog.getSearchTitle();
                                        }
                                        shareContent(blog.getTitle(),contentURL,"Share Article");
                                        if(shareMenu.isOpened()){
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });

                                /*itemView.setOnTouchListener(new OnSwipeTouchListener(mContext){
                                    public void onSwipeRight() {
                                    }

                                    public void onSwipeLeft() {
                                    }

                                    public void onSwipeTop() {
                                    }

                                    public void onSwipeBottom() {
                                        if(position == 0) {
                                            showToast(mContext, toastMessageText, toastView, "Already showing first articles");
                                        }
                                    }
                                });*/
                                /*blogImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showBlogDetails(blog);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                blogAuthor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showBlogDetails(blog);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                blogDate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showBlogDetails(blog);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                blogTitle.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showBlogDetails(blog);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                blogSummer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showBlogDetails(blog);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });*/
                                itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(shareMenu.isOpened()) {
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                                readMoreTextLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!shareMenu.isOpened()) {
                                            showBlogDetails(blog);
                                        }else{
                                            shareMenu.toggle(true);
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (mTotalBlogCount != 0 && (mTotalBlogCount > (getCount() - 1)) && (position > (getCount() - 4))) {
                Log.i(TAG, "instantiateItem: getting blog list - current count -> " + (getCount() - 1) + " Position " + position);
                if(articleType.equalsIgnoreCase("news")){
                    setNewsList();
                }else if(articleType.equalsIgnoreCase("blog")) {
                    setBlogList();
                }
            }

            if (itemView != null) {
                container.addView(itemView);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (itemView != null && container != null) {
                container.addView(itemView);
            }
        }
        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setNewsList() {
        new GetNewsList().execute();
    }

    private void setBlogList() {
        new GetBlogList().execute();
    }

    BroadcastReceiver internetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "internetReceiver - onReceive: ");
            if (mTotalBlogCount != 0 && (mTotalBlogCount > (getCount() - 11))) {
                if(articleType.equalsIgnoreCase("news")){
                    setNewsList();
                }else if(articleType.equalsIgnoreCase("blog")) {
                    setBlogList();
                }
            }
        }
    };

    private class GetNewsList extends AsyncTask<Void, Void, List<News>> {

        private String errorMsg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mContext == null) {
                return;
            }
            if (!UtilityMethods.isInternetConnected(mContext)) {
                if (mTotalBlogCount != 0 && (mTotalBlogCount > (getCount() - 1))) {
                    isLoadingDataViewEnable = true;
                }
                UtilityMethods.showErrorSnackBar(mRootView, mContext.getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                cancel(true);
            } else {
                isLoadingDataViewEnable = false;
            }
        }

        @Override
        protected List<News> doInBackground(Void... objects) {
            try {
                int currentOffsetCount = getCount() - 1;
                HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                NewsListResponse newsListResponse = mNewsEndPoint.getNewsList(1l, "1", currentOffsetCount, 10)
                        .setRequestHeaders(httpHeaders).execute();
                if (newsListResponse != null && newsListResponse.getStatus()) {
                    final List<News> newsList = newsListResponse.getNews();
                    return newsList;
                } else {
                    if (newsListResponse != null && !TextUtils.isEmpty(newsListResponse.getErrorMessage())) {
                        errorMsg = newsListResponse.getErrorMessage();
                    } else {
                        errorMsg = "Something went wrong.Please try again!";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isLoadingDataViewEnable = true;
            return null;
        }

        @Override
        protected void onPostExecute(List<News> newsList) {
            super.onPostExecute(newsList);
            if (newsList != null && !newsList.isEmpty()) {
                if (mNewsList == null) {
                    mNewsList = new ArrayList<>();
                }
                List<News> newNewsList = new ArrayList<>();
                for (int i = 0; i < newsList.size(); i++) {
                    News news = newsList.get(i);
                    boolean isContain = false;
                    for (int k = 0; k < mNewsList.size(); k++) {
                        News news1 = mNewsList.get(k);
                        if (news1.getTitle() != null && news1.containsValue(news.getTitle())) {
                            isContain = true;
                            break;
                        }
                    }
                    if (!isContain) {
                        newNewsList.add(news);
                    }
                }
                mNewsList.addAll(newNewsList);
                notifyDataSetChanged();
            }
        }
    }

    private class GetBlogList extends AsyncTask<Void, Void, List<Blog>> {

        private String errorMsg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mContext == null) {
                return;
            }
            if (!UtilityMethods.isInternetConnected(mContext)) {
                if (mTotalBlogCount != 0 && (mTotalBlogCount > (getCount() - 1))) {
                    isLoadingDataViewEnable = true;
                }
                UtilityMethods.showErrorSnackBar(mRootView, mContext.getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                cancel(true);
            } else {
                isLoadingDataViewEnable = false;
            }
        }

        @Override
        protected List<Blog> doInBackground(Void... objects) {
            try {
                int currentOffsetCount = getCount() - 1;
                HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                BlogListResponse blogsResponse = mBlogEndPoint.getBlogs(1l, "1", currentOffsetCount, 10)
                        .setBlogStatus(Constants.BlogStatus.Publish.toString()).setRequestHeaders(httpHeaders).execute();
                if (blogsResponse != null && blogsResponse.getStatus()) {
                    final List<Blog> blogList = blogsResponse.getBlogs();
                    return blogList;
                } else {
                    if (blogsResponse != null && !TextUtils.isEmpty(blogsResponse.getErrorMessage())) {
                        errorMsg = blogsResponse.getErrorMessage();
                    } else {
                        errorMsg = "Something went wrong.Please try again!";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isLoadingDataViewEnable = true;
            return null;
        }

        @Override
        protected void onPostExecute(List<Blog> blogList) {
            super.onPostExecute(blogList);
            try {
                if (blogList != null && !blogList.isEmpty()) {
                    if (mBlogList == null) {
                        mBlogList = new ArrayList<>();
                    }
                    List<Blog> newBlogList = new ArrayList<>();
                    for (int i = 0; i < blogList.size(); i++) {
                        Blog blog = blogList.get(i);
                        boolean isContain = false;
                        for (int k = 0; k < mBlogList.size(); k++) {
                            Blog blog1 = mBlogList.get(k);
                            if (blog1.getSearchTitle() != null && blog1.containsValue(blog.getSearchTitle())) {
                                isContain = true;
                                break;
                            }
                        }
                        if (!isContain) {
                            newBlogList.add(blog);
                        }
                    }
                    mBlogList.addAll(newBlogList);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showNewsDetails(News news){
        if (mContext == null) {
            return;
        }
        if (!UtilityMethods.isInternetConnected(mContext)) {
            UtilityMethods.showErrorSnackBar(mRootView, mContext.getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            return;
        }
        String data = news.getClickURL();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            UtilityMethods.showSnackBar(mRootView,"Page not found!",Snackbar.LENGTH_LONG);
        }catch (Exception e) {
            e.printStackTrace();
            UtilityMethods.showSnackBar(mRootView,"Page not found!",Snackbar.LENGTH_LONG);
        }
        /*try {
            List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentActivities(intent,0);
            if(resolveInfoList != null){
                for(int i=0; i<resolveInfoList.size();i++){
                    if(resolveInfoList.get(i).activityInfo != null && resolveInfoList.get(i).activityInfo.packageName != null
                            && resolveInfoList.get(i).activityInfo.packageName.equalsIgnoreCase("com.android.chrome")){
                        intent.setPackage("com.android.chrome");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    private void showBlogDetails(Blog blog) {
        if (mContext == null) {
            return;
        }
        Intent intent = new Intent(mContext, BlogDetailsActivity.class);
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
        intent.putExtra("ArticleType","blog");
        mContext.startActivity(intent);
    }

    /*class GetBitmapImage extends AsyncTask<Void,Void,Bitmap> {
        String imageURL;
        ImageView newsImage, blurImage;
        GetBitmapImage(String imageURL, ImageView blurImage,ImageView newsImage){
            this.imageURL = imageURL;
            this.blurImage = blurImage;
            this.newsImage = newsImage;
        }

        @Override
        protected Bitmap doInBackground(Void... objects) {
            Bitmap bitmapImage=null;
            try {
                URL imageurl = new URL(imageURL);
                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                imageurl = uri.toURL();
                URLConnection connection = new URL(imageurl.toString()).openConnection();
                bitmapImage = BitmapFactory.decodeStream(connection.getInputStream()) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmapImage;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                newsImage.setImageBitmap(bitmap);
                blurImage.setImageBitmap(getBlurImageBitmap(bitmap));
            }else{
                blurImage.setImageResource(R.drawable.bg);
            }
        }
    }*/

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap getBlurImageBitmap(Bitmap image, float blurRadius) {
        try {
            if(image != null) {
                final float BITMAP_SCALE = 0.5f;
                //final float BLUR_RADIUS = 2f;
                int width = Math.round(image.getWidth() * BITMAP_SCALE);
                int height = Math.round(image.getHeight() * BITMAP_SCALE);

                Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
                Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

                RenderScript rs = RenderScript.create(mContext);
                ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
                Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
                theIntrinsic.setRadius(blurRadius);
                theIntrinsic.setInput(tmpIn);
                theIntrinsic.forEach(tmpOut);
                tmpOut.copyTo(outputBitmap);
                return outputBitmap;
            }else{
                return BitmapFactory.decodeResource(mContext.getResources(),R.drawable.splash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeResource(mContext.getResources(),R.drawable.splash);
    }

    private void shareBlogOnFacebook(Blog blog){
        String title = "",description="",imageURL = "",contentUrl="";
        if(!TextUtils.isEmpty(blog.getTitle())){
            title = blog.getTitle();
        }
        if(!TextUtils.isEmpty(blog.getExcerpt())){
            description = blog.getExcerpt();
        }
        if(!TextUtils.isEmpty(blog.getSTitleImage())){
            imageURL = blog.getSTitleImage();
        }
        if(!TextUtils.isEmpty(blog.getSearchTitle())){
            contentUrl = BuildConfigConstants.WEB_URL + "post/"+ blog.getSearchTitle();
        }
        CallbackManager callbackManager;
        if(!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(mContext);
        }
        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentTitle(title)
                .setContentDescription(description)
                .setContentUrl(Uri.parse(contentUrl))
                .setImageUrl(Uri.parse(imageURL))
                .build();
        ShareDialog.show((BlogListActivity)mContext,shareLinkContent);

    }

    private void shareNewsOnFacebook(News news){
        String title = "",description="",imageURL = "",contentUrl="";
        if(!TextUtils.isEmpty(news.getTitle())){
            title = news.getTitle();
        }
        if(!TextUtils.isEmpty(news.getDescription())){
            description = news.getDescription();
        }
        if(!TextUtils.isEmpty(news.getImage())){
            imageURL = news.getImage();
        }
        if(!TextUtils.isEmpty(news.getSearchTitle())){
            contentUrl = BuildConfigConstants.WEB_URL + "news-detail/" + news.getSearchTitle();
        }
        //CallbackManager callbackManager;
        if(!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(mContext);
        }
        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentTitle(title)
                .setContentDescription(description)
                .setContentUrl(Uri.parse(contentUrl))
                .setImageUrl(Uri.parse(imageURL))
                .build();
        ShareDialog.show((BlogListActivity)mContext,shareLinkContent);



        /*FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "onError: " + error);
            }

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.i(TAG, "onSuccess: " + result);
            }
        };
        callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog((BlogListActivity)mContext);
        shareDialog.registerCallback(callbackManager,shareCallback);
        shareDialog.show(shareLinkContent);*/

    }

    private void sharePostOnTwitter(String title, String contentURL, String shareIntentTitle){
        Intent tweet = new Intent(Intent.ACTION_VIEW);
        String tweetUrl = "https://twitter.com/intent/tweet?text="+ title+ " &url=" + contentURL;

        tweet.setData(Uri.parse(tweetUrl));
        mContext.startActivity(tweet);
    }

    private void shareContent(String title , String contentURL, String shareIntentTitle){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String content = "";
        if(!TextUtils.isEmpty(title)){
            content = title;
        }
        if(!TextUtils.isEmpty(contentURL)){
            content =  content + "\n" + contentURL;
        }
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "clicbrics");
        mContext.startActivity(Intent.createChooser(intent, shareIntentTitle));
    }

    private void showToast(Context context, TextView textView, View toastView, String message){
        Toast toast = new Toast(context);
        toast.setView(toastView);
        textView.setText(message);
        toast.setDuration(500);
        toast.show();
    }
}

