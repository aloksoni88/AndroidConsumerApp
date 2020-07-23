package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.model.PropertyBooking;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.MyPropertyDetails;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.MyPropertyScreen;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by root on 19/12/16.
 */
public class MyPropertiesAdapter extends RecyclerView.Adapter<MyPropertiesAdapter.PropertyViewHolder> {
    private final String TAG = MyPropertiesAdapter.class.getSimpleName();
    Context context;
    List<PropertyBooking> propertyList;

    public MyPropertiesAdapter(Context context, List<PropertyBooking> propertyList) {
        this.context = context;
        this.propertyList = propertyList;
    }

    public class PropertyViewHolder extends RecyclerView.ViewHolder {
        private TextView propertyAddress, projectName, description, dueAmount, paidAmount, totalAmount, floor,
                        paidAmountText, pendingAmountText, totalAmountText;
        private ImageView projectImage;
        private ProgressBar mPropertyImagePB;
        private Button bookingStatusTag;

        public PropertyViewHolder(View itemView) {
            super(itemView);
            propertyAddress = (TextView) itemView.findViewById(R.id.property_address);
            projectName = (TextView) itemView.findViewById(R.id.project_name_my_property);
            description = (TextView) itemView.findViewById(R.id.property_description_my_property);
            paidAmount = (TextView) itemView.findViewById(R.id.paid_amount);
            paidAmountText = (TextView) itemView.findViewById(R.id.id_paid_amount_text);
            dueAmount = (TextView) itemView.findViewById(R.id.due_amount);
            pendingAmountText = (TextView) itemView.findViewById(R.id.id_pending_amount_text);
            totalAmount = (TextView) itemView.findViewById(R.id.total_amount);
            totalAmountText = (TextView) itemView.findViewById(R.id.id_total_amount_text);
            //floor = (TextView) itemView.findViewById(R.id.property_floor);
            projectImage = (ImageView) itemView.findViewById(R.id.project_image_my_property);
            mPropertyImagePB = (ProgressBar) itemView.findViewById(R.id.id_myproperty_image_pb);
            bookingStatusTag = (Button) itemView.findViewById(R.id.booking_status_tag);
        }
    }

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_properties_list, parent, false);
        return new PropertyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PropertyViewHolder holder, final int position) {
        if ((propertyList != null) && !propertyList.isEmpty()) {
            if (!TextUtils.isEmpty(propertyList.get(position).getProjectURL())) {
                String imageUrl = propertyList.get(position).getProjectURL()+"=h300";
                Log.i(TAG, "onBindViewHolder: Project Image URL -> "+ imageUrl + " for position " +position);
                holder.mPropertyImagePB.setVisibility(View.VISIBLE);
                Picasso.get().load(imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.projectImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.mPropertyImagePB.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.mPropertyImagePB.setVisibility(View.GONE);
                            }
                        });
            }else{
                holder.projectImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder));
            }

            if(!TextUtils.isEmpty(propertyList.get(position).getPropertyBookingStatus())){
                if(propertyList.get(position).getPropertyBookingStatus().equalsIgnoreCase(Constants.PropertyBookingStatus.Canceled.toString())){
                    holder.bookingStatusTag.setVisibility(View.VISIBLE);
                    holder.bookingStatusTag.setText("CANCELLED");
                    UtilityMethods.setDrawableBackground(context,holder.bookingStatusTag,R.drawable.booking_status_cancelled_btn_bg);
                    UtilityMethods.setButtonTextColor(context,holder.bookingStatusTag,R.color.colorWhite);
                }else if(propertyList.get(position).getPropertyBookingStatus().equalsIgnoreCase(Constants.PropertyBookingStatus.UserRequset.toString())){
                    holder.bookingStatusTag.setVisibility(View.VISIBLE);
                    holder.bookingStatusTag.setText("REQUESTED");
                    UtilityMethods.setDrawableBackground(context,holder.bookingStatusTag,R.drawable.booking_status_requested_btn_bg);
                    UtilityMethods.setButtonTextColor(context,holder.bookingStatusTag,R.color.gray_600);
                }else if(propertyList.get(position).getPropertyBookingStatus().equalsIgnoreCase(Constants.PropertyBookingStatus.Requested.toString())){
                    holder.bookingStatusTag.setVisibility(View.VISIBLE);
                    holder.bookingStatusTag.setText("PROCESSING");
                    UtilityMethods.setDrawableBackground(context,holder.bookingStatusTag,R.drawable.booking_status_processing_btn_bg);
                    UtilityMethods.setButtonTextColor(context,holder.bookingStatusTag,R.color.gray_600);
                }else{
                    holder.bookingStatusTag.setVisibility(View.GONE);
                }
            }

            /*if(!TextUtils.isEmpty(propertyList.get(position).getPropertyBookingStatus())
                    && propertyList.get(position).getPropertyBookingStatus().equalsIgnoreCase(Constants.PropertyBookingStatus.Canceled.toString())){
                holder.bookingStatusTag.setVisibility(View.VISIBLE);
            }else{
                holder.bookingStatusTag.setVisibility(View.GONE);
            }*/


            if (!TextUtils.isEmpty(propertyList.get(position).getProjectName())) {
                holder.projectName.setText(propertyList.get(position).getProjectName());
            }else{
                holder.projectName.setText("");
            }

            if ((propertyList.get(position).getHouseNumber() != null)
                    && (!TextUtils.isEmpty(propertyList.get(position).getBlockName()))) {

                holder.propertyAddress.setText(propertyList.get(position).getHouseNumber() + ", "
                        + propertyList.get(position).getBlockName());

            } else if ((propertyList.get(position).getHouseNumber() != null)
                    && (TextUtils.isEmpty(propertyList.get(position).getBlockName()))) {
                holder.propertyAddress.setText(propertyList.get(position).getHouseNumber());

            } else if ((propertyList.get(position).getHouseNumber() == null)
                    && (!TextUtils.isEmpty(propertyList.get(position).getBlockName()))) {
                holder.propertyAddress.setText(propertyList.get(position).getBlockName());
            } else {
                holder.propertyAddress.setText("");
            }

            String propertyType = "";
            String propertyArea = "";
            if (!TextUtils.isEmpty(propertyList.get(position).getPropertyTypeName())) {
                if(UtilityMethods.isCommercial(propertyList.get(position).getPropertyTypeName())) {
                    propertyType = UtilityMethods.getCommercialTypeName(propertyList.get(position).getPropertyTypeName());
                }else{
                    propertyType = propertyList.get(position).getPropertyTypeName();
                }
            }

            if (!TextUtils.isEmpty(propertyList.get(position).getPropertyArea())) {
                propertyArea = propertyList.get(position).getPropertyArea();
            }

            if (!TextUtils.isEmpty(propertyType) && !TextUtils.isEmpty(propertyArea)) {
                //holder.description.setText(propertyType + ", " + propertyArea + " sq ft.");
                if(UtilityMethods.isCommercialLand(propertyType) || propertyType.toLowerCase().contains("land")){
                    try {
                        holder.description.setText(propertyType + ", " + UtilityMethods.getAreaInYards(Long.parseLong(propertyArea)) + " sq yd.");
                    } catch (Exception e) {
                        holder.description.setText(propertyType + ", " + propertyArea + " sq ft.");
                        e.printStackTrace();
                    }
                }else{
                    holder.description.setText(propertyType + ", " + propertyArea + " sq ft.");
                }
            } else if (!TextUtils.isEmpty(propertyType) && TextUtils.isEmpty(propertyArea)) {
                holder.description.setText(propertyType);
            } else if (TextUtils.isEmpty(propertyType) && !TextUtils.isEmpty(propertyArea)) {
                holder.description.setText(propertyArea);
            } else {
                holder.description.setVisibility(View.GONE);
            }

            long totalAmount = 0;
            if (propertyList.get(position).getTotalAmount() != null) {
                totalAmount = Long.parseLong(propertyList.get(position).getTotalAmount());
                long amount = Long.parseLong(propertyList.get(position).getTotalAmount());
                holder.totalAmount.setText(" \u20B9 " + getPrice(amount));
            }else{
                holder.totalAmount.setText("");
            }

            if ((propertyList.get(position).getPaymentDetailList() != null) &&
                    (!propertyList.get(position).getPaymentDetailList().isEmpty())) {
                long paid = 0;
                for (int i = 0; i < propertyList.get(position).getPaymentDetailList().size(); i++) {
                    if (!TextUtils.isEmpty(propertyList.get(position).getPaymentDetailList().get(i).getAmount())) {
                        paid += Long.parseLong(propertyList.get(position).getPaymentDetailList().get(i).getAmount());
                    }
                }

                //holder.paidAmount.setText("₹ " + getPrice(paid) + "");

                //if total amount is missing, paid amount need to show in center
                if(totalAmount != 0){
                    holder.paidAmount.setText("₹ " + getPrice(paid) + "");
                    holder.dueAmount.setText("\u20B9 " + getPrice(totalAmount - paid));
                    holder.paidAmount.setVisibility(View.VISIBLE);
                    holder.totalAmount.setVisibility(View.VISIBLE);
                    holder.paidAmountText.setVisibility(View.VISIBLE);
                    holder.totalAmountText.setVisibility(View.VISIBLE);
                    holder.pendingAmountText.setText("PENDING");
                }else{
                    holder.paidAmount.setVisibility(View.GONE);
                    holder.totalAmount.setVisibility(View.GONE);
                    holder.paidAmountText.setVisibility(View.GONE);
                    holder.totalAmountText.setVisibility(View.GONE);
                    holder.pendingAmountText.setText("PAID");
                    holder.dueAmount.setText("₹ " + getPrice(paid) + "");
                }
                /*if (totalAmount != 0)
                    holder.dueAmount.setText("\u20B9 " + getPrice(totalAmount - paid));
                else {
                    holder.dueAmount.setText("");
                }*/
            }else{
                holder.dueAmount.setText("");
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleOnItemClick(propertyList.get(holder.getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if ((propertyList != null) && (!propertyList.isEmpty()))
            return propertyList.size();
        else
            return 0;
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

    private void handleOnItemClick(PropertyBooking property){
        if(context != null && !UtilityMethods.isInternetConnected(context)){
            UtilityMethods.showErrorSnackbarOnTop((MyPropertyScreen)context);
            return;
        }
        Log.d(TAG, "handleOnItemClick Clicked!!");
        Intent intent = new Intent(context, MyPropertyDetails.class);
        try {
            if(property != null && !property.isEmpty()) {
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, property.getProjectId());
                intent.putExtra("PropertyImageURL", property.getProjectURL());
                intent.putExtra("PropertyName", property.getProjectName());
                intent.putExtra("PropertyArea", property.getPropertyArea());
                intent.putExtra("PropertyId", property.getId());
                intent.putExtra("PropertyTypeName", property.getPropertyTypeName());
                intent.putExtra("BlockName", property.getBlockName());
                intent.putExtra("Floor", property.getFloorNumber());
                intent.putExtra("Unit", property.getHouseNumber());
                intent.putExtra("AgentId", property.getAgentId());
                intent.putExtra("AgentName", property.getAgentName());
                intent.putExtra("TotalAmount", property.getTotalAmount());
                if(!TextUtils.isEmpty(property.getPropertyBookingStatus())) {
                    intent.putExtra("PropertyBookingStatus", property.getPropertyBookingStatus());
                }
                if (property.getApplicationForm() != null && !property.getApplicationForm().isEmpty()) {
                    if (property.getApplicationForm().getName() != null && !property.getApplicationForm().getName().isEmpty()) {
                        intent.putExtra("ApplicationFormName", property.getApplicationForm().getName());
                    }
                    if (property.getApplicationForm().getUrl() != null && !property.getApplicationForm().getUrl().isEmpty()) {
                        intent.putExtra("ApplicationFormURL", property.getApplicationForm().getUrl());
                    }
                }
                if(property.getPaymentDetailList() != null && !property.getPaymentDetailList().isEmpty()) {
                    ((HousingApplication) context.getApplicationContext()).setmPaymentDetailList(property.getPaymentDetailList());
                }
                if(property.getOtherDocs() != null && !property.getOtherDocs().isEmpty()) {
                    ((HousingApplication) context.getApplicationContext()).setmDocumentList(property.getOtherDocs());
                }

                if(property.getCustomerList() != null && !property.getCustomerList().isEmpty()) {
                    ((HousingApplication) context.getApplicationContext()).setCustomerList(property.getCustomerList());
                }
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(intent);
        }
    }

}
