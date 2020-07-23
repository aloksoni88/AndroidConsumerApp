package com.clicbrics.consumer.fragment;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.Task;
import com.buy.housing.backend.userEndPoint.model.TaskCollection;
import com.clicbrics.consumer.EndPointBuilder;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.MyPropertyDetails;
import com.clicbrics.consumer.adapter.ServiceRequestHistoryAdapter;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Alok on 15-03-2017.
 */

@SuppressWarnings("deprecation")
public class ServiceRequestHistoryFragment extends Fragment {
    private static final String TAG = ServiceRequestHistoryFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RelativeLayout mParentLayout;
    private TextView emptyView, allReqTitle;
    private Button raiseReqBtn;
    UserEndPoint mUserEndPoint;
    ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.servicereq_history_frag_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParentLayout = (RelativeLayout) view.findViewById(R.id.id_service_req_history_fragment);
        emptyView = (TextView) view.findViewById(R.id.empty_view_req_history);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_servicereq_history_fragment_list);
        allReqTitle = (TextView) view.findViewById(R.id.id_all_req_title);
        raiseReqBtn = (Button) view.findViewById(R.id.id_new_service_req_btn);
        mProgressBar = (ProgressBar) view.findViewById(R.id.id_req_history_progressbar);

        raiseReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNewRaiseRequest();
            }
        });
        buildUserWebService();
        getRequestHistory();
    }

    private void buildUserWebService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        ((MyPropertyDetails)getActivity()).mToolbarText.setText(getResources().getString(R.string.service_request));
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((MyPropertyDetails)getActivity()).mToolbarLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorAccent));
        }else {
            ((MyPropertyDetails)getActivity()).mToolbarLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_red));
        }*/
    }

    public void onClickNewRaiseRequest(){
        Log.i(TAG, "onClickServiceRequest: ");
        ServiceRequestFragment serviceRequestFragment = new ServiceRequestFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(serviceRequestFragment.isAdded()){
            fragmentTransaction.show(serviceRequestFragment);
        }else {
            fragmentTransaction.replace(R.id.id_fragment_details_container, serviceRequestFragment, Constants.AppConstants.TAG_SERVICE_REQUEST_FRAGMENT);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getRequestHistory(){
        if (!UtilityMethods.isInternetConnected(getActivity())) {
            Snackbar snackbar = Snackbar
                    .make(mParentLayout, getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction("TRY AGAIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getRequestHistory();
                        }
                    });

            snackbar.setActionTextColor(Color.WHITE);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));
            snackbar.show();
            return;
        }
        final Handler handler = new Handler();
        final long userId = UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, 0l);
        mProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            String errMsg = "";
            @Override
            public void run() {
                try {
                    UserEndPoint.GetMyTaskList getMyTaskList = mUserEndPoint.getMyTaskList(userId);
                    if(getActivity() != null && getActivity().getIntent().hasExtra("PropertyId")){
                        getMyTaskList.setBookingId(getActivity().getIntent().getLongExtra("PropertyId",0l));
                    }
                    final TaskCollection taskCollection = getMyTaskList.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if(taskCollection != null && taskCollection.getItems() != null && !taskCollection.getItems().isEmpty()){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.GONE);
                                allReqTitle.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                                Collections.sort(taskCollection.getItems(), new Comparator<Task>() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public int compare(Task task1, Task task2) {
                                        return Long.compare(task2.getRequestTime(),task1.getRequestTime());
                                    }
                                });
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                ServiceRequestHistoryAdapter requestHistoryAdapter = new ServiceRequestHistoryAdapter(getActivity(), taskCollection.getItems());
                                mRecyclerView.setAdapter(requestHistoryAdapter);
                            }
                        });
                    }else{
                        errMsg = "No service request raised!";
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    errMsg = getResources().getString(R.string.network_error_msg);
                }catch (Exception e) {
                    e.printStackTrace();
                    errMsg = "Could not find service request.\nPlease try again!";
                }
                if(!TextUtils.isEmpty(errMsg)){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            if(!errMsg.equalsIgnoreCase("No service request raised!")){
                                UtilityMethods.showSnackBar(mParentLayout,errMsg, Snackbar.LENGTH_SHORT);
                            }
                            emptyView.setVisibility(View.VISIBLE);
                            allReqTitle.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();
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
            TrackAnalytics.trackEvent("ServiceReqHistoryScreen", Constants.AppConstants.HOLD_TIME,
                    UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), getActivity());
        }
    }
}
