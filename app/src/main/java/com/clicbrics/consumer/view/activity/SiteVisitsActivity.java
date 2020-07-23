package com.clicbrics.consumer.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.fragment.SiteVisitFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

public class SiteVisitsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_visits);
        //UtilityMethods.setStatusBarColor(this,R.color.status_bar_color_dark);
        initLayout();
    }

    private void initLayout(){
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        Toolbar toolbar = findViewById(R.id.site_visit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        SiteVisitFragment myPropertyFragment = new SiteVisitFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if(myPropertyFragment.isAdded()){
            fragmentTransaction.show(myPropertyFragment);
        }else {
            fragmentTransaction.replace(R.id.id_fragment_container, myPropertyFragment, Constants.AppConstants.TAG_SITE_VISITS_FRAGMENT);
        }
        fragmentTransaction.commit();
    }
}
