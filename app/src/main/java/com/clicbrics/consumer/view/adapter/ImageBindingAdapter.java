package com.clicbrics.consumer.view.adapter;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.squareup.picasso.Picasso;

import java.util.Set;

/**
 * Created by Alok on 31-07-2018.
 */
public class ImageBindingAdapter {

    public static final String COVER_IMAGE_SIZE = "=h1200-w800-c";
    /**
     * This is jsut to cache image url in picasso
     * @param view
     * @param imageURL
     */
    @BindingAdapter("setDummyImageListImage")
    public static void setDummyImageListImageResource(ImageView view, String imageURL){
        Picasso.get().load(imageURL+COVER_IMAGE_SIZE)
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter("setImageListImage")
    public static void setImageListImageResource(ImageView view, String imageURL){
        Picasso.get().load(imageURL+"=h400")
                .placeholder(R.drawable.placeholder)
                .config(Bitmap.Config.ARGB_8888)
                .priority(Picasso.Priority.HIGH)
                .into(view);
    }

    @BindingAdapter("setArticleCoverImage")
    public static void setArticleCoverResource(ImageView view, String imageURL){
        Picasso.get().load(imageURL+"=h400")
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter("setArticleImage")
    public static void setArticleImageResource(ImageView view, String imageURL){
        Picasso.get().load(imageURL+"=h200")
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter("setMapListImage")
    public static void setMapListImageResource(ImageView view, String imageURL){
        Picasso.get().load(imageURL+"=h400")
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter("setMapListDummyImage")
    public static void setMapListDummyImageResource(ImageView view, String imageURL){
        Picasso.get().load(imageURL+COVER_IMAGE_SIZE)
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter("setFavoriteImage")
    public static void setFavoriteImageResource(ImageView view, long projectId){
        Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(view.getContext(), Constants.AppConstants.PROJECT_ID_SET);
        if(favoriteList == null || favoriteList.isEmpty()){
            view.setImageResource(R.drawable.ic_favorite_border_gray_24dp);
        }else{
            if (favoriteList.contains(projectId + "")) {
                view.setImageResource(R.drawable.ic_favorite_red);
            }else{
                view.setImageResource(R.drawable.ic_favorite_border_gray_24dp);
            }
        }
    }

    @BindingAdapter("setMapFavoriteImage")
    public static void setMapFavoriteImageResource(ImageView view, long projectId){
        Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(view.getContext(), Constants.AppConstants.PROJECT_ID_SET);
        if(favoriteList == null || favoriteList.isEmpty()){
            view.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }else{
            if (favoriteList.contains(projectId + "")) {
                view.setImageResource(R.drawable.ic_favorite_red);
            }else{
                view.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            }
        }
    }
}
