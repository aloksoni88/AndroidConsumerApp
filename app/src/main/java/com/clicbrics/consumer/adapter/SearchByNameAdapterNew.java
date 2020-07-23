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
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.model.ProjectsByname;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.clicbrics.consumer.wrapper.RecentSearch;
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

public class SearchByNameAdapterNew extends RecyclerView.Adapter<SearchByNameAdapterNew.MyViewHolder>

    {
    Activity mActivity;
    ArrayList<ProjectsByname> projectsBynames;
    GeoDataClient geoDataClient;
    public SearchByNameAdapterNew(Activity mActivity, ArrayList<ProjectsByname> projectsBynames, GeoDataClient geoDataClient) {
        this.mActivity=mActivity;
        this.projectsBynames=projectsBynames;
        this.geoDataClient=geoDataClient;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_list_new, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

//        holder.address_detail.setText(projectsBynames.get(position).getName());
        holder.address.setText(projectsBynames.get(position).getName());
        holder.type.setText(projectsBynames.get(position).getType());
        holder.imageView.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return projectsBynames.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView address,address_detail,type;
        ImageView imageView;
        View line;
        public MyViewHolder(View itemView) {
            super(itemView);

            address = (TextView) itemView.findViewById(R.id.address);
            address_detail = (TextView) itemView.findViewById(R.id.address_detail);
            type= (TextView) itemView.findViewById(R.id.type);
            line = (View) itemView.findViewById(R.id.line);
            line.setVisibility(View.VISIBLE);
            //holder.mSelectionLayout = (LinearLayout) convertView.findViewById(R.id.id_focused_bg_layout);
            imageView = (ImageView) itemView.findViewById(R.id.powered_by_google);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(projectsBynames.get(getAdapterPosition()).getType().equalsIgnoreCase("Developer"))
                    {
                        String primartyText = "", secondaryText = "";
                        if(projectsBynames.get(getAdapterPosition()).getName() != null) {
                            primartyText = projectsBynames.get(getAdapterPosition()).getName();
                        }
                       /* if(mResultList.get(position).secondaryText != null) {
                            secondaryText =  mResultList.get(position).secondaryText.toString();
                        }*/
                        long cityId = 0;
                        String cityName = "";
                       /* if(mResultList.get(position).cityId != 0) {
                            cityId = mResultList.get(position).cityId;
                        }else*/{
                        cityId = UtilityMethods.getLongInPref(mActivity, Constants.AppConstants.SAVED_CITY_ID,0);
                    }
                       /* if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                            cityName = mResultList.get(position).cityName;
                        }else*/{
                        cityName = UtilityMethods.getStringInPref(mActivity,Constants.AppConstants.SAVED_CITY,"");
                    }
                        Intent data = new Intent();
                    /*data.putExtra("lat", String.valueOf(mResultList.get(position).latitude));
                    data.putExtra("lng", String.valueOf(mResultList.get(position).longitude));*/
                        data.putExtra("lat", "");
                        data.putExtra("lng", "");
                        if(!TextUtils.isEmpty(primartyText)) {
                            data.putExtra("address", primartyText);
                        }
                        if(!TextUtils.isEmpty(secondaryText)) {
                            data.putExtra("additional_address", secondaryText);
                        }
                        Log.i("SearchByNameAdapter", "onClick: =========="+projectsBynames.get(getAdapterPosition()).getId()+
                                ";;;;"+projectsBynames.get(getAdapterPosition()).getName());
                        data.putExtra("builder_id",Long.parseLong(projectsBynames.get(getAdapterPosition()).getId()));
                        data.putExtra("builder_name",projectsBynames.get(getAdapterPosition()).getName());
                        data.putExtra("city_id",cityId);
                        data.putExtra("cityName",cityName);
                        data.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_DEVELOPER,true);
                        mActivity.setResult(Activity.RESULT_OK, data);
                        mActivity.finish();

                        addToRecentSearches(primartyText, secondaryText,
                                new LatLng(-1,-1), projectsBynames.get(getAdapterPosition()).getType(),
                                0,Long.parseLong(projectsBynames.get(getAdapterPosition()).getId()),
                                projectsBynames.get(getAdapterPosition()).getName(),cityId, cityName);
                    }
                    else if(projectsBynames.get(getAdapterPosition()).getType().equalsIgnoreCase("Project"))
                    {
                        String primartyText = "", secondaryText = "";
                        if(projectsBynames.get(getAdapterPosition()).getName() != null) {
                            primartyText = projectsBynames.get(getAdapterPosition()).getName().toString();
                        }
                        /*if(mResultList.get(position).secondaryText != null) {
                            secondaryText =  mResultList.get(position).secondaryText.toString();
                        }*/
                        Intent intent = new Intent(mActivity, ProjectDetailsScreen.class);
                        intent.putExtra("ISDirectCall",true);
                        intent.putExtra("isProjectSearch",true);
                        long cityId = 0;
                        String cityName = "";
                       /* if(mResultList.get(position).cityId != 0) {
                            cityId = mResultList.get(position).cityId;
                        }else*/{
                        cityId = UtilityMethods.getLongInPref(mActivity,Constants.AppConstants.SAVED_CITY_ID,0);
                    }
                        /*if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                            cityName = mResultList.get(position).cityName;
                        }else*/{
                        cityName = UtilityMethods.getStringInPref(mActivity,Constants.AppConstants.SAVED_CITY,"");
                    }
                        intent.putExtra("city_id", cityId);
                        intent.putExtra("cityName", cityName);
                        Log.i("SearchAdapter", "onClick: ======="+projectsBynames.get(getAdapterPosition()).getId());
                        intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,Long.parseLong(projectsBynames.get(getAdapterPosition()).getId()));
                        mActivity.startActivity(intent);
                        mActivity.finish();

                        addToRecentSearches(primartyText, secondaryText,
                                new LatLng(-1,-1), projectsBynames.get(getAdapterPosition()).getType().toString(),
                                Long.parseLong(projectsBynames.get(getAdapterPosition()).getId()),-1,
                                "",cityId,
                                cityName);
                    }
                    else  if(projectsBynames.get(getAdapterPosition()).getType().equalsIgnoreCase("Locality"))
                    {


                        getlatlong(projectsBynames.get(getAdapterPosition()));
                    }

                }
            });
        }
    }

    private void getlatlong(final ProjectsByname position) {

        geoDataClient.getPlaceById(""+position.getId()).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
            @Override
            public void onSuccess(PlaceBufferResponse places) {
                Log.i("SearchByNameAdapter", "onSuccess: ====="+position.getName());
//                Log.i(TAG, "onSuccess: ====="+projectsBynames.get(position).secondaryText);
                Log.i("SearchByNameAdapter", "onSuccess: ====="+places.getCount());
                if(places.getCount() == 1){
                    String primartyText = "", secondaryText = "";
                    if(position.getName() != null) {
                        primartyText = position.getName().toString();
                    }
                   /* if(mResultList.get(position).secondaryText != null) {
                        secondaryText =  mResultList.get(position).secondaryText.toString();
                    }*/
                    Intent data = new Intent();
                    data.putExtra("lat", String.valueOf(places.get(0).getLatLng().latitude));
                    data.putExtra("lng", String.valueOf(places.get(0).getLatLng().longitude));
                    long cityId = 0;
                    String cityName = "";
                    /*if(mResultList.get(position).cityId != 0) {
                        cityId = mResultList.get(position).cityId;
                    }else*/{
                        cityId = UtilityMethods.getLongInPref(mActivity,Constants.AppConstants.SAVED_CITY_ID,0);
                    }
                   /* if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                        cityName = mResultList.get(position).cityName;
                    }else*/{
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
                            places.get(0).getLatLng(),position.getType().toString(),
                            0,-1,
                            "",cityId, cityName);


                }
                else {
                    if (Geocoder.isPresent()) {
                        try {
                            String primartyText = "", secondaryText = "";
                            if(position.getName() != null) {
                                primartyText = position.getName().toString();
                            }
                           /* if(mResultList.get(position).secondaryText != null) {
                                secondaryText =  mResultList.get(position).secondaryText.toString();
                            }*/
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
                                    /*if(mResultList.get(position).cityId != 0) {
                                        cityId = mResultList.get(position).cityId;
                                    }else*/{
                                        cityId = UtilityMethods.getLongInPref(mActivity,Constants.AppConstants.SAVED_CITY_ID,0);
                                    }
                                   /* if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                                        cityName = mResultList.get(position).cityName;
                                    }else*/{
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
                                            position.getType(),
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
