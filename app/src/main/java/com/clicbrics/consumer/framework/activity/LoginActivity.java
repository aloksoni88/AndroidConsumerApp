package com.clicbrics.consumer.framework.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.buy.housing.backend.userEndPoint.model.SearchCriteria;
import com.buy.housing.backend.userEndPoint.model.UserLoginResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.activities.AboutUsActivity;
import com.clicbrics.consumer.activities.ConceirgeActivity;
import com.clicbrics.consumer.activities.NotificationActivity;
import com.clicbrics.consumer.activities.WebviewActivity;
import com.clicbrics.consumer.analytics.AppEventAnalytics;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProfileScreen;
import com.clicbrics.consumer.view.fragment.MapFragment;
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
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.firebase.iid.FirebaseInstanceId;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.CountryListAdapter;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.CountryDto;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.SaveSearchWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.Manifest.permission.READ_CONTACTS;
import static com.clicbrics.consumer.framework.activity.RegistrationActivity.PERMISSIONS;
import static com.clicbrics.consumer.framework.activity.RegistrationActivity.RC_SIGN_IN;

@SuppressWarnings("deprecation")
public class LoginActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_UP = 101;
    private static final String TAG = "LoginActivity";
    Handler mHanlder;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int GET_ACCOUNTS_PERMISSION = 1;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    //Constants.AppConstants.LoginType loginType = Constants.AppConstants.LoginType.Housing;
    UserEndPoint mLoginWebService;

    CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    LinearLayout mCountryLayout;

    ImageButton fbLoginButton, gmail_btn_login;

    TextView countryStdCodeTxt, forgetPassewordText;
    List<CountryDto> countryList;
    ArrayList<Bitmap> countryFlagBitmap;
    Dialog customDialog;
    String selectedCountryStdCode = "+91";
    String selectedCountryName = "";

    String phonePattern = "\\d+";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static String mUserEmail;
    public static String googleName;
    public static String fbName;
    public static Constants.AppConstants.LoginType mLoginType = Constants.AppConstants.LoginType.Housing;

    private Intent googleIntent;
    private String googleEmail;
    private String googleImageUrl;
    private String googleID;
    private URL image_path;
    private ImageView notificationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        //toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.transparent));
        setSupportActionBar(toolbar);
        /*ImageView backBtnImg = findViewById(R.id.id_back_button);
        backBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
        mEmailView = (EditText) findViewById(R.id.editTextEmailId);

        fbLoginButton = (ImageButton) findViewById(R.id.login_button);
        gmail_btn_login = (ImageButton) findViewById(R.id.gmail_btn_sign_in);
        forgetPassewordText = (TextView) findViewById(R.id.id_forget_password);

        forgetPassewordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickForgotPassword(null);
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                OnFBLoginSuccess(loginResult);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "FB Login: onCancel");
                showProgress(false);
                showLoginPanel(true);
                resetFacebookValue();
            }

            @Override
            public void onError(FacebookException error) {
                showProgress(false);
                showLoginPanel(true);
                showNetworkErrorSnackBar(findViewById(R.id.loginParentView));
                //UtilityMethods.showSnackBar(findViewById(R.id.loginParentView), "" + error.getMessage(), Snackbar.LENGTH_LONG);
            }
        });


        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackAnalytics.trackEvent("LoginPage_fb_event", Constants.AppConstants.HOLD_TIME ,
                        1, LoginActivity.this);
                if(!UtilityMethods.isInternetConnected(LoginActivity.this)){
                    UtilityMethods.showErrorSnackBar(findViewById(R.id.loginParentView),
                            getResources().getString(R.string.network_error_msg),
                            Snackbar.LENGTH_LONG);
                    return;
                }
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, PERMISSIONS);
                //showProgressBar();
                showProgress(true);
                showLoginPanel(false);
            }
        });

        gmail_btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TrackAnalytics.trackEvent("LoginPage_gplus_event", Constants.AppConstants.HOLD_TIME ,
                        1, LoginActivity.this);
                if(!UtilityMethods.isInternetConnected(LoginActivity.this)){
                    UtilityMethods.showErrorSnackBar(findViewById(R.id.loginParentView),
                            getResources().getString(R.string.network_error_msg),
                            Snackbar.LENGTH_LONG);
                    return;
                }
                signInWithGplus();
            }
        });

        notificationIcon = findViewById(R.id.id_notification_icon);
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(LoginActivity.this, Constants.AnaylticsClassName.LoginScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.notificationClick.toString());
                startActivity(new Intent(LoginActivity.this, NotificationActivity.class));
            }
        });

        mHanlder = new Handler();
        populateLoginPanel();
        buildLoginWebService();

        mPasswordView = (EditText) findViewById(R.id.editTextPassword);
        mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_black_24dp, 0);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(null);
                    return true;
                }
                return false;
            }
        });

        Button mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(null);
            }
        });

        //mLoginFormView = findViewById(loginPanel);
        mCountryLayout = (LinearLayout) findViewById(R.id.country_code_select);
        mCountryLayout.setVisibility(View.GONE);
        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s == null){
                    return;
                }
                String text = s.toString().trim();
                if (text.matches(phonePattern)) {
                    mCountryLayout.setVisibility(View.VISIBLE);
                }
                if (!text.matches(phonePattern)) {
                    mCountryLayout.setVisibility(View.GONE);
                }
                if (text.matches(phonePattern) && (text.length() > 13)) {
                    mEmailView.setError("Not a valid number");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                //if ((!text.matches(emailPattern)) && (!text.matches(phonePattern))) {
                //    mEmailView.setError("Invalid Email/Phone");
                //}
            }
        });

        mPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if(!TextUtils.isEmpty(mPasswordView.getError())){
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (mPasswordView.getRight() - mPasswordView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mPasswordView.setError(null);
                        mPasswordView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        if (mPasswordView.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                            mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_black_24dp, 0);
                            mPasswordView.setSelection(mPasswordView.getText().length());
                        } else {
                            mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_black_24dp, 0);
                        }
                        return true;
                    }
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilityMethods.getBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, false)) {
            notificationIcon.setImageResource(R.drawable.notification_red_icon_small);
        } else {
            notificationIcon.setImageResource(R.drawable.ic_notification_small);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_screen_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_support:{
                new EventAnalyticsHelper().ItemClickEvent(LoginActivity.this, Constants.AnaylticsClassName.LoginScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.contactSupport.toString());
                Intent intent = new Intent(this, ConceirgeActivity.class);
                startActivity(intent);
            }break;
            case R.id.id_about:{
                new EventAnalyticsHelper().ItemClickEvent(LoginActivity.this, Constants.AnaylticsClassName.LoginScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.AboutUsClick.toString());
                Intent intent = new Intent(this, AboutUsActivity.class);
                startActivity(intent);
            }break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void buildLoginWebService() {
        mLoginWebService = EndPointBuilder.getUserEndPoint();
    }

    public void onClickRegistrationButton(View view){
        mLoginType = Constants.AppConstants.LoginType.Housing;
        new EventAnalyticsHelper().ItemClickEvent(LoginActivity.this, Constants.AnaylticsClassName.LoginScreen,
                null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.RegistrationClick.toString());
        doRegistration(null);
    }

    public void doRegistration(View view) {
        TrackAnalytics.trackEvent("LoginPage_register_click", Constants.AppConstants.HOLD_TIME ,
                1, LoginActivity.this);
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        if(mLoginType == Constants.AppConstants.LoginType.Facebook){
            if(!TextUtils.isEmpty(mUserEmail)){
                intent.putExtra("EmailId",mUserEmail);
            }
            if(!TextUtils.isEmpty(fbName)){
                intent.putExtra("Name",fbName);
            }
            if(!TextUtils.isEmpty(mThirdPartyID)) {
                intent.putExtra("ThirdPartyId", mThirdPartyID);
            }

        }else if(mLoginType == Constants.AppConstants.LoginType.Google){
            if(!TextUtils.isEmpty(googleName)){
                intent.putExtra("Name",googleName);
            }
            if(!TextUtils.isEmpty(mUserEmail)){
                intent.putExtra("EmailId",mUserEmail);
            }
        }else{
            if(mEmailView != null && !mEmailView.getText().toString().trim().isEmpty()){
                String text = mEmailView.getText().toString().trim();
                if (text.matches(phonePattern)) {
                    intent.putExtra("Phone",text);
                }else{
                    intent.putExtra("EmailId",text);
                }
            }
        }
        if(mLoginType != null){
            intent.putExtra("LoginType",mLoginType.toString());
        }
        if(image_path != null){
            intent.putExtra("ImagePath",image_path.toString());
        }
        startActivityForResult(intent, RC_SIGN_UP);
//        finish();
    }

    private void attemptLogin(View view) {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordView.setCompoundDrawables(null, null, null, null);
        mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_black_24dp, 0);


        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!isPasswordValid(password)) {
            focusView = mPasswordView;
            cancel = true;
        }

        if (!isEmailValid(email)) {
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mLoginType = Constants.AppConstants.LoginType.Housing;
            UtilityMethods.saveStringInPref(LoginActivity.this, Constants.AppConstants.LOGIN_TYPE_PREF_KEY, mLoginType.toString());
            clrearPreviousData();
            doSignIn(email, password, mLoginType);
        }
    }

    private void doSignIn(String email, String password, Constants.AppConstants.LoginType loginType) {
       /* TrackAnalytics.trackEvent("LoginPage_submit", Constants.AppConstants.HOLD_TIME ,
                1, this);*/

        if(!UtilityMethods.isInternetConnected(this)){
            new EventAnalyticsHelper().logAPICallEvent(LoginActivity.this, Constants.AnaylticsClassName.LoginScreen,
                    null, Constants.ApiName.login.toString(),Constants.AnalyticsEvents.FAILED,
                    getResources().getString(R.string.network_error_msg));
            UtilityMethods.showErrorSnackBar(findViewById(R.id.loginParentView),
                    getResources().getString(R.string.network_error_msg),Snackbar.LENGTH_LONG);
            return;
        }
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        showProgress(true);
        mAuthTask = new UserLoginTask(email.trim(), password, loginType);
        mAuthTask.execute((Void) null);
    }

    private void populateLoginPanel() {
        if (!mayRequestContacts()) {
            return;
        }
        mHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoginPanel(true);
            }
        }, 1000);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateLoginPanel();
            }
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        //addEmailsToAutoComplete(emails);
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private boolean mSignInClicked;

    @Override
    public void onConnected(Bundle arg0) {
        if (mGoogleApiClient.hasConnectedApi(Plus.API) && mGoogleApiClient.isConnected() && !mSignInClicked) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.stopAutoManage(LoginActivity.this);
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
                        mLoginType = Constants.AppConstants.LoginType.Google;
                        URL imageUrl = new URL(personPhotoUrl);
                        //RegistrationActivity.saveFacebookProfilePicture(LoginActivity.this, imageUrl, mLoginWebService, 0);
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
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RegistrationActivity.RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
                TrackAnalytics.trackException(TAG, e.getMessage(), e);
            }
        } else if (mConnectionResult == null && mGoogleApiClient != null) {
            mIntentInProgress = false;
            mGoogleApiClient.connect();
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private String mEmail;
        private final String mPassword;
        private final Constants.AppConstants.LoginType mLoginType;

        UserLoginTask(String email, String password, Constants.AppConstants.LoginType loginType) {
            mEmail = email;
            mPassword = password;
            mLoginType = loginType;
            if (mLoginType == Constants.AppConstants.LoginType.Facebook) {
                if (TextUtils.isEmpty(mEmail))
                    mEmail = "Unknown";
            }
        }

        @Override
        protected void onPreExecute(){
            showProgressBar();
        }

        @Override
        protected String doInBackground(Void... params) {
            String errorMSG = "";
            int errorCode = 0;
            try {
                /*final GCMRegistration gcmRegistration = new GCMRegistration(LoginActivity.this);
                String gcmID = gcmRegistration.getRegistrationId();
                if (TextUtils.isEmpty(gcmID)) {
                    gcmID = gcmRegistration.registerGCM();
                }*/
                UserEndPoint.Login login = mLoginWebService.login(mLoginType.toString(), mEmail);
                switch (mLoginType) {
                    case Housing: {
                        //login.setEmail(mEmail);
                        if (mEmail.matches(phonePattern)) {
                            login.setSTDCode(selectedCountryStdCode);
                        }
                        login.setPassword(mPassword);
                    }
                    break;
                    case Facebook: {
                        Log.d(TAG, "login type FB " + mThirdPartyID);
                        login.setRThirdPartyID(mThirdPartyID);
                    }
                    break;
                    case Google: {
                        login.setRThirdPartyID(mThirdPartyID);
                        //login.setEmail(mUserEmail);
                    }
                    break;
                }
                UserLoginResponse userLoginResponse = login.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                Log.d(TAG, userLoginResponse.getStatus() + "   " + userLoginResponse.getErrorMessage() + "\n"
                        + userLoginResponse.toString());
                dismissProgressBar();
                if (userLoginResponse.getStatus()) {
                    //UtilityMethods.clearUserProfile(LoginActivity.this);
                    Log.d(TAG, "status->" + userLoginResponse.getStatus() + "    " + userLoginResponse.getUser().getName());
                    Log.d(TAG, "MobNo: " + userLoginResponse.getUser().getPhone());
                    AppEventAnalytics.setAnalyticsProperty(LoginActivity.this,userLoginResponse.getUser());
                    UtilityMethods.saveUserProfile(LoginActivity.this, userLoginResponse.getUser().getLoginType(),
                            userLoginResponse.getUser().getName(), userLoginResponse.getUser().getEmail(),
                            userLoginResponse.getUser().getCountrySTDCode(), userLoginResponse.getUser().getPhone(),
                            userLoginResponse.getUser().getImageURL(), userLoginResponse.getUser().getCountryName(), mPassword);
                    Log.d(TAG, "IMAGE_URL->" + userLoginResponse.getUser().getImageURL());
                    if (userLoginResponse.getUser().getPassword() != null) {
                        UtilityMethods.saveBooleanInPref(LoginActivity.this, Constants.AppConstants.IS_PASSWORD_AVAILABLE_PREF_KEY, true);
                    } else {
                        UtilityMethods.saveBooleanInPref(LoginActivity.this, Constants.AppConstants.IS_PASSWORD_AVAILABLE_PREF_KEY, false);
                           }
                    UtilityMethods.saveLongInPref(LoginActivity.this, Constants.ServerConstants.USER_ID, userLoginResponse.getUid());
                    UtilityMethods.saveStringInPref(LoginActivity.this, Constants.ServerConstants.EMAIL, userLoginResponse.getUser().getEmail());
                    UtilityMethods.saveStringInPref(LoginActivity.this, Constants.ServerConstants.MOBILE, userLoginResponse.getUser().getPhone());
                    UtilityMethods.saveStringInPref(LoginActivity.this, Constants.ServerConstants.NAME, userLoginResponse.getUser().getName());
                    if (userLoginResponse.getUser() != null && userLoginResponse.getUser().getFavouriteProjectLIst() != null) {
                        if (!userLoginResponse.getUser().getFavouriteProjectLIst().isEmpty()) {

                            List<Long> favList = userLoginResponse.getUser().getFavouriteProjectLIst();
                            //List<Long> al = new ArrayList<>();
                            Set<String> favSet = new HashSet<>();
                            for (Long fav : favList){
                                favSet.add(fav+"");
                            }
                            /*Set<Long> hs = new HashSet<>();
                            hs.addAll(favList);
                            favList.clear();
                            favList.addAll(hs);*/

                            //UtilityMethods.saveSetInPref(LoginActivity.this, Constants.AppConstants.PROJECT_ID_SET, favList);
                            UtilityMethods.saveWishlistInPref(LoginActivity.this, favSet);
                                    //userLoginResponse.getUser().getFavouriteProjectLIst());

                        }
                    }

                    if (userLoginResponse.getUser() != null && userLoginResponse.getUser().getSearchCriteriaList() != null) {
                        if (!userLoginResponse.getUser().getSearchCriteriaList().isEmpty()) {
                            UtilityMethods.saveIntInPref(LoginActivity.this, Constants.AppConstants.SAVED_SEARCH_COUNT,
                                    userLoginResponse.getUser().getSearchCriteriaList().size());

                            ArrayList<SaveSearchWrapper> saveSearchWrapperList = new ArrayList<>();
                            ArrayList<SearchCriteria> searchCriteriaArrayList =
                                    (ArrayList<SearchCriteria>) userLoginResponse.getUser().getSearchCriteriaList();

                            for (int i = 0; i < searchCriteriaArrayList.size(); i++) {
                                SaveSearchWrapper saveSearchWrapper = new SaveSearchWrapper();

                                if (!TextUtils.isEmpty(searchCriteriaArrayList.get(i).getName())) {
                                    saveSearchWrapper.name = searchCriteriaArrayList.get(i).getName();
                                }

                                saveSearchWrapper.filterApplied = searchCriteriaArrayList.get(i).getFilterApplied();

                                if (searchCriteriaArrayList.get(i).getId() != null) {
                                    saveSearchWrapper.id = searchCriteriaArrayList.get(i).getId();
                                }
                                if ((searchCriteriaArrayList.get(i).getLatitude() != null)
                                        && (searchCriteriaArrayList.get(i).getLongitude() != null)) {
                                    saveSearchWrapper.latitude = searchCriteriaArrayList.get(i).getLatitude();
                                    saveSearchWrapper.longitude = searchCriteriaArrayList.get(i).getLongitude();
                                }

                                if ((searchCriteriaArrayList.get(i).getLatLongList() != null)
                                        && (!searchCriteriaArrayList.get(i).getLatLongList().isEmpty())) {
                                    saveSearchWrapper.latLongList = searchCriteriaArrayList.get(i).getLatLongList();
                                }

                                if(searchCriteriaArrayList.get(i).getCity() != null
                                         && !searchCriteriaArrayList.get(i).getCity().isEmpty()){
                                    saveSearchWrapper.cityName = searchCriteriaArrayList.get(i).getCity();
                                }
                                if(searchCriteriaArrayList.get(i).getCityId() != null){
                                    saveSearchWrapper.cityId = searchCriteriaArrayList.get(i).getCityId();
                                }

                                if(searchCriteriaArrayList.get(i).getBuilderId() != null){
                                    saveSearchWrapper.builderId = searchCriteriaArrayList.get(i).getBuilderId();
                                }

                                if(searchCriteriaArrayList.get(i).getBuilder() != null
                                        && !searchCriteriaArrayList.get(i).getBuilder().isEmpty()){
                                    saveSearchWrapper.builderName = searchCriteriaArrayList.get(i).getBuilder();
                                }

                                if(searchCriteriaArrayList.get(i).getTime() != null){
                                    saveSearchWrapper.time = searchCriteriaArrayList.get(i).getTime();
                                }

                                if (searchCriteriaArrayList.get(i).getFilterApplied()) {
                                    if ((searchCriteriaArrayList.get(i).getBedList() != null) &&
                                            (!searchCriteriaArrayList.get(i).getBedList().isEmpty())) {
                                        saveSearchWrapper.bedList = searchCriteriaArrayList.get(i).getBedList();
                                    }
                                    if (searchCriteriaArrayList.get(i).getMaxCost() != null) {
                                        saveSearchWrapper.maxCost = searchCriteriaArrayList.get(i).getMaxCost();
                                    }
                                    if (searchCriteriaArrayList.get(i).getMinCost() != null) {
                                        saveSearchWrapper.minCost = searchCriteriaArrayList.get(i).getMinCost();
                                    }
                                    if ((searchCriteriaArrayList.get(i).getPropertyTypeEnum() != null)
                                            && (!searchCriteriaArrayList.get(i).getPropertyTypeEnum().isEmpty())) {
                                        saveSearchWrapper.propertyTypeEnum = searchCriteriaArrayList.get(i).getPropertyTypeEnum();
                                    }
                                    if ((searchCriteriaArrayList.get(i).getPropertyStatusList() != null)
                                            && (!searchCriteriaArrayList.get(i).getPropertyStatusList().isEmpty())) {
                                        saveSearchWrapper.propertyStatusList = searchCriteriaArrayList.get(i).getPropertyStatusList();
                                    }
                                    if ((searchCriteriaArrayList.get(i).getZoomLevel() != null)
                                            && (searchCriteriaArrayList.get(i).getZoomLevel() != 0)) {
                                        saveSearchWrapper.zoomLevel = searchCriteriaArrayList.get(i).getZoomLevel();
                                    }
                                }
                                saveSearchWrapperList.add(saveSearchWrapper);
                            }

                            UtilityMethods.putSaveSearchListInPrefs(LoginActivity.this,
                                    //(ArrayList<SearchCriteria>)
                                    //userLoginResponse.getUser().getSearchCriteriaList());
                                    saveSearchWrapperList);
                        }
                    }
                    sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());

                    return Constants.AppConstants.SUCCESS;
                } else {
                    if (userLoginResponse.getErrorId() == 12) {
                        showLoginPanel(true);
                        if (mLoginType.equals(Constants.AppConstants.LoginType.Google)
                                || mLoginType.equals(Constants.AppConstants.LoginType.Facebook)) {
                            return userLoginResponse.getErrorMessage();
                        }
                        return "InvalidPassword";

                    } else if (userLoginResponse.getErrorId() == 13){
                        //doRegistration(null);
                        return "register";
                    }else
                        return userLoginResponse.getErrorMessage();
                }

            } catch (UnknownHostException e) {
                dismissProgressBar();
                e.printStackTrace();
                TrackAnalytics.trackException(TAG, e.getMessage(), e);
                return Constants.AppConstants.NETWORK_ERROR;
            }catch (Exception e) {
                dismissProgressBar();
                e.printStackTrace();
                TrackAnalytics.trackException(TAG, e.getMessage(), e);
                return Constants.AppConstants.FAILED;
            }


            // TODO: register the new account here.
            // UtilityMethods.saveStringInPref(LoginActivity.this,Constants.AppConstants.USER_NAME_PREF_KEY);
            //return Constants.FAILED;
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);

            if (result.equals(Constants.AppConstants.SUCCESS)) {
                new EventAnalyticsHelper().logAPICallEvent(LoginActivity.this, Constants.AnaylticsClassName.LoginScreen,
                        null, Constants.ApiName.login.toString(),Constants.AnalyticsEvents.SUCCESS,
                       null);
                HousingLogs.i(TAG, "Successfull login");
                HousingLogs.i(TAG, "LoginType : " + mLoginType);
                ((HousingApplication)getApplicationContext()).setFromLogin(true);
                if (HousingApplication.mLoginSuccessCallback != null) {
                    Log.d(TAG, "Not null!");
                    HousingLogs.i(TAG, "Successfull login calling call back");
                    HousingApplication.mLoginSuccessCallback.isLoggedin();
                }
                /*Intent intent = new Intent();
                intent.putExtra("isFrom","Login");
                setResult(RESULT_OK,intent);
                finish();*/
                finish();
                UtilityMethods.saveBooleanInPref(LoginActivity.this,Constants.MORE_FRAGMENT_UPDATE,true);
             /*   Intent intent = new Intent(LoginActivity.this, HomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            } else if (result.equals("InvalidPassword")) {
                Log.d(TAG, "Invalid");
                mPasswordView.setError(getString(R.string.incorrect_password));
                mPasswordView.requestFocus();
            } else if (result.equals(Constants.AppConstants.FAILED)) {
                showNetworkErrorSnackBar(findViewById(R.id.loginParentView));
                showLoginPanel(true);
            }else if (result.equals(Constants.AppConstants.NETWORK_ERROR)) {
                showNetworkErrorSnackBar(findViewById(R.id.loginParentView));
                showLoginPanel(true);
            } else if(result.equals("register")){
                doRegistration(null);
            } else {
                //Snackbar.make(findViewById(R.id.loginParentView), result, Snackbar.LENGTH_SHORT).show();
                Log.i(TAG, "onPostExecute:Login ======="+result);
                UtilityMethods.showErrorSnackBar(findViewById(R.id.loginParentView),result,Snackbar.LENGTH_LONG);
                showLoginPanel(true);
            }

            if (mLoginType.equals(Constants.AppConstants.LoginType.Facebook)) {
                LoginManager.getInstance().logOut();
                Log.d(TAG, "loginTypeFacebook!");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showProgress(final boolean show) {
        //if (mProgressBar != null) {
        //    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        //}
        //if(mProgressView != null && !isFinishing() && !isDestroyed()){
        //    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        //}
    }

    private void showLoginPanel(boolean show) {
        if (mLoginFormView != null) {
            // int animationType = show ? R.anim.bottom_up : R.anim.bottom_down;
            // Animation animation = AnimationUtils.loadAnimation(LoginActivity.this,
            //         animationType);
            // mLoginFormView.startAnimation(animation);
            //mLoginFormView.setVisibility(View.VISIBLE);//show ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isEmailValid(String email) {
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
    }

    private boolean isPasswordValid(String password) {
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.password_cannot_be_empty));
            return false;
        }
        return true;
    }

    private void clrearPreviousData() {
        File file = new File(getFilesDir(), Constants.AppConstants.PROFILE_PIC);
        if (file.exists()) {
            file.delete();
        }
    }

    String mThirdPartyID = "";

    private void OnFBLoginSuccess(final LoginResult loginResult) {
        HousingApplication.loginResult = loginResult;
        Log.i(TAG, "OnFBLoginSuccess");
        mLoginType = Constants.AppConstants.LoginType.Facebook;
        //get user details
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Log.d(TAG, "object->" + object.toString());
                            mUserEmail = object.getString("email");
                            try {
                                mThirdPartyID = object.getString("id");
                            } catch (Exception e) {
                                e.printStackTrace();
                                mThirdPartyID = loginResult.getAccessToken().getToken();
                            }
                            if(object.has("name")){
                                fbName = object.getString("name");
                            }
                            try {
                                image_path = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large");
                                //saveFacebookProfilePicture(RegistrationActivity.this, image_path,mLoginWebService, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "email->" + mUserEmail);
                            doSignIn(mUserEmail, "", mLoginType);
                        } catch (JSONException e) {
                            UtilityMethods.showSnackBar(findViewById(R.id.loginParentView)
                                    , getString(R.string.cant_login_from_FB), Snackbar.LENGTH_SHORT);
                            if(TextUtils.isEmpty(mUserEmail)){
                                mLoginType = Constants.AppConstants.LoginType.Housing;
                            }
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture"); // Parámetros que pedimos a facebook
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        String errorMsg = "";
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            //GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,gso);
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(LoginActivity.this,LoginActivity.this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            //showNetworkErrorSnackBar(findViewById(R.id.loginParentView));
            errorMsg = "error";
        }
        if(!TextUtils.isEmpty(errorMsg)){
            UtilityMethods.showErrorSnackBar(findViewById(R.id.loginParentView),"Could not login using google",Snackbar.LENGTH_LONG);
            return;
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        callbackManager.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            try {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
                int statusCode = result.getStatus().getStatusCode();
                Log.i(TAG, "status code int : " + statusCode + " ::status code string: " + GoogleSignInStatusCodes.getStatusCodeString(statusCode));
                boolean error = false;
                Log.i(TAG, "onActivityResult: RC_SIGN_IN ");
                if (resultCode == RESULT_OK) {
                    googleIntent = intent;
                    Log.i(TAG, "onActivityResult: RC_SIGN_IN  RESULT_OK ");
                    try {
                        GoogleSignInAccount currentPerson = result.getSignInAccount();
                        Uri personPhotoUrl = currentPerson.getPhotoUrl();
                        mUserEmail = currentPerson.getEmail();
                        googleEmail = currentPerson.getEmail();
                        googleName = currentPerson.getDisplayName();
                        googleID = currentPerson.getId();
                        mThirdPartyID = currentPerson.getId();
                        mLoginType = Constants.AppConstants.LoginType.Google;

                        UtilityMethods.removeFromPref(LoginActivity.this, Constants.SharedPreferConstants.CONCEIRGE_PHOTO_URL);
                        UtilityMethods.removeFromPref(LoginActivity.this, Constants.SharedPreferConstants.CONCEIRGE_NAME);

//                    if (personPhotoUrl != null) {
//                        URL imageUrl = new URL(personPhotoUrl.toString());
//                        googleImageUrl = personPhotoUrl.toString();
//                        RegistrationActivity.saveFacebookProfilePicture(LoginActivity.this, imageUrl, mLoginWebService, 0);
//                    }
                        if(personPhotoUrl != null) {
                            image_path = new URL(personPhotoUrl.toString());
                        }
                        doSignIn(mUserEmail, "", mLoginType);
                        if (mGoogleApiClient.hasConnectedApi(Plus.API) && mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        error = true;
                        Log.i(TAG, "onActivityResult: RC_SIGN_IN  Exception ");
                    }
                } else {
                    error = true;
                }

                if (error) {
                    HousingLogs.d(TAG, "Error!");
                    if (mProgressView != null) {
                        mProgressView.setVisibility(View.INVISIBLE);
                    }
                    //UtilityMethods.showSnackBar(findViewById(R.id.loginParentView), getString(R.string.google_auth_error), Snackbar.LENGTH_LONG);
                    showNetworkErrorSnackBar(findViewById(R.id.loginParentView));
                }

                try {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mGoogleApiClient.clearDefaultAccountAndReconnect();
                    mGoogleApiClient.stopAutoManage(LoginActivity.this);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.i(TAG, "onActivityResult: RC_SIGN_IN  Exception ");
                showNetworkErrorSnackBar(findViewById(R.id.loginParentView));
            }
        }
        if (requestCode == RC_SIGN_UP && resultCode == Activity.RESULT_OK) {
            finish();
            UtilityMethods.removeFromPref(LoginActivity.this, Constants.SharedPreferConstants.CONCEIRGE_PHOTO_URL);
            UtilityMethods.removeFromPref(LoginActivity.this, Constants.SharedPreferConstants.CONCEIRGE_NAME);
            if (HousingApplication.mLoginSuccessCallback != null) {
                Log.d(TAG, "Not null!");
                HousingLogs.i(TAG, "Successfull login calling call back");
                HousingApplication.mLoginSuccessCallback.isLoggedin();
            }
        }
    }


    public void onClickForgotPassword(View view) {
        //showProgressBar("Please Wait..");

        new EventAnalyticsHelper().ItemClickEvent(LoginActivity.this, Constants.AnaylticsClassName.LoginScreen,
                null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.forgotPasswordClick.toString());
       /* TrackAnalytics.trackEvent("LoginPage_forgot_pass", Constants.AppConstants.HOLD_TIME ,
                1, this);*/
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        intent.putExtra(Constants.AppConstants.ACTION_BAR_TITLE, getString(R.string.forgot_password));
        intent.putExtra(Constants.AppConstants.EDIT_FIELD_IS_ENABLE, true);
        startActivity(intent);
        //TrackAnalytics.trackEvent(LoginActivity.this.getResources().getString(R.string.ga_button_category), getString(R.string.forgot_password_button_clicked), this);
        //showForgotPasswordDialog();
    }

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

    private Bitmap getBitmapFromAsset(String strName) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open(Constants.AppConstants.COUNTRY_FLAG_DIR + File.separator + strName.trim());
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
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

    private void sendRegistrationToServer(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    final long userId = UtilityMethods.getLongInPref(LoginActivity.this, Constants.ServerConstants.USER_ID, -1);
                    UserEndPoint.UpdateFCMKey updateFCMKey = mLoginWebService.updateFCMKey(userId,token);
                    updateFCMKey.setOSType(Constants.AppConstants.OSType.ANDROID.toString());

                    final CommonResponse response = updateFCMKey.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (response != null && response.getStatus()) {
                    } else {
                        errorMsg = "No target found";
                    }
                } catch (UnknownHostException e) {
                    errorMsg = getString(R.string.network_error_msg);
                    e.printStackTrace();
                } catch (Exception e) {
                    errorMsg = getString(R.string.something_went_wrong);
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;
                }
            }
        }).start();
    }

    public void showNetworkErrorSnackBar(View view) {
        final Snackbar snackbar = Snackbar.make(view, getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.uber_red));
        /*snackbar.setActionTextColor(ContextCompat.getColor(LoginActivity.this, R.color.white));
        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });*/
        snackbar.show();
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.LoginScreen);

        /*TrackAnalytics.trackEvent("LoginPage", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);*/
    }

    private void resetFacebookValue(){
        fbName = "";
        mUserEmail = "";
        mLoginType = Constants.AppConstants.LoginType.Housing;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
