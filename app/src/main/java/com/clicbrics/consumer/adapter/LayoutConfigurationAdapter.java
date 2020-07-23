package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.PropertyDetail;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 7/2/17.
 */
public class LayoutConfigurationAdapter extends RecyclerView.Adapter<LayoutConfigurationAdapter.ConfigurationViewHolder> {

    public int itemCount = 4;
    ArrayList<PropertyDetail> detailArrayList;
    Context mContext;

    public LayoutConfigurationAdapter(Context context, List<PropertyDetail> amenityList) {
        detailArrayList = (ArrayList<PropertyDetail>) amenityList;
        mContext = context;
        itemCount = mContext.getResources().getInteger(R.integer.ameities_column_count);
        if (detailArrayList.size() < itemCount) {
            itemCount = amenityList.size();
        }
    }

    @Override
    public ConfigurationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_configuration_adapter, parent, false);
        int width = parent.getMeasuredWidth() / (mContext.getResources().getInteger(R.integer.ameities_column_count));
        itemView.setMinimumWidth(width);
        return new ConfigurationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConfigurationViewHolder holder, int position) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.amenityImage.setBackground(detailArrayList.get(position).icon);
        }else{
            holder.amenityImage.setBackgroundDrawable(detailArrayList.get(position).icon);
        }
        //holder.amenityImage.setImageDrawable(detailArrayList.get(position).icon);
        holder.amenityName.setText(detailArrayList.get(position).count+" "+detailArrayList.get(position).label);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public class ConfigurationViewHolder extends RecyclerView.ViewHolder {
        TextView amenityName;
        ImageView amenityImage;

        public ConfigurationViewHolder(View itemView) {
            super(itemView);
            amenityImage = (ImageView) itemView.findViewById(R.id.amenities_icon);
            amenityName = (TextView) itemView.findViewById(R.id.amenity_name);
        }

    }
}

