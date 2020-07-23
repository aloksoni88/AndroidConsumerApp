package com.clicbrics.consumer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.TypedValue;

import com.clicbrics.consumer.R;

/**
 * Created by Alok on 10-02-2017.
 */

@SuppressLint("ParcelCreator")
public class CustomTypeFace extends TypefaceSpan {

    private final Typeface newType;
    private Context mContext;

    public CustomTypeFace(Context context, String family, Typeface type) {
        super(family);
        newType = type;
        mContext = context;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, newType);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }

    private void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTextSize(convertSpToPixels(mContext.getResources().getInteger(R.integer.drawer_font_size), mContext));
        paint.setTypeface(tf);
    }

    public static int convertSpToPixels(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }
}
