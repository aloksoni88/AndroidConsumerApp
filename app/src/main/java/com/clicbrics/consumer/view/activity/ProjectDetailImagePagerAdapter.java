package com.clicbrics.consumer.view.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.ImageGalleryActivity;
import com.clicbrics.consumer.customview.TwoThreeImageView;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.adapter.ImageBindingAdapter;
import com.clicbrics.consumer.wrapper.ImageObject;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by Alok on 14-08-2018.
 */
public class ProjectDetailImagePagerAdapter extends PagerAdapter {
    private final String TAG = "ImagePagerAdapter";

    private Context mContext;
    private LayoutInflater inflater;
    private List<String> imageList;
    private OnImageTouchListener mListener;
    private long touchTime;
    private final long CLICK_INTERVAL = 300;
    private LinkedHashMap<String, ArrayList<ImageObject>> galleryImageList = new LinkedHashMap<String, ArrayList<ImageObject>>();
    private long projectId;

    public interface OnImageTouchListener {
        void onActionDown();

        void onActionUp();

        void onActionCancel();
    }

    public ProjectDetailImagePagerAdapter(Context context, List<String> imageList) {
        this.mContext = context;
        this.imageList = imageList;
        mListener = (ProjectDetailsScreen) context;
    }

    public void setGalleryImageList(LinkedHashMap<String, ArrayList<ImageObject>> galleryImageList,long projectId){
        this.galleryImageList = galleryImageList;
        this.projectId = projectId;
    }

    @Override
    public int getCount() {
        if (imageList != null) {
            return imageList.size();
        } else {
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


            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.item_image_pager_adapter, container,
                    false);

            final TwoThreeImageView coverImage = itemView.findViewById(R.id.cover_image);

            if (imageList != null && !imageList.isEmpty()) {

                String imageURL = imageList.get(position) + ImageBindingAdapter.COVER_IMAGE_SIZE;
                Picasso.get().load(imageURL)
                        .config(Bitmap.Config.ARGB_8888)
                        .placeholder(R.drawable.project_detail_placeholder)
                        .into(coverImage);

                /*final AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.image_zoom_animation);
                animator.setTarget(coverImage);
                animator.start();*/

                final ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(coverImage, "scaleX", 2.0f);
                objectAnimator1.setDuration(20000);
                objectAnimator1.start();

                final ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(coverImage, "scaleY", 2.0f);
                objectAnimator2.setDuration(20000);
                objectAnimator2.start();

                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(objectAnimator1);
                animatorSet.play(objectAnimator2);
                animatorSet.start();

                coverImage.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                /*objectAnimator1.pause();
                                objectAnimator2.pause();*/
                                animatorSet.pause();
                                if (mListener != null) {
                                    mListener.onActionDown();
                                }
                                touchTime = System.currentTimeMillis();
                                Log.i(TAG, "onTouch: Touch Down time " + touchTime);
                                return true;
                            }
                            case MotionEvent.ACTION_UP:
                                /*objectAnimator1.resume();
                                objectAnimator2.resume();*/
                                animatorSet.resume();
                                if (mListener != null) {
                                    mListener.onActionUp();
                                }
                                long touchUptime = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - touchTime);
                                Log.i(TAG, "onTouch: Touch UP time " + touchUptime);
                                if(touchUptime < CLICK_INTERVAL){
                                    Log.i(TAG, "onTouch: Launch the gallery mode");
                                    if(!UtilityMethods.isInternetConnected(mContext)){
                                        UtilityMethods.showErrorSnackbarOnTop((ProjectDetailsScreen)mContext);
                                        return true;
                                    }
                                    if(!galleryImageList.isEmpty()) {
                                        Gson gson = new Gson();
                                        String list = gson.toJson(galleryImageList);

                                        Intent intent = new Intent(mContext, ImageGalleryActivity.class);
                                        intent.putExtra("Images", list);
                                        intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, projectId);
                                        mContext.startActivity(intent);
                                    }else{
                                        UtilityMethods.showSnackbarOnTop((ProjectDetailsScreen)mContext,
                                                "Error","Image list empty",false,Constants.AppConstants.ALERTOR_LENGTH_LONG);
                                    }
                                }
                                return true;
                            case MotionEvent.ACTION_CANCEL:
                                /*objectAnimator1.resume();
                                objectAnimator2.resume();*/
                                animatorSet.resume();
                                if (mListener != null) {
                                    mListener.onActionCancel();
                                }
                                return true;
                        }
                        return false;
                    }
                });

            }
            container.addView(itemView);
            return itemView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(inflater.inflate(R.layout.list_item_picture_view, container, false));
        return inflater.inflate(R.layout.list_item_picture_view, container, false);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}