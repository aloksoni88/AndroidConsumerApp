package com.clicbrics.consumer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.SectioningAdapter;
import com.clicbrics.consumer.activities.ImageGalleryPictureView;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.wrapper.ImageObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viven.imagezoom.ImageZoomHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ImageGalleryAdapter extends SectioningAdapter {

    private final String TAG = ImageGalleryAdapter.class.getSimpleName();
    private Context mContext;
    private LinkedHashMap<String, ArrayList<ImageObject>> urlMap;
    private ArrayList<String> galleryTitles;

    public ImageGalleryAdapter(Context context, LinkedHashMap<String, ArrayList<ImageObject>> urlMap) {
        this.urlMap = urlMap;
        mContext = context;
        galleryTitles = new ArrayList<>(urlMap.keySet());
        /*if(urlMap.containsKey("Images")){
            ArrayList<ImageObject> imageObjects = urlMap.get("Images");
            if(urlMap.containsKey("OVERVIEW")){
                ArrayList<ImageObject> overviewObjects = urlMap.get("OVERVIEW");
                if(imageObjects != null && imageObjects.size() > 0) {
                    overviewObjects.addAll(imageObjects);
                }
                urlMap.remove("Images");
                galleryTitles.remove("Images");
                urlMap.remove("OVERVIEW");
                urlMap.put("OVERVIEW",overviewObjects);
            }
        }*/
        //Collections.sort(galleryTitles, new ImageGallerySorting());
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
        //ThreeTwoImageView image;
        ImageView image;
        ImageView videoLayout;
        ProgressBar mImagePB;

        public ItemViewHolder(View itemView) {
            super(itemView);
            image = (/*ThreeTwo*/ImageView) itemView.findViewById(R.id.item_image);
            videoLayout = (ImageView) itemView.findViewById(R.id.video_layout);
            mImagePB = (ProgressBar) itemView.findViewById(R.id.id_gallery_image_PB);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        TextView title;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.image_title);
        }
    }

    @Override
    public int getNumberOfSections() {
        return urlMap.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return urlMap.get(galleryTitles.get(sectionIndex)).size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_gallery, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.header_gallery, parent, false);
        return new HeaderViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, final int sectionIndex, final int itemIndex, int itemType) {
        final ItemViewHolder ivh = (ItemViewHolder) viewHolder;
        if (!urlMap.get(galleryTitles.get(sectionIndex)).get(itemIndex).isVideo) {
            ImageObject imageObject = urlMap.get(galleryTitles.get(sectionIndex)).get(itemIndex);
            String imageURL = imageObject.image_url;
            if(imageURL!= null){
                if(!imageURL.contains("http")) {
                    imageURL = BuildConfigConstants.storage_path + imageURL;
                }
                ivh.videoLayout.setVisibility(View.GONE);
                ivh.mImagePB.setVisibility(View.VISIBLE);
                ImageZoomHelper.setViewZoomable(ivh.image);
                Picasso.get().load(imageURL)
                        .placeholder(R.drawable.placeholder)
                        .into(ivh.image, new Callback() {
                            @Override
                            public void onSuccess() {
                                ivh.mImagePB.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                ivh.mImagePB.setVisibility(View.GONE);
                            }
                        });
                ivh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String imageURL = urlMap.get(galleryTitles.get(sectionIndex)).get(itemIndex).image_url;
                        if(!imageURL.contains("http")){
                            imageURL = BuildConfigConstants.storage_path + imageURL;
                        }
                        Log.i(TAG, "onClick: ImageGallery adapter");
                        Intent pictureView = new Intent(mContext, ImageGalleryPictureView.class);
                        pictureView.putExtra("URLMap",urlMap);
                        pictureView.putExtra("Title",galleryTitles.get(sectionIndex));
                        pictureView.putExtra("GalleryTitleList",galleryTitles);
                        pictureView.putExtra("GalleryImageURL",imageURL);
                        mContext.startActivity(pictureView);
                    }
                });
            }
            Log.i(TAG, "onBindItemViewHolder: ImageURL -> " + imageURL);
        } else {
            ivh.videoLayout.setVisibility(View.VISIBLE);
            ivh.mImagePB.setVisibility(View.GONE);
            String videoUrl = urlMap.get(galleryTitles.get(sectionIndex)).get(itemIndex).image_url;
            if(videoUrl != null){
                if(!videoUrl.contains("http")) {
                    videoUrl = BuildConfigConstants.storage_path + videoUrl;
                }
                String videoId = "";
                if(videoUrl.contains("https://www.youtube.com/embed/")){
                    videoId = videoUrl.replace("https://www.youtube.com/embed/", "");
                }else if(videoUrl.contains("http://www.youtube.com/embed/")){
                    videoId = videoUrl.replace("http://www.youtube.com/embed/", "");
                }else if(videoUrl.contains("http://www.youtube.com/watch?v=")){
                    videoId = videoUrl.replace("http://www.youtube.com/watch?v=", "");
                }else if(videoUrl.contains("https://www.youtube.com/watch?v=")){
                    videoId = videoUrl.replace("https://www.youtube.com/watch?v=", "");
                }
                if(videoUrl.contains("youtube")) {
                    String imgURL = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                    Picasso.get().load(imgURL)
                            .placeholder(R.drawable.default_video_thumbnail)
                            .into(ivh.image);
                }else{
                    Picasso.get().load(videoUrl)
                            .placeholder(R.drawable.default_video_thumbnail)
                            .into(ivh.image);
                }
                Log.i(TAG, "onBindItemViewHolder: VideoURL -> " + videoUrl);
                ivh.videoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String videoUrl = urlMap.get(galleryTitles.get(sectionIndex)).get(itemIndex).image_url;
                        if(!videoUrl.contains("http")){
                            videoUrl = BuildConfigConstants.storage_path + videoUrl;
                        }
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        youtubeIntent.putExtra("force_fullscreen",true);
                        mContext.startActivity(youtubeIntent);
                    }
                });
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
        if(galleryTitles.get(sectionIndex).equalsIgnoreCase("OVERVIEW") || galleryTitles.get(sectionIndex).equalsIgnoreCase("Images")){
            hvh.title.setText("OVERVIEW");
        }else {
            hvh.title.setText(galleryTitles.get(sectionIndex));
        }
    }
}
