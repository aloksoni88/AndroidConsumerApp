package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clicbrics.consumer.R;;
import com.clicbrics.consumer.wrapper.AmenitiesWrapper;

import java.util.List;

/**
 * Created by Alok on 15-06-2016.
 */
public class AmenitiesAdapter extends BaseAdapter {
    Context context;
    private List<AmenitiesWrapper> amenitiesList;
    AssetManager assetManager;
    private static LayoutInflater inflater = null;
    Typeface mFont;

    public AmenitiesAdapter(Context context, List<AmenitiesWrapper> amenitiesList){
        assetManager = context.getAssets();
        this.context = context;
        this.amenitiesList = amenitiesList;
//        mFont= Typeface.createFromAsset(context.getAssets(),
//                "font/HelveticaNeueUltraLight.ttf");
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return  amenitiesList.size();
    }

    @Override
    public AmenitiesWrapper getItem(int position) {
        return  amenitiesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder = null;
        if(view == null){
            holder = new Holder();
            view = inflater.inflate(R.layout.e_amenitis,null,false);
            holder.name = (TextView) view.findViewById(R.id.amenitiesTxt);
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            view.setTag(holder);
        }
        else{
            holder = (Holder) view.getTag();
        }
        final ViewGroup mContainer = parent;
  //      UtilityMethods.setAppFont(mContainer, mFont, false);
        final AmenitiesWrapper amenitiesWrapper =  amenitiesList.get(position);
        holder.name.setText(amenitiesWrapper.name);
        holder.icon.setImageBitmap(amenitiesWrapper.image);
        return view;
    }


    public static class Holder
    {
        TextView name;
        ImageView icon;
    }
}
