package com.clicbrics.consumer.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.ImageGalleryPictureViewPagerAdapter;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.ImageObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ImageGalleryPictureView extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = ImageGalleryPictureView.class.getSimpleName();
    private TextView toolbarTitle;
    private ImageGalleryPictureViewPagerAdapter mPagerAdapter;
    private ArrayList<String> galleryTitles;
    ArrayList<ImageObject> imageObjectList;
    int selectedImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery_picture_view);

        UtilityMethods.setStatusBarColor(this,R.color.profile_bg_color);
        Toolbar toolbar = (Toolbar) findViewById(R.id.image_gallery_picture_view_toolbar);
        toolbarTitle = (TextView) findViewById(R.id.id_gallery_picture_view_text);
        if(getIntent().hasExtra("Title")){
            toolbarTitle.setText(getIntent().getStringExtra("Title"));
        }
        String selectedImageURL = "";
        if(getIntent().hasExtra("GalleryImageURL")){
            selectedImageURL = getIntent().getStringExtra("GalleryImageURL");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        int count=0;
        if(getIntent().hasExtra("GalleryTitleList")){
            imageObjectList = new ArrayList<>();
            galleryTitles = new ArrayList<>();
            ArrayList<String> titles = getIntent().getStringArrayListExtra("GalleryTitleList");
            if(getIntent().hasExtra("URLMap")){
                HashMap<String,ArrayList<ImageObject>> urlMap = (HashMap<String, ArrayList<ImageObject>>) getIntent().getSerializableExtra("URLMap");
                if(titles != null && titles.size() > 0){
                    for(int i=0; i<titles.size(); i++){
                        List<ImageObject> imageObjects = urlMap.get(titles.get(i));
                        if(imageObjects != null && imageObjects.size() > 0){
                            for(int k=0; k<imageObjects.size(); k++){
                                imageObjectList.add(imageObjects.get(k));
                                galleryTitles.add(titles.get(i));
                                String imageURL = imageObjects.get(k).image_url;
                                if(imageURL.equals(selectedImageURL)){
                                    selectedImageIndex = count;
                                }
                                count++;
                                Log.i(TAG, "Title : " + titles.get(i) + ", " + " Image URL : " + imageObjects.get(k).image_url);
                            }
                        }
                    }
                }
            }
            ViewPager mViewPager = (ViewPager) findViewById(R.id.id_image_picture_viewpager);
            setupViewPager(mViewPager, imageObjectList);
            mViewPager.setCurrentItem(selectedImageIndex);
            if(mViewPager != null) {
                mViewPager.setOffscreenPageLimit(2);
                mViewPager.addOnPageChangeListener(this);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager, ArrayList<ImageObject> imageObjectList) {
        mPagerAdapter = new ImageGalleryPictureViewPagerAdapter(this,imageObjectList);
        viewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(galleryTitles.get(position).equalsIgnoreCase("OVERVIEW") || galleryTitles.get(position).equalsIgnoreCase("Images")){
            toolbarTitle.setText("OVERVIEW");
        }else{
            toolbarTitle.setText(galleryTitles.get(position));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
        TrackAnalytics.trackEvent("ImageGalleryZoomViewActivity", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
