package com.clicbrics.consumer.clicworth;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.PlaceAutocompleteAdapter;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.PlaceBufferResponse;
import com.google.android.libraries.places.compat.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PropertySearchForEstimation extends BaseActivity
        implements PlaceAutoCompleteLocalityAdapter.PlacesAutocompleteLocalityInterface,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private GeoDataClient geoDataClient;
    private ListView mRecyclerView;
    private PlaceAutoCompleteLocalityAdapter mAdapter;
    EditText mSearchEdittext;
    Context mContext;
    long mLastRequestTime;
    private double latitude=0, longitude=0;
    private String searchAddress_clicworth = "";
    private String city_name;
    private ImageView id_cancel_btn;
    private static final String TAG = PropertySearchForEstimation.class.getSimpleName();
    private String search_string="";
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_search_layout);
        geoDataClient= Places.getGeoDataClient(PropertySearchForEstimation.this);
        UtilityMethods.setStatusBarColor(this,R.color.white);
        mContext = this;
        Toolbar toolbar = (Toolbar)findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        ImageView back= (ImageView)toolbar.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onBackPressed: ======");
               /* Intent data = new Intent();
                data.putExtra("lat", String.valueOf(latitude));
                data.putExtra("lng", String.valueOf(longitude));
                data.putExtra("cityName", city_name);
                data.putExtra("address", searchAddress_clicworth);
                data.putExtra("search_string", search_string);
                setResult(PropertySearchForEstimation.RESULT_OK, data);*/
                finish();
            }
        });
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
//        addBackButton();
        initViews();
    }



    private void initViews() {
        mRecyclerView = (ListView) findViewById(R.id.list_search);
        mSearchEdittext = (EditText)findViewById(R.id.search_edit);
        id_cancel_btn = (ImageView) findViewById(R.id.id_cancel_btn);
        final TextView zero_results = (TextView) findViewById(R.id.search_result_zero);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .build();
        mAdapter = new PlaceAutoCompleteLocalityAdapter(PropertySearchForEstimation.this, R.layout.item_search_list,
                geoDataClient, BOUNDS_INDIA, typeFilter, zero_results, findViewById(R.id.root_layout));
        /*mAdapter = new PlaceAutoCompleteLocalityAdapter(PropertySearchForEstimation.this, R.layout.item_search_list,
                geoDataClient, BOUNDS_INDIA, null, zero_results, findViewById(R.id.root_layout));*/
        mRecyclerView.setAdapter(mAdapter);
        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(mSearchEdittext.getText().toString().trim())) {
                    Log.i(TAG, "onTextChanged: ===1");
                    id_cancel_btn.setVisibility(View.GONE);
                } else {
                    Log.i(TAG, "onTextChanged: ===2");
                    id_cancel_btn.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.i(TAG, "Search editText, onTextChanged -> " + s);
                if(s != null && !TextUtils.isEmpty(s.toString().trim())){
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 100 && mAdapter != null) {
                        mAdapter.onAutocompleteSearch(s.toString());
                    }
                    mLastRequestTime = System.currentTimeMillis();
                }else{
                    if (mAdapter != null) {
                        mRecyclerView.setVisibility(View.GONE);
                        mAdapter.clearList();
                        mAdapter.clear();
                    }
                }
            }
        });

        if(getIntent().hasExtra("city_name"))
        {
            if(!TextUtils.isEmpty(getIntent().getStringExtra("city_name")))
            {
//                longitude=Double.parseDouble(getIntent().getStringExtra("lng"));
//                latitude=Double.parseDouble(getIntent().getStringExtra("lat"));
//                searchAddress_clicworth=getIntent().getStringExtra("address");
                mSearchEdittext.setText(getIntent().getStringExtra("search_string"));
                mSearchEdittext.setSelection(getIntent().getStringExtra("search_string").length());
                id_cancel_btn.setVisibility(View.VISIBLE);
            }


        }

        id_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Cancel");
                mSearchEdittext.getText().clear();
                searchAddress_clicworth="";
                search_string="";
                city_name="";
                latitude=-1;
                longitude=-1;
                id_cancel_btn.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
            }
        });
        /*mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(mSearchEdittext.getText().toString().trim())) {
                    Log.i(TAG, "onTextChanged: ===1");
                    id_cancel_btn.setVisibility(View.GONE);
                } else {
                    Log.i(TAG, "onTextChanged: ===2");
                    id_cancel_btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.i(TAG, "Search editText, onTextChanged -> " + s);
                if(s != null && !TextUtils.isEmpty(s.toString().trim())){
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 100 && mAdapter != null) {
                        mAdapter.onAutocompleteSearch(s.toString());
                    }
                    mLastRequestTime = System.currentTimeMillis();
                }else{
                    if (mAdapter != null) {
                        mAdapter.clearList();
                    }
                }
            }
        });*/
    }

    @Override
    public void onPlaceClick(final ArrayList<PlaceAutoCompleteLocalityAdapter.PlaceAutocomplete> mResultList, final int position) {
        if (mResultList != null && mResultList.size() > 0 && mResultList.get(position) != null) {
            //hideSoftKeyboard();
            try {
                if (mResultList.get(position).placeId != null && !mResultList.get(position).placeId.toString().isEmpty()) {
                    final String placeId = String.valueOf(mResultList.get(position).placeId);

                    geoDataClient.getPlaceById(placeId).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
                        @Override
                        public void onSuccess(PlaceBufferResponse places) {
                            if (places.getCount() == 1) {
                                //Do the things here on Click.....
                                String primartyText = "", secondaryText = "";
                                if (mResultList.get(position).primaryText != null) {
                                    primartyText = mResultList.get(position).primaryText.toString();
                                    search_string=primartyText;
                                    Log.i(TAG, "onSuccess: ======1"+primartyText);
                                }
                                if (mResultList.get(position).secondaryText != null) {
                                    secondaryText = mResultList.get(position).secondaryText.toString().replace(", India","");
                                    Log.i(TAG, "onSuccess: ======2"+secondaryText+":"+secondaryText.replace(", India",""));
                                }
                                Intent data = new Intent();
                                data.putExtra("lat", String.valueOf(places.get(0).getLatLng().latitude));
                                data.putExtra("lng", String.valueOf(places.get(0).getLatLng().longitude));
                                long cityId = 0;
                                String cityName = "";
                                if (mResultList.get(position).cityId != 0) {
                                    cityId = mResultList.get(position).cityId;
                                } else {
                                    cityId = UtilityMethods.getLongInPref(PropertySearchForEstimation.this, Constants.AppConstants.ESTIMATE_CITY_ID, 0);
                                }
                                if (!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                                    cityName = mResultList.get(position).cityName;

                                } else {
                                    cityName = UtilityMethods.getStringInPref(PropertySearchForEstimation.this, Constants.AppConstants.ESTIMATE_CITY, "");
                                }
                                data.putExtra("city_id", cityId);
                                data.putExtra("cityName", cityName);
                                if (!TextUtils.isEmpty(primartyText)) {
                                    data.putExtra("address", primartyText);
                                }
                                if (!TextUtils.isEmpty(secondaryText)) {
                                    data.putExtra("additional_address", secondaryText);
                                }
                                data.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION, true);

                                LatLng latlng = places.get(0).getLatLng();
                                latitude = latlng.latitude;
                                longitude = latlng.longitude;
//                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                                city_name = cityName;
                                if (!TextUtils.isEmpty(primartyText)) {
                                    if (!TextUtils.isEmpty(secondaryText)) {
                                        searchAddress_clicworth = primartyText + " " + secondaryText;
                                        setPropertydata();
//                                        clicworth_interface.updatefragvalue(primartyText + " " + secondaryText, latitude, longitude);
//                                        binding.searchLocality.setText(primartyText + " " + secondaryText);
                                    } else {
                                        searchAddress_clicworth = primartyText;
//                                        binding.searchLocality.setText(primartyText);
                                        setPropertydata();
//                                        clicworth_interface.updatefragvalue(primartyText, latitude, longitude);
                                    }
                                }
//                                binding.listSearch.setVisibility(View.GONE);
//                                clicworth_interface.updateViewVisibility("gone");
//                                isDraggable = true;


                            } else {
                                if (Geocoder.isPresent()) {
                                    try {
                                        String primartyText = "", secondaryText = "";
                                        if (mResultList.get(position).primaryText != null) {
                                            primartyText = mResultList.get(position).primaryText.toString();
                                            search_string=primartyText;
                                        }
                                        if (mResultList.get(position).secondaryText != null) {
                                            secondaryText = mResultList.get(position).secondaryText.toString().replace(", India","");
                                        }
                                        String location = UtilityMethods.getStringInPref(PropertySearchForEstimation.this, Constants.AppConstants.ESTIMATE_CITY, "");
                                        Geocoder gc = new Geocoder(PropertySearchForEstimation.this);
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
                                                if (mResultList.get(position).cityId != 0) {
                                                    cityId = mResultList.get(position).cityId;
                                                } else {
                                                    cityId = UtilityMethods.getLongInPref(PropertySearchForEstimation.this, Constants.AppConstants.ESTIMATE_CITY_ID, 0);
                                                }
                                                if (!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                                                    cityName = mResultList.get(position).cityName;
                                                } else {
                                                    cityName = UtilityMethods.getStringInPref(PropertySearchForEstimation.this, Constants.AppConstants.ESTIMATE_CITY, "");
                                                }
                                                data.putExtra("city_id", cityId);
                                                data.putExtra("cityName", cityName);
                                                if (!TextUtils.isEmpty(primartyText)) {
                                                    data.putExtra("address", primartyText);
                                                }
                                                if (!TextUtils.isEmpty(secondaryText)) {
                                                    data.putExtra("additional_address", secondaryText);
                                                }
                                                data.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION, true);

                                                latitude = latlng.get(0).latitude;
                                                longitude = latlng.get(0).longitude;
//                                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng.get(0), 15));

                                                if (!TextUtils.isEmpty(primartyText)) {
                                                    if (!TextUtils.isEmpty(secondaryText)) {
                                                        searchAddress_clicworth = primartyText + " " + secondaryText;
                                                        setPropertydata();
                                                    } else {
                                                        searchAddress_clicworth = primartyText;
                                                        setPropertydata();
                                                    }
                                                }
                                                city_name = cityName;


                                            } else {
                                                Toast.makeText(PropertySearchForEstimation.this, "results not found!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(PropertySearchForEstimation.this, "results not found!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(PropertySearchForEstimation.this, "results not found!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(PropertySearchForEstimation.this, "results not found!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } else {
                    String primartyText = "", secondaryText = "";
                    if (mResultList.get(position).primaryText != null) {
                        primartyText = mResultList.get(position).primaryText.toString();
                        search_string=primartyText;
                    }
                    if (mResultList.get(position).secondaryText != null) {
                        secondaryText = mResultList.get(position).secondaryText.toString().replace(", India","");
                    }
                    Intent data = new Intent();
                    data.putExtra("lat", String.valueOf(mResultList.get(position).latitude));
                    data.putExtra("lng", String.valueOf(mResultList.get(position).longitude));
                    long cityId = 0;
                    String cityName = "";
                    if (mResultList.get(position).cityId != 0) {
                        cityId = mResultList.get(position).cityId;
                    } else {
                        cityId = UtilityMethods.getLongInPref(PropertySearchForEstimation.this, Constants.AppConstants.ESTIMATE_CITY_ID, 0);
                    }
                    if (!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                        cityName = mResultList.get(position).cityName;
                    } else {
                        cityName = UtilityMethods.getStringInPref(PropertySearchForEstimation.this, Constants.AppConstants.ESTIMATE_CITY, "");
                    }
                    if (cityId != 0) {
                        data.putExtra("city_id", cityId);
                    }
                    if (!TextUtils.isEmpty(cityName)) {
                        data.putExtra("cityName", cityName);
                    }
                    if (!TextUtils.isEmpty(primartyText)) {
                        data.putExtra("address", primartyText);
                    }
                    if (!TextUtils.isEmpty(secondaryText)) {
                        data.putExtra("additional_address", secondaryText);
                    }
                    data.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION, true);
//                    clicworth_interface.updateViewVisibility("gone");
//                    binding.listSearch.setVisibility(View.GONE);
                    latitude = mResultList.get(position).latitude;
                    longitude = mResultList.get(position).longitude;
                    LatLng latlng = new LatLng(latitude, longitude);
                    if (!TextUtils.isEmpty(primartyText)) {
                        if (!TextUtils.isEmpty(secondaryText)) {
                            searchAddress_clicworth = primartyText + " " + secondaryText;
//                            binding.searchLocality.setText(primartyText + " " + secondaryText);
                            setPropertydata();
//                            clicworth_interface.updatefragvalue(primartyText + " " + secondaryText, latitude, longitude);
                        } else {
                            searchAddress_clicworth = primartyText;
                            setPropertydata();
//                            clicworth_interface.updatefragvalue(primartyText,latitude,longitude);
//                            binding.searchLocality.setText(primartyText);
                        }
                    }
                    city_name = cityName;
//                    isDraggable = true;
//                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void setPropertydata() {
        Intent data = new Intent();
        data.putExtra("lat", String.valueOf(latitude));
        data.putExtra("lng", String.valueOf(longitude));
        data.putExtra("cityName", city_name);
        data.putExtra("address", searchAddress_clicworth);
        data.putExtra("search_string", search_string);
        setResult(PropertySearchForEstimation.RESULT_OK, data);
        finish();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
