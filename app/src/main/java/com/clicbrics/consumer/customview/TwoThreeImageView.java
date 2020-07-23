package com.clicbrics.consumer.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Alok on 13-08-2018.
 */
public class TwoThreeImageView extends ImageView{
    public TwoThreeImageView(Context context) {
        super(context);
    }

    public TwoThreeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoThreeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        double threeTwoHeight = View.MeasureSpec.getSize(widthMeasureSpec) * 3/ 2;
        int threeTwoHeightSpec = View.MeasureSpec.makeMeasureSpec((int)threeTwoHeight, View.MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec,threeTwoHeightSpec);
    }
}
