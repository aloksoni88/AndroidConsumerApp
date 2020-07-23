package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.CountryDto;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 15-06-2016.
 */
public class CountryListAdapter extends BaseAdapter implements Filterable {
    Context context;
    List<CountryDto> countryList;
    List<CountryDto> countryListForSearch;
    AssetManager assetManager;
    private static LayoutInflater inflater = null;
    Typeface mFont;

    public CountryListAdapter(Context context, List<CountryDto> countryList){
        assetManager = context.getAssets();
        this.context = context;
        this.countryListForSearch = countryList;
        this.countryList = countryList;
        mFont= Typeface.createFromAsset(context.getAssets(),
                "fonts/FiraSans-Medium.ttf");
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public CountryDto getItem(int position) {
        return countryList.get(position);
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
            view = inflater.inflate(R.layout.country_list_view,null,false);
            holder.countryName = (TextView) view.findViewById(R.id.countryName);
            holder.countryFlag = (ImageView) view.findViewById(R.id.flagImage);
            holder.countryStdCode = (TextView) view.findViewById(R.id.countryStdCode);
            view.setTag(holder);
        }
        else{
            holder = (Holder) view.getTag();
        }
        final ViewGroup mContainer = parent;
        UtilityMethods.setAppFont(mContainer, mFont, false);
        final CountryDto countryDto = countryList.get(position);
        holder.countryName.setText(countryDto.getCountryName());
        holder.countryFlag.setImageBitmap(countryDto.getFlagImage());
        holder.countryStdCode.setText(countryDto.getCountryCode());
        /*if(countryDto.getFlagImage() == null){
            try {
                Bitmap bitmap = getBitmapFromAsset(countryDto.getFlagName());
                holder.countryFlag.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            holder.countryFlag.setImageBitmap(countryDto.getFlagImage());
        }*/

        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0)
                {
                    filterResults.values = countryListForSearch;
                    filterResults.count = countryListForSearch.size();
                }
                else
                {
                    List<CountryDto> countryListDto = new ArrayList<>();
                    for(CountryDto countryDto : countryListForSearch)
                    {
                        if(countryDto.getCountryName().toLowerCase().contains(constraint) ||
                                countryDto.getCountryName().toUpperCase().contains(constraint)) {
                            countryListDto.add(countryDto);
                        }
                    }

                    if(countryListDto.size()==0){
                        CountryDto ct   =    new CountryDto();
                        ct.setCountryName("No country found!");
                        countryListDto.add(ct);
                    }
                    filterResults.values = countryListDto;
                    filterResults.count = countryListDto.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                countryList = (ArrayList<CountryDto>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class Holder
    {
        TextView countryName;
        ImageView countryFlag;
        TextView countryStdCode;
    }
}
