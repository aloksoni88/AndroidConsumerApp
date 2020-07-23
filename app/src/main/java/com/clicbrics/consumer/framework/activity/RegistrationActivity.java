package com.clicbrics.consumer.framework.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.TermsAndConditionActivity;
import com.clicbrics.consumer.adapter.CountryListAdapter;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.CountryDto;
import com.clicbrics.consumer.utils.UtilityMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.clicbrics.consumer.R.id.editTextEmailId;
import static com.clicbrics.consumer.R.id.editTextName;
import static com.clicbrics.consumer.R.id.password_editText;
import static com.clicbrics.consumer.R.id.phone_number_editText;


@SuppressWarnings("deprecation")
public class RegistrationActivity extends BaseActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    public static final List<String> PERMISSIONS = Arrays.asList( "public_profile", "email");
    private ImageButton fbLoginButton, gmail_btn_login;
    ;
    CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;

    private static final int GET_ACCOUNTS_PERMISSION = 0;
    private static final int OTP_VERIFICATION= 100;

    UserEndPoint mLoginWebService;
    TextView countryStdCodeTxt;
    EditText mNameText;
    EditText mEmailText;
    EditText mMobileText;
    EditText mPassText;
    CheckBox mAgreeCheckBox;
    Button mRegistrationButton;
    URL image_path = null;
    ImageView tnc_error;

    private final int RECEIVE_N_READ_SMS_PERMISSION = 100;

    private void init(){
        tnc_error = (ImageView) findViewById(R.id.error_tnc);
        countryStdCodeTxt = (TextView)findViewById(R.id.countryStdCode);
        mNameText = (EditText)findViewById(editTextName);
        mEmailText = (EditText)findViewById(editTextEmailId);
        mMobileText = (EditText)findViewById(phone_number_editText);
        mPassText = (EditText)findViewById(password_editText);

        mAgreeCheckBox = (CheckBox)findViewById(R.id.checkBox);
        mRegistrationButton = (Button) findViewById(R.id.register_button);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        showHashKey(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.registration_toolbar);
        //toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));
        setSupportActionBar(toolbar);
        ImageView backBtnImg = findViewById(R.id.id_back_button);
        backBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        /*getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();*/

        fbLoginButton = (ImageButton) findViewById(R.id.login_button);
        gmail_btn_login = (ImageButton) findViewById(R.id.gmail_btn_sign_in);

        buildLoginWebService();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackAnalytics.trackEvent("RegisterPage_fb_event", Constants.AppConstants.HOLD_TIME ,
                        1, RegistrationActivity.this);
                if(!UtilityMethods.isInternetConnected(RegistrationActivity.this)){
                    UtilityMethods.showErrorSnackBar(findViewById(R.id.parentLayout),
                            getResources().getString(R.string.network_error_msg),Snackbar.LENGTH_LONG);
                    return;
                }
                LoginManager.getInstance().logInWithReadPermissions(RegistrationActivity.this, PERMISSIONS);
            }
        });

        gmail_btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TrackAnalytics.trackEvent("RegisterPage_gplus_event", Constants.AppConstants.HOLD_TIME ,
                        1, RegistrationActivity.this);
                if(!UtilityMethods.isInternetConnected(RegistrationActivity.this)){
                    UtilityMethods.showErrorSnackBar(findViewById(R.id.parentLayout),
                            getResources().getString(R.string.network_error_msg),Snackbar.LENGTH_LONG);
                    return;
                }
                signInWithGplus();
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                OnFBLoginSuccess(loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                UtilityMethods.showErrorSnackBar(findViewById(R.id.parentLayout), "" +
                                                            error.getMessage(), Snackbar.LENGTH_LONG);
            }
        });

        init();

        mPassText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if(!TextUtils.isEmpty(mPassText.getError())){
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (mPassText.getRight() - mPassText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mPassText.setError(null);
                        mPassText.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                        if (mPassText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                            mPassText.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            mPassText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_black_24dp, 0);
                            mPassText.setSelection(mPassText.getText().length());
                        } else {
                            mPassText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            mPassText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_black_24dp, 0);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mAgreeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tnc_error.setVisibility(View.GONE);
                    mAgreeCheckBox.setTextColor(Color.BLACK);
                }
            }
        });


        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegistration(null);
            }
        });


        /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.M){
            if (!UtilityMethods.selfPermissionGranted(new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS},RegistrationActivity.this)) {
                ActivityCompat.requestPermissions(RegistrationActivity.this,
                        new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_STATE},
                        RECEIVE_N_READ_SMS_PERMISSION);
            }
        }*/

        if(getIntent().hasExtra("Name")){
            mNameText.setText(getIntent().getStringExtra("Name"));
        }
        if(getIntent().hasExtra("EmailId")){
            mEmailText.setText(getIntent().getStringExtra("EmailId"));
        }
        if(getIntent().hasExtra("LoginType")){
            loginType = Enum.valueOf(Constants.AppConstants.LoginType.class,getIntent().getStringExtra("LoginType"));
        }
        if(getIntent().hasExtra("ImagePath")){
            String imagePath = getIntent().getStringExtra("ImagePath");
            try {
                image_path = new URL(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(loginType == Constants.AppConstants.LoginType.Housing){
            if(getIntent().hasExtra("Phone")){
                mMobileText.setText(getIntent().getStringExtra("Phone"));
            }else if(getIntent().hasExtra("EmailId")){
                mEmailText.setText(getIntent().getStringExtra("EmailId"));
            }
        }
        setFieldsStatusByLoginType(loginType);
    }

    private void buildLoginWebService(){
        mLoginWebService = EndPointBuilder.getUserEndPoint();
    }

    String mFBAccessToken = "";

    private void OnFBLoginSuccess(LoginResult loginResult) {
        mFBAccessToken = loginResult.getAccessToken().getToken();
        if (!hasAllPermission(loginResult)) {
            LoginManager.getInstance().logInWithReadPermissions(RegistrationActivity.this, PERMISSIONS);
            return;
        }

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            Log.i(TAG, "response: " + object);
                            if (object != null) {
                                LinearLayout fb_google_login_layout = (LinearLayout) findViewById(R.id.fb_google_login_layout);
                                fb_google_login_layout.setVisibility(View.GONE);
                                mPassText.setText(mFBAccessToken);
                                mPassText.setVisibility(View.GONE);

                                EditText emailEditText = (EditText) findViewById(editTextEmailId);
                                if (object.has("email")) {
                                    emailEditText.setText(object.getString("email"));
                                    emailEditText.setEnabled(false);
                                }
                                if (object.has("name")) {
                                    mNameText.setText(object.getString("name"));
                                    mNameText.setEnabled(false);
                                }

                                try {
                                    image_path = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large");

                                    //saveFacebookProfilePicture(RegistrationActivity.this, image_path,mLoginWebService, 0);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                loginType = Constants.AppConstants.LoginType.Facebook;
                                mThirdPartyID = object.getString("id");
                                doRegistration(null);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            UtilityMethods.showSnackBar(findViewById(R.id.parentLayout)
                                    , getString(R.string.cant_login_from_FB), Snackbar.LENGTH_SHORT);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,gender,picture");
        request.setParameters(parameters);
        request.executeAsync();
        HousingLogs.d(TAG, "FB login success");
    }

    public static boolean hasAllPermission(LoginResult loginResult) {
        if (loginResult.getAccessToken().getPermissions().contains("email"))
            return true;
        else
            return false;
    }

    public static final int RC_SIGN_IN = 0;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        callbackManager.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN  && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: RC_SIGN_IN ");
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult: RC_SIGN_IN  RESULT_OK ");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
                handleGoogleSignInResult(result);
            }else{
                View.OnClickListener positiveListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View View) {
                        if(mCustomDialog!=null)
                            mCustomDialog.dismiss();
                    }
                };
                mCustomDialog = UtilityMethods.showAlert(RegistrationActivity.this, -1, getString(R.string.google_auth_error), true, R.string.ok, positiveListener, -1, null,null);
                //mProgressView.setVisibility(View.GONE);
                setFieldsStatus(true);
            }
        }else if(requestCode == OTP_VERIFICATION){
            if(resultCode == RESULT_OK)
            {

                //mProgressView.setVisibility(View.VISIBLE);
                //setFieldsStatus(false);

                setResult(RESULT_OK);
                finish();
            } else {
                View.OnClickListener positiveListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View View) {
                        if(mCustomDialog!=null)
                            mCustomDialog.dismiss();
                    }
                };
                mCustomDialog = UtilityMethods.showAlert(RegistrationActivity.this, -1 , getString(R.string.signup_error), true, R.string.ok, positiveListener, -1, null,null);
                //mProgressView.setVisibility(View.GONE);
                setFieldsStatus(true);
            }
        }
    }

    public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.clicbrics.consumer", PackageManager.GET_SIGNATURES); //Your package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.v("KeyHash:", keyHash);
            }
        } catch (PackageManager.NameNotFoundException e) {
            TrackAnalytics.trackException(TAG, "getting hashkey ", e);
        } catch (NoSuchAlgorithmException e) {
            TrackAnalytics.trackException(TAG, "getting hashkey", e);
        }
    }

    public static void saveFacebookProfilePicture(final Context context, final URL imageUrl, final UserEndPoint mLoginWebService,final int retry) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                File file = new File(context.getFilesDir(), Constants.AppConstants.PROFILE_PIC);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());

                    if (!file.getParentFile().isDirectory())
                        file.getParentFile().mkdirs();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
                } catch (Exception e) {
                    Log.e("RegistrationActivity", "" + e.getMessage());
                    TrackAnalytics.trackException(TAG, "saving FB profile picture ", e);
                }

                if (file.exists()) {
                //    UtilityMethods.updateImageOnServerInBackground(context,mLoginWebService,0);
                } else if (retry < 2) {
                 //   saveFacebookProfilePicture(context, imageUrl,mLoginWebService, retry + 1);
                }
            }
        }).start();


    }

    public void doRegistration(View view) {
        new EventAnalyticsHelper().ItemClickEvent(RegistrationActivity.this, Constants.AnaylticsClassName.RegisterPage,
                null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.submitClick.toString());
        TrackAnalytics.trackEvent("RegisterPage_submit", Constants.AppConstants.HOLD_TIME ,
                1, this);
        mNameText.setError(null);
        mEmailText.setError(null);
        mMobileText.setError(null);
        mPassText.setError(null);


        boolean cancel = false;
        View focusView = null;

        if (!isNameValid(mNameText.getText().toString())) {
            focusView = mNameText;
            cancel = true;
            Log.d(TAG,"nameValid!");
        }
        if (!isEmailValid(mEmailText.getText().toString().trim())) {
            focusView = mEmailText;
            cancel = true;
            Log.d(TAG,"email!");
        }
        if (!isMobileValid(mMobileText.getText().toString().trim())) {
            focusView = mMobileText;
            cancel = true;
            Log.d(TAG,"mobile!");
        }
        if(!mAgreeCheckBox.isChecked()){
            focusView = mAgreeCheckBox;
            tnc_error.setVisibility(View.VISIBLE);
            mAgreeCheckBox.setTextColor(Color.RED);
            cancel = true;
            Log.d(TAG,"CheckBox!");
        }
        if ((loginType== Constants.AppConstants.LoginType.Housing) && (!isPasswordValid(mPassText.getText().toString()))) {
            focusView = mPassText;
            cancel = true;
            Log.d(TAG,"Password!");
        }

        if (cancel) {
            Log.d(TAG,"cancel!");
            if(focusView != null){
                focusView.requestFocus();
            }

        } else {
            Log.d(TAG,"Reg!");
            registerUserOnServer();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult!=null && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RegistrationActivity.RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
                TrackAnalytics.trackException(TAG, e.getMessage(), e);
            }
        }else if(mConnectionResult==null && mGoogleApiClient!=null){
            mIntentInProgress = false;
            mGoogleApiClient.connect();
        }
    }

    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private boolean mSignInClicked;

    String mUserEmail;
    String mThirdPartyID = "";
    Constants.AppConstants.LoginType loginType = Constants.AppConstants.LoginType.Housing;

    @Override
    public void onConnected(Bundle arg0) {
        if (mGoogleApiClient.hasConnectedApi(Plus.API) && mGoogleApiClient.isConnected() && !mSignInClicked) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.stopAutoManage(RegistrationActivity.this);
            mGoogleApiClient.disconnect();
            // mGoogleApiClient.connect();
            return;
        }
        mSignInClicked = false;
        // Toast.makeText(this, R.string.user_is_connected, Toast.LENGTH_LONG).show();

        if (mGoogleApiClient.hasConnectedApi(Plus.API) && Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            try {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personPhotoUrl = currentPerson.getImage().getUrl();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, GET_ACCOUNTS_PERMISSION);
                    } else {
                        mUserEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                        mThirdPartyID = currentPerson.getId();
                        loginType = Constants.AppConstants.LoginType.Google;
                        URL imageUrl = new URL(personPhotoUrl);
                        //RegistrationActivity.saveFacebookProfilePicture(RegistrationActivity.this, imageUrl, mLoginWebService, 0);
                        if (mGoogleApiClient.hasConnectedApi(Plus.API) && mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        }
                        //signIn();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorMessage());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }
        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
//	        updateUI(false);
    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount currentPerson = result.getSignInAccount();
            try {
//                setFieldsStatus(true);
//                regsiter_progress_bar.setVisibility(View.GONE);
                if (currentPerson != null) {
                    String personName = currentPerson.getDisplayName();
                    Uri personPhotoUrl = currentPerson.getPhotoUrl();
                    String email = currentPerson.getEmail();

                    Log.e(TAG, "Name: " + personName + " , email: " + email
                            + ", Image: " + personPhotoUrl);

                    mPassText.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(email)) {
                        mEmailText.setText(email);
                        mEmailText.setEnabled(false);
                    }
                    if (!TextUtils.isEmpty(personName)) {
                        mNameText.setText(personName);
                        mNameText.setEnabled(false);
                    }
                    if (personPhotoUrl != null) {
                        image_path = new URL(personPhotoUrl.toString());
                        //saveFacebookProfilePicture(RegistrationActivity.this, imageUrl, mLoginWebService,0);
                    }
//	            UtilityMethods.saveStringInPref(getApplicationContext(), Constants.FB_USER_ID, user.getId());
//                    UtilityMethods.saveStringInPref(getApplicationContext(), Constants.FB_NAME, personName);
//                    UtilityMethods.saveStringInPref(getApplicationContext(), Constants.FB_EMAIL, email);
                    LinearLayout fb_google_login_layout = (LinearLayout) findViewById(R.id.fb_google_login_layout);
                    fb_google_login_layout.setVisibility(View.GONE);
//                    mThirdPartyID = currentPerson.getId();
                    loginType = Constants.AppConstants.LoginType.Google;
                    setFieldsStatusByLoginType(loginType);
                    doRegistration(null);
                } else {
                    Toast.makeText(RegistrationActivity.this, "Unable to login through google.", Toast.LENGTH_SHORT).show();
                    //UtilityMethods.showSnackBar(parentLayout, "Unable to login through google.", Snackbar.LENGTH_SHORT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.clearDefaultAccountAndReconnect();
            mGoogleApiClient.stopAutoManage(RegistrationActivity.this);
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == GET_ACCOUNTS_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    Person currentPerson = Plus.PeopleApi
                            .getCurrentPerson(mGoogleApiClient);
                    String personPhotoUrl = currentPerson.getImage().getUrl();
                    mUserEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    mThirdPartyID = currentPerson.getId();
                    loginType = Constants.AppConstants.LoginType.Google;
                    URL imageUrl = new URL(personPhotoUrl);
                    //RegistrationActivity.saveFacebookProfilePicture(RegistrationActivity.this,imageUrl,mLoginWebService,0);
                    if (mGoogleApiClient.hasConnectedApi(Plus.API) && mGoogleApiClient.isConnected()) {
                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    }
                    //signIn();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

        }else if(requestCode == RECEIVE_N_READ_SMS_PERMISSION){
            for( int i = 0; i < permissions.length; i++ ) {
                if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {

                } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                    UtilityMethods.showSnackBar(findViewById(R.id.parentLayout),getResources().getString(R.string.permision),Snackbar.LENGTH_SHORT);
                }
            }

        }
    }


    private boolean isNameValid(String name) {
        if (TextUtils.isEmpty(name)) {
            mNameText.setError(getString(R.string.name_cannot_be_empty));
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            mEmailText.setError(getString(R.string.email_cannot_be_empty));
            return false;
        } else if (!UtilityMethods.isValidEmail(email)) {
            mEmailText.setError(getString(R.string.invalid_emailid));
            return false;
        }
        return true;
    }

    private boolean isMobileValid(String phone) {
        if (TextUtils.isEmpty(phone)) {
            mMobileText.setError(getString(R.string.phone_no_cannot_be_empty));
            return false;
        } else if (phone.length() < 10) {
            mMobileText.setError(getString(R.string.invalid_phone_no));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if(TextUtils.isEmpty(password)){
            mPassText.setError(getString(R.string.password_cannot_be_empty));
            return false;
        }else if(password.length() < 1){
            mPassText.setError(getString(R.string.password_short));
            return false;
        }
        return true;
    }

    private void registerUserOnServer(){
        if(!UtilityMethods.isInternetConnected(this)){
            View.OnClickListener positiveListener = new View.OnClickListener() {

                @Override
                public void onClick(View dialog) {
                    dismissProgressBar();
                    if(mCustomDialog!=null)
                        mCustomDialog.dismiss();
                }
            };


            mCustomDialog = UtilityMethods.showAlert(RegistrationActivity.this, -1/*R.string.cannot_connect*/, getString(R.string.network_error_msg), true, R.string.ok, positiveListener, -1, null,null);
            setFieldsStatus(true);
            return ;
        }

        //showProgress(true);
        //setFieldsStatus(false);

        showProgressBar();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    UserEndPoint.SignUpUser signUpUser = mLoginWebService.signUpUser(mNameText.getText().toString(),
                                                    mEmailText.getText().toString().trim(),mMobileText.getText().toString().trim(),
                                                    "Male", loginType.toString());
                    signUpUser.setCountryName("India");
                    signUpUser.setCountrySTDCode(countryStdCodeTxt.getText().toString());
                    Log.d(TAG,"loginType->"+loginType);
                    switch(loginType){
                        case Housing:{
                            signUpUser.setPassword(mPassText.getText().toString());
                        }break;
                        case Facebook:{
                            if(TextUtils.isEmpty(mThirdPartyID)){
                                mThirdPartyID = getIntent().hasExtra("ThirdPartyId") ? getIntent().getStringExtra("ThirdPartyId") : "";
                            }
                            signUpUser.setThirdPartyID(mThirdPartyID);
                        }break;
                        case Google:{

                        }break;
                    }
                    signUpUser.setRequestHeaders(UtilityMethods.getHttpHeaders());
                    final CommonResponse loginResponse = signUpUser.execute();
                    Log.d(TAG,"Status->"+loginResponse.getStatus());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            Log.d(TAG,"Status->"+loginResponse.getStatus()+"    "+loginResponse.getUid());
                            if(loginResponse.getStatus()){
                                new EventAnalyticsHelper().logAPICallEvent(RegistrationActivity.this, Constants.AnaylticsClassName.RegisterPage,
                                        null, Constants.ApiName.signUpUser.toString(),Constants.AnalyticsEvents.SUCCESS,
                                        null);

                                if(loginType.equals(Constants.AppConstants.LoginType.Facebook)) {
                                    LoginManager.getInstance().logOut();
                                }

                                //we should save the user id and other credentials after succesfull verification of OTP
                                //UtilityMethods.saveLongInPref(RegistrationActivity.this, Constants.ServerConstants.USER_ID, loginResponse.getUid());
                                //UtilityMethods.saveStringInPref(RegistrationActivity.this, Constants.AppConstants.LOGIN_TYPE_PREF_KEY, loginType.toString());
                                Intent intent = new Intent(RegistrationActivity.this, OTPActivity.class);
                                intent.putExtra("UID", loginResponse.getUid());
                                intent.putExtra("EMAIL", mEmailText.getText().toString());
                                intent.putExtra("PASSWORD", mPassText.getText().toString());
                                intent.putExtra("MOBILENO", mMobileText.getText().toString().trim());
                                intent.putExtra("LoginType", loginType.toString());
                                intent.putExtra("NAME", mNameText.getText().toString());
                                intent.putExtra("COUNTRY_CODE",countryStdCodeTxt.getText().toString());
                                if(image_path!=null){
                                    intent.putExtra("IMAGE_URL",image_path.toString());
                                }
                                if (!TextUtils.isEmpty(mThirdPartyID))
                                    intent.putExtra("ThirdPartyID", mThirdPartyID);
                                startActivityForResult(intent, OTP_VERIFICATION);
                            }else {
                                if(loginResponse != null) {
                                    View.OnClickListener positiveListener = new View.OnClickListener() {

                                        @Override
                                        public void onClick(View View) {
                                            if (mCustomDialog != null)
                                                mCustomDialog.dismiss();
                                        }
                                    };
                                    mCustomDialog = UtilityMethods.showAlert(RegistrationActivity.this, -1, loginResponse.getErrorMessage(), false, R.string.ok, positiveListener, -1, null, null);
                                    setFieldsStatusByLoginType(loginType);
                                }else{
                                    errorMsg = "Could not able to register.\nPlease try again";
                                }
                            }
                        }
                    });
                }catch (UnknownHostException e){
                    errorMsg = getResources().getString(R.string.network_error_msg);
                    e.printStackTrace();
                    dismissProgressBar();
                }catch (Exception e){
                    errorMsg = getResources().getString(R.string.something_went_wrong);
                    e.printStackTrace();
                    dismissProgressBar();
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    new EventAnalyticsHelper().logAPICallEvent(RegistrationActivity.this, Constants.AnaylticsClassName.RegisterPage,
                            null, Constants.ApiName.signUpUser.toString(),Constants.AnalyticsEvents.FAILED,
                            errorMsg);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            View.OnClickListener positiveListener = new View.OnClickListener() {

                                @Override
                                public void onClick(View View) {
                                    if (mCustomDialog != null)
                                        mCustomDialog.dismiss();
                                }
                            };
                            mCustomDialog = UtilityMethods.showAlert(RegistrationActivity.this, -1, errorMsg, false, R.string.ok, positiveListener, -1, null, null);
                            setFieldsStatusByLoginType(loginType);
                        }
                    });
                }
            }
        }).start();
    }

    private void setFieldsStatus(boolean isEnable){
        mNameText.setEnabled(isEnable);
        mEmailText.setEnabled(isEnable);
        mMobileText.setEnabled(isEnable);
        mPassText.setEnabled(isEnable);
        mAgreeCheckBox.setEnabled(isEnable);
        mRegistrationButton.setEnabled(isEnable);

    }

    private void setFieldsStatusByLoginType(Constants.AppConstants.LoginType loginType){
        if(loginType != Constants.AppConstants.LoginType.Housing){
            final EditText editTextName = (EditText)findViewById(R.id.editTextName);
            final EditText editTextEmailId = (EditText)findViewById(R.id.editTextEmailId);
            if(editTextName.getText() != null && !editTextName.getText().toString().trim().isEmpty()) {
                editTextName.setEnabled(false);
            }
            if(editTextEmailId.getText() != null && !editTextEmailId.getText().toString().trim().isEmpty()) {
                editTextEmailId.setEnabled(false);
            }
            LinearLayout fb_google_login_layout = (LinearLayout) findViewById(R.id.fb_google_login_layout);
            fb_google_login_layout.setVisibility(View.GONE);
            mPassText.setVisibility(View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showProgress(final boolean show) {
        /* if(mProgressView != null && !isFinishing() && !isDestroyed()){
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }*/
    }

    Dialog customDialog;

    List<CountryDto> countryList;
    String selectedCountryName = "";
    String selectedCountryCode = "";
    public void showCountryList(View v){

        if(customDialog == null) {
            customDialog = new Dialog(this);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View view = getLayoutInflater().inflate(R.layout.dialog_country_list, null);
            ListView listView =  (ListView)view.findViewById(R.id.countryList);
            EditText countrySearch = (EditText) view.findViewById(R.id.inputCountrySearch);

            if(countryList == null){
                countryList = new ArrayList<>();
                final List<String> countryNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.country_names)));
                final List<String> countryStdCodes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.country_std_code)));
                for(int i=0; i< countryNames.size() ; i++){
                    CountryDto countryDto = new CountryDto();
                    countryDto.setCountryName(countryNames.get(i));
                    countryDto.setCountryCode(countryStdCodes.get(i));
                    countryDto.setFlagName(countryNames.get(i) + ".png");
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
                        if(!TextUtils.isEmpty(countryDto.getCountryCode())) {
                            countryStdCodeTxt.setText(countryDto.getCountryCode());
                            //countryFlagImg.setImageBitmap(countryDto.getFlagImage());
                            selectedCountryCode = countryDto.getCountryCode();
                            selectedCountryName = countryDto.getCountryName();
                        }
                        customDialog.dismiss();
                        hideSoftKeyboard();
                        customDialog = null;
                    } catch (Exception e) {
//                        ((ImageView)findViewById(R.id.countryFlag)).setImageResource(R.drawable.india);
                        e.printStackTrace();
                    }
                }
            });
            countrySearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d(TAG,"beforeTextChanged : " + s);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(TAG,"onTextChanged : " + s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG,"afterTextChanged : " + s);
                    adapter.getFilter().filter(s);
                }
            });
            customDialog.setContentView(view);
            final ViewGroup mContainer = (ViewGroup) customDialog.findViewById(
                    android.R.id.content).getRootView();
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
            if(!customDialog.isShowing()){
                customDialog.show();
            }
        }
    }

    /*private ArrayList<Bitmap> getBitmapListFromAsset(List<String> listFlagNames) {
        AssetManager assetManager = getAssets();
        for(int i=0; i<listFlagNames.size(); i++){
            try {
                InputStream inputStream = assetManager.open(Constants.AppConstants.COUNTRY_FLAG_DIR + File.separator +  listFlagNames.get(i).trim()+".png");
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
               // countryFlagBitmap.add(bitmap);
                inputStream.close();
            } catch (IOException e) {
                Bitmap.Config conf = Bitmap.Config.ARGB_4444;
                Bitmap bmp = Bitmap.createBitmap(25, 20, conf);
                //countryFlagBitmap.add(bmp);
                e.printStackTrace();
            }
        }
        return countryFlagBitmap;
    }*.

    private Bitmap getBitmapFromAsset(String strName) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open(Constants.AppConstants.COUNTRY_FLAG_DIR + File.separator +  strName.trim());
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }

    protected void onResume() {
        super.onResume();
        findViewById(R.id.register_button).setEnabled(true);
        setFieldsStatus(true);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.registration_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done: {
                doRegistration(null);
            }
            break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }*/


    public void doShowTnC (View v){
        if(!UtilityMethods.isInternetConnected(this)){
            UtilityMethods.showErrorSnackBar(findViewById(R.id.parentLayout),
                    getResources().getString(R.string.network_error_msg),Snackbar.LENGTH_LONG);
            return;
        }
        Intent intent = new Intent(this, TermsAndConditionActivity.class);
        startActivity(intent);
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.RegisterPage);

        /*TrackAnalytics.trackEvent("RegisterPage", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                try {
                    LoginActivity.fbName = "";
                    LoginActivity.mUserEmail = "";
                    LoginActivity.googleName = "";
                    LoginActivity.mLoginType = Constants.AppConstants.LoginType.Housing;
                    onBackPressed();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            LoginActivity.fbName = "";
            LoginActivity.mUserEmail = "";
            LoginActivity.googleName = "";
            LoginActivity.mLoginType = Constants.AppConstants.LoginType.Housing;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }
}
