package com.clicbrics.consumer.view.activity;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.UserLoginResponse;
import com.buy.housing.backend.userEndPoint.model.UserProfileResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.AboutUsActivity;
import com.clicbrics.consumer.activities.ConceirgeActivity;
import com.clicbrics.consumer.activities.FavoritesActivity;
import com.clicbrics.consumer.activities.NotificationActivity;
import com.clicbrics.consumer.customview.CircularImageView;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.activity.EditProfileActivity;
import com.clicbrics.consumer.framework.activity.ForgotPasswordActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.helper.APIHelperResultCallback;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.SystemConfig;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.viewmodel.ProfileViewModel;
import com.clicbrics.consumer.wrapper.Notificaiton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ProfileScreen extends BaseActivity implements APIHelperResultCallback {

    private static final String TAG = "ProfileScreen";
    private TextView nameTxt, emailTxt, mobileNoTxt, countryStdCodeTxt;
    private CircularImageView profileImage;
    private TextView wishListCount, saveSearchCount, sitevisitCount,propertyCount;
    private ImageView notificationIcon;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        UtilityMethods.setStatusBarColor(this,R.color.profile_bg_color);
        initView();

        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        getProfileInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilityMethods.getBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, false)) {
            notificationIcon.setImageResource(R.drawable.notification_red_icon);
        } else {
            notificationIcon.setImageResource(R.drawable.ic_notification);
        }
        getProfileCount();
    }

    private void initView(){
        nameTxt = findViewById(R.id.id_name);
        emailTxt = findViewById(R.id.id_email);
        mobileNoTxt =findViewById(R.id.id_mobile);
        profileImage = findViewById(R.id.id_profile_image);

        ImageView backButton = findViewById(R.id.ic_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayout imageLayout = findViewById(R.id.id_image_layout);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEditProfile(true);
            }
        });

        notificationIcon = findViewById(R.id.id_notification_icon);
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout wishlistIconLayout = findViewById(R.id.id_wishlist_icon_layout);
        wishlistIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });

        final LinearLayout saveSearchIconLayout = findViewById(R.id.id_save_search_icon_layout);
        saveSearchIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this, SaveSearchActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout siteVisitIconLayout = findViewById(R.id.id_sitevisit_icon_layout);
        siteVisitIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this, SiteVisitsActivity.class);
                startActivity(intent);
            }
        });

        final LinearLayout propertyIconLayout = findViewById(R.id.id_property_icon_layout);
        propertyIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this, MyPropertyScreen.class);
                startActivity(intent);
            }
        });

        LinearLayout supportIconLayout = findViewById(R.id.id_support_icon_layout);
        supportIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this, ConceirgeActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout aboutUsIconLayout = findViewById(R.id.id_about_us_icon_layout);
        aboutUsIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        /**
         * To make the all box height same, we need to check the height of biggest box at run time
         * which is saved searches box has the maximum height becuase of the text length
         * making all box height same
         */
        ViewTreeObserver observer = saveSearchIconLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = saveSearchIconLayout.getMeasuredHeight();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) propertyIconLayout.getLayoutParams();
                if(height != 0){
                    layoutParams.height = height;
                    propertyIconLayout.setLayoutParams(layoutParams);
                }
                saveSearchIconLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void getProfileInfo() {
        TextView nameTextView = findViewById(R.id.id_name);
        TextView emailTextView = findViewById(R.id.id_email);
        TextView mobileTextView = findViewById(R.id.id_mobile);
        final CircularImageView profileImageView = findViewById(R.id.id_profile_image);
        nameTextView.setText(UtilityMethods.getStringInPref(this, Constants.AppConstants.USER_NAME_PREF_KEY, ""));
        if(!TextUtils.isEmpty(UtilityMethods.getStringInPref(this, Constants.AppConstants.EMAIL_PREF_KEY, ""))) {
            emailTextView.setVisibility(View.VISIBLE);
            emailTextView.setText(UtilityMethods.getStringInPref(this, Constants.AppConstants.EMAIL_PREF_KEY, ""));
        }else{
            emailTextView.setVisibility(View.GONE);
        }
        String stdCode = UtilityMethods.getStringInPref(this, Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, "+91");
        if(!stdCode.contains("+")){
            stdCode = "+"+stdCode;
        }
        String mobileNo = UtilityMethods.getStringInPref(this, Constants.AppConstants.MOBILE_PREF_KEY, "");
        mobileTextView.setText(stdCode + "-" + mobileNo);

        String imageURL = UtilityMethods.getStringInPref(this, Constants.AppConstants.IMAGE_URL, "");
        if (!TextUtils.isEmpty(imageURL)) {
            findViewById(R.id.id_profile_image_pb).setVisibility(View.VISIBLE);
            Picasso.get().load(imageURL)
                    .placeholder(R.drawable.ic_bank_icon)
                    .into(profileImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            findViewById(R.id.id_profile_image_pb).setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            findViewById(R.id.id_profile_image_pb).setVisibility(View.GONE);
                        }
                    });
            /*Picasso.get().load(imageURL).placeholder(R.drawable.ic_person)
                    .into(profileImageView);*/
        } else {
            UtilityMethods.loadCircularImageFromPicasso(this, profileImageView);
        }

        wishListCount = findViewById(R.id.id_wishlist_badge_icon);
        saveSearchCount = findViewById(R.id.id_savesearch_badge_icon);
        sitevisitCount = findViewById(R.id.id_sitevisit_badge_icon);
        propertyCount = findViewById(R.id.id_property_badge_icon);

    }

    private void getProfileCount(){
        long userId = UtilityMethods.getLongInPref(this,Constants.ServerConstants.USER_ID,-1);

        Observer<UserProfileResponse> observer = new Observer<UserProfileResponse>() {
            @Override
            public void onChanged(@Nullable UserProfileResponse response) {
                if(response !=null){
                    if(response.getFavoriteCount() != null && response.getFavoriteCount() != 0){
                        wishListCount.setVisibility(View.VISIBLE);
                        wishListCount.setText(response.getFavoriteCount()+"");
                    }else{
                        wishListCount.setVisibility(View.GONE);
                    }
                    if(response.getSavedSearchCount() != null && response.getSavedSearchCount() != 0){
                        saveSearchCount.setVisibility(View.VISIBLE);
                        saveSearchCount.setText(response.getSavedSearchCount() + "");
                    }else{
                        saveSearchCount.setVisibility(View.GONE);
                    }
                    if(response.getSiteVisitCount() != null && response.getSiteVisitCount() != 0){
                        sitevisitCount.setVisibility(View.VISIBLE);
                        sitevisitCount.setText(response.getSiteVisitCount()+"");
                    }else{
                        sitevisitCount.setVisibility(View.GONE);
                    }
                    if(response.getBookedPropertyCount() != null && response.getBookedPropertyCount() != 0){
                        propertyCount.setVisibility(View.VISIBLE);
                        propertyCount.setText(response.getBookedPropertyCount()+"");
                    }else{
                        propertyCount.setVisibility(View.GONE);
                    }
                }
            }
        };
        profileViewModel.getUserProfileDetail(this,userId).observe(this,observer);
    }

    public void onClickLogout(View view) {
        TrackAnalytics.trackEvent(ProfileScreen.this.getResources().getString(R.string.ga_button_category), "Sign Out Clicked ", this);
        showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMSG = "";
                removePrivateNotifications();
                UtilityMethods.removeFromPref(ProfileScreen.this, Constants.ServerConstants.USER_ID);
                UtilityMethods.removeFromPref(ProfileScreen.this, Constants.AppConstants.EMAIL_PREF_KEY);
                //GCMRegistration gcmRegistration = new GCMRegistration(ProfileScreen.this);
                SystemConfig.getInstanceSystemConfig(ProfileScreen.this).resetConfig();
                UtilityMethods.removeFromPref(ProfileScreen.this, Constants.SharedPreferConstants.CONCEIRGE_PHOTO_URL);
                UtilityMethods.removeFromPref(ProfileScreen.this, Constants.SharedPreferConstants.CONCEIRGE_NAME);
                UtilityMethods.removeFromPref(ProfileScreen.this, Constants.AppConstants.IMAGE_URL);
                UtilityMethods.removeFromPref(ProfileScreen.this, Constants.SharedPreferConstants.IS_NOTIFICATION_SEEN);
                UtilityMethods.removeFromPref(ProfileScreen.this, Constants.AppConstants.VIRTUAL_UID);
                UtilityMethods.saveLongInPref(ProfileScreen.this,Constants.SharedPreferConstants.TIME_STAMP,System.currentTimeMillis());
                //gcmRegistration.resetRegistrationId();
                UtilityMethods.clearUserProfile(ProfileScreen.this);
                if (UtilityMethods.favoriteIDs != null) {
                    UtilityMethods.favoriteIDs.clear();
                }
                UtilityMethods.clearPreference(ProfileScreen.this,Constants.SharedPreferConstants.RECENT_PROJECT_LIST);
                UtilityMethods.clearPreference(ProfileScreen.this,"recent_search");

                //Clear favorites!
                UtilityMethods.clearPreference(ProfileScreen.this, Constants.AppConstants.PROJECT_ID_SET);

                File profilePicFile = new File(getFilesDir() + File.separator + Constants.AppConstants.PROFILE_PIC);
                if (profilePicFile.exists()) {
                    profilePicFile.delete();
                }

                Intent intent = new Intent(ProfileScreen.this, HomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //dismissProgressBar();
                //finish();

                if (!TextUtils.isEmpty(errorMSG)) {
                    dismissProgressBar();
                    final String errmsg = errorMSG;
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
                            mCustomDialog = UtilityMethods.showAlert(ProfileScreen.this, R.string.error, errmsg, true, R.string.ok, positiveListener, -1, null, null);
                        }
                    });
                }
            }
        }).start();
    }


    private void removePrivateNotifications() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.NOTIFICATIONS, "");
        Type type = new TypeToken<List<Notificaiton>>() {
        }.getType();

        List<Notificaiton> notificationList = gson.fromJson(json, type);
        UtilityMethods.clearPreference(ProfileScreen.this, Constants.AppConstants.NOTIFICATIONS);

        List<Notificaiton> publicNotifications = new ArrayList<>();
        if (notificationList == null || notificationList.isEmpty()) {
            return;
        } else {
            for (Notificaiton toRemove : notificationList) {
                Log.d(TAG,"startTime->"+toRemove.getStartTime());
                if ((toRemove.getStartTime() != null) && (toRemove.getStartTime() != 0)) {
                    publicNotifications.add(toRemove);
                }
            }
        }

        Log.d(TAG,"notificationList->"+notificationList.size()+"\n public->"+publicNotifications.size());

        SharedPreferences.Editor prefsEditor = prefs.edit();
        gson = new Gson();
        json = gson.toJson(publicNotifications);
        prefsEditor.putString(Constants.AppConstants.NOTIFICATIONS, json);
        prefsEditor.apply();
    }

    Dialog editDialog;
    private void onClickEditProfile(final boolean isProfileUpdate) {
        final Constants.AppConstants.LoginType loginType = Enum.valueOf(Constants.AppConstants.LoginType.class,
                UtilityMethods.getStringInPref(this, Constants.AppConstants.LOGIN_TYPE_PREF_KEY,
                        Constants.AppConstants.LoginType.Housing.toString()));
        TrackAnalytics.trackEvent(this.getResources().getString(R.string.ga_button_category), "Edit Profile Clicked ", this);
        editDialog = new Dialog(this);
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.dialog_edit_profile);

        editDialog.setCancelable(true);
        final EditText profileChangeConfirmPassTxt = (EditText) editDialog.findViewById(R.id.confirmPassword);
        final TextView invalidPasswordTxt = (TextView) editDialog.findViewById(R.id.invalidPasswordTxt);
        final TextView forgotPassword = (TextView) editDialog.findViewById(R.id.forgotPassword);
        if (!UtilityMethods.getBooleanInPref(this, Constants.AppConstants.IS_PASSWORD_AVAILABLE_PREF_KEY, true)) {
            forgotPassword.setText(R.string.dont_have_password);
        }
        //final View focusedView = editDialog.getCurrentFocus();
        profileChangeConfirmPassTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                invalidPasswordTxt.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        final TextView cancel = (TextView) editDialog.findViewById(R.id.cancelaction);
        final TextView submit = (TextView) editDialog.findViewById(R.id.submitaction);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDialog.dismiss();
                hideSoftKeyboard();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                final String emailID = UtilityMethods.getStringInPref(ProfileScreen.this, Constants.AppConstants.EMAIL_PREF_KEY, "");
                //final GCMRegistration gcmRegistration = new GCMRegistration(ProfileScreen.this);
                Log.d(TAG, "internet connected->" + UtilityMethods.isInternetConnected(ProfileScreen.this));
                if (!UtilityMethods.isInternetConnected(ProfileScreen.this)) {
                    /*editDialog.dismiss();
                    View.OnClickListener positiveListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View View) {
                            if (mCustomDialog != null)
                                mCustomDialog.dismiss();
                        }
                    };
                    mCustomDialog = UtilityMethods.showAlert(ProfileScreen.this, R.string.error, getString(R.string.internet_is_very_slow), true, R.string.ok, positiveListener, -1, null, null);*/
                    UtilityMethods.showErrorSnackbarOnTop(ProfileScreen.this);
                    return;
                }

                if (TextUtils.isEmpty(profileChangeConfirmPassTxt.getText())) {
                    ProgressBar progressBar = (ProgressBar) editDialog.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                    submit.setEnabled(true);
                    cancel.setEnabled(true);
                    forgotPassword.setEnabled(true);
                    profileChangeConfirmPassTxt.setEnabled(true);
                    profileChangeConfirmPassTxt.setText("");
                    invalidPasswordTxt.setVisibility(View.VISIBLE);
                    return;
                }
                new Thread(new Runnable() {
                    boolean isPassMatch;

                    @Override
                    public void run() {
                        String errorMSG = "";
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ProgressBar progressBar = (ProgressBar) editDialog.findViewById(R.id.progressBar);
                                progressBar.setVisibility(View.VISIBLE);
                                submit.setEnabled(false);
                                cancel.setEnabled(false);
                                forgotPassword.setEnabled(false);
                                profileChangeConfirmPassTxt.setEnabled(false);
                            }
                        });
                        if (profileChangeConfirmPassTxt.getText() != null) {
                            try {
                                Log.d(TAG, "loginUpdate->" + loginType.toString() + " \n " + emailID);
                                String userPhone = UtilityMethods.getStringInPref(ProfileScreen.this,Constants.AppConstants.MOBILE_PREF_KEY,"");
                                String userValue = "";
                                if(TextUtils.isEmpty(userPhone)){
                                    userValue = emailID;
                                }else{
                                    userValue = userPhone;
                                }
                                UserEndPoint userEndPoint = EndPointBuilder.getUserEndPoint();
                                UserEndPoint.Login login = userEndPoint.login(Constants.AppConstants.LoginType.Housing.toString(), userValue);
                                final Constants.AppConstants.LoginType login_type = Constants.AppConstants.LoginType.Housing;
                                switch (login_type) {
                                    case Housing: {
                                        login.setPassword(profileChangeConfirmPassTxt.getText().toString());
                                        login.setEmail(userValue);
                                    }
                                    break;
                                    case Facebook: {
                                        login.setPassword(profileChangeConfirmPassTxt.getText().toString());
                                        login.setRThirdPartyID(getIntent().getStringExtra("ThirdPartyID"));
                                        login.setEmail(userValue);
                                    }
                                    break;
                                    case Google: {
                                        login.setPassword(profileChangeConfirmPassTxt.getText().toString());
                                        login.setRThirdPartyID(getIntent().getStringExtra("ThirdPartyID"));
                                        login.setEmail(userValue);
                                    }
                                    break;
                                }

                                final UserLoginResponse loginResponse = login.setFields("status, passWordAvailable")
                                        .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                                Gson gson = new Gson();
                                Log.i(TAG, "run: LoginResponse : " + gson.toJson(loginResponse));
                                if (loginResponse != null && loginResponse.getStatus()) {
                                    Log.d(TAG, "Error->" + loginResponse.getErrorMessage() + "\n" + loginResponse.getUser());
                                    isPassMatch = true;
                                    //To be resolved        //               UtilityMethods.saveBooleanInPref(ProfileScreen.this,Constants.IS_PASSWORD_AVAILABLE_PREF_KEY,loginResponse.getPassWordAvailable());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProgressBar progressBar = (ProgressBar) editDialog.findViewById(R.id.progressBar);
                                            progressBar.setVisibility(View.GONE);
                                            submit.setEnabled(true);
                                            cancel.setEnabled(true);
                                            forgotPassword.setEnabled(true);
                                            profileChangeConfirmPassTxt.setEnabled(true);

                                            Log.d(TAG, "updateReq->" + isProfileUpdate);
                                            String mobile = mobileNoTxt.getText().toString();
                                            String mobileNo="",countryCode = "";
                                            if(mobile.contains("-")){
                                                countryCode = mobile.split("-")[0];
                                                mobileNo = mobile.split("-")[1];
                                            }
                                            if (isProfileUpdate) {
                                                editDialog.dismiss();
                                                Intent intent = new Intent(ProfileScreen.this, EditProfileActivity.class);
                                                intent.putExtra("Name", nameTxt.getText().toString());
                                                intent.putExtra("Email", emailTxt.getText().toString());
                                                intent.putExtra("Mobile", mobileNo);
                                                intent.putExtra("CountryStdCode", countryCode);
                                                intent.putExtra("Password", profileChangeConfirmPassTxt.getText().toString());

                                                startActivityForResult(intent, Constants.AppConstants.EDIT_ACTIVITY_RESULT);
//                                                editDialog.dismiss();
                                            } else {
                                                editPassword();
                                                editDialog.dismiss();
                                            }
                                        }
                                    });
                                } else {
                                    errorMSG = loginResponse.getErrorMessage();
                                    isPassMatch = false;
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProgressBar progressBar = (ProgressBar) editDialog.findViewById(R.id.progressBar);
                                            progressBar.setVisibility(View.GONE);
                                            submit.setEnabled(true);
                                            cancel.setEnabled(true);
                                            forgotPassword.setEnabled(true);
                                            profileChangeConfirmPassTxt.setEnabled(true);
                                            profileChangeConfirmPassTxt.setText("");
                                            invalidPasswordTxt.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            } catch (UnknownHostException e) {
                                isPassMatch = false;
                                errorMSG = ProfileScreen.this.getResources().getString(R.string.network_error_msg);
                                e.printStackTrace();
                            }catch (Exception e) {
                                isPassMatch = false;
                                errorMSG = ProfileScreen.this.getResources().getString(R.string.something_went_wrong);
                                e.printStackTrace();
                            }
                            if (!TextUtils.isEmpty(errorMSG)) {
                                final String errorMsg = errorMSG;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressBar progressBar = (ProgressBar) editDialog.findViewById(R.id.progressBar);
                                        progressBar.setVisibility(View.GONE);
                                        submit.setEnabled(true);
                                        cancel.setEnabled(true);
                                        forgotPassword.setEnabled(true);
                                        profileChangeConfirmPassTxt.setEnabled(true);
                                        /*View.OnClickListener positiveListener = new View.OnClickListener() {

                                            @Override
                                            public void onClick(View View) {
                                                if (mCustomDialog != null)
                                                    mCustomDialog.dismiss();
                                            }
                                        };
                                        mCustomDialog = UtilityMethods.showAlert(ProfileScreen.this, R.string.alert, errorMsg, true, R.string.ok, positiveListener, -1, null, null);*/
                                    }
                                });
                            }
                        }

                    }
                }).start();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDialog.dismiss();
                hideSoftKeyboard();
                Intent intent = new Intent(ProfileScreen.this, ForgotPasswordActivity.class);
                intent.putExtra(Constants.AppConstants.ACTION_BAR_TITLE, getString(R.string.get_password));
                intent.putExtra(Constants.AppConstants.EDIT_FIELD_IS_ENABLE, false);
                startActivity(intent);
            }
        });
        editDialog.show();
    }


    private void editPassword() {
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_edit_profile_password);

        final ViewGroup mContainer = (ViewGroup) customDialog.findViewById(
                android.R.id.content).getRootView();

        customDialog.setCancelable(true);
        final EditText passChangePasswordTxt = (EditText) customDialog.findViewById(R.id.password);
        final EditText passChangeConfirmPassTxt = (EditText) customDialog.findViewById(R.id.confirmPassword);
        final TextView invalidConfirmPassTxt = (TextView) customDialog.findViewById(R.id.invalidConfirmPassTxt);
        View view = this.getCurrentFocus();
        passChangePasswordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                invalidConfirmPassTxt.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passChangeConfirmPassTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                invalidConfirmPassTxt.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        TextView cancel = (TextView) customDialog.findViewById(R.id.cancelaction);
        TextView submit = (TextView) customDialog.findViewById(R.id.submitaction);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                hideSoftKeyboard();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passChangePasswordTxt.getText() != null && passChangeConfirmPassTxt != null &&
                        !(passChangePasswordTxt.getText().toString().equals(passChangeConfirmPassTxt.getText().toString()))) {
                    invalidConfirmPassTxt.setVisibility(View.VISIBLE);
                } else {
                    customDialog.dismiss();
                    hideSoftKeyboard();
                }
            }
        });
        customDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.AppConstants.EDIT_ACTIVITY_RESULT) {
            getProfileInfo();
        }
    }

    @Override
    public boolean isInternetConnected() {
        if(UtilityMethods.isInternetConnected(this)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void onError(String errMsg) {
        wishListCount.setVisibility(View.GONE);
        saveSearchCount.setVisibility(View.GONE);
        sitevisitCount.setVisibility(View.GONE);
        propertyCount.setVisibility(View.GONE);
    }
}
