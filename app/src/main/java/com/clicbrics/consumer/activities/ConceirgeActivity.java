package com.clicbrics.consumer.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.Agent;
import com.buy.housing.backend.userEndPoint.model.GetConciergeAndAgentResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.SupportPagerAdapter;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.fragment.PropertyAdvisorFragment;
import com.clicbrics.consumer.fragment.RelationshipManagerFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.net.UnknownHostException;


public class ConceirgeActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private static final String TAG = ConceirgeActivity.class.getSimpleName();
    public SupportPagerAdapter mSupportPagerAdapter;
    public int mSelectedFragmentPos = 0;


    public UserEndPoint mUserEndPoint;
    CoordinatorLayout mParentLayout;

    private long rmId;
    private String rmName;
    private String rmImageURL;
    private float rmRating;
    private boolean isRMAssigned;
    private String rmComments;

    private long advisorId;
    private String advisorName;
    private String advisorImageURL;
    private float advisorRating;
    private boolean isAdvisorAssigned;
    private String advisorPhoneNo;
    private String advisorComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conceirge);
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        /*FrameLayout containerParent = (FrameLayout) findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_conceirge, containerParent);*/

        buildUserWebService();
        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRMAndAdvisorDetails();
    }

    private void initLayout(){
        Toolbar toolbar = findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        mParentLayout = (CoordinatorLayout) findViewById(R.id.activity_conceirge);

    }

    private void setupViewPager(ViewPager viewPager) {
        mSupportPagerAdapter = new SupportPagerAdapter(getSupportFragmentManager());
        mSupportPagerAdapter.addFragment(new RelationshipManagerFragment(), getString(R.string.relationship_manager));
        mSupportPagerAdapter.addFragment(new PropertyAdvisorFragment(), getString(R.string.property_advisor));
        viewPager.setAdapter(mSupportPagerAdapter);
    }

    private void buildUserWebService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }


    private void getRMAndAdvisorDetails(){
        if (!UtilityMethods.isInternetConnected(this)) {
            Snackbar snackbar = Snackbar
                    .make(mParentLayout, getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction("TRY AGAIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getRMAndAdvisorDetails();
                        }
                    });

            snackbar.setActionTextColor(Color.WHITE);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.uber_red));

            snackbar.show();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setViewPager();
                }
            });
            return;
        }
        if(TextUtils.isEmpty(UtilityMethods.getStringInPref(ConceirgeActivity.this, Constants.AppConstants.USER_NAME_PREF_KEY, ""))){
            setRMAssigned(false);
            setAdvisorAssigned(false);
            setViewPager();
        }else {
            final long userId = UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0);
            HousingLogs.i(TAG, "User Id " + userId);
            showProgressBar();
            new Thread(new Runnable() {
                String errormsg = "";

                @Override
                public void run() {
                    try {
                        final GetConciergeAndAgentResponse response = mUserEndPoint.getConciergeAndAgent(userId)
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        if (response != null && response.getStatus()) {
                            final Agent rm = response.getRm();
                            final Agent advisor = response.getAgent();
                            if (rm != null && !rm.isEmpty()) {
                                setRMAssigned(true);
                                if (rm.getId() != null) {
                                    setRmId(rm.getId());
                                }
                                if (!TextUtils.isEmpty(rm.getName())) {
                                    setRmName(rm.getName());
                                }
                                if (!TextUtils.isEmpty(rm.getPhoto().getUrl())) {
                                    setRmImageURL(rm.getPhoto().getUrl());
                                }
                                if (rm.getCurrentUserRating() != null) {
                                    setRmRating(rm.getCurrentUserRating());
                                }
                                setRmComments("");
                            } else {
                                setRMAssigned(false);
                            }
                            if (advisor != null && !advisor.isEmpty()) {
                                setAdvisorAssigned(true);
                                if (advisor.getId() != null) {
                                    setAdvisorId(advisor.getId());
                                }
                                if (!TextUtils.isEmpty(advisor.getName())) {
                                    setAdvisorName(advisor.getName());
                                }
                                if (!TextUtils.isEmpty(advisor.getPhoto().getUrl())) {
                                    setAdvisorImageURL(advisor.getPhoto().getUrl());
                                }
                                if (advisor.getCurrentUserRating() != null) {
                                    setAdvisorRating(advisor.getCurrentUserRating());
                                }
                                if (!TextUtils.isEmpty(advisor.getPhoneOfficial())) {
                                    setAdvisorPhoneNo(advisor.getPhoneOfficial());
                                }
                                setAdvisorComments("");
                            } else {
                                setAdvisorAssigned(false);
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressBar();
                                    setViewPager();
                                }
                            });
                        } else {
                            if (response != null && !TextUtils.isEmpty(response.getErrorMessage())) {
                                errormsg = response.getErrorMessage();
                            } else {
                                errormsg = "No Relationship Manager found";
                            }
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        errormsg = getResources().getString(R.string.network_error_msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        errormsg = "No Relationship Manager found";
                    }
                    if (!TextUtils.isEmpty(errormsg)) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressBar();
                                setRMAssigned(false);
                                setAdvisorAssigned(false);
                                if (errormsg.equalsIgnoreCase(getResources().getString(R.string.network_error_msg))) {
                                    UtilityMethods.showErrorSnackBar(mParentLayout, errormsg, Snackbar.LENGTH_LONG);
                                }
                                setViewPager();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private ViewPager mViewPager;
    private void setViewPager(){
        mViewPager = (ViewPager) findViewById(R.id.id_support_viewpager);
        setupViewPager(mViewPager);

        TabLayout mTabLayout = (TabLayout)findViewById(R.id.id_support_tablayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(mSelectedFragmentPos);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(ConceirgeActivity.this);
    }

    private String classname="";
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mSelectedFragmentPos = position;

        Fragment selectedFragment = mSupportPagerAdapter.getSelectedFragment(position);
        if(position == 0 && selectedFragment != null){

            if(onStartTime!=0)
            {
                new EventAnalyticsHelper().logUsageStatsEvents(ConceirgeActivity.this,onStartTime,System.currentTimeMillis(),classname);
            }
            classname=Constants.AnaylticsClassName.RELATIONSHIP_MANAGER;
            onStartTime  = System.currentTimeMillis();

        }else if(position == 1 && selectedFragment != null){


            new EventAnalyticsHelper().logUsageStatsEvents(ConceirgeActivity.this,onStartTime,System.currentTimeMillis(),classname);
            classname=Constants.AnaylticsClassName.PROPERTY_ADVISOR;
            onStartTime  = System.currentTimeMillis();

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public long getRmId() {
        return rmId;
    }

    private void setRmId(long rmId) {
        this.rmId = rmId;
    }

    public String getRmName() {
        return rmName;
    }

    private void setRmName(String rmName) {
        this.rmName = rmName;
    }

    public String getRmImageURL() {
        return rmImageURL;
    }

    private void setRmImageURL(String rmImageURL) {
        this.rmImageURL = rmImageURL;
    }

    public float getRmRating() {
        return rmRating;
    }

    private void setRmRating(float rmRating) {
        this.rmRating = rmRating;
    }

    public boolean isRMAssigned() {
        return isRMAssigned;
    }

    public void setRMAssigned(boolean RMAssigned) {
        isRMAssigned = RMAssigned;
    }

    public String getRmComments() {
        return rmComments;
    }

    public void setRmComments(String rmComments) {
        this.rmComments = rmComments;
    }

    public long getAdvisorId() {
        return advisorId;
    }

    private void setAdvisorId(long advisorId) {
        this.advisorId = advisorId;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    private void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }

    public String getAdvisorImageURL() {
        return advisorImageURL;
    }

    private void setAdvisorImageURL(String advisorImageURL) {
        this.advisorImageURL = advisorImageURL;
    }

    public float getAdvisorRating() {
        return advisorRating;
    }

    private void setAdvisorRating(float advisorRating) {
        this.advisorRating = advisorRating;
    }

    public boolean isAdvisorAssigned() {
        return isAdvisorAssigned;
    }

    public void setAdvisorAssigned(boolean advisorAssigned) {
        isAdvisorAssigned = advisorAssigned;
    }

    public String getAdvisorPhoneNo() {
        return advisorPhoneNo;
    }

    public void setAdvisorPhoneNo(String advisorPhoneNo) {
        this.advisorPhoneNo = advisorPhoneNo;
    }

    public String getAdvisorComments() {
        return advisorComments;
    }

    public void setAdvisorComments(String advisorComments) {
        this.advisorComments = advisorComments;
    }

    long onStartTime;



    @Override
    protected void onStop() {
        super.onStop();
        new EventAnalyticsHelper().logUsageStatsEvents(ConceirgeActivity.this,onStartTime,System.currentTimeMillis(),classname);
    }
}
