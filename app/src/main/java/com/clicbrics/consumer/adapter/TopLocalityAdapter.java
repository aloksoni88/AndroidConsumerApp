package com.clicbrics.consumer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.RecentSearch;
import com.clicbrics.consumer.wrapper.TopLocalityModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.PlaceBufferResponse;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TopLocalityAdapter extends RecyclerView.Adapter<TopLocalityAdapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<TopLocalityModel> topLocalityArraylist;
    private GeoDataClient geoDataClient;
    private int size;
    public TopLocalityAdapter(Activity mActivity, ArrayList<TopLocalityModel> topLocalityArraylist, int size, GeoDataClient geoDataClient) {
        this.mActivity=mActivity;
//        this.topLocalityArraylist=topLocalityArraylist;
        this.topLocalityArraylist=topLocalityArraylist;
        this.geoDataClient=geoDataClient;
        this.size=size;
    }

    @NonNull
    @Override
    public TopLocalityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_locality_row_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopLocalityAdapter.MyViewHolder holder, int position) {
        holder.cityname.setText(topLocalityArraylist.get(position).cityName);
        holder.locality.setText(topLocalityArraylist.get(position).name);

    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView locality,cityname;
        public MyViewHolder(View itemView) {
            super(itemView);
            locality=itemView.findViewById(R.id.locality);
            cityname=itemView.findViewById(R.id.cityname);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EventAnalyticsHelper().ItemClickEvent(mActivity, Constants.AnaylticsClassName.SearchScreen,
                            topLocalityArraylist.get(getAdapterPosition()), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.SelectTopLocality.toString());
                    getlatlong(topLocalityArraylist.get(getAdapterPosition()));
                }
            });
        }
    }

    private void getlatlong(final TopLocalityModel position) {

        geoDataClient.getPlaceById(""+position.id).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
            @Override
            public void onSuccess(PlaceBufferResponse places) {
                Log.i("SearchByNameAdapter", "onSuccess: ====="+position.name);
//                Log.i(TAG, "onSuccess: ====="+projectsBynames.get(position).secondaryText);
                Log.i("SearchByNameAdapter", "onSuccess: ====="+places.getCount());
                if(places.getCount() == 1){
                    String primartyText = "", secondaryText = "";
                    if(position.name != null) {
                        primartyText = position.name.toString();
                    }
                    if(position.cityName != null) {
                        secondaryText =  position.cityName.toString();
                    }
                    Intent data = new Intent();
                    data.putExtra("lat", String.valueOf(places.get(0).getLatLng().latitude));
                    data.putExtra("lng", String.valueOf(places.get(0).getLatLng().longitude));
                    long cityId = 0;
                    String cityName = "";
                    if(position.cityId != 0) {
                        cityId = position.cityId;
                    }else{
                        cityId = UtilityMethods.getLongInPref(mActivity, Constants.AppConstants.SAVED_CITY_ID,0);
                    }
                    if(!TextUtils.isEmpty(position.cityName)) {
                        cityName = position.cityName;
                    }else{
                        cityName = UtilityMethods.getStringInPref(mActivity,Constants.AppConstants.SAVED_CITY,"");
                    }
                    data.putExtra("city_id", cityId);
                    data.putExtra("cityName", cityName);
                    if(!TextUtils.isEmpty(primartyText)) {
                        data.putExtra("address", primartyText);
                    }
                    if(!TextUtils.isEmpty(secondaryText)) {
                        data.putExtra("additional_address", secondaryText);
                    }
                    data.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION,true);
                    mActivity.setResult(Activity.RESULT_OK, data);
                    mActivity.finish();

                    addToRecentSearches(primartyText, secondaryText,
                            places.get(0).getLatLng(),"Locality",
                            0,-1,
                            "",cityId, cityName);


                }
                else {
                    if (Geocoder.isPresent()) {
                        try {
                            String primartyText = "", secondaryText = "";
                            if(position.name != null) {
                                primartyText = position.name.toString();
                            }
                            if(position.cityName != null) {
                                secondaryText =  position.cityName.toString();
                            }
                            String location = UtilityMethods.getStringInPref(mActivity, Constants.AppConstants.SAVED_CITY, "");
                            Geocoder gc = new Geocoder(mActivity);
                            List<Address> addresses = gc.getFromLocationName(location, 1); // get the found Address Objects

                            if (addresses != null && !addresses.isEmpty()) {
                                List<LatLng> latlng = new ArrayList<LatLng>(addresses.size());
                                for (Address address : addresses) {
                                    if (address != null && address.hasLatitude() && address.hasLongitude()) {
                                        latlng.add(new LatLng(address.getLatitude(), address.getLongitude()));
                                    }
                                }
                                if (latlng != null && !latlng.isEmpty()) {
                                    Intent data = new Intent();
                                    data.putExtra("lat", String.valueOf(latlng.get(0).latitude));
                                    data.putExtra("lng", String.valueOf(latlng.get(0).longitude));
                                    long cityId = 0;
                                    String cityName = "";
                                    if(position.cityId != 0) {
                                        cityId = position.cityId;
                                    }else{
                                        cityId = UtilityMethods.getLongInPref(mActivity,Constants.AppConstants.SAVED_CITY_ID,0);
                                    }
                                    if(!TextUtils.isEmpty(position.cityName)) {
                                        cityName = position.cityName;
                                    }else{
                                        cityName = UtilityMethods.getStringInPref(mActivity, Constants.AppConstants.SAVED_CITY,"");
                                    }
                                    data.putExtra("city_id", cityId);
                                    data.putExtra("cityName", cityName);
                                    if(!TextUtils.isEmpty(primartyText)) {
                                        data.putExtra("address", primartyText);
                                    }
                                    if(!TextUtils.isEmpty(secondaryText)) {
                                        data.putExtra("additional_address", secondaryText);
                                    }
                                    data.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION,true);
                                    mActivity.setResult(Activity.RESULT_OK, data);
                                    mActivity.finish();

                                    addToRecentSearches(primartyText, secondaryText,
                                            new LatLng(latlng.get(0).latitude, latlng.get(0).longitude),
                                            "Locality",
                                            0,-1,
                                            "",cityId, cityName);
                                } else {
                                    Toast.makeText(mActivity, "results not found!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mActivity, "results not found!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(mActivity, "results not found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mActivity, "results not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void addToRecentSearches(String primaryText, String secondaryText, LatLng latLng, String searchType,
                                     long projectId, long builderId, String builderName, long cityId, String cityName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        Gson gson = new Gson();
        String json = prefs.getString("recent_search", "");
        Type type = new TypeToken<List<RecentSearch>>() {
        }.getType();
        List<RecentSearch> recentSearches = gson.fromJson(json, type);

        if (recentSearches == null || recentSearches.isEmpty()) {
            recentSearches = new ArrayList<>();
        }

        RecentSearch recentSearch = new RecentSearch(primaryText, secondaryText, latLng, System.currentTimeMillis(),searchType,
                projectId,builderId,builderName,cityId,cityName);
        for (int i = 0; i < recentSearches.size(); i++) {
            /*if ((recentSearches.get(i).latLng.equals(latLng)) ||
                    ((recentSearches.get(i).primaryText.equals(primaryText)) && (recentSearches.get(i).secondaryText.equals(secondaryText)))) {
                recentSearches.remove(i);
            }*/
            if (((recentSearches.get(i).primaryText.equals(primaryText)) && (recentSearches.get(i).secondaryText.equals(secondaryText)))) {
                recentSearches.remove(i);
            }
        }
        recentSearches.add(recentSearch);
        if (recentSearches.size() > 3) {
            recentSearches.remove(0);
        }

        SharedPreferences.Editor prefsEditor = prefs.edit();
        gson = new Gson();
        json = gson.toJson(recentSearches);
        prefsEditor.putString("recent_search", json);
        prefsEditor.commit();
    }
}
