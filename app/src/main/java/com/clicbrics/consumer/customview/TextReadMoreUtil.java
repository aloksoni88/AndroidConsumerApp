package com.clicbrics.consumer.customview;

import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Alok on 19-08-2018.
 */
public class TextReadMoreUtil {

    public TextReadMoreUtil(final TextView tv, final int maxLine, final String expandText,
                            final boolean viewMore, final String summery){
        makeTextViewResizable(tv,maxLine,expandText,viewMore,summery);
    }

    private void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText,
                                      final boolean viewMore, final String summery) {
        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }

                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(tv.getText().toString(), tv, maxLine, expandText,
                            viewMore, summery), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text;
                    int subSeqIndex2 = lineEndIndex - expandText.length() + 1;
                    if (expandText.equals("Read More") && subSeqIndex2 > 8) {
                        text = tv.getText().subSequence(0,  subSeqIndex2- 8).toString().trim() + "... " + expandText;
                    } else {
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1).toString().trim() + " " + expandText;
                    }
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(tv.getText().toString().trim(), tv, maxLine, expandText,
                                    viewMore,summery.trim()), TextView.BufferType.SPANNABLE);
                } else {
                    String text;
                    int lineEndIndex;
                    if(tv.getLayout() != null && tv.getLineCount() > 3) {
                        lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        if (expandText.equals("Read More")) {
                            text = tv.getText().subSequence(0, lineEndIndex).toString().trim() + "... " + expandText;
                        } else {
                            text = tv.getText().subSequence(0, lineEndIndex).toString().trim() + " " + expandText;
                        }
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(tv.getText().toString().trim(), tv, lineEndIndex, expandText,
                                        viewMore, summery.trim()), TextView.BufferType.SPANNABLE);
                    }else{
                        text = tv.getText().toString();
                        tv.setText(text);
                    }
                }
            }
        });

    }

    private int collapseViewHeight = 0;
    private SpannableStringBuilder addClickablePartTextViewResizable(String strSpanned, final TextView tv,
                                                                     int maxLine, String spanableText,
                                                                     final boolean viewMore, final String summery) {

        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (strSpanned.contains(spanableText)) {
            ssb.setSpan(new SpannableText(false, tv.getContext()) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        //tv.setText(summery.trim(),TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        collapseViewHeight = tv.getMeasuredHeight();
                        makeTextViewResizable(tv, -1, "Less", false,summery.trim());
                        //Animation readMoreAnimation = AnimationUtils.loadAnimation(ProjectDetailActivity.this,R.anim.read_more_text);
                        //tv.setAnimation(readMoreAnimation);
                        expand(tv);
                    } else {
                        /*Animation readLessAnimation = AnimationUtils.loadAnimation(ProjectDetailActivity.this,R.anim.read_less_text);
                        tv.setAnimation(readLessAnimation);*/
                        collapse(tv);
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "Read More", true, summery.trim());
                    }

                }
            }, strSpanned.indexOf(spanableText), strSpanned.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }

    private int layoutHeight = 0;
    private void expand(final TextView v) {
        //v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int lineCount = v.getLineCount();
        final int lineHeight = v.getLineHeight();
        final int targetHeight = lineHeight*lineCount;
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        //v.getLayoutParams().height = 1;
        //v.setVisibility(View.VISIBLE);
        layoutHeight = v.getMeasuredHeight();
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1 || (targetHeight-layoutHeight) <= lineHeight){
                    v.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                }else{
                    layoutHeight = layoutHeight + lineHeight;
                    v.getLayoutParams().height = layoutHeight;
                }
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        //a.setDuration(700);
        v.startAnimation(a);
    }

    private int targetCollapsedHeight = 0;
    private void collapse(final TextView v) {
        targetCollapsedHeight = v.getMeasuredHeight();
        final int lineHeight = v.getLineHeight();
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1 || targetCollapsedHeight <= collapseViewHeight){
                    v.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                }else{
                    targetCollapsedHeight = targetCollapsedHeight - lineHeight;
                    v.getLayoutParams().height = targetCollapsedHeight;
                }
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(v.getMeasuredHeight() / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(600);
        v.startAnimation(a);
    }
}
