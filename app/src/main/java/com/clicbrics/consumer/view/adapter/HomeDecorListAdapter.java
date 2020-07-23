package com.clicbrics.consumer.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buy.housing.backend.decorEndPoint.model.Decor;
import com.buy.housing.backend.decorEndPoint.model.DecorItem;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.DecorDetailViewActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 13-07-2018.
 */
public class HomeDecorListAdapter extends RecyclerView.Adapter<HomeDecorListAdapter.ViewHolder>{

    private static final String TAG = "HomeDecorListAdapter";

    private List<Decor> decorList = new ArrayList<>();
    private Activity mContext;
    private int mTotalDecorCount;

    public HomeDecorListAdapter(Activity mContext, List<Decor> decorList, int totalDecorCount) {
        this.decorList = decorList;
        this.mContext = mContext;
        this.mTotalDecorCount = totalDecorCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView converImage;
        ImageView decorImage;
        TextView decorTitle,coverImageTitle;
        TextView coverImageTime,time;
        FrameLayout coverLayout;
        LinearLayout decorListLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            converImage = itemView.findViewById(R.id.cover_image);
            decorImage = itemView.findViewById(R.id.decor_image);
            coverImageTitle = itemView.findViewById(R.id.cover_image_decor_title);
            decorTitle = itemView.findViewById(R.id.decor_title);
            time = itemView.findViewById(R.id.time);
            coverImageTime = itemView.findViewById(R.id.cover_image_decor_time);
            coverLayout = itemView.findViewById(R.id.id_cover_image_layout);
            decorListLayout = itemView.findViewById(R.id.id_decor_list_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_decor_list_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(decorList != null && !decorList.isEmpty()){
            final Decor decor = decorList.get(position);
            try {
                if(position == 0){
                    holder.coverLayout.setVisibility(View.VISIBLE);
                    holder.decorListLayout.setVisibility(View.GONE);
                    if(decor != null && decor.getTitleSection() != null){
                        DecorItem decorItem = decor.getTitleSection();
                        if(decor.getCreateTime() != null && decor.getCreateTime() != 0){
                            holder.coverImageTime.setText(UtilityMethods.getDate(decor.getCreateTime(),"dd MMM, yyyy"));
                        }
                        if(decorItem != null){
                            if(!TextUtils.isEmpty(decorItem.getTitle())){
                                holder.coverImageTitle.setVisibility(View.VISIBLE);
                                holder.coverImageTitle.setText(decorItem.getTitle());
                            }else{
                                holder.coverImageTitle.setVisibility(View.GONE);
                            }

                            if(!TextUtils.isEmpty(decorItem.getUrl())){
                                String url = decorItem.getUrl().trim();
                                Log.i(TAG, "onBindViewHolder: position -> "+ position + " Image URL -> " + url);
                                Picasso.get().load(url)
                                        .resize(0,256)
                                        .placeholder(R.drawable.placeholder)
                                        .into(holder.converImage);
                            }

                        }
                    }
                }else{
                    holder.coverLayout.setVisibility(View.GONE);
                    holder.decorListLayout.setVisibility(View.VISIBLE);
                    if(decor != null && decor.getTitleSection() != null){
                        DecorItem decorItem = decor.getTitleSection();
                        if(decor.getCreateTime() != null && decor.getCreateTime() != 0){
                            holder.time.setText(UtilityMethods.getDate(decor.getCreateTime(),"dd MMM, yyyy"));
                        }
                        if(decorItem != null){
                            if(!TextUtils.isEmpty(decorItem.getTitle())){
                                holder.decorTitle.setVisibility(View.VISIBLE);
                                holder.decorTitle.setText(decorItem.getTitle());
                            }else{
                                holder.decorTitle.setVisibility(View.GONE);
                            }

                            if(!TextUtils.isEmpty(decorItem.getUrl())){
                                String url = decorItem.getUrl().trim();
                                Log.i(TAG, "onBindViewHolder: position -> "+ position + " Image URL -> " + url);
                                Picasso.get().load(url)
                                        .resize(0,256)
                                        .placeholder(R.drawable.placeholder)
                                        .into(holder.decorImage);
                            }

                        }
                    }
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new EventAnalyticsHelper().ItemClickEvent(mContext, Constants.AnaylticsClassName.HomeDecor,
                                decor.get(position), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.homeDecoreItemclick.toString());
                        Intent intent = new Intent(mContext, DecorDetailViewActivity.class);
                        intent.putExtra("Position",position);
                        intent.putExtra("TotalDecorCount",mTotalDecorCount);
                        mContext.startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if(decorList != null){
            return decorList.size();
        }else {
            return 0;
        }
    }
}
