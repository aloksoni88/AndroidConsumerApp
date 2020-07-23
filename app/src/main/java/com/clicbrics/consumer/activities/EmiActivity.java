package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.Bank;
import com.buy.housing.backend.propertyEndPoint.model.KeyValuePair;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.customview.SpannableText;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.List;


/**
 * Created by Paras on 18-10-2016.
 */
@SuppressWarnings("deprecation")
public class EmiActivity extends BaseActivity implements LoginSuccessCallback, AdapterView.OnItemSelectedListener {
    private static final String TAG = EmiActivity.class.getSimpleName();
    private long propertyAmount;
    private double downPaymentAmount;
    private TextView propertyType, propertyValue, propertySize, loanAmountTxt, tenure, emiAmount,
            downPaymentMin, downPaymentMax, bankNameText, bankOffer, offerValue;
    private SeekBar tenureSeek, downpaymentSeek;
    private UserEndPoint userEndPoint;
    private CoordinatorLayout rootLayout;
    private EditText interestRateTxt;
    private ImageView bankLogoImage, offerImage;
    private CardView bankOfferLayout;
    private ProgressBar mBanklogoPB;

    private Spinner spinnerPropertyType;
    private LinearLayout spinnerPropertyTypeLayout;
    private ImageView dropDownImage;
    List<PropertyType> mPropertyList;
    List<Bank> mBankLoanList;
    long mProjectId, mPropertyId;
    double downPaymentValue = 0.20;
    float defaultInterestRate = 8.60f;


    private void buildUserWebService() {
        userEndPoint = EndPointBuilder.getUserEndPoint();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.emi_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        propertyAmount = getIntent().getLongExtra(Constants.IntentKeyConstants.PROPERTY_VALUE, 0);
        buildUserWebService();
        addBackButton();
        initView();
        loadData();

        UtilityMethods.setStatusBarColor(this,R.color.light_white);
    }


    private void initView() {

        findViewById(R.id.speak_to_expert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(UtilityMethods.getStringInPref(mActivity, Constants.AppConstants.USER_NAME_PREF_KEY, ""))) {
                    HousingApplication.mLoginSuccessCallback = EmiActivity.this;
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else
                    speakToLoadAgent();
            }
        });
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);
        emiAmount = (TextView) findViewById(R.id.emi);
        propertyValue = (TextView) findViewById(R.id.property_value);
        propertySize = (TextView) findViewById(R.id.property_size);
        interestRateTxt = (EditText) findViewById(R.id.edit_text_interest_rate);
        propertyType = (TextView) findViewById(R.id.property_type);
        spinnerPropertyType = (Spinner) findViewById(R.id.id_spinner_property_type);
        spinnerPropertyTypeLayout = (LinearLayout) findViewById(R.id.id_spinner_property_type_layout);
        dropDownImage = (ImageView) findViewById(R.id.id_drop_down_image);
        loanAmountTxt = (TextView) findViewById(R.id.loan_amount);
        tenure = (TextView) findViewById(R.id.tenure);
        tenureSeek = (SeekBar) findViewById(R.id.tenureSeek);
        downPaymentMin = (TextView) findViewById(R.id.downpaymentMin);
        downPaymentMax = (TextView) findViewById(R.id.downpaymentMax);
        bankNameText = (TextView) findViewById(R.id.id_bank_name);
        bankOffer = (TextView) findViewById(R.id.id_offer_text);
        bankLogoImage = (ImageView) findViewById(R.id.id_bank_logo);
        offerImage = (ImageView) findViewById(R.id.id_offer_image);
        bankOfferLayout = (CardView) findViewById(R.id.id_bank_offer_layout);
        mBanklogoPB = (ProgressBar) findViewById(R.id.id_emi_banklogo_pb);

        tenureSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tenure.setText((progress + 5) + " Yr");
                if((interestRateTxt != null && interestRateTxt.getText() != null)
                        && ( TextUtils.isEmpty(interestRateTxt.getText().toString())
                        ||(!TextUtils.isEmpty(interestRateTxt.getText().toString())
                        && interestRateTxt.getText().toString().length() == 1
                        && interestRateTxt.getText().toString().contains("."))) ){
                    interestRateTxt.setText(defaultInterestRate + "");
                    interestRateTxt.setSelection(interestRateTxt.getText().toString().length());
                }
                calculateEmi();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        downpaymentSeek = (SeekBar) findViewById(R.id.downpaymentSeek);
        downpaymentSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /*double downPaymentPercentage = getDownpaymentPercentage((int) ((long) downPaymentAmount + progress));
                ((TextView) findViewById(R.id.downpayment)).setText(UtilityMethods.getPriceWordTillThousand((int) ((long) downPaymentAmount + progress)) + " (" + (int)downPaymentPercentage + "%)");
                loanAmountTxt.setText(UtilityMethods.getPriceWordTillThousand(propertyAmount - (downPaymentAmount + progress)));
                calculateEmi();*/
                if((interestRateTxt != null && interestRateTxt.getText() != null)
                        && ( TextUtils.isEmpty(interestRateTxt.getText().toString())
                        ||(!TextUtils.isEmpty(interestRateTxt.getText().toString())
                        && interestRateTxt.getText().toString().length() == 1
                        && interestRateTxt.getText().toString().contains("."))) ){
                    interestRateTxt.setText(defaultInterestRate + "");
                    interestRateTxt.setSelection(interestRateTxt.getText().toString().length());
                }
                double downPaymentPercentage = getDownpaymentPercentage((int) ((long) progress));
                //((TextView) findViewById(R.id.downpayment)).setText(getPriceWordTillThousand((int) ((long) progress)) + " (" + (int) downPaymentPercentage + "%)");
                ((TextView) findViewById(R.id.downpayment)).setText("₹" + " " + NumberFormat.getInstance().format(Math.round((long) progress)) + " (" + (int) downPaymentPercentage + "%)");
                //loanAmountTxt.setText(UtilityMethods.getPriceWordTillThousand(propertyAmount - (progress)));
                loanAmountTxt.setText("₹" + " " + NumberFormat.getInstance().format(Math.round(propertyAmount-progress)));
                calculateEmi();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        interestRateTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                try {
                    if(interestRateTxt!= null && !interestRateTxt.getText().toString().isEmpty()){
                        if(Float.parseFloat(interestRateTxt.getText().toString()) >= 10){
                            interestRateTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                        } else{
                            interestRateTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateEmi();
            }
        });

        dropDownImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinnerPropertyType != null) {
                    spinnerPropertyType.performClick();
                }
            }
        });

        if(getIntent().hasExtra(Constants.IntentKeyConstants.IS_FROM_FINANCE)
                && getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_FROM_FINANCE,false)){
            //spinnerPropertyType.setVisibility(View.VISIBLE);
            spinnerPropertyTypeLayout.setVisibility(View.VISIBLE);
            propertyType.setVisibility(View.GONE);
            bankOfferLayout.setVisibility(View.VISIBLE);
            initSpinner();
            Gson gson = new Gson();
            String json = getIntent().getStringExtra(Constants.IntentKeyConstants.BANK_DETAILS);
            Type type = new TypeToken<com.clicbrics.consumer.wrapper.Bank>(){}.getType();
            com.clicbrics.consumer.wrapper.Bank bank = gson.fromJson(json,type);
            if(bank != null) {
                String bankName = bank.getName();
                defaultInterestRate = (bank.getInterest() != null) ? bank.getInterest() : 8.60f;
                final String bankLogo = bank.getLogo();
                List<KeyValuePair> bankOfferList = bank.getOffersList();
                bankNameText.setText(bankName);
                if (bankLogo != null && !bankLogo.isEmpty()) {
                    mBanklogoPB.setVisibility(View.VISIBLE);
                    Picasso.get().load(bankLogo).into(bankLogoImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mBanklogoPB.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            mBanklogoPB.setVisibility(View.GONE);
                        }
                    });
                }
                if (bankOfferList != null && bankOfferList.size() > 0) {
                    offerImage.setVisibility(View.VISIBLE);
                    bankOffer.setVisibility(View.VISIBLE);
                    UtilityMethods.setDrawableBackground(this, offerImage, R.drawable.bankloan_offer);
                    int size = bankOfferList.size();
                    for (int i = 0; i < size; i++) {
                        if (size == 1) {
                            bankOffer.append(Html.fromHtml(bankOfferList.get(i).getKey() + "<br>" + bankOfferList.get(i).getValue()));
                        } else {
                            bankOffer.append(bankOfferList.get(i).getKey() + "<br>" + bankOfferList.get(i).getValue() + "<br>");
                        }
                    }
                    if (size > 1) {
                        makeTextViewResizable(bankOffer, 2, "Read More", true);
                    }
                } else {
                    offerImage.setVisibility(View.GONE);
                    bankOffer.setVisibility(View.GONE);
                }
            }else{
                offerImage.setVisibility(View.GONE);
                bankOffer.setVisibility(View.GONE);
            }
        }else if(getIntent().hasExtra(Constants.IntentKeyConstants.IS_FROM_LAYOUT)
                && getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_FROM_LAYOUT,false)){
            defaultInterestRate = UtilityMethods.getFloatInPref(this, Constants.AppConfigConstants.DEFAULT_INTEREST_RATE, 8.60f);
            bankOfferLayout.setVisibility(View.GONE);
            //spinnerPropertyType.setVisibility(View.GONE);
            spinnerPropertyTypeLayout.setVisibility(View.GONE);
            propertyType.setVisibility(View.VISIBLE);
            String type = "";
            if (getIntent().hasExtra(Constants.IntentKeyConstants.TYPE)) {
                type = getIntent().getStringExtra(Constants.IntentKeyConstants.TYPE);
                int noOfBedrooms = 0;
                if (getIntent().hasExtra(Constants.IntentKeyConstants.PROPERTY_TYPE)) {
                    noOfBedrooms = getIntent().getIntExtra(Constants.IntentKeyConstants.PROPERTY_TYPE, 0);
                }
                propertyType.setText(getPropertyType(type, noOfBedrooms));
            }
            if (getIntent().hasExtra(Constants.IntentKeyConstants.PROPERTY_SIZE)) {
                int superArea = getIntent().getIntExtra(Constants.IntentKeyConstants.PROPERTY_SIZE, 0);
                if(type.equalsIgnoreCase("Land") || UtilityMethods.isCommercialLand(type)){
                    String area = UtilityMethods.getArea(this,superArea,true);
                    String unit = UtilityMethods.getUnit(this,true);
                    propertySize.setText(area + unit);
                }else {
                    String area = UtilityMethods.getArea(this,superArea,false);
                    String unit = UtilityMethods.getUnit(this,false);
                    propertySize.setText(area +unit);
                }
            }
        }else if(getIntent().hasExtra(Constants.IntentKeyConstants.IS_FROM_DETAIL_LINK)
                && getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_FROM_DETAIL_LINK,false)){
            if(getIntent().hasExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL)
                    && getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL,false)){
                bankOfferLayout.setVisibility(View.VISIBLE);
                String json = getIntent().getStringExtra(Constants.IntentKeyConstants.BANK_DETAILS);
                Gson gson = new Gson();
                Type type = new TypeToken<com.clicbrics.consumer.wrapper.Bank>(){}.getType();
                com.clicbrics.consumer.wrapper.Bank bank = gson.fromJson(json,type);
                if(bank != null) {
                    String bankName = bank.getName();
                    defaultInterestRate = bank.getInterest() != null ? bank.getInterest() : 8.60f;
                    final String bankLogo = bank.getLogo();
                    List<KeyValuePair> bankOfferList = bank.getOffersList();
                    bankNameText.setText(bankName);
                    if (bankLogo != null && !bankLogo.isEmpty()) {
                        mBanklogoPB.setVisibility(View.VISIBLE);
                        Picasso.get().load(bankLogo).into(bankLogoImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                mBanklogoPB.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                mBanklogoPB.setVisibility(View.GONE);
                            }
                        });
                    }
                    if (bankOfferList != null && bankOfferList.size() > 0) {
                        offerImage.setVisibility(View.VISIBLE);
                        bankOffer.setVisibility(View.VISIBLE);
                        UtilityMethods.setDrawableBackground(this, offerImage, R.drawable.bankloan_offer);
                        int size = bankOfferList.size();
                        for (int i = 0; i < size; i++) {
                            if (size == 1) {
                                bankOffer.append(Html.fromHtml(bankOfferList.get(i).getKey() + "<br>" + bankOfferList.get(i).getValue()));
                            } else {
                                bankOffer.append(bankOfferList.get(i).getKey() + "<br>" + bankOfferList.get(i).getValue() + "<br>");
                            }
                        }
                        if (size > 1) {
                            makeTextViewResizable(bankOffer, 2, "Read More", true);
                        }
                    } else {
                        offerImage.setVisibility(View.GONE);
                        bankOffer.setVisibility(View.GONE);
                    }
                }else{
                    offerImage.setVisibility(View.GONE);
                    bankOffer.setVisibility(View.GONE);
                }
            }else{
                bankOfferLayout.setVisibility(View.GONE);
                defaultInterestRate = getIntent().getFloatExtra(Constants.IntentKeyConstants.INTEREST_RATE, 8.60f);
            }
            //spinnerPropertyType.setVisibility(View.VISIBLE);
            spinnerPropertyTypeLayout.setVisibility(View.VISIBLE);
            propertyType.setVisibility(View.GONE);
            initSpinner();
        }

    }


    private void calculateEmi() {
        //double downPayment = this.downPaymentAmount + downpaymentSeek.getProgress();
        double downPayment = downpaymentSeek.getProgress();
        double principleAmount = (propertyAmount - downPayment);
        double tenue = Constants.AppConstants.TENURE_SEEK + tenureSeek.getProgress();
        double interestRate = Constants.AppConstants.DEFAULT_INTEREST_RATE;
        if (interestRateTxt != null && interestRateTxt.getText() != null
                && !TextUtils.isEmpty(interestRateTxt.getText().toString())) {
            if(interestRateTxt.getText().toString().length() == 1 && interestRateTxt.getText().toString().contains(".")){
                return;
            }
            if(Float.parseFloat(interestRateTxt.getText().toString()) >= 10){
                interestRateTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            }else{
                interestRateTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            }
            interestRate = Double.parseDouble(interestRateTxt.getText().toString());
        } else if (TextUtils.isEmpty(interestRateTxt.getText().toString())) {
            //interestRateTxt.setText(defaultInterestRate + "");
            //interestRate = defaultInterestRate;
            return;
        }

        String emiValue = calculateEmi(principleAmount, interestRate, tenue);

        Log.d("TAG", "Emi->" + emiValue + "downpayment-> " + downPayment);
        Log.d("TAG", "dp->" + downPayment + "  " + propertyAmount);
        emiAmount.setText("₹" + " " + emiValue);

        String downPaymentStrWithBracket = ((TextView) findViewById(R.id.downpayment)).getText().toString();
        if (downPaymentStrWithBracket != null && downPaymentStrWithBracket.contains("(")) {
            String downPaymentStr = downPaymentStrWithBracket.substring(0, downPaymentStrWithBracket.indexOf("(") - 1);
            if (String.valueOf(downPaymentStr)
                    .equals(String.valueOf(((TextView) findViewById(R.id.downpaymentMax)).getText()))) {
                emiAmount.setText("Loan not required");
            }
        }
    }

    /*
   * Method responsible for getting project details according to lat long
   * */
    private void speakToLoadAgent() {


        if (!UtilityMethods.isInternetConnected(mActivity)) {
            Snackbar snackbar = Snackbar
                    .make(rootLayout, getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            speakToLoadAgent();
                        }
                    });
            ;
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.uber_red));
            snackbar.show();
            return;
        }
        showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    CommonResponse commonResponse = userEndPoint.speakToLoanExpert().setId(UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0l))
                            .setProjectId(mProjectId)
                            .setPropertyId(mPropertyId)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (commonResponse.getStatus()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UtilityMethods.showSnackBar(rootLayout, getString(R.string.loan_exp_msg), Snackbar.LENGTH_LONG);
                                dismissProgressBar();
                            }
                        });


                    } else {
                        errorMsg = commonResponse.getErrorMessage();
                    }
                } catch (UnknownHostException e) {
                    errorMsg = getResources().getString(R.string.network_error_msg);
                }catch (IOException e) {
                    errorMsg = getResources().getString(R.string.something_went_wrong);
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            UtilityMethods.showSnackBar(rootLayout, errmsg, Snackbar.LENGTH_LONG);
                        }
                    });
                }


            }

        }).start();
    }


    private void initSpinner() {
        mPropertyList = ((HousingApplication) getApplicationContext()).getPropertyTypeList();
        if (mPropertyList != null && mPropertyList.size() > 0) {
            //ArrayList<Long> housePriceList = new ArrayList<>();
            String[] propertyTypeArr = new String[mPropertyList.size()];
            for (int i = 0; i < mPropertyList.size(); i++) {
                PropertyType property = mPropertyList.get(i);
                if(property.getType().equalsIgnoreCase("Land")|| UtilityMethods.isCommercialLand(property.getType())){
                    int superArea = property.getSuperArea();
                    /*long bsp = property.getBsp();
                    int superArea = property.getSuperArea();
                    long housePrice = bsp * superArea * 9;
                    housePriceList.add(housePrice);*/
                    String area = UtilityMethods.getArea(this,superArea,true);
                    String unit = UtilityMethods.getUnit(this,true);
                    propertyTypeArr[i] = getPropertyType(property.getType(), property.getNumberOfBedrooms())
                            + " " + getString(R.string.bullet_symbol) + " " + area + unit;
                }else {
                    int superArea = property.getSuperArea();
                    /*long bsp = property.getBsp();
                    int superArea = property.getSuperArea();
                    long housePrice = bsp * superArea;
                    housePriceList.add(housePrice);*/
                    String area = UtilityMethods.getArea(this,superArea,false);
                    String unit = UtilityMethods.getUnit(this,false);
                    propertyTypeArr[i] = getPropertyType(property.getType(), property.getNumberOfBedrooms())
                            + " " + getString(R.string.bullet_symbol) + " " + area + unit;
                }
            }
            //Collections.sort(housePriceList);
            spinnerPropertyType.setOnItemSelectedListener(this);
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_property_type_list, propertyTypeArr);
            adapter.setDropDownViewResource(R.layout.spinner_drop_down_text_layout);
            spinnerPropertyType.setAdapter(adapter);
            spinnerPropertyType.setSelection(0);
            propertyAmount = mPropertyList.get(0).getBsp() * mPropertyList.get(0).getSuperArea();
            mProjectId = mPropertyList.get(0).getProjectId();
            mPropertyId = mPropertyList.get(0).getId();
        }


    }

    @Override
    public void isLoggedin() {
        speakToLoadAgent();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        TextView textView = (TextView) view.findViewById(R.id.id_spinner_text);
        PropertyType property = mPropertyList.get(position);
        propertyAmount = property.getBsp() * property.getSuperArea();
        mProjectId = property.getProjectId();
        mPropertyId = property.getId();
        if (property.getType().equalsIgnoreCase("Apartment") || property.getType().equalsIgnoreCase("Studio")
                || property.getType().equalsIgnoreCase("Penthouse")
                || property.getType().equalsIgnoreCase("IndependentHouseVilla")
                || property.getType().equalsIgnoreCase("Villa")) {
            downPaymentValue = 0.20;
        } else if (property.getType().equalsIgnoreCase("Land")) {
            downPaymentValue = 0.40;
        }
        textView.setText(getPropertyType(property.getType(), property.getNumberOfBedrooms()));
        if(property.getType().equalsIgnoreCase("Land") || UtilityMethods.isCommercialLand(property.getType())){
            propertySize.setText(UtilityMethods.getArea(this,property.getSuperArea(),true) + UtilityMethods.getUnit(this,true));
        }else {
            propertySize.setText(UtilityMethods.getArea(this,property.getSuperArea(),false) + UtilityMethods.getUnit(this,false));
        }
        loadData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void loadData() {
        String interestRate = String.format("%.2f", defaultInterestRate);
        interestRateTxt.setText(interestRate);
        interestRateTxt.setSelection(interestRateTxt.getText().toString().length());
        tenureSeek.setMax(25);
        downPaymentAmount = propertyAmount * downPaymentValue;
        //downpaymentSeek.setMax((int) (propertyAmount - (long) downPaymentAmount));
        //downPaymentMin.setText(UtilityMethods.getPriceWordTillThousand((int) (downPaymentAmount)));
        downpaymentSeek.setMax((int) propertyAmount);
        downPaymentMin.setText("₹" + " " + 0);
        //downPaymentMax.setText(getPriceWordTillThousand((int) propertyAmount));
        downPaymentMax.setText("₹" + " " + NumberFormat.getInstance().format(Math.round(propertyAmount)));
        //loanAmountTxt.setText(UtilityMethods.getPriceWordTillThousand((propertyAmount - downPaymentAmount)));
        loanAmountTxt.setText("₹" + " " + NumberFormat.getInstance().format(Math.round(propertyAmount-downPaymentAmount)));
        //propertyValue.setText(UtilityMethods.getPriceWithLACWord(propertyAmount));
        propertyValue.setText("₹" + " " + NumberFormat.getInstance().format(Math.round(propertyAmount)));
        calculateEmi();
        tenureSeek.setProgress(15);
        downpaymentSeek.setProgress((int) (downPaymentAmount));

        if(interestRateTxt!= null && !interestRateTxt.getText().toString().isEmpty()){
            if(Float.parseFloat(interestRateTxt.getText().toString()) >= 10){
                interestRateTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            }else{
                interestRateTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            }
        }
    }

    private String getPropertyType(String type, int noOfBedrooms) {
        if (type.equalsIgnoreCase("Land")) {
            downPaymentValue = 0.40;
            return "Plot";
        }else if(UtilityMethods.isCommercial(type)){
            downPaymentValue = 0.40;
            return UtilityMethods.getCommercialTypeName(type);
        } else if (type.equalsIgnoreCase("Villa") || type.equalsIgnoreCase("IndependentHouseVilla")) {
            downPaymentValue = 0.20;
            return noOfBedrooms + " BHK";
            //return "Villa";
        } else if (type.equalsIgnoreCase("Apartment")
                || type.equalsIgnoreCase("Penthouse")
                || type.equalsIgnoreCase("IndependentFloor")) {
            downPaymentValue = 0.20;
            return noOfBedrooms + " BHK";
        }else if(type.equalsIgnoreCase("Studio")){
            downPaymentValue = 0.20;
            return "1RK Studio";
        } else{
            downPaymentValue = 0.20;
            return noOfBedrooms + " BHK";
        }
    }

    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
        try {
            if (tv.getTag() == null) {
                tv.setTag(tv.getText());
            }
            ViewTreeObserver vto = tv.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {

                    ViewTreeObserver obs = tv.getViewTreeObserver();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        obs.removeOnGlobalLayoutListener(this);
                    } else {
                        obs.removeGlobalOnLayoutListener(this);
                    }

                    if (maxLine == 0) {
                        int lineEndIndex = tv.getLayout().getLineEnd(0);
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
                    } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                        int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                        String text;
                        String completeStr = tv.getText().toString();
                        int firstLineEndIndex = completeStr.indexOf("<br>");
                        int secondLineEndIndex = completeStr.indexOf("<br>", firstLineEndIndex + 1);
                        if (expandText.equals("Read More")) {
                            text = tv.getText().subSequence(0, secondLineEndIndex - 1) + "... " + expandText;
                        } else {
                            text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        }
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);
                    } else {
                        int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        String text;

                        if (expandText.equals("Read More")) {
                            text = tv.getText().subSequence(0, lineEndIndex) + "... " + expandText;
                        } else {
                            text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                        }
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(Spanned strSpanned, final TextView tv,
                                                                     int maxLine, String spanableText,
                                                                     final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new SpannableText(false, this) {

                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 2, "Read More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }

    private double getDownpaymentPercentage(double price) {
        double percentage = (price * 100) / propertyAmount;
        return Math.round(percentage);
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
        TrackAnalytics.trackEvent("EmiActivity", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static String calculateEmi(double principal, double rate, double time) {

        if(rate == 0){
            return  " 0 / month";
        }
        double emi;
        rate = rate / (12 * 100); /*one month interest*/
        time = time * 12; /*one month period*/
        emi = (principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);
        if (emi == 0)
            return "Loan not required";
        else
            return NumberFormat.getInstance().format(Math.round(emi)) + " / month";
    }

}
