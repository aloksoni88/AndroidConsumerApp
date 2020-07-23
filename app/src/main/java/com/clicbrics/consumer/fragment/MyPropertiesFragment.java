package com.clicbrics.consumer.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.GetMyPropertyListResponse;
import com.buy.housing.backend.userEndPoint.model.PropertyBooking;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.MyPropertiesAdapter;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.UnknownHostException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("deprecation")
public class MyPropertiesFragment extends BaseFragment {

    private final String TAG = MyPropertiesFragment.class.getSimpleName();
    private UserEndPoint userEndPoint;
    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private LinearLayout parentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_properties, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout_my_properties);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_my_properties);
        emptyView = (LinearLayout) view.findViewById(R.id.empty_view_my_properties);
        buildUserWebService();
        getMyProperties();
    }

    private void buildUserWebService() {
        userEndPoint = EndPointBuilder.getUserEndPoint();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void getMyProperties() {
        if (!UtilityMethods.isInternetConnected(mActivity)) {
            /*if(((MyProperties) getActivity()).mSelectedFragmentPos == 0) {
                Snackbar snackbar = Snackbar
                        .make(parentLayout, getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_INDEFINITE)
                        .setAction("TRY AGAIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if ((((MyProperties) mActivity).mPropertiesPagerAdapter) != null
                                        && (((MyProperties) mActivity).mPropertiesPagerAdapter).getItem(0) != null) {
                                    MyPropertiesFragment myPropertiesFragment = (MyPropertiesFragment) (((MyProperties) mActivity).mPropertiesPagerAdapter).getItem(0);
                                    myPropertiesFragment.getMyProperties();
                                }
                                if ((((MyProperties) mActivity).mPropertiesPagerAdapter) != null
                                        && (((MyProperties) mActivity).mPropertiesPagerAdapter).getItem(1) != null) {
                                    SiteVisitFragment siteVisitFragment = (SiteVisitFragment) (((MyProperties) mActivity).mPropertiesPagerAdapter).getItem(1);
                                    siteVisitFragment.getMyRedbrics();
                                }
                                //getMyProperties();
                            }
                        });
                snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));
                snackbar.show();
            }*/
            UtilityMethods.showErrorSnackbarOnTop(mActivity);
            return;
        }
        showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    HousingLogs.i(TAG, "User Id " + UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0l));
                    final GetMyPropertyListResponse myPropertyList = userEndPoint
                            .getMyPropertyList(UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0l))
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (myPropertyList != null && myPropertyList.getStatus()) {

                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                dismissProgressBar();
                                setListData(myPropertyList.getPropertyBookingList());
                            }
                        });
                    } else {
                        errorMsg = getResources().getString(R.string.no_properties_found);
                    }
                } catch (UnknownHostException e) {
                    errorMsg = getString(R.string.network_error_msg);
                    e.printStackTrace();

                }catch (Exception e) {
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
                            if(errmsg.equalsIgnoreCase(getResources().getString(R.string.no_properties_found))){
                                recyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            }else {
                                if(!errmsg.equalsIgnoreCase(getResources().getString(R.string.no_properties_found))) {
                                    UtilityMethods.showSnackbarOnTop(mActivity, "Error", errmsg, true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }


    private void setListData(List<PropertyBooking> propertyList) {
        if(propertyList!=null && !propertyList.isEmpty()) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new MyPropertiesAdapter(getActivity(), propertyList));
        } else {
            emptyView.setVisibility(View.VISIBLE);
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
            TrackAnalytics.trackEvent("MyPropertiesScreen", Constants.AppConstants.HOLD_TIME,
                    UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), getActivity());
        }
    }


}
