package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.customview.TouchImageView;
import com.clicbrics.consumer.wrapper.ImageObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Alok on 21-03-2017.
 */

public class ImageGalleryPictureViewPagerAdapter extends PagerAdapter {
    private final String TAG = ImageGalleryPictureViewPagerAdapter.class.getSimpleName();

    Context mContext;
    LayoutInflater inflater;
    List<ImageObject> imageObjectList;

    public ImageGalleryPictureViewPagerAdapter(Context context, ArrayList<ImageObject> imageObjectList) {
        this.mContext = context;
        this.imageObjectList = imageObjectList;
    }

    @Override
    public int getCount() {
        return imageObjectList.size();
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
            View itemView = inflater.inflate(R.layout.list_item_picture_view, container,
                    false);

            pictureView = (TouchImageView) itemView.findViewById(R.id.id_picture_view) ;
            videoView = (ImageView) itemView.findViewById(R.id.id_video_view);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.id_imageloading_progressBar);
            pictureView.setMaxZoom(4f);
            if(imageObjectList != null && !imageObjectList.isEmpty()) {
                if (imageObjectList.get(position).isVideo) {
                    mProgressBar.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    final String videoUrl = imageObjectList.get(position).image_url;
                    String videoId = "";
                    if (videoUrl.contains("https://www.youtube.com/embed/")) {
                        videoId = videoUrl.replace("https://www.youtube.com/embed/", "");
                    } else if (videoUrl.contains("http://www.youtube.com/embed/")) {
                        videoId = videoUrl.replace("http://www.youtube.com/embed/", "");
                    } else if (videoUrl.contains("http://www.youtube.com/watch?v=")) {
                        videoId = videoUrl.replace("http://www.youtube.com/watch?v=", "");
                    } else if (videoUrl.contains("https://www.youtube.com/watch?v=")) {
                        videoId = videoUrl.replace("https://www.youtube.com/watch?v=", "");
                    }
                    Picasso.get().load("https://img.youtube.com/vi/" + videoId + "/0.jpg")
                            .placeholder(R.drawable.default_video_thumbnail)
                            .into(pictureView);
                    videoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                            youtubeIntent.putExtra("force_fullscreen", true);
                            mContext.startActivity(youtubeIntent);
                        }
                    });
                } else {
                    String imageURL = imageObjectList.get(position).image_url+"=h1200";
                    videoView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    Log.i(TAG, "instantiateItem: ImageURL -> " + imageURL);
                    pictureView.setEnabled(false);
                    Picasso.get().load(imageURL)
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
                }
                ((ViewPager) container).addView(itemView);
                return itemView;
            }
            ((ViewPager) container).addView(itemView);
            return itemView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ViewPager) container).addView(inflater.inflate(R.layout.list_item_picture_view, container, false));
        return inflater.inflate(R.layout.list_item_picture_view, container, false);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
