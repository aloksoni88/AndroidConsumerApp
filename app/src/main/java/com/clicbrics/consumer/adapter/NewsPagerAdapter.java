package com.clicbrics.consumer.adapter;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buy.housing.backend.blogEndPoint.model.BlogListResponse;
import com.buy.housing.backend.newsEndPoint.NewsEndPoint;
import com.buy.housing.backend.newsEndPoint.model.News;
import com.buy.housing.backend.newsEndPoint.model.NewsListResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.BlogListActivity;
import com.clicbrics.consumer.customview.ThreeTwoImageView;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpHeaders;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 02-01-2018.
 */

public class NewsPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener{

    private static final String TAG = NewsPagerAdapter.class.getSimpleName();

    private NewsEndPoint mNewsEndPoint;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<News> mNewsList;
    private int mTotalNewsCount;
    private View mRootView;
    private boolean isLoadingDataViewEnable = false;
    private Handler mHandler;
    //private ImageButton backImage,nextImage;
    private ViewPager mViewPager;
    private boolean isNotifyDataSetChangeCalled = false;

    public NewsPagerAdapter(Context context,List<News> newsList, int totalNewsCount, View rootView, ViewPager newsListPager) {
        try {
            mContext = context;
            mNewsList = newsList;
            mTotalNewsCount = totalNewsCount;
            mRootView = rootView;
            mHandler = new Handler();
            mViewPager = newsListPager;
            /*this.backImage = backImage;
            this.nextImage = nextImage;*/
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            buildWebService();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.clicbrics.internetconnection");
            context.registerReceiver(internetReceiver, intentFilter);
            isNotifyDataSetChangeCalled = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void setArrowImageVisibility(int backImageVisibility, int nextImageVisibility){
        if(backImage != null){
            backImage.setVisibility(backImageVisibility);
        }
        if(nextImage != null){
            nextImage.setVisibility(nextImageVisibility);
        }
    }*/


    @Override
    public int getCount() {
        if(isNotifyDataSetChangeCalled){
            isNotifyDataSetChangeCalled = false;
            notifyDataSetChanged();
        }
        if(mNewsList != null){
            return mNewsList.size()+1;
        }else {
            return 0;
        }
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        /*int index = mNewsList.indexOf(object);
        if(index == -1) {
            return POSITION_NONE;
        }else{
            return  index;
        }*/
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildWebService() {
        mNewsEndPoint = EndPointBuilder.getNewsEndPoint();
    }

    /*@Override
    public Object instantiateItem(ViewGroup container, int position) {
        return setLayoutView(container,position);
    }*/

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position){
        View itemView = null;
        try {
            if ((mTotalNewsCount == (getCount() - 1)) && (mTotalNewsCount == position)) {
                if (isLoadingDataViewEnable) {
                    itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, viewGroup, false);
                    TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                    loadingTextMsg.setText("Loading News...");
                    /*backImage = (ImageView) itemView.findViewById(R.id.id_back_image);
                    nextImage = (ImageView) itemView.findViewById(R.id.id_next_image);
                    setArrowImageVisibility(View.GONE,View.GONE);*/
                } else {
                    itemView = mLayoutInflater.inflate(R.layout.content_all_blog_read_layout, viewGroup, false);
                    TextView allListReadMsg  = itemView.findViewById(R.id.id_all_list_read_msg);
                    allListReadMsg.setText(mContext.getString(R.string.read_all_news_msg));
                    /*backImage = (ImageView) itemView.findViewById(R.id.id_back_image);
                    nextImage = (ImageView) itemView.findViewById(R.id.id_next_image);
                    setArrowImageVisibility(View.GONE,View.GONE);*/
                }
            } else if (isLoadingDataViewEnable && ((getCount() - 1) == position)) {
                itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, viewGroup, false);
                TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                loadingTextMsg.setText("Loading News...");
                /*backImage = (ImageView) itemView.findViewById(R.id.id_back_image);
                nextImage = (ImageView) itemView.findViewById(R.id.id_next_image);
                setArrowImageVisibility(View.GONE,View.GONE);*/
            } else{
                if (mTotalNewsCount > (getCount() - 1) && ((getCount() - 1) == position)) {
                    itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, viewGroup, false);
                    TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                    loadingTextMsg.setText("Loading News...");
                    //setArrowImageVisibility(View.GONE, View.GONE);
                    /*backImage = (ImageView) itemView.findViewById(R.id.id_back_image);
                    nextImage = (ImageView) itemView.findViewById(R.id.id_next_image);
                    setArrowImageVisibility(View.GONE,View.GONE);*/
                }else{
                    itemView = mLayoutInflater.inflate(R.layout.content_news_list, viewGroup, false);
                    final ThreeTwoImageView newsImage = (ThreeTwoImageView) itemView.findViewById(R.id.id_news_image);
                    final ThreeTwoImageView newsBlurImage = (ThreeTwoImageView) itemView.findViewById(R.id.id_news_blurr_image);
                    TextView newsSource = (TextView) itemView.findViewById(R.id.id_news_source);
                    TextView newsDate = (TextView) itemView.findViewById(R.id.id_news_date);
                    TextView newsDate2 = (TextView) itemView.findViewById(R.id.id_news_date2);
                    final TextView newsTitle = (TextView) itemView.findViewById(R.id.id_news_title);
                    final TextView newsDesc = (TextView) itemView.findViewById(R.id.id_news_summery);
                    final LinearLayout readMoreLayout = (LinearLayout) itemView.findViewById(R.id.id_read_more_txt_layout);
                    final FloatingActionButton shareButton = (FloatingActionButton) itemView.findViewById(R.id.id_share_menu);
                    final ImageView collapsedShareButton = (ImageView) itemView.findViewById(R.id.share_collapsed);
                    /*backImage = (ImageButton) itemView.findViewById(R.id.id_back_image);
                    nextImage = (ImageButton) itemView.findViewById(R.id.id_next_image);*/
                    /*if(position == 0) {
                        setArrowImageVisibility(View.GONE, View.VISIBLE);
                    }else if(position == (getCount()-1)){
                        setArrowImageVisibility(View.GONE,View.GONE);
                    }else{
                        setArrowImageVisibility(View.VISIBLE, View.VISIBLE);
                    }
                    backImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mViewPager != null){
                                mViewPager.setCurrentItem(position-1);
                            }
                        }
                    });
                    nextImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mViewPager != null){
                                mViewPager.setCurrentItem(position+1);
                            }
                        }
                    });*/
                    /*Toolbar toolbar = (Toolbar) itemView.findViewById(R.id.id_bloglist_toolbar);
                    toolbar.setTitle("");
                    toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.white));
                    ((BlogListActivity) mContext).setSupportActionBar(toolbar);
                    ((BlogListActivity)mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    ((BlogListActivity)mContext).getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);
                    ((BlogListActivity)mContext).getSupportActionBar().setDisplayShowTitleEnabled(true);*/
                    final TextView collapsed_title = (TextView) itemView.findViewById(R.id.toolbar_collapsed_title);
                    AppBarLayout appBarLayout = (AppBarLayout) itemView.findViewById(R.id.id_appbar);


                    final News news = mNewsList.get(position);
                    if (news != null) {
                        final View finalItemView = itemView;
                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;

                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                Log.d(TAG, "verticalOffset->" + verticalOffset);

                                if (verticalOffset == 0) {
                                    collapsed_title.setVisibility(View.GONE);
                                    collapsedShareButton.setVisibility(View.GONE);
                                    return;
                                }
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    ((BlogListActivity)mContext).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
                                    finalItemView.findViewById(R.id.toolbar_collapsed_layout).setBackground(ContextCompat.getDrawable(mContext,R.color.light_white));
                                    finalItemView.findViewById(R.id.toolbar_collapsed_layout).setVisibility(View.VISIBLE);
                                    collapsed_title.setVisibility(View.VISIBLE);
                                    collapsedShareButton.setVisibility(View.VISIBLE);
                                    if (!TextUtils.isEmpty(news.getTitle())) {
                                        collapsed_title.setText(news.getTitle());
                                    }else{
                                        collapsed_title.setText("News");
                                    }
                                    isShow = true;
                                } else if (isShow) {
                                    ((BlogListActivity)mContext).getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);
                                    finalItemView.findViewById(R.id.toolbar_collapsed_layout).setBackground(null);
                                    finalItemView.findViewById(R.id.toolbar_collapsed_layout).setVisibility(View.INVISIBLE);
                                    collapsed_title.setVisibility(View.GONE);
                                    collapsedShareButton.setVisibility(View.GONE);
                                    isShow = false;
                                }
                            }
                        });

                        if (!TextUtils.isEmpty(news.getImage())) {
                            newsBlurImage.setVisibility(View.GONE);
                            try {
                                Log.i(TAG,  news.getTitle() + " " + news.getImage());
                                final String newsImageURL = news.getImage();
                                String decodedURL = URLDecoder.decode(newsImageURL,"UTF-8");
                                URL url = new URL(decodedURL);
                                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                                Log.i(TAG, "instantiateItem: ImageURl -> "+ uri.toString());
                                //final Bitmap bitmap = BitmapFactory.decodeStream(uri.toURL().openConnection().getInputStream());
                                //final  Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(uri.toURL().openConnection().getInputStream()),400,200,false);
                                Picasso.get().load(uri.toString().trim())
                                        .resize(0,300)
                                        .placeholder(R.drawable.placeholder).into(newsImage);
                                /*final  Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeStream(uri.toURL().openConnection().getInputStream()));
                                newsImage.setImageBitmap(bitmap);*/
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {
                                    final String newsImageURL = news.getImage();
                                    String decodedURL = URLDecoder.decode(newsImageURL,"UTF-8");
                                    URL url = new URL(decodedURL);
                                    URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                                    final  Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(uri.toURL().openConnection().getInputStream()),400,200,false);
                                    newsImage.setImageBitmap(bitmap);
                                }catch (Exception e2){
                                    newsImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder));
                                }
                            }
                            /*new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String decodedURL = URLDecoder.decode(newsImageURL,"UTF-8");
                                        URL url = new URL(decodedURL);
                                        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                                        //final Bitmap bitmap = BitmapFactory.decodeStream(uri.toURL().openConnection().getInputStream());
                                        final  Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(uri.toURL().openConnection().getInputStream()),400,200,false);
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
                                                    Picasso.with(mContext).load(uri.toString())
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
                                                                public void onBitmapFailed(Drawable errorDrawable) {
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
                            }).start();*/

                        }else{
                            newsImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder));
                        }
                        shareButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i(TAG, "onClick: More button");
                                String contentURL = "";
                                if(!TextUtils.isEmpty(news.getSearchTitle())){
                                    contentURL = BuildConfigConstants.WEB_URL + "news-detail/" + news.getSearchTitle();
                                }
                                shareContent(news,news.getTitle(),contentURL,"Share News");
                            }
                        });
                        collapsedShareButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i(TAG, "onClick: More button");
                                String contentURL = "";
                                if(!TextUtils.isEmpty(news.getSearchTitle())){
                                    contentURL = BuildConfigConstants.WEB_URL + "news-detail/" + news.getSearchTitle();
                                }
                                shareContent(news,news.getTitle(),contentURL,"Share News");
                            }
                        });
                        readMoreLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showNewsDetails(news);
                            }
                        });

                        if (!TextUtils.isEmpty(news.getSource())) {
                            newsSource.setVisibility(View.VISIBLE);
                            newsSource.setText(news.getSource());
                        }else{
                            newsSource.setVisibility(View.GONE);
                        }
                        if (news.getCreateTime() != null) {
                            newsDate.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(news.getSource())) {
                                if(news.getSource().length() <= 20) {
                                    newsDate2.setVisibility(View.GONE);
                                    newsDate.setText(" | " + UtilityMethods.getDate(news.getCreateTime(), "dd MMM yyyy"));
                                }else {
                                    newsDate.setVisibility(View.GONE);
                                    newsDate2.setVisibility(View.VISIBLE);
                                    newsDate2.setText(UtilityMethods.getDate(news.getCreateTime(), "dd MMM yyyy"));
                                }
                            } else {
                                newsDate2.setVisibility(View.GONE);
                                newsDate.setText(UtilityMethods.getDate(news.getCreateTime(), "dd MMM yyyy"));
                            }
                        }else{
                            newsDate.setVisibility(View.GONE);
                            newsDate2.setVisibility(View.GONE);
                        }
                        if (!TextUtils.isEmpty(news.getTitle())) {
                            newsTitle.setVisibility(View.VISIBLE);
                            newsTitle.setText(news.getTitle());
                        }else{
                            newsTitle.setVisibility(View.GONE);
                        }
                        if (!TextUtils.isEmpty(news.getDescription())) {
                            newsDesc.setVisibility(View.VISIBLE);
                            newsDesc.setText(news.getDescription());
                        }else{
                            newsDesc.setVisibility(View.GONE);
                        }
                        newsImage.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                        newsBlurImage.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                        newsSource.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                        newsDate.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                        newsTitle.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                        newsDesc.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                    }
                }
            }
            if (mTotalNewsCount != 0 && (mTotalNewsCount > (getCount() - 1)) && (position > (getCount() - 4))) {
                Log.i(TAG, "instantiateItem: getting blog list - current count -> " + (getCount() - 1) + " Position " + position);
                setNewsList();
            }

            if (itemView != null) {
                viewGroup.addView(itemView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (itemView != null && viewGroup != null) {
                viewGroup.addView(itemView);
            }
        }
        return itemView;
    }

    private void setNewsList() {
        new GetNewsList().execute();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i(TAG, "onPageScrolled: position -> "+  position);
    }

    @Override
    public void onPageSelected(int position) {
        Log.i(TAG, "onPageSelected: position -> " + position );
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class GetNewsList extends AsyncTask<Void, Void, List<News>> {

        private String errorMsg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mContext == null) {
                return;
            }
            if (!UtilityMethods.isInternetConnected(mContext)) {
                if (mTotalNewsCount != 0 && (mTotalNewsCount > (getCount() - 1))) {
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
            if(UtilityMethods.isPackageInstalled(mContext,"com.android.chrome")){
                intent.setPackage("com.android.chrome");
            }
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            UtilityMethods.showSnackBar(mRootView,"Page not found!",Snackbar.LENGTH_LONG);
        }catch (Exception e) {
            e.printStackTrace();
            UtilityMethods.showSnackBar(mRootView,"Page not found!",Snackbar.LENGTH_LONG);
        }
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

    }

    private void sharePostOnTwitter(String title, String contentURL, String shareIntentTitle){
        Intent tweet = new Intent(Intent.ACTION_VIEW);
        String tweetUrl = "https://twitter.com/intent/tweet?text="+ title+ " &url=" + contentURL;

        tweet.setData(Uri.parse(tweetUrl));
        mContext.startActivity(tweet);
    }

    private void shareContent(final News news, final String title , final String contentURL, final String shareIntentTitle){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View viewLayout = mLayoutInflater.inflate(R.layout.bottom_sheet_share_dialog_layout,null);
        TextView titleText = viewLayout.findViewById(R.id.id_share_title_text);
        LinearLayout facebookLayout = viewLayout.findViewById(R.id.id_facebook_layout);
        LinearLayout twitterLayout = viewLayout.findViewById(R.id.id_twitter_layout);
        LinearLayout moreLayout = viewLayout.findViewById(R.id.id_more_layout);
        titleText.setText(shareIntentTitle);
        facebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsOnFacebook(news);
                bottomSheetDialog.dismiss();
            }
        });

        twitterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePostOnTwitter(title,contentURL,shareIntentTitle);
                bottomSheetDialog.dismiss();
            }
        });

        moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String content = "";
                if(!TextUtils.isEmpty(title)){
                    content = title;
                }
                if(!TextUtils.isEmpty(contentURL)){
                    content =  content + "\n" + contentURL;
                }
                intent.putExtra(Intent.EXTRA_TEXT, content);
                if(title != null && !title.isEmpty()) {
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                }else{
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "clicbrics");
                }
                mContext.startActivity(Intent.createChooser(intent, shareIntentTitle));
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(viewLayout);
        bottomSheetDialog.show();

        /*final Intent intent = new Intent(Intent.ACTION_SEND);
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
        final List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(intent, 0);
        List<String> appNames = new ArrayList<String>();
        if (resInfo != null && !resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Log.i(TAG, "shareContent: " + info);
                appNames.add(info.loadLabel(mContext.getPackageManager()).toString());
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(shareIntentTitle);
        builder.setItems(appNames.toArray(new CharSequence[appNames.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ResolveInfo info = resInfo.get(item);
                if (info.activityInfo.packageName.equals("com.facebook.katana")) {
                    shareNewsOnFacebook(news);
                } else if (info.activityInfo.packageName.equals("com.twitter.android")) {
                    sharePostOnTwitter(title,contentURL,shareIntentTitle);
                }else{
                    mContext.startActivity(Intent.createChooser(intent, shareIntentTitle));
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        mContext.startActivity(Intent.createChooser(intent, shareIntentTitle));*/
    }


    BroadcastReceiver internetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "internetReceiver - onReceive: ");
            if (mTotalNewsCount != 0 && (mTotalNewsCount > (getCount() - 11))) {
                setNewsList();
            }
        }
    };
}
