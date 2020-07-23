package com.clicbrics.consumer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.FooterLinkListResponse;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.RecentSearchAdapter;
import com.clicbrics.consumer.adapter.SearchByNameAdapter;
import com.clicbrics.consumer.adapter.TopLocalityAdapter;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.clicworth.GetEstimateActivity;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.helper.PopularProjectCallback;
import com.clicbrics.consumer.model.ProjectsByname;
import com.clicbrics.consumer.retrofit.pojoclass.SearchByName;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.viewmodel.ProjectListViewModel;
import com.clicbrics.consumer.viewmodel.SearchActivityCallbacks;
import com.clicbrics.consumer.wrapper.RecentSearch;
import com.clicbrics.consumer.wrapper.TopLocalityModel;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.Places;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;

public class SearchActivity extends BaseActivity implements SearchActivityCallbacks, PopularProjectCallback {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private Context mContext;
    private RecyclerView mRecyclerView, recentProject, topLocalityrecyle;
    private EditText mSearchEdittext;
    private SearchActivityViewModel searchActivityViewModel;
    private long mLastRequestTime;
    public ArrayList<RecentSearch> recentSearchList;
    private SearchByNameAdapter adapter;
    private TopLocalityAdapter topLocalityAdapter;
    private RecentSearchAdapter recentSearchAdapter;
    private NestedScrollView nestedscroll;
    private TextView recent_textView, toplocality_tv, search_result_zero, searchincitytext;
    private ArrayList<TopLocalityModel> topLocalityModels;
    private GeoDataClient geoDataClient;
    private ProgressBar progressBar2;
    private LinearLayout liner_search;
    private long sequence;
    private static final int RESULT_CHANGE_CITY = 102;
    private ImageView back, id_search_clear_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchActivityViewModel = new SearchActivityViewModel(this);
        setContentView(R.layout.activity_search);
        UtilityMethods.setStatusBarColor(this, R.color.white);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) toolbar.findViewById(R.id.back);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
//        addBackButton();
        geoDataClient = Places.getGeoDataClient(this);
        initViews();

    }

    private void initViews() {
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_search);
        LinearLayoutManager verticalLayoutManagaer
                = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(verticalLayoutManagaer);
        nestedscroll = (NestedScrollView) findViewById(R.id.nestedscroll);
        recentProject = (RecyclerView) findViewById(R.id.recentProject);
        recent_textView = (TextView) findViewById(R.id.recent_textView);
        search_result_zero = (TextView) findViewById(R.id.search_result_zero);
        searchincitytext = (TextView) findViewById(R.id.searchincitytext);
        liner_search = (LinearLayout) findViewById(R.id.liner_search);
        id_search_clear_btn = (ImageView) findViewById(R.id.id_search_clear_btn);
        if (!TextUtils.isEmpty(UtilityMethods.getStringInPref(SearchActivity.this, Constants.AppConstants.SAVED_CITY, ""))) {
            searchincitytext.setVisibility(View.VISIBLE);
            searchincitytext.setText(getResources().getString(R.string.search_in_city) + " " +
                    UtilityMethods.getStringInPref(SearchActivity.this, Constants.AppConstants.SAVED_CITY, ""));
        }


//        change = (TextView) findViewById(R.id.change);
        LinearLayoutManager recentsearchLayoutManagaer
                = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recentProject.setLayoutManager(recentsearchLayoutManagaer);
        topLocalityrecyle = (RecyclerView) findViewById(R.id.topLocalityrecyle);
        toplocality_tv = (TextView) findViewById(R.id.toplocality_tv);
        LinearLayoutManager topLocalityLayoutManagaer
                = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        topLocalityrecyle.setLayoutManager(topLocalityLayoutManagaer);

        setToplocality();

        if (getRecentlyViewed() != null && getRecentlyViewed().size() > 0) {
            recentSearchList = getRecentlyViewed();
            recent_textView.setVisibility(View.VISIBLE);
            recentProject.setVisibility(View.VISIBLE);
            recentSearchAdapter = new RecentSearchAdapter(SearchActivity.this, recentSearchList);
            recentProject.setAdapter(recentSearchAdapter);
        } else {
            recent_textView.setVisibility(View.GONE);
            recentProject.setVisibility(View.GONE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCityChanged) {
                    isCityChanged = false;
                    Intent intent = new Intent();
                    intent.putExtra(Constants.IntentKeyConstants.RESULT_CHANGE_CITY, true);
                    setResult(RESULT_OK, intent);
                    finish();

                } else {
                    finish();
                }
            }
        });
        mSearchEdittext = (EditText) findViewById(R.id.search_edit);
        mSearchEdittext.setHint("Search by Locality, Project or Developer");
        final TextView zero_results = (TextView) findViewById(R.id.search_result_zero);
        mSearchEdittext.setHint("Search by Locality, Project or Developer");
        Log.i(TAG, "initViews: ===" + System.currentTimeMillis());
        UtilityMethods.hideKeyboard(SearchActivity.this);
        mSearchEdittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "Search editText, onTextChanged -> " + s);
                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    nestedscroll.setVisibility(View.GONE);
                    id_search_clear_btn.setVisibility(View.VISIBLE);
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    long cityId = UtilityMethods.getLongInPref(SearchActivity.this, Constants.AppConstants.SAVED_CITY_ID, -1);
                    String state = UtilityMethods.getStringInPref(SearchActivity.this, Constants.AppConstants.SAVED_STATE, "");
                    String city = UtilityMethods.getStringInPref(SearchActivity.this, Constants.AppConstants.SAVED_CITY, "");
                    sequence = System.currentTimeMillis();
                    Log.i(TAG, "afterTextChanged: onSuccessSearchByName: api " + UtilityMethods.getDate(sequence, "dd-MM-yyyy HH:mm:ss SSS a"));
                    searchActivityViewModel.GetSearchByName(s.toString(), cityId, city + " " + state + " " + s.toString(), sequence);

                    mLastRequestTime = System.currentTimeMillis();
                } else {

                    nestedscroll.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    search_result_zero.setVisibility(View.GONE);
                    id_search_clear_btn.setVisibility(View.INVISIBLE);


                }
            }

        });
        id_search_clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEdittext.setText("");
                nestedscroll.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                search_result_zero.setVisibility(View.GONE);
                id_search_clear_btn.setVisibility(View.INVISIBLE);
            }
        });
        liner_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetConnected()) {
                    return;
                }
                Intent intent = new Intent(SearchActivity.this, PickCity.class);
                startActivityForResult(intent, RESULT_CHANGE_CITY);
            }
        });

    }


    private void setToplocality() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (UtilityMethods.getTopLocalitytList(SearchActivity.this) != null && UtilityMethods.getTopLocalitytList(SearchActivity.this).size() > 0) {
                    nestedscroll.setVisibility(View.VISIBLE);
                    topLocalityrecyle.setVisibility(View.VISIBLE);
                    toplocality_tv.setVisibility(View.VISIBLE);
                    topLocalityModels = UtilityMethods.getTopLocalitytList(SearchActivity.this);
                    int size = topLocalityModels.size();
                    if (size > 5) {
                        size = 5;   //we need to show only 5 element from the list
                    }
                    topLocalityAdapter = new TopLocalityAdapter(SearchActivity.this, topLocalityModels,size, geoDataClient);
                    topLocalityrecyle.setAdapter(topLocalityAdapter);
                } else {
                    topLocalityrecyle.setVisibility(View.GONE);
                    toplocality_tv.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recentSearchList != null && recentSearchList.size() > 0) {
            if (recentSearchList.size() > 0) {
//                mAdapter.showRecentSearch(recentSearchList);
            }
        }
    }

    @Override
    public boolean isInternetConnected() {
        if (UtilityMethods.isInternetConnected(SearchActivity.this)) {
            return true;
        } else {
//            isLoading = false;
            progressBar2.setVisibility(View.GONE);
            UtilityMethods.showSnackbarOnTop(SearchActivity.this, "Error", getString(R.string.no_network_connection), true, 1500);
            return false;
        }

    }

    @Override
    public void onSuccessPopularProject(FooterLinkListResponse projectBuilderCollection) {

        if (projectBuilderCollection.getTopLocalityList() != null && projectBuilderCollection.getTopLocalityList().size() > 0) {
            new EventAnalyticsHelper().logAPICallEvent(SearchActivity.this, Constants.AnaylticsClassName.SearchScreen,
                    null, Constants.ApiName.getFooterLink.toString(),Constants.AnalyticsEvents.SUCCESS,null);
            ArrayList<TopLocalityModel> topLocalityModelsarray = new ArrayList<>();
//            Log.i(TAG, "onSuccessPopularProject: ======"+projectBuilderCollection.getTopLocalityList().toString());
            /*Log.i(TAG, "onSuccessPopularProject: ======"+projectBuilderCollection.getTopLocalityList().toString);
            Log.i(TAG, "onSuccessPopularProject: ======"+projectBuilderCollection.getTopLocalityList().get(0).getCityId());
            Log.i(TAG, "onSuccessPopularProject: ======"+projectBuilderCollection.getTopLocalityList().get(0).getName());*/
            int size = projectBuilderCollection.getTopLocalityList().size();
           /* if(size > 5){
                size = 5;   //we need to show only 5 element from the list
            }*/
            for (int i = 0; i < size; i++) {
                TopLocalityModel topLocality = new TopLocalityModel();
                topLocality.cityName = (projectBuilderCollection.getTopLocalityList().get(i).getCityName());
                topLocality.cityId = (projectBuilderCollection.getTopLocalityList().get(i).getId());
                topLocality.name = (projectBuilderCollection.getTopLocalityList().get(i).getName());
                topLocality.cityId = (projectBuilderCollection.getTopLocalityList().get(i).getCityId());
                topLocality.rank = (projectBuilderCollection.getTopLocalityList().get(i).getRank());
                topLocalityModelsarray.add(topLocality);
            }
            /*if (projectBuilderCollection.getTopLocalityList().size() >= 5) {
                for (int i = 0; i < 5; i++) {
                    TopLocalityModel topLocality = new TopLocalityModel();
                    topLocality.cityName = (projectBuilderCollection.getTopLocalityList().get(i).getCityName());
                    topLocality.cityId = (projectBuilderCollection.getTopLocalityList().get(i).getId());
                    topLocality.name = (projectBuilderCollection.getTopLocalityList().get(i).getName());
                    topLocality.cityId = (projectBuilderCollection.getTopLocalityList().get(i).getCityId());
                    topLocality.rank = (projectBuilderCollection.getTopLocalityList().get(i).getRank());
                    topLocalityModelsarray.add(topLocality);
                }
            } else if (projectBuilderCollection.getTopLocalityList().size() < 5) {
                for (int i = 0; i < projectBuilderCollection.getTopLocalityList().size(); i++) {
                    TopLocalityModel topLocality = new TopLocalityModel();
                    topLocality.cityName = (projectBuilderCollection.getTopLocalityList().get(i).getCityName());
                    topLocality.cityId = (projectBuilderCollection.getTopLocalityList().get(i).getId());
                    topLocality.name = (projectBuilderCollection.getTopLocalityList().get(i).getName());
                    topLocality.cityId = (projectBuilderCollection.getTopLocalityList().get(i).getCityId());
                    topLocality.rank = (projectBuilderCollection.getTopLocalityList().get(i).getRank());
                    topLocalityModelsarray.add(topLocality);
                }
            }*/


            if (topLocalityModelsarray != null && topLocalityModelsarray.size() > 0) {
                UtilityMethods.clearPreference(this, Constants.AppConstants.Top_Locality_LIST);

                Collections.sort(topLocalityModelsarray, new Comparator<TopLocalityModel>() {
                    @Override
                    public int compare(TopLocalityModel lhs, TopLocalityModel rhs) {

                        return Integer.valueOf(rhs.rank).compareTo(lhs.rank);
                    }
                });
                UtilityMethods.saveTopLocalityList(this, topLocalityModelsarray);
            }
        } else {
            UtilityMethods.clearPreference(this, Constants.AppConstants.Top_Locality_LIST);
        }

        setToplocality();
    }

    @Override
    public void onErrorPopularProject(String errorMsg) {
        topLocalityrecyle.setVisibility(View.GONE);
        toplocality_tv.setVisibility(View.GONE);
        new EventAnalyticsHelper().logAPICallEvent(SearchActivity.this, Constants.AnaylticsClassName.SearchScreen,
                null, Constants.ApiName.getFooterLink.toString(),Constants.AnalyticsEvents.FAILED,errorMsg);
//        Log.i(TAG, "onErrorPopularProject: " + errorMsg);
//        UtilityMethods.showSnackbarOnTop(SearchActivity.this, errorMsg, "", true, 1500);
    }


    @Override
    public void showLoader() {
        progressBar2.setVisibility(View.VISIBLE);
//        mSearchEdittext.setEnabled(false);

    }

    @Override
    public void hideLoader() {
        progressBar2.setVisibility(View.GONE);

    }

    @Override
    public void onErrorSearchByName(String errorMsg) {
        progressBar2.setVisibility(View.GONE);
        new EventAnalyticsHelper().logAPICallEvent(SearchActivity.this, Constants.AnaylticsClassName.SearchScreen,
                null, Constants.ApiName.searchByName.toString(),Constants.AnalyticsEvents.FAILED,errorMsg);
        UtilityMethods.showSnackbarOnTop(SearchActivity.this, errorMsg, "", true, 1500);
        Log.i(TAG, "onErrorSearchByName: ======" + errorMsg);

    }

    //    ArrayList<SearchByName> items;
    ArrayList<ProjectsByname> projectsBynames;

    @Override
    public void onSuccessSearchByName(Response<SearchByName> response, String seq) {
        Log.i(TAG, " onSuccessSearchByName: api success " + UtilityMethods.getDate(System.currentTimeMillis(), "dd-MM-yyyy HH:mm:ss SSS a"));
//        Log.i(TAG, "onSuccessSearchByName: " + searchincitytext.getText().toString());
        Log.i(TAG, "searchincitytext: " + mSearchEdittext.getText().toString());

        if (!TextUtils.isEmpty(mSearchEdittext.getText().toString())) {
            Log.i(TAG, "searchincitytext1: " + mSearchEdittext.getText().toString());
            new EventAnalyticsHelper().logAPICallEvent(SearchActivity.this, Constants.AnaylticsClassName.SearchScreen,
                    null, Constants.ApiName.searchByName.toString(),Constants.AnalyticsEvents.SUCCESS,null);
            projectsBynames = new ArrayList<>();
            projectsBynames.clear();
            if (sequence <= Long.parseLong(seq)) {
                Log.i(TAG, "onSuccessSearchByName: sequence " + sequence);
                SearchByName value = new SearchByName();
                SearchByName body = response.body();
               String city_name= UtilityMethods.getStringInPref(SearchActivity.this, Constants.AppConstants.SAVED_CITY, "");
                if (body.getDevelopers() != null && body.getDevelopers().size() > 0) {

                    //value.setDevelopers(response.body().getDevelopers());
                    for (int i = 0; i < body.getDevelopers().size(); i++) {
                        ProjectsByname values = new ProjectsByname();
                        values.setName(body.getDevelopers().get(i).getBuilderName());
                        values.setId(body.getDevelopers().get(i).getBuilderId());
                        values.setAddresss(city_name);
                        values.setType("Developer");
                        projectsBynames.add(values);
                    }
                }


                if (body.getProjects() != null && body.getProjects().size() > 0) {
                    Log.i(TAG, "onSuccessSearchByName: " + body.getProjects().size());
//                    value.setProjectList(body.getProjects());
                    for (int i = 0; i < body.getProjects().size(); i++) {
                        ProjectsByname values = new ProjectsByname();
                        values.setName(body.getProjects().get(i).getProjectName());
                        values.setId(body.getProjects().get(i).getProjectId());
                        values.setAddresss(body.getProjects().get(i).getAddress());
                        values.setType("Project");
                        projectsBynames.add(values);
                    }
                }
                if (body.getLocalities() != null && body.getLocalities().size() > 0) {
//                    value.setLocalities(body.getLocalities());
                    for (int i = 0; i < body.getLocalities().size(); i++) {
                        ProjectsByname values = new ProjectsByname();
//                        values.setName(body.getLocalities().get(i).getDescription());
                        values.setName(body.getLocalities().get(i).getName());
                        values.setId(body.getLocalities().get(i).getPlaceId());
                        values.setAddresss(body.getLocalities().get(i).getLocality());
                        values.setType("Locality");
                        projectsBynames.add(values);
                    }

                }


//                Log.i(TAG, "onSuccessSearchByName: " + value.toString());
                if (projectsBynames.size() > 0) {
                    adapter = new SearchByNameAdapter(SearchActivity.this, projectsBynames, geoDataClient);
                    mRecyclerView.setAdapter(adapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    search_result_zero.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    search_result_zero.setVisibility(View.VISIBLE);
                }
            }
            progressBar2.setVisibility(View.GONE);
        } else {
            nestedscroll.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            search_result_zero.setVisibility(View.GONE);
        }

        Log.i(TAG, "onSuccessSearchByName: api end time " + UtilityMethods.getDate(System.currentTimeMillis(), "dd-MM-yyyy HH:mm:ss SSS a"));

    }


    public ArrayList<RecentSearch> getRecentlyViewed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString("recent_search", "");
        Type type = new TypeToken<List<RecentSearch>>() {
        }.getType();
        ArrayList<RecentSearch> list = gson.fromJson(json, type);
        if ((list != null) && (!list.isEmpty()))
            Collections.reverse(list);
        return list;
    }


    private boolean isCityChanged = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CHANGE_CITY && resultCode == RESULT_OK) {
            Log.d(TAG, "city changed!");
            isCityChanged = true;
            updateView();
        }
    }

    private void updateView() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (!TextUtils.isEmpty(UtilityMethods.getStringInPref(SearchActivity.this, Constants.AppConstants.SAVED_CITY, ""))) {
                    searchincitytext.setVisibility(View.VISIBLE);
                    searchincitytext.setText(getResources().getString(R.string.search_in_city) + " " +
                            UtilityMethods.getStringInPref(SearchActivity.this, Constants.AppConstants.SAVED_CITY, ""));
                    mSearchEdittext.setText("");
                    nestedscroll.setVisibility(View.VISIBLE);
                    toplocality_tv.setVisibility(View.GONE);
                    topLocalityrecyle.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    search_result_zero.setVisibility(View.GONE);
                    id_search_clear_btn.setVisibility(View.INVISIBLE);
                }

            }
        });
        saveBuilderAndProjectList();

    }

    private void saveBuilderAndProjectList() {
//        model.getBuilderAndProjectList(selectedCityId);
        long cityId = UtilityMethods.getLongInPref(SearchActivity.this, Constants.AppConstants.SAVED_CITY_ID, -1);
        ProjectListViewModel model = new ProjectListViewModel(this);
        model.getTopProjet(cityId);
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.SearchScreen);

    }
}
