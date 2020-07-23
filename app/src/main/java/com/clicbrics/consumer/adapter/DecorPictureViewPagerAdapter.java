package com.clicbrics.consumer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.buy.housing.backend.decorEndPoint.model.DecorItem;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.TouchImageView;
import com.clicbrics.consumer.utils.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Alok on 21-03-2017.
 */

public class DecorPictureViewPagerAdapter extends PagerAdapter {
    private final String TAG = "DecorPictureViewAdapter";

    Activity mContext;
    LayoutInflater inflater;
    List<DecorItem> decorItemList;

    public DecorPictureViewPagerAdapter(Activity context, ArrayList<DecorItem> decorItemList) {
        this.mContext = context;
        this.decorItemList = decorItemList;
    }

    @Override
    public int getCount() {
        if(decorItemList != null){
            return decorItemList.size();
        }else{
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        // Declare Variables
        try {
            final TouchImageView pictureView;
            final ImageView videoView;
            final ProgressBar mProgressBar;

            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.list_item_decor_picture_view, container,
                    false);

            pictureView = (TouchImageView) itemView.findViewById(R.id.id_decor_picture_view) ;
            videoView = (ImageView) itemView.findViewById(R.id.id_video_view);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.id_imageloading_progressBar);
            pictureView.setMaxZoom(4f);
            if(decorItemList != null && !decorItemList.isEmpty()) {
                DecorItem decorItem = decorItemList.get(position);
                if(decorItem != null){
                    if(!TextUtils.isEmpty(decorItem.getUrl())){
                        if(decorItem.getType() != null && decorItem.getType().equalsIgnoreCase("Image")) {
                            String imageURL = decorItem.getUrl();
                            videoView.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.VISIBLE);
                            Log.i(TAG, "instantiateItem: ImageURL -> " + imageURL);
                            pictureView.setEnabled(false);
                            Picasso.get().load(imageURL)
                                    .resize(0,1080)
                                    .placeholder(R.drawable.placeholder)
                                    .into(pictureView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            pictureView.setEnabled(true);
                                            mProgressBar.setVisibility(View.GONE);
                                            Log.i(TAG, "Picasso onSuccess: ImageLoaded ");
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            pictureView.setEnabled(true);
                                            mProgressBar.setVisibility(View.GONE);
                                            Log.i(TAG, "Picasso onError: error in image loading ");
                                        }
                                    });
                        }else if(decorItem.getType() != null && decorItem.getType().equalsIgnoreCase("Video")) {
                            videoView.setVisibility(View.VISIBLE);
                            final String url = decorItem.getUrl().trim();
                            String videoId = "";
                            if(url.contains("https://www.youtube.com/embed/")){
                                videoId = url.replace("https://www.youtube.com/embed/", "");
                            }else if(url.contains("http://www.youtube.com/embed/")){
                                videoId = url.replace("http://www.youtube.com/embed/", "");
                            }else if(url.contains("http://www.youtube.com/watch?v=")){
                                videoId = url.replace("http://www.youtube.com/watch?v=", "");
                            }else if(url.contains("https://www.youtube.com/watch?v=")){
                                videoId = url.replace("https://www.youtube.com/watch?v=", "");
                            }
                            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                            if(!TextUtils.isEmpty(thumbnailUrl)){
                                pictureView.setEnabled(false);
                                Picasso.get().load(thumbnailUrl)
                                        .resize(0,1080)
                                        .placeholder(R.drawable.placeholder)
                                        .into(pictureView, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                pictureView.setEnabled(true);
                                                mProgressBar.setVisibility(View.GONE);
                                                Log.i(TAG, "Picasso onSuccess: ImageLoaded ");
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                pictureView.setEnabled(true);
                                                mProgressBar.setVisibility(View.GONE);
                                                Log.i(TAG, "Picasso onError: error in image loading ");
                                            }
                                        });
                            }else{
                                pictureView.setImageResource(R.drawable.default_video_thumbnail);
                            }
                            videoView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new EventAnalyticsHelper().ItemClickEvent(mContext, Constants.AnaylticsClassName.ImageGalleryZoomViewActivity,
                                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.videoPlay.toString());
                                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    youtubeIntent.putExtra("force_fullscreen", true);
                                    mContext.startActivity(youtubeIntent);
                                }
                            });
                        }
                    }
                    ((ViewPager) container).addView(itemView);
                    return itemView;
                }
            }
            ((ViewPager) container).addView(itemView);
            return itemView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ViewPager) container).addView(inflater.inflate(R.layout.list_item_decor_picture_view, container, false));
        return inflater.inflate(R.layout.list_item_decor_picture_view, container, false);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
