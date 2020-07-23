package com.clicbrics.consumer.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buy.housing.backend.propertyEndPoint.model.FooterLinkListResponse;
import com.buy.housing.backend.userEndPoint.model.LatLong;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.MapViewHelpScreen;
import com.clicbrics.consumer.activities.PickCity;
import com.clicbrics.consumer.activities.SearchActivity;
import com.clicbrics.consumer.analytics.AppEventAnalytics;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.DrawingView;
import com.clicbrics.consumer.fragment.MoreFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.helper.PopularProjectCallback;
import com.clicbrics.consumer.interfaces.IPropertyOperationsListener;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.CheckUpdateStatusUtility;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.SaveSearchUtility;
import com.clicbrics.consumer.utils.SystemConfig;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.clicworth.ClicworthFragment;
import com.clicbrics.consumer.view.fragment.HomeDecorFragment;
import com.clicbrics.consumer.view.fragment.MapFragment;
import com.clicbrics.consumer.view.fragment.NewsFragment;
import com.clicbrics.consumer.view.fragment.ProjectListFragment;
import com.clicbrics.consumer.viewmodel.ProjectListViewModel;
import com.clicbrics.consumer.wrapper.Notificaiton;
import com.clicbrics.consumer.wrapper.SaveSearchWrapper;
import com.clicbrics.consumer.wrapper.TopLocalityModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import com.clicbrics.consumer.view.fragment.RecentProjectFragment;

public class HomeScreen extends BaseActivity implements
        TabLayout.OnTabSelectedListener, PopularProjectCallback,
        DrawingView.IDrawMapBound, IPropertyOperationsListener/*, PlaceAutoCompleteLocalityAdapter.PlacesAutocompleteLocalityInterface*/ {

    private static final String TAG = "HomeScreen";
    private TabLayout tabLayout;
    private TextView cityNameText,searchTextView;
    private int mSelectedFragment = 0;
    private SaveSearchWrapper saveSearchWrapper;

    private String saveSearchName;
    private long searchByBuilderId, searchByCityId;
    private String searchByBuilderName;
    private String searchByCityName;
    public String searchAddress;
    private String searchAdditionAddress;
    private LatLng searchByLocation;
    private String selectedCityName;
    private long selectedCityId;
    private boolean doubleTapToExit;

    //Constants
    private static final int NOTIFICATION_REFRESH_INTERVAL = 900000; // 15 minutes in miliseconds
    private final int HOME =0;
    private final int CLICWORTH =1;
    private final int HOME_DECOR =2;
    private final int NEWS =3;
    private final int MORE =4;
    private final int MAP =5;
    private static final int RESULT_CHANGE_CITY = 102;
    public static final int FILTER_REQUEST_CODE = 103;
    private static final int RESULT_PROPERTY_SEARCH = 9879;
    protected LoginFor loginFor;
    public static FloatingActionButton switchBtn;
    public static LatLng searchLocation;
    private ProjectListViewModel model;

    private String[] title = {"","CLICWORTH","HOME DECOR IDEAS","NEWS","EXPLORE MORE"};
    private String selectedUnit = "";
    private boolean isPolygonDrawn = false;

    private String searchType;
    public double latitude, longitude;
    private long developerId;
   /* private PlaceAutoCompleteLocalityAdapter mAdapter;
    public GeoDataClient geoDataClient;
    private String searchAddress_clicworth = "";
    private String city_name;*/
    private List<com.clicbrics.consumer.model.Project> projectList= new ArrayList<>();

    /*@Override
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
                                }
                                if (mResultList.get(position).secondaryText != null) {
                                    secondaryText = mResultList.get(position).secondaryText.toString();
                                }
                                Intent data = new Intent();
                                data.putExtra("lat", String.valueOf(places.get(0).getLatLng().latitude));
                                data.putExtra("lng", String.valueOf(places.get(0).getLatLng().longitude));
                                long cityId = 0;
                                String cityName = "";
                                if (mResultList.get(position).cityId != 0) {
                                    cityId = mResultList.get(position).cityId;
                                } else {
                                    cityId = UtilityMethods.getLongInPref(HomeScreen.this, Constants.AppConstants.ESTIMATE_CITY_ID, 0);
                                }
                                if (!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                                    cityName = mResultList.get(position).cityName;

                                } else {
                                    cityName = UtilityMethods.getStringInPref(HomeScreen.this, Constants.AppConstants.ESTIMATE_CITY, "");
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
                                        clicworth_interface.updatefragvalue(primartyText + " " + secondaryText, latitude, longitude);
//                                        binding.searchLocality.setText(primartyText + " " + secondaryText);
                                    } else {
                                        searchAddress_clicworth = primartyText;
//                                        binding.searchLocality.setText(primartyText);
                                        clicworth_interface.updatefragvalue(primartyText, latitude, longitude);
                                    }
                                }
//                                binding.listSearch.setVisibility(View.GONE);
                                clicworth_interface.updateViewVisibility("gone");
//                                isDraggable = true;


                            } else {
                                if (Geocoder.isPresent()) {
                                    try {
                                        String primartyText = "", secondaryText = "";
                                        if (mResultList.get(position).primaryText != null) {
                                            primartyText = mResultList.get(position).primaryText.toString();
                                        }
                                        if (mResultList.get(position).secondaryText != null) {
                                            secondaryText = mResultList.get(position).secondaryText.toString();
                                        }
                                        String location = UtilityMethods.getStringInPref(HomeScreen.this, Constants.AppConstants.ESTIMATE_CITY, "");
                                        Geocoder gc = new Geocoder(HomeScreen.this);
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
                                                    cityId = UtilityMethods.getLongInPref(HomeScreen.this, Constants.AppConstants.ESTIMATE_CITY_ID, 0);
                                                }
                                                if (!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                                                    cityName = mResultList.get(position).cityName;
                                                } else {
                                                    cityName = UtilityMethods.getStringInPref(HomeScreen.this, Constants.AppConstants.ESTIMATE_CITY, "");
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
                                                        clicworth_interface.updatefragvalue(primartyText + " " + secondaryText, latitude, longitude);
//                                                        binding.searchLocality.setText(primartyText + " " + secondaryText);
                                                    } else {
                                                        searchAddress_clicworth = primartyText;
//                                                        binding.searchLocality.setText(primartyText);
                                                        clicworth_interface.updatefragvalue(primartyText, latitude, longitude);
                                                    }
                                                }
                                                city_name = cityName;
//                                                isDraggable = true;
                                                clicworth_interface.updateViewVisibility("gone");
//                                                binding.listSearch.setVisibility(View.GONE);

                                            } else {
                                                Toast.makeText(HomeScreen.this, "results not found!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(HomeScreen.this, "results not found!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(HomeScreen.this, "results not found!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(HomeScreen.this, "results not found!", Toast.LENGTH_SHORT).show();
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
                    }
                    if (mResultList.get(position).secondaryText != null) {
                        secondaryText = mResultList.get(position).secondaryText.toString();
                    }
                    Intent data = new Intent();
                    data.putExtra("lat", String.valueOf(mResultList.get(position).latitude));
                    data.putExtra("lng", String.valueOf(mResultList.get(position).longitude));
                    long cityId = 0;
                    String cityName = "";
                    if (mResultList.get(position).cityId != 0) {
                        cityId = mResultList.get(position).cityId;
                    } else {
                        cityId = UtilityMethods.getLongInPref(HomeScreen.this, Constants.AppConstants.ESTIMATE_CITY_ID, 0);
                    }
                    if (!TextUtils.isEmpty(mResultList.get(position).cityName)) {
                        cityName = mResultList.get(position).cityName;
                    } else {
                        cityName = UtilityMethods.getStringInPref(HomeScreen.this, Constants.AppConstants.ESTIMATE_CITY, "");
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
                    clicworth_interface.updateViewVisibility("gone");
//                    binding.listSearch.setVisibility(View.GONE);
                    latitude = mResultList.get(position).latitude;
                    longitude = mResultList.get(position).longitude;
                    LatLng latlng = new LatLng(latitude, longitude);
                    if (!TextUtils.isEmpty(primartyText)) {
                        if (!TextUtils.isEmpty(secondaryText)) {
                            searchAddress_clicworth = primartyText + " " + secondaryText;
//                            binding.searchLocality.setText(primartyText + " " + secondaryText);
                            clicworth_interface.updatefragvalue(primartyText + " " + secondaryText, latitude, longitude);
                        } else {
                            searchAddress_clicworth = primartyText;
                            clicworth_interface.updatefragvalue(primartyText, latitude, longitude);
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
    }*/


   /* Clicworth_interface clicworth_interface;

    public void Intialiselistener(Clicworth_interface context)
    {
        clicworth_interface=context;
    }*/

    private enum SearchBy{
        SearchByLocation,SearchByDeveloper,SearchByCity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
//        Intialiselistener(HomeScreen.this);
        selectedCityId = UtilityMethods.getLongInPref(this,Constants.AppConstants.SAVED_CITY_ID,-1);
        selectedCityName = UtilityMethods.getStringInPref(this,Constants.AppConstants.SAVED_CITY,"");
        if(searchLocation == null){
            searchLocation = new LatLng(Constants.AppConstants.DEFAULT_LAT,Constants.AppConstants.DEFAULT_LONG);
        }
        UtilityMethods.saveBooleanInPref(this, Constants.SharedPreferConstants.FIRST_LAUNCH, false);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        UtilityMethods.setStatusBarColor(this,R.color.gray_300);
//        model = new ProjectListViewModel(this);
        model = new ProjectListViewModel(this);
//        geoDataClient= Places.getGeoDataClient(this);
        saveBuilderAndProjectList();

        initToolbar();
        initView();

        if(getIntent().hasExtra("InvalidProjectError") && !TextUtils.isEmpty(getIntent().getStringExtra("InvalidProjectError"))){
            String errorMsg = getIntent().getStringExtra("InvalidProjectError");
            UtilityMethods.showSnackbarOnTop(this,"Error",errorMsg,true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
        }


        checkUpdateStatus();
        if(BuildConfigConstants.configName.equalsIgnoreCase("housingTestServer-Config-App.png")){
            FirebaseMessaging.getInstance().subscribeToTopic("offerstest");
        }else {
            FirebaseMessaging.getInstance().subscribeToTopic("offers");
        }
        selectedUnit = UtilityMethods.getSelectedUnit(this);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.id_fragment,new ProjectListFragment(),Constants.AppConstants.PROJECT_LIST_FRAGMENT);
        transaction.commit();
        //handlePushNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginFor = LoginFor.DEFAULT;
        String cityName = UtilityMethods.getStringInPref(this, Constants.AppConstants.SAVED_CITY, "");
        if (!TextUtils.isEmpty(cityName)) {
            cityNameText.setText(cityName);
        }
        setToolBar();

        long notificationRefreshTime = UtilityMethods.getLongInPref(this, Constants.SharedPreferConstants.NOTIFICATION_REFRESH_TIME, 0l);
        if (notificationRefreshTime != 0 && (System.currentTimeMillis() - NOTIFICATION_REFRESH_INTERVAL) >= notificationRefreshTime) {
            checkUpdateStatus();
        }

        if(((HousingApplication)getApplicationContext()).isFromLogin()){
            ((HousingApplication)getApplicationContext()).setFromLogin(false);
        }

    }

    @Override
    public void onBackPressed() {
        if (doubleTapToExit) {
            super.onBackPressed();
            return;
        }

        this.doubleTapToExit = true;
        Toast toast = new Toast(this);
        toast.setView(getLayoutInflater().inflate(R.layout.custom_toast_layout,null));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleTapToExit=false;
            }
        }, 2000);
    }

    /**
     * onClick lister of city text
     * @param view- onClicked view
     */
    public void onCityChangeClickListener(View view) {
        Log.i(TAG, "onClickOnCityChange: ");
        if(!isInternetConnected()){
            return;
        }
        Intent intent = new Intent(this, PickCity.class);
        startActivityForResult(intent, RESULT_CHANGE_CITY);
    }

    /**
     * onClick lister of search bar
     * @param view- search bar view
     */
    public void onClickSearchBar(View view) {
        Log.i(TAG, "onClickSearchBar: ");
        if(!isInternetConnected()){
            return;
        }
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent,RESULT_PROPERTY_SEARCH);
    }

    /**
     * onClick lister of search bar clear button
     * @param view- search bar view
     */
    public void onClickSearchClearBtn(View view){
        Log.i(TAG, "onClickSearchClearBtn: ");
        searchType = SearchBy.SearchByCity.toString();
        searchByBuilderId = -1;
        searchByBuilderName = "";
        searchAddress = "";
        searchAdditionAddress = "";
        saveSearchName = "";
        ImageView clearBtn = findViewById(R.id.id_search_clear_btn);
        TextView searchTextView = findViewById(R.id.search_title);
        clearBtn.setVisibility(View.GONE);
        searchTextView.setText("");
        Fragment fragment = getFragment(mSelectedFragment);
        if(fragment instanceof ProjectListFragment){
            ProjectListFragment projectListFragment = (ProjectListFragment) fragment;
            projectListFragment.getProjectListByCity();
        }
        else if(fragment instanceof MapFragment){
            MapFragment mapFragment = (MapFragment) fragment;
            mapFragment.getProjectListByCity();
        }
    }


    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.id_home_screen_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView(){

        tabLayout = findViewById(R.id.id_home_tabbar_layout);
        tabLayout.setTabTextColors(Color.parseColor("#474747"), Color.parseColor("#F83618"));
        cityNameText = findViewById(R.id.id_change_city);

        createTabs();
        setupTabIcons(HOME);

        mSelectedFragment = HOME;
        tabLayout.addOnTabSelectedListener(this);

        switchBtn = findViewById(R.id.fragment_switch);
        switchBtn.setTag("list");
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.i(TAG, "On click of switch button " + mSelectedFragment);
                if(mSelectedFragment == HOME) {
                    switchBtn.setImageResource(R.drawable.image_list);
                    switchBtn.setTag("map");
                    setFragment(MAP);
                    mSelectedFragment = MAP;

                    /**
                     * To show MapView Help screen
                     */
                    if (UtilityMethods.isInternetConnected(HomeScreen.this)
                            && !UtilityMethods.getBooleanInPref(HomeScreen.this, Constants.SharedPreferConstants.IS_MAP_HELPSCREEN_SEEN, false)) {
                        UtilityMethods.saveBooleanInPref(HomeScreen.this, Constants.SharedPreferConstants.IS_MAP_HELPSCREEN_SEEN, true);
                        MapViewHelpScreen mapViewHelpScreen = new MapViewHelpScreen(HomeScreen.this);
                        mapViewHelpScreen.show();
                    }
                }else{
                    switchBtn.setTag("list");
                    switchBtn.setImageResource(R.drawable.shape);
                    setFragment(HOME);
                    mSelectedFragment = HOME;
                }
            }
        });

        /**
         * if user come from splash screen in case of deeplinking and wants to show news/homedecor/articles list
         */
        if(getIntent().hasExtra("selectedTab") && getIntent().getStringExtra("selectedTab").equalsIgnoreCase("news")){
            mSelectedFragment = NEWS;
            tabLayout.getTabAt(mSelectedFragment).select();
        }/*else if(getIntent().hasExtra("selectedTab") && getIntent().getStringExtra("selectedTab").equalsIgnoreCase("articles")){
            mSelectedFragment = ARTICLE;
            tabLayout.getTabAt(mSelectedFragment).select();
        }*/
        else if(getIntent().hasExtra("selectedTab") && getIntent().getStringExtra("selectedTab").equalsIgnoreCase("homeDecor")){
            mSelectedFragment = HOME_DECOR;
            tabLayout.getTabAt(mSelectedFragment).select();
        }

    }

    private void createTabs(){
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.home_custom_tab_view));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.home_custom_tab_view));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.home_custom_tab_view));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.home_custom_tab_view));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.home_custom_tab_view));
    }

    private void setupTabIcons(int position){
        View homeView = tabLayout.getTabAt(HOME).getCustomView();
        ((ImageView)homeView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_home);
        ((TextView)homeView.findViewById(R.id.id_tab_text)).setText("HOME");
        UtilityMethods.setTextViewColor(this,(TextView)homeView.findViewById(R.id.id_tab_text),R.color.gray_800);

        View decorView = tabLayout.getTabAt(HOME_DECOR).getCustomView();
        ((ImageView)decorView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_ideas);
        ((TextView)decorView.findViewById(R.id.id_tab_text)).setText("IDEAS");
        UtilityMethods.setTextViewColor(this,(TextView)decorView.findViewById(R.id.id_tab_text),R.color.gray_800);

        View newsView = tabLayout.getTabAt(NEWS).getCustomView();
        ((ImageView)newsView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_news);
        ((TextView)newsView.findViewById(R.id.id_tab_text)).setText("NEWS");
        UtilityMethods.setTextViewColor(this,(TextView)newsView.findViewById(R.id.id_tab_text),R.color.gray_800);

        View blogView = tabLayout.getTabAt(CLICWORTH).getCustomView();
        ((ImageView)blogView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_clicworth);
        ((TextView)blogView.findViewById(R.id.id_tab_text)).setText("CLICWORTH");
        UtilityMethods.setTextViewColor(this,(TextView)blogView.findViewById(R.id.id_tab_text),R.color.gray_800);

        View recentView = tabLayout.getTabAt(MORE).getCustomView();
        ((ImageView)recentView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_more);
        ((TextView)recentView.findViewById(R.id.id_tab_text)).setText("MORE");
        UtilityMethods.setTextViewColor(this,(TextView)recentView.findViewById(R.id.id_tab_text),R.color.gray_800);

        switch (position){
            case HOME:{
                ((ImageView)homeView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_home_selected);
                UtilityMethods.setTextViewColor(this,(TextView)homeView.findViewById(R.id.id_tab_text),R.color.colorAccent);
            }break;
            case HOME_DECOR:{
                ((ImageView)decorView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_ideas_selected);
                UtilityMethods.setTextViewColor(this,(TextView)decorView.findViewById(R.id.id_tab_text),R.color.colorAccent);
            }break;
            case NEWS:{
                ((ImageView)newsView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_news_selected);
                UtilityMethods.setTextViewColor(this,(TextView)newsView.findViewById(R.id.id_tab_text),R.color.colorAccent);
            }break;
            case CLICWORTH:{
                ((ImageView)blogView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_clicworth_selected);
                UtilityMethods.setTextViewColor(this,(TextView)blogView.findViewById(R.id.id_tab_text),R.color.colorAccent);
            }break;
            case MORE:{
                ((ImageView)recentView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_more_red);
                UtilityMethods.setTextViewColor(this,(TextView)recentView.findViewById(R.id.id_tab_text),R.color.colorAccent);
            }break;
            case MAP:{
                ((ImageView)homeView.findViewById(R.id.id_tab_icon)).setImageResource(R.drawable.ic_home_selected);
                UtilityMethods.setTextViewColor(this,(TextView)homeView.findViewById(R.id.id_tab_text),R.color.colorAccent);
            }break;
        }
    }

    private void setToolBar(){
        if(mSelectedFragment == HOME || mSelectedFragment == MAP) {
            findViewById(R.id.id_home_screen_appbar).setVisibility(View.VISIBLE);
            findViewById(R.id.id_home_screen_toolbar).setVisibility(View.VISIBLE);
            findViewById(R.id.id_other_screen_toolbar).setVisibility(View.GONE);
            findViewById(R.id.id_clicworth_head).setVisibility(View.GONE);

        }
        else if(mSelectedFragment==CLICWORTH)
        {

            findViewById(R.id.id_home_screen_appbar).setVisibility(View.GONE);
            findViewById(R.id.id_home_screen_toolbar).setVisibility(View.GONE);
            findViewById(R.id.id_other_screen_toolbar).setVisibility(View.GONE);
            findViewById(R.id.id_clicworth_head).setVisibility(View.VISIBLE);
//            findViewById(R.id.id_clicworth_head).setVisibility(View.VISIBLE);
           /* Toolbar toolbar = findViewById(R.id.id_clicworth_head);
            ImageButton backbutton = toolbar.findViewById(R.id.backbutton);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            setSupportActionBar(toolbar);
            toolbar.setVisibility(View.VISIBLE);*/
        }
        else
            {
                findViewById(R.id.id_home_screen_appbar).setVisibility(View.VISIBLE);
            findViewById(R.id.id_home_screen_toolbar).setVisibility(View.GONE);
            findViewById(R.id.id_clicworth_head).setVisibility(View.GONE);
            Toolbar toolbar = findViewById(R.id.id_other_screen_toolbar);
            TextView titleView = toolbar.findViewById(R.id.id_title);
            setSupportActionBar(toolbar);
            titleView.setText(title[mSelectedFragment]);
            toolbar.setVisibility(View.VISIBLE);
            }
    }


    private void handleSwitchBtn(){
        if(mSelectedFragment == HOME || mSelectedFragment == MAP) {
            switchBtn.setVisibility(View.VISIBLE);
        }else{
            switchBtn.setVisibility(View.GONE);
        }
    }


    private void checkUpdateStatus(){
        CheckUpdateStatusUtility updateStatusUtility = new CheckUpdateStatusUtility(this);
        updateStatusUtility.checkUpdateStatus();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        if(position == HOME && switchBtn.getTag().equals("map")){
            position = MAP;
        }
        setFragment(position);
        mSelectedFragment = position;
        setupTabIcons(position);
        setToolBar();
        handleSwitchBtn();

        /**
         * To check the notification, for every 15 minutes interval we need to hit server to check if there is any notificaiton has come.
         */
        long notificationRefreshTime = UtilityMethods.getLongInPref(this, Constants.SharedPreferConstants.NOTIFICATION_REFRESH_TIME, 0l);
        if (notificationRefreshTime != 0 && (System.currentTimeMillis() - NOTIFICATION_REFRESH_INTERVAL) >= notificationRefreshTime) {
            checkUpdateStatus();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }



    private void setCityName() {
        //String cityName = UtilityMethods.getStringInPref(this, Constants.AppConstants.SAVED_CITY,"");
        UtilityMethods.saveStringInPref(this,Constants.AppConstants.SAVED_CITY,selectedCityName);
        UtilityMethods.saveLongInPref(this,Constants.AppConstants.SAVED_CITY_ID,selectedCityId);
        if(!selectedCityName.isEmpty()){
            cityNameText.setText(selectedCityName);
        }
        handleLocalCityData();
        saveBuilderAndProjectList();
    }

    /**
     * Method to save the pname and state for selected city which we use to search the result
     */
    private void handleLocalCityData(){
        ArrayList<com.clicbrics.consumer.utils.City> cityList = UtilityMethods.getCityList(this);
        if(cityList != null && !cityList.isEmpty()){
            for(int i=0; i<cityList.size(); i++){
                com.clicbrics.consumer.utils.City city = cityList.get(i);
                if(city != null  && city.getId() != null && city.getId().equals(selectedCityId)){
                    if(!TextUtils.isEmpty(city.getpName())){
                        Log.i(TAG, "handleLocalCityData: city name " + city.getpName() + " pname " + city.getpName());
                        UtilityMethods.saveStringInPref(this,Constants.AppConstants.SAVED_PNAME,city.getpName());
                    }else{
                        UtilityMethods.removeFromPref(this,Constants.AppConstants.SAVED_PNAME);
                    }
                    if(!TextUtils.isEmpty(city.getState())){
                        UtilityMethods.saveStringInPref(this,Constants.AppConstants.SAVED_STATE,city.getState());
                    }
                    break;
                }
            }
        }
    }

    private void setSearchTextView(){
        searchTextView = findViewById(R.id.search_title);
        ImageView clearBtn = findViewById(R.id.id_search_clear_btn);
        if(!TextUtils.isEmpty(searchAddress) && !TextUtils.isEmpty(searchAdditionAddress)){
            clearBtn.setVisibility(View.VISIBLE);
            searchTextView.setText(searchAddress + ", " + searchAdditionAddress);
        }else if(!TextUtils.isEmpty(searchAddress)){
            clearBtn.setVisibility(View.VISIBLE);
            searchTextView.setText(searchAddress);
        }
    }

    /**
     * Method to check if city id has changed(same city is not selected again)
     * then fetch the builder and project list of selected city to save locally
     */
    private void saveBuilderAndProjectList(){
//        model.getBuilderAndProjectList(selectedCityId);
        model.getTopProjet(selectedCityId);
    }


    /**
     * Method to get projects by developer
     * @param developerId
     * @param developerName
     */
    private void getProjectByDeveloper(long developerId, String developerName){
        if(developerId != -1 && !TextUtils.isEmpty(developerName)) {
            searchType = SearchBy.SearchByDeveloper.toString();
            this.developerId = developerId;
            Fragment fragment = getFragment(mSelectedFragment);
            if(fragment instanceof ProjectListFragment){
                ProjectListFragment projectListFragment = (ProjectListFragment) fragment;
                //projectListFragment.setBuilderIdVaribale(developerId);
                projectListFragment.getProjectListByDeveloper(developerId);
            }
            else if(fragment instanceof MapFragment){
                MapFragment mapFragment = (MapFragment) fragment;
                //mapFragment.setBuilderIdVaribale(developerId);
                mapFragment.getProjectListByDeveloper(developerId);
            }
            //onCityChanged();
        }
    }

    /**
     * Method to get projects list by given location
     * @param latitude
     * @param longitude
     */
    private void getProjectByLocation(double latitude, double longitude){
        if(latitude != -1 && longitude != -1) {
            onSearchApplied(latitude,longitude);
        }
    }

    /**
     * callback method of interface SearchPropertyResultCallback to check internect connectivity
     * @return - true or false
     */
    @Override
    public boolean isInternetConnected() {
        if(UtilityMethods.isInternetConnected(this)){
            return true;
        }else{
            UtilityMethods.showSnackbarOnTop(mActivity,"Error",getString(R.string.no_network_connection),true,1500);
            return false;
        }
    }

    @Override
    public void onSuccessPopularProject(FooterLinkListResponse projectBuilderCollection) {
//        Log.i(TAG, "onSuccessPopularProject: ======="+projectBuilderCollection.getTopLocalityList());
        if(projectBuilderCollection.getTopLocalityList()!=null&&projectBuilderCollection.getTopLocalityList().size()>0)
        {
            new EventAnalyticsHelper().logAPICallEvent(HomeScreen.this, Constants.AnaylticsClassName.HomeScreen,
                    null, Constants.ApiName.getFooterLink.toString(),Constants.AnalyticsEvents.SUCCESS,null);
            ArrayList<TopLocalityModel> topLocalityModelsarray = new ArrayList<>();
            int size = projectBuilderCollection.getTopLocalityList().size();
          /*  if(size > 5){
                size = 5;   //we need to show only 5 element from the list
            }*/

            for(int i=0; i<size; i++)
            {
                TopLocalityModel topLocality=new TopLocalityModel();
                topLocality.cityName=(projectBuilderCollection.getTopLocalityList().get(i).getCityName());
                topLocality.cityId=(projectBuilderCollection.getTopLocalityList().get(i).getId());
                topLocality.name=(projectBuilderCollection.getTopLocalityList().get(i).getName());
                topLocality.cityId=(projectBuilderCollection.getTopLocalityList().get(i).getCityId());
                topLocality.rank=(projectBuilderCollection.getTopLocalityList().get(i).getRank());
                topLocalityModelsarray.add(topLocality);
            }

            /*if(projectBuilderCollection.getTopLocalityList().size()>=5)
            {
                for(int i=0; i<size; i++)
                {
                    TopLocalityModel topLocality=new TopLocalityModel();
                    topLocality.cityName=(projectBuilderCollection.getTopLocalityList().get(i).getCityName());
                    topLocality.cityId=(projectBuilderCollection.getTopLocalityList().get(i).getId());
                    topLocality.name=(projectBuilderCollection.getTopLocalityList().get(i).getName());
                    topLocality.cityId=(projectBuilderCollection.getTopLocalityList().get(i).getCityId());
                    topLocality.rank=(projectBuilderCollection.getTopLocalityList().get(i).getRank());
                    topLocalityModelsarray.add(topLocality);
                }
            }
            else  if(projectBuilderCollection.getTopLocalityList().size()<5)
            {
                for(int i=0; i<projectBuilderCollection.getTopLocalityList().size(); i++)
                {
                    TopLocalityModel topLocality=new TopLocalityModel();
                    topLocality.cityName=(projectBuilderCollection.getTopLocalityList().get(i).getCityName());
                    topLocality.cityId=(projectBuilderCollection.getTopLocalityList().get(i).getId());
                    topLocality.name=(projectBuilderCollection.getTopLocalityList().get(i).getName());
                    topLocality.cityId=(projectBuilderCollection.getTopLocalityList().get(i).getCityId());
                    topLocality.rank=(projectBuilderCollection.getTopLocalityList().get(i).getRank());
                    topLocalityModelsarray.add(topLocality);
                }
            }*/


            if(topLocalityModelsarray != null && topLocalityModelsarray.size() > 0) {
                UtilityMethods.clearPreference(this,Constants.AppConstants.Top_Locality_LIST);
                Collections.sort(topLocalityModelsarray, new Comparator<TopLocalityModel>()
                {
                    @Override
                    public int compare(TopLocalityModel lhs, TopLocalityModel rhs) {

                        return Integer.valueOf(rhs.rank).compareTo(lhs.rank);
                    }
                });
                UtilityMethods.saveTopLocalityList(this, topLocalityModelsarray);
            }
        }
        else
        {
            UtilityMethods.clearPreference(this,Constants.AppConstants.Top_Locality_LIST);
        }



//        List<FooterLinkListResponse> projectList = projectBuilderCollection.getTopProjectList();
    }

    @Override
    public void onErrorPopularProject(String errorMsg) {
        Log.i(TAG, "onErrorPopularProject: "+errorMsg);
        new EventAnalyticsHelper().logAPICallEvent(HomeScreen.this, Constants.AnaylticsClassName.HomeScreen,
                null, Constants.ApiName.getFooterLink.toString(),Constants.AnalyticsEvents.FAILED,errorMsg);

    }

    /**
     * interface(BuilderNProjectDataResuleCallback) method called onSuccess of getProjectBuilderListCityWise API
     * @param projectCollection - response from getProjectBuilderListCityWise API
     */
    /*@Override
    public void onSuccessProjectBuilderListAPI(ProjectCollection projectCollection) {
        List<Project> projectList = projectCollection.getItems();
        int size = projectList.size();
        ArrayList<ProjectBuilderWrapper> projectBuilderListWrapper = new ArrayList<>();
        for(int i=0; i<size; i++){
            Project project = projectList.get(i);
            ProjectBuilderWrapper projectBuilderWrapper = new ProjectBuilderWrapper();
            projectBuilderWrapper.projectName = project.getName();
            projectBuilderWrapper.address = project.getAddress();
            projectBuilderWrapper.builderName = project.getBuilderName();
            projectBuilderWrapper.projectId = project.getId();
            projectBuilderWrapper.builderId = project.getBuilderId();
            projectBuilderWrapper.cityId = project.getCityId();
            projectBuilderWrapper.cityName = project.getCity();
            projectBuilderListWrapper.add(projectBuilderWrapper);
        }

        if(projectBuilderListWrapper != null && projectBuilderListWrapper.size() > 0) {
            UtilityMethods.clearPreference(this,Constants.AppConstants.PROJECT_AND_BUILDER_LIST);
            UtilityMethods.saveBuilderAndProjectList(this, projectBuilderListWrapper);
        }
    }

    *//**
     * interface(BuilderNProjectDataResuleCallback) method called OnError of getProjectBuilderListCityWise API
     * @param errMsg - error message response of getProjectBuilderListCityWise API
     *//*
    @Override
    public void onErrorProjectBuilderListAPI(String errMsg) {
        Log.i(TAG, "onErrorProjectBuilderListAPI: " + errMsg);
    }
*/
    /**
     * OnActivityResult callback of activity
     * @param requestCode - request code which we have provided while starting an activity
     * @param resultCode - result code of an activity
     * @param data - resultant data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment1 = getFragment(mSelectedFragment);
        fragment1.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == RESULT_CHANGE_CITY && resultCode == RESULT_OK) {
            Log.d(TAG, "city changed!");
            /*if(mView.findViewById(R.id.nested_scroll_home) != null) {
                mView.findViewById(R.id.nested_scroll_home).scrollTo(0, 0);
            }*/
            handleOnActivityResult(RESULT_CHANGE_CITY,data);
        }else if(requestCode == RESULT_PROPERTY_SEARCH && resultCode == RESULT_OK){

            if(data.hasExtra(Constants.IntentKeyConstants.RESULT_CHANGE_CITY)
                    && data.getBooleanExtra(Constants.IntentKeyConstants.RESULT_CHANGE_CITY,false))
            {
                handleOnActivityResult(RESULT_CHANGE_CITY,data);
            }
            else
            {
                handleOnActivityResult(RESULT_PROPERTY_SEARCH,data);
            }

        }else if(requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK){
            handleOnActivityResult(FILTER_REQUEST_CODE,data);
        }
        else if(requestCode == Constants.ActivityRequestCode.SAVE_SEARCH_RESULT_CODE && resultCode == RESULT_OK){
            tabLayout.getTabAt(HOME).select();
            Log.i(TAG, "onActivityResult: save search activity result");
            if(data != null){
                if(data.hasExtra(Constants.SavedSearchConstants.CITY_ID)) {
                    selectedCityId= data.getLongExtra(Constants.SavedSearchConstants.CITY_ID,-1);
                }
                if(data.hasExtra(Constants.SavedSearchConstants.CITY_NAME)) {
                    selectedCityName = data.getStringExtra(Constants.SavedSearchConstants.CITY_NAME);
                }
                if(data.hasExtra(Constants.SavedSearchConstants.IS_SAVE_SEARCH)
                        && data.getBooleanExtra(Constants.SavedSearchConstants.IS_SAVE_SEARCH,false)){
                    if(data.hasExtra(Constants.SavedSearchConstants.SEARCH_NAME) &&
                            !TextUtils.isEmpty(data.getStringExtra(Constants.SavedSearchConstants.SEARCH_NAME))){
                        saveSearchName = data.getStringExtra(Constants.SavedSearchConstants.SEARCH_NAME);
                    }
                    if (data.hasExtra(Constants.SavedSearchConstants.SEARCH_WRAPPER)) {
                        Gson gson = new Gson();
                        String json = data.getStringExtra(Constants.SavedSearchConstants.SEARCH_WRAPPER);
                        if(!TextUtils.isEmpty(json)) {
                            Type type = new TypeToken<SaveSearchWrapper>() {
                            }.getType();
                            saveSearchWrapper = gson.fromJson(json, type);

                            if (saveSearchWrapper != null && (saveSearchWrapper.latLongList != null)
                                    && (!saveSearchWrapper.latLongList.isEmpty())) {
                                ArrayList<LatLng> latLngArrayList = new ArrayList<>();
                                for(int i=0; i<saveSearchWrapper.latLongList.size(); i++){
                                    LatLong latLong = saveSearchWrapper.latLongList.get(i);
                                    LatLng latLngGoogle = new LatLng(latLong.getLatitude(),latLong.getLongitude());
                                    latLngArrayList.add(latLngGoogle);
                                }
                                ((HousingApplication)getApplicationContext()).setPolygonLatLngList(latLngArrayList);
                            }
                            if(saveSearchWrapper != null && saveSearchWrapper.filterApplied){
                                setSaveSearchFilter(true);
                            }else{
                                setSaveSearchFilter(false);
                            }
                        }
                    }
                    if(data.hasExtra(Constants.SavedSearchConstants.ADDRESS)){
                        searchAddress = data.getStringExtra(Constants.SavedSearchConstants.ADDRESS);
                    }
                    if(data.hasExtra(Constants.SavedSearchConstants.ADDITIONAL_ADDRESS)
                            && !data.getStringExtra(Constants.SavedSearchConstants.ADDITIONAL_ADDRESS).equalsIgnoreCase(getResources().getString(R.string.no_filter_applied))){
                        searchAdditionAddress = data.getStringExtra(Constants.SavedSearchConstants.ADDITIONAL_ADDRESS);
                    }
                    if(data.hasExtra(Constants.SavedSearchConstants.SEARCH_PROPERTY_BY_CITY)
                            && data.getBooleanExtra(Constants.SavedSearchConstants.SEARCH_PROPERTY_BY_CITY,false)){
                        UtilityMethods.saveLongInPref(this,Constants.AppConstants.SAVED_CITY_ID,selectedCityId);
                        UtilityMethods.saveStringInPref(this,Constants.AppConstants.SAVED_CITY,selectedCityName);
                        Fragment fragment = getFragment(mSelectedFragment);
                        if(fragment instanceof ProjectListFragment){
                            ProjectListFragment projectListFragment = (ProjectListFragment) fragment;
                            projectListFragment.getProjectListByCity();
                        }else if(fragment instanceof MapFragment){
                            MapFragment mapFragment = (MapFragment) fragment;
                            mapFragment.getProjectListByCity();
                        }
                    }
                    else if(data.hasExtra(Constants.SavedSearchConstants.BUILDER_ID)
                            && data.hasExtra(Constants.SavedSearchConstants.BUILDER_NAME)){
                        searchByBuilderId = data.getLongExtra(Constants.SavedSearchConstants.BUILDER_ID,-1);
                        searchByBuilderName = data.getStringExtra(Constants.SavedSearchConstants.BUILDER_NAME);
                        getProjectByDeveloper(searchByBuilderId,searchByBuilderName);
                    }else if(data.hasExtra(Constants.SavedSearchConstants.LATITUDE)
                            && data.hasExtra(Constants.SavedSearchConstants.LONGITUDE)
                            && !data.getStringExtra(Constants.SavedSearchConstants.LATITUDE).isEmpty()
                            && !data.getStringExtra(Constants.SavedSearchConstants.LONGITUDE).isEmpty()){
                        double latitude = Double.parseDouble(data.getStringExtra(Constants.SavedSearchConstants.LATITUDE));
                        double longitude = Double.parseDouble(data.getStringExtra(Constants.SavedSearchConstants.LONGITUDE));
                        getProjectByLocation(latitude,longitude);
                    }else{
                        Fragment fragment = getFragment(mSelectedFragment);
                        if(fragment instanceof ProjectListFragment){
                            ProjectListFragment projectListFragment = (ProjectListFragment) fragment;
                            projectListFragment.getProjectListByCity();
                        }else if(fragment instanceof MapFragment){
                            MapFragment mapFragment = (MapFragment) fragment;
                            mapFragment.getProjectListByCity();
                        }
                    }
                }

                setSearchTextView();
                setCityName();
            }
        }
    }

    /**
     * Method to handle the onActivityResult method
     * @param requestCode
     * @param intent
     */
    private void handleOnActivityResult(int requestCode, Intent intent){
        switchBtn.setVisibility(View.VISIBLE);
        if(requestCode == RESULT_CHANGE_CITY){
            if(!isInternetConnected()){
                return;
            }
            selectedCityId = UtilityMethods.getLongInPref(this,Constants.AppConstants.SAVED_CITY_ID,-1);
            selectedCityName = UtilityMethods.getStringInPref(this,Constants.AppConstants.SAVED_CITY,"");
            searchByCityId =  selectedCityId;
            searchByCityName = selectedCityName;
            searchByLocation = null;
            resetOnCityChange();
            setCityName();
            Fragment fragment = getFragment(mSelectedFragment);
            if(fragment instanceof ProjectListFragment){
                ProjectListFragment projectListFragment = (ProjectListFragment) fragment;
                projectListFragment.getProjectListByCity();
            }else if(fragment instanceof MapFragment){
                MapFragment mapFragment = (MapFragment) fragment;
                mapFragment.getProjectListByCity();
            }
        }
        else if(requestCode == RESULT_PROPERTY_SEARCH){
            saveSearchName = "";
            searchByBuilderName = "";
            if(!isInternetConnected()){
                return;
            }
            if(intent != null){
                if(intent.hasExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION)
                        && intent.getBooleanExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION,false)){
                    double latitude = -1, longitude = -1;
                    if(intent.hasExtra("lat") && !TextUtils.isEmpty(intent.getStringExtra("lat"))){
                        latitude = Double.parseDouble(intent.getStringExtra("lat"));
                    }
                    if(intent.hasExtra("lng") && !TextUtils.isEmpty(intent.getStringExtra("lng"))){
                        longitude = Double.parseDouble(intent.getStringExtra("lng"));
                    }
                    if(intent.hasExtra("cityName") && !TextUtils.isEmpty(intent.getStringExtra("cityName"))){
                        searchByCityName = intent.getStringExtra("cityName");
                        Log.i(TAG, "handleOnActivityResult: cityName " + searchByCityName);
                    }
                    if(intent.hasExtra("city_id")){
                        // is user select any recent search of another city then we need to reset the polygon
                        if(searchByCityId != intent.getLongExtra("city_id",0)){
                            isPolygonDrawn = false;
                            //MapFragment mapFragment = (MapFragment) getFragment(MAP);
                            //mapFragment.onCityChange();
                        }
                        searchByCityId = intent.getLongExtra("city_id",0);
                        Log.i(TAG, "handleOnActivityResult: cityId " + searchByCityId);
                    }
                    if(intent.hasExtra("address")){
                        searchAddress = intent.getStringExtra("address");
                    }
                    if(intent.hasExtra("additional_address")){
                        searchAdditionAddress = intent.getStringExtra("additional_address");
                    }
                    if(searchByCityId != 0){
                        selectedCityName = searchByCityName;
                        selectedCityId = searchByCityId;
                        UtilityMethods.saveLongInPref(this,Constants.AppConstants.SAVED_CITY_ID,searchByCityId);
                        UtilityMethods.saveStringInPref(this,Constants.AppConstants.SAVED_CITY,searchByCityName);
                        //setCityName();
                    }
                    searchLocation = new LatLng(latitude,longitude);
                    Log.d("Locations ","Lat:"+latitude+" long"+longitude);
                    searchByLocation = searchLocation;
                    getProjectByLocation(latitude,longitude);
                }
                else if(intent.hasExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_DEVELOPER)
                        && intent.getBooleanExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_DEVELOPER,false)){

                    if(intent.hasExtra("builder_id") && intent.getLongExtra("builder_id",0) != 0){
                        searchByBuilderId = intent.getLongExtra("builder_id",0);
                    }
                    if(intent.hasExtra("builder_name") && !TextUtils.isEmpty(intent.getStringExtra("builder_name"))){
                        searchByBuilderName = intent.getStringExtra("builder_name");
                    }
                    if(intent.hasExtra("address")){
                        searchAddress = intent.getStringExtra("address");
                    }
                    if(intent.hasExtra("additional_address")){
                        searchAdditionAddress = intent.getStringExtra("additional_address");
                    }
                    if(intent.hasExtra("cityName") && !TextUtils.isEmpty(intent.getStringExtra("cityName"))){
                        selectedCityName = intent.getStringExtra("cityName");
                        Log.i(TAG, "handleOnActivityResult: cityName " + selectedCityName);
                    }
                    if(intent.hasExtra("city_id")){
                        // is user select any recent search of another city then we need to reset the polygon
                        if(searchByCityId != intent.getLongExtra("city_id",0)){
                            isPolygonDrawn = false;
                            //MapFragment mapFragment = (MapFragment) getFragment(MAP);
                            //mapFragment.onCityChange();
                        }
                        selectedCityId = intent.getLongExtra("city_id",0);
                        Log.i(TAG, "handleOnActivityResult: cityId " + selectedCityId);
                    }
                    if(selectedCityId != 0){
                        UtilityMethods.saveLongInPref(this,Constants.AppConstants.SAVED_CITY_ID,selectedCityId);
                        UtilityMethods.saveStringInPref(this,Constants.AppConstants.SAVED_CITY,selectedCityName);
                    }
                    searchByLocation = null;
                    getProjectByDeveloper(searchByBuilderId,searchByBuilderName);
                }
                setSearchTextView();
                setCityName();
                saveBuilderAndProjectList();
            }
        }
    }

    /**
     * Method which reset parameters and variable etc while user change city
     */
    private void resetOnCityChange(){
        TextView searchTextView = findViewById(R.id.search_title);
        ImageView clearBtn = findViewById(R.id.id_search_clear_btn);
        searchAdditionAddress="";
        searchAddress="";
        searchByBuilderId=0;
        searchByBuilderName="";
        searchTextView.setText("");
        saveSearchName = "";
        clearBtn.setVisibility(View.GONE);
        isPolygonDrawn = false;
        searchType = "";
        //MapFragment mapFragment = (MapFragment) getFragment(MAP);
        //mapFragment.onCityChange();
    }

    /**
     * Method of IDrawMapBound interface which gets called while user draw polygon on map
     */
    @Override
    public void drawMapTask() {
        Log.i(TAG, "drawMapTask: ");
        /*MapFragment mapFragment = (MapFragment) getFragment(MAP);
        if(mapFragment instanceof MapFragment){
            ((MapFragment) mapFragment).drawMapTask();
        }*/
    }


    /**
     * Method which sets the Save search filter
     * @param isFilterApplied - boolean flag which indicate weather filter is applied or not
     */
    private void setSaveSearchFilter(boolean isFilterApplied){
        if(isFilterApplied) {
            UtilityMethods.saveBooleanInPref(this, Constants.SharedPreferConstants.PARAM_FILTER_APPLIED, true);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_APARTMENT, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_VILLA, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_PLOTS, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_COMMERCIAL, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_FLOOR, false);
            if (saveSearchWrapper.propertyTypeEnum != null) {
                if (saveSearchWrapper.propertyTypeEnum.contains(Constants.AppConstants.PropertyType.Apartment.toString())) {
                    UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_APARTMENT, true);
                }

                if (saveSearchWrapper.propertyTypeEnum.contains(Constants.AppConstants.PropertyType.IndependentHouseVilla.toString())) {
                    UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_VILLA, true);
                }

                if (saveSearchWrapper.propertyTypeEnum.contains(Constants.AppConstants.PropertyType.Land.toString())) {
                    UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_PLOTS, true);
                }

                if (saveSearchWrapper.propertyTypeEnum.contains(Constants.AppConstants.PropertyType.Shop.toString())) {
                    UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_COMMERCIAL, true);
                }

                if (saveSearchWrapper.propertyTypeEnum.contains(Constants.AppConstants.PropertyType.IndependentFloor.toString())) {
                    UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_FLOOR, true);
                }
            }

            UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_ROOM, 0);
            if ((saveSearchWrapper.bedList != null) && (!saveSearchWrapper.bedList.isEmpty())) {
                int index = 0;
                for (int i = 0; i < saveSearchWrapper.bedList.size(); i++) {
                    if (saveSearchWrapper.bedList.get(i) == 1) {
                        index = index | 1;
                    } else if (saveSearchWrapper.bedList.get(i) == 2) {
                        index = index | 2;
                    } else if (saveSearchWrapper.bedList.get(i) == 3) {
                        index = index | 4;
                    } else {
                        index = index | 8;
                    }
                }
                UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_ROOM, index);
            }

            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_NEW, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_ONGOING, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_READY, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_UPCOMING, false);
            if ((saveSearchWrapper.propertyStatusList != null) && (!saveSearchWrapper.propertyStatusList.isEmpty())) {
                if (saveSearchWrapper.propertyStatusList.contains(Constants.AppConstants.PropertyStatus.NotStarted.toString())) {
                    UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_NEW, true);
                }
                if (saveSearchWrapper.propertyStatusList.contains(Constants.AppConstants.PropertyStatus.InProgress.toString())) {
                    UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_ONGOING, true);
                }
                if (saveSearchWrapper.propertyStatusList.contains(Constants.AppConstants.PropertyStatus.ReadyToMove.toString())) {
                    UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_READY, true);
                }
                if (saveSearchWrapper.propertyStatusList.contains(Constants.AppConstants.PropertyStatus.UpComing.toString())) {
                    UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_UPCOMING, true);
                }
            }

            UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MIN_PRICE, 0);
            UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MAX_PRICE,
                    getResources().getStringArray(R.array.price_array).length - 1);

            if (saveSearchWrapper.maxCost != 0) {
                int[] valuePriceArray = getResources().getIntArray(R.array.price_array_value);

                for (int i = 0; i < (valuePriceArray.length - 1); i++) {
                    if (saveSearchWrapper.maxCost == valuePriceArray[i]) {
                        UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MAX_PRICE, i);
                        break;
                    }
                }
            }
            if (saveSearchWrapper.minCost != 0) {
                int[] valuePriceArray = getResources().getIntArray(R.array.price_array_value);

                for (int i = 0; i < (valuePriceArray.length - 1); i++) {
                    if (saveSearchWrapper.minCost == valuePriceArray[i]) {
                        UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MIN_PRICE, i);
                        break;
                    }
                }
            }
        }else{
            UtilityMethods.saveBooleanInPref(this, Constants.SharedPreferConstants.PARAM_FILTER_APPLIED, false);

            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_APARTMENT, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_VILLA, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_PLOTS, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_COMMERCIAL, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_FLOOR, false);

            UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_ROOM, 0);

            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_NEW, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_ONGOING, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_READY, false);
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_UPCOMING, false);

            UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MIN_PRICE, 0);
            UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MAX_PRICE,
                    getResources().getStringArray(R.array.price_array).length - 1);
        }
    }

    public boolean isPolygonDrawn() {
        return isPolygonDrawn;
    }

    /**
     * Method to set the variable that polygon has been drawn
     * @param polygonDrawn : boolean variable to set polygon draw status
     */
    public void setPolygonDrawn(boolean polygonDrawn) {
        isPolygonDrawn = polygonDrawn;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((HousingApplication)getApplicationContext()).setPolygonLatLngList(null);
    }


    public void onClickLogout() {
//        TrackAnalytics.trackEvent(HomeScreen.this.getResources().getString(R.string.ga_button_category), "Sign Out Clicked ", this);
        AppEventAnalytics.clearAnalyticsProperty(HomeScreen.this);
        removePrivateNotifications();
        UtilityMethods.removeFromPref(HomeScreen.this, Constants.ServerConstants.USER_ID);
        UtilityMethods.removeFromPref(HomeScreen.this, Constants.ServerConstants.NAME);
        UtilityMethods.removeFromPref(HomeScreen.this, Constants.ServerConstants.EMAIL);
        UtilityMethods.removeFromPref(HomeScreen.this, Constants.ServerConstants.MOBILE);
        UtilityMethods.removeFromPref(HomeScreen.this, Constants.AppConstants.EMAIL_PREF_KEY);
        SystemConfig.getInstanceSystemConfig(HomeScreen.this).resetConfig();
        UtilityMethods.removeFromPref(HomeScreen.this, Constants.SharedPreferConstants.CONCEIRGE_PHOTO_URL);
        UtilityMethods.removeFromPref(HomeScreen.this, Constants.SharedPreferConstants.CONCEIRGE_NAME);
        UtilityMethods.removeFromPref(HomeScreen.this, Constants.AppConstants.IMAGE_URL);
        UtilityMethods.removeFromPref(HomeScreen.this, Constants.SharedPreferConstants.IS_NOTIFICATION_SEEN);
//        UtilityMethods.removeFromPref(HomeScreen.this, Constants.AppConstants.VIRTUAL_UID);
        UtilityMethods.saveLongInPref(HomeScreen.this,Constants.SharedPreferConstants.TIME_STAMP,System.currentTimeMillis());
        //gcmRegistration.resetRegistrationId();
        UtilityMethods.clearUserProfile(HomeScreen.this);
        if (UtilityMethods.favoriteIDs != null) {
            UtilityMethods.favoriteIDs.clear();
        }
        UtilityMethods.clearPreference(HomeScreen.this,Constants.SharedPreferConstants.RECENT_PROJECT_LIST);
        UtilityMethods.clearPreference(HomeScreen.this,"recent_search");

        //Clear favorites!
        UtilityMethods.clearPreference(HomeScreen.this, Constants.AppConstants.PROJECT_ID_SET);

        File profilePicFile = new File(getFilesDir() + File.separator + Constants.AppConstants.PROFILE_PIC);
        if (profilePicFile.exists()) {
            profilePicFile.delete();
        }
        onClickSearchClearBtn(null);
        tabLayout.getTabAt(HOME).select();
    }

    private void removePrivateNotifications() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.NOTIFICATIONS, "");
        Type type = new TypeToken<List<Notificaiton>>() {
        }.getType();

        List<Notificaiton> notificationList = gson.fromJson(json, type);
        UtilityMethods.clearPreference(HomeScreen.this, Constants.AppConstants.NOTIFICATIONS);

        List<Notificaiton> publicNotifications = new ArrayList<>();
        if (notificationList == null || notificationList.isEmpty()) {
            return;
        } else {
            for (Notificaiton toRemove : notificationList) {
                Log.d(TAG,"startTime->"+toRemove.getStartTime());
                if ((toRemove.getStartTime() != null) && (toRemove.getStartTime() != 0)) {
                    publicNotifications.add(toRemove);
                }
            }
        }

        Log.d(TAG,"notificationList->"+notificationList.size()+"\n public->"+publicNotifications.size());

        SharedPreferences.Editor prefsEditor = prefs.edit();
        gson = new Gson();
        json = gson.toJson(publicNotifications);
        prefsEditor.putString(Constants.AppConstants.NOTIFICATIONS, json);
        prefsEditor.apply();
    }


    /*public void handleActionBar(int visibility){
        findViewById(R.id.id_home_screen_appbar).setVisibility(visibility);
    }*/



    /**
     * Interface(IPropertyOperationsListener) method which gets called while we apply Save Search on project List or map screen
     * This method will set the search by city id , name and also set the save search parameters
     * This method will ask popup to select the search name, while user give the save search name, save search will be saved by
     * given name.
     */
    @Override
    public void onSaveSearchApplied() {

        new EventAnalyticsHelper().ItemClickEvent(HomeScreen.this, Constants.AnaylticsClassName.FilterScreen,
                null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.applyFilter.toString());

        SaveSearchUtility saveSearchUtility = new SaveSearchUtility(this);
        searchByCityId = selectedCityId;
        searchByCityName = selectedCityName;
        saveSearchUtility.setSearchByParameter(saveSearchName,searchByCityId,searchByCityName,searchByBuilderId,searchByBuilderName,
                searchByLocation,searchAddress,searchAdditionAddress);
        saveSearchUtility.doSaveSearch();
    }

    /**
     *  This method will gets call while user search any property in either project list or map
     * @param latitude
     * @param longitude
     */
    public void onSearchApplied(double latitude, double longitude) {
        searchType = SearchBy.SearchByLocation.toString();
        this.latitude = latitude;
        this.longitude = longitude;
        Fragment fragment = getFragment(mSelectedFragment);
        if(fragment instanceof ProjectListFragment){
            ProjectListFragment projectListFragment = (ProjectListFragment) fragment;
            projectListFragment.getProjectListByDistance(latitude,longitude);
        }else if(fragment instanceof MapFragment){
            MapFragment mapFragment = (MapFragment) fragment;
            //mapFragment.callOnSearchingApplied(latitude,longitude);
            mapFragment.getProjectListByDistance(latitude,longitude);
        }
    }


    /**
     * Interface(IPropertyOperationsListener) gets called while there is no internet on device & user applied either filer or
     * sorting, we will show the no internet UI for project list screen and map screen.
     */
    @Override
    public void onNoInternetTryAgain() {
        if(!UtilityMethods.isInternetConnected(this)){
            UtilityMethods.showSnackbarOnTop(this,"Error",getString(R.string.no_network_connection),true,1500);
            return;
        }
        Fragment fragment = getFragment(mSelectedFragment);
        if(fragment instanceof ProjectListFragment){
            ProjectListFragment projectListFragment = (ProjectListFragment) fragment;
            projectListFragment.callOnNoInternetTryAgain();
        }else if(fragment instanceof MapFragment){
            MapFragment mapFragment = (MapFragment) fragment;
            mapFragment.callOnNoInternetTryAgain();
        }
    }

    /**
     * Based on the selected tab we are setting fragment
     * @param position - position of tab selected(selected fragment position)
     */
    private void setFragment(int position){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("ServerAPItype",searchType);
        if(position == HOME){
            transaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.AppConstants.PROJECT_LIST_FRAGMENT);
            ProjectListFragment projectListFragment;
            if(fragment == null) {
                projectListFragment = new ProjectListFragment();
            }else{
                projectListFragment = (ProjectListFragment) fragment;
            }
            projectListFragment.setArguments(bundle);
            transaction.replace(R.id.id_fragment,projectListFragment,Constants.AppConstants.PROJECT_LIST_FRAGMENT);
        }else if(position == MAP){
            transaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.AppConstants.MAP_FRAGMENT);
            MapFragment mapFragment;
            if(fragment == null) {
                mapFragment = new MapFragment();
            }else{
                mapFragment = (MapFragment) fragment;
            }
            mapFragment.setArguments(bundle);
            transaction.replace(R.id.id_fragment,mapFragment,Constants.AppConstants.MAP_FRAGMENT);
        }else if(position == HOME_DECOR){
            transaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
            HomeDecorFragment homeDecorFragment = new HomeDecorFragment();
            transaction.replace(R.id.id_fragment,homeDecorFragment,Constants.AppConstants.HOME_DECOR_IDEAS_FRAGMENT);
        }else if(position == NEWS){
            transaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
            NewsFragment newsFragment = new NewsFragment();
            transaction.replace(R.id.id_fragment,newsFragment,Constants.AppConstants.NEWS_FRAGMENT);
        }else if(position == CLICWORTH){
            transaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
            ClicworthFragment clicworthFragment = new ClicworthFragment();
            transaction.replace(R.id.id_fragment,clicworthFragment,Constants.AppConstants.CLICWORTH_FRAGMENT);
        }else if(position == MORE){
            transaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
            MoreFragment moreFragment = new MoreFragment();
            transaction.replace(R.id.id_fragment,moreFragment,Constants.AppConstants.MORE_FRAGMENT);
        }
        transaction.commit();
    }

    /**
     * Get the selected fragment based on the position
     * @param position - selected fragment position
     * @return - selected fragment
     */
    private Fragment getFragment(int position){
        if(position == HOME) {
            return getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.PROJECT_LIST_FRAGMENT);
        }else if(position == MAP){
            return getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.MAP_FRAGMENT);
        }else if(position == HOME_DECOR){
            return getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.HOME_DECOR_IDEAS_FRAGMENT);
        }else if(position == NEWS){
            return getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.NEWS_FRAGMENT);
        }else if(position == CLICWORTH){
            return getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.CLICWORTH_FRAGMENT);
        }else if(position == MORE){
            return getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.MORE_FRAGMENT);
        }
        return getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.PROJECT_LIST_FRAGMENT);
    }

    /**
     * This method gets called while user move the map i.e user changed the current location in map
     * based on the user moved location on map and if the distance is equal or greater than the given distance,
     * we fetch the project list and same project list we get for list also because user changed the location
     * @param searchType - search type
     * @param latitude - latitude
     * @param longitude - longitude
     */
    public void setMapMovedLocation(String searchType,double latitude, double longitude){
        this.searchType = searchType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Method to set the project list
     * @param projectList - list of project
     */
    public void setProjectList(List<com.clicbrics.consumer.model.Project> projectList){
        this.projectList = projectList;
    }

    /**
     * To get the project list which will be required for project list and map fragment
     * @return
     */
    public List<com.clicbrics.consumer.model.Project> getProjectList(){
        return projectList;
    }

    public long getDeveloperId(){
        return developerId;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    /*long onStartTime;
    @Override
    protected void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.HomeScreen);

    }*/
}
