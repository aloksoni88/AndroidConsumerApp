package com.clicbrics.consumer.utils;

import android.content.Context;
import android.view.View;

import com.clicbrics.consumer.velocityviewpager.VelocityViewPager;

/**
 * Created by root on 8/1/17.
 */
public class CarouselPageTransformer implements VelocityViewPager.PageTransformer {

    private int maxTranslateOffsetX;
    private VelocityViewPager viewPager;

    public CarouselPageTransformer(Context context) {
        this.maxTranslateOffsetX = dp2px(context, 140);
    }

    public void transformPage(View view, float position) {
        if (viewPager == null) {
            viewPager = (VelocityViewPager) view.getParent();
        }

        int leftInScreen = view.getLeft() - viewPager.getScrollX();
        int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
        int offsetX = centerXInViewPager - viewPager.getMeasuredWidth() / 2;
        float offsetRate = (float) offsetX * 0.38f / viewPager.getMeasuredWidth();
        float scaleFactor = 1 - Math.abs(offsetRate);
        if (scaleFactor > 0) {
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setTranslationX(-maxTranslateOffsetX * offsetRate);
        }
    }

    private int dp2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

}

