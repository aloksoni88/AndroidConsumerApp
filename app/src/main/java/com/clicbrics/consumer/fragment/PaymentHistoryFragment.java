package com.clicbrics.consumer.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.model.PaymentDetail;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.*;
import com.clicbrics.consumer.adapter.PaymentHistoryAdapter;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Alok on 02-02-2017.
 */

@SuppressWarnings("deprecation")
public class PaymentHistoryFragment extends BaseFragment {

    private final String TAG = PaymentHistoryFragment.class.getSimpleName();
    List<PaymentDetail> mPaymentHistoryList;
    TextView emptyListView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            emptyListView = (TextView) view.findViewById(R.id.empty_view_payment_history);
            RecyclerView mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_payment_history);
            mRecyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mPaymentHistoryList = ((HousingApplication)getActivity().getApplicationContext()).getPaymentDetailList();

            if(mPaymentHistoryList != null && mPaymentHistoryList.size() > 0) {
                Collections.sort(mPaymentHistoryList, new Comparator<PaymentDetail>() {
                    @Override
                    public int compare(PaymentDetail lhs, PaymentDetail rhs) {
                        return -(lhs.getPaymentTime().compareTo(rhs.getPaymentTime())); //latest date first
                    }
                });
                PaymentHistoryAdapter paymentHistoryAdapter = new PaymentHistoryAdapter(getActivity(), mPaymentHistoryList);
                mRecyclerView.setAdapter(paymentHistoryAdapter);
            }
            else{
                mRecyclerView.setVisibility(View.GONE);
                emptyListView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        ((MyPropertyDetails)getActivity()).mToolbarText.setText(getResources().getString(R.string.payment_history));
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
            TrackAnalytics.trackEvent("PaymentHistoryScreen", Constants.AppConstants.HOLD_TIME,
                    UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), getActivity());
        }
    }

}
