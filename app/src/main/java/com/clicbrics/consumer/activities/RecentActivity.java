package com.clicbrics.consumer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.databinding.ActivityRecentBinding;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.helper.RecentProjectResultCallback;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.adapter.HomeProjectListAdapter;
import com.clicbrics.consumer.viewmodel.RecentProjectViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class RecentActivity extends BaseActivity implements RecentProjectResultCallback {

    private static final String TAG = "RecentActivity";
    List<Project> recentlyViewed;
    private HomeProjectListAdapter homeProjectListAdapter;
    private RecentProjectViewModel model;
    private ActivityRecentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recent);
        model = new RecentProjectViewModel(this);
        binding.setRecentProjectModel(model);
        binding.recentProjectRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        getRecentProject();
    }

    @Override
    public boolean isInternetConnected() {
        return false;
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void onSuccess(Response<ProjectListResponse> response) {
        if(response.body().getProjectList() != null && !response.body().getProjectList().isEmpty()){
            new EventAnalyticsHelper().logAPICallEvent(RecentActivity.this, Constants.AnaylticsClassName.RecentScreen,
                    null, Constants.ApiName.getFavoriteProjectList.toString(),Constants.AnalyticsEvents.SUCCESS,
                    null);
            recentlyViewed = response.body().getProjectList();
            UtilityMethods.updateRecentProjectList(this, recentlyViewed);
            if(homeProjectListAdapter != null){
                homeProjectListAdapter.notifyDataSetChanged();
            }else{
                binding.recentProjectRecyclerView.setHasFixedSize(true);
                homeProjectListAdapter  = new HomeProjectListAdapter(recentlyViewed);
                binding.idEmptyView.setVisibility(View.GONE);
                binding.recentProjectRecyclerView.setVisibility(View.VISIBLE);
                binding.recentProjectRecyclerView.setAdapter(homeProjectListAdapter);
            }
        }else{
            binding.idEmptyView.setVisibility(View.VISIBLE);
            binding.recentProjectRecyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onError(String errMsg) {
        new EventAnalyticsHelper().logAPICallEvent(RecentActivity.this, Constants.AnaylticsClassName.RecentScreen,
                null, Constants.ApiName.getFavoriteProjectList.toString(),Constants.AnalyticsEvents.FAILED,
                errMsg);
        binding.idEmptyView.setVisibility(View.VISIBLE);
        binding.recentProjectRecyclerView.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    long onStartTime;
    @Override
    protected void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
        registerReceiver(updateFavorite,new IntentFilter(Constants.BroadCastConstants.FAVORITE_CHANGE));

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(updateFavorite);
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.RecentScreen);

    }

    public void getRecentProject(){
        try {
            recentlyViewed =  UtilityMethods.getRecentProjectList(this);
            if (recentlyViewed != null && !recentlyViewed.isEmpty()) {
                homeProjectListAdapter  = new HomeProjectListAdapter(recentlyViewed);
                binding.idEmptyView.setVisibility(View.GONE);
                binding.recentProjectRecyclerView.setHasFixedSize(true);
                binding.recentProjectRecyclerView.setVisibility(View.VISIBLE);
                binding.recentProjectRecyclerView.setAdapter(homeProjectListAdapter);
                handleExpireProjects();
            }else{
                binding.idEmptyView.setVisibility(View.VISIBLE);
                binding.recentProjectRecyclerView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleExpireProjects(){
        final List<String> recentProjectIds=new ArrayList<>();
        if(recentlyViewed != null){
            for(int i=0; i< recentlyViewed.size(); i++){
                recentProjectIds.add(recentlyViewed.get(i).getId()+"");
            }
            if(!recentProjectIds.isEmpty()){
                model.getRecentProjectList(recentProjectIds);
            }
        }
    }

    /**
     * Broadcast receiver to update the favorite in recent project list
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.ActivityRequestCode.RECENT_ACT_RESULT_CODE){
            getRecentProject();
        }
    }
}
