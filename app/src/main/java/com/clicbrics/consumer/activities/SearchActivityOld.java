package com.clicbrics.consumer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.PlaceAutocompleteAdapter;
import com.clicbrics.consumer.adapter.RecentSearchAdapter;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.clicbrics.consumer.wrapper.ProjectBuilderWrapper;
import com.clicbrics.consumer.wrapper.RecentSearch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.PlaceBufferResponse;
import com.google.android.libraries.places.compat.Places;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SearchActivityOld extends BaseActivity implements PlaceAutocompleteAdapter.PlacesAutocompleteInterface,
        RecentSearchAdapter.RecentSearchInterface, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, View.OnClickListener{//, SavedPlaceListener {

    private static final String TAG = SearchActivityOld.class.getSimpleName();
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    private GeoDataClient geoDataClient;

    //private RecyclerView mRecyclerView, mRecentList;
    //private RecyclerView mRecentList;
    private ListView mRecyclerView;
    LinearLayoutManager llm, llm2;
    //PlacesAutoCompleteAdapter mAdapter;
    PlaceAutocompleteAdapter mAdapter;
    //List<SavedAddress> mSavedAddressList;
    //PlaceSavedAdapter mSavedAdapter;
    EditText mSearchEdittext;
    TextView recentTxt;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));

    long onStartTime;
    long mLastRequestTime;

    public ArrayList<ProjectBuilderWrapper> projectBuilderListWrapper;
    //public Map<String,Integer> builderMap;
    public ArrayList<RecentSearch> recentSearchList;
  //  private PlacesClient placesClient;

    @Override
    public void onStart() {
        //mGoogleApiClient.connect();
        onStartTime = System.currentTimeMillis();
        super.onStart();
    }

    @Override
    public void onStop() {
        //mGoogleApiClient.disconnect();
        TrackAnalytics.trackEvent("SearchResultSActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_old);
        UtilityMethods.setStatusBarColor(this,R.color.white);
        mContext = this;
        Toolbar toolbar = (Toolbar)findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();


        /*com.google.android.libraries.places.api.Places.initialize(this, getString(R.string.google_map_api_key));
        placesClient = com.google.android.libraries.places.api.Places.createClient(this);*/
        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 *//* clientId *//*, this)
                .addApi(Places.GEO_DATA_API)
                .build();*/
        geoDataClient= Places.getGeoDataClient(this);

        initViews();

        projectBuilderListWrapper = UtilityMethods.getBuilderAndProjectList(mContext);
        /*Log.i(TAG, "Project Builder List");
        for(int i=0; i<projectBuilderListWrapper.size(); i++){
            Log.i(TAG, projectBuilderListWrapper.get(i)+"");
        }*/
        //builderMap = UtilityMethods.getProjectCount(mContext);
        recentSearchList = getRecentlyViewed();

    }


    private class IOBLinearLayoutManager extends LinearLayoutManager{

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public IOBLinearLayoutManager(Context context) {
            super(context);
        }

        public IOBLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public IOBLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initViews(){
        //mRecyclerView = (RecyclerView)findViewById(R.id.list_search);
        mRecyclerView = (ListView) findViewById(R.id.list_search);
        //UtilityMethods.setListViewHeightBasedOnChildren(this,mRecyclerView);
        /*llm = new IOBLinearLayoutManager(SearchActivityOld.this);
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.setLayoutManager(llm);*/

        /*recentTxt = (TextView) findViewById(R.id.recent_txt);
        mRecentList = (RecyclerView) findViewById(R.id.list_recent_searches);
        //llm2 = new LinearLayoutManager(SearchActivityOld.this);
        llm2 = new IOBLinearLayoutManager(SearchActivityOld.this);
        mRecentList.setNestedScrollingEnabled(false);
        ((SimpleItemAnimator)mRecentList.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecentList.setLayoutManager(llm2);
        if((getRecentlyViewed()!=null) && (!getRecentlyViewed().isEmpty())){
            recentTxt.setVisibility(View.VISIBLE);
            mRecentList.setAdapter(new RecentSearchAdapter(SearchActivityOld.this, getRecentlyViewed()));

        } else {
            recentTxt.setVisibility(View.GONE);
        }*/

        mSearchEdittext = (EditText)findViewById(R.id.search_edit);

        /*if(!UtilityMethods.getStringInPref(SearchActivityOld.this, Constants.AppConstants.SAVED_CITY,"").isEmpty()){
            mSearchEdittext.setHint("Search in "+UtilityMethods.getStringInPref(SearchActivityOld.this, Constants.AppConstants.SAVED_CITY,""));
        }*/
        mSearchEdittext.setHint("Search by Locality, Project or Developer");
        //mClear = (ImageView)findViewById(R.id.clear);
        //mClear.setOnClickListener(this);
        final TextView zero_results = (TextView) findViewById(R.id.search_result_zero);
        /*mAdapter = new PlacesAutoCompleteAdapter(SearchActivityOld.this, R.layout.item_search_list,
                mGoogleApiClient, BOUNDS_INDIA, null, zero_results, findViewById(R.id.root_layout));*/
        mAdapter = new PlaceAutocompleteAdapter(SearchActivityOld.this, R.layout.item_search_list,
                geoDataClient, BOUNDS_INDIA, null, zero_results, findViewById(R.id.root_layout));

        /*mRecyclerView.setLayoutManager(new PreCachingLayoutManager(SearchActivityOld.this));
        mRecyclerView.setNestedScrollingEnabled(false);
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.setAdapter(mAdapter);*/
        mRecyclerView.setAdapter(mAdapter);

        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*Log.i(TAG, "Search editText, onTextChanged -> " + s);
                if (s!= null && mGoogleApiClient.isConnected()) {
                    *//*if (SystemClock.elapsedRealtime() - mLastRequestTime >= 250) {
                        mLastRequestTime = SystemClock.elapsedRealtime();
                        mAdapter.getFilter().filter(s.toString());
                    }*//*
                    mAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Log.e(TAG, "Google Client NOT CONNECTED");
                }else if(s != null && s.toString().trim().isEmpty()){
                    if(mAdapter != null) {
                        mAdapter.clearList();
                        if(recentSearchList != null && recentSearchList.size() > 0) {
                            if (recentSearchList.size() > 0) {
                                mAdapter.showRecentSearch(recentSearchList);
                            }
                        }
                        //mAdapter.notifyDataSetChanged();
                    }
                }*/

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "Search editText, onTextChanged -> " + s);
                if(s != null && !TextUtils.isEmpty(s.toString().trim())){
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 100 && mAdapter != null) {
                        //mAdapter.getFilter().filter(s.toString());
                        mAdapter.onAutocompleteSearch(s.toString());
                    }
                    mLastRequestTime = System.currentTimeMillis();
                }else{
                    if(mAdapter != null) {
                        mAdapter.clearList();
                        if(recentSearchList != null && recentSearchList.size() > 0) {
                            if (recentSearchList.size() > 0) {
                                mAdapter.showRecentSearch(recentSearchList);
                            }
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        //if(v == mClear){
        //    mSearchEdittext.setText("");
        //    if(mAdapter!=null){
        //        mAdapter.clearList();
        //    }

        //}
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onPlaceClick(final ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> mResultList, final int position) {
        if(mResultList!=null && mResultList.size() > 0 && mResultList.get(position) != null){
            try {
                if(mResultList.get(position).placeId != null && !mResultList.get(position).placeId.toString().isEmpty()) {
                    final String placeId = String.valueOf(mResultList.get(position).placeId);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */
                    geoDataClient.getPlaceById(placeId).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
                        @Override
                        public void onSuccess(PlaceBufferResponse places) {
                            Log.i(TAG, "onSuccess: ====="+mResultList.get(position).primaryText);
                            Log.i(TAG, "onSuccess: ====="+mResultList.get(position).secondaryText);
                            Log.i(TAG, "onSuccess: ====="+mResultList.get(position).projectId);
                            Log.i(TAG, "onSuccess: ====="+mResultList.get(position).searchType.toString());
                            Log.i(TAG, "onSuccess: ====="+places.getCount());
                            if(places.getCount() == 1){
                                String primartyText = "", secondaryText = "";
                                if(mResultList.get(position).primaryText != null) {
                                    primartyText = mResultList.get(position).primaryText.toString();
                                }
                                if(mResultList.get(position).secondaryText != null) {
                                    secondaryText =  mResultList.get(position).secondaryText.toString();
                                }
                                Intent data = new Intent();
                                data.putExtra("lat", String.valueOf(places.get(0).getLatLng().latitude));
                                data.putExtra("lng", String.valueOf(places.get(0).getLatLng().longitude));
                                long cityId = 0;
                                String cityName = "";
                                if(mResultList.get(position).cityId != 0) {
                                    cityId = mResultList.get(position).cityId;
                                }else{
                                    cityId = UtilityMethods.getLongInPref(mContext,Constants.AppConstants.SAVED_CITY_ID,0);
                                }
                                if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                                    cityName = mResultList.get(position).cityName;
                                }else{
                                    cityName = UtilityMethods.getStringInPref(mContext,Constants.AppConstants.SAVED_CITY,"");
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
                                setResult(SearchActivityOld.RESULT_OK, data);
                                finish();

                                addToRecentSearches(primartyText, secondaryText,
                                        places.get(0).getLatLng(),mResultList.get(position).searchType.toString(),
                                        mResultList.get(position).projectId,-1,
                                        "",cityId, cityName);
                            }
                            else {
                                if (Geocoder.isPresent()) {
                                    try {
                                        String primartyText = "", secondaryText = "";
                                        if(mResultList.get(position).primaryText != null) {
                                            primartyText = mResultList.get(position).primaryText.toString();
                                        }
                                        if(mResultList.get(position).secondaryText != null) {
                                            secondaryText =  mResultList.get(position).secondaryText.toString();
                                        }
                                        String location = UtilityMethods.getStringInPref(mContext, Constants.AppConstants.SAVED_CITY, "");
                                        Geocoder gc = new Geocoder(mContext);
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
                                                if(mResultList.get(position).cityId != 0) {
                                                    cityId = mResultList.get(position).cityId;
                                                }else{
                                                    cityId = UtilityMethods.getLongInPref(mContext,Constants.AppConstants.SAVED_CITY_ID,0);
                                                }
                                                if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                                                    cityName = mResultList.get(position).cityName;
                                                }else{
                                                    cityName = UtilityMethods.getStringInPref(mContext,Constants.AppConstants.SAVED_CITY,"");
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
                                                setResult(SearchActivityOld.RESULT_OK, data);
                                                finish();

                                                addToRecentSearches(primartyText, secondaryText,
                                                        new LatLng(latlng.get(0).latitude, latlng.get(0).longitude),
                                                        mResultList.get(position).searchType.toString(),
                                                        mResultList.get(position).projectId,-1,
                                                        "",cityId, cityName);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "results not found!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "results not found!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "results not found!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "results not found!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
                else if(mResultList.get(position).searchType.toString().equalsIgnoreCase(mContext.getResources().getString(R.string.project))){
                    String primartyText = "", secondaryText = "";
                    if(mResultList.get(position).primaryText != null) {
                        primartyText = mResultList.get(position).primaryText.toString();
                    }
                    if(mResultList.get(position).secondaryText != null) {
                        secondaryText =  mResultList.get(position).secondaryText.toString();
                    }
                    Intent intent = new Intent(SearchActivityOld.this, ProjectDetailsScreen.class);
                    intent.putExtra("ISDirectCall",true);
                    intent.putExtra("isProjectSearch",true);
                    long cityId = 0;
                    String cityName = "";
                    if(mResultList.get(position).cityId != 0) {
                        cityId = mResultList.get(position).cityId;
                    }else{
                        cityId = UtilityMethods.getLongInPref(mContext,Constants.AppConstants.SAVED_CITY_ID,0);
                    }
                    if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                        cityName = mResultList.get(position).cityName;
                    }else{
                        cityName = UtilityMethods.getStringInPref(mContext,Constants.AppConstants.SAVED_CITY,"");
                    }
                    intent.putExtra("city_id", cityId);
                    intent.putExtra("cityName", cityName);
                    intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,mResultList.get(position).projectId);
                    startActivity(intent);
                    finish();

                    addToRecentSearches(primartyText, secondaryText,
                            new LatLng(-1,-1), mResultList.get(position).searchType.toString(),
                            mResultList.get(position).projectId,-1,
                            "",cityId,
                            cityName);
                }
                else if(mResultList.get(position).searchType.toString().equalsIgnoreCase(mContext.getResources().getString(R.string.developer))){
                    String primartyText = "", secondaryText = "";
                    if(mResultList.get(position).primaryText != null) {
                        primartyText = mResultList.get(position).primaryText.toString();
                    }
                    if(mResultList.get(position).secondaryText != null) {
                        secondaryText =  mResultList.get(position).secondaryText.toString();
                    }
                    long cityId = 0;
                    String cityName = "";
                    if(mResultList.get(position).cityId != 0) {
                        cityId = mResultList.get(position).cityId;
                    }else{
                        cityId = UtilityMethods.getLongInPref(mContext,Constants.AppConstants.SAVED_CITY_ID,0);
                    }
                    if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                        cityName = mResultList.get(position).cityName;
                    }else{
                        cityName = UtilityMethods.getStringInPref(mContext,Constants.AppConstants.SAVED_CITY,"");
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
                    data.putExtra("builder_id",mResultList.get(position).builderId);
                    data.putExtra("builder_name",mResultList.get(position).builderName);
                    data.putExtra("city_id",cityId);
                    data.putExtra("cityName",cityName);
                    data.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_DEVELOPER,true);
                    setResult(SearchActivityOld.RESULT_OK, data);
                    finish();

                    addToRecentSearches(primartyText, secondaryText,
                            new LatLng(-1,-1), mResultList.get(position).searchType.toString(),
                            mResultList.get(position).projectId,mResultList.get(position).builderId,
                            mResultList.get(position).builderName,cityId, cityName);
                }
                else{
                    String primartyText = "", secondaryText = "";
                    if(mResultList.get(position).primaryText != null) {
                        primartyText = mResultList.get(position).primaryText.toString();
                    }
                    if(mResultList.get(position).secondaryText != null) {
                        secondaryText =  mResultList.get(position).secondaryText.toString();
                    }
                    Intent data = new Intent();
                    data.putExtra("lat", String.valueOf(mResultList.get(position).latitude));
                    data.putExtra("lng", String.valueOf(mResultList.get(position).longitude));
                    long cityId = 0;
                    String cityName = "";
                    if(mResultList.get(position).cityId != 0) {
                        cityId = mResultList.get(position).cityId;
                    }else{
                        cityId = UtilityMethods.getLongInPref(mContext,Constants.AppConstants.SAVED_CITY_ID,0);
                    }
                    if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                        cityName = mResultList.get(position).cityName;
                    }else{
                        cityName = UtilityMethods.getStringInPref(mContext,Constants.AppConstants.SAVED_CITY,"");
                    }
                    if(cityId != 0) {
                        data.putExtra("city_id", cityId);
                    }
                    if(!TextUtils.isEmpty(cityName)) {
                        data.putExtra("cityName", cityName);
                    }
                    if(!TextUtils.isEmpty(primartyText)) {
                        data.putExtra("address", primartyText);
                    }
                    if(!TextUtils.isEmpty(secondaryText)) {
                        data.putExtra("additional_address", secondaryText);
                    }
                    data.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION,true);
                    setResult(SearchActivityOld.RESULT_OK, data);
                    finish();

                    addToRecentSearches(primartyText, secondaryText,
                            new LatLng(mResultList.get(position).latitude,mResultList.get(position).longitude),
                            mResultList.get(position).searchType.toString(),
                            mResultList.get(position).projectId,-1,
                            "",cityId, cityName);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRecentPlaceClick(ArrayList<RecentSearch> mResultList, int position) {
        if(mResultList!=null && mResultList.size() > 0 && mResultList.get(position) != null){
            String primartyText = "", secondaryText = "";
            if(mResultList.get(position).primaryText != null) {
                primartyText = mResultList.get(position).primaryText.toString();
            }
            if(mResultList.get(position).secondaryText != null) {
                secondaryText =  mResultList.get(position).secondaryText.toString();
            }
            long cityId = 0;
            String cityName = "";
            if(mResultList.get(position).cityId != 0) {
                cityId = mResultList.get(position).cityId;
            }else{
                cityId = UtilityMethods.getLongInPref(mContext,Constants.AppConstants.SAVED_CITY_ID,0);
            }
            if(!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                cityName = mResultList.get(position).cityName;
            }else{
                cityName = UtilityMethods.getStringInPref(mContext,Constants.AppConstants.SAVED_CITY,"");
            }
            if(mResultList.get(position).searchType.equalsIgnoreCase(mContext.getResources().getString(R.string.locality))) {
                Intent intent = new Intent(SearchActivityOld.this, HomeScreen.class);
                intent.putExtra("lat", String.valueOf(mResultList.get(position).latLng.latitude));
                intent.putExtra("lng", String.valueOf(mResultList.get(position).latLng.longitude));
                intent.putExtra("city_id",cityId);
                intent.putExtra("cityName",cityName);
                if(!TextUtils.isEmpty(primartyText)) {
                    intent.putExtra("address", primartyText);
                }
                if(!TextUtils.isEmpty(secondaryText)) {
                    intent.putExtra("additional_address", secondaryText);
                }
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION,true);
                setResult(RESULT_OK,intent);
                finish();
            }else if(mResultList.get(position).searchType.equalsIgnoreCase(mContext.getResources().getString(R.string.project))){
                Intent intent = new Intent(SearchActivityOld.this, ProjectDetailsScreen.class);
                intent.putExtra("ISDirectCall",true);
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,mResultList.get(position).projectId);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(SearchActivityOld.this, HomeScreen.class);
                intent.putExtra("lat", String.valueOf(mResultList.get(position).latLng.latitude));
                intent.putExtra("lng", String.valueOf(mResultList.get(position).latLng.longitude));
                if(!TextUtils.isEmpty(primartyText)) {
                    intent.putExtra("address", primartyText);
                }
                if(!TextUtils.isEmpty(secondaryText)) {
                    intent.putExtra("additional_address", secondaryText);
                }
                intent.putExtra("builder_id",mResultList.get(position).builderId);
                intent.putExtra("builder_name",mResultList.get(position).builderName);
                intent.putExtra("city_id",cityId);
                intent.putExtra("cityName",cityName);
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_DEVELOPER,true);
                setResult(RESULT_OK,intent);
                finish();
            }
            addToRecentSearches(primartyText, secondaryText,
                    mResultList.get(position).latLng,mResultList.get(position).searchType,
                    mResultList.get(position).projectId,mResultList.get(position).builderId,
                    mResultList.get(position).builderName,cityId, cityName);
        }
    }

    private void addToRecentSearches(String primaryText, String secondaryText, LatLng latLng, String searchType,
                                     long projectId, long builderId, String builderName, long cityId,String cityName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SearchActivityOld.this);
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

    public ArrayList<RecentSearch> getRecentlyViewed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SearchActivityOld.this);
        Gson gson = new Gson();
        String json = prefs.getString("recent_search", "");
        Type type = new TypeToken<List<RecentSearch>>() {
        }.getType();
        ArrayList<RecentSearch> list = gson.fromJson(json, type);
        if ((list != null) && (!list.isEmpty()))
            Collections.reverse(list);
        return list;
    }

    @Override
    public void onResume(){
        super.onResume();
        /*if( ((SearchActivityOld)mContext).recentSearchList!=null && !((SearchActivityOld)mContext).recentSearchList.isEmpty()){
            recentTxt.setVisibility(View.VISIBLE);
            mRecentList.setLayoutManager(new PreCachingLayoutManager(SearchActivityOld.this));
            mRecentList.setNestedScrollingEnabled(false);
            ((SimpleItemAnimator)mRecentList.getItemAnimator()).setSupportsChangeAnimations(false);
            mRecentList.setAdapter(new RecentSearchAdapter(SearchActivityOld.this, ((SearchActivityOld)mContext).recentSearchList));
        } else {
            recentTxt.setVisibility(View.GONE);
        }*/
        if(recentSearchList != null && recentSearchList.size() > 0) {
            if (recentSearchList.size() > 0) {
                mAdapter.showRecentSearch(recentSearchList);
            }
        }

    }

}
