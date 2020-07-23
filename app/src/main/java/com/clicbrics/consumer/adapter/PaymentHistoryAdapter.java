package com.clicbrics.consumer.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.model.PaymentDetail;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.List;

/**
 * Created by Alok on 04-10-2016.
 */

@SuppressWarnings("ResourceType")
public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>{

    private static final String TAG =  PaymentHistoryAdapter.class.getSimpleName();
    Activity mContext;
    List<PaymentDetail> mPaymentHistory;

    public PaymentHistoryAdapter(Activity mContext, List<PaymentDetail> mPaymentHistory) {
        this.mContext = mContext;
        this.mPaymentHistory = mPaymentHistory;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView paidAmountValue, paidAmountDate, paymentStaus;
        public ViewHolder(View view) {
            super(view);
            paidAmountValue = (TextView) view.findViewById(R.id.id_paid_amount_value);
            paidAmountDate = (TextView) view.findViewById(R.id.id_payment_history_date);
            paymentStaus = (TextView) view.findViewById(R.id.id_payment_history_status);
        }
    }


    @Override
    public PaymentHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_history_adapter_layout, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: Position " + position);
        String paymentMode = "";
        if(mPaymentHistory != null && mPaymentHistory.size() > 0){
            PaymentDetail paymentHistory = mPaymentHistory.get(position);
            //holder.paidAmountValue.setText("₹ " + getPrice(Integer.parseInt(paymentHistory.getAmount()))+"");
            paymentMode = paymentHistory.getMode() != null ? paymentHistory.getMode() : "CASH";
            if(paymentHistory.getPayed() != null){
                if(paymentHistory.getPayed()){
                    holder.paidAmountDate.setText(getDate(paymentHistory.getPaymentTime()));
                    holder.paymentStaus.setText(mContext.getString(R.string.paid).toUpperCase());
                    UtilityMethods.setTextViewColor(mContext,holder.paymentStaus,R.color.paid_button_color);
                    UtilityMethods.setDrawableBackground(mContext,holder.paymentStaus,R.drawable.paid_button_selector_bg);
                    String paidAmountStr = getPrice(Integer.parseInt(paymentHistory.getAmount()));
                    String s= "₹ " + paidAmountStr + " paid via " + paymentMode;
                    SpannableString ss1=  new SpannableString(s);
                    int index = s.indexOf("paid");
                    ss1.setSpan(new TextAppearanceSpan(mContext, Typeface.BOLD), 0,index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                    holder.paidAmountValue.setText(ss1);
                }else{
                    holder.paymentStaus.setText(mContext.getString(R.string.due).toUpperCase());
                    UtilityMethods.setTextViewColor(mContext,holder.paymentStaus,R.color.red_dark);
                    UtilityMethods.setDrawableBackground(mContext,holder.paymentStaus,R.drawable.due_button_selector_bg);
                    String paidAmountStr = getPrice(Integer.parseInt(paymentHistory.getAmount()));
                    String s= "₹ " + paidAmountStr + " due Amount ";
                    SpannableString ss1=  new SpannableString(s);
                    int index = s.indexOf("due");
                    ss1.setSpan(new TextAppearanceSpan(mContext, Typeface.BOLD), 0,index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                    holder.paidAmountValue.setText(ss1);
                    if(paymentHistory.getDueDate() != null){
                        holder.paidAmountDate.setText(getDate(paymentHistory.getDueDate()));
                    }else{
                        holder.paidAmountDate.setText(getDate(paymentHistory.getPaymentTime()));
                    }
                }
            }else{
                if(paymentHistory.getPaymentTime() != null) {
                    holder.paidAmountDate.setText(getDate(paymentHistory.getPaymentTime()));
                }
                holder.paymentStaus.setText(mContext.getString(R.string.paid).toUpperCase());
                UtilityMethods.setTextViewColor(mContext,holder.paymentStaus,R.color.paid_button_color);
                UtilityMethods.setDrawableBackground(mContext,holder.paymentStaus,R.drawable.paid_button_selector_bg);
                int  amount = !TextUtils.isEmpty(paymentHistory.getAmount()) ? Integer.parseInt(paymentHistory.getAmount()) : 0;
                String paidAmountStr = getPrice(amount);
                String s= "₹ " + paidAmountStr + " paid via " + paymentMode;
                SpannableString ss1=  new SpannableString(s);
                int index = s.indexOf("paid");
                ss1.setSpan(new TextAppearanceSpan(mContext, Typeface.BOLD), 0,index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                holder.paidAmountValue.setText(ss1);
            }

            //ss1.setSpan(new RelativeSizeSpan(0.5f),10,18,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //ss1.setSpan(new RelativeSizeSpan(1f),19,s.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private String getDate(long date) {
        String dayStr = UtilityMethods.getFormatedDay(date);
        String dateStr = DateFormat.format("MMM yyyy", date).toString();
        String formatedDate = dayStr + " " + dateStr;
        return formatedDate;
    }


    private String getPrice(float amount) {
        if (amount >= 10000000) {
            float price = amount / 10000000;
            return String.format("%.2f",price) + " Cr";
        } else if (amount >= 100000) {
            float price = amount / 100000;
            return String.format("%.2f",price) + " Lac";
        } else {
            float price = amount / 1000;
            return String.format("%.2f",price) + " K";
        }
    }

    @Override
    public int getItemCount() {
        if(mPaymentHistory != null && mPaymentHistory.size() > 0) {
            Log.i(TAG, "getItemCount: " + mPaymentHistory.size());
            return mPaymentHistory.size();
        }else{
            return 0;
        }
    }
}
