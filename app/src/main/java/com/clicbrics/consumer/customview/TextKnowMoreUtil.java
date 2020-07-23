package com.clicbrics.consumer.customview;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.clicbrics.consumer.activities.OfferDetailActivity;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;

/**
 * Created by Alok on 19-08-2018.
 */
public class TextKnowMoreUtil {
    public TextKnowMoreUtil(final TextView tv, final String expandText, final String offer, final String detailLink) {
        makeKnowMoreTextUI(tv,expandText,offer,detailLink);
    }

    private void makeKnowMoreTextUI(final TextView tv, final String expandText, final String offer, final String detailLink) {
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

                String text = offer.trim()+ " " + expandText;
                tv.setText(text);
                //tv.setText(offer.trim(), TextView.BufferType.SPANNABLE);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(addKnowMoreClickListener(tv.getContext(),tv.getText().toString(),expandText,detailLink),
                        TextView.BufferType.SPANNABLE);
            }
        });
    }

    private SpannableStringBuilder addKnowMoreClickListener(final Context context, String strSpanned, String spanableText,
                                                            final String detailLink) {

        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (strSpanned.contains(spanableText)) {
            ssb.setSpan(new SpannableText(true, context) {
                @Override
                public void onClick(View widget) {
                    if(!UtilityMethods.isInternetConnected(context)){
                        UtilityMethods.showErrorSnackbarOnTop((ProjectDetailsScreen)context);
                        return;
                    }
                    Intent intent = new Intent(context, OfferDetailActivity.class);
                    intent.putExtra("DetailLink",detailLink);
                    context.startActivity(intent);
                }
            }, strSpanned.indexOf(spanableText), strSpanned.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }
}
