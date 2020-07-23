package com.clicbrics.consumer.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.buy.housing.backend.propertyEndPoint.model.PaymentPlan;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.fragment.PaymentPlanFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.PaymentPlanItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@SuppressWarnings("deprecation")
public class PaymentPlanActivity extends BaseActivity {

    public static LinkedHashMap<String, List<PaymentPlanItem>> paymentPlans;
    public static List<String> keys;
    public static List<String> planDetail;

    TabLayout tabLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_payment);
        Toolbar tabToolbar = (Toolbar) findViewById(R.id.toolbar_payment_plan);
        tabToolbar.setTitle("");
        setSupportActionBar(tabToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        addBackButton();

        int pageNo = getIntent().getIntExtra(Constants.IntentKeyConstants.PAGE_INDEX,0);

        setData();
        setTabLayout();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.payment_plan_view_pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setCurrentItem(pageNo);

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
    }

    private void setData() {
        paymentPlans = new LinkedHashMap<>();
        keys = new ArrayList<String>();
        planDetail = new ArrayList<String>();

        List<PaymentPlan> paymentPlanList = ((HousingApplication)getApplicationContext()).getPaymentPlanList();
        for (int i = 0; i < paymentPlanList.size(); i++) {
            List<PaymentPlanItem> planDetailList = new ArrayList<PaymentPlanItem>();
            //dont't shwo payment plan if getHide flag for the payment plan
            if(paymentPlanList.get(i).getHide() != null && paymentPlanList.get(i).getHide()){
                continue;
            }
            String planName = paymentPlanList.get(i).getName();
            String planDescription = paymentPlanList.get(i).getDetail();

            for (int j = 0; j < paymentPlanList.get(i).getKeyValuePairList().size(); j++) {
                planDetailList.add(new PaymentPlanItem(paymentPlanList.get(i).getKeyValuePairList().get(j).getKey()
                        , paymentPlanList.get(i).getKeyValuePairList().get(j).getValue()));
            }

            paymentPlans.put(planName, planDetailList);
            keys.add(planName);
            planDetail.add(planDescription);
        }

        Log.d("TAG","Keys size->"+keys.size());
        for(int i=0;i <keys.size();i++){
            Log.d("TAG","Key->"+keys.get(i));
        }

        for(int i=0;i<paymentPlans.size();i++){
            List<PaymentPlanItem> ppi = paymentPlans.get(keys.get(i));
            for(int x=0;x<ppi.size();x++) {
                Log.d("TAG","Item->" + ppi.get(x).title + " " + ppi.get(x).subtitle );
            }
        }
    }

    private void setTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for (int i = 0; i < keys.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(keys.get(i)));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            //Log.d("PPa","position->"+position);
            return PaymentPlanFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
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
        TrackAnalytics.trackEvent("PaymentPlanActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);
    }

}
