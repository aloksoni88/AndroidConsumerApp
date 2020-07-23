package com.clicbrics.consumer.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.FilterActivity;
import com.clicbrics.consumer.customview.DrawingView;
import com.clicbrics.consumer.databinding.FragmentMapLayoutBinding;
import com.clicbrics.consumer.fragment.BaseFragment;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.helper.ProjectMapResultCallback;
import com.clicbrics.consumer.interfaces.IPropertyOperationsListener;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.utils.CarouselPageTransformer;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.DataParser;
import com.clicbrics.consumer.utils.FilterUtility;
import com.clicbrics.consumer.utils.FloatPointDrawn;
import com.clicbrics.consumer.utils.PermissionManager;
import com.clicbrics.consumer.utils.SortUtility;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.velocityviewpager.VelocityViewPager;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.adapter.MapViewListAdapter;
import com.clicbrics.consumer.viewmodel.ProjectMapViewModel;
import com.clicbrics.consumer.wrapper.SaveSearchWrapper;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;

@SuppressWarnings("deprecation")
public class MapFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, ProjectMapResultCallback,
        SortUtility.ISortSelectionListener {

    private static final String TAG = "MapFragment";
    public static final int REQUEST_PERMISSIONS_CODE = 5;
    public static final int FILTER_REQUEST_CODE = 103;
    public static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private String placesList[] = {"bus_station", "train_station", "airport", "school", "restaurant",
            "grocery_or_supermarket", "atm", "hospital", "park", "petrol_pump"};
    private final int proximityRadius[] = {5000, 10000, 100000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};
    RadioGroup mapTypeGroup;
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private GoogleApiClient googleApiClient;
    float zoomLevel = 11.5f;
    private HashMap<Long, Project> filteredProjectMap = new LinkedHashMap<>();
    private ArrayList<Marker> markerArrayList = new ArrayList<>();
    private HashMap<String,Long> markerProjectIdMap = new HashMap<>();
    List<Marker> nearByMarkers = new ArrayList<Marker>();
    MapViewListAdapter fragmentPagerAdapter;

    private Polygon polygon;
    private ArrayList<LatLng> polygonLatlngList = new ArrayList<LatLng>();

    public FloatingActionButton draw, currentLocation, layers;

    private List<Project> projectList = new ArrayList<>();
    private int totalProjectCount;
    private ProjectMapViewModel model;
    //filter variables
    private Long filterBuilderId = null;
    private String filterPropertyType = null;
    private String filterBedType = null;
    private String filterProjectStatus = null;
    private Integer filterMinPrice = null;
    private Integer filterMaxPrice = null;
    private Integer filterMinArea = null;
    private Integer filterMaxArea = null;
    private String filterSort = null;
    private Long requestId=null;
    private String searchText=null;
    private Boolean getSummary=true;
    private String city=null;
    private String appVersion=null;
    private Long userId=null;
    private String userEmail=null;
    private String userPhone=null;
    private String userName=null;
    private Long virtualId=null;
    private String leadsource=null;
    private String browser=null;
    private String browserVersion=null;
    private String browserType=null;


    private FragmentMapLayoutBinding binding;
    private IPropertyOperationsListener listener;
    private LatLng mapCenterBeforeMoving = null;
    private boolean apiCallOnMovingMap = false; //flag which will indicate weather api has been called while moving map or not
    private static int ON_CAMERA_MOVE_REASON = 0; //camera move reason while map camera move my user or its api animation etc.
    private GroundOverlay groundOverlay1;
    private LatLng prevLatLng = null;
    private boolean dontAnimateCamera = false;

    private String resultTypeValue;

    private enum ResultType{
        onSuccess, onError, onEmpty, onInternet
    }

    public enum DrawBtnState {
        drawing, drawn, notdrawing
    }
    private enum NearbySelection {
        NO_SELECTION, BUS, TRAIN, AIRPORT, SCHOOL, RESTAURANT, MARKET, ATM, HOSPITAL, PARK, PETROL_PUMP
    }
    private DrawBtnState drawBtnState = DrawBtnState.notdrawing;
    private NearbySelection nearbySelected = NearbySelection.NO_SELECTION;
    private BitmapDescriptor selectedCircularView;
    private BitmapDescriptor circleYellowIcon;
    LatLng currentSelProjectLoc;
    private Marker lastMarkerHit;
    boolean isMarkerClick = false;//flag;
    private VelocityViewPager listPager;
    long nearByPlaceFetchTime = 0;

    LinearLayout drawingLayout;
    private DrawingView drawingView;
    private View mRootView;
    private LatLng mapInitialLoc;
    private static int MAP_MOVING_DISTANCE = 1000; //in mtr
    private String searchType="";
    private enum SearchBy{
        SearchByLocation,SearchByDeveloper,SearchByCity
    }
    private String selPropUnit = "";

    @Override
    public boolean isInternetConnected() {
        if(getActivity() == null)
        {
            return false;
        }

        if(UtilityMethods.isInternetConnected(getActivity())){
            return true;
        }else{
            UtilityMethods.showSnackbarOnTop(getActivity(),"Error",getString(R.string.no_network_connection),true,1500);
            return false;
        }
    }

    @Override
    public void showLoader() {
        if(apiCallOnMovingMap){
            return;
        }
        //binding.idProgressLayout.setVisibility(View.VISIBLE);
        if(getActivity() != null) {
            showProgressBar();
        }
        /*getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
    }

    @Override
    public void hideLoader() {
        if(apiCallOnMovingMap){
            return;
        }

        if(getActivity() != null)
        dismissProgressBar();
        //binding.idProgressLayout.setVisibility(View.GONE);
        //getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onSuccessProjectList(Response<ProjectListResponse> projectListResponse) {
        filteredProjectMap.clear();
        //need to clear the map if flag(indicate weather api has been called while moving map) true and again add marker
        if(apiCallOnMovingMap){
            apiCallOnMovingMap = false;
            clearMarkersNVariables();
        }
        if(projectListResponse.body().getProjectList() != null && !projectListResponse.body().getProjectList().isEmpty()){
            resultTypeValue = ResultType.onSuccess.toString();
            totalProjectCount = projectListResponse.body().getCount();
            projectList = projectListResponse.body().getProjectList();
        }else{
            resultTypeValue = ResultType.onEmpty.toString();
        }
        if(getActivity() != null) {
            ((HomeScreen) mActivity).setProjectList(projectList);
            setResultView();
        }
    }

    @Override
    public void onErrorProjectList(String errMsg) {
        resultTypeValue = ResultType.onError.toString();
        if(getActivity() != null) {
            setResultView();
        }
    }


    @Override
    public void onSortApplied(Constants.SortType sortType) {
        Log.i(TAG, "callOnSortApplied: ");
        apiCallOnMovingMap = false;
        if(!UtilityMethods.isInternetConnected(getActivity())){
            resultTypeValue = ResultType.onInternet.toString();
            setResultView();
            return;
        }
        if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString()) && ((HomeScreen)mActivity).getDeveloperId() != 0){
            getProjectListByDeveloper(((HomeScreen)mActivity).getDeveloperId());
        }else if(searchType.equalsIgnoreCase(SearchBy.SearchByLocation.toString())
                && ((HomeScreen)mActivity).getLatitude() != 0 && ((HomeScreen)mActivity).getLongitude() != 0){
            getProjectListByDistance(((HomeScreen)mActivity).getLatitude(),((HomeScreen)mActivity).getLongitude());
        }else{
            getProjectListByCity();
        }
    }

    public void getProjectListByCity(){
        searchType = SearchBy.SearchByCity.toString();
        long cityId = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.SAVED_CITY_ID,-1);
        int offset = 0;
        int limit = UtilityMethods.getIntInPref(getActivity(),Constants.AppConfigConstants.MAP_PROJECT_COUNT,50);
        setFilterVariables();
        setSortVariable();
        model.getProjectListByCity(cityId+"",offset,limit,null,filterPropertyType,filterBedType,filterProjectStatus,filterMinPrice,
                filterMaxPrice,filterMinArea,filterMaxArea,filterSort,
                requestId,appVersion,userId,userEmail,userPhone,userName,
                virtualId,leadsource,browser,browserVersion,browserType,getSummary);
    }

    public void getProjectListByDeveloper(long developerId){
        searchType = SearchBy.SearchByDeveloper.toString();
        long cityId = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.SAVED_CITY_ID,-1);
        int offset = 0;
        int limit = UtilityMethods.getIntInPref(getActivity(),Constants.AppConfigConstants.MAP_PROJECT_COUNT,50);
        setFilterVariables();
        setSortVariable();
        model.getProjectListByCity(cityId+"",offset,limit,developerId,filterPropertyType,filterBedType,filterProjectStatus,filterMinPrice,
                filterMaxPrice,filterMinArea,filterMaxArea,filterSort,
                requestId,appVersion,userId,userEmail,userPhone,userName,
                virtualId,leadsource,browser,browserVersion,browserType,getSummary);
    }

    public void getProjectListByDistance(double latitude, double longitude){
        searchType = SearchBy.SearchByLocation.toString();
        long cityId = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.SAVED_CITY_ID,-1);
        int offset = 0;
        int limit = UtilityMethods.getIntInPref(getActivity(),Constants.AppConfigConstants.MAP_PROJECT_COUNT,50);
        setFilterVariables();
        setSortVariable();
        model.getProjectListByDistance(latitude, longitude,cityId+"",offset,limit,filterPropertyType,filterBedType,filterProjectStatus,filterMinPrice,
                filterMaxPrice,filterMinArea,filterMaxArea,filterSort,requestId,appVersion,userId,userEmail,userPhone,userName,
                virtualId,leadsource,browser,browserVersion,browserType,city,searchText);
    }
    /**
     * Method which calls API projectlist by city
     * Also check filter and sorting parameters value
     */
    /*private void callProjectListAPI(){
        long cityId = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.SAVED_CITY_ID,-1);
        int offset = 0;
        int limit = UtilityMethods.getIntInPref(getActivity(),Constants.AppConfigConstants.MAP_PROJECT_COUNT,50);
        setFilterVariables();
        setSortVariable();
        model.getProjectListByCity(cityId+"",offset,limit,filterBuilderId,filterPropertyType,filterBedType,filterProjectStatus,filterMinPrice,
                filterMaxPrice,filterMinArea,filterMaxArea,filterSort);
    }*/

    /**
     * Method which calls API projectlist by city
     * Also check filter and sorting parameters value
     */
    /*private void callProjectListByDistanceAPI(double latitude, double longitude){
        long cityId = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.SAVED_CITY_ID,-1);
        int offset = 0;
        int limit = UtilityMethods.getIntInPref(getActivity(),Constants.AppConfigConstants.MAP_PROJECT_COUNT,50);
        setFilterVariables();
        setSortVariable();
        model.getProjectListByDistance(latitude, longitude,cityId+"",offset,limit,filterPropertyType,filterBedType,filterProjectStatus,filterMinPrice,
                filterMaxPrice,filterMinArea,filterMaxArea,filterSort);
    }*/

    /**
     * setting builder id from home screen while user search any property by builder name
     * @param filterBuilderId
     */
    public void setBuilderIdVaribale(Long filterBuilderId){
        this.filterBuilderId = filterBuilderId;
    }

    /**
     * Method to set the filter parameters(parameters of API)
     */
    private void setFilterVariables(){
        FilterUtility utility = new FilterUtility(getActivity());
        filterProjectStatus = utility.getFilterProjectStatus();
        filterPropertyType = utility.getFilterPropertyType();
        filterBedType = utility.getFilterBedRoom();
        filterMinPrice = utility.getFilterMinPrice();
        filterMaxPrice = utility.getFilterMaxPrice();

        Log.i(TAG, "filterProjectStatus : " + filterProjectStatus);
        Log.i(TAG, "filterPropertyType : " + filterPropertyType);
        Log.i(TAG, "filterBedType : " + filterBedType);
        Log.i(TAG, "filterMinPrice : " + filterMinPrice);
        Log.i(TAG, "filterMaxPrice : " + filterMaxPrice);


        searchText=((HomeScreen)getActivity()).searchAddress;
        city=UtilityMethods.getStringInPref(getActivity(),Constants.AppConstants.SAVED_CITY,"");
        appVersion=String.valueOf(UtilityMethods.getAppVersionUtility(getActivity()));
        if(UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, -1)!=-1) {
            userId = UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, -1);
        }
        if(!TextUtils.isEmpty(UtilityMethods.getStringInPref(getActivity(), Constants.ServerConstants.EMAIL, "")))
        {
            userEmail=UtilityMethods.getStringInPref(getActivity(), Constants.ServerConstants.EMAIL, "");

        }
        if(!TextUtils.isEmpty(UtilityMethods.getStringInPref(getActivity(), Constants.ServerConstants.MOBILE, "")))

        {
            userPhone=UtilityMethods.getStringInPref(getActivity(), Constants.ServerConstants.MOBILE, "");

        }
        if(!TextUtils.isEmpty(UtilityMethods.getStringInPref(getActivity(), Constants.ServerConstants.NAME, "")))

        {
            userName=UtilityMethods.getStringInPref(getActivity(), Constants.ServerConstants.NAME, "");
        }

        virtualId=UtilityMethods.getLongInPref(getActivity(),Constants.AppConstants.VIRTUAL_UID,-1);
        leadsource=Constants.LeadSource.AndroidApp.toString();
    }


    /**
     * Method to set the sorting parameters(parameters of API)
     */
    private void setSortVariable(){
        SortUtility sortUtility = new SortUtility(this);
        if(sortUtility.getSortType() != null){
            if(sortUtility.getSortType().equals(Constants.SortType.PriceDESC)){
                filterSort = "price DESC";
            }else if(sortUtility.getSortType().equals(Constants.SortType.PriceASC)){
                filterSort = "price ASC";
            }else if(sortUtility.getSortType().equals(Constants.SortType.AreaDESC)){
                filterSort = "area DESC";
            }else if(sortUtility.getSortType().equals(Constants.SortType.AreaASC)){
                filterSort = "area ASC";
            }else{
                filterSort = null;
            }
            Log.i(TAG, "filterSort : " + filterSort);
        }
    }

    public static String getKey() {
        return "6HdFGKT23JHFdjjhdu4ndhdy7HG878BHGB8787";
    }

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(){
        return new MapFragment();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(mActivity, R.raw.redbrics_mapstyle);
        mGoogleMap.setMapStyle(mapStyleOptions);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        }
        if(mapInitialLoc == null){

            mapInitialLoc = new LatLng(Constants.AppConstants.DEFAULT_LAT,Constants.AppConstants.DEFAULT_LONG);

        }
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mapInitialLoc));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        addMarkers(filteredProjectMap);

        /**
         * To get the latlng of center of the map before moving the map
         * we need to get the distance of 2 location
         * if user drag the map or move the map then we need to find the distance of center of the map
         * before moving map and the center of the map after moving the map
         * will check the distance if the distance is greater or equal of the given distance then
         * we need to get data from server
         */
        mapCenterBeforeMoving = mGoogleMap.getCameraPosition().target;

        /**
         * This call back will give the location of the center of the map before moving the map
         */
        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                Log.i(TAG, "onCameraMoveStarted: Reason " + reason);
                ON_CAMERA_MOVE_REASON = reason;
                if(reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
                    Log.i(TAG, "User dragged the camera");
                }else if(reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION){
                    Log.i(TAG, "REASON_API_ANIMATION");
                }else if(reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION){
                    Log.i(TAG, "REASON_DEVELOPER_ANIMATION");
                }
                mapCenterBeforeMoving = mGoogleMap.getCameraPosition().target;
                Log.i(TAG, "onCameraMoveStarted: location "+ mapCenterBeforeMoving);
            }
        });


        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (nearByMarkers.contains(marker)) {
                    marker.showInfoWindow();
                    return true;
                }
                isMarkerClick = true;
                selectMarker(marker, filteredProjectMap);
                return true;
            }
        });


        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(currentSelProjectLoc == null){
                    if(filteredProjectMap != null && !filteredProjectMap.isEmpty()){
                        Project searchProperty = new ArrayList<>(filteredProjectMap.values()).get(0);
                        if(searchProperty != null){
                            currentSelProjectLoc = new LatLng(searchProperty.getLat(),searchProperty.getLng());
                        }
                    }else{
                        currentSelProjectLoc = new LatLng(Constants.AppConstants.DEFAULT_LAT,Constants.AppConstants.DEFAULT_LONG);
                    }
                }
                LatLng currentLatLng = mGoogleMap.getCameraPosition().target;
                Location oldLoc = new Location("locationA");
                oldLoc.setLatitude(currentSelProjectLoc.latitude);
                oldLoc.setLongitude(currentSelProjectLoc.longitude);

                Location currentLoc = new Location("locationB");
                currentLoc.setLatitude(currentLatLng.latitude);
                currentLoc.setLongitude(currentLatLng.longitude);

                float distanceInMtr = oldLoc.distanceTo(currentLoc);
                Log.i(TAG, "Distance between 2 location " + distanceInMtr);
                double distanceCovered = getMapDistance(new Float(zoomLevel));

                Location locBeforeMovingMap = new Location("MovingLocation");
                locBeforeMovingMap.setLatitude(mapCenterBeforeMoving.latitude);
                locBeforeMovingMap.setLongitude(mapCenterBeforeMoving.longitude);

                /**
                 * Api will gets call only while user drag the map camera else don't call
                 * finding the distance of the 2 center location of map
                 * first location (before moving map center of the map location & after moving map center of the map location)
                 */
                Log.i(TAG, "Camera moved location -> " + currentLatLng);
                float mapMovingDistance = locBeforeMovingMap.distanceTo(currentLoc);
                Log.i(TAG, "Map Moving Distance -> " + mapMovingDistance);
                if(ON_CAMERA_MOVE_REASON == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE
                            && mapMovingDistance >= MAP_MOVING_DISTANCE){
                    apiCallOnMovingMap = true;
                    dontAnimateCamera = true;
                    ((HomeScreen)mActivity).setMapMovedLocation(SearchBy.SearchByLocation.toString(),currentLoc.getLatitude(),currentLoc.getLongitude());
                    getProjectListByDistance(currentLoc.getLatitude(),currentLoc.getLongitude());
                }

                Log.i(TAG, "onCameraIdle: " + currentLatLng);
                float zoomDifference = 0;
                float currentZoomLevel = mGoogleMap.getCameraPosition().zoom;
                if(zoomLevel > currentZoomLevel){
                    zoomDifference = zoomLevel - currentZoomLevel;
                }else {
                    zoomDifference = currentZoomLevel - zoomLevel;
                }
                zoomLevel = currentZoomLevel;
                long currentTime = System.currentTimeMillis();
                long timeDiff = currentTime - nearByPlaceFetchTime;
                timeDiff = TimeUnit.MILLISECONDS.toSeconds(timeDiff);
                Log.i(TAG, "onCameraIdle: Time Diff " + timeDiff);
                if(!nearbySelected.equals(NearbySelection.NO_SELECTION)
                        && ( distanceInMtr >= distanceCovered
                        || zoomDifference > 1 ) && timeDiff >=2 ){
                    Log.i(TAG, "onCameraIdle: nearByPlace called....");
                    nearByPlaceFetchTime = System.currentTimeMillis();
                    showNearByPlaces();
                    currentSelProjectLoc = mGoogleMap.getCameraPosition().target;
                }
            }
        });
    }


    /**
     * To reset the polygon, if user drawn polygon and after user changes the city then we need to reset the polygon
     */
    public void onCityChange(){
        if(drawBtnState.equals(DrawBtnState.drawing) || drawBtnState.equals(DrawBtnState.drawn)) {
            polygon = null;
            if (getActivity() != null) {
                ((HousingApplication) getActivity().getApplicationContext()).setPolygonLatLngList(null);
            }
            drawingLayout.setVisibility(View.GONE);
            drawingView.setVisibility(View.GONE);
            mGoogleMap.clear();
            if (getActivity() != null) {
                draw.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.draw_icon));
            }
            currentLocation.setVisibility(View.VISIBLE);
            layers.setVisibility(View.VISIBLE);
            clearMap();
            drawBtnState = DrawBtnState.notdrawing;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (IPropertyOperationsListener) context;
        getActivity().registerReceiver(onUnitChangeReceiver, new IntentFilter(Constants.BroadCastConstants.ON_UNIT_CHANGE));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_map_layout,container,false);
        View view = binding.getRoot();
        mMapView = view.findViewById(R.id.map_search_result);
        selPropUnit = UtilityMethods.getSelectedUnit(mActivity);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        MAP_MOVING_DISTANCE = UtilityMethods.getIntInPref(getActivity(),Constants.AppConfigConstants.MAP_PROJECT_REF_DISTANCE,1000);

        selectedCircularView = BitmapDescriptorFactory.fromResource(R.drawable.home_marker);
        circleYellowIcon = BitmapDescriptorFactory.fromBitmap(createMarker(mActivity));

        listPager = view.findViewById(R.id.map_viewpager);
        UtilityMethods.setColorBackground(mActivity,view.findViewById(R.id.id_criteria_layout),R.color.overlay_color3);
        UtilityMethods.setTextViewColor(mActivity,(TextView)view.findViewById(R.id.id_property_count),R.color.black);

        draw = (FloatingActionButton) view.findViewById(R.id.draw_btn);
        currentLocation = (FloatingActionButton) view.findViewById(R.id.current_location_btn);
        layers = (FloatingActionButton) view.findViewById(R.id.layers_btn);

        if (getActivity() != null) {
            draw.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
            currentLocation.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
            layers.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));

            drawingLayout = (LinearLayout) view.findViewById(R.id.drawing_layout);
            drawingView = new DrawingView(getActivity());
            drawingLayout.addView(drawingView);
            drawingView.setVisibility(View.GONE);

            draw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawClicked();
                }
            });

            layers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLayersDialog();
                }
            });

            currentLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToCurrentLocation();
                }
            });
        }

        view.findViewById(R.id.id_filter_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, FilterActivity.class);
                startActivityForResult(intent, FILTER_REQUEST_CODE);
            }
        });
        view.findViewById(R.id.id_sort_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showSortDialog();
                SortUtility sortUtility = new SortUtility(MapFragment.this);
                sortUtility.showSortDialog(getActivity());
            }
        });
        view.findViewById(R.id.id_saved_search_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onSaveSearchApplied();
                }
            }
        });
        binding.idNoInternetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onNoInternetTryAgain();
                }
            }
        });
        mRootView = view;
        // To set initial list from listview
        /*projectList = ((HomeScreen)mActivity).getProjectList();
        Log.i(TAG, "onCreateView: Project List " + projectList.size());
        if(projectList != null && !projectList.isEmpty()) {
            resultTypeValue = ResultType.onSuccess.toString();
            setResultView();
        }*/
        model = new ProjectMapViewModel(this);
        Bundle bundle = getArguments();
        if(bundle != null && !TextUtils.isEmpty(bundle.getString("ServerAPItype",""))){
            searchType = bundle.getString("ServerAPItype","");
        }
        if(!UtilityMethods.isInternetConnected(getActivity())){
            resultTypeValue = ResultType.onInternet.toString();
            setResultView();
            return view;
        }


        if(searchType.equalsIgnoreCase(SearchBy.SearchByLocation.toString())){
            double latitude = ((HomeScreen) mActivity).getLatitude();
            double longitude = ((HomeScreen) mActivity).getLongitude();
            callOnSearchingApplied(latitude,longitude);
        }else if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString())){
            filterBuilderId = ((HomeScreen) mActivity).getDeveloperId();
            getProjectListByDeveloper(filterBuilderId);
        }else{
            getProjectListByCity();
        }

        setFilterView(true);
        return view;
    }

    private void setMapViewPager(final HashMap<Long, Project> projectMap) {
        if(projectMap == null){
            return;
        }
        //setResultCount(projectMap.size());
        setFilterView(true);
        if (listPager != null) {
            listPager.setAdapter(null);
        }
        fragmentPagerAdapter = null;
        if (getActivity() != null) {
            listPager.setPageTransformer(false, new CarouselPageTransformer(getActivity()));
        }
        listPager.setOffscreenPageLimit(3);

        /*if (getChildFragmentManager().getFragments() != null) {
            getChildFragmentManager().getFragments().clear();
        }*/

        if ((projectMap != null) && (!projectMap.isEmpty())) {
            final ArrayList<Project> filteredArray = new ArrayList(projectMap.values());
            fragmentPagerAdapter = new MapViewListAdapter(getChildFragmentManager(), filteredArray, getActivity());
            listPager.setAdapter(fragmentPagerAdapter);

            listPager.setOnPageChangeListener(new VelocityViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    if ((!isMarkerClick) && (markerArrayList != null) && (position < markerArrayList.size())
                            && (positionOffset == 0.0f) && (positionOffsetPixels == 0)) {
                        Log.i(TAG, "onPageScrolled: ");
                        selectMarker(markerArrayList.get(position), projectMap);//.showInfoWindow();
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    Log.i(TAG, "onPageSelected: ");
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    //Log.d(TAG, "PageScrollStateChanged->" + state);
                    if ((state == 0) && (isMarkerClick)) {
                        isMarkerClick = false;
                    }
                }
            });
            mRootView.findViewById(R.id.no_results_in_map).setVisibility(View.GONE);
        } else {
            mRootView.findViewById(R.id.no_results_in_map).setVisibility(View.VISIBLE);
            if (getActivity() != null) {
                ((TextView) mRootView.findViewById(R.id.no_results_in_map)).setText(getActivity().getResources().getString(R.string.no_result_found));
            }
            listPager.setAdapter(null);
        }
    }

    private void moveToCurrentLocation() {
        if (getActivity() != null && !PermissionManager.checkIfAlreadyhasPermission(getActivity(), PERMISSIONS)) {
            Log.d(TAG, "Requesting!!");
            PermissionManager.requestForSpecificPermission(getActivity(), PERMISSIONS, REQUEST_PERMISSIONS_CODE);
        } else {
            Log.d(TAG, "RequestLocation!!");
            requestPermissionForLocation();
            //getCurrentLocation();
        }
    }

    ToggleButton toggleButton[] = new ToggleButton[10];
    private boolean checkList[] = new boolean[10]; // to keep state of all togglebuttons
    private void showLayersDialog() {
        Typeface typefaceBook = Typeface.createFromAsset(getResources().getAssets(), "fonts/FiraSans-Book.ttf");
        final Typeface typefaceMedium = Typeface.createFromAsset(getResources().getAssets(), "fonts/FiraSans-Medium.ttf");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_map_layers, null);
        builder.setView(dialogView);

        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) dialogView.findViewById(R.id.horizontal_scroll_view);
        final RadioButton normal = (RadioButton) dialogView.findViewById(R.id.normal_view);
        final RadioButton satellite = (RadioButton) dialogView.findViewById(R.id.satellite_view);
        final RadioButton hybrid = (RadioButton) dialogView.findViewById(R.id.hybrid_view);
        normal.setTypeface(typefaceBook);
        satellite.setTypeface(typefaceBook);
        hybrid.setTypeface(typefaceBook);

        mapTypeGroup = (RadioGroup) dialogView.findViewById(R.id.radio_group_map_type);
        Button clearButton = (Button) dialogView.findViewById(R.id.clear_map);
        initCheckList();
        initButtons(dialogView);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearbySelected = NearbySelection.NO_SELECTION;
                mapTypeGroup.clearCheck();
                clearSelections(-1);
                if ((nearByMarkers != null) && (!nearByMarkers.isEmpty())) {
                    for (Marker nearbyMarker : nearByMarkers) {
                        nearbyMarker.remove();
                    }
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int checkedRadioButton = mapTypeGroup.getCheckedRadioButtonId();
                if (checkedRadioButton == R.id.normal_view) {
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (checkedRadioButton == R.id.satellite_view) {
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (checkedRadioButton == R.id.hybrid_view) {
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else{
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                showNearByPlaces();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        if(!nearbySelected.equals(NearbySelection.NO_SELECTION)){
            final int scrolledPos = nearbySelected.ordinal()-1;
            toggleButton[scrolledPos].setChecked(true);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(scrolledPos <= 6) {
                        horizontalScrollView.scrollBy(scrolledPos * (int)UtilityMethods.convertDpToPx(getResources(),60), 0);
                    }else{
                        horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                }
            });
        }

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(typefaceMedium);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(typefaceMedium);
                //nearbyListAdapter.setItem(nearbySelected);
                //nearbyListAdapter.notifyDataSetChanged();
                if (mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID)
                    hybrid.setChecked(true);
                else if (mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE)
                    satellite.setChecked(true);
                else if (mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                    normal.setChecked(true);
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void initCheckList() {
        for (int i = 0; i < checkList.length; i++) {
            checkList[i] = false;
        }
    }

    private void initButtons(View view) {
        toggleButton[0] = (ToggleButton) view.findViewById(R.id.bus_stops);
        toggleButton[1] = (ToggleButton) view.findViewById(R.id.railways);
        toggleButton[2] = (ToggleButton) view.findViewById(R.id.airports);
        toggleButton[3] = (ToggleButton) view.findViewById(R.id.schools);
        toggleButton[4] = (ToggleButton) view.findViewById(R.id.restaurants);
        toggleButton[5] = (ToggleButton) view.findViewById(R.id.groceries);
        toggleButton[6] = (ToggleButton) view.findViewById(R.id.atms);
        toggleButton[7] = (ToggleButton) view.findViewById(R.id.hospitals);
        toggleButton[8] = (ToggleButton) view.findViewById(R.id.parks);
        toggleButton[9] = (ToggleButton) view.findViewById(R.id.petrol_pumps);

        for (int i = 0; i < toggleButton.length; i++) {
            toggleButton[i].setOnCheckedChangeListener(checkedChangeListener);
        }
    }

    private void clearSelections(int id) {
        if(toggleButton != null) {
            for (int i = 0; i < toggleButton.length; i++) {
                if(toggleButton[i] != null) {
                    if (i != id) {
                        toggleButton[i].setChecked(false);
                    } else {
                        toggleButton[i].setChecked(true);
                        Log.d(TAG, "Not cleared " + id);
                    }
                }
            }
        }
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            switch (compoundButton.getId()){
                case R.id.bus_stops:
                    checkList[0] = isChecked;
                    if (isChecked) {
                        clearSelections(0);
                        nearbySelected = NearbySelection.BUS;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
                case R.id.railways:
                    checkList[1] = isChecked;
                    if (isChecked) {
                        clearSelections(1);
                        nearbySelected = NearbySelection.TRAIN;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
                case R.id.airports:
                    checkList[2] = isChecked;
                    if (isChecked) {
                        clearSelections(2);
                        nearbySelected = NearbySelection.AIRPORT;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
                case R.id.schools:
                    checkList[3] = isChecked;
                    if (isChecked) {
                        clearSelections(3);
                        nearbySelected = NearbySelection.SCHOOL;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
                case R.id.restaurants:
                    checkList[4] = isChecked;
                    if (isChecked) {
                        clearSelections(4);
                        nearbySelected = NearbySelection.RESTAURANT;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
                case R.id.groceries:
                    checkList[5] = isChecked;
                    if (isChecked) {
                        clearSelections(5);
                        nearbySelected = NearbySelection.MARKET;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
                case R.id.atms:
                    checkList[6] = isChecked;
                    if (isChecked) {
                        clearSelections(6);
                        nearbySelected = NearbySelection.ATM;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
                case R.id.hospitals:
                    checkList[7] = isChecked;
                    if (isChecked) {
                        clearSelections(7);
                        nearbySelected = NearbySelection.HOSPITAL;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
                case R.id.parks:
                    checkList[8] = isChecked;
                    if (isChecked) {
                        clearSelections(8);
                        nearbySelected = NearbySelection.PARK;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
                case R.id.petrol_pumps:
                    checkList[9] = isChecked;
                    if (isChecked) {
                        clearSelections(9);
                        nearbySelected = NearbySelection.PETROL_PUMP;
                    }else{
                        nearbySelected = NearbySelection.NO_SELECTION;
                    }
                    break;
            }
        }
    };

    private void showNearByPlaces(){

        if(!UtilityMethods.isInternetConnected(mActivity)){
            UtilityMethods.showSnackbarOnTop(mActivity,"Error",getString(R.string.no_internet_connection),true,1500);
            return;
        }

        if (!nearbySelected.equals(NearbySelection.NO_SELECTION)) {
            Log.d(TAG, "selectedPosition->" + nearbySelected);

            /*for (Marker marker : nearByMarkerList) {
                marker.remove();
            }
            nearByMarkerList.clear();*/
            LatLng leftBottomLatLng = mGoogleMap.getProjection().getVisibleRegion().nearLeft;
            LatLng rightBottomLatLng = mGoogleMap.getProjection().getVisibleRegion().farRight;
            float[] screenRadius = new float[1];
            Location.distanceBetween(leftBottomLatLng.latitude, leftBottomLatLng.longitude,
                    rightBottomLatLng.latitude, rightBottomLatLng.longitude, screenRadius);
            Log.d(TAG, "distance=" + (double) screenRadius[0] / 1000);
            //DecimalFormat twoDForm = new DecimalFormat("#.##");
            //float distance = Float.valueOf(twoDForm.format(screenRadius[0] / 1000));
            int nearbyIndex = 0;
            if (nearbySelected.equals(NearbySelection.BUS)) {
                nearbyIndex = 0;
            } else if (nearbySelected.equals(NearbySelection.TRAIN)) {
                nearbyIndex = 1;
            } else if (nearbySelected.equals(NearbySelection.AIRPORT)) {
                nearbyIndex = 2;
            }else if (nearbySelected.equals(NearbySelection.SCHOOL)) {
                nearbyIndex = 3;
            }else if (nearbySelected.equals(NearbySelection.RESTAURANT)) {
                nearbyIndex = 4;
            }else if (nearbySelected.equals(NearbySelection.MARKET)) {
                nearbyIndex = 5;
            }else if (nearbySelected.equals(NearbySelection.ATM)) {
                nearbyIndex = 6;
            }else if (nearbySelected.equals(NearbySelection.HOSPITAL)) {
                nearbyIndex = 7;
            }else if (nearbySelected.equals(NearbySelection.PARK)) {
                nearbyIndex = 8;
            }else if (nearbySelected.equals(NearbySelection.PETROL_PUMP)) {
                nearbyIndex = 9;
            }
            /*getPlaceData(mGoogleMap, placesList[nearbyIndex],
                    ((int) Math.ceil(screenRadius[0])) / 4);*/
            getPlaceData(mGoogleMap, placesList[nearbyIndex],proximityRadius[nearbyIndex]);
        }
    }

    private void showNearbyPlaces(String placeType, List<HashMap<String, String>> nearbyPlacesList) {
        if (nearbyPlacesList == null) {
            return;
        }

        if(nearByMarkers != null){
            Log.i(TAG, "Already added marker size " + nearByMarkers.size());
            for (final Marker marker : nearByMarkers) {
                if(marker != null) {
                    marker.remove();
                }
            }
            nearByMarkers.clear();
            Log.i(TAG, "After clearing marker size " + nearByMarkers.size());
        }
        LatLng center = mGoogleMap.getCameraPosition().target;
        //LatLng center = currentSelProjectLoc;
        BitmapDescriptor dotIcon = BitmapDescriptorFactory.fromResource(R.drawable.nearby_marker);
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            final MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);

            markerOptions.position(latLng);
            markerOptions.title(placeName);

            float[] result = new float[1];
            Location.distanceBetween(center.latitude, center.longitude, lat, lng, result);

            markerOptions.icon(dotIcon);
            //markerOptionsList.add(markerOptions);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Marker placeMarker = mGoogleMap.addMarker(markerOptions);
                    nearByMarkers.add(placeMarker);
                }
            },(i*15));
        }
        Log.i(TAG, "Total marker added size " + nearByMarkers.size());
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
    }

    GetNearbyData getNearbyPlacesData;
    private void getPlaceData(GoogleMap googleMap, String placeType, int radius) {
        LatLng center = googleMap.getCameraPosition().target;
        //LatLng center = currentSelProjectLoc;
        //getting 3 points from center of the map
        LatLng boundLatLng = mGoogleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        Location centerPointLocaiton = new Location("center_point");
        centerPointLocaiton.setLatitude(center.latitude);
        centerPointLocaiton.setLongitude(center.longitude);
        Location boundPointLocation = new Location("bound_point");
        boundPointLocation.setLatitude(boundLatLng.latitude);
        boundPointLocation.setLongitude(boundLatLng.longitude);
        float distance = centerPointLocaiton.distanceTo(boundPointLocation);
        Log.i(TAG, "getPlaceData: distance " + distance);
        if(distance < 1000){
            distance = 1000;
        }else if(distance > 15000){
            distance = 15000;
        }
        distance = distance/3;
        LatLng latLngPoint1 = getBearingPoints(0,distance/1000,center);         //latlng point at 0 degree
        LatLng latLngPoint2 = getBearingPoints(135,distance/1000,center);         //latlng point at 135 degree
        LatLng latLngPoint3 = getBearingPoints(225,distance/1000,center);         //latlng point at 225 degree

        //LatLng center = currentSelProjectLoc;

        String cityName = UtilityMethods.getStringInPref(mActivity,Constants.AppConstants.SAVED_CITY,"");
        String stateName = UtilityMethods.getStringInPref(mActivity,Constants.AppConstants.SAVED_STATE,"");
        String url = "";

        radius = (int)distance;
        List<LatLng> latLngList = new ArrayList<>();
        latLngList.add(center);
        latLngList.add(latLngPoint1);
        latLngList.add(latLngPoint2);
        latLngList.add(latLngPoint3);
        List<Object[]> dataTransferList = new ArrayList<>();
        for(int i=0; i<latLngList.size(); i++) {
            LatLng latLngPoint = latLngList.get(i);
            if (placeType.equalsIgnoreCase("airport")) {
                try {
                    if (!TextUtils.isEmpty(stateName) && !TextUtils.isEmpty(cityName)) {
                        url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=airport+in+"
                                + URLEncoder.encode(cityName.toLowerCase(), "UTF-8") + "+"
                                + URLEncoder.encode(stateName.toLowerCase(), "UTF-8") +
                                "&key=" + getString(R.string.place_api_key);
                    } else if (!TextUtils.isEmpty(cityName)) {
                        url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=airport+in+"
                                + URLEncoder.encode(cityName.toLowerCase(), "UTF-8") +
                                "&key=" + getString(R.string.place_api_key);
                    } else {
                        url = DataParser.getNearbyUrl(latLngPoint.latitude, latLngPoint.longitude, placeType, radius, getString(R.string.place_api_key));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    url = DataParser.getNearbyUrl(latLngPoint.latitude, latLngPoint.longitude, placeType, radius, getString(R.string.place_api_key));
                }
            } else {
                url = DataParser.getNearbyUrl(latLngPoint.latitude, latLngPoint.longitude, placeType, radius, getString(R.string.place_api_key));
            }

            Object[] DataTransfer = new Object[3];
            DataTransfer[0] = googleMap;
            DataTransfer[1] = url;
            DataTransfer[2] = placeType;
            dataTransferList.add(DataTransfer);
            /*GetNearbyData getNearbyPlacesData = new GetNearbyData();
            getNearbyPlacesData.execute(DataTransfer);*/
        }
        if(getNearbyPlacesData != null && getNearbyPlacesData.getStatus() == GetNearbyData.Status.RUNNING){
            getNearbyPlacesData.cancel(true);
            getNearbyPlacesData = null;
        }
        getNearbyPlacesData = new GetNearbyData();
        getNearbyPlacesData.execute(dataTransferList);

    }

    private class GetNearbyData extends AsyncTask<List<Object[]>, String, List<String>> {
        String googlePlacesData;
        GoogleMap mMap;
        String placeType;
        private List<String> googlePlaceDataList = null;

        @Override
        protected void onPreExecute() {
            //progressDialog.show();
            googlePlaceDataList = new ArrayList<>();
        }


        @Override
        protected List<String> doInBackground(List<Object[]>... params) {
            try {
                for(int i=0; i<params[0].size(); i++){
                    HousingLogs.d("GetNearbyPlacesData", "doInBackground entered");
                    mMap = (GoogleMap) params[0].get(i)[0];
                    URL url = new URL((String) (params[0].get(i))[1]);
                    placeType = (String) params[0].get(i)[2];
                    googlePlacesData = DataParser.getData(url);
                    HousingLogs.d("GooglePlacesReadTask", "doInBackground Exit");
                    googlePlaceDataList.add(googlePlacesData);
                }
            } catch (Exception e) {
                HousingLogs.e("GooglePlacesReadTask", e.toString());
            }
            return googlePlaceDataList;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            //progressDialog.hide();

            if (result == null && getActivity() != null && mRootView != null) {
                showNetworkErrorSnackBar(mRootView);
                return;
            }

            /*if(snackbar != null){
                snackbar.dismiss();
            }*/
            HousingLogs.d("GooglePlacesReadTask", "onPostExecute Entered");
            List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();
            //HousingLogs.d(TAG, "result->" + result.toString());
            for(int i=0; i<result.size(); i++) {
                DataParser dataParser = new DataParser();
                nearbyPlacesList.addAll(dataParser.parse(result.get(i)));
            }
            /*if (nearbyPlacesList.isEmpty()) {
                int i = 0;
                while (!placeType.equals(placesList[i])) {
                    i++;
                }
                Toast.makeText(getActivity(), "No " + placesList[i] + " nearby.", Toast.LENGTH_SHORT).show();
            }*/
            showNearbyPlaces(placeType, nearbyPlacesList);
            //listofPlacesList.put(placeType, nearbyPlacesList);
            //HousingLogs.d("GooglePlacesReadTask", "onPostExecute Exit");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult->" + requestCode + " " + resultCode);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            Location myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (myLocation != null) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
            }
        }else if(requestCode == FILTER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            callOnFilterApplied();
        }
    }

    public void setProjectMap(List<Project> projectList){
        if(!isAdded()){
            return;
        }
        if(projectList != null && !projectList.isEmpty()) {
            for(int i=0; i<projectList.size(); i++){
                Project project = projectList.get(i);
                if(project != null && project.getId() != null){
                    filteredProjectMap.put(project.getId(),project);
                }
            }
            //setResultView();
            setMarkersOnUpdate();
        }

        /**
         * If draw buttn status is drawing i.e user is reday to draw polygon and if user has perform search or city change then we need to reset it
         */
        if(getActivity() != null && drawBtnState.equals(DrawBtnState.drawing)){
            ((HomeScreen)getActivity()).setPolygonDrawn(false);
            drawingLayout.setVisibility(View.GONE);
            drawingView.setVisibility(View.GONE);
            draw.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.draw_icon));
            currentLocation.setVisibility(View.VISIBLE);
            layers.setVisibility(View.VISIBLE);
            drawBtnState = DrawBtnState.notdrawing;
        }
        /**
         * To check if polygon list is not empty then we need to draw polygon
         */
        if(getActivity() != null && getActivity().getIntent().hasExtra(Constants.SavedSearchConstants.IS_SAVE_SEARCH)
                && getActivity().getIntent().getBooleanExtra(Constants.SavedSearchConstants.IS_SAVE_SEARCH,false)
                && ((HousingApplication)getActivity().getApplicationContext()).getPolygonLatLngList() != null
                && !((HousingApplication)getActivity().getApplicationContext()).getPolygonLatLngList().isEmpty()){
            ((HomeScreen) getActivity()).setPolygonDrawn(true);
            drawingView.setVisibility(View.GONE);
            draw.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.cross));
            currentLocation.setVisibility(View.GONE);
            layers.setVisibility(View.GONE);
            drawBtnState = DrawBtnState.drawn;
            polygonLatlngList = ((HousingApplication)getActivity().getApplicationContext()).getPolygonLatLngList();
            drawPath();
        }

    }

    /**
     * To get current google map camera zoom level
     * @return zoomlevel
     */
    public float getMapCameraZoom(){
        if(mGoogleMap != null){
            return mGoogleMap.getCameraPosition().zoom;
        }else{
            return zoomLevel;
        }
    }

    /**
     * To get the Polygon state (drawing, drawn or notdrawing)
     * @return drawBtnState
     */
    public DrawBtnState getPolygonState(){
        return drawBtnState;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(onUnitChangeReceiver);
    }

    private void addMarkers(HashMap<Long, Project> projectMap) {
        if(projectMap != null){
            ArrayList<Long> keySet = new ArrayList<>(projectMap.keySet());
            for (int i = 0; i < keySet.size(); i++) {
                LatLng latLng = new LatLng(projectMap.get(keySet.get(i)).getLat(),
                        projectMap.get(keySet.get(i)).getLng());

                if(selectedCircularView == null){
                    selectedCircularView = BitmapDescriptorFactory.fromResource(R.drawable.home_marker);
                }
                MarkerOptions markerOptions = new MarkerOptions()
                        .icon(selectedCircularView)
                        .anchor(0.5f,0.5f)
                        .position(latLng).title(projectMap.get(keySet.get(i)).getName());
                Marker marker = mGoogleMap.addMarker(markerOptions);
                markerArrayList.add(marker);
                markerProjectIdMap.put(marker.getId(),keySet.get(i));

            }
            /*if (markerArrayList != null && markerArrayList.size() > 0) {
                selectMarker(markerArrayList.get(0), projectMap);//.showInfoWindow();
            }*/
        }
    }

    Handler handler = new Handler(Looper.getMainLooper());
    long selectMarkerTime = 0;
    private void selectMarker(final Marker marker, final HashMap<Long, Project> projectMap) {
        Log.i(TAG, "selectMarker time " + (System.currentTimeMillis() - selectMarkerTime));
        if((System.currentTimeMillis() - selectMarkerTime) < 500){
            return;
        }
        selectMarkerTime = System.currentTimeMillis();
        ArrayList<Long> keySet = new ArrayList<>(projectMap.keySet());
        if (nearByMarkers.contains(marker)) {
            return;
        }
        if ((lastMarkerHit != null) && (lastMarkerHit.equals(marker))) {
            Log.d(TAG, "Same!!");
            return;
        } else {

            Long markerId = markerProjectIdMap.get(marker.getId());
            final int currentItemIndex = keySet.indexOf(markerId);
            Log.i(TAG, "selectMarker: Current Item Index " + currentItemIndex);
            if(isMarkerClick){
                if(fragmentPagerAdapter != null) {
                    listPager.setAdapter(fragmentPagerAdapter);
                    listPager.setCurrentItem(currentItemIndex);
                }else{
                    listPager.setCurrentItem(currentItemIndex, false);
                }
            }else{
                listPager.setCurrentItem(currentItemIndex, false);
            }
            try {
                if(selectedCircularView == null){
                    selectedCircularView = BitmapDescriptorFactory.fromResource(R.drawable.home_marker);
                }
                if(filteredProjectMap.get(markerProjectIdMap.get(marker.getId())) != null) {
                    marker.setIcon(selectedCircularView);
                    marker.setAnchor(0.5f,0.5f);
                    final LatLng markerPos = marker.getPosition();
                    animateIcon(markerPos, marker);
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                try {
                                    if (groundOverlay1 != null) {
                                        groundOverlay1.setTransparency(0);
                                        groundOverlay1.setVisible(false);
                                        groundOverlay1.remove();
                                    }
                                    if (circleYellowIcon != null && marker != null && mGoogleMap != null) {
                                        marker.setIcon(circleYellowIcon);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },950);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!dontAnimateCamera) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(marker.getPosition())
                        .zoom(zoomLevel)
                        .build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            dontAnimateCamera = false;
        }

        if (lastMarkerHit != null && !lastMarkerHit.equals(marker)) {
            try {
                if(selectedCircularView == null){
                    selectedCircularView = BitmapDescriptorFactory.fromResource(R.drawable.home_marker);
                }
                if(filteredProjectMap.get(markerProjectIdMap.get(lastMarkerHit.getId())) != null) {
                    lastMarkerHit.setIcon(selectedCircularView);
                    lastMarkerHit.setAnchor(0.5f,0.5f);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isMarkerClick = false;
        lastMarkerHit = marker;
    }


    public void setMarkersOnUpdate() {

        try {
            for (Marker marker : markerArrayList) {
                marker.remove();
            }
            clearMap();
            markerArrayList = new ArrayList<Marker>();
            markerProjectIdMap = new HashMap<>();
            //mGoogleMap.clear();
            //Log.d(TAG, "filteredSize->" + filteredProjectMap.size() + "\npolygon->" + polygon.getPoints().size());
            if(filteredProjectMap == null){
                return;
            }
            Log.d(TAG, "val null");
            for (Map.Entry<Long, Project> entry : filteredProjectMap.entrySet()) {
                Project pw = entry.getValue();
                //Creating a LatLng Object to store Coordinates
                LatLng latLng = new LatLng(pw.getLat(), pw.getLng());

                if(selectedCircularView == null){
                    selectedCircularView = BitmapDescriptorFactory.fromResource(R.drawable.home_marker);
                }
                MarkerOptions markerOptions = new MarkerOptions()
                        .icon(selectedCircularView)
                        .anchor(0.5f,0.5f)
                        .position(latLng).title(pw.getName());
                if(mGoogleMap!=null){
                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    markerArrayList.add(marker);
                    markerProjectIdMap.put(marker.getId(), pw.getId());
                    filteredProjectMap.put(pw.getId(), pw);
                }

            }
            //}

            if ((filteredProjectMap != null) && (!filteredProjectMap.isEmpty())) {
                listPager.setVisibility(View.VISIBLE);
                setMapViewPager(filteredProjectMap);
            } else {
                Log.d(TAG, "shownSize is null");
                setMapViewPager(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawClicked() {
        try {
            if (drawBtnState.equals(DrawBtnState.notdrawing)) {
                //((SearchResults) getActivity()).setPolygonDrawn(true);
                HomeScreen.switchBtn.setVisibility(View.GONE);
                if(getActivity() != null) {
                    ((HomeScreen) getActivity()).setPolygonDrawn(true);
                }
                setFilterView(false);
                setSortView(false);
                setSaveSearchView(false);
                drawingView.clearPoints();
                drawingView.setVisibility(View.VISIBLE);
                drawingLayout.setVisibility(View.VISIBLE);
                if(mGoogleMap != null) {
                    mGoogleMap.clear();
                }
                polygonLatlngList.clear();
                listPager.setVisibility(View.GONE);
                if(getActivity() != null) {
                    draw.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.cross));
                }
                currentLocation.setVisibility(View.GONE);
                layers.setVisibility(View.GONE);
                drawBtnState = DrawBtnState.drawing;
            } else {//if ((drawBtnState.equals(DrawBtnState.drawn)) || (drawBtnState.equals(DrawBtnState.drawing))) {
                //((SearchResults) getActivity()).setPolygonDrawn(false);
                /*if (saveSearchWrapper != null && saveSearchWrapper.latLongList != null) {
                    saveSearchWrapper.latLongList = null;
                }
                if (((SearchResults) getActivity()).polygonLatLngList != null) {
                    ((SearchResults) getActivity()).polygonLatLngList = null;
                }*/
                HomeScreen.switchBtn.setVisibility(View.VISIBLE);
                if(getActivity() != null) {
                    ((HomeScreen) getActivity()).setPolygonDrawn(false);
                    ((HousingApplication)getActivity().getApplicationContext()).setPolygonLatLngList(null);
                }
                polygon = null;
                /*if(mListener != null){
                    mListener.filterPropertyOnMap();
                }*/
                drawingLayout.setVisibility(View.GONE);
                drawingView.setVisibility(View.GONE);
                mGoogleMap.clear();
                if(getActivity() != null) {
                    draw.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.draw_icon));
                }
                //draw.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
                currentLocation.setVisibility(View.VISIBLE);
                layers.setVisibility(View.VISIBLE);
                clearMap();
                Log.d(TAG, "Markers->" + filteredProjectMap.size());
                addMarkers(filteredProjectMap);
                setMapViewPager(filteredProjectMap);
                drawBtnState = DrawBtnState.notdrawing;
                listPager.setVisibility(View.VISIBLE);

                //((OnPolygonDrawListener) getActivity()).onPolygonRemoved(shownProjectMap);

            }
            if(drawBtnState.equals(DrawBtnState.drawing)){
                setSwitchButtonVisibility(false);
            }else{
                setSwitchButtonVisibility(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearMarkersNVariables(){
        //mGoogleMap.clear();
        try {
            if(markerArrayList != null){
                for(Marker marker : markerArrayList){
                    if(marker != null) {
                        marker.remove();
                    }
                }
            }
            filteredProjectMap.clear();
            markerArrayList.clear();
            markerProjectIdMap.clear();
            projectList.clear();

            lastMarkerHit = null;
            isMarkerClick = false;
            groundOverlay1 = null;
            prevLatLng = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearMap() {
        lastMarkerHit = null;
        isMarkerClick = false;
        if (markerArrayList != null) {
            markerArrayList.clear();
        }
        if (markerProjectIdMap != null) {
            markerProjectIdMap.clear();
        }
    }

    /**
     * listener method of an interface IDrawMapBound which is to draw polygon
     */
    public void drawMapTask() {
        Log.d(TAG, "drwTask called");
        /**
         * This method gets called when user finishes the polygon draw
         */
        drawBtnState = DrawBtnState.drawn;
        if (getActivity() != null) {
            ((HomeScreen) getActivity()).setPolygonDrawn(true);
        }
        HomeScreen.switchBtn.setVisibility(View.VISIBLE);  //once user draw the polygon we need to enable switch btn
        polygonLatlngList.clear();
        mGoogleMap.clear();
        new DrawPathMap().execute();
    }

    class DrawPathMap extends AsyncTask<Void, Void, Void> {
        ArrayList<FloatPointDrawn> pointArray;

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            drawingView.setVisibility(View.GONE);
            for (int i = 0; i < pointArray.size(); i++) {
                Point x_y_points = new Point((int) pointArray.get(i).getX(), (int) pointArray.get(i).getY());
                polygonLatlngList.add(mGoogleMap.getProjection().fromScreenLocation(x_y_points));
                //drawBtnState = DrawBtnState.notdrawing;
            }
            drawPath();
        }

        @Override
        protected void onPreExecute() {
            pointArray = drawingView.getPoints();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void drawPath() {
        try {
            mGoogleMap.clear();
            PolygonOptions rectOptions = new PolygonOptions();

            if(polygonLatlngList != null) {
                rectOptions.addAll(polygonLatlngList);
            }
            rectOptions.strokeColor(Color.BLACK);
            rectOptions.strokeWidth(6);
            rectOptions.fillColor(0x7F000000);
            polygon = mGoogleMap.addPolygon(rectOptions);
            /*if (saveSearchWrapper != null && polygon != null && polygon.getPoints() != null) {
                saveSearchWrapper.latLongList = new ArrayList<>();
                for (int i = 0; i < polygon.getPoints().size(); i++) {
                    LatLong latLong = new LatLong();
                    latLong.setLongitude(polygon.getPoints().get(i).longitude);
                    latLong.setLatitude(polygon.getPoints().get(i).latitude);
                    saveSearchWrapper.latLongList.add(latLong);
                }
            }*/
            setBoundedMarkers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBoundedMarkers() {
        clearMap();
        setSwitchButtonVisibility(true);
        Log.d(TAG, "filteredSize->" + filteredProjectMap.size() + "\npolygon->" + polygon.getPoints().size());


        LinkedHashMap<Long,Project> polygonFilteredMap = new LinkedHashMap<>();
        for (Map.Entry<Long, Project> entry : filteredProjectMap.entrySet()) {
            Project pw = entry.getValue();
            //Creating a LatLng Object to store Coordinates
            LatLng latLng = new LatLng(pw.getLat(), pw.getLng());
            if ((polygon != null) && (!polygon.getPoints().isEmpty())) {
                if (PolyUtil.containsLocation(latLng, polygon.getPoints(), false)) {
                    if(selectedCircularView == null){
                        selectedCircularView = BitmapDescriptorFactory.fromResource(R.drawable.home_marker);
                    }
                    MarkerOptions markerOptions = new MarkerOptions()
                            .icon(selectedCircularView)
                            .anchor(0.5f,0.5f)
                            .position(latLng).title(pw.getName());
                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    markerArrayList.add(marker);
                    markerProjectIdMap.put(marker.getId(), pw.getId());
                    polygonFilteredMap.put(entry.getKey(),pw);
                    Log.d(TAG, "Added!");
                }
            }
        }
        if(filteredProjectMap != null){
            filteredProjectMap.clear();
        }
        filteredProjectMap.putAll(polygonFilteredMap);

        //setResultCount(filteredProjectMap.size());
        if ((filteredProjectMap != null) && (!filteredProjectMap.isEmpty())) {
            listPager.setVisibility(View.VISIBLE);
            setSortView(true);
            setSaveSearchView(true);
            setMapViewPager(filteredProjectMap);
        } else {
            Log.d(TAG, "shownSize is null");
            setSortView(false);
            setSaveSearchView(false);
            mRootView.findViewById(R.id.no_results_in_map).setVisibility(View.VISIBLE);
            setMapViewPager(null);
        }
        Log.i(TAG, "setBoundedMarkers: zoom Value -> " + mGoogleMap.getCameraPosition().zoom);
        if(polygonLatlngList != null && getActivity() != null) {
            ((HousingApplication)getActivity().getApplicationContext()).setPolygonLatLngList(polygonLatlngList);
        }
        /*if(mListener != null) {
            mListener.setListViewData(filteredProjectMap);
        }*/
    }

    private void setSwitchButtonVisibility(boolean isVisible){
        if(drawBtnState != null) {
            /*if(isVisible){
                ((SearchResults) getActivity()).fragmentSwitch.setVisibility(View.VISIBLE);
            }else{
                ((SearchResults) getActivity()).fragmentSwitch.setVisibility(View.GONE);
            }*/
        }
    }

    private void setResultCount(int count){
        if(count <= 1) {
            ((TextView) mRootView.findViewById(R.id.id_property_count)).setText(count + " Property");
        }else{
            ((TextView) mRootView.findViewById(R.id.id_property_count)).setText(count + " Properties");
        }
        if(getActivity() != null && ((HomeScreen)getActivity()).isPolygonDrawn()){
            mRootView.findViewById(R.id.id_polygon_msg).setVisibility(View.VISIBLE);
        }else{
            mRootView.findViewById(R.id.id_polygon_msg).setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setFilterView(boolean isEnable){
        if(getActivity() == null) {
            return;
        }
        boolean isSelected = UtilityMethods.getBooleanInPref(getActivity(), Constants.SharedPreferConstants.PARAM_FILTER_APPLIED, false);
        if(isSelected){
            if(mRootView != null){
                LinearLayout layout = mRootView.findViewById(R.id.id_criteria_layout);
                RelativeLayout filterLayout = layout.findViewById(R.id.id_filter_layout);
                TextView filterView = layout.findViewById(R.id.id_filter_view);
                ImageView filterImage = layout.findViewById(R.id.id_filter_img);
                filterLayout.setEnabled(true);
                filterLayout.setBackgroundResource(R.drawable.filter_rect_bg_red);
                UtilityMethods.setTextViewColor(getActivity(), filterView, R.color.colorAccent);
                filterImage.setImageResource(R.drawable.ic_filter_red);
            }
        }else{
            if(mRootView != null) {
                LinearLayout layout = mRootView.findViewById(R.id.id_criteria_layout);
                RelativeLayout filterLayout = layout.findViewById(R.id.id_filter_layout);
                TextView filterView = layout.findViewById(R.id.id_filter_view);
                ImageView filterImage = layout.findViewById(R.id.id_filter_img);
                if(isEnable) {
                    filterLayout.setEnabled(true);
                    filterLayout.setBackgroundResource(R.drawable.filter_rect_bg_gray);
                    UtilityMethods.setTextViewColor(getActivity(), filterView, R.color.gray_800);
                    filterImage.setImageResource(R.drawable.ic_filter_gray);
                }else{
                    filterLayout.setEnabled(false);
                    filterLayout.setBackgroundResource(R.drawable.filter_rect_bg_gray_disable);
                    UtilityMethods.setTextViewColor(getActivity(), filterView, R.color.gray_400);
                    filterImage.setImageResource(R.drawable.ic_filter_gray_disable);
                }

            }
        }
    }

    private void setSortView(boolean isEnable){
        if(getActivity() == null) {
            return;
        }
        boolean isSorted = UtilityMethods.isSortingApplied(getActivity());
        if(mRootView != null) {
            LinearLayout layout = mRootView.findViewById(R.id.id_criteria_layout);
            RelativeLayout relativeLayout = layout.findViewById(R.id.id_sort_view);
            ImageView sortImage = layout.findViewById(R.id.id_sort_img);
            TextView sortText = layout.findViewById(R.id.id_sort_text);
            if(isSorted){
                if(isEnable){
                    relativeLayout.setBackgroundResource(R.drawable.filter_rect_bg_red);
                    relativeLayout.setEnabled(true);
                    UtilityMethods.setTextViewColor(getActivity(), sortText, R.color.colorAccent);
                    sortImage.setImageResource(R.drawable.ic_sort_red);
                }else {
                    relativeLayout.setBackgroundResource(R.drawable.filter_rect_bg_red_disable);
                    relativeLayout.setEnabled(false);
                    UtilityMethods.setTextViewColor(getActivity(), sortText, R.color.red_light);
                    sortImage.setImageResource(R.drawable.ic_sort_red_disabled);
                }
            }
            else{
                if(isEnable){
                    relativeLayout.setBackgroundResource(R.drawable.filter_rect_bg_gray);
                    relativeLayout.setEnabled(true);
                    UtilityMethods.setTextViewColor(getActivity(), sortText, R.color.gray_800);
                    sortImage.setImageResource(R.drawable.ic_sort_gray);
                }else {
                    relativeLayout.setBackgroundResource(R.drawable.filter_rect_bg_gray_disable);
                    relativeLayout.setEnabled(false);
                    UtilityMethods.setTextViewColor(getActivity(), sortText, R.color.gray_400);
                    sortImage.setImageResource(R.drawable.ic_sort_disable);
                }
            }
        }
    }

    private void setSaveSearchView(boolean isEnable){
        if(getActivity() == null) {
            return;
        }
        boolean isSelected = false;
        if(isSelected){
            if(mRootView != null){
                LinearLayout layout = mRootView.findViewById(R.id.id_criteria_layout);
                RelativeLayout relativeLayout = mRootView.findViewById(R.id.id_saved_search_view);
                ImageView saveSearchImage = layout.findViewById(R.id.id_saved_search_img);
                TextView saveSearchText = layout.findViewById(R.id.id_save_search_text);
                if(isEnable){
                    relativeLayout.setEnabled(true);
                    UtilityMethods.setTextViewColor(getActivity(), saveSearchText, R.color.colorAccent);
                    saveSearchImage.setImageResource(R.drawable.ic_sort_red);
                }else{
                    relativeLayout.setEnabled(false);
                    UtilityMethods.setTextViewColor(getActivity(), saveSearchText, R.color.red_light);
                    saveSearchImage.setImageResource(R.drawable.ic_save_search_selected_disable);
                }
            }
        }else{
            if(mRootView != null) {
                LinearLayout layout = mRootView.findViewById(R.id.id_criteria_layout);
                RelativeLayout relativeLayout = mRootView.findViewById(R.id.id_saved_search_view);
                ImageView saveSearchImage = layout.findViewById(R.id.id_saved_search_img);
                TextView saveSearchText = layout.findViewById(R.id.id_save_search_text);
                if(isEnable){
                    relativeLayout.setEnabled(true);
                    UtilityMethods.setTextViewColor(getActivity(), saveSearchText, R.color.gray_800);
                    saveSearchImage.setImageResource(R.drawable.ic_save_search_gray);
                }else{
                    relativeLayout.setEnabled(false);
                    UtilityMethods.setTextViewColor(getActivity(), saveSearchText, R.color.gray_400);
                    saveSearchImage.setImageResource(R.drawable.ic_save_search_gray_disable);
                }
            }
        }
    }

    private void animateIcon(LatLng latLng, final Marker marker){
        if(prevLatLng != null && prevLatLng.equals(latLng)){
            return;
        }
        prevLatLng = latLng;
        AnimationSet animationSet = new AnimationSet(false);
        RippleExpand rippleExpand;

        Bitmap mDotMarkerBitmap1 = generateBitmapFromDrawable(R.drawable.un_selecteded_dot_bg);

        float dimension = getGroundOverlayDimention(latLng);
        if(dimension > 2500){
            dimension = 2500;
        }
        Log.i(TAG, "animateIcon: dimension " + dimension);
        final int animationDurationOne = 1000;

        if(groundOverlay1 != null && groundOverlay1.isVisible()){
            groundOverlay1.remove();
        }

        groundOverlay1 = mGoogleMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap1))
                .position(latLng, 100f));
        groundOverlay1.setZIndex(20.0f);

        rippleExpand = new RippleExpand(groundOverlay1,dimension,latLng);
        rippleExpand.setRepeatCount(Animation.ABSOLUTE);
        rippleExpand.setRepeatMode(Animation.ABSOLUTE);
        rippleExpand.setDuration(animationDurationOne);
        animationSet.addAnimation(rippleExpand);
        mMapView.startAnimation(animationSet);
    }


    class RippleExpand extends Animation {
        private GroundOverlay groundOverlay;
        private float startingSize = 1800;
        private LatLng latLng;

        public RippleExpand(GroundOverlay groundOverlay,float dimention,LatLng latLng) {
            this.groundOverlay = groundOverlay;
            startingSize = dimention;
            this.latLng = latLng;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            groundOverlay.setDimensions((startingSize * interpolatedTime));
            groundOverlay.setTransparency(0.4f);
            /*if(interpolatedTime==1){
                groundOverlay1 = mGoogleMap.addGroundOverlay(new GroundOverlayOptions()
                        .image(getMarkerIconFromDrawable(R.drawable.ripple_yellow_bg))
                        .position(latLng, 100f));
                groundOverlay.remove();
            }*/
        }


        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }
    }

    private float getGroundOverlayDimention(LatLng myLocation){
        //calculate meters*********************
        LatLngBounds myBounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng myCenter=  mGoogleMap.getCameraPosition().target;
        if (myCenter.latitude==0 || myCenter.longitude==0) {
            myCenter=new LatLng(myLocation.latitude,myLocation.longitude);
        }

        LatLng ne = myBounds.northeast;
        // r = radius of the earth in statute miles
        double r = 3963.0;

        // Convert lat or lng from decimal degrees into radians (divide by 57.2958)
        double lat1 = myCenter.latitude / 57.2958;
        double lon1 = myCenter.longitude / 57.2958;
        final double lat2 = ne.latitude / 57.2958;
        final double lon2 = ne.longitude / 57.2958;
        // distance = circle radius from center to Northeast corner of bounds
        double dis = r * Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1));
        //1 Meter = 0.000621371192237334 Miles
        double meters_calc=dis/0.000621371192237334;
        float factor=2;
        // if my zoom level change then I have to calculate dimension scale factor
        factor=(float) (meters_calc/7300);
        //******************************* now we are ready to set dimension of background overlay
        float dimensions=1000*factor;
        return dimensions;
    }


    @NonNull
    private Bitmap generateBitmapFromDrawable(int drawablesRes) {
        int px = 500;
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = getResources().getDrawable(drawablesRes);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }

    /**
     *
     20 : 1128.497220
     19 : 2256.994440
     18 : 4513.988880
     17 : 9027.977761
     16 : 18055.955520
     15 : 36111.911040
     14 : 72223.822090
     13 : 144447.644200
     12 : 288895.288400
     11 : 577790.576700
     10 : 1155581.153000
     9  : 2311162.307000
     8  : 4622324.614000
     7  : 9244649.227000
     6  : 18489298.450000
     5  : 36978596.910000
     4  : 73957193.820000
     3  : 147914387.600000
     2  : 295828775.300000
     1  : 591657550.500000
     * To Calculate google map distance based on zoom level
     * @param zoomLevel : current zoom level of map
     * @return : actual distance based on zoom level
     */
    private double getMapDistance(Float zoomLevel){
        System.out.println("Acutal zoom level " + zoomLevel);
        int baseDistance = 15000;
        int zoomLevelIntValue = zoomLevel.intValue();
        System.out.println("int value of zoom level " + zoomLevelIntValue);
        double distance = (baseDistance/Math.pow(2,zoomLevelIntValue+1)) * 1000;
        System.out.println("distance based on int value of zoom level " + distance);
        if(zoomLevel == zoomLevel.intValue()){
            return distance;
        }else{
            float decimalValue = zoomLevel-zoomLevel.intValue();
            System.out.println("Decimal value " + decimalValue);
            double percentValue = distance/100;
            System.out.println("Decimal percentage  " + percentValue);
            double decimalValueDistance = decimalValue*100*percentValue;
            System.out.println("Decimal Value distance " + decimalValueDistance);
            System.out.println("Distance based on zoom "+(distance-(decimalValueDistance/2)));
            return (distance+decimalValueDistance);
        }
    }

    private LatLng getBearingPoints(double bearing, double distance, LatLng mapCenter){
        double R = 6378.1;                         //Radius of the Earth
        bearing = Math.toRadians(bearing);              //Bearing is 90 degrees converted to radians.

        double lat1 = Math.toRadians(mapCenter.latitude);   //Current lat point converted to radians
        double lon1 = Math.toRadians(mapCenter.longitude);   //Current long point converted to radians

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / R) +
                Math.cos(lat1) * Math.sin(distance / R) * Math.cos(bearing));

        double lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(distance / R) * Math.cos(lat1),
                Math.cos(distance / R) - Math.sin(lat1) * Math.sin(lat2));

        double latitude = Math.toDegrees(lat2);
        double longitude = Math.toDegrees(lon2);

        return new LatLng(latitude,longitude);
    }

    public Bitmap createMarker(Context context) {
        Bitmap returnedBitmap = null;
        try {
            View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_circle_marker_view, null);

            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            returnedBitmap = Bitmap.createBitmap(customMarkerView.getDrawingCache());

            if(returnedBitmap != null){
                customMarkerView.setDrawingCacheEnabled(true);
                BitmapDrawable drawable = new BitmapDrawable(returnedBitmap);
                return drawable.getBitmap();
            }else {
                returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(returnedBitmap);
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);


                if (customMarkerView.getDrawingCache() != null) {
                    return customMarkerView.getDrawingCache();
                }
                Drawable drawable = customMarkerView.getBackground();
                if (drawable != null) {
                    drawable.draw(canvas);
                }
                customMarkerView.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnedBitmap;
    }

    public void showNetworkErrorSnackBar(View view) {
        draw.setEnabled(false);
        layers.setEnabled(false);
        currentLocation.setEnabled(false);
        final Snackbar snackbar = Snackbar.make(view, getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(mActivity, R.color.uber_red));
        snackbar.setActionTextColor(ContextCompat.getColor(mActivity, R.color.white));
        snackbar.show();
    }

    private void setResultView(){
        if(resultTypeValue.equalsIgnoreCase(ResultType.onSuccess.toString())){
            binding.noResultsInMap.setVisibility(View.GONE);
            binding.drawingLayout.setVisibility(View.VISIBLE);
            binding.idFloatingBtnLayout.setVisibility(View.VISIBLE);
            binding.mapViewpager.setVisibility(View.VISIBLE);
            binding.idNoInternetLayout.setVisibility(View.GONE);
            setResultCount(totalProjectCount);
            setSortView(true);
            setSaveSearchView(true);
            setFilterView(true);
            setProjectMap(projectList);
        }else if(resultTypeValue.equalsIgnoreCase(ResultType.onEmpty.toString())
                || resultTypeValue.equalsIgnoreCase(ResultType.onError.toString())){
            binding.noResultsInMap.setVisibility(View.VISIBLE);
            binding.drawingLayout.setVisibility(View.GONE);
            if(polygonLatlngList == null || polygonLatlngList.isEmpty()) {
                binding.idFloatingBtnLayout.setVisibility(View.GONE);
            }
            binding.idNoInternetLayout.setVisibility(View.GONE);
            binding.mapViewpager.setVisibility(View.GONE);
            projectList.clear();
            filteredProjectMap.clear();
            setMarkersOnUpdate();
            setResultCount(0);
            setSortView(false);
            setSaveSearchView(false);
            setFilterView(false);
        }else if(resultTypeValue.equalsIgnoreCase(ResultType.onInternet.toString())){
            binding.noResultsInMap.setVisibility(View.GONE);
            binding.drawingLayout.setVisibility(View.GONE);
            if(polygonLatlngList == null || polygonLatlngList.isEmpty()) {
                binding.idFloatingBtnLayout.setVisibility(View.GONE);
            }
            binding.mapViewpager.setVisibility(View.GONE);
            binding.idNoInternetLayout.setVisibility(View.VISIBLE);
            projectList.clear();
            filteredProjectMap.clear();
            setMarkersOnUpdate();
            setResultCount(0);
            setSortView(false);
            setSaveSearchView(false);
            setFilterView(false);
        }


        /*if(mRootView != null) {
            mRootView.findViewById(R.id.no_results_in_map).setVisibility(View.GONE);
            mRootView.findViewById(R.id.drawing_layout).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.id_floating_btn_layout).setVisibility(View.VISIBLE);
            listPager.setVisibility(View.VISIBLE);
            setSortView(true);
            setSaveSearchView(true);
            setFilterView(true);
        }*/
    }

    public void callOnFilterApplied(){
        Log.i(TAG, "callOnFilterApplied: ");
        apiCallOnMovingMap = false;
        if(!UtilityMethods.isInternetConnected(getActivity())){
            resultTypeValue = ResultType.onInternet.toString();
            setResultView();
            return;
        }
        if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString()) && ((HomeScreen)mActivity).getDeveloperId() != 0){
            getProjectListByDeveloper(((HomeScreen)mActivity).getDeveloperId());
        }else if(searchType.equalsIgnoreCase(SearchBy.SearchByLocation.toString())
                && ((HomeScreen)mActivity).getLatitude() != 0 && ((HomeScreen)mActivity).getLongitude() != 0){
            getProjectListByDistance(((HomeScreen)mActivity).getLatitude(),((HomeScreen)mActivity).getLongitude());
        }else{
            getProjectListByCity();
        }
    }


    public void callOnSaveSearchApplied(SaveSearchWrapper saveSearchWrapper){
        Log.i(TAG, "callOnSaveSearchApplied: ");
        apiCallOnMovingMap = false;
    }

    public void callOnSearchingApplied(double latitude, double longitude){
        Log.i(TAG, "callOnSearchingApplied: ");
        apiCallOnMovingMap = false;
        //callProjectListByDistanceAPI(latitude,longitude);
        getProjectListByDistance(latitude,longitude);
    }

    public void callOnCityChanged(){
        Log.i(TAG, "callOnCityChanged: ");
        apiCallOnMovingMap = false;
        //callProjectListAPI();
        getProjectListByCity();
    }

    public void callOnNoInternetTryAgain(){
        Log.i(TAG, "callOnNoInternetTryAgain: ");
        //callProjectListAPI();
        if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString()) && ((HomeScreen)mActivity).getDeveloperId() != 0){
            getProjectListByDeveloper(((HomeScreen)mActivity).getDeveloperId());
        }else if(searchType.equalsIgnoreCase(SearchBy.SearchByLocation.toString())
                && ((HomeScreen)mActivity).getLatitude() != 0 && ((HomeScreen)mActivity).getLongitude() != 0){
            getProjectListByDistance(((HomeScreen)mActivity).getLatitude(),((HomeScreen)mActivity).getLongitude());
        }else{
            getProjectListByCity();
        }
    }

    /**
     * Broadcaste Receiver which gets called while unit change from project details screen
     */

    private BroadcastReceiver onUnitChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String selectedPropertyUnit = UtilityMethods.getSelectedUnit(mActivity);
            if(selPropUnit.equalsIgnoreCase(selectedPropertyUnit)){
               return;
            }
            if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString()) && ((HomeScreen)mActivity).getDeveloperId() != 0){
                getProjectListByDeveloper(((HomeScreen)mActivity).getDeveloperId());
            }else if(searchType.equalsIgnoreCase(SearchBy.SearchByLocation.toString())
                    && ((HomeScreen)mActivity).getLatitude() != 0 && ((HomeScreen)mActivity).getLongitude() != 0){
                getProjectListByDistance(((HomeScreen)mActivity).getLatitude(),((HomeScreen)mActivity).getLongitude());
            }else{
                getProjectListByCity();
            }
        }
    };

}
