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

public class PlaceAutocompleteAdapterNew extends ArrayAdapter<PlaceAutocompleteAdapterNew.PlaceAutocomplete> implements
        Filterable {

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

    public PlaceAutocompleteAdapterNew(Context context, int resource, GeoDataClient geoDataClient,
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
            PlaceAutocompleteAdapterNew.PlaceAutocomplete placeAutocomplete = new PlaceAutocompleteAdapterNew.PlaceAutocomplete("",
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

