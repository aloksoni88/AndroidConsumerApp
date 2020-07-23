package com.clicbrics.consumer.customview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by root on 8/1/17.
 */
public class ScaledCardView extends CardView {

    private float ratio = 1.0f;

    public ScaledCardView(Context context) {
        this(context, null);
    }

    public ScaledCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaledCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
       /* if (ratio > 0) {
            int ratioHeight = (int) (getMeasuredWidth() * ratio);
            setMeasuredDimension(getMeasuredWidth(), ratioHeight);
            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.height = (int) (ratioHeight);
            setLayoutParams(lp);
        }*/
        if (ratio > 0) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
            ViewGroup.LayoutParams lp = getLayoutParams();
        }
    }
}

