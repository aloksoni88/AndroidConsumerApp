package com.clicbrics.consumer.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Alok on 19-09-2017.
 */

public class VerticalViewPager extends ViewPager{
    private static final String TAG = VerticalViewPager.class.getSimpleName();

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // The majority of the magic happens here
        setPageTransformer(true, new VerticalPageTransformer());
        // The easiest way to get rid of the overscroll drawing that happens on the left and right
        setOverScrollMode(OVER_SCROLL_ALWAYS);
    }



    private class VerticalPageTransformer implements ViewPager.PageTransformer {
        //private static final float MIN_SCALE = 0.75f;
        private static final float MIN_SCALE = 0.95f;
        @Override
        public void transformPage(View view, float position) {
            //Log.i(TAG, "transformPage: position -> "+ position);
            try {
                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);

                }  else if (position < 0) { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    view.setAlpha(1);
                    // Counteract the default slide transition
                    view.setTranslationX(view.getWidth() * -position);

                    //set Y position to swipe in from top
                    float yPosition = position * view.getHeight();
                    view.setTranslationY(yPosition);
                    view.setScaleX(1);
                    view.setScaleY(1);

                } else if (position <= 1) { // [0,1]
                    view.setAlpha(1);

                    // Counteract the default slide transition
                    view.setTranslationX(view.getWidth() * -position);


                    // Scale the page down (between MIN_SCALE and 1)
                    float scaleFactor = MIN_SCALE
                            + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        boolean intercepted = false;
        try {
            intercepted = super.onInterceptTouchEvent(swapXY(ev));
            Log.i(TAG, "onInterceptTouchEvent: intercepted " +intercepted + " event " + ev);
            swapXY(ev); // return touch coordinates to original reference frame for any child views
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i(TAG, "onTouchEvent: " + ev);
        return super.onTouchEvent(swapXY(ev));
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }

    @Override
    protected boolean canAnimate() {
        return true;
    }

}
