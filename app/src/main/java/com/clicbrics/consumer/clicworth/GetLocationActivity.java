package com.clicbrics.consumer.clicworth;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.PermissionManager;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.PlaceBufferResponse;
import com.google.android.libraries.places.compat.Places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GetLocationActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        PlaceAutoCompleteLocalityAdapter.PlacesAutocompleteLocalityInterface {

    private static final String TAG = "GetLocationActivity";
    private static final int REQUEST_PERMISSIONS_CODE = 4;
    private static final int LOCATION_SETTING_CHANGE_CODE = 1000;
    public static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    float zoomLevel = 11.5f;
    private double latitude;
    private double longitude;
    private ImageView back;
    private ImageView idCancelBtn;
    private Button doneButton;
    private EditText searchView;
    private TextView mapView;
    private TextView satelliteView;

    private Location mLastLocation;
    private double mcurrentLat, mcurrentLong;
    private GoogleMap mGoogleMap;
    private GoogleApiClient googleApiClient;
    private GeoDataClient geoDataClient;
    private LocationRequest mLocationRequest;

    private SupportMapFragment mMapFragment;


    private String searchAddress = "";
    private String city_name;
    private ScaleGestureDetector gestureDetector;
    private int fingers = 0;
    private long lastZoomTime = 0;
    private float lastSpan = -1;

    private ListView mRecyclerView;
    private PlaceAutoCompleteLocalityAdapter mAdapter;
    long onStartTime;
    long mLastRequestTime;
//    public ArrayList<RecentSearch> recentSearchList;

    private TextView zero_results;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
    private boolean isFirst;
    private boolean isDraggable;
    private boolean isMaptouched;
    private Dialog notInfoDialog;
    private String country;
    private String search_string;
    private ImageView map_type, current_location;
//    private Marker marker;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlocation);
        initGoogleApiClient();
        initView();
        showNotInformationDialog();
        if (getIntent() != null && getIntent().hasExtra("latitude")) {
            latitude = getIntent().getDoubleExtra("latitude", 0l);
            longitude = getIntent().getDoubleExtra("longitude", 0l);
            if(latitude<=0.0&&longitude<=0.0)
            {
                moveToCurrentLocation();
            }
            else
            {
                if (getIntent().hasExtra("address")) {
                    searchAddress = getIntent().getStringExtra("address");
                    if(!TextUtils.isEmpty(getIntent().getStringExtra("address")))
                    {
                        searchView.setText(getIntent().getStringExtra("address"));
                        searchView.setSelection(searchView.getText().length());
                    }

                }
            }

        }
        geoDataClient = Places.getGeoDataClient(this);
//        recentSearchList = getRecentlyViewed();
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        onStartTime = System.currentTimeMillis();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        TrackAnalytics.trackEvent("SearchLocalityResultSActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);
        super.onStop();
    }

    private void initView() {
        back = findViewById(R.id.backbutton);
        idCancelBtn = findViewById(R.id.id_cancel_btn);
        doneButton = findViewById(R.id.id_done_button);
        searchView = findViewById(R.id.search_view);
        map_type = findViewById(R.id.map_type);
        current_location = findViewById(R.id.current_location);
//        searchView.setFocusable(true);
        //showSoftKeyboard();
        mapView = findViewById(R.id.id_map_view);
        satelliteView = findViewById(R.id.id_satellite_view);
        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCurrentLocation();
//                setUpMapIfNeeded();
            }
        });
        map_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map_type.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.satellite_icon).getConstantState()) {
                    if (mGoogleMap != null)
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                    map_type.setImageResource(R.drawable.normal_map_icon);
                } else if (map_type.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.normal_map_icon).getConstantState()) {
                    if (mGoogleMap != null)
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    map_type.setImageResource(R.drawable.satellite_icon);
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        idCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Cancel");
                searchView.getText().clear();
                searchAddress="";
                search_string="";
                city_name="";
                latitude=-1;
                longitude=-1;
                idCancelBtn.setVisibility(View.GONE);
                searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_round_cancel, 0);
                mRecyclerView.setVisibility(View.GONE);
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(searchView.getText().toString().trim())) {
                    Intent data = new Intent();
                    data.putExtra("lat", String.valueOf(latitude));
                    data.putExtra("lng", String.valueOf(longitude));
                    data.putExtra("cityName", city_name);
                    data.putExtra("address", searchAddress);
                    data.putExtra("search_string", search_string);
                    setResult(GetLocationActivity.RESULT_OK, data);
                    finish();
                } else {
                    UtilityMethods.showSnackbarOnTop(GetLocationActivity.this, "Error", getResources().getString(R.string.select_locality), true, 1500);
                }
            }
        });


        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.action_search || id == EditorInfo.IME_NULL) {
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_mapview);
        mMapFragment.getMapAsync(this);
        mRecyclerView = (ListView) findViewById(R.id.list_search);
        zero_results = (TextView) findViewById(R.id.search_result_zero);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mapView.setTextColor(getResources().getColor(R.color.white));
                satelliteView.setBackgroundColor(getResources().getColor(R.color.gray));
                satelliteView.setTextColor(getResources().getColor(R.color.black));
                if (mGoogleMap != null)
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });


        satelliteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.setBackgroundColor(getResources().getColor(R.color.gray));
                mapView.setTextColor(getResources().getColor(R.color.black));
                satelliteView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                satelliteView.setTextColor(getResources().getColor(R.color.white));
                if (mGoogleMap != null)
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


            }
        });
        /*searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });*/
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(searchView.getText().toString().trim())) {
                    Log.i(TAG, "onTextChanged: ===1");
                    idCancelBtn.setVisibility(View.GONE);
                    searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_icon, 0);
                } else {
                    Log.i(TAG, "onTextChanged: ===2");
                    idCancelBtn.setVisibility(View.VISIBLE);
                    searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isMaptouched) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                isMaptouched = false;
                Log.i(TAG, "Search editText, onTextChanged -> " + s);
                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 100 && mAdapter != null) {
                        //mAdapter.getFilter().filter(s.toString());
                        mAdapter.onAutocompleteSearch(s.toString());
                    }
                    mLastRequestTime = System.currentTimeMillis();
                } else {
                    if (mAdapter != null) {
                        mAdapter.clearList();
                    }
                }
            }
        });


    }



    private void moveToCurrentLocation() {
        if (GetLocationActivity.this != null && !PermissionManager.checkIfAlreadyhasPermission(GetLocationActivity.this, PERMISSIONS)) {
            Log.d(TAG, "Requesting!!");
            PermissionManager.requestForSpecificPermission(GetLocationActivity.this, PERMISSIONS, REQUEST_PERMISSIONS_CODE);
        } else {
            Log.d(TAG, "RequestLocation!!");
            requestPermissionForLocation();
            //getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.splash_screen);
        for (int i = 0; i < permissions.length; i++) {
            Log.d(TAG, "permission:" + "\nreqCode->" + requestCode + "   \n" + permissions[i] + "   \n" +
                    "result->" + grantResults[i]);
        }
        if (requestCode == REQUEST_PERMISSIONS_CODE) {

            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
            Log.d(TAG, "Granted Permission for Location!");
            requestPermissionForLocation();
        } else {
            Log.d(TAG, "Invalid code->" + requestCode);
        }
    }
    private void requestPermissionForLocation() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setInterval(30 * 1000);
        //locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true);
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Success!");
                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                        if (myLocation != null) {
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
                            mGoogleMap.setMyLocationEnabled(true);
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), zoomLevel));
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "REsolutionRequired!");
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(mActivity, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void setUpMapIfNeeded() {
        if (mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (myLocation != null) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), zoomLevel));
            }
            mGoogleMap.setMyLocationEnabled(true);

            if(true){
                return;
            }
          /*  Location myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (myLocation != null) {

            }*/
            // Check if we were successful in obtaining the map.
            if (mGoogleMap != null) {
                mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude())));
//                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                        Geocoder geocoder;
                        List<Address> addresses;


                        try {
                            latitude=arg0.getLatitude();
                            longitude=arg0.getLongitude();
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            String address = "", city = "", state = "", country = "", postalCode = "", knownName = "";
                            if (!TextUtils.isEmpty(addresses.get(0).getAddressLine(0))) {
                                address = addresses.get(0).getAddressLine(0);
                            }
                            if (!TextUtils.isEmpty(addresses.get(0).getLocality())) {
                                city = addresses.get(0).getLocality();
                            }

                            if (!TextUtils.isEmpty(addresses.get(0).getAdminArea())) {
                                state = addresses.get(0).getAdminArea();
                            }
                            if (!TextUtils.isEmpty(addresses.get(0).getCountryName())) {
                                country = addresses.get(0).getCountryName();
                            }
                            if (!TextUtils.isEmpty(addresses.get(0).getPostalCode())) {
                                postalCode = addresses.get(0).getPostalCode();
                            }
                            if (!TextUtils.isEmpty(addresses.get(0).getFeatureName())) {
                                knownName = addresses.get(0).getFeatureName();
                            }


                            Log.i(TAG, "onLocationChanged: ====3" + address + " " + city + " " + state + " " + country + " " +
                                    postalCode + " " + knownName);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }


    private synchronized void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this).addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        googleApiClient.connect();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
//        if (googleApiClient != null && googleApiClient.isConnected())
//        {
//            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,mLocationRequest,this);
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .build();
        mAdapter = new PlaceAutoCompleteLocalityAdapter(GetLocationActivity.this, R.layout.item_search_list,
                geoDataClient, BOUNDS_INDIA, typeFilter, zero_results, findViewById(R.id.root_layout));
        mRecyclerView.setAdapter(mAdapter);


        LatLng latlng = new LatLng(latitude, longitude);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mRecyclerView.setVisibility(View.GONE);
            }
        });


        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.i(TAG, "onMapReady: ==============setOnCamera");
                //get latlng at the center by calling
                LatLng midLatLng = mGoogleMap.getCameraPosition().target;
                latitude = midLatLng.latitude;
                longitude = midLatLng.longitude;
                if (isFirst)
                {
                    if (!isDraggable) {
                        isMaptouched = true;

                        new GetAddress().execute();
                    }
                    isDraggable = false;
                }else
                {
                    new GetAddress().execute();
                    mRecyclerView.setVisibility(View.GONE);
                }
                isFirst = true;
            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                isMaptouched = true;

            }
        });

        gestureDetector = new ScaleGestureDetector(GetLocationActivity.this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.i(TAG, "onMapReady: ==============onScale");
                if (lastSpan == -1) {
                    lastSpan = detector.getCurrentSpan();
                } else if (detector.getEventTime() - lastZoomTime >= 50) {
                    lastZoomTime = detector.getEventTime();
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomBy(getZoomValue(detector.getCurrentSpan(), lastSpan)), 50, null);
                    lastSpan = detector.getCurrentSpan();
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                lastSpan = -1;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                lastSpan = -1;

            }
        });


    }

    private float getZoomValue(float currentSpan, float lastSpan) {
        double value = (Math.log(currentSpan / lastSpan) / Math.log(1.55d));
        return (float) value;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                fingers = fingers + 1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                fingers = fingers - 1;
                break;
            case MotionEvent.ACTION_UP:
                fingers = 0;
                break;
            case MotionEvent.ACTION_DOWN:
                fingers = 1;
                break;
        }
        if (fingers > 1) {
            disableScrolling();
        } else if (fingers < 1) {
            enableScrolling();
        }
        if (fingers > 1) {
            return gestureDetector.onTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    private void enableScrolling() {
        if (mGoogleMap != null && !mGoogleMap.getUiSettings().isScrollGesturesEnabled()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                }
            }, 50);
        }
    }

    private void disableScrolling() {
        new Handler().removeCallbacksAndMessages(null);
        if (mGoogleMap != null && mGoogleMap.getUiSettings().isScrollGesturesEnabled()) {
            mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        }
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
                                }
                                if (mResultList.get(position).secondaryText != null) {
                                    secondaryText = mResultList.get(position).secondaryText.toString().replace(", India","");
                                }
                                Intent data = new Intent();
                                data.putExtra("lat", String.valueOf(places.get(0).getLatLng().latitude));
                                data.putExtra("lng", String.valueOf(places.get(0).getLatLng().longitude));
                                long cityId = 0;
                                String cityName = "";
                                if (mResultList.get(position).cityId != 0) {
                                    cityId = mResultList.get(position).cityId;
                                } else {
                                    cityId = UtilityMethods.getLongInPref(mActivity, Constants.AppConstants.ESTIMATE_CITY_ID, 0);
                                }
                                if (!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                                    cityName = mResultList.get(position).cityName;

                                } else {
                                    cityName = UtilityMethods.getStringInPref(mActivity, Constants.AppConstants.ESTIMATE_CITY, "");
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
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                                city_name = cityName;
                                if (!TextUtils.isEmpty(primartyText)) {
                                    if (!TextUtils.isEmpty(secondaryText)) {
                                        searchAddress = primartyText + " " + secondaryText;
                                        searchView.setText(primartyText + " " + secondaryText);
                                        searchView.setSelection(searchView.getText().toString().length());
                                    } else {
                                        searchAddress = primartyText;
                                        searchView.setText(primartyText);
                                        searchView.setSelection(searchView.getText().toString().length());
                                    }
                                }
                                mRecyclerView.setVisibility(View.GONE);
                                isDraggable = true;


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
                                        String location = UtilityMethods.getStringInPref(mActivity, Constants.AppConstants.ESTIMATE_CITY, "");
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
                                                if (mResultList.get(position).cityId != 0) {
                                                    cityId = mResultList.get(position).cityId;
                                                } else {
                                                    cityId = UtilityMethods.getLongInPref(mActivity, Constants.AppConstants.ESTIMATE_CITY_ID, 0);
                                                }
                                                if (!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                                                    cityName = mResultList.get(position).cityName;
                                                } else {
                                                    cityName = UtilityMethods.getStringInPref(mActivity, Constants.AppConstants.ESTIMATE_CITY, "");
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
                                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng.get(0), 15));

                                                if (!TextUtils.isEmpty(primartyText)) {
                                                    if (!TextUtils.isEmpty(secondaryText)) {
                                                        searchAddress = primartyText + " " + secondaryText;
                                                        searchView.setText(primartyText + " " + secondaryText);
                                                        searchView.setSelection(searchView.getText().toString().length());
                                                    } else {
                                                        searchAddress = primartyText;
                                                        searchView.setText(primartyText);
                                                        searchView.setSelection(searchView.getText().toString().length());
                                                    }
                                                }
                                                city_name = cityName;
                                                isDraggable = true;
                                                mRecyclerView.setVisibility(View.GONE);

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
                        cityId = UtilityMethods.getLongInPref(mActivity, Constants.AppConstants.ESTIMATE_CITY_ID, 0);
                    }
                    if (!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                        cityName = mResultList.get(position).cityName;
                    } else {
                        cityName = UtilityMethods.getStringInPref(mActivity, Constants.AppConstants.ESTIMATE_CITY, "");
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

                    mRecyclerView.setVisibility(View.GONE);

                    latitude = mResultList.get(position).latitude;
                    longitude = mResultList.get(position).longitude;
                    LatLng latlng = new LatLng(latitude, longitude);
                    if (!TextUtils.isEmpty(primartyText)) {
                        if (!TextUtils.isEmpty(secondaryText)) {
                            searchAddress = primartyText + " " + secondaryText;
                            searchView.setText(primartyText + " " + secondaryText);
                            searchView.setSelection(searchView.getText().toString().length());
                        } else {
                            searchAddress = primartyText;
                            searchView.setText(primartyText);
                            searchView.setSelection(searchView.getText().toString().length());
                        }
                    }
                    city_name = cityName;
                    isDraggable = true;
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null)
        {
            Log.i(TAG, "onLocationChanged: ====1");
            mcurrentLat =mLastLocation.getLatitude();
            mcurrentLat =mLastLocation.getLongitude();
        }
        else
        {
            Log.i(TAG, "onLocationChanged: ====2");
            mcurrentLat=location.getLatitude();
            mcurrentLat=location.getLongitude();
        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public class GetAddress extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected String doInBackground(String... strings) {
            Log.i(TAG, "doInBackground: GetAddress=====");
            {
                StringBuilder add = new StringBuilder("");
                try {
                    Geocoder geocoder = new Geocoder(GetLocationActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses != null && !addresses.isEmpty() && addresses.get(0) != null) {
                        String address = "";
                        if (addresses.get(0).getAddressLine(0) != null && !addresses.get(0).getAddressLine(0).isEmpty()) {
                            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        }
                        String city = addresses.get(0).getLocality();
                        city_name = city;
//                        String knownName = addresses.get(0).getFeatureName();
//                        if (knownName != null) {
//                            searchAddress = knownName + ", " + city;
//                            return knownName + ", " + city;
//                        }
//
//                        String postalCode = addresses.get(0).getPostalCode();

                       /* List<City> cityList = ((HousingApplication) getApplicationContext()).getCityListEstimation();
                        for (int i = 0; i < cityList.size(); i++) {
                            if (cityList.get(i).getName().equalsIgnoreCase(city_name)) {
                                UtilityMethods.saveStringInPref(GetLocationActivity.this,
                                        Constants.AppConstants.ESTIMATE_PNAME, cityList.get(i).getPName());
                                UtilityMethods.saveStringInPref(GetLocationActivity.this,
                                        Constants.AppConstants.ESTIMATE_CITY, cityList.get(i).getName());
                                UtilityMethods.saveStringInPref(GetLocationActivity.this, Constants.AppConstants.SAVED_STATE,
                                        cityList.get(i).getState());
                            }
                        }
*/
                        String state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                        if (!TextUtils.isEmpty(city)) {
                            if (city.equalsIgnoreCase("Gurugram")||city.equalsIgnoreCase("gurgaon")) {
                                UtilityMethods.saveStringInPref(GetLocationActivity.this,
                                        Constants.AppConstants.ESTIMATE_PNAME, "gurgaon haryana");
                            } else {
                                if(!TextUtils.isEmpty(state)) {
                                    UtilityMethods.saveStringInPref(GetLocationActivity.this,
                                            Constants.AppConstants.ESTIMATE_PNAME, city + " " + state);
                                }else {
                                    UtilityMethods.saveStringInPref(GetLocationActivity.this,
                                            Constants.AppConstants.ESTIMATE_PNAME, "");
                                }
                            }
                            UtilityMethods.saveStringInPref(GetLocationActivity.this,
                                    Constants.AppConstants.ESTIMATE_CITY, city);
                        } else {
                            UtilityMethods.saveStringInPref(GetLocationActivity.this,
                                    Constants.AppConstants.ESTIMATE_PNAME, "");
                            UtilityMethods.saveStringInPref(GetLocationActivity.this,
                                    Constants.AppConstants.ESTIMATE_CITY, "");
                        }

                        if(!TextUtils.isEmpty(state)) {
                            UtilityMethods.saveStringInPref(GetLocationActivity.this, Constants.AppConstants.ESTIMATE_STATE,
                                    state);
                        }else {
                            UtilityMethods.saveStringInPref(GetLocationActivity.this, Constants.AppConstants.ESTIMATE_STATE,
                                    "");
                        }

                        if (!TextUtils.isEmpty(address)) {
                            add.append(address);
                        }
                        if (!TextUtils.isEmpty(add)) {
                            if (!TextUtils.isEmpty(city)) {
                                if (!add.toString().contains(city)) {
                                    add.append(", " + city);
                                }

                            }
                        } else {
                            add.append(city);
                        }


                    }
                    searchAddress = add.toString();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return add.toString();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!TextUtils.isEmpty(s)) {
                if (!TextUtils.isEmpty(country) && country.equalsIgnoreCase("India")) {
                    search_string=s;
                    searchView.setText(s);
                    searchView.setSelection(searchView.getText().toString().length());
                } else {
//                    showNotInformationDialog();
                    if (!isDestroyed() && !isFinishing() && !notInfoDialog.isShowing()) {
                        notInfoDialog.show();
                    }
                }
            } else {
//                showNotInformationDialog();
                if (!isDestroyed() && !isFinishing() && !notInfoDialog.isShowing()) {
                    notInfoDialog.show();
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showNotInformationDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_no_info, null);
        notInfoDialog = new Dialog(this);
        notInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        notInfoDialog.setContentView(view);
        notInfoDialog.setCancelable(false);
        notInfoDialog.setCanceledOnTouchOutside(false);
        Button ok = (Button) view.findViewById(R.id.btn_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notInfoDialog != null && notInfoDialog.isShowing() && !isDestroyed()) {
                    searchView.getText().clear();
                    notInfoDialog.dismiss();
                }

            }
        });

     /*   if (!isDestroyed() && !isFinishing() && !notInfoDialog.isShowing()) {
            notInfoDialog.show();
        }*/

    }


    @Override
    public void onBackPressed() {
        if (mRecyclerView.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }

    }


}
