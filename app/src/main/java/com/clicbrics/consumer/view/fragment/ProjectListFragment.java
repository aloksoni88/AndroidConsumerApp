package com.clicbrics.consumer.view.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.FilterActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.CustomProgressDialog;
import com.clicbrics.consumer.databinding.FragmentProjectListBinding;
import com.clicbrics.consumer.fragment.BaseFragment;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.activity.SplashActivity;
import com.clicbrics.consumer.helper.ProjectListResultCallback;
import com.clicbrics.consumer.interfaces.IPropertyOperationsListener;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.FilterUtility;
import com.clicbrics.consumer.utils.SortUtility;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.adapter.HomeProjectListAdapter;
import com.clicbrics.consumer.viewmodel.ProjectListViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ProjectListFragment extends BaseFragment implements ProjectListResultCallback,
        SortUtility.ISortSelectionListener{

    private static final String TAG = "ProjectListFragment";
    private FragmentProjectListBinding binding;
    public static final int FILTER_REQUEST_CODE = 103;
    private static final int RESULT_PROPERTY_SEARCH = 9879;
    private final int INIT_OFFSET = 0;
    private final int INIT_LIMIT = 15;
    private final int OFFSET_DIFF = 15;
    private TextView txtPropertyCount,txtPolygonMsg,txtNoResultTxt,txtFilter, txtSort, txtSaveSearch;
    private RelativeLayout lytFilter,lytSort,lytSaveSearch;
    private LinearLayout lytNoInternet,lytErrorView;
    private ImageView imgFilter,imgSort, imgSaveSearch;
    private Button btnTryAgain;
    private boolean isLoading = false;
    private int totalProjectCount;
    private ProjectListViewModel model;
    private HomeProjectListAdapter homeProjectListAdapter;
    private List<Project> projectList= new ArrayList<>();

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

    private CustomProgressDialog progressDialog;
    private IPropertyOperationsListener listener;

    private double latitude, longitude;

    private String resultTypeValue;
    private enum ResultType{
        onSuccess, onError, onEmpty, onInternet
    }
    private String searchType="";
    private enum SearchBy{
        SearchByLocation,SearchByDeveloper,SearchByCity
    }
    private String selPropUnit = "";

    public ProjectListFragment() {

    }

    public static ProjectListFragment newInstance() {
        ProjectListFragment fragment = new ProjectListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.registerReceiver(updateFavorite, new IntentFilter(Constants.BroadCastConstants.FAVORITE_CHANGE));
    }

    @Override
    public void onResume() {
        super.onResume();

        String selectedPropertyUnit = UtilityMethods.getSelectedUnit(mActivity);
        if(!selPropUnit.equalsIgnoreCase(selectedPropertyUnit)){
            selPropUnit = selectedPropertyUnit;
            if(homeProjectListAdapter != null){
                homeProjectListAdapter.notifyDataSetChanged();
            }
        }
    }
    long onStartTime;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*model.getProjectListByCity(selectedCityId+"",0,20,null,null,null,
                null,null,null,null,null);*/
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_project_list,container,false);
        View view = binding.getRoot();
        initView(view);
        onStartTime = System.currentTimeMillis();
        // To set initial list from Mapview
        /*projectList = ((HomeScreen)mActivity).getProjectList();
        Log.i(TAG, "onCreateView: Project List " + projectList.size());
        if(projectList != null && !projectList.isEmpty()) {
            resultTypeValue = ResultType.onSuccess.toString();
            setResultView();
        }*/
        selPropUnit = UtilityMethods.getSelectedUnit(mActivity);
        model = new ProjectListViewModel(this);
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
            latitude = ((HomeScreen) mActivity).getLatitude();
            longitude = ((HomeScreen) mActivity).getLongitude();
            getProjectListByDistance(latitude,longitude);
        }else if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString())){
            filterBuilderId = ((HomeScreen) mActivity).getDeveloperId();
            getProjectListByDeveloper(filterBuilderId);
        }else{
            getProjectListByCity();
        }
        return view;
    }

    private void initView(View view){
        lytFilter = view.findViewById(R.id.id_filter_layout);
        lytSort = view.findViewById(R.id.id_sort_view);
        lytSaveSearch = view.findViewById(R.id.id_saved_search_view);
        txtPropertyCount = view.findViewById(R.id.id_property_count);
        txtPolygonMsg = view.findViewById(R.id.id_polygon_msg);
        lytNoInternet = view.findViewById(R.id.id_no_internet_layout);
        lytErrorView = view.findViewById(R.id.error_view);
        txtNoResultTxt = view.findViewById(R.id.no_results_txt);
        btnTryAgain = view.findViewById(R.id.id_try_again_btn);
        txtFilter = view.findViewById(R.id.id_filter_view);
        txtSort = view.findViewById(R.id.id_sort_text);
        txtSaveSearch = view.findViewById(R.id.id_save_search_text);
        imgFilter = view.findViewById(R.id.id_filter_img);
        imgSort = view.findViewById(R.id.id_sort_img);
        imgSaveSearch = view.findViewById(R.id.id_saved_search_img);

        lytFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                startActivityForResult(intent, FILTER_REQUEST_CODE);
            }
        });
        lytSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortUtility sortUtility = new SortUtility(ProjectListFragment.this);
                sortUtility.showSortDialog(getActivity());
            }
        });
        lytSaveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onSaveSearchApplied();
                }
            }
        });

        binding.idNoInternetLayout.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onNoInternetTryAgain();
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (IPropertyOperationsListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Interface SortUtility.ISortSelectionListener method gets called when user open sorting dialog and selected any type of sorting
     * @param sortType - selected sortType
     */
    @Override
    public void onSortApplied(Constants.SortType sortType) {
        Log.i(TAG, "onSortSelected: SortType " + sortType);
        if(!UtilityMethods.isInternetConnected(getActivity())){
            resultTypeValue = ResultType.onInternet.toString();
            setResultView();
            return;
        }
        if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString()) && filterBuilderId != 0){
            getProjectListByDeveloper(((HomeScreen)mActivity).getDeveloperId());
        }else if(searchType.equalsIgnoreCase(SearchBy.SearchByLocation.toString())
                && latitude != 0 && longitude != 0){
            getProjectListByDistance(((HomeScreen)mActivity).getLatitude(),((HomeScreen)mActivity).getLongitude());
        }else{
            getProjectListByCity();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     */
    /*public interface OnListFragmentInteractionListener {
        void onFilterApplied();
        void onSortApplied();
        void onSaveSearchApplied();
        void onSearchApplied();
        void onCityChanged();
    }*/

    /**
     * To get the project list by city (City search)
     */
    public void getProjectListByCity(){
        searchType = SearchBy.SearchByCity.toString();
        long cityId = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.SAVED_CITY_ID,-1);
        setFilterVariables();
        setSortVariable();
        model.getProjectListByCity(cityId+"",INIT_OFFSET,INIT_LIMIT,null,filterPropertyType,filterBedType,filterProjectStatus,filterMinPrice,
                filterMaxPrice,filterMinArea,filterMaxArea,filterSort,
                requestId,appVersion,userId,userEmail,userPhone,userName,
                virtualId,leadsource,browser,browserVersion,browserType,getSummary);
    }

    /**
     * To get the project list by developer(developer search)
     * @param developerId
     */
    public void getProjectListByDeveloper(long developerId){
        searchType = SearchBy.SearchByDeveloper.toString();
        filterBuilderId = developerId;
        long cityId = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.SAVED_CITY_ID,-1);
        setFilterVariables();
        setSortVariable();
        model.getProjectListByCity(cityId+"",INIT_OFFSET,INIT_LIMIT,developerId,filterPropertyType,filterBedType,filterProjectStatus,filterMinPrice,
                filterMaxPrice,filterMinArea,filterMaxArea,filterSort,
                requestId,appVersion,userId,userEmail,userPhone,userName,
                virtualId,leadsource,browser,browserVersion,browserType,getSummary);
    }

    /**
     * Get Project list by distance(search)
     * @param latitude - latitude
     * @param longitude - longitude
     */
    public void getProjectListByDistance(double latitude, double longitude){
        searchType = SearchBy.SearchByLocation.toString();
        this.latitude = latitude;
        this.longitude = longitude;
        long cityId = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.SAVED_CITY_ID,-1);
        setFilterVariables();
        setSortVariable();




        model.getProjectListByDistance(latitude,longitude,cityId+"",INIT_OFFSET,INIT_LIMIT,filterPropertyType,filterBedType,filterProjectStatus,filterMinPrice,
                filterMaxPrice,filterMinArea,filterMaxArea,filterSort,requestId,appVersion,userId,userEmail,userPhone,userName,
                virtualId,leadsource,browser,browserVersion,browserType,city,searchText);
    }

    /**
     * An interface ProjectListResultCallback method to check internet connectivity
     * @return - true or false
     */
    @Override
    public boolean isInternetConnected() {

        if(getActivity() == null)
        {
            return false;
        }

        if(UtilityMethods.isInternetConnected(getActivity())){
            return true;
        }else{
            isLoading = false;
            UtilityMethods.showSnackbarOnTop(getActivity(),"Error",getString(R.string.no_network_connection),true,1500);
            return false;
        }
    }

    /**
     * An interface ProjectListResultCallback method to show progress bar on full screen
     * this method gets called while we call getProjectByCity API
     */
    @Override
    public void showLoader() {
        isLoading = true;
        //showProgressBar();
        if(getActivity() != null) {
            binding.homeRecyclerView.setVisibility(View.GONE);
            binding.idShimmerLayout.setVisibility(View.VISIBLE);
            binding.idShimmerLayout.startShimmer();
        }
    }

    /**
     * An interface ProjectListResultCallback method to dismiss progressbar
     * this method gets called while we get response from server either true or false
     */
    @Override
    public void hideLoader() {
        //dismissProgressBar();
        if(getActivity() != null) {
            binding.homeRecyclerView.setVisibility(View.VISIBLE);
            binding.idShimmerLayout.stopShimmer();
            binding.idShimmerLayout.setVisibility(View.GONE);
        }
    }

    /**
     * An interface ProjectListResultCallback method which gets called on Success of projectListByCity API
     * @param projectListResponse - server response
     */
    @Override
    public void onSuccessProjectByCity(Response<ProjectListResponse> projectListResponse) {
        isLoading = false;
        if(projectListResponse.body().getProjectList() != null && !projectListResponse.body().getProjectList().isEmpty()){
            new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.HomeScreen,
                    null, Constants.ApiName.getProjectByCity.toString(),Constants.AnalyticsEvents.SUCCESS,null);
            totalProjectCount = projectListResponse.body().getCount();
            projectList = projectListResponse.body().getProjectList();
            //setListView(projectListResponse.body().getProjectList());
            resultTypeValue = ResultType.onSuccess.toString();
        }else{
            resultTypeValue = ResultType.onEmpty.toString();
            /*setResultCount(0);
            txtEmptyView.setVisibility(View.VISIBLE);
            binding.homeRecyclerView.setVisibility(View.GONE);*/
        }


        if(getActivity() != null)
        {
            ((HomeScreen) mActivity).setProjectList(projectList);
            setResultView();
        }

    }

    /**
     * An interface ProjectListResultCallback method which gets called on fail of projectListByCity API
     * @param errMsg - error message from server api
     */
    @Override
    public void onErrorProjectByCity(String errMsg) {
        new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.HomeScreen,
                null, Constants.ApiName.getProjectByCity.toString(),Constants.AnalyticsEvents.FAILED,errMsg);
        isLoading = false;
        resultTypeValue = ResultType.onError.toString();
        //setEmptyView(false);

        if(getActivity() != null)
        {
            setResultView();
        }

    }

    /**
     * An interface ProjectListResultCallback method which gets called while we load next paging data to show loader on bottom
     * pagination loader
     */
    @Override
    public void showPagingLoader() {
        isLoading = true;
        if(getActivity() != null) {
            binding.idNextPageLoaderView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * An interface ProjectListResultCallback method which gets called while we get response from server(either true or fasle)
     * to hide the loader on bottom of the screen
     */
    @Override
    public void hidePagingLoader() {
        if(getActivity() != null) {
            binding.idNextPageLoaderView.setVisibility(View.GONE);
        }
    }

    /**
     * An interface ProjectListResultCallback method - success of the pagination loading project list
     * @param projectListResponse - server response
     */
    @Override
    public void onSuccessNextProjectListByCity(Response<ProjectListResponse> projectListResponse) {
        isLoading = false;
        if(projectListResponse.body().getProjectList() != null && !projectListResponse.body().getProjectList().isEmpty()){
            int loadingIndexFrom = projectList.size();
            int loadedIndexTo = loadingIndexFrom + projectListResponse.body().getProjectList().size();
            projectList.addAll(projectListResponse.body().getProjectList());
            if(getActivity() != null)
            {
                ((HomeScreen) mActivity).setProjectList(projectList);
                homeProjectListAdapter.notifyItemRangeChanged(loadingIndexFrom,loadedIndexTo);
            }

        }else{
            if(getActivity() != null)
            {
                UtilityMethods.showSnackbarOnTop(getActivity(), "Error", "No more project found", false, Constants.AppConstants.ALERTOR_LENGTH_LONG);
            }

        }
    }

    /**
     * An interface ProjectListResultCallback method - gets called on failed of pagination loading project list API
     * @param errMsg - error message from server
     */
    @Override
    public void onErrorNextProjectListByCity(String errMsg) {
        isLoading = false;
        if(getActivity() != null)
        {
            UtilityMethods.showSnackbarOnTop(getActivity(), "Error", errMsg, false, Constants.AppConstants.ALERTOR_LENGTH_LONG);
        }

    }

    /**
     * Method to set the Total Project count of selected city
     * @param count - no of project in the list
     */
    private void setResultCount(int count){
        try {
            totalProjectCount = count;
            if(count <= 1) {
                txtPropertyCount.setText(count + " Property");
            }else{
                txtPropertyCount.setText(count + " Properties");
            }
            if(getActivity() != null &&((HomeScreen)getActivity()).isPolygonDrawn()){
                txtPolygonMsg.setVisibility(View.VISIBLE);
            }else{
                txtPolygonMsg.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to set the UI view of project list screen(adapter , filter view, sorting view also success view)
     *
     */
    private void setListView(){
        try {
            //this.projectList = projectList;
            setFilterView();
            setSuccessView();
            setResultCount(totalProjectCount);
            binding.homeRecyclerView.setHasFixedSize(true);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            binding.homeRecyclerView.setLayoutManager(layoutManager);
            homeProjectListAdapter = new HomeProjectListAdapter(projectList);
            binding.homeRecyclerView.setAdapter(homeProjectListAdapter);

            final long cityId = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.SAVED_CITY_ID,-1);
            binding.homeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(dy > 0){
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && totalItemCount < totalProjectCount) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                    && firstVisibleItemPosition >= 0) {
                                if(searchType.equalsIgnoreCase(SearchBy.SearchByLocation.toString())) {
                                    model.getNextProjectListByDistance(latitude,longitude,cityId + "", totalItemCount, OFFSET_DIFF, filterPropertyType, filterBedType, filterProjectStatus, filterMinPrice,
                                            filterMaxPrice, filterMinArea, filterMaxArea, filterSort,requestId,appVersion,userId,userEmail,userPhone,userName,
                                            virtualId,leadsource,browser,browserVersion,browserType,city,searchText);
                                }else if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString())){
                                    model.getProjectListByCity(cityId+"",totalItemCount,OFFSET_DIFF,filterBuilderId,filterPropertyType,filterBedType,filterProjectStatus,filterMinPrice,
                                            filterMaxPrice,filterMinArea,filterMaxArea,filterSort,
                                            requestId,appVersion,userId,userEmail,userPhone,userName,
                                            virtualId,leadsource,browser,browserVersion,browserType,getSummary);
                                }else{
                                    model.getNextProjectListByCity(cityId + "", totalItemCount, OFFSET_DIFF, null,
                                            filterPropertyType, filterBedType, filterProjectStatus, filterMinPrice,
                                            filterMaxPrice, filterMinArea, filterMaxArea, filterSort,
                                            requestId,appVersion,userId,userEmail,userPhone,userName,
                                            virtualId,leadsource,browser,browserVersion,browserType,getSummary);
                                }
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Method to set the Filter View UI
     */
    private void setFilterView(){
        try {
            boolean isSelected = UtilityMethods.getBooleanInPref(getActivity(), Constants.SharedPreferConstants.PARAM_FILTER_APPLIED, false);
            if(isSelected){
                lytFilter.setBackgroundResource(R.drawable.filter_rect_bg_red);
                UtilityMethods.setTextViewColor(getActivity(),txtFilter,R.color.colorAccent);
                imgFilter.setImageResource(R.drawable.ic_filter_red);
            }else{
                lytFilter.setBackgroundResource(R.drawable.filter_rect_bg_gray);
                UtilityMethods.setTextViewColor(getActivity(),txtFilter,R.color.gray_800);
                imgFilter.setImageResource(R.drawable.ic_filter_gray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setResultView(){

        setFilterView();
        if(resultTypeValue.equalsIgnoreCase(ResultType.onSuccess.toString())){
            setListView();
        }else if(resultTypeValue.equalsIgnoreCase(ResultType.onEmpty.toString())){
            setResultCount(0);
            binding.idContainerLayout.setVisibility(View.VISIBLE);
            binding.idEmptyView.setVisibility(View.VISIBLE);
            binding.homeRecyclerView.setVisibility(View.GONE);
            binding.idNoInternetLayout.setVisibility(View.GONE);
        }else if(resultTypeValue.equalsIgnoreCase(ResultType.onInternet.toString())){
            setResultCount(0);
            binding.idContainerLayout.setVisibility(View.VISIBLE);
            binding.idEmptyView.setVisibility(View.GONE);
            binding.idNoInternetLayout.setVisibility(View.VISIBLE);
            binding.homeRecyclerView.setVisibility(View.GONE);
        }else if(resultTypeValue.equalsIgnoreCase(ResultType.onError.toString())){
            setResultCount(0);
            setSortView(false);
            setSaveSearchView(false);
            binding.idNoInternetLayout.setVisibility(View.GONE);
            binding.noResultsFound.setVisibility(View.VISIBLE);
            final boolean isFilterApplied = UtilityMethods.isFilterApplied(getActivity());
            if(isFilterApplied){
                txtNoResultTxt.setText(getResources().getString(R.string.no_results_with_filter));
            }else{
                txtNoResultTxt.setText(getResources().getString(R.string.no_results_full));
            }
            binding.homeRecyclerView.setVisibility(View.GONE);
            binding.idContainerLayout.setVisibility(View.VISIBLE);
            lytNoInternet.setVisibility(View.GONE);
            lytErrorView.setVisibility(View.GONE);
            btnTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(snackbar != null) {
                        snackbar.dismiss();
                    }
                    if (isFilterApplied) {
                        Intent intent = new Intent(getActivity(), FilterActivity.class);
                        startActivityForResult(intent, FILTER_REQUEST_CODE);
                    } else {
                        //callProjectListAPI();
                        binding.noResultsFound.setVisibility(View.GONE);
                        getProjectListByCity();
                    }
                }
            });
        }
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

    /**
     * Method to set the success view(if api return true and reutrn at least 1 project then this method gets called)
     */
    private void setSuccessView(){
        setSortView(true);
        setSaveSearchView(true);
        try {
            binding.idCriteriaLayout.setVisibility(View.VISIBLE);
            binding.homeRecyclerView.setVisibility(View.VISIBLE);
            binding.errorView.setVisibility(View.GONE);
            binding.noResultsFound.setVisibility(View.GONE);
            binding.idEmptyView.setVisibility(View.GONE);
            binding.idContainerLayout.setVisibility(View.VISIBLE);
            binding.idNoInternetLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to set the Sort View UI
     * @param isEnable - boolean flag which shows weather sorting is enable or not
     */
    private void setSortView(boolean isEnable){
        try {
            boolean isSorted = UtilityMethods.isSortingApplied(getActivity());
            if(isSorted){
                if(isEnable){
                    lytSort.setBackgroundResource(R.drawable.filter_rect_bg_red);
                    lytSort.setEnabled(true);
                    UtilityMethods.setTextViewColor(getActivity(), txtSort, R.color.colorAccent);
                    imgSort.setImageResource(R.drawable.ic_sort_red);
                }else {
                    lytSort.setBackgroundResource(R.drawable.filter_rect_bg_red_disable);
                    lytSort.setEnabled(false);
                    UtilityMethods.setTextViewColor(getActivity(), txtSort, R.color.red_light);
                    imgSort.setImageResource(R.drawable.ic_sort_red_disabled);
                }
            }
            else{
                if(isEnable){
                    lytSort.setBackgroundResource(R.drawable.filter_rect_bg_gray);
                    lytSort.setEnabled(true);
                    UtilityMethods.setTextViewColor(getActivity(), txtSort, R.color.gray_800);
                    imgSort.setImageResource(R.drawable.ic_sort_gray);
                }else {
                    lytSort.setBackgroundResource(R.drawable.filter_rect_bg_gray_disable);
                    lytSort.setEnabled(false);
                    UtilityMethods.setTextViewColor(getActivity(), txtSort, R.color.gray_400);
                    imgSort.setImageResource(R.drawable.ic_sort_disable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to set the save search UI view
     * @param isEnable - boolean flag, is save search selected or not
     */
    private void setSaveSearchView(boolean isEnable){
        try {
            boolean isSelected = false;
            if(isSelected){
                if(isEnable){
                    lytSaveSearch.setEnabled(true);
                    UtilityMethods.setTextViewColor(getActivity(), txtSaveSearch, R.color.colorAccent);
                    imgSaveSearch.setImageResource(R.drawable.ic_sort_red);
                }else{
                    lytSaveSearch.setEnabled(false);
                    UtilityMethods.setTextViewColor(getActivity(), txtSaveSearch, R.color.red_light);
                    imgSaveSearch.setImageResource(R.drawable.ic_save_search_selected_disable);
                }
            }else{
                if(isEnable){
                    lytSaveSearch.setEnabled(true);
                    UtilityMethods.setTextViewColor(getActivity(), txtSaveSearch, R.color.gray_800);
                    imgSaveSearch.setImageResource(R.drawable.ic_save_search_gray);
                }else{
                    lytSaveSearch.setEnabled(false);
                    UtilityMethods.setTextViewColor(getActivity(), txtSaveSearch, R.color.gray_400);
                    imgSaveSearch.setImageResource(R.drawable.ic_save_search_gray_disable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to show snackbar indefinate tyep so that user can do try again
     */
    Snackbar snackbar;
    public void showNetworkErrorSnackBar() {
        if(snackbar == null) {
            snackbar = Snackbar.make(binding.getRoot(), getResources().getString(R.string.please_check_network_connection),
                    Snackbar.LENGTH_LONG);
        }
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));
        if(snackbar != null && !snackbar.isShown()){
            snackbar.show();
        }
    }

    /**
     * onActivity result of filter applied
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: ");
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == FILTER_REQUEST_CODE){
                callOnFilterApplied();
            }
        }
    }

    /**
     * This method called while user applied filter
     * Based on the search type it will fetch the project list from server
     */
    private void callOnFilterApplied(){
        Log.i(TAG, "callOnFilterApplied: ");
        if(!UtilityMethods.isInternetConnected(getActivity())){
            resultTypeValue = ResultType.onInternet.toString();
            setResultView();
            return;
        }
        if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString()) && filterBuilderId != 0){
            getProjectListByDeveloper(((HomeScreen)mActivity).getDeveloperId());
        }else if(searchType.equalsIgnoreCase(SearchBy.SearchByLocation.toString())
                && latitude != 0 && longitude != 0){
            getProjectListByDistance(((HomeScreen)mActivity).getLatitude(),((HomeScreen)mActivity).getLongitude());
        }else{
            getProjectListByCity();
        }
    }


    /**
     * This method gets called while user click on try again button of no internet connectivity
     */
    public void callOnNoInternetTryAgain(){
        Log.i(TAG, "callOnNoInternetTryAgain: ");
        //callProjectListAPI();
        if(searchType.equalsIgnoreCase(SearchBy.SearchByDeveloper.toString()) && filterBuilderId != 0){
            getProjectListByDeveloper(((HomeScreen)mActivity).getDeveloperId());
        }else if(searchType.equalsIgnoreCase(SearchBy.SearchByLocation.toString())
                && latitude != 0 && longitude != 0){
            getProjectListByDistance(((HomeScreen)mActivity).getLatitude(),((HomeScreen)mActivity).getLongitude());
        }else{
            getProjectListByCity();
        }
    }

    /**
     * Broadcast receiver to update the favorite in home project list
     */
    private BroadcastReceiver updateFavorite = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constants.BroadCastConstants.FAVORITE_CHANGE) {
                if(homeProjectListAdapter != null){
                    homeProjectListAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        new EventAnalyticsHelper().logUsageStatsEvents(getActivity(),onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.HomeScreen);
    }
}

/*
    Activity activity = getActivity();
                    if (activity != null && isAdded())*/
