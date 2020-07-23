package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.model.City;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.PickCity;
import com.clicbrics.consumer.fragment.PickCityFragment;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.clicbrics.consumer.fragment.PickCityFragment.filtercityList;

/**
 * Created by root on 29/12/16.
 */
public class CityAdapter extends BaseAdapter implements Filterable {

    Context mContext;
    List<City> cityList;
//    List<City> originalcityList;
    private int selectedItem = -1;
    private Typeface light,bold;
    public CityAdapter(Context context, List<City> cityList){
        mContext = context;
        this.cityList = cityList;
//        this.originalcityList = cityList;
        filtercityList = cityList;
        light = Typeface.createFromAsset(mContext.getAssets(), "fonts/FiraSans-Light.ttf");
        bold = Typeface.createFromAsset(mContext.getAssets(), "fonts/FiraSans-Bold.ttf");
        Collections.reverse(cityList);
    }

    @Override
    public int getCount() {
        return filtercityList.size();
    }

    @Override
    public Object getItem(int position) {
        return filtercityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_city_list, parent, false);
        }
        TextView cityName = (TextView)convertView.findViewById(R.id.city_name);
        String city = filtercityList.get(position).getName();
        cityName.setText(city);
        if(selectedItem==-2){
            cityName.setTextColor(mContext.getResources().getColor(R.color.text_color_login_reg));
            cityName.setTypeface(light);
        } else if((position==selectedItem) ||
                (city.equals(UtilityMethods.getStringInPref(mContext,
                        Constants.AppConstants.SAVED_CITY, "")))){
            Log.d("TAG", "Set selected!! " + filtercityList.get(position).getName());
            cityName.setTextColor(mContext.getResources().getColor(R.color.black));
            cityName.setTypeface(bold);
        } else {
            cityName.setTextColor(mContext.getResources().getColor(R.color.text_color_login_reg));
            cityName.setTypeface(light);
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults=new FilterResults();
                ArrayList<City> FilteredArrList = new ArrayList<City>();
                if(cityList==null)
                {
                    cityList = new ArrayList<City>(cityList);
                }
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    filterResults.count = cityList.size();
                    filterResults.values = cityList;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < cityList.size(); i++) {
                        String data = cityList.get(i).getName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(cityList.get(i));
                        }
                    }
                    // set the Filtered result to return
                    filterResults.count = FilteredArrList.size();
                    filterResults.values = FilteredArrList;
                    Log.i("CityAdapter", "performFiltering: ======="+filterResults.values);
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtercityList=(List<City>) filterResults.values;
                Log.i("CityAdapter", "performFiltering: =======2:"+filtercityList);
                notifyDataSetChanged();

            }
        };
        return filter;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
    }
}
