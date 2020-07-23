package com.clicbrics.consumer.framework.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.ResetPasswordActivity;
import com.clicbrics.consumer.adapter.CountryListAdapter;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.framework.customview.CustomDialog;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.CountryDto;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ForgotPasswordActivity extends BaseActivity{

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    ProgressDialog mProgressDialog;
    UserEndPoint mLoginWebService = null;
    Handler mHandler;
    boolean isEmailSent;
    EditText etMailId;
    Button submit;
    TextView countryStdCodeTxt;
    LinearLayout mCountryLayout;
    CustomDialog mCustomDialog;
    //boolean isEmailFieldIsEnable;
    String phonePattern = "\\d+";
    private Dialog customDialog;
    private List<CountryDto> countryList;
    private ArrayList<Bitmap> countryFlagBitmap;
    private String selectedCountryStdCode = "+91";
    private String selectedCountryName = "";
    private int REQUEST_CODE = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mHandler = new Handler();

        Toolbar toolbar = (Toolbar) findViewById(R.id.forgot_password_toolbar);
        //toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.black));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        buildRegService();
        etMailId = (EditText)findViewById(R.id.editTextForgotEmailId);
        mCountryLayout = (LinearLayout) findViewById(R.id.country_code_select);
        mCountryLayout.setVisibility(View.GONE);
        etMailId.setText(UtilityMethods.getStringInPref(ForgotPasswordActivity.this, Constants.AppConstants.EMAIL_PREF_KEY, ""));
        etMailId.setSelection(etMailId.getText().toString().trim().length());
        submit = (Button) findViewById(R.id.submitButton);
        /*if(getIntent().hasExtra(Constants.AppConstants.EDIT_FIELD_IS_ENABLE)) {
            isEmailFieldIsEnable = getIntent().getExtras().getBoolean(Constants.AppConstants.EDIT_FIELD_IS_ENABLE);
        }
        if(isEmailFieldIsEnable){*/
        etMailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence == null){
                    return;
                }
                String text = charSequence.toString().trim();
                if (text.matches(phonePattern)) {
                    mCountryLayout.setVisibility(View.VISIBLE);
                    //etMailId.setInputType(InputType.TYPE_CLASS_NUMBER);
                    if(text.length()>13){
                        etMailId.setError("Not a valid number");
                    }
                }else{
                    mCountryLayout.setVisibility(View.GONE);
                    //etMailId.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                final TextView msgText = (TextView) findViewById(R.id.msgText);
                final ImageView tickImage = (ImageView) findViewById(R.id.success);
                if (TextUtils.isEmpty(etMailId.getText().toString().trim())) {
                    submit.setEnabled(false);
                } else {
                    submit.setEnabled(true);
                }
                etMailId.setTextColor(getResources().getColor(R.color.text_color_login_reg));
                isEmailSent = false;
                submit.setText(R.string.submit);
                msgText.setVisibility(View.GONE);
                tickImage.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        /*}else{
            setEmailFieldIsEnable(true);
            hideSoftKeyboard();
        }*/
        String title = "";
        if(getIntent().hasExtra(Constants.AppConstants.ACTION_BAR_TITLE)) {
            title = getIntent().getExtras().getString(Constants.AppConstants.ACTION_BAR_TITLE, getString(R.string.forgot_password));
        }
        if(!title.equalsIgnoreCase(getString(R.string.forgot_password)) &&
                UtilityMethods.getBooleanInPref(ForgotPasswordActivity.this,Constants.AppConstants.IS_PASSWORD_AVAILABLE_PREF_KEY,true)){
            title = getString(R.string.forgot_password);
        }
        addBackButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private void buildRegService()
    {
        mLoginWebService = EndPointBuilder.getUserEndPoint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        submit.setEnabled(true);
    }

    public void sendPassWord(View view)
    {
        hideSoftKeyboard();
        if(!UtilityMethods.isInternetConnected(this)){
            new EventAnalyticsHelper().logAPICallEvent(ForgotPasswordActivity.this, Constants.AnaylticsClassName.ForgotPasswordScreen,
                    null, Constants.ApiName.forgotPassword.toString(),Constants.AnalyticsEvents.FAILED,
                    getResources().getString(R.string.network_error_msg));
            UtilityMethods.showErrorSnackBar(findViewById(R.id.rootLayout),
                    getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
            return;
        }
//        TrackAnalytics.trackEvent(ForgotPasswordActivity.this.getResources().getString(R.string.ga_button_category), getString(R.string.forgot_password), ForgotPasswordActivity.this);
        if(isEmailSent){
            finish();
        }else{
            setEmailFieldIsEnable(false);
            String email = etMailId.getText().toString().trim();
            if (email.matches(phonePattern)) {
                if (TextUtils.isEmpty(email)) {
                    etMailId.setError(getString(R.string.phone_no_cannot_be_empty));
                    setEmailFieldIsEnable(true);
                    return;
                }
            } else {
                if (TextUtils.isEmpty(email)) {
                    etMailId.setError(getString(R.string.email_cannot_be_empty));
                    setEmailFieldIsEnable(true);
                    return;
                } else if (!UtilityMethods.isValidEmail(email)) {
                    etMailId.setError(getString(R.string.invalid_emailid));
                    setEmailFieldIsEnable(true);
                    return;
                }
            }
            final String inputValue = etMailId.getText().toString().trim();
            if(!UtilityMethods.isInternetConnected(getApplicationContext()))
            {
                final View.OnClickListener positiveListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View dialog) {
                        if(mCustomDialog!=null)
                            mCustomDialog.dismiss();
                    }
                };
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCustomDialog = UtilityMethods.showAlert(ForgotPasswordActivity.this, R.string.network_error_title, getString(R.string.network_error_msg), true, R.string.close, positiveListener, -1, null,null);
                    }
                },300);
                setEmailFieldIsEnable(true);
                return;
            }
            //showProgressBar("Sending password to " + userName);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            final TextView msgText = (TextView) findViewById(R.id.msgText);
            progressBar.setVisibility(View.VISIBLE);
            submit.setEnabled(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String errorMSG = "";
                    try {

                        CommonResponse loginResponse = null;
                        if(inputValue.matches(phonePattern)){
                            if(countryStdCodeTxt != null && !TextUtils.isEmpty(countryStdCodeTxt.getText().toString().trim())){
                                selectedCountryStdCode = countryStdCodeTxt.getText().toString().trim();
                            }
                            UserEndPoint.ForgotPassword forgotPassword  = mLoginWebService.forgotPassword(inputValue);
                            forgotPassword.setCountrySTDCode(selectedCountryStdCode);
                            loginResponse = forgotPassword.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        }else{
                            loginResponse = mLoginWebService.forgotPassword(inputValue).setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        }
                        if(loginResponse.getStatus())
                        {
                            new EventAnalyticsHelper().logAPICallEvent(ForgotPasswordActivity.this, Constants.AnaylticsClassName.ForgotPasswordScreen,
                                    null, Constants.ApiName.forgotPassword.toString(),Constants.AnalyticsEvents.SUCCESS,
                                   null);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    hideSoftKeyboard();
                                    progressBar.setVisibility(View.GONE);
                                    setEmailFieldIsEnable(true);
                                    if(inputValue.matches(phonePattern)){
                                        msgText.setText("OTP sent");
                                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                                        intent.putExtra("MobileNo",inputValue);
                                        intent.putExtra("CountryStdCode",selectedCountryStdCode);
                                        startActivityForResult(intent,REQUEST_CODE);
                                    }else{
                                        ImageView tickImage = (ImageView) findViewById(R.id.success);
                                        tickImage.setVisibility(View.VISIBLE);
                                        msgText.setVisibility(View.VISIBLE);


                                        Button submit = (Button) findViewById(R.id.submitButton);
                                        EditText etMailId = (EditText)findViewById(R.id.editTextForgotEmailId);
                                        submit.setText(getString(R.string.ok));
                                        submit.setEnabled(true);
                                        isEmailSent = true;
                                        msgText.setText(R.string.email_sent);

                                        View.OnClickListener positiveListener = new View.OnClickListener() {
                                            //
                                            @Override
                                            public void onClick(View dialog) {
                                                if(mCustomDialog!=null) {
                                                    mCustomDialog.dismiss();
                                                }
                                                finish();
                                            }
                                        };
                                        String message = "Email has been sent to " + inputValue +  " to reset your password.";
                                        mCustomDialog = UtilityMethods.showAlert(ForgotPasswordActivity.this,
                                                R.string.success, message, false, R.string.ok, positiveListener, -1, null,null);
                                    }
                                }
                            });

                        }else
                        {
                            errorMSG = loginResponse.getErrorMessage();
                        }
                    }
                    catch (UnknownHostException e)
                    {
                        e.printStackTrace();
                        errorMSG = getResources().getString(R.string.network_error_msg);
                        TrackAnalytics.trackException(TAG,e.getMessage(),e);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        errorMSG = getString(R.string.something_went_wrong);
                        TrackAnalytics.trackException(TAG,e.getMessage(),e);
                    }
                    if(!TextUtils.isEmpty(errorMSG))
                    {
                    if (mProgressDialog != null) {
                       mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }

                        new EventAnalyticsHelper().logAPICallEvent(ForgotPasswordActivity.this, Constants.AnaylticsClassName.ForgotPasswordScreen,
                                null, Constants.ApiName.forgotPassword.toString(),Constants.AnalyticsEvents.FAILED,
                                errorMSG);
                        final String errmsg = errorMSG;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                View.OnClickListener positiveListener = new View.OnClickListener() {
                                    //
                                    @Override
                                    public void onClick(View dialog) {
                                        if(mCustomDialog!=null)
                                            mCustomDialog.dismiss();
                                    }
                                };
                                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                                final TextView msgText = (TextView) findViewById(R.id.msgText);
                                setEmailFieldIsEnable(true);
                                progressBar.setVisibility(View.GONE);
                                msgText.setVisibility(View.GONE);
                                mCustomDialog = UtilityMethods.showAlert(ForgotPasswordActivity.this, R.string.error, errmsg, true, R.string.ok, positiveListener, -1, null,null);
                                submit.setEnabled(true);
                            }
                        });

                    }
                }
            }).start();
        }
    }

    private void setEmailFieldIsEnable(boolean isEnable){
        if(isEnable){
            etMailId.setEnabled(true);
        }else{
            etMailId.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideSoftKeyboard();
        if(progressBar.getVisibility() != View.VISIBLE){
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideSoftKeyboard();
        return super.onOptionsItemSelected(item);
    }

    /*private boolean isEmailValid(String email) {
        email = email.trim();
        if (email.matches(phonePattern)) {
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.phone_no_cannot_be_empty));
                return false;
            }
        } else {
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.email_cannot_be_empty));
                return false;
            } else if (!UtilityMethods.isValidEmail(email)) {
                mEmailView.setError(getString(R.string.invalid_emailid));
                return false;
            }
        }
        return true;
    }*/


    public void showCountryList(View v) {
        hideSoftKeyboard();
        if (customDialog == null) {
            customDialog = new Dialog(this);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View view = getLayoutInflater().inflate(R.layout.dialog_country_list, null);
            ListView listView = (ListView) view.findViewById(R.id.countryList);
            EditText countrySearch = (EditText) view.findViewById(R.id.inputCountrySearch);
            countryStdCodeTxt = (TextView) findViewById(R.id.countryStdCode);
            if (countryList == null) {
                countryList = new ArrayList<>();
                countryFlagBitmap = new ArrayList<Bitmap>();
                final List<String> countryNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.country_names)));
                countryFlagBitmap = getBitmapListFromAsset(countryNames);
                final List<String> countryStdCodes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.country_std_code)));
                for (int i = 0; i < countryNames.size(); i++) {
                    CountryDto countryDto = new CountryDto();
                    countryDto.setCountryName(countryNames.get(i));
                    countryDto.setCountryCode(countryStdCodes.get(i));
                    countryDto.setFlagName(countryNames.get(i) + ".png");
                    countryDto.setFlagImage(countryFlagBitmap.get(i));
                    countryList.add(countryDto);
                }
            }
            final CountryListAdapter adapter = new CountryListAdapter(this, countryList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CountryDto countryDto = adapter.getItem(position);
                    try {
                        countryStdCodeTxt.setText(countryDto.getCountryCode());
                        selectedCountryStdCode = countryDto.getCountryCode();
                        selectedCountryName = countryDto.getCountryName();
                        customDialog.dismiss();
                        hideSoftKeyboard();
                        customDialog = null;
                    } catch (Exception e) {
                        ((TextView) findViewById(R.id.countryStdCode)).setText("+91");
                        e.printStackTrace();
                    }
                }
            });
            countrySearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d(TAG, "beforeTextChanged : " + s);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(TAG, "onTextChanged : " + s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG, "afterTextChanged : " + s);
                    adapter.getFilter().filter(s);
                }
            });
            customDialog.setContentView(view);
            final Typeface mFont = Typeface.createFromAsset(getAssets(),
                    "fonts/FiraSans-Regular.ttf");
            final ViewGroup mContainer = (ViewGroup) customDialog.findViewById(
                    android.R.id.content).getRootView();
            UtilityMethods.setAppFont(mContainer, mFont, false);
            customDialog.setCancelable(true);
            TextView cancelButton = (TextView) view.findViewById(R.id.cancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                    hideSoftKeyboard();
                    customDialog = null;
                }
            });
            customDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    customDialog = null;
                }
            });

            if (!customDialog.isShowing()) {
                customDialog.show();
            }
        }
    }

    private ArrayList<Bitmap> getBitmapListFromAsset(List<String> listFlagNames) {
        AssetManager assetManager = getAssets();
        for (int i = 0; i < listFlagNames.size(); i++) {
            try {
                InputStream inputStream = assetManager.open(Constants.AppConstants.COUNTRY_FLAG_DIR + File.separator + listFlagNames.get(i).trim() + ".png");
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                countryFlagBitmap.add(bitmap);
                inputStream.close();
            } catch (IOException e) {
                countryFlagBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.no_flag_icon));
                e.printStackTrace();
            }
        }
        return countryFlagBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode ==  REQUEST_CODE){
            finish();
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
  /*      mGoogleApiClient.stopAutoManage(LoginActivity.this);
        mGoogleApiClient.disconnect();*/
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.ForgotPasswordScreen);

        /*TrackAnalytics.trackEvent("LoginPage", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);*/
    }
}
