package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.PaymentPlan;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.PaymentPlanActivity;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.ArrayList;

/**
 * Created by root on 20/1/17.
 */
public class PaymentPlanListAdapter extends RecyclerView.Adapter<PaymentPlanListAdapter.PlanNameViewHolder>{

    Context mContext;
    ArrayList<PaymentPlan> planList;

    public PaymentPlanListAdapter(Context context, ArrayList<PaymentPlan> paymentPlanList) {
        mContext = context;
        planList = paymentPlanList;
        /*if(paymentPlanList != null){
            planList = new ArrayList<>();
            *//**
             * If getHide flag is true i.e we don't need to show that payment plan
             *//*
            for(int i=0; i<paymentPlanList.size(); i++){
                PaymentPlan paymentPlan = paymentPlanList.get(i);
                if(paymentPlan != null && (paymentPlan.getHide() == null || !paymentPlan.getHide())){
                    planList.add(paymentPlan);
                }
            }
        }*/
    }

    @Override
    public PlanNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_plan,parent,false);
        return new PlanNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanNameViewHolder holder, final int position) {
        if(position == 0) {
            holder.itemView.setPadding(UtilityMethods.dpToPx(16), 0, 0, 0);
        }else if(position == planList.size()-1){
            holder.itemView.setPadding(0, 0,  UtilityMethods.dpToPx(16),0);
        }else{
            holder.itemView.setPadding(0, 0, 0, 0);
        }
        holder.planName.setText(planList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PaymentPlanActivity.class);
                intent.putExtra(Constants.IntentKeyConstants.PAGE_INDEX,position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public class PlanNameViewHolder extends RecyclerView.ViewHolder{
        TextView planName;
        public PlanNameViewHolder(View itemView) {
            super(itemView);
            planName = (TextView) itemView.findViewById(R.id.plan_name);
        }
    }
}
