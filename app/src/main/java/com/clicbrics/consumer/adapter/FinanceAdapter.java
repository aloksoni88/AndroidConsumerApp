package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.Bank;
import com.buy.housing.backend.propertyEndPoint.model.KeyValuePair;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.EmiActivity;
import com.clicbrics.consumer.customview.roundedimageview.RoundedImageView;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by root on 20/1/17.
 */
public class FinanceAdapter extends RecyclerView.Adapter<FinanceAdapter.FinanceViewHolder> {

    Context mContext;
    List<Bank> bankList;
    boolean isPriceOnRequest;
    View mFinanceLayout;
    private boolean isViewEmpty = false;

    public FinanceAdapter(Context context, View view,List<Bank> bankLoan, boolean isPriceOnRequest) {
        mContext = context;
        mFinanceLayout = view;
        bankList = bankLoan;
        this.isPriceOnRequest = isPriceOnRequest;
    }

    public class FinanceViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView bankLogo;
        private TextView bankName;
        private TextView interestRate;
        private ImageView bankOfferImage;
        private ProgressBar mBankImagePB;
        public FinanceViewHolder(View itemView) {
            super(itemView);
            bankLogo = (RoundedImageView) itemView.findViewById(R.id.bank_logo);
            bankName = (TextView) itemView.findViewById(R.id.bank_name);
            interestRate = (TextView) itemView.findViewById(R.id.bank_interest_rate);
            bankOfferImage = (ImageView) itemView.findViewById(R.id.id_offer_image);
            mBankImagePB = (ProgressBar) itemView.findViewById(R.id.id_bank_list_image_pb);
        }
    }

    @Override
    public FinanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_financing,parent,false);
        return new FinanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FinanceViewHolder holder, final int position) {
        if(position == 0) {
            holder.itemView.setPadding(UtilityMethods.dpToPx(16), 0, 0, 0);
        }else if(position == bankList.size()-1){
            holder.itemView.setPadding(0, 0,  UtilityMethods.dpToPx(16),0);
        }else{
            holder.itemView.setPadding(0, 0, 0, 0);
        }
        if(bankList != null && !bankList.isEmpty()) {
            isViewEmpty = true;
            if(bankList.get(position).getLogo() !=null && !bankList.get(position).getLogo().isEmpty()) {
                isViewEmpty = false;
                holder.mBankImagePB.setVisibility(View.VISIBLE);
                String bankLogoURL = bankList.get(position).getLogo();
                Log.d("TAG", "bankLogoURL->" + bankLogoURL);
                Picasso.get().load(bankLogoURL)
                        .placeholder(R.drawable.ic_bank_icon)
                        .into(holder.bankLogo, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.mBankImagePB.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.mBankImagePB.setVisibility(View.GONE);
                            }
                        });
            }

            if(!TextUtils.isEmpty(bankList.get(position).getName())) {
                isViewEmpty = false;
                holder.bankName.setText(bankList.get(position).getName());
            }else{
                holder.bankName.setVisibility(View.GONE);
            }
            if(bankList.get(position).getInterest() != null ) {
                isViewEmpty = false;
                holder.interestRate.setText(bankList.get(position).getInterest() + "%");
            }else{
                holder.interestRate.setVisibility(View.GONE);
            }
            final List<KeyValuePair> bankOfferList = bankList.get(position).getOffersList();
            if (bankOfferList != null && bankOfferList.size() > 0) {
                holder.bankOfferImage.setVisibility(View.VISIBLE);
            } else {
                holder.bankOfferImage.setVisibility(View.GONE);
            }

            if(!isPriceOnRequest) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bank bank = bankList.get(holder.getAdapterPosition());
                        if(bank != null && !TextUtils.isEmpty(bank.getName()) && bank.getInterest() != null){
                            Intent intent = new Intent(mContext, EmiActivity.class);
                            intent.putExtra(Constants.IntentKeyConstants.IS_FROM_FINANCE, true);    //flag to evaluate from where we are calling this activity.
                            Gson gson = new Gson();
                            String json = gson.toJson(bankList.get(position));
                            intent.putExtra(Constants.IntentKeyConstants.BANK_DETAILS, json);
                            mContext.startActivity(intent);
                        }else{
                            return;
                        }
                    }
                });
            }
            //handling empty view
            if(isViewEmpty){
                if(mFinanceLayout != null){
                    mFinanceLayout.setVisibility(View.GONE);
                }
            }else{
                if(mFinanceLayout != null){
                    mFinanceLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }
}
