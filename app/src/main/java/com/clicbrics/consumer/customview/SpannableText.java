package com.clicbrics.consumer.customview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.clicbrics.consumer.R;

/**
 * Created by root on 18/1/17.
 */
public class SpannableText extends ClickableSpan{

    private boolean isUnderline = false;
    Context mContext;
    /**
     * Constructor
     */
    public SpannableText(boolean isUnderline, Context context) {
        this.isUnderline = isUnderline;
        this.mContext = context;
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        ds.setUnderlineText(isUnderline);
        ds.setColor(ContextCompat.getColor(mContext ,R.color.colorAccent));
    }

    @Override
    public void onClick(View widget) {

    }
}
