package com.clicbrics.consumer.framework.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.buy.housing.backend.userEndPoint.model.SendOTPToPhoneNumberResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProfileScreen;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.customview.PinEntryView;
import com.clicbrics.consumer.framework.gcm.GCMRegistration;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.UnknownHostException;

import static com.clicbrics.consumer.R.id.parentLayout;


@SuppressWarnings("deprecation")
public class OTPActivity extends BaseActivity {

    private static final String TAG = OTPActivity.class.getSimpleName();
    UserEndPoint mLoginWebService = null;
    Handler mHandler;
    int pStatus = 0;
    ProgressDialog progressDialog;
    String countryCode= "",mobNO = "";
    ProgressBar resend_otp_pbar;
    OTPSMSBroadcast otpSmsBroadcast;
    private boolean isFromEditProfile;
    private String otpFromProfile;

    //EditText otpCode1Text,otpCode2Text,otpCode3Text,otpCode4Text;
    PinEntryView otpEditText;
    TextView otpMsgInfo,otpResendCounterText;
    Button resendCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_otp_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
        init();
        mHandler = new Handler();
        buildRegService();
        regsiterOTPSMSBroadcast();

        try {
            isFromEditProfile = getIntent().getBooleanExtra("isFromEditProfile", false);
        } catch (Exception ex) {
            isFromEditProfile = false;
        }

        try {
            otpFromProfile = getIntent().getStringExtra("oTP");
        } catch (Exception ex) {
        }
    }

    private void init() {
        /*otpCode1Text = (EditText) findViewById(R.id.id_otp_code1);
        otpCode2Text = (EditText) findViewById(R.id.id_otp_code2);
        otpCode3Text = (EditText) findViewById(R.id.id_otp_code3);
        otpCode4Text = (EditText) findViewById(R.id.id_otp_code4);*/
        otpMsgInfo = (TextView) findViewById(R.id.otpMsg);
        otpResendCounterText = (TextView) findViewById(R.id.id_otp_resend_timer);
        resendCodeText = findViewById(R.id.id_resend_code);

        showSoftKeyboard();
        countryCode = getIntent().getStringExtra("COUNTRY_CODE");
        mobNO = getIntent().getStringExtra("MOBILENO");
        otpMsgInfo.setText(getString(R.string.otp_msg) + "\n" +  countryCode +  "-" + mobNO);
        //resendCodeText.setAlpha(0.4f);
        resendCodeText.setEnabled(false);

       /* otpCode1Text.addTextChangedListener(new GenericTextWatcher(otpCode1Text));
        otpCode2Text.addTextChangedListener(new GenericTextWatcher(otpCode2Text));
        otpCode3Text.addTextChangedListener(new GenericTextWatcher(otpCode3Text));
        otpCode4Text.addTextChangedListener(new GenericTextWatcher(otpCode4Text));

        //otpCode1Text.setOnKeyListener(new GenericKeyListener(otpCode1Text));
        otpCode2Text.setOnKeyListener(new GenericKeyListener(otpCode2Text));
        otpCode3Text.setOnKeyListener(new GenericKeyListener(otpCode3Text));
        otpCode4Text.setOnKeyListener(new GenericKeyListener(otpCode4Text));


        otpCode4Text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onclickSubmit();
                }
                return false;
            }
        });*/

        otpEditText = (PinEntryView) findViewById(R.id.pin_entry_border);

        otpEditText.setOnPinEnteredListener(new PinEntryView.OnPinEnteredListener() {
            @Override
            public void onPinEntered(String pin) {
                if(pin.length()==4){
                    hideSoftKeyboard();
                    onclickSubmit();
                }
            }
        });


        /*otpCode1Text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if(keycode == KeyEvent.KEYCODE_DEL){
                    otpCode1Text.setText("");
                    otpCode1Text.requestFocus();
                }
                else{
                    otpCode1Text.requestFocus();
                }
                return false;
            }
        });

        otpCode2Text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if(keycode == KeyEvent.KEYCODE_DEL){
                    //otpCode2Text.setText(null);
                    otpCode2Text.setText("");
                    otpCode1Text.requestFocus();
                }
                else{
                    otpCode2Text.requestFocus();
                }
                return false;
            }
        });
        otpCode3Text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if(keycode == KeyEvent.KEYCODE_DEL){
                    //otpCode3Text.setText(null);
                    otpCode3Text.setText("");
                    otpCode2Text.requestFocus();
                }
                else{
                    otpCode3Text.requestFocus();
                }
                return false;
            }
        });
        otpCode4Text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if(keycode == KeyEvent.KEYCODE_DEL){
                    //otpCode4Text.setText(null);
                    otpCode4Text.setText("");
                    otpCode3Text.requestFocus();
                }else{
                    otpCode4Text.requestFocus();
                }
                return false;
            }
        });*/

        resendCodeText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new EventAnalyticsHelper().ItemClickEvent(OTPActivity.this, Constants.AnaylticsClassName.OTPScreen,
                                null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.resendOtpclick.toString());
                        onClickResendOTP(null);
                    }
                });
        startResendTimer();
    }

    /*private class GenericTextWatcher implements TextWatcher{

        View view;
        private GenericTextWatcher(View view)
        {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!TextUtils.isEmpty(editable)) {
                String text = editable.toString();
                switch (view.getId()) {
                    case R.id.id_otp_code1:
                        if (text.length() == 1)
                            otpCode2Text.requestFocus();
                        break;
                    case R.id.id_otp_code2:
                        if (text.length() == 1)
                            otpCode3Text.requestFocus();
                        break;
                    case R.id.id_otp_code3:
                        if (text.length() == 1)
                            otpCode4Text.requestFocus();
                        break;
                    case R.id.id_otp_code4:
                        break;
                }
            }
        }
    }

    private class GenericKeyListener implements View.OnKeyListener{

        View view;
        private GenericKeyListener(View view)
        {
            this.view = view;
        }

        @Override
        public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
            switch (view.getId()){
                case R.id.id_otp_code1:
                    if(keycode == KeyEvent.KEYCODE_DEL) {
                        //otpCode1Text.setText("");
                    }
                    break;
                case R.id.id_otp_code2:
                    if(keycode == KeyEvent.KEYCODE_DEL) {
                        //otpCode2Text.setText("");
                        otpCode1Text.requestFocus();
                    }
                    break;
                case R.id.id_otp_code3 :
                    if(keycode == KeyEvent.KEYCODE_DEL) {
                        //otpCode3Text.setText("");
                        otpCode2Text.requestFocus();
                    }
                    break;
                case R.id.id_otp_code4:
                    if(keycode == KeyEvent.KEYCODE_DEL) {
                        //otpCode4Text.setText("");
                        otpCode3Text.requestFocus();
                    }
                    break;
            }
            return false;
        }
    }*/

    private void buildRegService() {
        mLoginWebService = EndPointBuilder.getUserEndPoint();
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

    private void startResendTimer() {
        try {
            pStatus = 31;
            //resendCodeText.setAlpha(0.4f);
            resendCodeText.setEnabled(false);
            otpResendCounterText.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (pStatus > 0) {
                        pStatus -= 1;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                otpResendCounterText.setText(pStatus + getString(R.string.sec));
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
                            //resendCodeText.setAlpha(1.0f);
                            resendCodeText.setEnabled(true);
                            otpResendCounterText.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        //Log.d(TAG,"substring->"+otp);
                        try {
                            if (senderNum.contains("CLCBRC")) {
                                otpEditText.clearText();
                                otpEditText.setText(otp);
                                //onclickSubmit();
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

    private void onclickSubmit() {
        hideSoftKeyboard();
        TrackAnalytics.trackEvent("OTPScreen_submit_event", Constants.AppConstants.HOLD_TIME ,
                1, OTPActivity.this);
        if(!UtilityMethods.isInternetConnected(this)){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.parentLayout), getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction("TRY AGAIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onclickSubmit();
                        }
                    });
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.uber_red));
            snackbar.show();
            return;
        }

        if (!TextUtils.isEmpty(otpEditText.getText())){
            final  String otpValue = otpEditText.getText().toString();
            if (isFromEditProfile) {
                if (otpFromProfile.equals(otpValue)) {
                    // long uid = Long.parseLong(UtilityMethods.getStringInPref(this, Constants.ServerConstants.USER_ID, "-1"));
                    Intent otpIntent = new Intent();
                    otpIntent.putExtra("OTP", otpValue);
                    setResult(Activity.RESULT_OK , otpIntent );
//                        if (!getIntent().hasExtra("isFromEditProfile")) {
//                            startActivity(new Intent(OTPActivity.this, ProfileActivity.class));
//                        }
                    finish();
//                        if(HousingApplication.mLoginSuccessCallback!=null)
//                            HousingApplication.mLoginSuccessCallback.isLoggedin();
                } else {
                    final String errMSG = getResources().getString(R.string.invalid_otp);
                    View.OnClickListener positiveListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View View) {
                            if (mCustomDialog != null)
                                mCustomDialog.dismiss();
                        }
                    };
                    mCustomDialog = UtilityMethods.showAlert(OTPActivity.this, R.string.error, errMSG, false, R.string.ok, positiveListener, -1, null, null);
                }
                return;
            }

            showProgress(getString(R.string.verifying_user));
            final long uid = getIntent().hasExtra("UID") ? getIntent().getLongExtra("UID",0):-1;
            final String emailId = getIntent().getStringExtra("EMAIL");
            final String password = getIntent().getStringExtra("PASSWORD");
            final String mobileno = getIntent().getStringExtra("MOBILENO");
            final String username = getIntent().getStringExtra("NAME");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String errorMsg = "";
                    try {
                        UserEndPoint.VerifyOTP verifyOTP = mLoginWebService.verifyOTP(uid, otpValue);
                        long virtualId = UtilityMethods.getLongInPref(OTPActivity.this,Constants.AppConstants.VIRTUAL_UID,-1);
                        if(virtualId != -1){
                            verifyOTP.setVirtualId(virtualId);
                        }
                        verifyOTP.setSource(Constants.AppConstants.SOURCE);
                        CommonResponse commonResponse = verifyOTP.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        if (commonResponse != null && commonResponse.getStatus()) {
                            new EventAnalyticsHelper().logAPICallEvent(OTPActivity.this, Constants.AnaylticsClassName.OTPScreen,
                                    null, Constants.ApiName.verifyOTP.toString(),Constants.AnalyticsEvents.SUCCESS,
                                    null);
                            Log.i(TAG, "Verify OTP called with success ");
                            /*GCMRegistration gcmRegistration = new GCMRegistration(OTPActivity.this);
                            String gcmID = gcmRegistration.getRegistrationId();
                            if (TextUtils.isEmpty(gcmID)) {
                                gcmID = gcmRegistration.registerGCM();
                            }*/
                            Constants.AppConstants.LoginType loginType = Enum.valueOf(Constants.AppConstants.LoginType.class, getIntent().getSerializableExtra("LoginType").toString());
                            UserEndPoint.Login loginResponse = mLoginWebService.login(loginType.toString(), emailId);
                            switch (loginType) {
                                case Housing: {
                                    loginResponse.setPassword(password);
                                    loginResponse.setEmail(getIntent().getStringExtra("EMAIL"));
                                }
                                break;
                                case Facebook: {
                                    loginResponse.setRThirdPartyID(getIntent().getStringExtra("ThirdPartyID"));
                                }
                                break;
                                case Google: {
                                    loginResponse.setRThirdPartyID(getIntent().getStringExtra("ThirdPartyID"));
                                    loginResponse.setEmail(getIntent().getStringExtra("EMAIL"));
                                }
                                break;
                            }
                            Log.d(TAG, "response->" + commonResponse.getErrorMessage() + "   " + commonResponse.getStatus()
                                    + "   " + commonResponse.toPrettyString());
                            Log.i(TAG, "Login Success with OTP submit and status-" + commonResponse.getStatus()
                                    + " " + commonResponse.getErrorMessage());

                            UtilityMethods.saveLongInPref(OTPActivity.this, Constants.ServerConstants.USER_ID,
                                        uid);
                            UtilityMethods.saveStringInPref(OTPActivity.this, Constants.ServerConstants.EMAIL, emailId);
                            UtilityMethods.saveStringInPref(OTPActivity.this, Constants.ServerConstants.MOBILE, mobileno);
                            UtilityMethods.saveStringInPref(OTPActivity.this, Constants.ServerConstants.NAME, username);
                            UtilityMethods.saveStringInPref(OTPActivity.this, Constants.AppConstants.LOGIN_TYPE_PREF_KEY,
                                    getIntent().getStringExtra("LoginType"));
                            UtilityMethods.saveStringInPref(OTPActivity.this, Constants.AppConstants.USER_NAME_PREF_KEY,
                                    getIntent().getStringExtra("NAME"));
                            UtilityMethods.saveStringInPref(OTPActivity.this, Constants.AppConstants.EMAIL_PREF_KEY,
                                    getIntent().getStringExtra("EMAIL"));
                            UtilityMethods.saveStringInPref(OTPActivity.this, Constants.AppConstants.SELECTED_COUNTRY_STD_CODE,
                                    getIntent().getStringExtra("COUNTRY_CODE"));
                            String mobNo = getIntent().getStringExtra("MOBILENO");
                            UtilityMethods.saveStringInPref(OTPActivity.this, Constants.AppConstants.MOBILE_PREF_KEY,
                                    mobNo);
                            UtilityMethods.saveStringInPref(OTPActivity.this, Constants.AppConstants.PASS_PREF_KEY,
                                    password);
                            UtilityMethods.saveBooleanInPref(OTPActivity.this,Constants.AppConstants.IS_PASSWORD_AVAILABLE_PREF_KEY,
                                    loginType.equals(Constants.AppConstants.LoginType.Housing)?true:false);
                            try {
                                UtilityMethods.removeFromPref(OTPActivity.this, Constants.AppConstants.IMAGE_URL);
                                if (getIntent().hasExtra("IMAGE_URL")) {
                                    URL url = new URL(getIntent().getStringExtra("IMAGE_URL"));
                                    UtilityMethods.saveStringInPref(OTPActivity.this,Constants.AppConstants.IMAGE_URL,url.toString());
                                    UtilityMethods.updateImageOnServerInBackground(OTPActivity.this, mLoginWebService, 0 , url.toString());
                                    //saveFacebookProfilePicture(OTPActivity.this, url,
                                    //        mLoginWebService, 0);
                                }

                            } catch (Exception e) {
                                TrackAnalytics.trackException(TAG, e.getMessage(), e);
                            }


                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }

                                    Intent otpIntent = new Intent();
                                    otpIntent.putExtra("OTP", otpValue);
                                    setResult(Activity.RESULT_OK , otpIntent );
                                    //setResult(RESULT_OK);
                                    if (getIntent().hasExtra("isFromEditProfile")) {
                                        startActivity(new Intent(OTPActivity.this, ProfileScreen.class)
                                                        .putExtra("OTP", otpValue));
                                    }
                                    finish();
                                }
                            });

                        } else {
                            if(commonResponse != null && commonResponse.getErrorMessage() != null) {
                                errorMsg = commonResponse.getErrorMessage();
                            }else{
                                errorMsg = "Could not verify OTP.\nPlease try again";
                            }
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        errorMsg = getString(R.string.network_error_msg);
                        TrackAnalytics.trackException(TAG, e.getMessage(), e);
                    }catch (Exception e) {
                        e.printStackTrace();
                        errorMsg = getString(R.string.unable_to_process_request);
                        TrackAnalytics.trackException(TAG, e.getMessage(), e);
                    }
                    if (!TextUtils.isEmpty(errorMsg)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        final String errMSG = errorMsg;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                new EventAnalyticsHelper().logAPICallEvent(OTPActivity.this, Constants.AnaylticsClassName.OTPScreen,
                                        null, Constants.ApiName.resendOTPRequest.toString(),Constants.AnalyticsEvents.FAILED,
                                        errMSG);
                                View.OnClickListener positiveListener = new View.OnClickListener() {

                                    @Override
                                    public void onClick(View View) {
                                        if (mCustomDialog != null)
                                            mCustomDialog.dismiss();
                                    }
                                };

                                mCustomDialog = UtilityMethods.showAlert(OTPActivity.this, R.string.error, errMSG, false, R.string.ok, positiveListener, -1, null, null);

                            }
                        });
                    }
                }
            }).start();
        } else {
            UtilityMethods.showSnackBar(findViewById(parentLayout), getString(R.string.otp_should_not_be_empty), Snackbar.LENGTH_SHORT);
        }
    }

    public void onClickResendOTP(View view) {
        TrackAnalytics.trackEvent("OTPScreen_resend_otp", Constants.AppConstants.HOLD_TIME ,
                1, this);
        if (isFromEditProfile) {
            sendOTP();
            return;
        }

        showProgress(getString(R.string.resending_otp));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    final long uid = getIntent().getLongExtra("UID", 0);
                    CommonResponse resendOTPRequest = mLoginWebService.resendOTPRequest(uid).setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (resendOTPRequest != null && resendOTPRequest.getStatus()) {
                        new EventAnalyticsHelper().logAPICallEvent(OTPActivity.this, Constants.AnaylticsClassName.OTPScreen,
                                null, Constants.ApiName.resendOTPRequest.toString(),Constants.AnalyticsEvents.SUCCESS,
                                null);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                                /*if(otpEditText != null){
                                    otpEditText.clearText();
                                }*/
                                String phoneNo = "";
                                if(getIntent().hasExtra("phoneNo") && !TextUtils.isEmpty(getIntent().getStringExtra("phoneNo"))){
                                    phoneNo = getIntent().getStringExtra("phoneNo");
                                }
                                UtilityMethods.showSnackBar(findViewById(parentLayout), getString(R.string.otp_msg)+" " + countryCode +  "-" + mobNO, Snackbar.LENGTH_LONG);
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
                    new EventAnalyticsHelper().logAPICallEvent(OTPActivity.this, Constants.AnaylticsClassName.OTPScreen,
                            null, Constants.ApiName.resendOTPRequest.toString(),Constants.AnalyticsEvents.FAILED,
                            errorMsg);
                    final String errMSG = errorMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            UtilityMethods.showErrorSnackBar(findViewById(parentLayout), errMSG, Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    private void showProgress(String msg) {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(OTPActivity.this);
        //progressDialog.setTitle(getString(R.string.register_user));
        progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bg));
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bg));
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void sendOTP() {
        showProgressBar();//getString(R.string.please_wait));
        //final long rUID = UtilityMethods.getLongInPref(OTPActivity.this, Constants.ServerConstants.USER_ID, -1);
        final long rUID = getIntent().hasExtra("UID") ? getIntent().getLongExtra("UID",0):-1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMSG = "";
                try {
                    final SendOTPToPhoneNumberResponse loginResponse = mLoginWebService.verifyPhoneNumber(rUID)
                            .setPhoneNumber(getIntent().getStringExtra("phoneNo")).setId(rUID)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();

                    if (loginResponse.getStatus()) {
                        new EventAnalyticsHelper().logAPICallEvent(OTPActivity.this, Constants.AnaylticsClassName.OTPScreen,
                                null, Constants.ApiName.verifyPhoneNumber.toString(),Constants.AnalyticsEvents.SUCCESS,
                                null);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //progressBar.setVisibility(View.GONE);
                                dismissProgressBar();

                                Intent otpIntent = new Intent();
                                final String otpValue = otpEditText.getText().toString();
                                otpIntent.putExtra("OTP", otpValue);
                                setResult(Activity.RESULT_OK , otpIntent );

//                                if (!getIntent().hasExtra("isFromEditProfile")) {
//                                    startActivity(new Intent(OTPActivity.this, ProfileActivity.class));
//                                }
                                finish();
                            }
                        });
                    } else {
                        errorMSG = loginResponse.getErrorMessage(

                        );
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressBar();
                            }
                        });
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    errorMSG = OTPActivity.this.getResources().getString(R.string.network_error_msg);
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                }catch (Exception e) {
                    e.printStackTrace();
                    errorMSG = OTPActivity.this.getResources().getString(R.string.something_went_wrong);
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                }
                if (!TextUtils.isEmpty(errorMSG)) {
                    final String errorMsg = errorMSG;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //progressBar.setVisibility(View.GONE);
                            dismissProgressBar();
                            View.OnClickListener positiveListener = new View.OnClickListener() {

                                @Override
                                public void onClick(View dialog) {
                                    if (mCustomDialog != null)
                                        mCustomDialog.dismiss();
                                }
                            };
                            mCustomDialog = UtilityMethods.showAlert(OTPActivity.this, R.string.alert, errorMsg, true, R.string.ok, positiveListener, -1, null, null);
                        }
                    });
                }


            }
        }).start();
    }

    private static void saveFacebookProfilePicture(final Context context, final URL imageUrl, final UserEndPoint mLoginWebService, final int retry) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                File file = new File(context.getFilesDir(), Constants.AppConstants.PROFILE_PIC);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());

                    if (!file.getParentFile().isDirectory())
                        file.getParentFile().mkdirs();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                } catch (Exception e) {
                    Log.e("RegistrationActivity", "" + e.getMessage());
                    TrackAnalytics.trackException(TAG, "saving FB profile picture ", e);
                }

                if (file.exists()) {
                    UtilityMethods.updateImageOnServerInBackground(context, mLoginWebService, 0, String.valueOf(imageUrl));
                } else if (retry < 2) {
                    saveFacebookProfilePicture(context, imageUrl, mLoginWebService, retry + 1);
                }
            }
        }).start();


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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.OTPScreen);

    /*    TrackAnalytics.trackEvent("OTPScreen", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);*/
    }

}

