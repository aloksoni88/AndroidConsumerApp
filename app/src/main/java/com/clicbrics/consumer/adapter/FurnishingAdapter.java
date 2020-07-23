package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.Amenity;
import com.clicbrics.consumer.R;

import java.util.List;

/**
 * Created by root on 3/2/17.
 */
public class FurnishingAdapter extends RecyclerView.Adapter<FurnishingAdapter.AmenityViewHolder> {

    Context mContext;
    List<Amenity> mAmenityList;
    int mItemsShown=4;
    public FurnishingAdapter(Context context, List<Amenity> amenityList, int itemsShown) {
        mContext = context;
        mAmenityList = amenityList;
        mItemsShown = itemsShown;
    }

    @Override
    public AmenityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_furnishing_list, parent, false);
        return new AmenityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AmenityViewHolder holder, int position) {
        holder.furnishingName.setText(mAmenityList.get(position).getName());
        holder.furnishingType.setText(mAmenityList.get(position).getDetails());
        if((position%2) !=0){
            holder.itemLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_300));
        }
    }

    @Override
    public int getItemCount() {
        return mItemsShown;
    }

    public class AmenityViewHolder extends RecyclerView.ViewHolder {
        TextView furnishingName, furnishingType;
        LinearLayout itemLayout;
        public AmenityViewHolder(View itemView) {
            super(itemView);
            furnishingName=(TextView)itemView.findViewById(R.id.furnishing_name);
            furnishingType=(TextView)itemView.findViewById(R.id.furnishing_type);
            if(mItemsShown == 4){
                furnishingType.setMaxLines(1);
                furnishingType.setEllipsize(TextUtils.TruncateAt.END);
                furnishingType.setSingleLine(true);
            }
            itemLayout=(LinearLayout)itemView.findViewById(R.id.layout_furnishing_item);
        }
    }
}
