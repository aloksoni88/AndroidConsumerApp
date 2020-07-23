package com.clicbrics.consumer.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.buy.housing.backend.propertyEndPoint.model.Nearby;
import com.buy.housing.backend.propertyEndPoint.model.NearbyResponse;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.customview.CustomProgressDialog;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.helper.NearByResultCallback;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.DataParser;
import com.clicbrics.consumer.utils.PermissionManager;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.viewmodel.NearByViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;



public class ExploreNeighbourhood extends BaseActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NearByResultCallback,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng homeLatLng;
    private String homeTitle;
    private static final String TAG = ExploreNeighbourhood.class.getSimpleName();
    private String PLACES_KEY;
    private ToggleButton toggleButton[] = new ToggleButton[10];
    private boolean checkList[] = new boolean[10]; // to keep state of all togglebuttons
    private String placesList[] = {"bus_station", "train_station", "airport", "school", "restaurant",
            "grocery_or_supermarket", "atm", "hospital", "park", "petrol_pump"};

    private String toastList[] = {"bus stop", "train station", "airport", "school", "restaurant", "store", "ATM", "hospital", "park", "petrol pump"};
    private final int proximityRadius[] = {5000, 10000, 100000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

    private HashMap<String, List<HashMap<String, String>>> listofPlacesList = null;
    private List<HashMap<String, String>> routeCoordinates = null;
    private MaterialProgressBar progressBar;
    Polyline line = null;

    private static final int REQUEST_PERMISSIONS_CODE = 4;
    private static final int LOCATION_SETTING_CHANGE_CODE = 1000;
    public static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    long mLastClickTime;
    ImageButton mNavigateButton;
    CustomProgressDialog progressDialog;
    private NearByViewModel nearByViewModel;
    private ArrayList<Nearby> nearByAirportList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_neighbourhood);
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        Toolbar toolbar = (Toolbar) findViewById(R.id.explore_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        PLACES_KEY = getString(R.string.place_api_key);
        addBackButton();
        progressBar = (MaterialProgressBar) findViewById(R.id.load_nearbydata_progress_bar);
        listofPlacesList = new HashMap<String, List<HashMap<String, String>>>();

        //set home LatLng.
        if (getIntent().hasExtra(Constants.AppConstants.LOCATION)) {
            Bundle locationBundle = getIntent().getParcelableExtra(Constants.AppConstants.LOCATION);
            homeLatLng = locationBundle.getParcelable("LatLng");
        }
        if (getIntent().hasExtra(Constants.AppConstants.HOME)) {
            homeTitle = getIntent().getStringExtra(Constants.AppConstants.HOME);
        } else {
            homeTitle = "";
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.explore_neighbour_map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        initCheckList();
        initButtons();

        nearByViewModel = new NearByViewModel(this);
    }

    private void initCheckList() {
        for (int i = 0; i < checkList.length; i++) {
            //   if (i == 0) {
            // checkList[i] = true;
            //   } else {
            checkList[i] = false;
            //   }
        }
    }

    private void initButtons() {
        toggleButton[0] = (ToggleButton) findViewById(R.id.bus_stops);
        toggleButton[1] = (ToggleButton) findViewById(R.id.railways);
        toggleButton[2] = (ToggleButton) findViewById(R.id.airports);
        toggleButton[3] = (ToggleButton) findViewById(R.id.schools);
        toggleButton[4] = (ToggleButton) findViewById(R.id.restaurants);
        toggleButton[5] = (ToggleButton) findViewById(R.id.groceries);
        toggleButton[6] = (ToggleButton) findViewById(R.id.atms);
        toggleButton[7] = (ToggleButton) findViewById(R.id.hospitals);
        toggleButton[8] = (ToggleButton) findViewById(R.id.parks);
        toggleButton[9] = (ToggleButton) findViewById(R.id.petrol_pumps);

        for (int i = 0; i < toggleButton.length; i++) {
            toggleButton[i].setOnCheckedChangeListener(checkedChangeListener);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleButton[0].setChecked(true);
                if (mMap != null && homeLatLng != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLatLng));
                }
            }
        }, 1500);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    Marker prevSelectedMarker = null;
    String prevMarkerCheckedType = "";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.redbrics_mapstyle);
        mMap.setMapStyle(mapStyleOptions);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG, "onMarkerClick: Maker Position : " + marker.getPosition().toString());
                Log.i(TAG, "onMarkerClick: Home Icon Position : " + homeLatLng.toString());
                //to reset the previous selected marker icon
                if (prevSelectedMarker != null && !prevMarkerCheckedType.isEmpty() && (!marker.getPosition().equals(homeLatLng))) {
                    prevSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(getIconId(prevMarkerCheckedType)));
                }
                if (line != null) {
                    line.remove();
                }

                Log.d(TAG, "LatLng->" + marker.getPosition().latitude + " " + marker.getPosition().longitude);
                LatLng clickedPosition = marker.getPosition();
                if (clickedPosition.equals(homeLatLng)) {
                    return false;
                }

                CameraPosition cameraPosition = new CameraPosition.Builder().target(clickedPosition)
                        .zoom(13f)
                        .bearing(UtilityMethods.getBearing(clickedPosition, homeLatLng))
                        .tilt(85)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                Log.d(TAG, "Title->" + marker.getTitle());
                marker.showInfoWindow();
                getRouteData(mMap, clickedPosition, homeLatLng);
                return true;
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                if (!marker.getPosition().equals(homeLatLng)) {  //if marker is homeIcon then no need to change icon
                    prevSelectedMarker = marker;
                    marker.setIcon(BitmapDescriptorFactory.fromResource(getSelectedMarkerIconId()));
                }
                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setGravity(Gravity.CENTER);
                snippet.setText(marker.getSnippet());
                info.addView(title);
                if (!snippet.getText().toString().isEmpty())
                    info.addView(snippet);
                return info;
            }
        });
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.addMarker(new MarkerOptions().position(homeLatLng).title(homeTitle)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_explore_lite)));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLatLng));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(homeLatLng)
                .zoom(13f)
                .tilt(85)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        mNavigateButton = (ImageButton) findViewById(R.id.id_navigate_icon);
        mNavigateButton.setEnabled(true);
        mNavigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick Nearby Map Navigate Button ");
                TrackAnalytics.trackEvent("Nearby_Navigation_click", Constants.AppConstants.HOLD_TIME,
                        1, ExploreNeighbourhood.this);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }

                mNavigateButton.setEnabled(false);
                mLastClickTime = SystemClock.elapsedRealtime();
                if (!PermissionManager.checkIfAlreadyhasPermission(ExploreNeighbourhood.this, PERMISSIONS)) {
                    Log.d(TAG, "Requesting!!");
                    PermissionManager.requestForSpecificPermission(ExploreNeighbourhood.this, PERMISSIONS, REQUEST_PERMISSIONS_CODE);
                } else {
                    Log.d(TAG, "RequestLocation!!");
                    requestPermissionForLocation();
                }
            }
        });
    }

    private void getPlaceData(GoogleMap googleMap, String placeType, int radius, int buttonIndex) {
        /*"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=51.503186,-0.126446&radius=5000&types=hospital&key=";
                 +getResources().getString(R.string.google_map_api_key);*/
        String cityName = UtilityMethods.getStringInPref(this,Constants.AppConstants.SAVED_CITY,"");
        String stateName = UtilityMethods.getStringInPref(this,Constants.AppConstants.SAVED_STATE,"");
        String url = "";
        if(placeType.equalsIgnoreCase("airport")) {
            try {
                if (!TextUtils.isEmpty(stateName) && !TextUtils.isEmpty(cityName)) {
                    url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=airport+in+"
                            + URLEncoder.encode(cityName.toLowerCase(),"UTF-8") + "+"
                            + URLEncoder.encode(stateName.toLowerCase(),"UTF-8") +
                            "&key="+ PLACES_KEY;
                }else if(!TextUtils.isEmpty(cityName)){
                    url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=airport+in+"
                            + URLEncoder.encode(cityName.toLowerCase(),"UTF-8") +
                            "&key="+ PLACES_KEY;
                }else{
                    url = DataParser.getNearbyUrl(homeLatLng.latitude, homeLatLng.longitude, placeType, radius, PLACES_KEY);
                }
            } catch (Exception e) {
                e.printStackTrace();
                url = DataParser.getNearbyUrl(homeLatLng.latitude, homeLatLng.longitude, placeType, radius, PLACES_KEY);
            }
        }else {
            url = DataParser.getNearbyUrl(homeLatLng.latitude, homeLatLng.longitude, placeType, radius, PLACES_KEY);
        }

        Object[] DataTransfer = new Object[4];
        DataTransfer[0] = googleMap;
        DataTransfer[1] = url;
        DataTransfer[2] = placeType;
        DataTransfer[3] = buttonIndex;

        GetNearbyData getNearbyPlacesData = new GetNearbyData();
        getNearbyPlacesData.execute(DataTransfer);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        mGoogleApiClient.connect();
    }

    LocationRequest mLocationRequest;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleApi.isUserResolvableError(result)) {
                googleApi.getErrorDialog(this, result, 0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "OnConnected!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "OnConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "OnConnectionFailed");
    }

    private void getRouteData(GoogleMap googleMap, LatLng clickedPosition, LatLng homePosition) {
        String url = DataParser.getDirectionsURL(clickedPosition.latitude, clickedPosition.longitude,
                homePosition.latitude, homePosition.longitude, PLACES_KEY);

        Object[] DataTransfer = new Object[3];
        DataTransfer[0] = googleMap;
        DataTransfer[1] = url;
        //DataTransfer[2] = placeType;

        GetRoute getRoute = new GetRoute();
        getRoute.execute(DataTransfer);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: Location -> " + location);
        if (location == null) {
            if (!PermissionManager.checkIfAlreadyhasPermission(this, PERMISSIONS)) {
                dismissProgressDialog();
                UtilityMethods.showErrorSnackBar(findViewById(R.id.explore_neighbour_activity),
                        getResources().getString(R.string.permision),Snackbar.LENGTH_LONG);
                return;
            }
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(location != null) {
                if (mGoogleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                }
                new GetAddressFromLocTask(location).execute();
            }else{
                dismissProgressDialog();
                UtilityMethods.showErrorSnackBar(findViewById(R.id.explore_neighbour_activity),
                        "Failed to get your location",Snackbar.LENGTH_LONG);
                return;
            }
        }else{
            if(mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
            new GetAddressFromLocTask(location).execute();
        }
    }

    private class GetRoute extends AsyncTask<Object, String, String> {
        String routeData;
        GoogleMap mMap;
        String placeType;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                HousingLogs.d("GetRouteData", "doInBackground entered");
                mMap = (GoogleMap) params[0];
                URL url = new URL((String) params[1]);
                //placeType = (String) params[2];
                routeData = DataParser.getData(url);
                HousingLogs.d("GetRouteData", "doInBackground Exit");
            } catch (Exception e) {
                HousingLogs.e("GetRouteData", e.toString());
            }
            return routeData;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "result->" + result);
            progressBar.setVisibility(View.GONE);
            //  try {
            //DataParser dataParser = new DataParser();
            //dataParser.getRouteCoordinates(new JSONObject(result));
            if(result != null && !result.isEmpty()) {
                drawPath(result);
            }
            //   } catch (JSONException e) {
            //       e.printStackTrace();
            //   }
        }
    }

    public void drawPath(String result) {

        try {
            //Tranform the string into a json object

            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            DataParser parser = new DataParser();
            List<LatLng> list = parser.decodePoly(encodedString);
            line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
                    .geodesic(true)
            );
           /*
           for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                .width(2)
                .color(Color.BLUE).geodesic(true));
            }
           */

            //} catch (JSONException e) {
        } catch (Exception e) {
        }
    }

    private class GetNearbyData extends AsyncTask<Object, String, String> {
        String googlePlacesData;
        GoogleMap mMap;
        String placeType;
        int buttonIndex;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(Object... params) {
            try {
                HousingLogs.d("GetNearbyPlacesData", "doInBackground entered");
                mMap = (GoogleMap) params[0];
                URL url = new URL((String) params[1]);
                placeType = (String) params[2];
                buttonIndex = (int) params[3];
                googlePlacesData = DataParser.getData(url);
                HousingLogs.d("GooglePlacesReadTask", "doInBackground Exit");
            } catch (Exception e) {
                HousingLogs.e("GooglePlacesReadTask", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            HousingLogs.d("GooglePlacesReadTask", "onPostExecute Entered");
            List<HashMap<String, String>> nearbyPlacesList = null;
            //HousingLogs.d(TAG, "result->" + result.toString());
            DataParser dataParser = new DataParser();
            nearbyPlacesList = dataParser.parse(result);
            if (nearbyPlacesList != null && nearbyPlacesList.isEmpty()) {
                int i = 0;
                while (!placeType.equals(placesList[i])) {
                    i++;
                }
                Toast.makeText(ExploreNeighbourhood.this, "No " + toastList[i] + " nearby.", Toast.LENGTH_SHORT).show();
            }
            if(nearbyPlacesList != null){
                toggleButton[buttonIndex].setChecked(true);
            }
            showNearbyPlaces(placeType, nearbyPlacesList);
            listofPlacesList.put(placeType, nearbyPlacesList);
            HousingLogs.d("GooglePlacesReadTask", "onPostExecute Exit");
        }
    }

    private void showNearbyPlaces(String placeType, List<HashMap<String, String>> nearbyPlacesList) {
        if (nearbyPlacesList == null) {
            return;
        }
        prevSelectedMarker = null;
        prevMarkerCheckedType = "";
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            LatLng latLng = new LatLng(lat, lng);

            markerOptions.position(latLng);
            markerOptions.title(placeName);

            float[] result = new float[1];
            Location.distanceBetween(homeLatLng.latitude, homeLatLng.longitude, lat, lng, result);

            Log.d(TAG, "distance=" + (double) result[0] / 1000);
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            float distance = Float.valueOf(twoDForm.format(result[0] / 1000));

            markerOptions.snippet(distance + " km");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(getIconId(placeType)));

            mMap.addMarker(markerOptions);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLatLng));
            if (placeType.equals(placesList[1])) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            }else if (placeType.equals(placesList[2])) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
            } else {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            }
        }
    }

    private void clearPlaces() {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(homeLatLng).title(homeTitle))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_explore_lite));
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLatLng));
// For multi-selector
/*        for (int i = 0; i < checkList.length; i++) {
            if (checkList[i]) {
                showNearbyPlaces(placesList[i], listofPlacesList.get(placesList[i]));
            }
        }
*/
    }

    private int getIconId(String placeType) {
        prevMarkerCheckedType = placeType;
        if (placeType.equals(placesList[0])) {
            return R.drawable.busstop_marker;
        } else if (placeType.equals(placesList[1])) {
            return R.drawable.railway_marker;
        } else if (placeType.equals(placesList[2])) {
            return R.drawable.airport_marker;
        } else if (placeType.equals(placesList[3])) {
            return R.drawable.school_marker;
        } else if (placeType.equals(placesList[4])) {
            return R.drawable.rest_marker;
        } else if (placeType.equals(placesList[5])) {
            return R.drawable.grocery_marker;
        } else if (placeType.equals(placesList[6])) {
            return R.drawable.atm_marker;
        } else if (placeType.equals(placesList[7])) {
            return R.drawable.hospital_marker;
        } else if (placeType.equals(placesList[8])) {
            return R.drawable.park_marker;
        } else if (placeType.equals(placesList[9])) {
            return R.drawable.petrol_pump_marker;
        } else return R.drawable.camera_icon;
    }

    private int getSelectedMarkerIconId() {
        if (checkList[0]) {
            return R.drawable.busstop_marker_selected;
        } else if (checkList[1]) {
            return R.drawable.railway_marker_selected;
        } else if (checkList[2]) {
            return R.drawable.airport_marker_selected;
        } else if (checkList[3]) {
            return R.drawable.school_marker_selected;
        } else if (checkList[4]) {
            return R.drawable.rest_marker_selected;
        } else if (checkList[5]) {
            return R.drawable.grocery_marker_selected;
        } else if (checkList[6]) {
            return R.drawable.atm_marker_selected;
        } else if (checkList[7]) {
            return R.drawable.hospital_marker_selected;
        } else if (checkList[8]) {
            return R.drawable.park_marker_selected;
        } else if (checkList[9]) {
            return R.drawable.petrol_pump_marker_selected;
        } else return R.drawable.camera_icon;
    }

    private boolean checkIfEmpty(int i) {
        try {
            if (listofPlacesList.get(placesList[i]).isEmpty()) {
                Toast.makeText(ExploreNeighbourhood.this, "No " + toastList[i] + " nearby.", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    long compountBtnLastClkedTime = 0;
    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            /*Log.i(TAG, "onCheckedChanged: Last clicked time -> " + compountBtnLastClkedTime);
            Log.i(TAG, "onCheckedChanged: elapsed time -> " + SystemClock.elapsedRealtime());
            if (SystemClock.elapsedRealtime() - compountBtnLastClkedTime < 1500){
                buttonView.setChecked(!isChecked);
                return;
            }
            compountBtnLastClkedTime = SystemClock.elapsedRealtime();*/
            Log.d(TAG, "onCheckChanged!");
            if (!UtilityMethods.isInternetConnected(ExploreNeighbourhood.this)) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.explore_neighbour_activity),
                        R.string.network_error_msg, Snackbar.LENGTH_INDEFINITE);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(ExploreNeighbourhood.this, R.color.uber_red));
                snackbar.setActionTextColor(ContextCompat.getColor(ExploreNeighbourhood.this, R.color.white));
                snackbar.setAction("", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCheckedChanged(buttonView, isChecked);
                    }
                });
                snackbar.show();
                if (isChecked) {
                    buttonView.setChecked(false);
                }
                return;
            }
//for single selector
            clearPlaces();
            Log.d(TAG, "After check!");
            switch (buttonView.getId()) {
                case R.id.bus_stops:
                    Log.d(TAG, "groceries!! " + isChecked);
                    checkList[0] = isChecked;
                    if (isChecked) {
                        clearSelections(0);
                        if (listofPlacesList.containsKey(placesList[1])) {
                            if (checkIfEmpty(0)) {
                                break;
                            }
                            showNearbyPlaces(placesList[0], listofPlacesList.get(placesList[0]));
                        } else {
                            //call async task to add groceries
                            getPlaceData(mMap, placesList[0], proximityRadius[0], 0);
                        }
                        TrackAnalytics.trackEvent("Nearby_BusStop_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        //clear groceries from Map
                        clearPlaces();
                    }
                    break;

                case R.id.railways:
                    checkList[1] = isChecked;
                    if (isChecked) {
                        clearSelections(1);
                        if (listofPlacesList.containsKey(placesList[1])) {
                            if (checkIfEmpty(1)) {
                                break;
                            }
                            showNearbyPlaces(placesList[1], listofPlacesList.get(placesList[1]));
                        } else {
                            getPlaceData(mMap, placesList[1], proximityRadius[1], 1);
                        }
                        TrackAnalytics.trackEvent("Nearby_Railways_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        clearPlaces();
                    }
                    break;
                case R.id.airports:
                    checkList[2] = isChecked;
                    if (isChecked) {
                        clearSelections(2);
                        if(nearByAirportList.isEmpty()){
                            nearByViewModel.getNearByAirport(homeLatLng.latitude,homeLatLng.longitude);
                        }else{
                            showNearByAirport();
                        }
                        /*if (listofPlacesList.containsKey(placesList[2])) {
                            if (checkIfEmpty(2)) {
                                break;
                            }
                            showNearbyPlaces(placesList[2], listofPlacesList.get(placesList[2]));
                        } else {
                            getPlaceData(mMap, placesList[2], proximityRadius[2],2);
                        }*/
                        TrackAnalytics.trackEvent("Nearby_airports_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        clearPlaces();
                    }
                    break;
                case R.id.schools:
                    checkList[3] = isChecked;
                    if (isChecked) {
                        clearSelections(3);
                        if (listofPlacesList.containsKey(placesList[3])) {
                            if (checkIfEmpty(3)) {
                                break;
                            }
                            showNearbyPlaces(placesList[3], listofPlacesList.get(placesList[3]));
                        } else {
                            getPlaceData(mMap, placesList[3], proximityRadius[3],3);
                        }
                        TrackAnalytics.trackEvent("Nearby_schools_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        clearPlaces();
                    }
                    break;
                case R.id.restaurants:
                    checkList[4] = isChecked;
                    if (isChecked) {
                        clearSelections(4);
                        if (listofPlacesList.containsKey(placesList[4])) {
                            if (checkIfEmpty(4)) {
                                break;
                            }
                            showNearbyPlaces(placesList[4], listofPlacesList.get(placesList[4]));
                        } else {
                            getPlaceData(mMap, placesList[4], proximityRadius[4],4);
                        }
                        TrackAnalytics.trackEvent("Nearby_restaurants_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        clearPlaces();
                    }
                    break;
                case R.id.groceries:
                    checkList[5] = isChecked;
                    if (isChecked) {
                        clearSelections(5);
                        if (listofPlacesList.containsKey(placesList[5])) {
                            if (checkIfEmpty(5)) {
                                break;

                                //Toast.makeText(ExploreNeighbourhood.this, "No "+toastList[5]+" nearby.",Toast.LENGTH_SHORT).show();
                            }
                            showNearbyPlaces(placesList[5], listofPlacesList.get(placesList[5]));
                        } else {
                            getPlaceData(mMap, placesList[5], proximityRadius[5],5);
                        }
                        TrackAnalytics.trackEvent("Nearby_groceries_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        clearPlaces();
                    }
                    break;
                case R.id.atms:
                    checkList[6] = isChecked;
                    if (isChecked) {
                        clearSelections(6);
                        if (listofPlacesList.containsKey(placesList[6])) {
                            if (checkIfEmpty(6)) {
                                break;
                            }
                            showNearbyPlaces(placesList[6], listofPlacesList.get(placesList[6]));
                        } else {
                            getPlaceData(mMap, placesList[6], proximityRadius[6],6);
                        }
                        TrackAnalytics.trackEvent("Nearby_atms_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        clearPlaces();
                    }
                    break;
                case R.id.hospitals:
                    checkList[7] = isChecked;
                    if (isChecked) {
                        clearSelections(7);
                        if (listofPlacesList.containsKey(placesList[7])) {
                            if (checkIfEmpty(7)) {
                                break;
                            }
                            showNearbyPlaces(placesList[7], listofPlacesList.get(placesList[7]));
                        } else {
                            getPlaceData(mMap, placesList[7], proximityRadius[7],7);
                        }
                        TrackAnalytics.trackEvent("Nearby_hospitals_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        clearPlaces();
                    }
                    break;
                case R.id.parks:
                    checkList[8] = isChecked;
                    if (isChecked) {
                        clearSelections(8);
                        if (listofPlacesList.containsKey(placesList[8])) {
                            if (checkIfEmpty(8)) {
                                break;
                            }
                            showNearbyPlaces(placesList[8], listofPlacesList.get(placesList[8]));
                        } else {
                            getPlaceData(mMap, placesList[8], proximityRadius[8],8);
                        }
                        TrackAnalytics.trackEvent("Nearby_parks_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        clearPlaces();
                    }
                    break;
                case R.id.petrol_pumps:
                    checkList[9] = isChecked;
                    if (isChecked) {
                        clearSelections(9);
                        if (listofPlacesList.containsKey(placesList[9])) {
                            if (checkIfEmpty(9)) {
                                break;
                            }
                            showNearbyPlaces(placesList[9], listofPlacesList.get(placesList[9]));
                        } else {
                            getPlaceData(mMap, placesList[9], proximityRadius[9],9);
                        }
                        TrackAnalytics.trackEvent("Nearby_petrolpumps_click", Constants.AppConstants.HOLD_TIME ,
                                1, ExploreNeighbourhood.this);
                    } else {
                        clearPlaces();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private void clearSelections(int id) {
        for (int i = 0; i < toggleButton.length; i++) {
            if (i != id) {
                toggleButton[i].setChecked(false);
            } else {
                Log.d(TAG, "Not cleared " + id);
            }
        }
    }

    long onStartTime;
    @Override
    protected void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TrackAnalytics.trackEvent("ExploreAreaMapActivity", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);
    }

    private void getCurrentLocation(){
        if (PermissionManager.checkIfAlreadyhasPermission(ExploreNeighbourhood.this, PERMISSIONS)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                    ExploreNeighbourhood.this);
            mNavigateButton.setEnabled(true);
        }else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PermissionManager.requestForSpecificPermission(this, PERMISSIONS, REQUEST_PERMISSIONS_CODE);
        }else {
            mNavigateButton.setEnabled(true);
            Toast.makeText(ExploreNeighbourhood.this, "Permission denied!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private class GetAddressFromLocTask extends AsyncTask<Void, Void, String[]>{
        Location currentLocation;
        GetAddressFromLocTask(Location currentLoc){
            this.currentLocation = currentLoc;
        }
        @Override
        protected String[] doInBackground(Void... voids) {
            String[] addressArr = new String[2];
            Geocoder geoCoder = new Geocoder(ExploreNeighbourhood.this, Locale.getDefault());
            StringBuilder currLocbuilder = new StringBuilder();
            StringBuilder projectLocbuilder = new StringBuilder();
            try {
                List<Address> currentAddress = geoCoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                List<Address> projectAddress = geoCoder.getFromLocation(homeLatLng.latitude, homeLatLng.longitude, 1);
                if(currentAddress != null && !currentAddress.isEmpty()) {
                    int maxLines = currentAddress.get(0).getMaxAddressLineIndex();
                    for (int i = 0; i < maxLines; i++) {
                        String addressStr = currentAddress.get(0).getAddressLine(i);
                        if (!TextUtils.isEmpty(addressStr)) {
                            currLocbuilder.append(addressStr);
                            if (addressStr.charAt(addressStr.length() - 1) != ',') {
                                currLocbuilder.append(" ");
                            }
                        }
                    }
                }
                if(projectAddress != null && !projectAddress.isEmpty()) {
                    int maxLines = currentAddress.get(0).getMaxAddressLineIndex();
                    for (int i = 0; i < maxLines; i++) {
                        String projectaddStr = projectAddress.get(0).getAddressLine(i);
                        if (!TextUtils.isEmpty(projectaddStr)) {
                            projectLocbuilder.append(projectaddStr);
                            if (projectaddStr.charAt(projectaddStr.length() - 1) != ',') {
                                projectLocbuilder.append(" ");
                            }
                        }
                    }
                }
                addressArr[0] = currLocbuilder.toString().trim();
                addressArr[1] = projectLocbuilder.toString().trim();
                Log.d(TAG, "current loc complete address->" + currLocbuilder.toString()); //This is the complete address.
                Log.d(TAG, "project loc complete address->" + projectLocbuilder.toString()); //This is the complete address.
                Log.d(TAG, "city->" + currentAddress.get(0).getLocality());
            } catch (IOException e) {
                Log.i(TAG, "IOException: " + e.getMessage());
                e.printStackTrace();
            }catch (Exception e) {
                Log.i(TAG, "Exception: " + e.getMessage());
                e.printStackTrace();
            }
            return addressArr;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            mNavigateButton.setEnabled(true);
            dismissProgressDialog();
            String uri="";
            if(result != null && !TextUtils.isEmpty(result[0]) && !TextUtils.isEmpty(result[1])){
                uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f(%s)",
                        currentLocation.getLatitude(), currentLocation.getLongitude(),result[0],
                        homeLatLng.latitude, homeLatLng.longitude,(homeTitle+" "+result[1]));
            }else{
                uri = "http://maps.google.com/maps?saddr="+currentLocation.getLatitude()+","+currentLocation.getLongitude()
                        +"&daddr="+homeLatLng.latitude+","+homeLatLng.longitude;
            }
            Log.i(TAG, "Navigation MAP URI -> " + uri);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            //intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
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
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Success!");
                        //getCurrentLocation();
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                                ExploreNeighbourhood.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "ResolutionRequired!");
                        try {
                            dismissProgressDialog();
                            status.startResolutionForResult(ExploreNeighbourhood.this, LOCATION_SETTING_CHANGE_CODE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Unavailabe");
                        dismissProgressDialog();
                        break;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mNavigateButton.setEnabled(true);
        if (requestCode == LOCATION_SETTING_CHANGE_CODE && resultCode == RESULT_OK) {
            if (PermissionManager.checkIfAlreadyhasPermission(this, PERMISSIONS)) {
                showProgressDialog();
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                        this);
            } else {
                dismissProgressDialog();
                UtilityMethods.showErrorSnackBar(findViewById(R.id.explore_neighbour_activity),
                        getResources().getString(R.string.enable_gps_location),Snackbar.LENGTH_LONG);
                Log.d(TAG, "reqCode->" + requestCode);
            }
            //getCurrentLocation();
        }else{
            dismissProgressDialog();
            UtilityMethods.showErrorSnackBar(findViewById(R.id.explore_neighbour_activity),
                    getResources().getString(R.string.enable_gps_location),Snackbar.LENGTH_LONG);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: ");
        mNavigateButton.setEnabled(true);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                //getCurrentLocation();
                showProgressDialog();
                if(PermissionManager.checkIfAlreadyhasPermission(this, PERMISSIONS)) {
                    requestPermissionForLocation();
                }else{
                    dismissProgressDialog();
                    UtilityMethods.showErrorSnackBar(findViewById(R.id.explore_neighbour_activity),
                            getResources().getString(R.string.permision),Snackbar.LENGTH_LONG);
                }
                break;
            default:
                Log.d(TAG, "code->" + requestCode);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                if(mGoogleApiClient.isConnected()){
                    mGoogleApiClient.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showProgressDialog(){
        try {
            dismissProgressDialog();
            progressDialog = new CustomProgressDialog(this);
            progressDialog.setCancelable(true);
            if(!isFinishing() && !isDestroyed())
                progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void dismissProgressDialog(){
        try {
            if(progressDialog!=null  && progressDialog.isShowing() && !isDestroyed())
                progressDialog.dismiss();
            progressDialog=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(NearbyResponse nearbyResponse) {
        Log.i(TAG, "onSuccess: " + nearbyResponse.getNearbyList());
        if(nearbyResponse.getNearbyList() != null && !nearbyResponse.getNearbyList().isEmpty()) {
            if(nearByAirportList != null){
                nearByAirportList.clear();
            }
            nearByAirportList.addAll(nearbyResponse.getNearbyList());
            showNearByAirport();
        }
    }

    @Override
    public void onError(String errMsg) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
    }

    private void showNearByAirport(){
        prevSelectedMarker = null;
        prevMarkerCheckedType = "";
        for (int i = 0; i < nearByAirportList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            Nearby nearby = nearByAirportList.get(i);
            double lat = nearby.getLat();
            double lng = nearby.getLng();
            String placeName = nearby.getName()!= null ? nearby.getName() : "airport";
            LatLng latLng = new LatLng(lat, lng);

            markerOptions.position(latLng);
            markerOptions.title(placeName);

            float[] result = new float[1];
            Location.distanceBetween(homeLatLng.latitude, homeLatLng.longitude, lat, lng, result);

            Log.d(TAG, "distance=" + (double) result[0] / 1000);
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            float distance = Float.valueOf(twoDForm.format(result[0] / 1000));

            markerOptions.snippet(distance + " km");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(getIconId(placesList[2])));
            mMap.addMarker(markerOptions);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
        }
    }
}
