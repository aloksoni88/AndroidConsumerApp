/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.SearchActivityOld;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.ProjectBuilderWrapper;
import com.clicbrics.consumer.wrapper.RecentSearch;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.AutocompletePrediction;
import com.google.android.libraries.places.compat.AutocompletePredictionBufferResponse;
import com.google.android.libraries.places.compat.GeoDataClient;

import java.util.ArrayList;


public class PlaceAutocompleteAdapter
        extends ArrayAdapter<PlaceAutocompleteAdapter.PlaceAutocomplete> implements
        Filterable{

    private static final String TAG = PlaceAutocompleteAdapter.class.getName();

    Context mContext;
    PlacesAutocompleteInterface mListener;
    public ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
    private GeoDataClient geoDataClient;
    int resource;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;
    View zeroResults, rootLayout;
    private static LayoutInflater mLayoutInflator;
    Typeface typefaceNormal;
    Typeface typefaceBold;


    public interface PlacesAutocompleteInterface {
        void onPlaceClick(ArrayList<PlaceAutocomplete> mResultList, int position);
    }

    public PlaceAutocompleteAdapter(Context context, int resource, GeoDataClient geoDataClient,
                                    LatLngBounds bounds, AutocompleteFilter filter, View zeroResults, View rootLayout) {
        super(context, resource);
        this.mContext = context;
        this.geoDataClient = geoDataClient;
        mBounds = bounds;
        mPlaceFilter = filter;
        this.mListener = (PlacesAutocompleteInterface) mContext;
        this.zeroResults = zeroResults;
        this.rootLayout = rootLayout;
        this.resource = resource;
        this.mLayoutInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        typefaceNormal = Typeface.createFromAsset(mContext.getAssets(), "fonts/FiraSans-Light.ttf");
        typefaceBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/FiraSans-Medium.ttf");
    }

    /*
    Clear List items
     */
    public void clearList() {
        if (mResultList != null ) {
            mResultList.clear();
        }
    }

    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    public void onAutocompleteSearch(String constraint){
        if (constraint != null && !TextUtils.isEmpty(constraint.trim())) {
            String searchString = constraint;
            constraint = getConstraint(searchString);
            final ArrayList<PlaceAutocomplete> builderSearchList = new ArrayList<>();
            final ArrayList<PlaceAutocomplete> projectSearchList = new ArrayList<>();
            if(mContext != null && ((SearchActivityOld)mContext).projectBuilderListWrapper != null
                    && ((SearchActivityOld)mContext).projectBuilderListWrapper.size() > 0) {
                int size = ((SearchActivityOld)mContext).projectBuilderListWrapper.size();
                for (int i = 0; i < size ; i++){
                    ProjectBuilderWrapper wrapper = ((SearchActivityOld)mContext).projectBuilderListWrapper.get(i);
                    if(wrapper.builderName.toUpperCase().contains(searchString.toUpperCase())){
                        String secondaryStr = wrapper.cityName;
                        if(builderSearchList != null && builderSearchList.size() > 0){
                            boolean isExist = false;
                            for(PlaceAutocomplete placeAutocomplete : builderSearchList){
                                if(placeAutocomplete.builderId == wrapper.builderId){
                                    isExist = true;
                                    break;
                                }
                            }
                            if(!isExist){
                                PlaceAutocomplete builderWrapper = new PlaceAutocomplete("",wrapper.builderName,
                                        secondaryStr,mContext.getResources().getString(R.string.developer),-1,-1, wrapper.builderId,
                                        wrapper.projectId,wrapper.cityId,wrapper.cityName,wrapper.builderName,false);
                                builderSearchList.add(builderWrapper);
                            }
                        }else{
                            PlaceAutocomplete builderWrapper = new PlaceAutocomplete("",wrapper.builderName,
                                    secondaryStr,mContext.getResources().getString(R.string.developer),-1,-1, wrapper.builderId,
                                    wrapper.projectId,wrapper.cityId,wrapper.cityName,wrapper.builderName,false);
                            builderSearchList.add(builderWrapper);
                        }
                    }
                    if(wrapper.projectName.toUpperCase().contains(searchString.toUpperCase())){
                        PlaceAutocomplete builderWrapper = new PlaceAutocomplete("",wrapper.projectName,
                                wrapper.address,mContext.getResources().getString(R.string.project),-1,-1, wrapper.builderId,
                                wrapper.projectId,wrapper.cityId,wrapper.cityName,wrapper.builderName,false);
                        projectSearchList.add(builderWrapper);
                    }
                }
                
                Task<AutocompletePredictionBufferResponse> predictionRes = geoDataClient.getAutocompletePredictions(constraint.toString(), mBounds, mPlaceFilter);
                predictionRes.addOnSuccessListener(new OnSuccessListener<AutocompletePredictionBufferResponse>() {
                    @Override
                    public void onSuccess(AutocompletePredictionBufferResponse autocompletePredictions) {
                        Log.i(TAG, "onSuccess: autocomplete response");
                        ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
                        if(builderSearchList != null && builderSearchList.size() > 0){
                            resultList.addAll(builderSearchList);
                        }
                        if(projectSearchList != null && projectSearchList.size() > 0){
                            resultList.addAll(projectSearchList);
                        }
                        for(AutocompletePrediction prediction : autocompletePredictions){
                            if(prediction != null){
                                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(null)
                                        , prediction.getSecondaryText(null),mContext.getResources().getString(R.string.locality),0,0,0,0,0,"","",false));
                            }
                        }
                        if(resultList != null && resultList.size() > 0) {
                            if (mContext != null && ((SearchActivityOld) mContext).recentSearchList != null
                                    &&((SearchActivityOld) mContext).recentSearchList.size() > 0) {
                                int size = ((SearchActivityOld) mContext).recentSearchList.size();
                                PlaceAutocomplete pA = new PlaceAutocomplete("",
                                        "", mContext.getResources().getString(R.string.recent), "", -1, -1, -1, -1, -1, "", "", true);
                                resultList.add(pA);
                                for (int i = 0; i < size; i++) {
                                    PlaceAutocomplete placeAutocomplete = new PlaceAutocomplete(
                                            "",
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).primaryText,
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).secondaryText,
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).searchType,
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).latLng.latitude,
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).latLng.longitude,
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).builderId,
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).projectId,
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).cityId,
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).cityName,
                                            ((SearchActivityOld) mContext).recentSearchList.get(i).builderName,
                                            true);
                                    resultList.add(placeAutocomplete);
                                }
                            }
                        }
                        if(mResultList != null){
                            mResultList.clear();
                        }
                        mResultList.addAll(resultList);
                        refreshList(resultList);
                    }
                });
                predictionRes.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: autocomplete response");
                        ArrayList resultList = new ArrayList();
                        if(builderSearchList != null && builderSearchList.size() > 0){
                            resultList.addAll(builderSearchList);
                        }
                        if(projectSearchList != null && projectSearchList.size() > 0){
                            resultList.addAll(projectSearchList);
                        }
                        if(mResultList != null){
                            mResultList.clear();
                        }else{
                            mResultList = new ArrayList<>();
                        }
                        mResultList.addAll(resultList);
                        refreshList(mResultList);
                    }
                });
            }
        }
    }


    private void refreshList(ArrayList results){
        if (results != null){// && results.count > 0) {
            // The API returned at least one result, update the data.
            try {
                if(results.size() == 0){
                    if(UtilityMethods.isInternetConnected(mContext)) {
                        zeroResults.setVisibility(View.VISIBLE);
                        if(((SearchActivityOld)mContext).recentSearchList != null
                                && ((SearchActivityOld)mContext).recentSearchList.size() > 0) {
                            if (((SearchActivityOld)mContext).recentSearchList.size() > 0) {
                                showRecentSearch(((SearchActivityOld)mContext).recentSearchList);
                            }
                        }
                    } else {
                        zeroResults.setVisibility(View.GONE);
                        showNetworkErrorSnackBar();
                    }
                    notifyDataSetChanged();
                }else{
                    zeroResults.setVisibility(View.GONE);
                    notifyDataSetChanged();
                }
                /*if(results.size() > 0) {
                    notifyDataSetChanged();
                    //setNotifyOnChange(true);
                    zeroResults.setVisibility(View.GONE);
                }
                else if(results.size()==0){
                    if(UtilityMethods.isInternetConnected(mContext)) {
                        zeroResults.setVisibility(View.VISIBLE);
                        if(((SearchActivityOld)mContext).recentSearchList != null
                                && ((SearchActivityOld)mContext).recentSearchList.size() > 0) {
                            if (((SearchActivityOld)mContext).recentSearchList.size() > 0) {
                                showRecentSearch(((SearchActivityOld)mContext).recentSearchList);
                            }
                        }
                    } else {
                        zeroResults.setVisibility(View.GONE);
                        showNetworkErrorSnackBar();
                    }
                    notifyDataSetChanged();
                    //notifyDataSetInvalidated();
                } else {
                    zeroResults.setVisibility(View.GONE);
                    //notifyDataSetInvalidated();
                    notifyDataSetChanged();
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if(((SearchActivityOld)mContext).recentSearchList != null
                    && ((SearchActivityOld)mContext).recentSearchList.size() > 0) {
                if (((SearchActivityOld)mContext).recentSearchList.size() > 0) {
                    showRecentSearch(((SearchActivityOld)mContext).recentSearchList);
                }
            }
        }
    }

    private String getConstraint(String constraint){
        String appendStr = "";
        if(!UtilityMethods.getStringInPref(mContext, Constants.AppConstants.SAVED_PNAME, "").isEmpty()){
            appendStr = UtilityMethods.getStringInPref(mContext, Constants.AppConstants.SAVED_PNAME, "");
        }else {
            if (!UtilityMethods.getStringInPref(mContext, Constants.AppConstants.SAVED_CITY, "").isEmpty()) {
                appendStr = UtilityMethods.getStringInPref(mContext, Constants.AppConstants.SAVED_CITY, "");
            }
            if (!UtilityMethods.getStringInPref(mContext, Constants.AppConstants.SAVED_STATE, "").isEmpty()) {
                if (!appendStr.isEmpty()) {
                    appendStr = appendStr + " " + UtilityMethods.getStringInPref(mContext, Constants.AppConstants.SAVED_STATE, "");
                } else {
                    appendStr = UtilityMethods.getStringInPref(mContext, Constants.AppConstants.SAVED_STATE, "");
                }
            }

        }
        if(!appendStr.isEmpty()){
            constraint = appendStr + " " + constraint;
        }
        return constraint;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        try {
            if (convertView == null) {
                holder = new Holder();
                convertView = mLayoutInflator.inflate(R.layout.item_search_list, parent, false);
                holder.primaryText = (TextView) convertView.findViewById(R.id.address);
                holder.secondaryText = (TextView) convertView.findViewById(R.id.address_detail);
                holder.searchType = (TextView) convertView.findViewById(R.id.type);
                //holder.mSelectionLayout = (LinearLayout) convertView.findViewById(R.id.id_focused_bg_layout);
                holder.imageView = (ImageView) convertView.findViewById(R.id.powered_by_google);
                holder.containerLayout = (LinearLayout) convertView.findViewById(R.id.id_container_layout);
                convertView.setTag(holder);
            }else{
                holder = (Holder) convertView.getTag();
            }

            PlaceAutocomplete placeAutocomplete = mResultList.get(position);
            holder.primaryText.setText(placeAutocomplete.primaryText);
            holder.secondaryText.setText(placeAutocomplete.secondaryText);
            /*if(!TextUtils.isEmpty(placeAutocomplete.cityName) &&
                    !placeAutocomplete.secondaryText.toString().contains(placeAutocomplete.cityName)){
                holder.secondaryText.setText(placeAutocomplete.secondaryText + ", " + placeAutocomplete.cityName);
            }else{
                holder.secondaryText.setText(placeAutocomplete.secondaryText);
            }*/
            holder.searchType.setText(placeAutocomplete.searchType);
            //UtilityMethods.setTextViewColor(mContext,holder.secondaryText,R.color.search_text_color);
            if(placeAutocomplete.secondaryText.toString().equalsIgnoreCase(mContext.getResources().getString(R.string.recent))){
                holder.secondaryText.setTypeface(typefaceBold);
                holder.secondaryText.setTextSize(18);
                holder.searchType.setVisibility(View.GONE);
            }else{
                holder.secondaryText.setTextSize(14);
                holder.secondaryText.setTypeface(typefaceNormal);
                holder.searchType.setVisibility(View.VISIBLE);
            }
            if(position == mResultList.size()-1){
                holder.imageView.setVisibility(View.VISIBLE);
            }
            else
                {
                holder.imageView.setVisibility(View.GONE);
                }
            /*if(position%2==0){
                UtilityMethods.setDrawableBackground(mContext,holder.containerLayout,R.color.transparent);
            }else{
                if(!placeAutocomplete.secondaryText.toString().equalsIgnoreCase("Recent")) {
                    UtilityMethods.setDrawableBackground(mContext, holder.containerLayout, R.color.emi_item_gray);
                }else{
                    UtilityMethods.setDrawableBackground(mContext,holder.containerLayout,R.color.transparent);
                }
            }*/

            holder.containerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mResultList != null && mResultList.size() > position) {
                        if (mResultList.get(position).secondaryText != null &&
                                !mResultList.get(position).secondaryText.toString()
                                        .equalsIgnoreCase(mContext.getResources().getString(R.string.recent))) {
                            mListener.onPlaceClick(mResultList, position);
                        }

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public class Holder {
        TextView primaryText;
        TextView secondaryText;
        TextView searchType;
        //LinearLayout mSelectionLayout;
        ImageView imageView;
        LinearLayout containerLayout;
    }

    @Override
    public int getCount() {
        if(mResultList != null){
            return  mResultList.size();
        }else{
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence primaryText;
        public CharSequence secondaryText;
        public CharSequence searchType;
        public double latitude;
        public double longitude;
        public long builderId;
        public long projectId;
        public long cityId;
        public String cityName;
        public String builderName;
        public boolean isRecent;
        /*PlaceAutocomplete(CharSequence placeId, CharSequence primaryText, CharSequence secondaryText,
                          CharSequence searchType) {
            this.placeId = placeId;
            this.primaryText = primaryText;
            this.secondaryText = secondaryText;
            this.searchType = searchType;
        }*/

        PlaceAutocomplete(CharSequence placeId, CharSequence primaryText, CharSequence secondaryText,
                          CharSequence searchType, double latitude, double longitude, long builderId,
                          long projectId,long cityId, String cityName, String builderName, boolean isRecent) {
            this.placeId = placeId;
            this.primaryText = primaryText;
            this.secondaryText = secondaryText;
            this.searchType = searchType;
            this.builderId = builderId;
            this.projectId = projectId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.cityId = cityId;
            this.cityName = cityName;
            this.builderName = builderName;
            this.isRecent = isRecent;
        }
    }

    public void showRecentSearch(ArrayList<RecentSearch> recentSearches){
        int size = recentSearches.size();
        PlaceAutocomplete pA = new PlaceAutocomplete("",
                "", mContext.getResources().getString(R.string.recent), "", -1, -1, -1, -1, -1, "", "", true);
        if(mResultList == null){
            mResultList = new ArrayList<>();
        }else{
            clearList();
        }
        mResultList.add(pA);
        for(int i=0; i<size; i++){
            PlaceAutocompleteAdapter.PlaceAutocomplete placeAutocomplete = new PlaceAutocompleteAdapter.PlaceAutocomplete("",
                    recentSearches.get(i).primaryText,
                    recentSearches.get(i).secondaryText,
                    recentSearches.get(i).searchType,
                    recentSearches.get(i).latLng.latitude,
                    recentSearches.get(i).latLng.longitude,
                    recentSearches.get(i).builderId,
                    recentSearches.get(i).projectId,
                    recentSearches.get(i).cityId,
                    recentSearches.get(i).cityName,
                    recentSearches.get(i).builderName,
                    true);
            mResultList.add(placeAutocomplete);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void showNetworkErrorSnackBar() {
        final Snackbar snackbar = Snackbar.make(rootLayout, mContext.getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.uber_red));
        snackbar.setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
        /*snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });*/
        snackbar.show();
    }
}

/*
public class PlaceAutocompleteAdapter
        extends ArrayAdapter<PlaceAutocompleteAdapter.PlaceAutocomplete> implements Filterable {

    private static final String TAG = PlaceAutocompleteAdapter.class.getName();
    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<PlaceAutocomplete>();
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;
    int resource;
    private int zoomLevel = 5;

    public PlaceAutocompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter) {
        super(context, resource);
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
        this.resource = resource;
        mResultList = ((HousingApplication) context.getApplicationContext()).getRecent();
    }

    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(resource, parent, false);
            }
            TextView addr_header = (TextView) convertView.findViewById(R.id.addr_header);
            TextView addr_description = (TextView) convertView.findViewById(
                    R.id.addr_description);
            String str = mResultList.get(position).description.toString();
            String[] addrList = str.split(",");
            Log.d(TAG,"str->"+str);
            if (addrList != null) {
                try {
                    addr_header.setText(addrList[0]);
                    addr_description.setText(str.substring(addrList[0].length() +1));
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    } else {
                        mResultList = new ArrayList<PlaceAutocomplete>();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Log.i(TAG, "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    null, mPlaceFilter);


            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
//                Toast.makeText(getContext(), "Error contacting API: " + status.toString(),
//                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList<PlaceAutocomplete> resultList = new ArrayList<PlaceAutocomplete>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();

                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                Log.d(TAG, "prediction->" + prediction.getPlaceId() + " " +
                        prediction.getFullText(null));
                for (int i = 0; i < prediction.getPlaceTypes().size(); i++) {
                    Log.d(TAG, "type->" + prediction.getPlaceTypes().get(i));
                }
                if (prediction.getPlaceTypes().contains(1009) && prediction.getPlaceTypes().contains(1012)) {
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                            prediction.getFullText(null), 10));
                } else {
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                            prediction.getFullText(null), 5));
                }

            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();

            return resultList;
        }
        Log.e(TAG, "Google API client is not connected for autocomplete query.");
        return null;
    }

    public static class PlaceAutocomplete implements Serializable {

        private static final long serialVersionUID = 0L;
        private CharSequence placeId;
        private CharSequence description;
        private int zoomLevel = 5;

        PlaceAutocomplete(CharSequence placeId, CharSequence description, int zoomLevel) {
            this.placeId = placeId;
            this.description = description;
            this.zoomLevel = zoomLevel;
        }

        @Override
        public String toString() {
            return description.toString();
        }

        public CharSequence getPlaceId() {
            return placeId;
        }

        public void setPlaceId(CharSequence placeId) {
            this.placeId = placeId;
        }

        public CharSequence getDescription() {
            return description;
        }

        public void setDescription(CharSequence description) {
            this.description = description;
        }

        public void setZoomLevel(int zoomLevel) {
            this.zoomLevel = zoomLevel;
        }

        public int getZoomLevel() {
            return zoomLevel;
        }
    }
}
*/
