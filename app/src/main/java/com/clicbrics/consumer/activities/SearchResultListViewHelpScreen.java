package com.clicbrics.consumer.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

/**
 * Created by Alok on 05-04-2017.
 */

public class SearchResultListViewHelpScreen extends Dialog {

    private Context mContext;
    public SearchResultListViewHelpScreen(Context context){
        super(context, R.style.AppTheme_NoActionBar);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        mContext = context;
        //wlmp.gravity = Gravity.CENTER;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setContentView(R.layout.activity_searchresult_listview_help_screen);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.searchview_helpscreen_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    long onStartTime;
    @Override
    protected void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TrackAnalytics.trackEvent("SearchResultsListHelpScreen", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), mContext);
    }
}
