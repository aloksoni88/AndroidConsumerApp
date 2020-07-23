package com.clicbrics.consumer.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by root on 6/2/17.
 */
public class SpaceItemDecorator extends RecyclerView.ItemDecoration {

    private Context mContext;
    public SpaceItemDecorator(Context context) {
        mContext = context;
    }

    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = 14;//convertDpToPixels( 12, mContext);
        }
    }

    public static int convertDpToPixels(float dp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }
}