package com.clicbrics.consumer.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
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

import com.buy.housing.backend.newsEndPoint.model.News;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.BlogListActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 13-07-2018.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder>{

    private static final String TAG = "NewsListAdapter";

    private List<News> newsList = new ArrayList<>();
    private Activity mContext;
    private int mTotalNewsCount;


    public NewsListAdapter(Activity mContext, List<News> newsList,int totalNewsCount) {
        this.newsList = newsList;
        this.mContext = mContext;
        this.mTotalNewsCount = totalNewsCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView converImage;
        ImageView newsImage;
        TextView coverImageTitle,newsTitle;
        TextView time,coverImageTime;
        FrameLayout coverLayout;
        LinearLayout newsListLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            converImage = itemView.findViewById(R.id.cover_image);
            newsImage = itemView.findViewById(R.id.news_image);
            coverImageTitle = itemView.findViewById(R.id.cover_image_news_title);
            newsTitle = itemView.findViewById(R.id.news_title);
            time = itemView.findViewById(R.id.time);
            coverImageTime = itemView.findViewById(R.id.cover_image_news_time);
            coverLayout = itemView.findViewById(R.id.id_cover_image_layout);
            newsListLayout = itemView.findViewById(R.id.id_news_list_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news_list_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(newsList != null && !newsList.isEmpty()){
            final News news = newsList.get(position);
            try {
                if(news != null){
                    if(position == 0){
                        holder.coverLayout.setVisibility(View.VISIBLE);
                        holder.newsListLayout.setVisibility(View.GONE);
                        if(!TextUtils.isEmpty(news.getImage())){
                            Picasso.get().load(news.getImage().toString())
                                    .fit()
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.converImage);
                        }else{
                            holder.converImage.setImageResource(R.drawable.placeholder);
                        }
                        if(!TextUtils.isEmpty(news.getTitle())){
                            holder.coverImageTitle.setText(news.getTitle());
                        }
                        if(news.getCreateTime() != null){
                            holder.coverImageTime.setText(UtilityMethods.getDate(news.getCreateTime(),"dd MMM, yyyy"));
                        }
                    }else{
                        holder.newsListLayout.setVisibility(View.VISIBLE);
                        holder.coverLayout.setVisibility(View.GONE);
                        if(!TextUtils.isEmpty(news.getImage())){
                            Picasso.get().load(news.getImage().toString())
                                    .fit()
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.newsImage);
                        }else{
                            holder.newsImage.setImageResource(R.drawable.placeholder);
                        }

                        if(!TextUtils.isEmpty(news.getTitle())){
                            holder.newsTitle.setText(news.getTitle());
                        }
                        if(news.getCreateTime() != null){
                            holder.time.setText(UtilityMethods.getDate(news.getCreateTime(),"dd MMM, yyyy"));
                        }
                    }
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new EventAnalyticsHelper().ItemClickEvent(mContext, Constants.AnaylticsClassName.NewsScreen,
                                    news.get(position), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.newsItemClick.toString());
                            Intent intent = new Intent(mContext, BlogListActivity.class);
                            intent.putExtra("Position",position);
                            intent.putExtra("TotalNewsCount",mTotalNewsCount);
                            intent.putExtra("ArticleType","news");
                            mContext.startActivity(intent);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if(newsList != null){
            return newsList.size();
        }else {
            return 0;
        }
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        if(scaledBitmap != null) {
            Log.i(TAG, "scaledBitmap: w & h " + scaledBitmap.getWidth() + " " + scaledBitmap.getHeight());
        }
        return scaledBitmap;
    }
}
