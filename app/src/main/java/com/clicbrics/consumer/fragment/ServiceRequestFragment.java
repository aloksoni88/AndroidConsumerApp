package com.clicbrics.consumer.fragment;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.MyPropertyDetails;
import com.clicbrics.consumer.customview.CustomProgressDialog;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.UnknownHostException;


/**
 * Created by Alok on 10-02-2017.
 */

@SuppressWarnings("deprecation")
public class ServiceRequestFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = ServiceRequestFragment.class.getSimpleName();

    LinearLayout mSpinnerLayout;
    Spinner mSpinner;
    Button mSubmitButton;
    EditText mComments;
    RelativeLayout mParentLayout;
    UserEndPoint mUserEndPoint;
    CustomProgressDialog progressDialog;
    Handler mHanlder;
    String serviceReqType = "";
    static int mSelectedPos = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_request,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSpinnerLayout = view.findViewById(R.id.id_spinner_layout);
        mSpinner = (Spinner) view.findViewById(R.id.id_spinner_type);
        mSubmitButton = (Button) view.findViewById(R.id.id_service_request_submit_btn);
        mParentLayout = (RelativeLayout) view.findViewById(R.id.id_fragment_service_request);
        mComments = (EditText) view.findViewById(R.id.comments);
        progressDialog = new CustomProgressDialog(getActivity());
        mHanlder = new Handler();
        mSpinner.setOnItemSelectedListener(this);
        mSpinnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpinner.performClick();
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubmitButton();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.service_request_list, R.layout.spinner_service_request_layout);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_text_layout);
        mSpinner.setAdapter(adapter);
        buildUserWebService();
    }

    private void buildUserWebService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        ((MyPropertyDetails)getActivity()).mToolbarText.setText(getResources().getString(R.string.new_service_request));
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Log.i(TAG, "onItemSelected: ");
        mSelectedPos = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i(TAG, "onNothingSelected: ");
    }

    private void onClickSubmitButton(){
        Log.i(TAG, "onClick: Submit Button");
        if (!UtilityMethods.isInternetConnected(getActivity())) {
            Snackbar snackbar = Snackbar
                    .make(mParentLayout, getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction("TRY AGAIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onClickSubmitButton();
                        }
                    });

            snackbar.setActionTextColor(Color.WHITE);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));

            snackbar.show();

            return;
        }
        if(mSpinner.getSelectedItemPosition() > 0 && !TextUtils.isEmpty(mComments.getText().toString().trim())){
            progressDialog.show();
            if(mSelectedPos == 1){
                serviceReqType = Constants.ServiceRequestType.Booking.toString();
            }else if(mSelectedPos == 2){
                serviceReqType = Constants.ServiceRequestType.Cancellation.toString();
            }else if(mSelectedPos == 3){
                serviceReqType = Constants.ServiceRequestType.Transfer.toString();
            }else if(mSelectedPos == 4){
                serviceReqType = Constants.ServiceRequestType.ConstructionUpdate.toString();
            }else if(mSelectedPos == 5){
                serviceReqType = Constants.ServiceRequestType.Other.toString();;
            }
            final long userId = UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, 0l);
            new Thread(new Runnable() {
                String errMsg = "";
                @Override
                public void run() {
                    try {
                        UserEndPoint.ServiceRequest serviceRequest = mUserEndPoint.serviceRequest(userId, Long.valueOf(0));
                        if(getActivity() != null && getActivity().getIntent().hasExtra("PropertyId")){
                            serviceRequest.setBookingId(getActivity().getIntent().getLongExtra("PropertyId",0l));
                        }
                        serviceRequest.setServiceRequestType(serviceReqType);
                        serviceRequest.setDescription(mComments.getText().toString());
                        CommonResponse commonResponse = serviceRequest.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        if(commonResponse.getStatus()){
                            mHanlder.post(new Runnable() {
                                @Override
                                public void run() {
                                    if ((progressDialog != null) && (progressDialog.isShowing())) {
                                        progressDialog.hide();
                                    }
                                    showConfirmationMessage();
                                }
                            });
                        }else{
                            if(!TextUtils.isEmpty(commonResponse.getErrorMessage())) {
                                errMsg = commonResponse.getErrorMessage();
                            }else{
                                errMsg = "Could not raise service request.\nPlease try again!";
                            }
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        errMsg = getResources().getString(R.string.network_error_msg);
                    }catch (Exception e) {
                        e.printStackTrace();
                        errMsg = "Could not raise service request.\nPlease try again!";
                    }
                    if(!TextUtils.isEmpty(errMsg)){
                        mHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                if ((progressDialog != null) && (progressDialog.isShowing())) {
                                    progressDialog.hide();
                                }
                                UtilityMethods.showSnackBar(mParentLayout,errMsg, Snackbar.LENGTH_SHORT);
                            }
                        });
                    }
                }
            }).start();
        }else if(mSpinner.getSelectedItemPosition() == 0){
            UtilityMethods.showSnackBar(mParentLayout,getString(R.string.select_service_type_err_msg),Snackbar.LENGTH_SHORT);
        }else if(TextUtils.isEmpty(mComments.getText())){
            UtilityMethods.showSnackBar(mParentLayout,getString(R.string.comments_empty_err_msg),Snackbar.LENGTH_SHORT);
        }
    }

    private void showConfirmationMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.confirm));
        builder.setMessage(R.string.service_request_confimation_msg);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment serviceRequestFragment = getFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_SERVICE_REQUEST_FRAGMENT);
                if(serviceRequestFragment != null && serviceRequestFragment.isVisible()){
                    FragmentManager fm = getFragmentManager();
                    fm.popBackStack();
                }
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            }
        });
        alertDialog.show();
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
            TrackAnalytics.trackEvent("NewServiceRequestScreen", Constants.AppConstants.HOLD_TIME,
                    UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), getActivity());
        }
    }
}
