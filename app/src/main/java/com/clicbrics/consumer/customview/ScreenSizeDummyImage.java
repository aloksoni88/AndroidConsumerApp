package com.clicbrics.consumer.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.clicbrics.consumer.utils.UtilityMethods;

/**
 * Created by Alok on 16-08-2018.
 */
public class ScreenSizeDummyImage extends ImageView {
    private Context context;
    public ScreenSizeDummyImage(Context context) {
        super(context);
        this.context = context;
    }

    public ScreenSizeDummyImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ScreenSizeDummyImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int deviceHeight = UtilityMethods.getScreenHeight(context)- UtilityMethods.getStatusBarHeight(context);
        int screenHeight = View.MeasureSpec.makeMeasureSpec(deviceHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec,screenHeight);
    }
}
