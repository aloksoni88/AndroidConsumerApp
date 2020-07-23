package com.clicbrics.consumer.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.City;
import com.buy.housing.backend.userEndPoint.model.CityCollection;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.CityAdapter;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.framework.activity.SplashActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.CustomTypeFace;
import com.clicbrics.consumer.utils.PermissionManager;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static com.clicbrics.consumer.view.activity.HomeScreen.searchLocation;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("deprecation")
public class PickCityFragment extends BaseFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = PickCityFragment.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_CODE = 4;
    private static final int LOCATION_SETTING_CHANGE_CODE = 1000;
    public static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private GoogleApiClient googleApiClient;
    private double longitude = Constants.AppConstants.DEFAULT_LONG;
    private double latitude = Constants.AppConstants.DEFAULT_LAT;
    private FrameLayout rootLayout;
    ListView listView;
    public static List<City> filtercityList;
    private EditText citysearch;
    private final String[] cityList = {"Faridabad", "Gurugram", "Mumbai", "New Delhi", "Noida"};
    CityAdapter cityAdapter;
    Button currentLocation;
    private final int TOTAL_SECONDS_IN_A_DAY = 86400;
    private CityCollection cityCollection;
    private ImageView search_icon,id_cancel_btn;
    private ImageButton backbutton;

    public PickCityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pick_city, container, false);
        rootLayout = (FrameLayout) view.findViewById(R.id.city_list_root_layout);
        listView = (ListView) view.findViewById(R.id.list_pick_city);
        citysearch = (EditText) view.findViewById(R.id.citysearch);
        search_icon = (ImageView) view.findViewById(R.id.search_icon);
        id_cancel_btn = (ImageView) view.findViewById(R.id.id_cancel_btn);
        backbutton = (ImageButton) view.findViewById(R.id.backbutton);

        return view;
    }

    long mLastClickTime;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UtilityMethods.hideKeyboard(getActivity());
        citysearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null && !TextUtils.isEmpty(s.toString().trim()))
                {
                    id_cancel_btn.setVisibility(View.VISIBLE);

                }
                else
                {
                    id_cancel_btn.setVisibility(View.INVISIBLE);
                }
                cityAdapter.getFilter().filter(s.toString());
            }
        });

        String saveCity = UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.SAVED_CITY, "");
        if(TextUtils.isEmpty(saveCity))
        {
            backbutton.setVisibility(View.GONE);
        }
        else
        {
            backbutton.setVisibility(View.VISIBLE);
        }
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        id_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                citysearch.setText("");
                id_cancel_btn.setVisibility(View.INVISIBLE);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        googleApiClient.connect();
        getCityList();

        currentLocation = (Button) getActivity().findViewById(R.id.use_current_location);

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                if (getActivity() != null) {
                    TrackAnalytics.trackEvent("PickCity_current_loc", Constants.AppConstants.HOLD_TIME,
                            1, getActivity());
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                //showProgressBar();
                if (getActivity() != null && !UtilityMethods.isInternetConnected(getActivity())) {
                    UtilityMethods.showErrorSnackBar(rootLayout, getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
                    return;
                }
                if (cityAdapter != null) {
                    cityAdapter.setSelectedItem(-2);
                    cityAdapter.notifyDataSetChanged();
                    UtilityMethods.clearPreference(getActivity(), Constants.AppConstants.SAVED_CITY);
                    UtilityMethods.clearPreference(getActivity(), Constants.AppConstants.SAVED_STATE);
                    UtilityMethods.clearPreference(getActivity(), Constants.AppConstants.SAVED_PNAME);
                }
                showProgressBar();
                Log.d(TAG, "Clicked!!");
                if (!PermissionManager.checkIfAlreadyhasPermission(getActivity(), PERMISSIONS)) {
                    Log.d(TAG, "Requesting!!");
                    dismissProgressBar();
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSIONS_CODE);
                } else {
                    Log.d(TAG, "RequestLocation!!");
                    requestPermissionForLocation();
                    //getCurrentLocation();
                }
            }
        });


    }

    LocationRequest mLocationRequest;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Success!");
                        //getCurrentLocation();
                        /*if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }*/
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest,
                                PickCityFragment.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "ResolutionRequired!");
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            dismissProgressBar();
                            status.startResolutionForResult(getActivity(), LOCATION_SETTING_CHANGE_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Unavailabe");
                        dismissProgressBar();
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Failed to get your loacation", Toast.LENGTH_SHORT).show();
    }

    List<Address> address = null;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void getCurrentLocation() {
//        mMap.clear();
        //Creating a location object
        showProgressBar();
        if (PermissionManager.checkIfAlreadyhasPermission(getActivity(), PERMISSIONS)) {
            requestPermissionForLocation();
            /*LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest,
                    PickCityFragment.this);*/

        }else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //PermissionManager.requestForSpecificPermission(getActivity(), PERMISSIONS, REQUEST_PERMISSIONS_CODE);
            ActivityCompat.requestPermissions(getActivity(),PERMISSIONS,REQUEST_PERMISSIONS_CODE);
        }
        else{
            dismissProgressBar();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTING_CHANGE_CODE && resultCode == RESULT_OK) {
            if (PermissionManager.checkIfAlreadyhasPermission(getActivity(), PERMISSIONS)) {
                showProgressBar();
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest,
                        PickCityFragment.this);
            } else {
                dismissProgressBar();
                UtilityMethods.showErrorSnackBar(rootLayout,getResources().getString(R.string.enable_gps_location),Snackbar.LENGTH_LONG);
                Log.d(TAG, "reqCode->" + requestCode);
            }

        }else{
            dismissProgressBar();
            UtilityMethods.showErrorSnackBar(rootLayout,getResources().getString(R.string.enable_gps_location),Snackbar.LENGTH_LONG);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                //getCurrentLocation();
                showProgressBar();
                if(PermissionManager.checkIfAlreadyhasPermission(getActivity(), PERMISSIONS)) {
                    requestPermissionForLocation();
                }else{
                    dismissProgressBar();
                    UtilityMethods.showErrorSnackBar(rootLayout,getResources().getString(R.string.permision),Snackbar.LENGTH_LONG);
                }
                break;
            default:
                Log.d(TAG, "code->" + requestCode);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void getCityList() {
        showProgressBar();
        long cityListFetchTime = UtilityMethods.getLongInPref(getActivity(),Constants.SharedPreferConstants.CITY_LIST_FETCH_TIME,0);
        long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if(cityListFetchTime != 0 && (currentTime-cityListFetchTime) < TOTAL_SECONDS_IN_A_DAY ) {
            loadCityFromLocal();
        } else{
            if (!UtilityMethods.isInternetConnected(getActivity())) {
                showNetworkErrorSnackbar();
                new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.PickCity,
                        null, Constants.ApiName.getCityListAll.toString(),Constants.AnalyticsEvents.FAILED,getResources().getString(R.string.network_error_msg));
                return;
            }
            rootLayout.findViewById(R.id.use_current_location).setVisibility(View.INVISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String errorMsg = "";
                    try {
                        UserEndPoint userEndPoint = EndPointBuilder.getUserEndPoint();
                        final CityCollection cityList = userEndPoint.getCityListAll()
                                .setLive(true)
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();

                        if (cityList != null) {
                            mHandler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                                @Override
                                public void run() {
                                    UtilityMethods.saveLongInPref(getActivity(), Constants.SharedPreferConstants.CITY_LIST_FETCH_TIME,
                                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                                    /*Set<String> citySet = new LinkedHashSet<String>();
                                    Set<String> cityIdSet = new LinkedHashSet<String>();
                                    for (int i = 0; i < cityList.getItems().size(); i++) {
                                        citySet.add(cityList.getItems().get(i).getName());
                                        cityIdSet.add(String.valueOf(cityList.getItems().get(i).getId()));
                                    }
                                    UtilityMethods.saveCityListInPref(getActivity(),citySet , cityIdSet);*/
                                    UtilityMethods.putCityListInPrefs(getActivity(), cityList.getItems());
                                    dismissProgressBar();
                                    new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.PickCity,
                                            null, Constants.ApiName.getCityListAll.toString(),Constants.AnalyticsEvents.SUCCESS,null);
                                    setCityList(cityList);

                                }
                            });
                        } else {
                            errorMsg = "No city found";
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        errorMsg = getResources().getString(R.string.network_error_msg);
                    } catch (Exception e) {
                        errorMsg = getString(R.string.something_went_wrong);
//                    AnalyticsTrackers.trackException(e);
                        e.printStackTrace();

                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //rootLayout.findViewById(R.id.current_location_layout).setVisibility(View.VISIBLE);
                            rootLayout.findViewById(R.id.use_current_location).setVisibility(View.VISIBLE);
                        }
                    });

                    if (!TextUtils.isEmpty(errorMsg)) {
                        final String errmsg = errorMsg;
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                if(!loadCityFromLocal()){
                                    dismissProgressBar();
                                    new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.PickCity,
                                            null, Constants.ApiName.getCityListAll.toString(),Constants.AnalyticsEvents.FAILED,errmsg);
                                    UtilityMethods.showErrorSnackBar(rootLayout, errmsg, Snackbar.LENGTH_LONG);
                                    listView.setEmptyView(getActivity().findViewById(android.R.id.empty));
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }


    private void setCityList(final CityCollection cityList) {
        listView.setEmptyView(getActivity().findViewById(android.R.id.empty));
        cityCollection = cityList;
        filtercityList = cityList.getItems();
        Collections.sort(cityList.getItems(), new Comparator<City>() {
            @Override
            public int compare(City lhs, City rhs) {
                return rhs.getName().compareToIgnoreCase(lhs.getName());
            }
        });

        cityAdapter = new CityAdapter(getActivity(), cityList.getItems());
        listView.setAdapter(cityAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(getActivity() != null) {
                    TrackAnalytics.trackEvent("ChangeCity_event", Constants.AppConstants.HOLD_TIME,
                            1, getActivity());
                }
                if (getActivity() != null && !UtilityMethods.isInternetConnected(getActivity())) {
                    UtilityMethods.showErrorSnackBar(rootLayout,getResources().getString(R.string.network_error_msg),Snackbar.LENGTH_LONG);
                    return;
                }
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.PickCity,
                        filtercityList.get(position), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.SelectCityclick.toString());
                Log.i(TAG, "onItemClick: ======="+filtercityList.get(position).getPName()
                +"id"+filtercityList.get(position));
                UtilityMethods.saveStringInPref(getActivity(),
                        Constants.AppConstants.SAVED_PNAME, filtercityList.get(position).getPName());
                UtilityMethods.saveStringInPref(getActivity(),
                        Constants.AppConstants.SAVED_CITY, filtercityList.get(position).getName());
                UtilityMethods.saveLongInPref(getActivity(),
                        Constants.AppConstants.SAVED_CITY_ID, filtercityList.get(position).getId());
                UtilityMethods.saveStringInPref(getActivity(),Constants.AppConstants.SAVED_STATE,
                        filtercityList.get(position).getState());
                searchLocation=new LatLng(filtercityList.get(position).getLat(),
                        filtercityList.get(position).getLng());

//                Constants.CITY_LAT=cityList.getItems().get(position).getLat();
//                Constants.CITY_LONG=cityList.getItems().get(position).getLng();
               /* UtilityMethods.saveLongInPref(getActivity(),Constants.CITY_LAT,
                        cityList.getItems().get(position).getLat().longValue());
                UtilityMethods.saveLongInPref(getActivity(),Constants.CITY_LONG,
                        cityList.getItems().get(position).getLng().longValue());*/

                cityAdapter.setSelectedItem(position);
                cityAdapter.notifyDataSetChanged();
                openRequirements();
            }
        });

        UtilityMethods.putCityListInPrefs(getActivity(), cityList.getItems());

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean loadCityFromLocal(){
        try {
            ArrayList<com.clicbrics.consumer.utils.City> cityList = UtilityMethods.getCityList(getActivity());
            CityCollection cityCollection = new CityCollection();
            List<City> cities = new ArrayList<>();
            if(cityList != null && cityList.size() > 0){
                int size = cityList.size();
                for(int i=0; i< size ; i++){
                    com.clicbrics.consumer.utils.City city = cityList.get(i);
                    if(city != null) {
                        City endPointCity = new City();
                        endPointCity.setId(city.getId());
                        endPointCity.setName(city.getName());
                        endPointCity.setCountry(city.getCountry());
                        endPointCity.setPName(city.getpName());
                        endPointCity.setState(city.getState());
                        endPointCity.setLat((double) city.getLat());
                        endPointCity.setLng((double) city.getLng());
                        cities.add(endPointCity);
                    }
                }
                cityCollection.setItems(cities);
                setCityList(cityCollection);
                dismissProgressBar();
                return true;
            }
            dismissProgressBar();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void openRequirements() {
        Intent intent;
        dismissProgressBar();
        if(UtilityMethods.getBooleanInPref(getActivity(),Constants.SharedPreferConstants.IS_USING_DL,false)){
            UtilityMethods.saveBooleanInPref(getActivity(),Constants.SharedPreferConstants.IS_USING_DL,false);
            intent = new Intent(getActivity(), HomeScreen.class);
            startActivity(intent);
            getActivity().finish();
        }
        else if (UtilityMethods.getBooleanInPref(getActivity(), Constants.SharedPreferConstants.FIRST_LAUNCH, false)) {
            intent = new Intent(getActivity(), HomeScreen.class);
            intent.putExtra(Constants.IntentKeyConstants.FIRST_TIME, true);
            startActivity(intent);
            getActivity().finish();
        } else {
            getActivity().setResult(RESULT_OK);
            getActivity().finish();
            /*intent = new Intent(getActivity(), HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();*/
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showNetworkErrorSnackbar(){
        dismissProgressBar();
        final Snackbar snackbar = Snackbar
                .make(rootLayout, getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_INDEFINITE);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));
                snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                        getCityList();
                    }
                });
        snackbar.show();
    }

    long onStartTime;
    @Override
    public void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(getActivity() != null) {
            TrackAnalytics.trackEvent("ChangeCityPage", Constants.AppConstants.HOLD_TIME,
                    UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), getActivity());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: Location -> " + location);
        if (location == null) {
            if (!PermissionManager.checkIfAlreadyhasPermission(getActivity(), PERMISSIONS)) {
                dismissProgressBar();
                UtilityMethods.showErrorSnackBar(rootLayout,getResources().getString(R.string.permision),Snackbar.LENGTH_LONG);
                return;
            }
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                if (googleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                }
                doProcessCurrentLocation();
            }else{
                dismissProgressBar();
                UtilityMethods.showErrorSnackBar(rootLayout,"Failed to get your location",Snackbar.LENGTH_LONG);
                return;
            }
        }
        else {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if(googleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
            doProcessCurrentLocation();
        }
    }

    String cityName = "";
    private void doProcessCurrentLocation(){
        Log.d(TAG, "lat,lon->" + latitude + " " + longitude);

        //try {
        new Thread(new Runnable() {
            String error = "";
            @Override
            public void run() {
                Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
                StringBuilder builder = new StringBuilder();
                try {
                    address = geoCoder.getFromLocation(latitude, longitude, 1);
                    int maxLines = address.get(0).getMaxAddressLineIndex();
                    for (int i = 0; i < maxLines; i++) {
                        String addressStr = address.get(0).getAddressLine(i);
                        builder.append(addressStr);
                        builder.append(" ");
                    }
                    cityName = "";
                    Log.d(TAG, "complete address->" + builder.toString()); //This is the complete address.
                    Log.d(TAG, "city->" + address.get(0).getLocality());
                } catch (IOException e) {
                    e.printStackTrace();
                    Location location = new Location("Location");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    getAddressFromLocation(location);
                    return;
                }catch (Exception e) {
                    error = "Could not find your location.\n Please try again!";
                    e.printStackTrace();
                }
                if(!TextUtils.isEmpty(error)){
                    mHandler.post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void run() {
                            dismissProgressBar();
                            UtilityMethods.showErrorSnackBar(rootLayout,error,Snackbar.LENGTH_LONG);
                        }
                    });
                }else {
                    try {
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                if ( (address != null && !address.isEmpty() && address.get(0) != null
                                        && address.get(0).getLocality() != null) || !cityName.trim().isEmpty() ) {
                                    if(cityCollection != null && cityCollection.size() > 0) {
                                        boolean isCityExist = false;
                                        String pName = "";
                                        double lat=0;
                                        double lng=0;
                                        for(City city : cityCollection.getItems()){
                                            if(!cityName.trim().isEmpty() && city.getName().equalsIgnoreCase(cityName)){
                                                isCityExist = true;
                                                pName = city.getPName();
                                                lat=city.getLat();
                                                lng=city.getLng();
                                                if(address == null){
                                                    address = new ArrayList<>();
                                                }
                                                Address add = new Address(Locale.getDefault());
                                                add.setLocality(cityName);
                                                address.set(0,add);
                                                break;
                                            }
                                            else if(city.getName().equalsIgnoreCase(address.get(0).getLocality())){
                                                isCityExist = true;
                                                pName = city.getPName();
                                                lat=city.getLat();
                                                lng=city.getLng();
                                                break;
                                            }
                                        }
                                        if(isCityExist){
                                            if(!TextUtils.isEmpty(pName)) {
                                                UtilityMethods.saveStringInPref(getActivity(),
                                                        Constants.AppConstants.SAVED_PNAME, pName);
                                            }
                                            UtilityMethods.saveStringInPref(getActivity(),
                                                    Constants.AppConstants.SAVED_CITY, address.get(0).getLocality());
                                            if (address.get(0).getAdminArea() != null) {
                                                UtilityMethods.saveStringInPref(getActivity(),
                                                        Constants.AppConstants.SAVED_STATE, address.get(0).getAdminArea());
                                            }
                                            if (cityAdapter != null) {
                                                ArrayList<com.clicbrics.consumer.utils.City> cityListMapping = UtilityMethods.getCityList(getActivity());

                                                long cityId = 0;

                                                for (com.clicbrics.consumer.utils.City city : cityListMapping)
                                                    if (city.getName().equalsIgnoreCase(address.get(0).getLocality())) {
                                                        cityId = city.getId();
                                                    }
                                                if (cityId != 0) {
                                                    UtilityMethods.saveLongInPref(getActivity(),
                                                            Constants.AppConstants.SAVED_CITY_ID, cityId);
                                                }
                                                searchLocation=new LatLng(lat,lng);
//                                                Constants.CITY_LAT=address.get(0).getLatitude();
//                                                Constants.CITY_LONG=address.get(0).getLongitude();
//                                                UtilityMethods.saveLongInPref(getActivity(),Constants.CITY_LAT,(long)address.get(0).getLatitude());
//                                                UtilityMethods.saveLongInPref(getActivity(),Constants.CITY_LONG,(long)address.get(0).getLongitude());
                                            }
                                            openRequirements();
                                        }else{
                                            dismissProgressBar();
                                            UtilityMethods.showSnackBar(rootLayout,getResources().getString(R.string.no_city_found),Snackbar.LENGTH_LONG);
                                        }
                                    }else{
                                        dismissProgressBar();
                                        UtilityMethods.showSnackBar(rootLayout,getResources().getString(R.string.no_city_found),Snackbar.LENGTH_LONG);
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        error = "Could not find your location.\n Please try again!";
                        e.printStackTrace();
                    }
                    if(!TextUtils.isEmpty(error)){
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                dismissProgressBar();
                                UtilityMethods.showErrorSnackBar(rootLayout,error,Snackbar.LENGTH_LONG);
                            }
                        });
                    }
                }
            }
        }).start();
    }


    StringBuilder stringBuilder = new StringBuilder();
    private void getAddressFromLocation(Location location){
        //ArrayList<Address> addressList = new ArrayList<>();
        try {
            String getLocationURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="+ Locale.getDefault().getCountry()+"&key="+getString(R.string.place_api_key);
            final String addressURL = String.format(Locale.getDefault(), getLocationURL, location.getLatitude(), location.getLongitude());
            android.util.Log.i(TAG, "getAddressFromLocation: AddressURL :" + addressURL);
            new Thread(new Runnable() {
                String errorMsg = "";
                @Override
                public void run() {
                    HttpGet httpGet = new HttpGet(addressURL);
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response ;

                    try {
                        response = client.execute(httpGet);
                        HttpEntity entity = response.getEntity();
                        InputStream stream = entity.getContent();
                        int b;
                        while ((b = stream.read()) != -1) {
                            stringBuilder.append((char) b);
                        }
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                try{
                                    android.util.Log.i(TAG, "getAddressFromLocation: StringBuilder : " + stringBuilder.toString());
                                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                                    if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                                        JSONArray results = jsonObject.getJSONArray("results");

                                        JSONObject zero = results.getJSONObject(0);
                                        JSONArray address_components = zero.getJSONArray("address_components");
                                        for (int i = 0; i < address_components.length(); i++) {
                                            JSONObject zero2 = address_components.getJSONObject(i);
                                            String long_name = zero2.getString("long_name");
                                            JSONArray mtypes = zero2.getJSONArray("types");
                                            String Type = mtypes.getString(0);

                                            if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
                                                if (Type.equalsIgnoreCase("locality")) {
                                                    // Address2 = Address2 + long_name + ", ";
                                                    cityName = long_name;
                                                    if(!cityName.trim().isEmpty()){
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if(!TextUtils.isEmpty(cityName)){
                                        if(cityCollection != null && cityCollection.size() > 0) {
                                            boolean isCityExist = false;
                                            String pName = "";
                                            for(City city : cityCollection.getItems()){
                                                if(!cityName.trim().isEmpty() && city.getName().equalsIgnoreCase(cityName)){
                                                    isCityExist = true;
                                                    pName = city.getPName();
                                                    if(address == null){
                                                        address = new ArrayList<>();
                                                    }
                                                    Address add = new Address(Locale.getDefault());
                                                    add.setLocality(cityName);
                                                    address.add(add);
                                                    break;
                                                }
                                            }
                                            if(isCityExist){
                                                if(!TextUtils.isEmpty(pName)) {
                                                    UtilityMethods.saveStringInPref(getActivity(),
                                                            Constants.AppConstants.SAVED_PNAME, pName);
                                                }
                                                UtilityMethods.saveStringInPref(getActivity(),
                                                        Constants.AppConstants.SAVED_CITY, address.get(0).getLocality());
                                                if (address.get(0).getAdminArea() != null) {
                                                    UtilityMethods.saveStringInPref(getActivity(),
                                                            Constants.AppConstants.SAVED_STATE, address.get(0).getAdminArea());
                                                }
                                                if (cityAdapter != null) {
                                                    ArrayList<com.clicbrics.consumer.utils.City> cityListMapping = UtilityMethods.getCityList(getActivity());

                                                    long cityId = 0;

                                                    for (com.clicbrics.consumer.utils.City city : cityListMapping)
                                                        if (city.getName().equalsIgnoreCase(address.get(0).getLocality())) {
                                                            cityId = city.getId();
                                                        }
                                                    if (cityId != 0) {
                                                        UtilityMethods.saveLongInPref(getActivity(),
                                                                Constants.AppConstants.SAVED_CITY_ID, cityId);
                                                    }
                                                }
                                                openRequirements();
                                            }else{
                                                dismissProgressBar();
                                                UtilityMethods.showSnackBar(rootLayout,getResources().getString(R.string.no_city_found),Snackbar.LENGTH_LONG);
                                            }
                                        }else{
                                            dismissProgressBar();
                                            UtilityMethods.showSnackBar(rootLayout,getResources().getString(R.string.no_city_found),Snackbar.LENGTH_LONG);
                                        }
                                    }else{
                                        errorMsg = "Geocoder service is not available";
                                    }
                                    android.util.Log.i(TAG, "getAddressFromLocation: result address : " + cityName);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    errorMsg = "Something went wrong.please retry!";
                                }
                            }
                        });
                        if(!TextUtils.isEmpty(errorMsg)){
                            mHandler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                                @Override
                                public void run() {
                                    dismissProgressBar();
                                    UtilityMethods.showErrorSnackBar(rootLayout,errorMsg,Snackbar.LENGTH_LONG);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorMsg = "Something went wrong.please retry!";
                        stringBuilder = new StringBuilder("");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressBar();
                            }
                        });
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            dismissProgressBar();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if(googleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                if(googleApiClient.isConnected()){
                    googleApiClient.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
