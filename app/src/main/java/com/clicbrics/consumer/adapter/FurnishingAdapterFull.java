package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.wrapper.AmenityObject;

import java.util.List;

/**
 * Created by root on 3/2/17.
 */
public class FurnishingAdapterFull extends RecyclerView.Adapter<FurnishingAdapterFull.AmenityViewHolder> {

        Context mContext;
        List<AmenityObject> mAmenityList;
        public FurnishingAdapterFull(Context context, List<AmenityObject> amenityList) {
            mContext = context;
            mAmenityList = amenityList;
        }

        @Override
        public AmenityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_furnishing_list, parent, false);
            return new AmenityViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(AmenityViewHolder holder, int position) {
            holder.furnishingName.setText(mAmenityList.get(position).Name);
            holder.furnishingType.setText(mAmenityList.get(position).Type);
            if((position%2) !=0){
                holder.itemLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_300));
            }
        }

        @Override
        public int getItemCount() {
            return mAmenityList.size();
        }

        public class AmenityViewHolder extends RecyclerView.ViewHolder {
            TextView furnishingName, furnishingType;
            LinearLayout itemLayout;
            public AmenityViewHolder(View itemView) {
                super(itemView);
                furnishingName=(TextView)itemView.findViewById(R.id.furnishing_name);
                furnishingType=(TextView)itemView.findViewById(R.id.furnishing_type);
                itemLayout=(LinearLayout)itemView.findViewById(R.id.layout_furnishing_item);
            }
        }
    }

