package com.clicbrics.consumer.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.Task;
import com.buy.housing.backend.userEndPoint.model.TaskCollection;
import com.buy.housing.backend.userEndPoint.model.TimeLineStats;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.StickyHeaderLayoutManager;
import com.clicbrics.consumer.adapter.SiteVisitAdapter;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.SiteVisitsActivity;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SiteVisitFragment extends BaseFragment implements SiteVisitAdapter.UpdateListener{

    private final String TAG = SiteVisitFragment.class.getSimpleName();
    private LinearLayout rootLayout;
    private UserEndPoint userEndPoint;
    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private FrameLayout parentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_site_visit, container, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentLayout = (FrameLayout) view.findViewById(R.id.parentLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_site_visit);
        emptyView = (LinearLayout) view.findViewById(R.id.empty_view_site_visit);
        buildUserWebService();
        getMyRedbrics();
    }

    private void buildUserWebService() {
        userEndPoint= EndPointBuilder.getUserEndPoint();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void getMyRedbrics() {
        if (!UtilityMethods.isInternetConnected(mActivity)) {
            /*if(((MyProperties) getActivity()).mSelectedFragmentPos == 1){
                Snackbar snackbar = Snackbar
                        .make(parentLayout, getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_INDEFINITE)
                        .setAction("TRY AGAIN", new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void onClick(View view) {
                                //getMyRedbrics();
                                if( (((MyProperties) mActivity).mPropertiesPagerAdapter) != null
                                        && (((MyProperties) mActivity).mPropertiesPagerAdapter).getItem(0) != null){
                                    MyPropertiesFragment myPropertiesFragment = (MyPropertiesFragment) (((MyProperties) mActivity).mPropertiesPagerAdapter).getItem(0);
                                    myPropertiesFragment.getMyProperties();
                                }
                                if( (((MyProperties) mActivity).mPropertiesPagerAdapter) != null
                                        && (((MyProperties) mActivity).mPropertiesPagerAdapter).getItem(1) != null){
                                    SiteVisitFragment siteVisitFragment = (SiteVisitFragment) (((MyProperties) mActivity).mPropertiesPagerAdapter).getItem(1);
                                    siteVisitFragment.getMyRedbrics();
                                }
                            }
                        });
                snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));
                snackbar.show();
            }*/
            UtilityMethods.showErrorSnackbarOnTop((SiteVisitsActivity)mActivity);
            return;
        }

        showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    HousingLogs.i(TAG, "User Id " + UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0l));
                    final TaskCollection taskCollection = userEndPoint
                            .getMyTaskList(UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0l))
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (taskCollection != null) {
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                dismissProgressBar();
                                setListData(taskCollection.getItems());
                            }
                        });
                    } else {
                        errorMsg = getResources().getString(R.string.no_sitevisit);
                    }
                } catch (UnknownHostException e) {
                    mHandler.post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void run() {
                            dismissProgressBar();
                        }
                    });
                    errorMsg = getString(R.string.network_error_msg);
                    e.printStackTrace();
                }catch (Exception e) {
                    mHandler.post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void run() {
                            dismissProgressBar();
                        }
                    });
                    errorMsg = getString(R.string.something_went_wrong);
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;
                    mHandler.post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void run() {
                            dismissProgressBar();
                            if(errmsg.equalsIgnoreCase(getResources().getString(R.string.no_sitevisit))){
                                recyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            }else {
                                if(!errmsg.equalsIgnoreCase(getResources().getString(R.string.no_sitevisit))){
                                    UtilityMethods.showSnackbarOnTop(mActivity, "Error", errmsg, true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void setListData(List<Task> taskList) {
        if (taskList != null && !taskList.isEmpty()) {

            //Log.d(TAG,"taskList->"+taskList.size());
            recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
            Log.d(TAG,"taskListSize->"+taskList.size());

            for (int i = (taskList.size()-1); i>=0 ; i--) {
                Log.d(TAG,"taskCategory->"+taskList.get(i).getTaskCategory());
                if (!taskList.get(i).getTaskCategory().equals("SiteVisit")) {
                    taskList.remove(i);
                    Log.d(TAG,"removed");
                } else {
                    //sort timeLineStates of tasks
                    if ((taskList.get(i).getTimeLineStates() != null) && (!taskList.get(i).getTimeLineStates().isEmpty())) {
                        if (taskList.get(i).getTimeLineStates().size() > 1) {
                            for (int j = 0; j < taskList.get(i).getTimeLineStates().size(); j++) {
                                Collections.sort(taskList.get(i).getTimeLineStates(), new Comparator<TimeLineStats>() {
                                    @Override
                                    public int compare(TimeLineStats lhs, TimeLineStats rhs) {
                                        return rhs.getTime().compareTo(lhs.getTime());
                                    }
                                });
                            }
                        }
                    }
                }
            }

            //sort taskList w.r.t RequestTime
            if (taskList.size() > 1) {
                Collections.sort(taskList, new Comparator<Task>() {
                    @Override
                    public int compare(Task lhs, Task rhs) {
                        return rhs.getRequestTime().compareTo(lhs.getRequestTime());
                    }
                });
            }

            SiteVisitAdapter mSiteVisitAdapter = new SiteVisitAdapter(getActivity(), taskList, parentLayout, userEndPoint);
            mSiteVisitAdapter.setUpdateListener(this);
            recyclerView.setAdapter(mSiteVisitAdapter);

        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onUpdate() {
        getMyRedbrics();
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
            TrackAnalytics.trackEvent("SiteVisitPage", Constants.AppConstants.HOLD_TIME,
                    UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), getActivity());
        }
    }
}
