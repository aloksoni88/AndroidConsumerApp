package com.clicbrics.consumer.clicworth;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.buy.housing.backend.loanEndPoint.model.LoanLead;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.ExploreNeighbourhood;
import com.clicbrics.consumer.databinding.EstimateLayoutBinding;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.FilterUtility;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.adapter.HomeProjectListAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;


public class GetEstimateActivity extends BaseActivity implements EstimateCallback, OnMapReadyCallback, LoginSuccessCallback {

    private static final String TAG = "GetEstimateActivity";

    private EstimateLayoutBinding binding;
    private Dialog feedbackDialog;
    private EstimatedPropertyViewModel viewModel;
    private LatLng mProjectLatlng;
    private final int OFFSET_DIFF = 15;
    private double factor = 0.65, downpayment, eligibleLoanAmount, grossincome;
    float rateofinterest = 8;
    private int tenure = 30 * 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_estimate);
        binding = DataBindingUtil.setContentView(this, R.layout.estimate_layout);

        EstimatedPropertyModel model = new EstimatedPropertyModel();
        if (getIntent().hasExtra("estimatedResult")) {
            String estimatedResult = getIntent().getStringExtra("estimatedResult");
            model = new Gson().fromJson(estimatedResult, EstimatedPropertyModel.class);

        }

        viewModel = new EstimatedPropertyViewModel(this, model);
        binding.setItem(viewModel);
        selPropUnit = UtilityMethods.getSelectedUnit(GetEstimateActivity.this);
        registerReceiver(updateFavorite, new IntentFilter(Constants.BroadCastConstants.FAVORITE_CHANGE));
        setValue();
        estimatevalue();
        setupSeekBar();
        manageClicks();
//        if(mProjectName != null && mProjectLatlng != null)
        {
            setExploreArea();
        }

       /* binding.txtEditDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setResult(RESULT_OK);
                finish();
            }
        });*/
/*        binding.btnEstimateAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });*/

    }

    private String selPropUnit = "";
    private double propert_cost;
    private double loan_amount;

    private void setValue() {
        String bhk = "", floor = "", size = "";

        if (viewModel.getEstimatedPropertyModel().getAverate_rate() > 0) {
            propert_cost = viewModel.getEstimatedPropertyModel().getAverate_rate();
            binding.idNotifyLayout.amount.setText(UtilityMethods.getPriceWordWithoutSymbolofDifferentSize((long) viewModel.getEstimatedPropertyModel().getAverate_rate()));
            binding.idNotifyLayout.amount1.setText(UtilityMethods.getPriceWordWithoutSymbolofDifferentSize((long) viewModel.getEstimatedPropertyModel().getAverate_rate()));
            binding.idAffordabilityLayout.propertyCost.setText(UtilityMethods.getPriceWordWithoutSymbolofDifferentSize((long) viewModel.getEstimatedPropertyModel().getAverate_rate()));
        }

        if (!TextUtils.isEmpty(viewModel.getEstimatedPropertyModel().getEstimatePropertyType())) {
            binding.idNotifyLayout.propertytype.setText(viewModel.getEstimatePropertyType());
            binding.idNotifyLayout.propertytype1.setText(viewModel.getEstimatePropertyType());
            filterPropertyType = viewModel.getEstimatedPropertyModel().getEstimatePropertyType();
        }

        Log.i(TAG, "setValue :===============" + viewModel.getEstimatedPropertyModel().getPropertyAddress());
        if (!TextUtils.isEmpty(viewModel.getEstimatedPropertyModel().getPropertyAddress())) {
            binding.idNotifyLayout.address.setText(viewModel.getEstimatedPropertyModel().getPropertyAddress());
            binding.idNotifyLayout.address1.setText(viewModel.getEstimatedPropertyModel().getPropertyAddress());
        }

        if (!TextUtils.isEmpty(viewModel.getEstimatedPropertyModel().getNoOfRoom())) {
            bhk = viewModel.getEstimatedPropertyModel().getNoOfRoom() + ", ";
            filterBedType = String.valueOf(viewModel.getEstimatedPropertyModel().getBed());
        }
        if (!TextUtils.isEmpty(viewModel.getEstimatedPropertyModel().getSelectedFloor())) {
            floor = viewModel.getEstimatedPropertyModel().getSelectedFloor() + ", ";
        }

        if (!TextUtils.isEmpty(viewModel.getEstimatedPropertyModel().getPropertySize())) {
            size = viewModel.getEstimatedPropertyModel().getPropertySize();
        }

        String maxmin_values = viewModel.getEstimatedPropertyModel().getPropertyPriceRange();
        SpannableString maxmin_values_span = new SpannableString(maxmin_values);
//        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
//        maxmin_values_span.setSpan(new RelativeSizeSpan(0.5f), 0,1, 0);
//        maxmin_values_span.setSpan(new RelativeSizeSpan(0.5f), 0,maxmin_values.length(), 0);
        maxmin_values_span.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, maxmin_values.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.idNotifyLayout.maxMinPrice.setText(maxmin_values_span);
        binding.idNotifyLayout.maxMinPrice1.setText(maxmin_values_span);


        String bsp_values = "BSP " + viewModel.getEstimatedPropertyModel().getPropertyBsp();
        SpannableString txtSpannable = new SpannableString(bsp_values);
//        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        txtSpannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 3, bsp_values.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.idNotifyLayout.bspPrice.setText(txtSpannable);
        binding.idNotifyLayout.bspPrice1.setText(txtSpannable);
        binding.idNotifyLayout.bhkFloorSize.setText(bhk + floor + size);
        binding.idNotifyLayout.bhkFloorSize1.setText(bhk + floor + size);
        if (viewModel.getEstimatedPropertyModel().getLatitude() != 0 && viewModel.getEstimatedPropertyModel().getLongitude() != 0) {
            getProjectListByDistance(viewModel.getEstimatedPropertyModel().getLatitude(), viewModel.getEstimatedPropertyModel().getLongitude());
            mProjectLatlng = new LatLng(viewModel.getEstimatedPropertyModel().getLatitude(),
                    viewModel.getEstimatedPropertyModel().getLongitude());
        }

    }

    //filter variables
    private String filterPropertyType = null;
    private String filterBedType = null;
    private String filterProjectStatus = null;
    private Integer filterMinPrice = null;
    private Integer filterMaxPrice = null;
    private Integer filterMinArea = null;
    private Integer filterMaxArea = null;
    private String filterSort = null;
    private Long requestId=null;
    private String searchText=null;
    private Boolean getSummary=true;
    private String city=null;
    private String appVersion=null;
    private Long userId=null;
    private String userEmail=null;
    private String userPhone=null;
    private String userName=null;
    private Long virtualId=null;
    private String leadsource=null;
    private String browser=null;
    private String browserVersion=null;
    private String browserType=null;

    private final int INIT_OFFSET = 0;
    private final int INIT_LIMIT = 15;
    private String is_monthlyincome, is_loantenure, is_otheremi, is_intrestrate;
    private boolean isChanged = false;
//    private float is_intrestrate;


    /**
     * Method to set the filter parameters(parameters of API)
     */
    private void setFilterVariables() {
        FilterUtility utility = new FilterUtility(GetEstimateActivity.this);
//        filterProjectStatus = utility.getFilterProjectStatus();
//        filterPropertyType = utility.getFilterPropertyType();
//        filterBedType = utility.getFilterBedRoom();
//        filterMinPrice = utility.getFilterMinPrice();
//        filterMaxPrice = utility.getFilterMaxPrice();

        Log.i(TAG, "filterProjectStatus : " + filterProjectStatus);
        Log.i(TAG, "filterPropertyType : " + filterPropertyType);
        Log.i(TAG, "filterBedType : " + filterBedType);
        Log.i(TAG, "filterMinPrice : " + filterMinPrice);
        Log.i(TAG, "filterMaxPrice : " + filterMaxPrice);

        if(getIntent().hasExtra("propertyAddress")&&!TextUtils.isEmpty(getIntent().getStringExtra("propertyAddress")))
        {
            searchText=getIntent().getStringExtra("propertyAddress");
        }

        city=UtilityMethods.getStringInPref(GetEstimateActivity.this,Constants.AppConstants.SAVED_CITY,"");
        appVersion=String.valueOf(UtilityMethods.getAppVersionUtility(GetEstimateActivity.this));
        if(UtilityMethods.getLongInPref(GetEstimateActivity.this, Constants.ServerConstants.USER_ID, -1)!=-1) {
            userId = UtilityMethods.getLongInPref(GetEstimateActivity.this, Constants.ServerConstants.USER_ID, -1);
        }
        if(!TextUtils.isEmpty(UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.ServerConstants.EMAIL, "")))
        {
            userEmail=UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.ServerConstants.EMAIL, "");

        }
        if(!TextUtils.isEmpty(UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.ServerConstants.MOBILE, "")))

        {
            userPhone=UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.ServerConstants.MOBILE, "");

        }
        if(!TextUtils.isEmpty(UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.ServerConstants.NAME, "")))

        {
            userName=UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.ServerConstants.NAME, "");
        }

        virtualId=UtilityMethods.getLongInPref(GetEstimateActivity.this,Constants.AppConstants.VIRTUAL_UID,-1);
        leadsource=Constants.LeadSource.AndroidApp.toString();


    }

    /**
     * Get Project list by distance(search)
     *
     * @param latitude  - latitude
     * @param longitude - longitude
     */
    public void getProjectListByDistance(double latitude, double longitude) {

        long cityId = UtilityMethods.getLongInPref(GetEstimateActivity.this, Constants.AppConstants.SAVED_CITY_ID, -1);
//        setFilterVariables();
        showProgressBar();
        setFilterVariables();
        viewModel.getProjectListByDistance(latitude, longitude, null, INIT_OFFSET, INIT_LIMIT, filterPropertyType, filterBedType, filterProjectStatus, filterMinPrice,
                filterMaxPrice, filterMinArea, filterMaxArea, filterSort
                ,requestId,appVersion,userId,userEmail,userPhone,userName,
                virtualId,leadsource,browser,browserVersion,browserType,city,searchText);
    }

    private void estimatevalue() {

        if(!TextUtils.isEmpty(viewModel.getEstimatedPropertyModel().getEstimatePropertyType())) {
            calculatedownpayment();

          /*  if (!viewModel.getEstimatedPropertyModel().getEstimatePropertyType().equalsIgnoreCase("Plot")) {
                eligibleLoanAmount = (propert_cost * 80) / 100;
            } else {
                eligibleLoanAmount = (propert_cost * 60) / 100;
            }


//        eligibleLoanAmount = (propert_cost * 60) / 100;
            downpayment = propert_cost - eligibleLoanAmount;*/

            rateofinterest = 8;
            float rate = rateofinterest / 100 / 12;

            double z = (1 - (1 / Math.pow(1 + rate, tenure)));

            eligibleLoanAmount = propert_cost - downpayment;
            grossincome = (((eligibleLoanAmount * rate) / z) + 0) / factor;
            Log.i(TAG, "estimatevalue: grossincome=====" + grossincome + "down: " + downpayment + " prop:" + propert_cost);
            binding.idAffordabilityLayout.eligibleCost.setText(UtilityMethods.getPriceWordWithoutSymbolofDifferentSize((long) eligibleLoanAmount));
        }
    }

    private void calculatedownpayment() {
        double factor = 0.2;
        if (propert_cost <= 1800000) {
            factor = 0.1;
        } else if( propert_cost <= 7500000 ){
            factor = 0.2;
        } else {
            factor = 0.25;
        }
        //(propertyCost * factor)
        downpayment = (propert_cost * factor);
    }

    long mLastRequestTime;

    private void setupSeekBar() {
        binding.idAffordabilityLayout.downpayment.setProgress((int) downpayment / 10000);
        binding.idAffordabilityLayout.intrestRate.setProgress((int) rateofinterest * 10);
        binding.idAffordabilityLayout.intrestRatePeriod.setText(String.valueOf((float) (8)));
        binding.idAffordabilityLayout.monthlyIncome.setProgress((int) grossincome / 5000);
        binding.idAffordabilityLayout.loanTenure.setProgress(30);
        binding.idAffordabilityLayout.loanTenureAmount.setText(String.valueOf(30));
        binding.idAffordabilityLayout.monthlyIncomeAmount.setText(String.valueOf((int) grossincome));
        binding.idAffordabilityLayout.downpaymntamount.setText(String.valueOf((int) downpayment));


        binding.idAffordabilityLayout.downpaymntamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    if (isChanged) {
                        isChanged = false;
                        return;
                    }
                    isChanged = true;

                    assigndefaultvalue();
                    Log.i(TAG, "afterTextChanged: down payment");
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 50) {
                        String amount = binding.idAffordabilityLayout.downpaymntamount.getText().toString();
//                    binding.idAffordabilityLayout.downpaymntamount.setSelection(amount.length());
                        double amount_int = Double.parseDouble(amount);
                        if (amount_int >= 10000 && amount_int <= 10000000) {
                            binding.idAffordabilityLayout.downpayment.setProgress((int) amount_int / 10000);
                        } else if (amount_int > 10000000) {
                            binding.idAffordabilityLayout.downpayment.setProgress((int) 10000000 / 10000);
                            binding.idAffordabilityLayout.downpaymntamount.setText("10000000");
                        }
                        calculateeligibiltiyamount();
                    }
                    isChanged = false;
                    mLastRequestTime = System.currentTimeMillis();
                } else {

                }

            }
        });
        binding.idAffordabilityLayout.downpayment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                Log.i(TAG, "onProgressChanged: ==== " + i);
                if (isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
                binding.idAffordabilityLayout.downpaymntamount.setText(String.valueOf(i * 10000));
                binding.idAffordabilityLayout.downpaymntamount.setSelection(binding.idAffordabilityLayout.downpaymntamount
                        .getText().toString().trim().length());

                calculateeligibiltiyamount();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
              /*  binding.idAffordabilityLayout.downpaymntamount.setText(is_downpayment);
                binding.idAffordabilityLayout.downpaymntamount.setSelection(binding.idAffordabilityLayout.downpaymntamount
                        .getText().toString().trim().length());*/
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              /*  binding.idAffordabilityLayout.downpaymntamount.setText(is_downpayment);
                binding.idAffordabilityLayout.downpaymntamount.setSelection(binding.idAffordabilityLayout.downpaymntamount
                        .getText().toString().trim().length());*/
            }
        });

        binding.idAffordabilityLayout.monthlyIncomeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "Monthly afterTextChanged: isChanged " + isChanged);
                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    if (isChanged) {
                        isChanged = false;
                        return;
                    }
                    isChanged = true;
                    assigndefaultvalue();
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 50) {
                        String amount = binding.idAffordabilityLayout.monthlyIncomeAmount.getText().toString();
//                    binding.idAffordabilityLayout.monthlyIncomeAmount.setSelection(amount.length());
                        double amount_int = Double.parseDouble(amount);
                        if (amount_int >=10000 && amount_int <= 2000000) {
                            double amt_value = amount_int / 5000;
                            binding.idAffordabilityLayout.monthlyIncome.setProgress((int) amt_value);
                        } else if (amount_int > 2000000) {
                            binding.idAffordabilityLayout.monthlyIncome.setProgress(2000000 / 5000);
                            binding.idAffordabilityLayout.monthlyIncomeAmount.setText("2000000");
                        }
                        calculateeligibiltiyamount();
                    }
                    isChanged = false;
                    mLastRequestTime = System.currentTimeMillis();
                }
            }
        });
        binding.idAffordabilityLayout.monthlyIncome.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
                binding.idAffordabilityLayout.monthlyIncomeAmount.setText(String.valueOf(i * 5000));
                binding.idAffordabilityLayout.monthlyIncomeAmount.setSelection(binding.idAffordabilityLayout.
                        monthlyIncomeAmount.getText().toString().trim().length());

                calculateeligibiltiyamount();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        binding.idAffordabilityLayout.intrestRatePeriod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().trim())) {
                    if (charSequence.subSequence(0, 1).equals(".")) ;
                    {
//                        binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().replace(".","0");
                        assigndefaultvalue();
//                        binding.idAffordabilityLayout.intrestRatePeriod.setText("");
                    }

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {

                    Log.i(TAG, "Rate of intrest afterTextChanged: " + isChanged);
                    if (isChanged) {
                        isChanged = false;
                        return;
                    }
                    isChanged = true;
                    assigndefaultvalue();
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 50) {
                        if (!binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().equalsIgnoreCase(".")
                        ||!binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().equalsIgnoreCase("0")) {

                            String amount = binding.idAffordabilityLayout.intrestRatePeriod.getText().toString();
//                    binding.idAffordabilityLayout.intrestRatePeriod.setSelection(amount.length());

                            float amount_int = Float.parseFloat(amount);
                            if (amount_int >=1.0 && amount_int <= 15.0) {
                                binding.idAffordabilityLayout.intrestRate.setProgress((int) amount_int * 10);
                            } else if (amount_int < 1) {
                                binding.idAffordabilityLayout.intrestRate.setProgress(1);
                                binding.idAffordabilityLayout.intrestRatePeriod.setText("8");
                            } else if (amount_int > 15.0) {
                                binding.idAffordabilityLayout.intrestRate.setProgress((int) 15.0 * 10);
                                binding.idAffordabilityLayout.intrestRatePeriod.setText("15.0");
                            }
                            calculateeligibiltiyamount();
                        }

                    }
                    isChanged = false;
                    mLastRequestTime = System.currentTimeMillis();
                }


            }
        });
        binding.idAffordabilityLayout.intrestRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                Log.i(TAG, "Affordability onProgressChanged: ====" + ((float) i / 10));
//                String.format("%.2f", (float) minPrice / (float) cr)
//                rateofinterest = (float) (i*10);
                rateofinterest = (float) ((float) i / 10);
                if (isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
                Log.i(TAG, "onProgressChanged: intrestRate==== " + rateofinterest);

                if (rateofinterest >= 1.0 && rateofinterest <= 15.00) {
                    binding.idAffordabilityLayout.intrestRatePeriod.setText(String.valueOf((float) (rateofinterest)));
                    binding.idAffordabilityLayout.intrestRatePeriod.setSelection(binding.idAffordabilityLayout.
                            intrestRatePeriod.getText().toString().trim().length());
                }


                calculateeligibiltiyamount();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        binding.idAffordabilityLayout.loanTenureAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    if (isChanged) {
                        isChanged = false;
                        return;
                    }
                    isChanged = true;
                    assigndefaultvalue();
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 50) {
                        String amount = binding.idAffordabilityLayout.loanTenureAmount.getText().toString();
//                    binding.idAffordabilityLayout.loanTenureAmount.setSelection(amount.length());
                        int amount_int = Integer.parseInt(amount);
                        if (amount_int >= 1 && amount_int <= 30) {
                            binding.idAffordabilityLayout.loanTenure.setProgress(amount_int);
                        }
                        if (amount_int > 30) {

                            binding.idAffordabilityLayout.loanTenure.setProgress(30);
                            binding.idAffordabilityLayout.loanTenureAmount.setText("30");
                        }
                        calculateeligibiltiyamount();
                    }
                    isChanged = false;
                    mLastRequestTime = System.currentTimeMillis();
                }
            }
        });
        binding.idAffordabilityLayout.loanTenure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
                binding.idAffordabilityLayout.loanTenureAmount.setText(String.valueOf(i));
                binding.idAffordabilityLayout.loanTenureAmount.setSelection(binding.idAffordabilityLayout.
                        loanTenureAmount.getText().toString().trim().length());
                calculateeligibiltiyamount();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        binding.idAffordabilityLayout.emiAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "afterTextChanged: isChanged " + isChanged);
                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    if (isChanged) {
                        isChanged = false;
                        return;
                    }
                    isChanged = true;
                    assigndefaultvalue();
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 50) {
                        String amount = binding.idAffordabilityLayout.emiAmount.getText().toString();
//                    binding.idAffordabilityLayout.emiAmount.setSelection(amount.length());
                        double amount_int = Double.parseDouble(amount);
                        if (amount_int >= 5000 && amount_int <= 2000000) {
                            binding.idAffordabilityLayout.otherEmi.setProgress((int) amount_int / 5000);
                        }
                        if (amount_int > 2000000) {
                            binding.idAffordabilityLayout.otherEmi.setProgress((int) 2000000 / 5000);
                            binding.idAffordabilityLayout.emiAmount.setText("2000000");
                        }
                        calculateeligibiltiyamount();
                    }
                    isChanged = false;
                    mLastRequestTime = System.currentTimeMillis();
                }

            }
        });
        binding.idAffordabilityLayout.otherEmi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
                binding.idAffordabilityLayout.emiAmount.setText(String.valueOf(i * 5000));
                binding.idAffordabilityLayout.emiAmount.setSelection(binding.idAffordabilityLayout.
                        emiAmount.getText().toString().trim().length());
                calculateeligibiltiyamount();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void assigndefaultvalue() {

        if (TextUtils.isEmpty(binding.idAffordabilityLayout.downpaymntamount.getText().toString().trim())) {
            binding.idAffordabilityLayout.downpaymntamount.setText("10000");
        }

        if (TextUtils.isEmpty(binding.idAffordabilityLayout.monthlyIncomeAmount.getText().toString().trim())) {
            binding.idAffordabilityLayout.monthlyIncomeAmount.setText("10000");
        }

        if (TextUtils.isEmpty(binding.idAffordabilityLayout.loanTenureAmount.getText().toString().trim()) ||
                binding.idAffordabilityLayout.loanTenureAmount.getText().toString().trim().equalsIgnoreCase("0")) {
            binding.idAffordabilityLayout.loanTenureAmount.setText("30");
        }
        if (TextUtils.isEmpty(binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().trim())) {
            binding.idAffordabilityLayout.intrestRatePeriod.setText("8");
        }
        if (binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().trim().equalsIgnoreCase("0") ||
                binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().trim().equalsIgnoreCase("0.0") ||
                binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().trim().equalsIgnoreCase(".0")
                || binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().trim().equalsIgnoreCase(".")) {
            binding.idAffordabilityLayout.intrestRatePeriod.setText("8");
        }
        if (TextUtils.isEmpty(binding.idAffordabilityLayout.emiAmount.getText().toString().trim())) {
            binding.idAffordabilityLayout.emiAmount.setText("0");
        }
    }


    private void calculateeligibiltiyamount() {
        factor=0.65;
        grossincome = Double.parseDouble(binding.idAffordabilityLayout.monthlyIncomeAmount.getText().toString().trim());
        rateofinterest = Float.parseFloat(binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().trim());
        double otheremi = Double.parseDouble(binding.idAffordabilityLayout.emiAmount.getText().toString().trim());
        tenure = Integer.parseInt(binding.idAffordabilityLayout.loanTenureAmount.getText().toString().trim());
        downpayment = Double.parseDouble(binding.idAffordabilityLayout.downpaymntamount.getText().toString().trim());
        Log.i(TAG, "calculateeligibiltiyamount rateofinterest: " + rateofinterest);
        Log.i(TAG, "calculateeligibiltiyamount otheremi: " + otheremi);
        Log.i(TAG, "calculateeligibiltiyamount tenure: " + tenure);
        Log.i(TAG, "calculateeligibiltiyamount downpayment: " + downpayment);
        Log.i(TAG, "calculateeligibiltiyamount grossincome: " + grossincome);
        double loanAmount1, loanAmount2;
        double grossIncome_factor = grossincome * factor;
        if (grossIncome_factor < otheremi) {
            binding.idAffordabilityLayout.propertyCost.setText("0.0");
            binding.idAffordabilityLayout.eligibleCost.setText("0.0");
//            Toast.makeText(GetEstimateActivity.this, "Your are not eligible for loan", Toast.LENGTH_SHORT).show();
        } else {
            double hlemi = grossIncome_factor - otheremi;
            loanAmount1 = loanAmount1(rateofinterest / 100 / 12, tenure * 12, hlemi);
            loanAmount2 = loanAmount2(downpayment);
            eligibleLoanAmount = loanAmount1;
            if (loanAmount2 < loanAmount1) {
                eligibleLoanAmount = loanAmount2;
            }

            double propertyCost = eligibleLoanAmount + downpayment;

            Log.i(TAG, "calculateeligibiltiyamount: =========" + eligibleLoanAmount + ";" + propertyCost);
            binding.idAffordabilityLayout.propertyCost.setText(UtilityMethods.getPriceWordWithoutSymbolofDifferentSize((long) propertyCost));
            binding.idAffordabilityLayout.eligibleCost.setText(UtilityMethods.getPriceWordWithoutSymbolofDifferentSize((long) eligibleLoanAmount));

        }

    }


    private double loanAmount2(double downpayment) {
        double loanAmount2;
        if (downpayment <= 200000) {
            loanAmount2 = (downpayment / 0.1) - downpayment;
        } else if (downpayment <= 1875000) {
            loanAmount2 = (downpayment / 0.2) - downpayment;
        } else {
            loanAmount2 = (downpayment / 0.25) - downpayment;
        }
        return loanAmount2;

    }

    private double loanAmount1(float rate, int nper, double pmt) {
        return pmt / rate * (1 - (1 / Math.pow(1 + rate, nper)));

    }


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3456;

    public boolean checkPermissionEXTERNAL_STORAGE() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(GetEstimateActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(GetEstimateActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        GetEstimateActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(
                        GetEstimateActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("External storage");

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    GetEstimateActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GetEstimateActivity.this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(GetEstimateActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (ActivityCompat.checkSelfPermission(GetEstimateActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(GetEstimateActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                convertlayouttobitmap();
            } else {
                Toast.makeText(GetEstimateActivity.this, "Permission Denied",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private void manageClicks() {

        binding.idAffordabilityCordlayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = binding.idAffordabilityCordlayout.getRootView().getHeight() - binding.idAffordabilityCordlayout.getHeight();

                if (heightDiff > dpToPx(GetEstimateActivity.this, 200)) {

                    Log.i(TAG, "onGlobalLayout:===== keyboard opened");
                    showeditcursor();
                } else {
                    Log.i(TAG, "onGlobalLayout:===== keyboard closed");
                    assigndefaultvalue();
                    hideeditcursor();
                }
            }
        });

        binding.idAffordabilityLayout.downpaymntamount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                binding.idAffordabilityLayout.downpaymntamount.setCursorVisible(true);
                return false;
            }
        });
        binding.idAffordabilityLayout.monthlyIncomeAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                binding.idAffordabilityLayout.monthlyIncomeAmount.setCursorVisible(true);
                return false;
            }
        });
        binding.idAffordabilityLayout.loanTenureAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                binding.idAffordabilityLayout.loanTenureAmount.setCursorVisible(true);
                return false;
            }
        });
        binding.idAffordabilityLayout.intrestRatePeriod.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                binding.idAffordabilityLayout.intrestRatePeriod.setCursorVisible(true);
                return false;
            }
        });
        binding.idAffordabilityLayout.emiAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                binding.idAffordabilityLayout.emiAmount.setCursorVisible(true);
                return false;
            }
        });
        binding.header.backbutton.setVisibility(View.VISIBLE);
        binding.header.backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.idNotifyLayout.btnFeedback.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                openFeedBackDialog();
            }
        });

        binding.idNotifyLayout.share.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
//                openFeedBackDialog();

                if (checkPermissionEXTERNAL_STORAGE()) {
                    convertlayouttobitmap();
                }


            }
        });
        binding.idNotifyLayout.notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityMethods.isInternetConnected(GetEstimateActivity.this)) {
                    if (UtilityMethods.getLongInPref(GetEstimateActivity.this, Constants.ServerConstants.USER_ID, -1) > 0) {
                        showProgressBar();
                        viewModel.NotifyPriceChange(UtilityMethods.getLongInPref(GetEstimateActivity.this, Constants.ServerConstants.USER_ID, -1));
                    } else {
                        HousingApplication.mLoginSuccessCallback = GetEstimateActivity.this;
                        //favoritedPosition = position;
                        startActivity(new Intent(GetEstimateActivity.this, LoginActivity.class));
                    }
                }

            }
        });
        binding.idAffordabilityLayout.requestplanloan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityMethods.isInternetConnected(GetEstimateActivity.this)) {
                    if (UtilityMethods.getLongInPref(GetEstimateActivity.this, Constants.ServerConstants.USER_ID, -1) > 0) {
                        addValuestoappyLoan();

                    } else {
                        HousingApplication.mLoginSuccessCallback = GetEstimateActivity.this;
                        //favoritedPosition = position;
                        startActivity(new Intent(GetEstimateActivity.this, LoginActivity.class));
                    }
                }


            }
        });

    /*    binding.exploreLayout.taptoexplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapExploreActivity();
            }
        });*/
    }

    private void showeditcursor() {

        if (binding.idAffordabilityLayout.downpaymntamount.isInTouchMode()) {
            binding.idAffordabilityLayout.downpaymntamount.setCursorVisible(true);
        }

        if (binding.idAffordabilityLayout.monthlyIncomeAmount.isInTouchMode()) {
            binding.idAffordabilityLayout.monthlyIncomeAmount.setCursorVisible(true);
        }
        if (binding.idAffordabilityLayout.loanTenureAmount.isInTouchMode()) {
            binding.idAffordabilityLayout.loanTenureAmount.setCursorVisible(true);
        }
        if (binding.idAffordabilityLayout.intrestRatePeriod.isInTouchMode()) {
            binding.idAffordabilityLayout.intrestRatePeriod.setCursorVisible(true);
        }
        if (binding.idAffordabilityLayout.emiAmount.isInTouchMode()) {
            binding.idAffordabilityLayout.emiAmount.setCursorVisible(true);
        }

    }

    private void hideeditcursor() {
        binding.idAffordabilityLayout.downpaymntamount.setCursorVisible(false);
        binding.idAffordabilityLayout.monthlyIncomeAmount.setCursorVisible(false);
        binding.idAffordabilityLayout.loanTenureAmount.setCursorVisible(false);
        binding.idAffordabilityLayout.intrestRatePeriod.setCursorVisible(false);
        binding.idAffordabilityLayout.emiAmount.setCursorVisible(false);
    }

    private void convertlayouttobitmap() {

        binding.idNotifyLayout.resultCardView.setBackgroundResource(R.drawable.cardview_gradient_withborder);
        binding.idNotifyLayout.resultCardView.setDrawingCacheEnabled(true);

//
//        binding.idNotifyLayout.resultCardView.layout(0, 0, binding.idNotifyLayout.resultCardView.getMeasuredWidth(), binding.idNotifyLayout.resultCardView.getMeasuredHeight());

        binding.idNotifyLayout.resultCardView.buildDrawingCache(true);
        Bitmap property_rate_bmp = Bitmap.createBitmap(binding.idNotifyLayout.resultCardView.getDrawingCache());
//        Bitmap property_rate_bmp = binding.idNotifyLayout.resultCardView.getDrawingCache();
//        binding.idNotifyLayout.resultCardView.setDrawingCacheEnabled(false); // clear drawing cache

//        binding.shareImage.setImageBitmap(property_rate_bmp);
        binding.idNotifyLayout.resultCardView.setBackgroundResource(R.drawable.cardview_gradient);


//        shareimageWhtazapp("com.whatsapp",property_rate_bmp);
        shareProject(property_rate_bmp);
    }

    private void shareProject(Bitmap property_rate_bmp) {
        PackageManager pm = getPackageManager();
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            property_rate_bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
//            binding.shareImage.setImageBitmap(decoded);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), property_rate_bmp, "Property Rate", null);
            Uri imageUri = Uri.parse(path);
            Intent intent = new Intent(Intent.ACTION_SEND);
            String subject = "", text = "";
            String appName = " *clicbrics* ";

            intent.setType("image/*");
            intent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, appName);
            startActivity(Intent.createChooser(intent, "Share"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void shareimageWhtazapp(String pack, Bitmap property_rate_bmp) {
        PackageManager pm = getPackageManager();
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            property_rate_bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), property_rate_bmp, "Property Rate", null);
            Uri imageUri = Uri.parse(path);

            @SuppressWarnings("unused")
            PackageInfo info = pm.getPackageInfo(pack, PackageManager.GET_META_DATA);

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/*");
            waIntent.setPackage(pack);
            waIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
            waIntent.putExtra(Intent.EXTRA_TEXT, pack);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (Exception e) {
            Log.e("Error on sharing", e + " ");
            Toast.makeText(GetEstimateActivity.this, "App not Installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void addValuestoappyLoan() {
        LoanLead loanLead = new LoanLead();
        if (!TextUtils.isEmpty(UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.AppConstants.SELECTED_COUNTRY__NAME, ""))) {
            loanLead.setCountryName(UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.AppConstants.SELECTED_COUNTRY__NAME, ""));
        }

        loanLead.setCountrySTDCode(UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, ""));
        loanLead.setEmail(UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.AppConstants.EMAIL_PREF_KEY, ""));
        loanLead.setName(UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.AppConstants.USER_NAME_PREF_KEY, ""));
        loanLead.setPhone(UtilityMethods.getStringInPref(GetEstimateActivity.this, Constants.AppConstants.MOBILE_PREF_KEY, ""));
        loanLead.setCreatedTime(System.currentTimeMillis());
        loanLead.setCurrentEMI(Double.parseDouble(binding.idAffordabilityLayout.emiAmount.getText().toString().trim()));
        loanLead.setExternal(true);
        loanLead.setMonthlyIncome(Double.parseDouble(binding.idAffordabilityLayout.monthlyIncomeAmount.getText().toString().trim()));
        loanLead.setPropertyPrice(viewModel.getEstimatedPropertyModel().getAverate_rate());
        loanLead.setRate(Float.parseFloat(binding.idAffordabilityLayout.intrestRatePeriod.getText().toString().trim()));
        loanLead.setTenure(Float.parseFloat(binding.idAffordabilityLayout.loanTenureAmount.getText().toString().trim()));
        showProgressBar();
        viewModel.requestforloan(loanLead);
    }

    private void openMapExploreActivity() {
        if (!UtilityMethods.isInternetConnected(GetEstimateActivity.this)) {
            UtilityMethods.showErrorSnackbarOnTop(GetEstimateActivity.this);
            return;
        }
        Intent intent = new Intent(GetEstimateActivity.this, ExploreNeighbourhood.class);
        Bundle location = new Bundle();
        location.putParcelable("LatLng", mProjectLatlng);
        intent.putExtra(Constants.AppConstants.LOCATION, location);
        intent.putExtra(Constants.AppConstants.HOME, "Hello");
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void openFeedBackDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_estimation_feedback, null);
        feedbackDialog = new Dialog(this);
        feedbackDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        feedbackDialog.setContentView(view);
        feedbackDialog.setCancelable(true);
        ImageView cancel = (ImageView) view.findViewById(R.id.id_close);
        LinearLayout tooLow = (LinearLayout) view.findViewById(R.id.id_too_low_layout);
        LinearLayout tooHigh = (LinearLayout) view.findViewById(R.id.id_too_high_layout);
        LinearLayout goodMatch = (LinearLayout) view.findViewById(R.id.id_good_match_layout);

        tooLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackDialog != null && feedbackDialog.isShowing() && !isDestroyed()) {
                    feedbackDialog.dismiss();
                }
                showProgressBar();
                viewModel.sendFeedBack("TooLow");
            }
        });
        tooHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackDialog != null && feedbackDialog.isShowing() && !isDestroyed()) {
                    feedbackDialog.dismiss();
                }
                showProgressBar();
                viewModel.sendFeedBack("TooHigh");
            }
        });
        goodMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackDialog != null && feedbackDialog.isShowing() && !isDestroyed()) {
                    feedbackDialog.dismiss();
                }
                showProgressBar();
                viewModel.sendFeedBack("ExactMatch");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                if (feedbackDialog != null && feedbackDialog.isShowing() && !isDestroyed()) {
                    feedbackDialog.dismiss();
                }
            }
        });


        if (!isDestroyed() && !isFinishing() && !feedbackDialog.isShowing()) {
            feedbackDialog.show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendSuccess(String msg) {
        dismissProgressBar();
        UtilityMethods.showSnackbarOnTop(GetEstimateActivity.this, msg, "", false, 1500);
        Log.d(TAG, "Success");
    }

    @Override
    public void sendError() {
        dismissProgressBar();
    }

    private boolean isLoading = false;

    @Override
    public boolean isInternetConnected() {
        if (UtilityMethods.isInternetConnected(GetEstimateActivity.this)) {
            return true;
        } else {
            isLoading = false;
            UtilityMethods.showSnackbarOnTop(GetEstimateActivity.this, "Error", getString(R.string.no_network_connection), true, 1500);
            return false;
        }
    }

    @Override
    public void showLoader() {
        isLoading = true;
        binding.homeRecyclerView.setVisibility(View.GONE);
        binding.simialrPropertyText.setVisibility(View.GONE);

    }

    @Override
    public void hideLoader() {
//        binding.homeRecyclerView.setVisibility(View.VISIBLE);
//        binding.simialrPropertyText.setVisibility(View.VISIBLE);

    }

    private List<Project> projectList = new ArrayList<>();

    @Override
    public void isLoggedin() {

    }

    private enum ResultType {
        onSuccess, onError, onEmpty, onInternet
    }

    private String resultTypeValue;
    private int totalProjectCount;

    @Override
    public void onSuccessProjectByCity(Response<ProjectListResponse> projectListResponse) {

        isLoading = false;
        if (projectListResponse.body().getProjectList() != null && !projectListResponse.body().getProjectList().isEmpty()) {
            totalProjectCount = projectListResponse.body().getCount();
            projectList = projectListResponse.body().getProjectList();
            //setListView(projectListResponse.body().getProjectList());
            resultTypeValue = ResultType.onSuccess.toString();
        } else {
            resultTypeValue = ResultType.onEmpty.toString();
            /*setResultCount(0);
            txtEmptyView.setVisibility(View.VISIBLE);
            binding.homeRecyclerView.setVisibility(View.GONE);*/
        }
        setProjectList(projectList);
        setResultView();
        dismissProgressBar();
    }

    private void setResultView() {
        if (resultTypeValue.equalsIgnoreCase(ResultType.onSuccess.toString())) {
            binding.homeRecyclerView.setVisibility(View.VISIBLE);
            binding.simialrPropertyText.setVisibility(View.VISIBLE);
            setListView();

        } else if (resultTypeValue.equalsIgnoreCase(ResultType.onEmpty.toString())) {

            binding.homeRecyclerView.setVisibility(View.GONE);
            binding.simialrPropertyText.setVisibility(View.GONE);
        } else if (resultTypeValue.equalsIgnoreCase(ResultType.onInternet.toString())) {

            binding.homeRecyclerView.setVisibility(View.GONE);
            binding.simialrPropertyText.setVisibility(View.GONE);
        } else if (resultTypeValue.equalsIgnoreCase(ResultType.onError.toString())) {

            binding.simialrPropertyText.setVisibility(View.GONE);
            binding.homeRecyclerView.setVisibility(View.GONE);

        }
    }

    private HomeProjectListAdapter homeProjectListAdapter;

    /**
     * Method to set the UI view of project list screen(adapter , filter view, sorting view also success view)
     */
    private void setListView() {
        try {

            binding.homeRecyclerView.setHasFixedSize(true);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(GetEstimateActivity.this);
            binding.homeRecyclerView.setLayoutManager(layoutManager);
            homeProjectListAdapter = new HomeProjectListAdapter(projectList);
            binding.homeRecyclerView.setAdapter(homeProjectListAdapter);

            final long cityId = UtilityMethods.getLongInPref(GetEstimateActivity.this, Constants.AppConstants.SAVED_CITY_ID, -1);
            /*binding.homeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Log.i(TAG, "onScrolled: 0======" + dx + dy);
                    if (dy > 0) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                        Log.i(TAG, "onScrolled: 1======" + totalItemCount);
                        if (!isLoading && totalItemCount < totalProjectCount) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                    && firstVisibleItemPosition >= 0) {
                                {
                                    Log.i(TAG, "onScrolled: 2======" + totalItemCount);
                                    viewModel.getNextProjectListByDistance(viewModel.getEstimatedPropertyModel().getLatitude(),
                                            viewModel.getEstimatedPropertyModel().getLongitude(), cityId + "", totalItemCount, OFFSET_DIFF,
                                            filterPropertyType, filterBedType, filterProjectStatus, filterMinPrice,
                                            filterMaxPrice, filterMinArea, filterMaxArea, filterSort);
                                    *//*model.getNextProjectListByCity(cityId + "", totalItemCount, OFFSET_DIFF, null,
                                            filterPropertyType, filterBedType, filterProjectStatus, filterMinPrice,
                                            filterMaxPrice, filterMinArea, filterMaxArea, filterSort);*//*
                                }
                            }
                        }
                    }
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public void onErrorProjectByCity(String errMsg) {
        dismissProgressBar();
        binding.homeRecyclerView.setVisibility(View.GONE);
        binding.simialrPropertyText.setVisibility(View.GONE);

    }

    @Override
    public void showPagingLoader() {

    }

    @Override
    public void hidePagingLoader() {

    }

    @Override
    public void onSuccessNextProjectListByCity(Response<ProjectListResponse> projectListResponse) {

    }

    @Override
    public void onErrorNextProjectListByCity(String errMsg) {

    }

    private void setExploreArea() {
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.explore_map_lite);

        if (mapFragment != null) {
            mapFragment.getMapAsync(GetEstimateActivity.this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.i(TAG, "onMapLoaded: called...");
            }
        });
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.redbrics_mapstyle);
        mMap.setMapStyle(mapStyleOptions);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(viewModel.getEstimatedPropertyModel().getLatitude(), viewModel.getEstimatedPropertyModel().getLongitude()))
                .tilt(80)
                .zoom(14.0f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                openMapExploreActivity();
            }
        });
        binding.exploreLayout.taptoexplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapExploreActivity();
            }
        });
    }

    /**
     * Broadcast receiver to update the favorite in home project list
     */
    private BroadcastReceiver updateFavorite = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constants.BroadCastConstants.FAVORITE_CHANGE) {
                if (homeProjectListAdapter != null) {
                    homeProjectListAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateFavorite);
    }
}
