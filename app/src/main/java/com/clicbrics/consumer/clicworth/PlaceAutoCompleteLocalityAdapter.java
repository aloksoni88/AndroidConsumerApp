package com.clicbrics.consumer.clicworth;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
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

public class PlaceAutoCompleteLocalityAdapter
        extends ArrayAdapter<PlaceAutoCompleteLocalityAdapter.PlaceAutocomplete> implements
        Filterable {

    private static final String TAG = PlaceAutoCompleteLocalityAdapter.class.getName();

    Context mContext;
    PlacesAutocompleteLocalityInterface mListener;
    public ArrayList<PlaceAutocomplete> mResultList=new ArrayList<>();
    private GeoDataClient geoDataClient;
    int resource;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;
    View zeroResults, rootLayout;
    private static LayoutInflater mLayoutInflator;
    Typeface typefaceNormal;
    Typeface typefaceBold;


    public interface PlacesAutocompleteLocalityInterface {
        public void onPlaceClick(ArrayList<PlaceAutocomplete> mResultList, int position);
    }

    public PlaceAutoCompleteLocalityAdapter(Context context, int resource, GeoDataClient geoDataClient,
                                    LatLngBounds bounds, AutocompleteFilter filter, View zeroResults, View rootLayout) {
        super(context, resource);
        this.mContext = context;
        this.geoDataClient = geoDataClient;
        mBounds = bounds;
        mPlaceFilter = filter;
        this.mListener = (PlacesAutocompleteLocalityInterface) mContext;
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
        if (constraint != null && constraint.length() !=0) {
            //constraint = getConstraint(constraint);
            Task<AutocompletePredictionBufferResponse> predictionRes = geoDataClient
                    .getAutocompletePredictions(constraint, mBounds, mPlaceFilter);
            predictionRes.addOnSuccessListener(new OnSuccessListener<AutocompletePredictionBufferResponse>() {
                @Override
                public void onSuccess(AutocompletePredictionBufferResponse autocompletePredictions) {
                    ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
                    for(AutocompletePrediction prediction : autocompletePredictions){
                        if(prediction != null){
                            resultList.add(new PlaceAutoCompleteLocalityAdapter.PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(null)
                                    , prediction.getSecondaryText(null),mContext.getResources().getString(R.string.locality),0,0,0,0,0,"","",false));
                        }
                    }
                    if(mResultList != null){
                        mResultList.clear();
                    }else{
                        mResultList = new ArrayList<>();
                    }
                    mResultList.addAll(resultList);
                    if(mResultList.size() == 0){
                        if(!UtilityMethods.isInternetConnected(mContext)) {
                            showNetworkErrorSnackBar();
                        }
//                        zeroResults.setVisibility(View.VISIBLE);
                    }
//                    else
//                    {
//                        zeroResults.setVisibility(View.VISIBLE);
//                    }
                    notifyDataSetChanged();
                }
            });

            predictionRes.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private String getConstraint(String constraint){
        String appendStr = "";
        if(!UtilityMethods.getStringInPref(mContext, Constants.AppConstants.ESTIMATE_PNAME, "").isEmpty()){
            appendStr = UtilityMethods.getStringInPref(mContext, Constants.AppConstants.ESTIMATE_PNAME, "");
        }else {
            if (!UtilityMethods.getStringInPref(mContext, Constants.AppConstants.ESTIMATE_CITY, "").isEmpty()) {
                appendStr = UtilityMethods.getStringInPref(mContext, Constants.AppConstants.ESTIMATE_CITY, "");
            }
            if (!UtilityMethods.getStringInPref(mContext, Constants.AppConstants.ESTIMATE_STATE, "").isEmpty()) {
                if (!appendStr.isEmpty()) {
                    appendStr = appendStr + " " + UtilityMethods.getStringInPref(mContext, Constants.AppConstants.ESTIMATE_STATE, "");
                } else {
                    appendStr = UtilityMethods.getStringInPref(mContext, Constants.AppConstants.ESTIMATE_STATE, "");
                }
            }

        }
        if(!appendStr.isEmpty()){
            constraint = appendStr + " " + constraint;
        }
        Log.i(TAG, "Starting autocomplete query for: " + constraint);
        return constraint;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PlaceAutoCompleteLocalityAdapter.Holder holder = null;
        try {
            if (convertView == null) {
                holder = new PlaceAutoCompleteLocalityAdapter.Holder();
                convertView = mLayoutInflator.inflate(R.layout.item_search_list, parent, false);
                holder.primaryText = (TextView) convertView.findViewById(R.id.address);
                holder.secondaryText = (TextView) convertView.findViewById(R.id.address_detail);
                holder.searchType = (TextView) convertView.findViewById(R.id.type);

                holder.imageView = (ImageView) convertView.findViewById(R.id.powered_by_google);
                holder.containerLayout = (LinearLayout) convertView.findViewById(R.id.id_container_layout);
                convertView.setTag(holder);
            }else{
                holder = (PlaceAutoCompleteLocalityAdapter.Holder) convertView.getTag();
            }

            PlaceAutoCompleteLocalityAdapter.PlaceAutocomplete placeAutocomplete = mResultList.get(position);
            holder.primaryText.setText(placeAutocomplete.primaryText);
            holder.secondaryText.setText(placeAutocomplete.secondaryText);

            holder.searchType.setText(placeAutocomplete.searchType);

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
            }else{
                holder.imageView.setVisibility(View.GONE);
            }

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

    public PlaceAutoCompleteLocalityAdapter.PlaceAutocomplete getItem(int position) {
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
        PlaceAutoCompleteLocalityAdapter.PlaceAutocomplete pA = new PlaceAutoCompleteLocalityAdapter.PlaceAutocomplete("",
                "", mContext.getResources().getString(R.string.recent), "", -1, -1, -1, -1, -1, "", "", true);
        if(mResultList == null){
            mResultList = new ArrayList<>();
        }else{
            clearList();
        }
        mResultList.add(pA);
        for(int i=0; i<size; i++){
            PlaceAutoCompleteLocalityAdapter.PlaceAutocomplete placeAutocomplete = new PlaceAutoCompleteLocalityAdapter.PlaceAutocomplete("",
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
        snackbar.show();
    }
}

