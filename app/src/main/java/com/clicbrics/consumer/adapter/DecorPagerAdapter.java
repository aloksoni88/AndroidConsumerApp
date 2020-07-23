package com.clicbrics.consumer.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buy.housing.backend.decorEndPoint.DecorEndPoint;
import com.buy.housing.backend.decorEndPoint.model.Decor;
import com.buy.housing.backend.decorEndPoint.model.DecorItem;
import com.buy.housing.backend.decorEndPoint.model.DecorListResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.DecorDetailViewActivity;
import com.clicbrics.consumer.activities.HomeDecorPictureView;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.ThreeTwoImageView;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.SimpleDividerItemDecoration;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpHeaders;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alok on 02-01-2018.
 */

public class DecorPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener{

    private static final String TAG = DecorPagerAdapter.class.getSimpleName();

    private DecorEndPoint mDecorEndPoint;

    private Activity mContext;
    private LayoutInflater mLayoutInflater;
    private List<Decor> mDecorList;
    private int mTotalDecorCount;
    private View mRootView;
    private boolean isLoadingDataViewEnable = false;
    private String BASE_URL = "https://www.clicbrics.com/home-decor-ideas/post/";
    private int mWidth,mHeight;

    public DecorPagerAdapter(Activity context, List<Decor> decorList, int totalDecorCount, View rootView, ViewPager decorListPager) {
        try {
            mContext = context;
            mDecorList = decorList;
            mTotalDecorCount = totalDecorCount;
            mRootView = rootView;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            buildWebService();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.clicbrics.internetconnection");
            context.registerReceiver(internetReceiver, intentFilter);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            mWidth = (int)(displayMetrics.widthPixels*(2/3.0));
            mHeight = (int)(mWidth*(2/3.0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        if(mDecorList != null){
            return mDecorList.size()+1;
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
        mDecorEndPoint = EndPointBuilder.getDecorEndPoint();
    }


    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position){
        View itemView = null;
        try {
            Log.i(TAG, "instantiateItem: Postition " + position);
            if ((mTotalDecorCount == (getCount() - 1)) && (mTotalDecorCount == position)) {
                if (isLoadingDataViewEnable) {
                    itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, viewGroup, false);
                    TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                    loadingTextMsg.setText("Loading home decor ideas...");
                } else {
                    itemView = mLayoutInflater.inflate(R.layout.content_all_blog_read_layout, viewGroup, false);
                    TextView allListReadMsg  = itemView.findViewById(R.id.id_all_list_read_msg);
                    allListReadMsg.setText(mContext.getString(R.string.read_all_decor_msg));
                }
            } else if (isLoadingDataViewEnable && ((getCount() - 1) == position)) {
                itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, viewGroup, false);
                TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                loadingTextMsg.setText("Loading home decor ideas...");
            } else{
                if (mTotalDecorCount > (getCount() - 1) && ((getCount() - 1) == position)) {
                    itemView = mLayoutInflater.inflate(R.layout.content_article_loading_layout, viewGroup, false);
                    TextView loadingTextMsg = itemView.findViewById(R.id.id_loading_txt_msg);
                    loadingTextMsg.setText("Loading home decor ideas...");
                }else{
                    itemView = mLayoutInflater.inflate(R.layout.content_decor_pager_list, viewGroup, false);
                    final ThreeTwoImageView decorImage = (ThreeTwoImageView) itemView.findViewById(R.id.id_decor_image);
                    final ThreeTwoImageView decorBlurImage = (ThreeTwoImageView) itemView.findViewById(R.id.id_decor_blurr_image);
                    /*final TextView decorTitle = (TextView) itemView.findViewById(R.id.id_news_title);
                    final TextView decorDesc = (TextView) itemView.findViewById(R.id.id_news_summery);*/
                    final RecyclerView decorItemListView = itemView.findViewById(R.id.decor_item_list);
                    final FloatingActionButton shareButton = (FloatingActionButton) itemView.findViewById(R.id.id_share_menu);
                    final ImageView collapsedShareButton = (ImageView) itemView.findViewById(R.id.share_collapsed);
                    final TextView collapsed_title = (TextView) itemView.findViewById(R.id.toolbar_collapsed_title);
                    AppBarLayout appBarLayout = (AppBarLayout) itemView.findViewById(R.id.id_appbar);
                    decorItemListView.setLayoutManager(new LinearLayoutManager(mContext));

                    final Decor decor = mDecorList.get(position);
                    if (decor != null) {
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
                                    ((DecorDetailViewActivity)mContext).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
                                    finalItemView.findViewById(R.id.toolbar_collapsed_layout).setBackground(ContextCompat.getDrawable(mContext,R.color.light_white));
                                    finalItemView.findViewById(R.id.toolbar_collapsed_layout).setVisibility(View.VISIBLE);
                                    collapsed_title.setVisibility(View.VISIBLE);
                                    collapsedShareButton.setVisibility(View.VISIBLE);
                                    if(decor != null && decor.getTitleSection() != null
                                            && !TextUtils.isEmpty(decor.getTitleSection().getTitle())){
                                        collapsed_title.setText(decor.getTitleSection().getTitle());
                                    }else{
                                        collapsed_title.setText("Home Decor Ideas");
                                    }
                                    isShow = true;
                                } else if (isShow) {
                                    ((DecorDetailViewActivity)mContext).getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_with_shadow);
                                    finalItemView.findViewById(R.id.toolbar_collapsed_layout).setBackground(null);
                                    finalItemView.findViewById(R.id.toolbar_collapsed_layout).setVisibility(View.INVISIBLE);
                                    collapsed_title.setVisibility(View.GONE);
                                    collapsedShareButton.setVisibility(View.GONE);
                                    isShow = false;
                                }
                            }
                        });
                        if(decor.getDecorItems() != null && !decor.getDecorItems().isEmpty()){
                            final List<DecorItem> decorItemList = new ArrayList<>();
                            decorItemList.addAll(decor.getDecorItems());
                            if(decor.getTitleSection() != null){
                                DecorItem decorItem = decor.getTitleSection();
                                if(decorItem != null) {
                                    decorItemList.add(0, decorItem);
                                }
                            }
                            DecorItemListAdapter adapter = new DecorItemListAdapter(mContext,decorItemList,mRootView);
                            decorItemListView.setAdapter(adapter);
                            decorImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(mContext,HomeDecorPictureView.class);
                                    intent.putExtra("Position",0);
                                    Gson gson = new Gson();
                                    String decorItems = gson.toJson(decorItemList);
                                    intent.putExtra("DecorItemList",decorItems);
                                    mContext.startActivity(intent);
                                }
                            });
                        }
                        if(decor.getTitleSection() != null){
                            final DecorItem decorItem = decor.getTitleSection();
                            if (decorItem != null) {
                                if(!TextUtils.isEmpty(decorItem.getUrl())){
                                    decorBlurImage.setVisibility(View.GONE);
                                    String url = decorItem.getUrl().trim();
                                    Picasso.get().load(url)
                                            .resize(0,mHeight)
                                            .placeholder(R.drawable.placeholder)
                                            .into(decorImage);
                                }else{
                                    decorImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder));
                                }
                            }
                            shareButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.i(TAG, "onClick: More button");
                                    String contentURL = "";
                                    new EventAnalyticsHelper().ItemClickEvent(mContext, Constants.AnaylticsClassName.HomeDecorDetailScreen,
                                            decor.get(position), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.shareclick.toString());
                                    if(!TextUtils.isEmpty(decor.getSearchTitle())) {
                                        contentURL = BASE_URL + decor.getSearchTitle();
                                    }

                                    shareContent(decorItem,contentURL,"Share Ideas");
                                }
                            });
                            collapsedShareButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.i(TAG, "onClick: More button");
                                    String contentURL = "";
                                    if(!TextUtils.isEmpty(decor.getSearchTitle())){
                                        contentURL = BASE_URL + decor.getSearchTitle();
                                    }
                                    shareContent(decorItem,contentURL,"Share Ideas");
                                }
                            });
                            decorBlurImage.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    ((DecorDetailViewActivity) mContext).showButtonOnTouchView();
                                    return true;
                                }
                            });
                            
                            /*if (!TextUtils.isEmpty(decorItem.getTitle())) {
                                decorTitle.setVisibility(View.VISIBLE);
                                decorTitle.setText(decorItem.getTitle());
                            }else{
                                decorTitle.setVisibility(View.GONE);
                            }
                            if (!TextUtils.isEmpty(decorItem.getDetail())) {
                                decorDesc.setVisibility(View.VISIBLE);
                                decorDesc.setText(decorItem.getDetail());
                            }else{
                                decorDesc.setVisibility(View.GONE);
                            }
                            decorImage.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    ((DecorDetailViewActivity) mContext).showButtonOnTouchView();
                                    return true;
                                }
                            });
                            decorBlurImage.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    ((DecorDetailViewActivity) mContext).showButtonOnTouchView();
                                    return true;
                                }
                            });
                            decorTitle.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    ((DecorDetailViewActivity) mContext).showButtonOnTouchView();
                                    return true;
                                }
                            });
                            decorDesc.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    ((DecorDetailViewActivity) mContext).showButtonOnTouchView();
                                    return true;
                                }
                            });*/
                        }

                    }
                }
            }
            if (mTotalDecorCount != 0 && (mTotalDecorCount > (getCount() - 1)) && (position > (getCount() - 4))) {
                Log.i(TAG, "instantiateItem: getting blog list - current count -> " + (getCount() - 1) + " Position " + position);
                setDecorList();
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

    private void setDecorList() {
        new GetDecorList().execute();
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

    private class GetDecorList extends AsyncTask<Void, Void, List<Decor>> {

        private String errorMsg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mContext == null) {
                return;
            }
            if (!UtilityMethods.isInternetConnected(mContext)) {
                if (mTotalDecorCount != 0 && (mTotalDecorCount > (getCount() - 1))) {
                    isLoadingDataViewEnable = true;
                }
                UtilityMethods.showErrorSnackBar(mRootView, mContext.getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                cancel(true);
            } else {
                isLoadingDataViewEnable = false;
            }
        }

        @Override
        protected List<Decor> doInBackground(Void... objects) {
            try {
                int currentOffsetCount = getCount() - 1;
                HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                DecorListResponse decorListResponse = mDecorEndPoint.getDecorList(currentOffsetCount, 10)
                        .setRequestHeaders(httpHeaders).execute();
                if (decorListResponse != null && decorListResponse.getStatus()) {
                    final List<Decor> decorList = decorListResponse.getDecors();
                    return decorList;
                } else {
                    if (decorListResponse != null && !TextUtils.isEmpty(decorListResponse.getErrorMessage())) {
                        errorMsg = decorListResponse.getErrorMessage();
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
        protected void onPostExecute(List<Decor> decorList) {
            super.onPostExecute(decorList);
            if (decorList != null && !decorList.isEmpty()) {
                if (mDecorList == null) {
                    mDecorList = new ArrayList<>();
                }
                List<Decor> newDecorList = new ArrayList<>();
                for (int i = 0; i < decorList.size(); i++) {
                    Decor decor = decorList.get(i);
                    boolean isContain = false;
                    for (int k = 0; k < mDecorList.size(); k++) {
                        Decor decor1 = mDecorList.get(k);
                        if(decor1 != null && decor1.getTitleSection() != null
                                && decor != null && decor.getTitleSection() != null){
                            if(decor1.getTitleSection().containsValue(decor.getTitleSection().getTitle())){
                                isContain = true;
                                break;
                            }
                        }
                    }
                    if (!isContain) {
                        newDecorList.add(decor);
                    }
                }
                mDecorList.addAll(newDecorList);
                notifyDataSetChanged();
            }
        }
    }

    private void showNewsDetails(DecorItem decorItem){
        if (mContext == null) {
            return;
        }
        if (!UtilityMethods.isInternetConnected(mContext)) {
            UtilityMethods.showErrorSnackBar(mRootView, mContext.getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            return;
        }
        //String data = decorItem.getClickURL();
        String data = "";
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
    }

    private void shareIdeaOnFacebook(DecorItem decor,String contentURL){
        String title = "",description="",imageURL = "";
        if(!TextUtils.isEmpty(decor.getTitle())){
            title = decor.getTitle();
        }
        if(!TextUtils.isEmpty(decor.getDetail())){
            description = decor.getDetail();
        }
        if(!TextUtils.isEmpty(decor.getUrl())){
            imageURL = decor.getUrl();
        }
        //CallbackManager callbackManager;
        if(!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(mContext);
        }
        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentTitle(title)
                .setContentDescription(description)
                .setContentUrl(Uri.parse(contentURL))
                .setImageUrl(Uri.parse(imageURL.trim()))
                .build();
        ShareDialog.show((DecorDetailViewActivity)mContext,shareLinkContent);

    }

    private void shareIdeaOnTwitter(String title, String contentURL, String shareIntentTitle){
        Intent tweet = new Intent(Intent.ACTION_VIEW);
        String tweetUrl = "https://twitter.com/intent/tweet?text="+ title+ " &url=" + contentURL;

        tweet.setData(Uri.parse(tweetUrl));
        mContext.startActivity(tweet);
    }

    private void shareContent(final DecorItem decorItem, final String contentURL, final String shareIntentTitle){

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
                shareIdeaOnFacebook(decorItem,contentURL);
                bottomSheetDialog.dismiss();
            }
        });

        twitterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "";
                if(decorItem != null && decorItem.getTitle() != null){
                    title = decorItem.getTitle();
                }
                shareIdeaOnTwitter(title,contentURL,shareIntentTitle);
                bottomSheetDialog.dismiss();
            }
        });

        moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String content = "";
                String title = "";
                if(decorItem != null && decorItem.getTitle() != null){
                    title = decorItem.getTitle();
                }
                if(!TextUtils.isEmpty(title)){
                    content = title;
                }
                if(!TextUtils.isEmpty(contentURL)){
                    content =  content + "\n" + contentURL;
                }
                intent.putExtra(Intent.EXTRA_TEXT, content);
                if(title.isEmpty()){
                    intent.putExtra(Intent.EXTRA_SUBJECT, "clicbrics");
                }else{
                    intent.putExtra(Intent.EXTRA_SUBJECT, title);
                }
                mContext.startActivity(Intent.createChooser(intent, shareIntentTitle));
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(viewLayout);
        bottomSheetDialog.show();
    }


    BroadcastReceiver internetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "internetReceiver - onReceive: ");
            if (mTotalDecorCount != 0 && (mTotalDecorCount > (getCount() - 11))) {
                setDecorList();
            }
        }
    };
}
