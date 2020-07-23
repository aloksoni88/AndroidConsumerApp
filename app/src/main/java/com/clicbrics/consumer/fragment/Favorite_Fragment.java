package com.clicbrics.consumer.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buy.housing.backend.userEndPoint.model.PropertySearchResponse;
import com.buy.housing.backend.userEndPoint.model.SearchProperty;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.databinding.FavoriteFragmentLayoutBinding;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.helper.FavoriteProjectListResultCallback;
import com.clicbrics.consumer.helper.WishlistResultCallback;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.adapter.HomeProjectListAdapter;
import com.clicbrics.consumer.viewmodel.ProjectListViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favorite_Fragment extends BaseFragment implements FavoriteProjectListResultCallback {

    private final String TAG = "Favorite_Fragment";

    private List<Project> favProjectList = new ArrayList<>();
    private HomeProjectListAdapter favListAdapter;
    private FavoriteFragmentLayoutBinding binding;
    private static int totalFavListCount = 0;
    private ProjectListViewModel model;

    public Favorite_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.favorite_fragment_layout,container,false);
        View view = binding.getRoot();
        model = new ProjectListViewModel(this);
        Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(getContext(),Constants.AppConstants.PROJECT_ID_SET);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.wishlistRecyclerView.setLayoutManager(layoutManager);
        if(favoriteList == null || favoriteList.isEmpty()){
            onErrorFavoriteProjectList("No project in wishlist");
        }else{
            model.getFavoriteProjectList(new ArrayList<String>(favoriteList));
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * This method gets called when user coming from project details screen 
     */
    public void refreshListOnRestart(){
        Log.i(TAG, "refreshListOnRestart: ");
        Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(getContext(),Constants.AppConstants.PROJECT_ID_SET);
        model.getFavoriteProjectList(new ArrayList<String>(favoriteList));
    }

    @Override
    public boolean isInternetConnected() {
        if(UtilityMethods.isInternetConnected(mActivity)){
            return true;
        }else{
            UtilityMethods.showSnackbarOnTop(mActivity,"Error",getString(R.string.no_network_connection),true,1500);
            return false;
        }
    }

    @Override
    public void showLoader() {
        showProgressBar();
    }

    @Override
    public void hideLoader() {
        dismissProgressBar();
    }

    @Override
    public void onSuccessFavoriteProjectList(Response<ProjectListResponse> projectListResponse) {
        if(projectListResponse.body().getProjectList() != null && !projectListResponse.body().getProjectList().isEmpty()){
            favProjectList = projectListResponse.body().getProjectList();
            if(getActivity() != null)
            {
                setResultCount(favProjectList.size());
                setListData();
            }

            Set<String> favoriteList = new HashSet<String>();
            for (Project project : favProjectList) {
                favoriteList.add(project.getId()+"");
            }
            UtilityMethods.clearPreference(getActivity(),Constants.AppConstants.PROJECT_ID_SET);
            UtilityMethods.saveWishlistInPref(getActivity(),favoriteList);
        }
        else
            {
            UtilityMethods.clearPreference(getActivity(),Constants.AppConstants.PROJECT_ID_SET);
            favProjectList.clear();
            if(getActivity() != null){
                setListData();
            }
        }
    }

    @Override
    public void onErrorFavoriteProjectList(String errMsg) {
        if(getActivity() != null)
        {
            setResultCount(0);
            binding.favoritesEmpty.setVisibility(View.VISIBLE);
            binding.wishlistRecyclerView.setVisibility(View.GONE);
            binding.resultCount.setVisibility(View.GONE);
            if(!errMsg.equalsIgnoreCase("No project in wishlist")) {
                UtilityMethods.showSnackbarOnTop(mActivity, "Error", errMsg, true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
            }
        }

    }


    /*@Override
    public void onError(String errMsg) {
        setResultCount(0);
        binding.favoritesEmpty.setVisibility(View.VISIBLE);
        binding.wishlistRecyclerView.setVisibility(View.GONE);
        binding.resultCount.setVisibility(View.GONE);
        if(!errMsg.equalsIgnoreCase("No project in wishlist")) {
            UtilityMethods.showSnackbarOnTop(mActivity, "Error", errMsg, true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
        }
    }

    @Override
    public void onNextPageLoadError(String errMsg) {

    }*/

    /**
     * Method to check in OnResume callback weather user has marked property unfavorite, if yes then need to update list
     */
    /*private void refreshWishList(){
        Set<String> favorites = UtilityMethods.getWishListFromPrefs(mActivity, Constants.AppConstants.PROJECT_ID_SET);
        if(!favProjectList.isEmpty()) {
            ListIterator<Project> listIterator = favProjectList.listIterator();
            while (listIterator.hasNext()){
                Project project = listIterator.next();
                if(project != null && !favorites.contains(project.getId()+"")){
                    listIterator.remove();
                }
            }
            if(favProjectList != null && !favProjectList.isEmpty()){
                binding.favoritesEmpty.setVisibility(View.GONE);
                binding.wishlistRecyclerView.setVisibility(View.VISIBLE);
                binding.resultCount.setVisibility(View.VISIBLE);
                setResultCount(favProjectList.size());
                favListAdapter.notifyDataSetChanged();
            }else{
                binding.favoritesEmpty.setVisibility(View.VISIBLE);
                binding.wishlistRecyclerView.setVisibility(View.GONE);
                binding.resultCount.setVisibility(View.GONE);
            }
        }else{
            binding.favoritesEmpty.setVisibility(View.VISIBLE);
            binding.wishlistRecyclerView.setVisibility(View.GONE);
            binding.resultCount.setVisibility(View.GONE);
        }
    }*/

    private void setListData() {
        if(favProjectList != null && !favProjectList.isEmpty()) {
            favListAdapter = new HomeProjectListAdapter(favProjectList);
            binding.wishlistRecyclerView.setAdapter(favListAdapter);
        }else{
            binding.favoritesEmpty.setVisibility(View.VISIBLE);
            binding.wishlistRecyclerView.setVisibility(View.GONE);
            binding.resultCount.setVisibility(View.GONE);
        }
    }


    private void setResultCount(int count) {
        if(count != 0) {
            binding.resultCount.setVisibility(View.VISIBLE);
            if (count == 1) {
                binding.resultCount.setText(count + " Result");
            } else {
                binding.resultCount.setText(count + " Results");
            }
        }else{
            binding.resultCount.setVisibility(View.GONE);
        }
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
            TrackAnalytics.trackEvent("WishlistPage", Constants.AppConstants.HOLD_TIME,
                    UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), getActivity());
        }
    }

}
