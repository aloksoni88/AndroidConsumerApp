package com.clicbrics.consumer.customview;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Alok on 14-08-2018.
 */
public class FadeViewPagerTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(@NonNull View view, float position) {
        if(position < -1 || position > 1) {
            view.setAlpha(0);
        }
        // Page is sibling to left or right
        else if (position <= 0 || position <= 1) {

            // Calculate alpha.  Position is decimal in [-1,0] or [0,1]
            float alpha = (position <= 0) ? position + 1 : 1 - position;
            view.setAlpha(alpha);

        }
        // Page is active, make fully visible
        else if (position == 0) {
            view.setAlpha(1);
        }
    }
}
