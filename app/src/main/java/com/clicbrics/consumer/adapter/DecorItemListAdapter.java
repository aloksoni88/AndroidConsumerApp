package com.clicbrics.consumer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buy.housing.backend.decorEndPoint.model.DecorItem;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.DecorDetailViewActivity;
import com.clicbrics.consumer.activities.HomeDecorPictureView;
import com.clicbrics.consumer.customview.ThreeTwoImageView;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 19-09-2017.
 */

public class DecorItemListAdapter extends RecyclerView.Adapter<DecorItemListAdapter.ViewHolder>  {

    private final String TAG = DecorItemListAdapter.class.getSimpleName();
    private List<DecorItem> decorList = new ArrayList<>();
    private Context mContext;
    private int mWidth, mHeight;

    public DecorItemListAdapter(Context context, List<DecorItem> data, View v) {
        mContext = context;
        decorList = data;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = (int)(displayMetrics.widthPixels*(2/3.0));
        mHeight = (int)(mWidth*(2/3.0));
        Log.i(TAG, "Width :"+mWidth + " Height :"+mHeight);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ThreeTwoImageView decorItemImage;
        TextView  decorItemTitle,decorItemDesc;
        ImageView videoOverlay;
        LinearLayout rootLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            decorItemImage = itemView.findViewById(R.id.decor_item_image);
            decorItemTitle = itemView.findViewById(R.id.decor_item_title);
            decorItemDesc = itemView.findViewById(R.id.decor_item_desc);
            videoOverlay = itemView.findViewById(R.id.video_overlay);
            rootLayout = itemView.findViewById(R.id.id_root_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.decor_item_list_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(decorList != null && !decorList.isEmpty()){
            DecorItem decorItem = decorList.get(position);
            try {
                if(decorItem != null){
                    if(position == 0){
                        holder.decorItemImage.setVisibility(View.GONE);
                        if(!TextUtils.isEmpty(decorItem.getTitle())){
                            holder.decorItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                            holder.decorItemTitle.setText(decorItem.getTitle());
                        }else{
                            holder.decorItemTitle.setText("");
                        }
                        if(!TextUtils.isEmpty(decorItem.getDetail())){
                            //holder.decorItemDesc.setText(Html.fromHtml(decorItem.getDetail().trim()));
                            setTextViewHTML(holder.decorItemDesc,decorItem.getDetail().trim());
                        }else{
                            holder.decorItemDesc.setText("");
                        }
                    }else{
                        if(!TextUtils.isEmpty(decorItem.getTitle())){
                            holder.decorItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                            holder.decorItemTitle.setText(decorItem.getTitle());
                        }else{
                            holder.decorItemTitle.setText("");
                        }
                        holder.decorItemImage.setVisibility(View.VISIBLE);
                        if(!TextUtils.isEmpty(decorItem.getUrl())){
                            if(decorItem.getType() != null && decorItem.getType().equalsIgnoreCase("Image")) {
                                holder.videoOverlay.setVisibility(View.GONE);
                                String imageURL = decorItem.getUrl().trim();
                                Picasso.get().load(imageURL)
                                        .placeholder(R.drawable.placeholder)
                                        .resize(0, mHeight)
                                        .into(holder.decorItemImage);
                                /*Picasso.with(mContext).load(imageURL)
                                        .placeholder(R.drawable.placeholder)
                                        .into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                int width = bitmap.getWidth();
                                                int height = bitmap.getHeight();
                                                Log.i(TAG, "Width and height are " + width + "--" + height);

                                                if (width > height) {
                                                    // landscape
                                                    float ratio = (float) width / mWidth;
                                                    width = mWidth;
                                                    height = (int)(height / ratio);
                                                } else if (height > width) {
                                                    // portrait
                                                    float ratio = (float) height / mHeight;
                                                    height = mHeight;
                                                    width = (int)(width / ratio);
                                                } else {
                                                    // square
                                                    height = mHeight;
                                                    width = mWidth;
                                                }
                                                Log.i(TAG, "cal width :"+width + " Height :"+height);
                                                holder.decorItemImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,width,height,true));
                                            }

                                            @Override
                                            public void onBitmapFailed(Drawable errorDrawable) {

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });*/
                                holder.decorItemImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(mContext,HomeDecorPictureView.class);
                                        intent.putExtra("Position",position);
                                        Gson gson = new Gson();
                                        String decorItemList = gson.toJson(decorList);
                                        intent.putExtra("DecorItemList",decorItemList);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }else if(decorItem.getType() != null && decorItem.getType().equalsIgnoreCase("Video")) {
                                holder.videoOverlay.setVisibility(View.VISIBLE);
                                final String url = decorItem.getUrl().trim();
                                String videoId = "";
                                if(url.contains("https://www.youtube.com/embed/")){
                                    videoId = url.replace("https://www.youtube.com/embed/", "");
                                }else if(url.contains("http://www.youtube.com/embed/")){
                                    videoId = url.replace("http://www.youtube.com/embed/", "");
                                }else if(url.contains("http://www.youtube.com/watch?v=")){
                                    videoId = url.replace("http://www.youtube.com/watch?v=", "");
                                }else if(url.contains("https://www.youtube.com/watch?v=")){
                                    videoId = url.replace("https://www.youtube.com/watch?v=", "");
                                }
                                String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                                if(!TextUtils.isEmpty(thumbnailUrl)){
                                    Picasso.get().load(thumbnailUrl)
                                            .resize(0, 512)
                                            .placeholder(R.drawable.default_video_thumbnail)
                                            .into(holder.decorItemImage);
                                }else{
                                    holder.decorItemImage.setImageResource(R.drawable.default_video_thumbnail);
                                }
                                holder.decorItemImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        youtubeIntent.putExtra("force_fullscreen", true);
                                        mContext.startActivity(youtubeIntent);
                                    }
                                });
                            }else{
                                holder.videoOverlay.setVisibility(View.GONE);
                                holder.decorItemImage.setImageResource(R.drawable.placeholder);
                            }
                        }else{
                            holder.videoOverlay.setVisibility(View.GONE);
                            holder.decorItemImage.setImageResource(R.drawable.placeholder);
                        }
                        if(!TextUtils.isEmpty(decorItem.getDetail())){
                            //holder.decorItemDesc.setText(Html.fromHtml(decorItem.getDetail().trim()));
                            setTextViewHTML(holder.decorItemDesc,decorItem.getDetail().trim());
                        }else{
                            holder.decorItemDesc.setText("");
                        }
                    }
                    holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            ((DecorDetailViewActivity) mContext).showButtonOnTouchView();
                            return true;
                        }
                    });
                    if(position == decorItem.size()){
                        holder.rootLayout.setPadding(0,0,0,10);
                    }else{
                        holder.rootLayout.setPadding(0,0,0,0);
                    }
                }
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

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                try {
                    if(span != null) {
                        Log.i(TAG, "onClick: ClickURL -> " + span.getURL());
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(span.getURL()));
                        if(UtilityMethods.isPackageInstalled(mContext,"com.android.chrome")){
                            intent.setPackage("com.android.chrome");
                        }
                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(span.getURL()));
                        mContext.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html)
    {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
