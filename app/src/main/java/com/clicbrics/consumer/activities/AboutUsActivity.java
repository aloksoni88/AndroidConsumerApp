package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;


public class AboutUsActivity extends BaseActivity {

    private static final String TAG = AboutUsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        /*FrameLayout containerParent = (FrameLayout) findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_about_us, containerParent);*/

        Toolbar toolbar = findViewById(R.id.id_about_us_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
        initLayout();
    }

    private void initLayout(){
        TextView versionText = (TextView) findViewById(R.id.id_version);
        LinearLayout aboutUsLayout = (LinearLayout) findViewById(R.id.aboutus_card);
        LinearLayout rateOurAppLayout = (LinearLayout) findViewById(R.id.rate_our_app_card);
        LinearLayout shareAppLayout = (LinearLayout) findViewById(R.id.share_application_card);
        LinearLayout submitFeedbackLayout = (LinearLayout) findViewById(R.id.submit_feedback_card);
        LinearLayout tAndCLayout = (LinearLayout) findViewById(R.id.t_and_c_card);
        LinearLayout privacyPolicyLayout = (LinearLayout) findViewById(R.id.privacy_policy_card);
        LinearLayout usedLibraryLayout = (LinearLayout) findViewById(R.id.used_library_card);

        try {
            PackageInfo pinfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String versionName = pinfo.versionName;
            versionText.setText(getResources().getString(R.string.version) + " " + versionName );

            aboutUsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "About Us onClick: ");
                    TrackAnalytics.trackEvent("AboutUs_aboutus_clk_event", Constants.AppConstants.HOLD_TIME ,
                            1, AboutUsActivity.this);
                    if(!UtilityMethods.isInternetConnected(AboutUsActivity.this)){
                        UtilityMethods.showErrorSnackBar(findViewById(R.id.activity_aboutus),
                                getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
                        return;
                    }
                    Intent intent = new Intent(AboutUsActivity.this, AboutUsViewActivity.class);
                    startActivity(intent);
                }
            });

            shareAppLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Share App onClick: ");
                    new EventAnalyticsHelper().ItemClickEvent(AboutUsActivity.this, Constants.AnaylticsClassName.AboutUsScreen,
                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.shareclick.toString());
                  /*  TrackAnalytics.trackEvent("AboutUs_shareapp_clk_event", Constants.AppConstants.HOLD_TIME ,
                            1, AboutUsActivity.this);*/
                    /*if(!UtilityMethods.isInternetConnected(AboutUsActivity.this)){
                        UtilityMethods.showErrorSnackBar(findViewById(R.id.activity_conceirge),
                                getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
                        return;
                    }*/
                    UtilityMethods.shareRedbricsLink(AboutUsActivity.this);
                }
            });

            submitFeedbackLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EventAnalyticsHelper().ItemClickEvent(AboutUsActivity.this, Constants.AnaylticsClassName.AboutUsScreen,
                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.feedbackClick.toString());
                    Log.i(TAG, "Submit Feedback onClick: ");
                }
            });

            tAndCLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Terms and Conditions onClick");
                    new EventAnalyticsHelper().ItemClickEvent(AboutUsActivity.this, Constants.AnaylticsClassName.AboutUsScreen,
                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.termsAndConditionClick.toString());
                  /*  TrackAnalytics.trackEvent("AboutUs_tAndC_clk_event", Constants.AppConstants.HOLD_TIME ,
                            1, AboutUsActivity.this);*/
                    if(!UtilityMethods.isInternetConnected(AboutUsActivity.this)){
                        UtilityMethods.showErrorSnackBar(findViewById(R.id.activity_aboutus),
                                getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
                        return;
                    }
                    Intent intent = new Intent(AboutUsActivity.this, TermsAndConditionActivity.class);
                    startActivity(intent);
                }
            });

            privacyPolicyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EventAnalyticsHelper().ItemClickEvent(AboutUsActivity.this, Constants.AnaylticsClassName.AboutUsScreen,
                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.privacyAndPolicy.toString());
                    Log.i(TAG, "Privacy Policy onClick");
                   /* TrackAnalytics.trackEvent("AboutUs_policy_clk_event", Constants.AppConstants.HOLD_TIME ,
                            1, AboutUsActivity.this);*/
                    if(!UtilityMethods.isInternetConnected(AboutUsActivity.this)){
                        UtilityMethods.showErrorSnackBar(findViewById(R.id.activity_aboutus),
                                getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
                        return;
                    }
                    Intent intent = new Intent(AboutUsActivity.this, PrivacyPolicyActivity.class);
                    startActivity(intent);
                }
            });

            usedLibraryLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EventAnalyticsHelper().ItemClickEvent(AboutUsActivity.this, Constants.AnaylticsClassName.AboutUsScreen,
                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.usedLibraryClick.toString());

                   /* TrackAnalytics.trackEvent("AboutUs_lib_clk_event", Constants.AppConstants.HOLD_TIME ,
                            1, AboutUsActivity.this);*/
                    Log.i(TAG, "Used Library onClick ");
                    if(!UtilityMethods.isInternetConnected(AboutUsActivity.this)){
                        UtilityMethods.showErrorSnackBar(findViewById(R.id.activity_aboutus),
                                getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
                        return;
                    }
                    Intent intent = new Intent(AboutUsActivity.this, UsedLibraryActivity.class);
                    startActivity(intent);
                }
            });


            rateOurAppLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EventAnalyticsHelper().ItemClickEvent(AboutUsActivity.this, Constants.AnaylticsClassName.AboutUsScreen,
                            null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.rateClick.toString());
                    Log.i(TAG, "Rate our App onClick ");
                    final String appPackageName = getPackageName();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appPackageName));
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
        new EventAnalyticsHelper().logUsageStatsEvents(this,onStartTime,System.currentTimeMillis(),Constants.AnaylticsClassName.AboutUsScreen);

      /*  TrackAnalytics.trackEvent("AboutScreen", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);*/
    }
}
