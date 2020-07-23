package com.clicbrics.consumer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.activity.ForgotPasswordActivity;
import com.clicbrics.consumer.framework.customview.CustomDialog;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.net.UnknownHostException;

public class ResetPasswordActivity extends BaseActivity {

    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    OTPSMSBroadcast otpSmsBroadcast;
    private EditText otpEditText,passEditText, confirmPassEditText;
    private TextView resendOTPText;
    private int timerStatus;
    UserEndPoint mUserEndPoint = null;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initToolbar();
        initView();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.forgot_password_toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.black));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
    }

    private void initView(){
        otpEditText = (EditText) findViewById(R.id.id_otp_edit_text);
        resendOTPText = (TextView) findViewById(R.id.id_resend_otp_text);
        passEditText = (EditText) findViewById(R.id.id_new_password_edit_text);
        confirmPassEditText = (EditText) findViewById(R.id.id_confirm_password_edit_text);

        buildRegService();
        regsiterOTPSMSBroadcast();
        startResendTimer();
        resendOTPText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: resend OTP");
                new EventAnalyticsHelper().ItemClickEvent(ResetPasswordActivity.this, Constants.AnaylticsClassName.ResetPasswordScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.resendOtpclick.toString());
                resendOTP();
            }
        });

        confirmPassEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.reset_password || id == EditorInfo.IME_ACTION_DONE) {
                    /*new EventAnalyticsHelper().ItemClickEvent(ResetPasswordActivity.this, Constants.AnaylticsClassName.ResetPasswordScreen,
                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.resetPasswordClick.toString());*/
                    resetPassword(null);
                    return true;
                }
                return false;
            }
        });
    }

    private void buildRegService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }

    private void regsiterOTPSMSBroadcast() {
        IntentFilter intentFilter = new IntentFilter(Constants.AppConstants.OTP_SMS_BROADCAST);
        intentFilter.setPriority(999);
        otpSmsBroadcast = new OTPSMSBroadcast();
        registerReceiver(otpSmsBroadcast, intentFilter);
    }

    public class OTPSMSBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive!");
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();
                        String otp = message.substring(0, 4);
                        try {
                            if (senderNum.contains("CLCBRC")) {
                                otpEditText.setText("");
                                otpEditText.setText(otp);
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                TrackAnalytics.trackException(TAG, e.getMessage(), e);
            }
        }
    }

    private void startResendTimer() {
        try {
            timerStatus = 16;
            resendOTPText.setEnabled(false);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (timerStatus > 1) {
                        timerStatus -= 1;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                resendOTPText.setText(timerStatus + getString(R.string.sec));
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            resendOTPText.setText("Resend OTP");
                            resendOTPText.setEnabled(true);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resendOTP(){
        if(!UtilityMethods.isInternetConnected(this)){
            new EventAnalyticsHelper().logAPICallEvent(ResetPasswordActivity.this, Constants.AnaylticsClassName.ResetPasswordScreen,
                    null, Constants.ApiName.forgotPassword.toString(),Constants.AnalyticsEvents.FAILED,
                    getResources().getString(R.string.no_internet_connection));
            UtilityMethods.showSnackBar(findViewById(R.id.id_activity_reset_password),
                    getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    final long uid = getIntent().getLongExtra("UID", 0);
                    String phoneNo = "", stdCode = "";
                    if(getIntent().hasExtra("CountryStdCode") && !TextUtils.isEmpty(getIntent().getStringExtra("CountryStdCode"))){
                        stdCode = getIntent().getStringExtra("CountryStdCode");
                    }
                    if(getIntent().hasExtra("MobileNo")&& !TextUtils.isEmpty(getIntent().getStringExtra("MobileNo"))){
                        phoneNo = getIntent().getStringExtra("MobileNo");
                    }
                    CommonResponse resendOTPRequest = mUserEndPoint.forgotPassword(phoneNo).setCountrySTDCode(stdCode).setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (resendOTPRequest != null && resendOTPRequest.getStatus()) {
                        new EventAnalyticsHelper().logAPICallEvent(ResetPasswordActivity.this, Constants.AnaylticsClassName.ResetPasswordScreen,
                                null, Constants.ApiName.forgotPassword.toString(),Constants.AnalyticsEvents.SUCCESS,
                                null);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String phoneNo = "";
                                if(getIntent().hasExtra("CountryStdCode") && !TextUtils.isEmpty(getIntent().getStringExtra("CountryStdCode"))){
                                    phoneNo = getIntent().getStringExtra("CountryStdCode");
                                }
                                if(getIntent().hasExtra("MobileNo")&& !TextUtils.isEmpty(getIntent().getStringExtra("MobileNo"))){
                                    phoneNo = phoneNo + getIntent().getStringExtra("MobileNo");
                                }
                                UtilityMethods.showSnackBar(findViewById(R.id.id_activity_reset_password), "OTP sent to " + phoneNo, Snackbar.LENGTH_LONG);
                                startResendTimer();
                            }
                        });

                    } else {
                        if(resendOTPRequest != null && resendOTPRequest.getErrorMessage() != null) {
                            errorMsg = resendOTPRequest.getErrorMessage();
                        }else{
                            errorMsg = "Could not able to send otp.\nPlease try again!";
                        }

                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    errorMsg = getResources().getString(R.string.network_error_msg);
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                }catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Could not able to send otp.\nPlease try again!";
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    new EventAnalyticsHelper().logAPICallEvent(ResetPasswordActivity.this, Constants.AnaylticsClassName.ResetPasswordScreen,
                            null, Constants.ApiName.forgotPassword.toString(),Constants.AnalyticsEvents.FAILED,
                            errorMsg);
                    final String errMSG = errorMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            UtilityMethods.showErrorSnackBar(findViewById(R.id.id_activity_reset_password), errMSG, Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    private boolean isAllValuesValid(){
        if(TextUtils.isEmpty(otpEditText.getText().toString().trim())){
            otpEditText.setError("Invalid OTP");
            return false;
        }else if(otpEditText.getText().toString().trim().length() != 4){
            otpEditText.setError("Invalid OTP");
            return false;
        }else if(TextUtils.isEmpty(passEditText.getText().toString().trim())){
            passEditText.setError("Password cannot be empty");
            return false;
        }else if(TextUtils.isEmpty(confirmPassEditText.getText().toString().trim())){
            confirmPassEditText.setError("Confirm Password cannot be empty");
            return false;
        }else if(!passEditText.getText().toString().trim().equals(confirmPassEditText.getText().toString().trim())){
            confirmPassEditText.setError("Confrim password does not match with password");
            return false;
        }else{
            return true;
        }
    }

    public void resetPassword(View view){
        if(!UtilityMethods.isInternetConnected(this)){
            new EventAnalyticsHelper().logAPICallEvent(ResetPasswordActivity.this, Constants.AnaylticsClassName.ResetPasswordScreen,
                    null, Constants.ApiName.resetPasswordWithOTP.toString(),Constants.AnalyticsEvents.FAILED,
                    getResources().getString(R.string.no_internet_connection));
            UtilityMethods.showSnackBar(findViewById(R.id.id_activity_reset_password),
                    getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            return;
        }
        if(!isAllValuesValid()){
            return;
        }
        showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    String phoneNo = "", stdCode = "";
                    if(getIntent().hasExtra("CountryStdCode") && !TextUtils.isEmpty(getIntent().getStringExtra("CountryStdCode"))){
                        stdCode = getIntent().getStringExtra("CountryStdCode");
                    }
                    if(getIntent().hasExtra("MobileNo")&& !TextUtils.isEmpty(getIntent().getStringExtra("MobileNo"))){
                        phoneNo = getIntent().getStringExtra("MobileNo");
                    }
                    CommonResponse resendOTPRequest = mUserEndPoint.resetPasswordWithOTP(phoneNo.trim(),stdCode.trim(),
                                                otpEditText.getText().toString().trim(),passEditText.getText().toString())
                                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (resendOTPRequest != null && resendOTPRequest.getStatus()) {
                        new EventAnalyticsHelper().logAPICallEvent(ResetPasswordActivity.this, Constants.AnaylticsClassName.ResetPasswordScreen,
                                null, Constants.ApiName.resetPasswordWithOTP.toString(),Constants.AnalyticsEvents.SUCCESS,
                               null);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                View.OnClickListener positiveListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(customDialog != null){
                                            customDialog.dismiss();
                                            customDialog = null;
                                        }
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                };
                                dismissProgressBar();
                                customDialog = UtilityMethods.showAlert(ResetPasswordActivity.this,R.string.password_reset,
                                        "Your password has been reset successfully!",false,
                                        R.string.ok,positiveListener,-1,null);
                            }
                        });

                    } else {
                        if(resendOTPRequest != null && resendOTPRequest.getErrorMessage() != null) {
                            errorMsg = resendOTPRequest.getErrorMessage();
                        }else{
                            errorMsg = "Could not able to send otp.\nPlease try again!";
                        }

                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    errorMsg = getResources().getString(R.string.network_error_msg);
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                }catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Could not able to send otp.\nPlease try again!";
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    new EventAnalyticsHelper().logAPICallEvent(ResetPasswordActivity.this, Constants.AnaylticsClassName.ResetPasswordScreen,
                            null, Constants.ApiName.resetPasswordWithOTP.toString(),Constants.AnalyticsEvents.FAILED,
                            errorMsg);
                    final String errMSG = errorMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            UtilityMethods.showErrorSnackBar(findViewById(R.id.id_activity_reset_password), errMSG, Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(otpSmsBroadcast);
        } catch (Exception e) {
            e.printStackTrace();
            TrackAnalytics.trackException(TAG, e.getMessage(), e);
        }
        super.onDestroy();
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.ResetPasswordScreen);

        /*TrackAnalytics.trackEvent("LoginPage", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);*/
    }
}
