package com.clicbrics.consumer.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.Agent;
import com.buy.housing.backend.userEndPoint.model.Document;
import com.buy.housing.backend.userEndPoint.model.PaymentDetail;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.MyPropertyDetails;
import com.clicbrics.consumer.customview.CircularImageView;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.clicbrics.consumer.utils.UtilityMethods.getFormatedDay;

@SuppressWarnings("deprecation")
public class MyPropertyDetailFragment extends BaseFragment {
    private final String TAG = "PropertyDetailFragment";

    ImageView projectImage;
    TextView projectName, paidAmount, dueAmount, totalAmount, propertyAddress,
            description, bhkType,block, floorText,floor, unit, agentName, paymentAmount, paidVia, paymentDate,
            paidAmountText, pendingAmountText, totalAmountText, paymentText;
    CardView blockRowCardView, unitRowCardView, imageCardView, paymentCardView;
    CircularImageView agentImage;
    String imageURL;
    Button paymentStatus,bookingStatusTag,paymentHistoryBtn;
    RelativeLayout parentLayout;
    View paymentSepLine;
    List<PaymentDetail> mPaymentHistoryList;
    List<Document> mDocumentList;
    UserEndPoint mUserEndPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_property_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button contactBtn = (Button) view.findViewById(R.id.id_contact_button);
        paymentHistoryBtn = (Button) view.findViewById(R.id.id_payment_history_btn);
        Button viewAllBtn = (Button) view.findViewById(R.id.id_view_all_btn);
        parentLayout = (RelativeLayout) view.findViewById(R.id.id_fragment_my_property_details);

        bhkType = (TextView) view.findViewById(R.id.id_bhk_type_value);
        blockRowCardView = (CardView) view.findViewById(R.id.id_block_row_panel);
        block = (TextView) view.findViewById(R.id.id_block_value);
        floorText = (TextView) view.findViewById(R.id.id_floor_text);
        floor = (TextView) view.findViewById(R.id.id_floor_value);
        unitRowCardView = (CardView) view.findViewById(R.id.id_unit_row_panel);
        unit = (TextView) view.findViewById(R.id.id_unit_value);
        agentName = (TextView) view.findViewById(R.id.id_agent_name);
        paymentAmount = (TextView) view.findViewById(R.id.id_paid_money);
        paidVia = (TextView) view.findViewById(R.id.paid_via);
        paymentDate = (TextView) view.findViewById(R.id.id_payment_date);
        paymentStatus = (Button)view.findViewById(R.id.id_payment_status);
        propertyAddress = (TextView) view.findViewById(R.id.property_address);
        projectName = (TextView) view.findViewById(R.id.project_name_my_property);
        paidAmount = (TextView) view.findViewById(R.id.paid_amount);
        paidAmountText = (TextView) view.findViewById(R.id.id_paid_amount_text);
        dueAmount = (TextView) view.findViewById(R.id.due_amount);
        pendingAmountText = (TextView) view.findViewById(R.id.id_pending_amount_text);
        totalAmount = (TextView) view.findViewById(R.id.total_amount);
        totalAmountText = (TextView) view.findViewById(R.id.id_total_amount_text);
        description = (TextView) view.findViewById(R.id.property_description_my_property);
        projectImage = (ImageView) view.findViewById(R.id.project_image_my_property);
        agentImage = (CircularImageView) view.findViewById(R.id.profile_img) ;
        imageCardView = (CardView) view.findViewById(R.id.id_image_cardview);
        bookingStatusTag = (Button) view.findViewById(R.id.booking_status_tag);
        paymentText = (TextView) view.findViewById(R.id.id_payment_text);
        paymentCardView = (CardView) view.findViewById(R.id.id_payment_row_panel);
        paymentSepLine = view.findViewById(R.id.id_sep3);

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickServiceRequest();
            }
        });

        paymentHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPaymentHistory();
            }
        });

        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewAll();
            }
        });
        buildUserWebService();
        fillPropertyDetailsData();
    }

    private void buildUserWebService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }


    private void onClickPaymentHistory(){
        if(!UtilityMethods.isInternetConnected(getActivity())){
            UtilityMethods.showErrorSnackBar(parentLayout,getActivity().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
            return;
        }
        Log.i(TAG, "onClickPaymentHistory: ");
        PaymentHistoryFragment paymentHistoryFragment = new PaymentHistoryFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(paymentHistoryFragment.isAdded()){
            fragmentTransaction.show(paymentHistoryFragment);
        }else {
            fragmentTransaction.replace(R.id.id_fragment_details_container, paymentHistoryFragment, Constants.AppConstants.TAG_PAYMENT_HISTORY_FRAGMENT);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void onClickViewAll(){
        if(!UtilityMethods.isInternetConnected(getActivity())){
            UtilityMethods.showErrorSnackBar(parentLayout,getActivity().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
            return;
        }
        Log.i(TAG, "onClickViewAll: ");
        PropertyDocumentFragment propertyDocumentFragment = new PropertyDocumentFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(propertyDocumentFragment.isAdded()){
            fragmentTransaction.show(propertyDocumentFragment);
        }else {
            fragmentTransaction.replace(R.id.id_fragment_details_container, propertyDocumentFragment, Constants.AppConstants.TAG_PROPERTY_DOCUMENT_FRAGMENT);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void fillPropertyDetailsData(){
        Log.i(TAG, "getMyPropertyDetails");
        try {
            String propertyType = "";
            String propertyArea = "";
            if (getActivity().getIntent().hasExtra("PropertyBookingStatus")
                    && !TextUtils.isEmpty(getActivity().getIntent().getStringExtra("PropertyBookingStatus"))) {
                String bookingStatus = getActivity().getIntent().getStringExtra("PropertyBookingStatus");
                if(bookingStatus.equalsIgnoreCase(Constants.PropertyBookingStatus.Canceled.toString())){
                    bookingStatusTag.setVisibility(View.VISIBLE);
                    bookingStatusTag.setText("CANCELLED");
                    UtilityMethods.setDrawableBackground(getActivity(),bookingStatusTag,R.drawable.booking_status_cancelled_btn_bg);
                    UtilityMethods.setButtonTextColor(getActivity(),bookingStatusTag,R.color.colorWhite);
                }else if(bookingStatus.equalsIgnoreCase(Constants.PropertyBookingStatus.UserRequset.toString())){
                    bookingStatusTag.setVisibility(View.VISIBLE);
                    bookingStatusTag.setText("REQUESTED");
                    UtilityMethods.setDrawableBackground(getActivity(),bookingStatusTag,R.drawable.booking_status_requested_btn_bg);
                    UtilityMethods.setButtonTextColor(getActivity(),bookingStatusTag,R.color.gray_600);
                }else if(bookingStatus.equalsIgnoreCase(Constants.PropertyBookingStatus.Requested.toString())){
                    bookingStatusTag.setVisibility(View.VISIBLE);
                    bookingStatusTag.setText("PROCESSING");
                    UtilityMethods.setDrawableBackground(getActivity(),bookingStatusTag,R.drawable.booking_status_processing_btn_bg);
                    UtilityMethods.setButtonTextColor(getActivity(),bookingStatusTag,R.color.gray_600);
                }else{
                    bookingStatusTag.setVisibility(View.GONE);
                }
            }else{
                bookingStatusTag.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(getActivity().getIntent().getStringExtra("PropertyTypeName"))) {
                if(UtilityMethods.isCommercial(getActivity().getIntent().getStringExtra("PropertyTypeName"))) {
                    propertyType = UtilityMethods.getCommercialTypeName(getActivity().getIntent().getStringExtra("PropertyTypeName"));
                }else{
                    propertyType = getActivity().getIntent().getStringExtra("PropertyTypeName");
                }
            }

            if (!TextUtils.isEmpty(getActivity().getIntent().getStringExtra("PropertyArea"))) {
                propertyArea = getActivity().getIntent().getStringExtra("PropertyArea");
            }

            if (!TextUtils.isEmpty(propertyType) && !TextUtils.isEmpty(propertyArea)) {
                //description.setText(propertyType + ", " + propertyArea + " sq ft.");
                if(UtilityMethods.isCommercialLand(propertyType) || propertyType.toLowerCase().contains("land")){
                    try {
                        description.setText(propertyType + ", " + UtilityMethods.getAreaInYards(Long.parseLong(propertyArea)) + " sq yd.");
                    } catch (Exception e) {
                        description.setText(propertyType + ", " + propertyArea + " sq ft.");
                        e.printStackTrace();
                    }
                }else{
                    description.setText(propertyType + ", " + propertyArea + " sq ft.");
                }
            } else if (!TextUtils.isEmpty(propertyType) && TextUtils.isEmpty(propertyArea)) {
                description.setText(propertyType);
            } else if (TextUtils.isEmpty(propertyType) && !TextUtils.isEmpty(propertyArea)) {
                description.setText(propertyArea);
            } else {
                description.setVisibility(View.GONE);
            }

            String houseNo = getActivity().getIntent().getStringExtra("Unit");
            String blockName = getActivity().getIntent().getStringExtra("BlockName");
            if (!TextUtils.isEmpty(houseNo)
                    && (!TextUtils.isEmpty(blockName))) {

                propertyAddress.setText(houseNo + ", " + blockName);

            } else if (!(TextUtils.isEmpty(houseNo))
                    && (TextUtils.isEmpty(blockName))) {
                propertyAddress.setText(houseNo);

            } else if ((TextUtils.isEmpty(houseNo))
                    && (!TextUtils.isEmpty(blockName))) {
                propertyAddress.setText(blockName);
            }

            projectName.setText(getActivity().getIntent().getStringExtra("PropertyName"));
            bhkType.setText(propertyType);
            if(getActivity().getIntent().getStringExtra("BlockName") != null &&
                    !getActivity().getIntent().getStringExtra("BlockName").isEmpty()) {
                block.setText(getActivity().getIntent().getStringExtra("BlockName"));
            }else{
                blockRowCardView.setVisibility(View.GONE);
            }
            if(getActivity().getIntent().getIntExtra("Floor",0) != 0) {
                floor.setText(getActivity().getIntent().getIntExtra("Floor",0)+"");
            }else{
                floorText.setVisibility(View.GONE);
                floor.setVisibility(View.GONE);
            }
            if(getActivity().getIntent().getStringExtra("Unit") != null &&
                    !getActivity().getIntent().getStringExtra("Unit").isEmpty()) {
                unit.setText(getActivity().getIntent().getStringExtra("Unit"));
            }else{
                unitRowCardView.setVisibility(View.GONE);
            }
            agentName.setText(getActivity().getIntent().getStringExtra("AgentName"));

            long totalAmountValue = 0;
            String totalAmountStr = getActivity().getIntent().getStringExtra("TotalAmount");
            if (!TextUtils.isEmpty(totalAmountStr)) {
                totalAmountValue = Long.parseLong(totalAmountStr);
                totalAmount.setText(" \u20B9 " + getPrice(totalAmountValue));
            }
            final String projectImageURL = getActivity().getIntent().getStringExtra("PropertyImageURL");
            if (!TextUtils.isEmpty(projectImageURL)) {
                Picasso.get().load(projectImageURL)
                        .into(projectImage);
            }
            imageCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!UtilityMethods.isInternetConnected(getActivity())){
                        UtilityMethods.showErrorSnackBar(parentLayout,getActivity().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
                        return;
                    }
                    Intent intent = new Intent(getActivity(), ProjectDetailsScreen.class);
                    long projectId = 0;
                    if(getActivity().getIntent().hasExtra(Constants.IntentKeyConstants.PROJECT_ID)){
                        projectId = getActivity().getIntent().getLongExtra(Constants.IntentKeyConstants.PROJECT_ID, 0);
                    }
                    if (!TextUtils.isEmpty(projectImageURL)) {
                        intent.putExtra(Constants.IntentKeyConstants.PROJECT_COVER_IMAGE, projectImageURL);
                        Bitmap bitmap = UtilityMethods.getBitmap(projectImage);
                        ((HousingApplication) getActivity().getApplicationContext()).setProjectDetailImageBitmap(bitmap,projectId);
                    }
                    if (getActivity().getIntent().hasExtra("PropertyName") &&
                            !TextUtils.isEmpty(getActivity().getIntent().getStringExtra("PropertyName"))) {
                        intent.putExtra(Constants.IntentKeyConstants.PROJECT_NAME,
                                getActivity().getIntent().getStringExtra("PropertyName"));
                    }
                    if (projectId != 0) {
                        intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, projectId);
                    }
                    intent.putExtra("ISDirectCall",true);
                    startActivity(intent);
                }
            });

            long paidAmountValue=0;
            mPaymentHistoryList = ((HousingApplication)getActivity().getApplicationContext()).getPaymentDetailList();
            if(mPaymentHistoryList != null && mPaymentHistoryList.size() >0) {
                for (int i = 0; i < mPaymentHistoryList.size(); i++) {
                    if(!TextUtils.isEmpty(mPaymentHistoryList.get(i).getAmount())) {
                        paidAmountValue += Long.parseLong(mPaymentHistoryList.get(i).getAmount());
                    }
                }
            }

            if(paidAmountValue == 0){
                   paymentText.setVisibility(View.GONE);
                   paymentCardView.setVisibility(View.GONE);
                   paymentHistoryBtn.setVisibility(View.GONE);
                   paymentSepLine.setVisibility(View.GONE);
            }
            paidAmount.setText("₹ " + getPrice(paidAmountValue) + "");
            if (totalAmountValue != 0) {
                dueAmount.setText("\u20B9 " + getPrice(totalAmountValue - paidAmountValue));
                paidAmount.setVisibility(View.VISIBLE);
                totalAmount.setVisibility(View.VISIBLE);
                paidAmountText.setVisibility(View.VISIBLE);
                totalAmountText.setVisibility(View.VISIBLE);
                pendingAmountText.setText("PENDING");
            } else {
                paidAmount.setVisibility(View.GONE);
                totalAmount.setVisibility(View.GONE);
                paidAmountText.setVisibility(View.GONE);
                totalAmountText.setVisibility(View.GONE);
                pendingAmountText.setText("PAID");
                dueAmount.setText("₹ " + getPrice(paidAmountValue) + "");
            }

            if(getActivity().getIntent().hasExtra("DocumentsList")){
                String jsonString = getActivity().getIntent().getStringExtra("DocumentsList");
                Type type = new TypeToken<List<Document>>() {
                }.getType();
                mDocumentList = new Gson().fromJson(jsonString,type);
            }

            Log.i(TAG, "Payment History List before sorting : " + mPaymentHistoryList);
            Collections.sort(mPaymentHistoryList, new Comparator<PaymentDetail>() {
                @Override
                public int compare(PaymentDetail lhs, PaymentDetail rhs) {
                    return -(lhs.getPaymentTime().compareTo(rhs.getPaymentTime()));//latest date first
                }
            });
            Log.i(TAG, "Payment History List after sorting : " + mPaymentHistoryList);
            String paymentMode="CASH";
            if(mPaymentHistoryList != null && mPaymentHistoryList.size() > 0){
                if(mPaymentHistoryList.get(0).getMode() != null){
                    paymentMode = mPaymentHistoryList.get(0).getMode();
                }
                if(mPaymentHistoryList.get(0).getPayed() != null){
                    if(mPaymentHistoryList.get(0).getPayed()){
                        paymentStatus.setText(getString(R.string.paid));
                        UtilityMethods.setTextViewColor(getActivity(),paymentStatus,R.color.paid_button_color);
                        UtilityMethods.setDrawableBackground(getActivity(),paymentStatus,R.drawable.paid_button_selector_bg);
                        paymentDate.setText(getDate(mPaymentHistoryList.get(0).getPaymentTime()));
                        paymentAmount.setText("₹ " + getPrice(paidAmountValue));
                        paidVia.setText(" paid via " + paymentMode.toUpperCase());
                    }else{
                        paymentStatus.setText(getString(R.string.due));
                        UtilityMethods.setTextViewColor(getActivity(),paymentStatus,R.color.red_dark);
                        UtilityMethods.setDrawableBackground(getActivity(),paymentStatus,R.drawable.due_button_selector_bg);
                        if(mPaymentHistoryList.get(0).getDueDate() != null) {
                            paymentDate.setText(getDate(mPaymentHistoryList.get(0).getDueDate()));
                        }else{
                            paymentDate.setText(getDate(mPaymentHistoryList.get(0).getPaymentTime()));
                        }
                        paymentAmount.setText("₹ " + getPrice(paidAmountValue));
                        paidVia.setText(" due Amount ");
                    }
                }else{
                    paymentStatus.setText(getString(R.string.paid).toUpperCase());
                    if(mPaymentHistoryList.get(0).getPaymentTime() != null) {
                        paymentDate.setText(getDate(mPaymentHistoryList.get(0).getPaymentTime()));
                    }
                    paymentAmount.setText("₹ " + getPrice(paidAmountValue));
                    paidVia.setText(" paid via " + paymentMode.toUpperCase());
                };
            }

            final long agentId = getActivity().getIntent().getLongExtra("AgentId",0l);
            final long userId = UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0l);
            new Thread(new Runnable() {
                String errorMsg = "";
                @Override
                public void run() {
                    try {
                        Agent getAgentById = mUserEndPoint.getAgentById(userId,agentId).setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        if(getAgentById != null) {
                            imageURL = getAgentById.getPhoto().getUrl();
                            Log.i(TAG, "Image URL : " + imageURL);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(imageURL)) {
                                        Picasso.get().load(imageURL)
                                                .placeholder(R.drawable.ic_person)
                                                .into(agentImage);
                                        /*Picasso.with(getActivity()).load(imageURL)
                                                .placeholder(R.drawable.ic_person).into(target);*/
                                    } else {
                                        setDefaultImage();
                                    }
                                }
                            });
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        errorMsg = getResources().getString(R.string.network_error_msg);
                    }catch (Exception e) {
                        e.printStackTrace();
                        errorMsg = "Agent image not found";
                    }
                    if(!TextUtils.isEmpty(errorMsg)){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setDefaultImage();
                            }
                        });
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private String getDate(long date) {
        String dayStr = getFormatedDay(date);
        String dateStr = DateFormat.format("MMM yyyy", date).toString();
        String formatedDate = dayStr + " " + dateStr;
        return formatedDate;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        ((MyPropertyDetails)getActivity()).mToolbarText.setText(getResources().getString(R.string.property_details));
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((MyPropertyDetails)getActivity()).mToolbarLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        }else {
            ((MyPropertyDetails)getActivity()).mToolbarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }*/
    }

    private void setDefaultImage(){
        UtilityMethods.loadCircularImageFromPicasso(getActivity(), agentImage);
    }

    public void onClickServiceRequest(){
        if(!UtilityMethods.isInternetConnected(getActivity())){
            UtilityMethods.showErrorSnackBar(parentLayout,getActivity().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
            return;
        }
        Log.i(TAG, "onClickServiceRequest: ");
        ServiceRequestHistoryFragment serviceRequestHistoryFragment = new ServiceRequestHistoryFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(serviceRequestHistoryFragment.isAdded()){
            fragmentTransaction.show(serviceRequestHistoryFragment);
        }else {
            fragmentTransaction.replace(R.id.id_fragment_details_container, serviceRequestHistoryFragment, Constants.AppConstants.TAG_SERVICE_REQUEST_FRAGMENT);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Picasso.with(getActivity()).cancelRequest(target);
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
            TrackAnalytics.trackEvent("MyPropertyDetailsPage", Constants.AppConstants.HOLD_TIME,
                    UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), getActivity());
        }
    }

}
