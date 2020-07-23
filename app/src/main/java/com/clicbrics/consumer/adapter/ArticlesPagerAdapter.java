package com.clicbrics.consumer.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buy.housing.backend.blogEndPoint.BlogEndPoint;
import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.blogEndPoint.model.BlogListResponse;
import com.buy.housing.backend.blogEndPoint.model.BlogResponse;
import com.buy.housing.backend.blogEndPoint.model.BlogTag;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.BlogDetailsActivity;
import com.clicbrics.consumer.activities.BlogListActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.ThreeTwoImageView;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.api.client.http.HttpHeaders;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 04-01-2018.
 */

public class ArticlesPagerAdapter extends PagerAdapter {

    private static final String TAG = ArticlesPagerAdapter.class.getSimpleName();

    private BlogEndPoint mBlogEndPoint;

    private Activity mContext;
    private LayoutInflater mLayoutInflater;
    private List<Blog> mBlogList;
    private int mTotalBlogCount;
    private View mRootView;
    private boolean isLoadingDataViewEnable = false;
    private Handler mHandler;
    //private ImageButton backImage,nextImage;
    private ViewPager mViewPager;
    private boolean isNotifyDataSetChangeCalled = false;

    public ArticlesPagerAdapter(Activity context,List<Blog> blogList, int totalBlogCount, View rootView,
                                ViewPager articlesPager) {
        try {
            mContext = context;
            mBlogList = blogList;
            mTotalBlogCount = totalBlogCount;
            mRootView = rootView;
            mHandler = new Handler();
            mViewPager = articlesPager;
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

    public Blog getBlog(int position) {
        if(mBlogList  != null && mBlogList.size() > position) {
            return mBlogList.get(position);
        }else{
            return null;
        }
    }

    public int addBlog(Blog blog){
        if(mBlogList != null){
            return addBlog(blog,mBlogList.indexOf(blog));
        }else{
            return -1;
        }
    }

    public int addBlog(Blog blog, int position){
        if(mBlogList != null){
            mBlogList.add(position, blog);
        }
        return position;
    }

    public int removeBlog(ViewPager viewPager, Blog blog){
        if(mBlogList != null) {
            return removeBlog(viewPager, mBlogList.indexOf(blog));
        }else{
            return -1;
        }
    }

    public int removeBlog(ViewPager pager, int position){
        try {
            if(pager != null) {
                pager.setAdapter(null);
            }
            if(mBlogList != null && mBlogList.size() > position) {
                mBlogList.remove(position);
            }
            if(pager != null){
                pager.setAdapter(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }

    @Override
    public int getCount() {
        if(isNotifyDataSetChangeCalled){
            isNotifyDataSetChangeCalled = false;
            notifyDataSetChanged();
        }
        if(mBlogList != null){
            return mBlogList.size()+1;
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
        mBlogEndPoint = EndPointBuilder.getBlogEndPoint();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position){
        View itemView = null;
        try {
            if ((mTotalBlogCount == (getCount() - 1)) && (mTotalBlogCount == position)) {
                if (isLoadingDataViewEnable) {
                    itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, viewGroup, false);
                    TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                    loadingTextMsg.setText("Loading Articles...");
                } else {
                    itemView = mLayoutInflater.inflate(R.layout.content_all_blog_read_layout, viewGroup, false);
                    TextView allListReadMsg  = itemView.findViewById(R.id.id_all_list_read_msg);
                    allListReadMsg.setText(mContext.getString(R.string.read_all_blog_msg));
                }
            } else if (isLoadingDataViewEnable && ((getCount() - 1) == position)) {
                itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, viewGroup, false);
                TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                loadingTextMsg.setText("Loading Articles...");
            } else{
                if (mTotalBlogCount > (getCount() - 1) && ((getCount() - 1) == position)) {
                    itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, viewGroup, false);
                    TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                    loadingTextMsg.setText("Loading Articles...");
                }else{
                    itemView = mLayoutInflater.inflate(R.layout.content_blog_details, viewGroup, false);
                    final TextView blogAuthor = (TextView) itemView.findViewById(R.id.id_blog_author);
                    TextView blogDate = (TextView) itemView.findViewById(R.id.id_blog_date);
                    TextView blogTitle = (TextView) itemView.findViewById(R.id.id_blog_title);
                    WebView blogContent = (WebView) itemView.findViewById(R.id.id_blog_summery);
                    final ThreeTwoImageView blogImage = (ThreeTwoImageView) itemView.findViewById(R.id.id_blog_image);

                    final FloatingActionButton shareButton = (FloatingActionButton) itemView.findViewById(R.id.id_share_menu);
                    final ImageView collapsedShareButton = (ImageView) itemView.findViewById(R.id.share_collapsed);

                    final TextView collapsed_title = (TextView) itemView.findViewById(R.id.toolbar_collapsed_title);
                    AppBarLayout appBarLayout = (AppBarLayout) itemView.findViewById(R.id.id_appbar);
                    /*backImage = (ImageButton) itemView.findViewById(R.id.id_back_image);
                    nextImage = (ImageButton) itemView.findViewById(R.id.id_next_image);
                    if(position == 0) {
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
                    ((BlogListActivity) mContext).setSupportActionBar(toolbar);
                    ((BlogListActivity)mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    ((BlogListActivity)mContext).getSupportActionBar().setDisplayShowHomeEnabled(true);
                    ((BlogListActivity)mContext).getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);*/

                    final Blog blog = mBlogList.get(position);
                    if (blog != null) {
                        final View finalItemView = itemView;
                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;

                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                Log.d(TAG, "verticalOffset->" + verticalOffset);

                                if (verticalOffset == 0) {
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
                                    if (!TextUtils.isEmpty(blog.getTitle())) {
                                        collapsed_title.setText(blog.getTitle());
                                    }else{
                                        collapsed_title.setText("Article");
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

                        if (!TextUtils.isEmpty(blog.getSTitleImage())) {
                            String blogImageURL = mBlogList.get(position).getSTitleImage()+"=h300";
                            Picasso.get().load(blogImageURL)
                                    .placeholder(R.drawable.placeholder)
                                    .into(blogImage);
                            blogImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }else{
                            blogImage.setImageResource(R.drawable.placeholder);
                        }
                        /*if (!TextUtils.isEmpty(blog.getCreateByName())) {
                            blogAuthor.setVisibility(View.VISIBLE);
                            blogAuthor.setText(blog.getCreateByName());
                        }else{
                            blogAuthor.setVisibility(View.GONE);
                        }*/
                        blogAuthor.setText("Team Clicbrics");
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
                        if(!TextUtils.isEmpty(blog.getContent())) {
                            String data = blog.getContent();

                            blogContent.loadDataWithBaseURL(null, formatWebviewContentImage(data), "text/html", "UTF-8", null);
                            blogContent.getSettings().setJavaScriptEnabled(true);
                            blogContent.getSettings().setDomStorageEnabled(true);

                            final View finalItemView1 = itemView;
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
                                        view.getSettings().setJavaScriptEnabled(true);
                                        view.getSettings().setDomStorageEnabled(true);
                                        if (url.contains("clicbrics") && url.contains("/project/")) {
                                            String projectIdStr = UtilityMethods.getProjectIdFromURL(url);
                                            String projectId = projectIdStr.substring(projectIdStr.lastIndexOf('-') + 1, projectIdStr.length());
                                            intent = new Intent(mContext, ProjectDetailsScreen.class);
                                            intent.putExtra("ISDirectCall", true);
                                            intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, Long.valueOf(projectId));
                                            mContext.startActivity(intent);
                                        }else if(url.contains("clicbrics") && url.contains("/city/")){
                                            showAllCityProperty(url);
                                        }/*else if(url.toLowerCase().startsWith("https://www.clicbrics.com/news-and-articles/post/")){
                                            showOtherBlogDetails(url.replace("https://www.clicbrics.com/news-and-articles/post/",""));
                                        } */else{
                                            if(!UtilityMethods.isInternetConnected(mContext)){
                                                UtilityMethods.showErrorSnackBar(mRootView,mContext.getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                                            }else {
                                                if(UtilityMethods.isPackageInstalled(mContext,"com.android.chrome")){
                                                    intent.setPackage("com.android.chrome");
                                                }
                                                mContext.startActivity(intent);
                                            }
                                        }
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                        UtilityMethods.showSnackBar(mRootView,"Page not found!",Snackbar.LENGTH_LONG);
                                    }catch (Exception e) {
                                        mContext.startActivity(intent);
                                        e.printStackTrace();
                                    }
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
                                    TextView textView = (TextView) finalItemView1.findViewById(R.id.id_tag_text);
                                    List<BlogTag> blogTags = blog.getBlogTags();
                                    if(blogTags != null && !blogTags.isEmpty()) {
                                        textView.setVisibility(View.VISIBLE);
                                        setTagLayout(blogTags,finalItemView);
                                    }
                                }
                            });
                        }else{
                            blogContent.setVisibility(View.GONE);
                        }

                        shareButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i(TAG, "onClick: More button");
                                String contentURL = "";
                                new EventAnalyticsHelper().ItemClickEvent(mContext, Constants.AnaylticsClassName.BlogListScreen,
                                        blog.get(position), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.shareclick.toString());
                                if(!TextUtils.isEmpty(blog.getSearchTitle())){
                                    contentURL = BuildConfigConstants.WEB_URL + "post/" + blog.getSearchTitle();
                                }
                                shareContent(blog,blog.getTitle(),contentURL,"Share Article");
                            }
                        });
                        collapsedShareButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i(TAG, "onClick: More button");
                                String contentURL = "";
                                if(!TextUtils.isEmpty(blog.getSearchTitle())){
                                    contentURL = BuildConfigConstants.WEB_URL + "post/" + blog.getSearchTitle();
                                }
                                shareContent(blog,blog.getTitle(),contentURL,"Share Article");
                            }
                        });
                        blogImage.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                        blogAuthor.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                        blogDate.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                        blogTitle.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return true;
                            }
                        });
                        blogContent.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                ((BlogListActivity) mContext).showButtonOnTouchView();
                                return false;
                            }
                        });
                    }
                }
            }
            if (mTotalBlogCount != 0 && (mTotalBlogCount > (getCount() - 1)) && (position > (getCount() - 4))) {
                Log.i(TAG, "instantiateItem: getting blog list - current count -> " + (getCount() - 1) + " Position " + position);
                setBlogList();
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

    private void setBlogList() {
        new GetBlogList().execute();
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

    private void setTagLayout(List<BlogTag> blogTagList, View itemView){
        DisplayMetrics dm = new DisplayMetrics();
        ((BlogListActivity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int deviceWidth = dm.widthPixels;
        LinearLayout verticalLayout = (LinearLayout) itemView.findViewById(R.id.id_tag_vertical_layout);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        List<LinearLayout> linearLayoutList = new ArrayList<>();
        int rowWidth = UtilityMethods.dpToPx(15);
        int rowNo = 0;
        for(int i=0; i<blogTagList.size(); i++){
            BlogTag blogTag = blogTagList.get(i);
            if(blogTag != null){
                LinearLayout horizontalLayout = new LinearLayout(verticalLayout.getContext());
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayoutList.add(horizontalLayout);
                final TextView tagNameText = new TextView(mContext);
                tagNameText.setText(blogTag.getName());
                UtilityMethods.setDrawableBackground(mContext,tagNameText,R.drawable.tag_background);
                UtilityMethods.setTextViewColor(mContext,tagNameText,R.color.text_grey);
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

    private void shareContent(final Blog blog, final String title , final String contentURL, final String shareIntentTitle){
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
                shareBlogOnFacebook(blog);
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
        /*Intent intent = new Intent(Intent.ACTION_SEND);
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
        mContext.startActivity(Intent.createChooser(intent, shareIntentTitle));*/
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

    private void sharePostOnTwitter(String title, String contentURL, String shareIntentTitle){
        Intent tweet = new Intent(Intent.ACTION_VIEW);
        String tweetUrl = "https://twitter.com/intent/tweet?text="+ title+ " &url=" + contentURL;

        tweet.setData(Uri.parse(tweetUrl));
        mContext.startActivity(tweet);
    }

    private void showAllCityProperty(String url){
        try {
            if(!UtilityMethods.isInternetConnected(mContext)){
                UtilityMethods.showErrorSnackBar(mRootView,mContext.getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
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
            Intent intent = new Intent(mContext, HomeScreen.class);
            intent.putExtra(Constants.IntentKeyConstants.SHOW_PROPERTY_BY_CITY,true);
            if(cityId != -1 & cityId != 0){
                intent.putExtra("city_id",cityId);
                UtilityMethods.saveLongInPref(mContext,Constants.AppConstants.SAVED_CITY_ID,cityId);
            }
            if(!TextUtils.isEmpty(cityName)){
                UtilityMethods.saveStringInPref(mContext,Constants.AppConstants.SAVED_CITY,cityName);
                intent.putExtra("cityName",cityName);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ((BlogListActivity)mContext).finish();
            mContext.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOtherBlogDetails(final String blogName){
        if(!UtilityMethods.isInternetConnected(mContext)){
            UtilityMethods.showErrorSnackBar(mRootView,mContext.getResources().getString(R.string.no_internet_connection),Snackbar.LENGTH_LONG);
            return;
        }
        ((BlogListActivity)mContext).showProgressBar();
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
                                    ((BlogListActivity)mContext).dismissProgressBar();
                                    Blog blog = blogResponse.getBlog();
                                    if(blog != null) {
                                        new EventAnalyticsHelper().ItemClickEvent(mContext, Constants.AnaylticsClassName.BlogListScreen,
                                                blog, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.BlogClick.toString());
                                        Intent intent = new Intent(mContext, BlogDetailsActivity.class);
                                        if (!TextUtils.isEmpty(blog.getTitle())) {
                                            intent.putExtra("BlogTitle", blog.getTitle());
                                        }
                                        /*if (!TextUtils.isEmpty(blog.getCreateByName())) {
                                            intent.putExtra("BlogAuthor", blog.getCreateByName());
                                        }*/
                                        intent.putExtra("BlogAuthor","Team Clicbrics");
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
                                        mContext.startActivity(intent);
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
                            ((BlogListActivity)mContext).dismissProgressBar();
                            UtilityMethods.showSnackBar(mRootView,errorMsg,Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    private String formatWebviewContentImage(String data){
        //return "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + data;
        return "<style>img{display: inline;height: auto;max-width: 100%;}@font-face {font-family: FiraSans-Light;src: url('file:///android_asset/fonts/FiraSans-Light.ttf');</style>" + data;
    }

    BroadcastReceiver internetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "internetReceiver - onReceive: ");
            if (mTotalBlogCount != 0 && (mTotalBlogCount > (getCount() - 11))) {
                setBlogList();
            }
        }
    };
}
