package com.clicbrics.consumer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.PaymentPlan;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.PaymentPlanActivity;
import com.clicbrics.consumer.adapter.PaymentAdapter;
import com.clicbrics.consumer.utils.Item;
import com.clicbrics.consumer.wrapper.PaymentPlanHeader;
import com.clicbrics.consumer.wrapper.PaymentPlanItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PaymentPlanFragment extends BaseFragment {

    private List<PaymentPlanItem> paymentPlanItems = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<Item>();
    private RecyclerView mListView;
    TextView paymentDescription;
    private String key;
    private String description;

    public static PaymentPlanFragment newInstance(int page){//}, String key) {
        PaymentPlanFragment fragment = new PaymentPlanFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        //args.putString("title_key", key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG","key1->"+getArguments().getInt("page", 0));
        key = PaymentPlanActivity.keys.get(getArguments().getInt("page", 0));
        description = PaymentPlanActivity.planDetail.get(getArguments().getInt("page", 0));
        Log.d("TAG", "key->"+key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (RecyclerView) view.findViewById(R.id.listView_main);
        paymentDescription = (TextView) view.findViewById(R.id.payment_plan_description);
        setValues();

        mListView.setNestedScrollingEnabled(false);
        mListView.setHasFixedSize(true);
    }

    private void setValues(){
        paymentPlanItems = PaymentPlanActivity.paymentPlans.get(key);
        paymentDescription.setText(description);
        List<PaymentPlan> paymentPlanList = ((HousingApplication) getActivity().getApplicationContext()).getPaymentPlanList();
        for(int i = 0; i < paymentPlanList.size() ; i++){
            items.add(new PaymentPlanHeader(paymentPlanList.get(i).getName(),paymentPlanList.get(i).getDetail()));
            for(int j =0 ;j < paymentPlanList.get(i).getKeyValuePairList().size() ; j++){
                items.add(new PaymentPlanItem(paymentPlanList.get(i).getKeyValuePairList().get(j).getKey()
                        , paymentPlanList.get(i).getKeyValuePairList().get(j).getValue()));
            }
        }

        PaymentAdapter adapter = new PaymentAdapter(mActivity, paymentPlanItems);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListView.setAdapter(adapter);
    }
 }
