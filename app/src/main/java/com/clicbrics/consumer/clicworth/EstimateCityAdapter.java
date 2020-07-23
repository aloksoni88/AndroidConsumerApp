package com.clicbrics.consumer.clicworth;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.model.City;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.List;

public class EstimateCityAdapter extends BaseAdapter {

    Context mContext;
    List<City> cityList;
    private Typeface light,bold;
    private ClicworthFragment clicworthFragment;


    public EstimateCityAdapter(Context context, List<City> cityList,ClicworthFragment clicworthFragment){
        mContext = context;
        this.cityList = cityList;
        light = Typeface.createFromAsset(mContext.getAssets(), "fonts/FiraSans-Light.ttf");
        bold = Typeface.createFromAsset(mContext.getAssets(), "fonts/FiraSans-Bold.ttf");
        this.clicworthFragment = clicworthFragment;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Object getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_city_list, parent, false);
        }
        TextView cityName = (TextView)convertView.findViewById(R.id.city_name);
        final String city = cityList.get(position).getName();
        cityName.setText(city);

        if(city.equals(UtilityMethods.getStringInPref(mContext,
                Constants.AppConstants.ESTIMATE_CITY, ""))){
            cityName.setTextColor(mContext.getResources().getColor(R.color.black));
            cityName.setTypeface(bold);
        }else {
            cityName.setTextColor(mContext.getResources().getColor(R.color.text_color_login_reg));
            cityName.setTypeface(light);
        }

        cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cityList.get(position)!=null && clicworthFragment != null){
                    clicworthFragment.onCitySelect(cityList.get(position));
                }
                //((HomeScreen)mContext).setSelectedCity(cityList.get(position));

            }
        });


        return convertView;
    }


}
