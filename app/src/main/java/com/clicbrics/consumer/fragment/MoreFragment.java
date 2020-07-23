package com.clicbrics.consumer.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.UserLoginResponse;
import com.buy.housing.backend.userEndPoint.model.UserProfileResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.AboutUsActivity;
import com.clicbrics.consumer.activities.BlogActivity;
import com.clicbrics.consumer.activities.ConceirgeActivity;
import com.clicbrics.consumer.activities.FavoritesActivity;
import com.clicbrics.consumer.activities.NotificationActivity;
import com.clicbrics.consumer.activities.RecentActivity;
import com.clicbrics.consumer.analytics.AppEventAnalytics;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.databinding.FragmentMoreBinding;
import com.clicbrics.consumer.framework.activity.EditProfileActivity;
import com.clicbrics.consumer.framework.activity.ForgotPasswordActivity;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.helper.APIHelperResultCallback;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.MyPropertyScreen;
import com.clicbrics.consumer.view.activity.SaveSearchActivity;
import com.clicbrics.consumer.view.activity.SiteVisitsActivity;
import com.clicbrics.consumer.viewmodel.ProfileViewModel;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class MoreFragment extends BaseFragment implements APIHelperResultCallback {

    private static final String TAG = "MoreFragment";
    private View mView;
    private FragmentMoreBinding binding;
    private ProfileViewModel profileViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        View view = binding.getRoot();


        binding.idLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.LoginClick.toString());
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        binding.idLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.logout.toString());
                ((HomeScreen) getActivity()).onClickLogout();
            }
        });


        binding.idRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.FilterScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.recentClick.toString());
                startActivity(new Intent(getActivity(), RecentActivity.class));
            }
        });

        binding.idNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.notificationClick.toString());
                startActivity(new Intent(getActivity(), NotificationActivity.class));
            }
        });

        binding.idBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.blogClick.toString());
                startActivity(new Intent(getActivity(), BlogActivity.class));
            }
        });

        binding.idContactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.contactSupport.toString());
                startActivity(new Intent(getActivity(), ConceirgeActivity.class));
            }
        });

        binding.idAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.AboutUsClick.toString());
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
            }
        });


        binding.idEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.editImage.toString());
                onClickEditProfile(true);
            }
        });


        binding.idWishlistIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.wishlistClick.toString());
                Intent intent = new Intent(getActivity(), FavoritesActivity.class);
                startActivity(intent);
            }
        });

        binding.idSaveSearchIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.saveSerachClick.toString());
                Intent intent = new Intent(getActivity(), SaveSearchActivity.class);
                getActivity().startActivityForResult(intent, Constants.ActivityRequestCode.SAVE_SEARCH_RESULT_CODE);
            }
        });

        binding.idSitevisitIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.siteVisitLayout.toString());
                Intent intent = new Intent(getActivity(), SiteVisitsActivity.class);
                startActivity(intent);
            }
        });

        binding.idPropertyIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.propertyLayoutIdea.toString());
                Intent intent = new Intent(getActivity(), MyPropertyScreen.class);
                startActivity(intent);
            }
        });

        binding.idProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.profileClick.toString());
                long userId = UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, -1);
                if (userId == -1 || userId == 0) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    onClickEditProfile(true);
                }
            }
        });

        binding.idProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.MoreScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.profileClick.toString());
                onClickEditProfile(true);
            }
        });

        return view;
    }

    private void setProfileData() {

        if (!TextUtils.isEmpty(UtilityMethods.getStringInPref(getContext(), Constants.AppConstants.USER_NAME_PREF_KEY, ""))) {
            binding.idLoginTxt.setVisibility(View.GONE);
            binding.idLogout.setVisibility(View.VISIBLE);
            binding.idProfileLayout.setVisibility(View.VISIBLE);
            binding.afterLoginLayout.setVisibility(View.VISIBLE);
            binding.idName.setText(UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.USER_NAME_PREF_KEY, ""));
            if (!TextUtils.isEmpty(UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.EMAIL_PREF_KEY, ""))) {
                binding.idEmail.setText(UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.EMAIL_PREF_KEY, ""));
            }

            String stdCode = UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, "+91");
            if (!TextUtils.isEmpty(stdCode)) {
                if (!stdCode.contains("+")) {
                    stdCode = "+" + stdCode;
                }
            } else {
                stdCode = "+91";
            }
            if (!TextUtils.isEmpty(UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.MOBILE_PREF_KEY, ""))) {
                binding.idPhoneNumber.setText(stdCode + "-" + UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.MOBILE_PREF_KEY, ""));
            }


            String imageURL = UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.IMAGE_URL, "");
            if (!TextUtils.isEmpty(imageURL)) {
                Picasso.get().load(imageURL)
                        .placeholder(R.drawable.ic_bank_icon)
                        .into(binding.idProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                binding.idEditImg.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                binding.idEditImg.setVisibility(View.GONE);
                            }
                        });
            } else {
                UtilityMethods.loadCircularImageFromPicasso(getActivity(), binding.idProfileImage);
            }

        } else {
            binding.idLoginTxt.setVisibility(View.VISIBLE);
            binding.idLogout.setVisibility(View.GONE);
            binding.idProfileLayout.setVisibility(View.GONE);
            binding.afterLoginLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;

        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        setProfileData();
        getProfileCount();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfileData();
        boolean udpate=UtilityMethods.getBooleanInPref(getActivity(),Constants.MORE_FRAGMENT_UPDATE,false);
        if(udpate){
            getProfileCount();
            UtilityMethods.saveBooleanInPref(getActivity(),Constants.MORE_FRAGMENT_UPDATE,false);
        }
        if (UtilityMethods.getBooleanInPref(getActivity(), Constants.AppConstants.IS_NEW_NOTIFICATION, false)) {
            binding.notificationCircle.setVisibility(View.VISIBLE);
        } else {
            binding.notificationCircle.setVisibility(View.GONE);
        }
    }

  /*  @Override
    public boolean getUserVisibleHint() {

        if (isVisible()) {
            if (UtilityMethods.getBooleanInPref(getActivity(), Constants.AppConstants.IS_NEW_NOTIFICATION, false)) {
                notification_circle.setImageResource(R.drawable.notification_red_icon);
            } else {
                notification_circle.setImageResource(R.drawable.ic_notification);
            }
            getProfileCount();
        }
        return super.getUserVisibleHint();
    }*/


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, String.valueOf(isVisibleToUser));
        if(isVisibleToUser){
            setProfileData();
            boolean udpate=UtilityMethods.getBooleanInPref(getActivity(),Constants.MORE_FRAGMENT_UPDATE,false);
            if(udpate){
                getProfileCount();
                UtilityMethods.saveBooleanInPref(getActivity(),Constants.MORE_FRAGMENT_UPDATE,false);
            }
        }
    }


    @Override
    public boolean isInternetConnected() {

        if(getActivity() == null)
        {
            return false;
        }

        if (UtilityMethods.isInternetConnected(getActivity())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void showLoader() {
        if(getActivity() != null)
        binding.idProgressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        if(getActivity() != null)
        binding.idProgressLayout.setVisibility(View.GONE);
    }

    @Override
    public void onError(String errMsg) {
        if(getActivity() != null) {
            binding.idWishlistBadgeIcon.setVisibility(View.GONE);
            binding.idSavesearchBadgeIcon.setVisibility(View.GONE);
            binding.idSitevisitBadgeIcon.setVisibility(View.GONE);
            binding.idPropertyBadgeIcon.setVisibility(View.GONE);
        }
    }


    private void editPassword() {
        final Dialog customDialog = new Dialog(getActivity());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_edit_profile_password);

        final ViewGroup mContainer = (ViewGroup) customDialog.findViewById(
                android.R.id.content).getRootView();

        customDialog.setCancelable(true);
        final EditText passChangePasswordTxt = (EditText) customDialog.findViewById(R.id.password);
        final EditText passChangeConfirmPassTxt = (EditText) customDialog.findViewById(R.id.confirmPassword);
        final TextView invalidConfirmPassTxt = (TextView) customDialog.findViewById(R.id.invalidConfirmPassTxt);
        if (getActivity() != null) {
            View view = getActivity().getCurrentFocus();
        }
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

    private void hideSoftKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }


    private void getProfileCount() {
        long userId = UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, -1);

        Observer<UserProfileResponse> observer = new Observer<UserProfileResponse>() {
            @Override
            public void onChanged(@Nullable UserProfileResponse response) {
                if (response.getUser() != null && response.getUser().getFavouriteProjectLIst() != null) {
                    UtilityMethods.clearPreference(getActivity(), Constants.AppConstants.PROJECT_ID_SET);
                    Set<String> favSet = new HashSet<>();
                    for (Long fav : response.getUser().getFavouriteProjectLIst()) {
                        favSet.add(fav + "");
                    }
                    UtilityMethods.saveWishlistInPref(getActivity(), favSet);
                }
                if (response.getFavoriteCount() != null && response.getFavoriteCount() != 0) {
                    binding.idWishlistBadgeIcon.setVisibility(View.VISIBLE);
                    binding.idWishlistBadgeIcon.setText(response.getFavoriteCount() + "");
                } else {
                    binding.idWishlistBadgeIcon.setVisibility(View.GONE);
                }
                if (response.getSavedSearchCount() != null && response.getSavedSearchCount() != 0) {
                    binding.idSavesearchBadgeIcon.setVisibility(View.VISIBLE);
                    binding.idSavesearchBadgeIcon.setText(response.getSavedSearchCount() + "");
                } else {
                    binding.idSavesearchBadgeIcon.setVisibility(View.GONE);
                }
                if (response.getSiteVisitCount() != null && response.getSiteVisitCount() != 0) {
                    binding.idSitevisitBadgeIcon.setVisibility(View.VISIBLE);
                    binding.idSitevisitBadgeIcon.setText(response.getSiteVisitCount() + "");
                } else {
                    binding.idSitevisitBadgeIcon.setVisibility(View.GONE);
                }
                if (response.getBookedPropertyCount() != null && response.getBookedPropertyCount() != 0) {
                    binding.idPropertyBadgeIcon.setVisibility(View.VISIBLE);
                    binding.idPropertyBadgeIcon.setText(response.getBookedPropertyCount() + "");
                } else {
                    binding.idPropertyBadgeIcon.setVisibility(View.GONE);
                }
            }
        };
        profileViewModel.getUserProfileDetail(this, userId).observe(this, observer);
    }


    Dialog editDialog;

    private void onClickEditProfile(final boolean isProfileUpdate) {
        final Constants.AppConstants.LoginType loginType = Enum.valueOf(Constants.AppConstants.LoginType.class,
                UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.LOGIN_TYPE_PREF_KEY,
                        Constants.AppConstants.LoginType.Housing.toString()));
        TrackAnalytics.trackEvent(this.getResources().getString(R.string.ga_button_category), "Edit Profile Clicked ", getActivity());
        editDialog = new Dialog(getActivity());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.dialog_edit_profile);

        editDialog.setCancelable(true);
        final EditText profileChangeConfirmPassTxt = (EditText) editDialog.findViewById(R.id.confirmPassword);
        final TextView invalidPasswordTxt = (TextView) editDialog.findViewById(R.id.invalidPasswordTxt);
        final TextView forgotPassword = (TextView) editDialog.findViewById(R.id.forgotPassword);
        if (!UtilityMethods.getBooleanInPref(getActivity(), Constants.AppConstants.IS_PASSWORD_AVAILABLE_PREF_KEY, true)) {
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
                final String emailID = UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.EMAIL_PREF_KEY, "");
                //final GCMRegistration gcmRegistration = new GCMRegistration(ProfileScreen.this);
                Log.d(TAG, "internet connected->" + UtilityMethods.isInternetConnected(getActivity()));
                if (!UtilityMethods.isInternetConnected(getActivity())) {
                    /*editDialog.dismiss();
                    View.OnClickListener positiveListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View View) {
                            if (mCustomDialog != null)
                                mCustomDialog.dismiss();
                        }
                    };
                    mCustomDialog = UtilityMethods.showAlert(ProfileScreen.this, R.string.error, getString(R.string.internet_is_very_slow), true, R.string.ok, positiveListener, -1, null, null);*/
                    if (getActivity() != null) {
                        UtilityMethods.showErrorSnackbarOnTop(getActivity());
                    }
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
                                String userPhone = UtilityMethods.getStringInPref(getActivity(), Constants.AppConstants.MOBILE_PREF_KEY, "");
                                String userValue = "";
                                if (TextUtils.isEmpty(userPhone)) {
                                    userValue = emailID;
                                } else {
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
                                        if (getActivity() != null) {
                                            login.setPassword(profileChangeConfirmPassTxt.getText().toString());
                                            login.setRThirdPartyID(getActivity().getIntent().getStringExtra("ThirdPartyID"));
                                            login.setEmail(userValue);
                                        }
                                    }
                                    break;
                                    case Google: {
                                        if (getActivity() != null) {
                                            login.setPassword(profileChangeConfirmPassTxt.getText().toString());
                                            login.setRThirdPartyID(getActivity().getIntent().getStringExtra("ThirdPartyID"));
                                            login.setEmail(userValue);
                                        }
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
                                            String mobile = binding.idPhoneNumber.getText().toString();
                                            String mobileNo = "", countryCode = "";
                                            if (mobile.contains("-")) {
                                                countryCode = mobile.split("-")[0];
                                                mobileNo = mobile.split("-")[1];
                                            }
                                            if (isProfileUpdate) {
                                                editDialog.dismiss();
                                                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                                intent.putExtra("Name", binding.idName.getText().toString());
                                                intent.putExtra("Email", binding.idEmail.getText().toString());
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
                                errorMSG = getActivity().getResources().getString(R.string.network_error_msg);
                                e.printStackTrace();
                            } catch (Exception e) {
                                isPassMatch = false;
                                errorMSG = getActivity().getResources().getString(R.string.something_went_wrong);
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
                Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                intent.putExtra(Constants.AppConstants.ACTION_BAR_TITLE, getString(R.string.get_password));
                intent.putExtra(Constants.AppConstants.EDIT_FIELD_IS_ENABLE, false);
                startActivity(intent);
            }
        });
        editDialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

/* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        {
            if (resultCode == RESULT_OK && requestCode == Constants.ActivityRequestCode.SAVE_SEARCH_RESULT_CODE) {
                if (data != null) {
                }
            }
        }
    }*/
}
