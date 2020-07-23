package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.Amenity;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 19/1/17.
 */
public class AmenitiesGridAdapter extends RecyclerView.Adapter<AmenitiesGridAdapter.AmenitiesViewHolder> {

    private static final String TAG = "AmenitiesGridAdapter";
    public int itemCount= 4;
    ArrayList<Amenity> amenityArrayList;
    Context mContext;

    public AmenitiesGridAdapter(Context context, List<Amenity> amenityList){
        if((amenityList!=null) && (!amenityList.isEmpty())) {
            amenityArrayList = (ArrayList<Amenity>) amenityList;
            mContext = context;
            itemCount = mContext.getResources().getInteger(R.integer.ameities_column_count);
            if (amenityArrayList.size() < itemCount) {
                itemCount = amenityList.size();
            }
        }
    }

    @Override
    public AmenitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_amenities_grid, parent, false);
        int width = parent.getMeasuredWidth() / (mContext.getResources().getInteger(R.integer.ameities_column_count));
        itemView.setMinimumWidth(width);
        return new AmenitiesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AmenitiesViewHolder holder, int position) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.amenityImage.setImageBitmap(getAmenityIconFromAssets(
                    amenityArrayList.get(position).getName().toLowerCase().trim()
                            .replaceAll("-","_")
                            .replaceAll(" ","_")
                            .replaceAll("\\*","x")
                            .replaceAll("/","_")));
        }else{
            holder.amenityImage.setImageBitmap(getAmenityIconFromAssets(
                    amenityArrayList.get(position).getName().toLowerCase().trim()
                            .replaceAll("-","_")
                            .replaceAll(" ","_")
                            .replaceAll("\\*","x")
                            .replaceAll("/","_")));
        }
        holder.amenityName.setText(amenityArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public class AmenitiesViewHolder extends RecyclerView.ViewHolder {
        TextView amenityName;
        ImageView amenityImage;
        public AmenitiesViewHolder(View itemView) {
            super(itemView);
            amenityImage = (ImageView) itemView.findViewById(R.id.amenities_icon);
            amenityName = (TextView) itemView.findViewById(R.id.amenity_name);
        }
    }

    private Bitmap getAmenityIconFromAssets(String amenityName) {
        AssetManager assetManager = mContext.getAssets();
        Bitmap bitmap;
        try {
            InputStream inputStream = assetManager.open(Constants.AppConstants.AMENITY_ICON_DIR
                    + File.separator + amenityName.trim() + ".png");
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_amenity_default_36dp);
            e.printStackTrace();
        }
        return bitmap;
    }

    private static Drawable getDrawableFromAssets(Context context, String amenityName){
        Log.i(TAG, "getDrawableFromAssets: Amentity Name " + amenityName);
        AssetManager assetManager = context.getAssets();
        Drawable drawable;
        try {
            InputStream inputStream = assetManager.open(Constants.AppConstants.AMENITY_ICON_DIR
                    + File.separator + amenityName.trim() + ".png");
            drawable = BitmapDrawable.createFromStream(inputStream,null);
            inputStream.close();
        } catch (IOException e) {
            drawable = context.getDrawable(R.drawable.ic_amenity_default_36dp);
            e.printStackTrace();
        }
        return drawable;
    }

    private int getAmenityImage(Context context, String amenityName){
        Log.i(TAG, "getDrawableFromAssets: Amentity Name " + amenityName);
        int resID = 0;
        try {
            if(amenityName.equalsIgnoreCase("24x7_security")){
                amenityName = "twentyfour_by_seven_security";
            }else if(amenityName.equalsIgnoreCase("meditation_&_yoga_center")){
                amenityName = "meditation_and_yoga_center";
            }
            resID = context.getResources().getIdentifier(amenityName, "drawable",  context.getPackageName());
            if(resID == 0){
                resID = R.drawable.amenity_default;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resID = R.drawable.amenity_default;
        }
        return resID;
    }
}
