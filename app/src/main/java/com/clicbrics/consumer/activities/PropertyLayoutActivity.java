package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.TitleSpinnerAdapter;
import com.clicbrics.consumer.customview.DynamicWidthSpinner;
import com.clicbrics.consumer.fragment.PropertyLayoutFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class PropertyLayoutActivity extends BaseActivity implements LoginSuccessCallback {

    private final String TAG = PropertyLayoutActivity.class.getSimpleName();
    //private Spinner spinner;
    private DynamicWidthSpinner spinner;
    private PropertyLayoutFragment propertyLayoutFragment;
    private long propertyId, userId, projectId;
    CoordinatorLayout rootLayout;
    Button callMeBack;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_layout);
        buildRegService();
        initView();

        if(!UtilityMethods.isInternetConnected(this)){
            showNetworkErrorSnackBar();
        }

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
    }

    private void initView() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.property_layout_root);
        callMeBack = (Button) findViewById(R.id.contact_btn_layout_details);

        propertyLayoutFragment = (PropertyLayoutFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        //spinner = (Spinner) findViewById(R.id.spinner_nav);
        spinner = (DynamicWidthSpinner) findViewById(R.id.spinner_nav);

        if (getIntent().hasExtra(Constants.IntentKeyConstants.PROPERTY_TYPE)) {
            selectedRoom = getIntent().getExtras().getInt(Constants.IntentKeyConstants.PROPERTY_TYPE, 0);
        }
        if (getIntent().hasExtra(Constants.IntentKeyConstants.PROPERTY_ID)) {
            propertyId = getIntent().getExtras().getLong(Constants.IntentKeyConstants.PROPERTY_ID, 0);
        }

        if (UtilityMethods.getLongInPref(PropertyLayoutActivity.this, Constants.ServerConstants.USER_ID, -1) != -1) {
            userId = UtilityMethods.getLongInPref(PropertyLayoutActivity.this, Constants.ServerConstants.USER_ID, -1);
        }

        if (getIntent().hasExtra(Constants.IntentKeyConstants.PROJECT_ID)) {
            projectId = getIntent().getLongExtra(Constants.IntentKeyConstants.PROJECT_ID, -1);
        }

        //Log.d("TAG","list->"+ProjectDetailActivity.propertyTypeList.size());
        List<PropertyType> propertyTypes = ((HousingApplication)getApplicationContext()).getPropertyTypeList();
        if (propertyTypes != null) {
            addItemsToSpinner();
        }

        callMeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCallMeBackRequest();
            }
        });

    }

    // add items into spinner dynamically
    private int selectedRoom = 0;
    private int spinnerSelectedPosition = 0;

    public void addItemsToSpinner() {
        ArrayList<String> list = new ArrayList<String>();
        String propertyTypeValue = getIntent().hasExtra("PropertyTypeValue") ? getIntent().getStringExtra("PropertyTypeValue") : "";
        list.add("All");
        List<PropertyType> propertyTypes = ((HousingApplication)getApplicationContext()).getPropertyTypeList();
        for (int i = 0; i < propertyTypes.size(); i++) {
            PropertyType propertyType = propertyTypes.get(i);
            Log.d(TAG, "TYPE -> " + propertyType.getType().toString());
            if (!list.contains("PLOT") && (propertyType.getType().equalsIgnoreCase("LAND"))) {
                list.add("PLOT");
            }else if (!list.contains("1RK Studio") && (propertyType.getType().equalsIgnoreCase("Studio"))) {
                list.add("1RK Studio");
            } else if ((!list.contains(propertyType.getNumberOfBedrooms() + Constants.AppConstants.BHK))
                                && (propertyType.getNumberOfBedrooms()!=0) && !propertyType.getType().equalsIgnoreCase("Studio")){
                /*if(propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentFloor.toString())){
                    list.add(propertyType.getNumberOfBedrooms() + Constants.AppConstants.BHK + " Floor");
                }else {
                    list.add(propertyType.getNumberOfBedrooms() + Constants.AppConstants.BHK);
                }*/
                list.add(propertyType.getNumberOfBedrooms() + Constants.AppConstants.BHK);
                if (propertyType.getNumberOfBedrooms().equals(selectedRoom)) {
                    spinnerSelectedPosition = list.size() - 1;
                }
            } else if(!list.contains(UtilityMethods.getCommercialTypeName(propertyType.getType()).toUpperCase())
                    && UtilityMethods.isCommercial(propertyType.getType())){
                list.add(UtilityMethods.getCommercialTypeName(propertyType.getType()).toUpperCase());
            }
        }
        if(!TextUtils.isEmpty(propertyTypeValue)) {
            if (propertyTypeValue.equalsIgnoreCase("LAND")) {
                spinnerSelectedPosition = list.indexOf("PLOT");
            } else if (list.contains(UtilityMethods.getCommercialTypeName(propertyTypeValue).toUpperCase())){
                spinnerSelectedPosition = list.indexOf(UtilityMethods.getCommercialTypeName(propertyTypeValue).toUpperCase());
            } else if (list.contains("1RK Studio") && propertyTypeValue.equalsIgnoreCase("Studio")){
                spinnerSelectedPosition = list.indexOf("1RK Studio");
            }
        }

        final TextView titleText = findViewById(R.id.id_title);
        final LinearLayout linearLayout = findViewById(R.id.id_spinner_property_type_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });

        TitleSpinnerAdapter spinAdapter = new TitleSpinnerAdapter(
                getApplicationContext(), list);

        spinner.setAdapter(spinAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {

                String item = adapter.getItemAtPosition(position).toString();

                if (item.equalsIgnoreCase("PLOT")) {
                    propertyLayoutFragment.setViewPager("PLOT", propertyId);
                }else if (UtilityMethods.isCommercial(adapter.getSelectedItem().toString())) {
                    propertyLayoutFragment.setViewPager(adapter.getSelectedItem().toString(), propertyId);
                }/*else if (adapter.getSelectedItem().toString().contains("Floor")) {
                    propertyLayoutFragment.setViewPager(item.replaceAll(Constants.AppConstants.BHK + " Floor", ""), propertyId);
                }*/ else {
                    propertyLayoutFragment.setViewPager(item.replaceAll(Constants.AppConstants.BHK, ""), propertyId);
                }
                propertyId = 0;

                titleText.setText(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        Log.i("Positions ", "Positions " + spinnerSelectedPosition + "  " + selectedRoom);
        spinner.setSelection(spinnerSelectedPosition);
        titleText.setText(spinAdapter.getItem(0));
        spinner.setVisibility(View.INVISIBLE);
    }

    @Override
    public void isLoggedin() {
        //setCallMeBack();
        sendIntentForRequest(projectId);
    }


    private UserEndPoint mUserEndPoint;

    private void buildRegService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }

    public void sendIntentForRequest(Long projectId) {
        Intent intent = new Intent(PropertyLayoutActivity.this, FilterActivity.class);
        intent.putExtra(Constants.IntentKeyConstants.CALL_ME_BACK_REQUEST, true);
        intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, projectId);
        startActivity(intent);
    }

    /*private void setCallMeBack() {
        callMeBack.setBackgroundColor(getResources().getColor(R.color.gray));
        callMeBack.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    final CommonResponse callMeBackResponse = mUserEndPoint.callback().setProjectId(projectId).
                            setId(UtilityMethods.getLongInPref(PropertyLayoutActivity.this, Constants.ServerConstants.USER_ID, -1))
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();

                    Log.d(TAG, "bookAvisitResponse->" + callMeBackResponse.getStatus());
                    if (callMeBackResponse.getStatus()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressBar();
                                callMeBack.setBackgroundColor(ContextCompat.getColor(PropertyLayoutActivity.this, R.color.colorAccent));
                                callMeBack.setEnabled(true);
                                if (callMeBackResponse != null) {
                                    Snackbar.make(rootLayout, getString(R.string.thanks_msg), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        errorMsg = callMeBackResponse.getErrorMessage();
                    }
                } catch (IOException e) {
                    errorMsg = getString(R.string.network_error_msg);
//                    AnalyticsTrackers.trackException(e);
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callMeBack.setBackgroundColor(ContextCompat.getColor(PropertyLayoutActivity.this, R.color.colorAccent));
                            callMeBack.setEnabled(true);
                            dismissProgressBar();
                            UtilityMethods.showSnackBar(rootLayout, errmsg, Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }*/

    public void showNetworkErrorSnackBar() {
        final Snackbar snackbar = Snackbar.make(rootLayout, getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.uber_red));
        /*snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });*/
        snackbar.show();
    }

    public void handleCallMeBackRequest(){
        if (!UtilityMethods.isInternetConnected(PropertyLayoutActivity.this)) {
            Snackbar.make(rootLayout, getString(R.string.no_internet_connection), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (UtilityMethods.getLongInPref(PropertyLayoutActivity.this, Constants.ServerConstants.USER_ID, -1) == -1) {
            startActivity(new Intent(PropertyLayoutActivity.this, LoginActivity.class));
            HousingApplication.mLoginSuccessCallback = PropertyLayoutActivity.this;
            //loginFor = LoginFor.CALL_ME_BACK;
        } else {
            //setCallMeBack();
            sendIntentForRequest(projectId);
        }
    }

    long onStartTime;
    @Override
    protected void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TrackAnalytics.trackEvent("PropertyLayoutActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);
    }
}
