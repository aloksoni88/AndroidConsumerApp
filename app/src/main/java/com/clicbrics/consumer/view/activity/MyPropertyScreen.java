package com.clicbrics.consumer.view.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.fragment.MyPropertiesFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

public class MyPropertyScreen extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_property_screen);
        initLayout();
    }

    private void initLayout(){
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        Toolbar toolbar = findViewById(R.id.my_property_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        MyPropertiesFragment myPropertyFragment = new MyPropertiesFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if(myPropertyFragment.isAdded()){
            fragmentTransaction.show(myPropertyFragment);
        }else {
            fragmentTransaction.replace(R.id.id_fragment_container, myPropertyFragment, Constants.AppConstants.TAG_MY_PROPERTY_FRAGMENT);
        }
        fragmentTransaction.commit();
    }
}
