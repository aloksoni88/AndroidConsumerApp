package com.clicbrics.consumer.activities;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.PropertyDocumentListAdapter;
import com.clicbrics.consumer.fragment.MyPropertyDetailFragment;
import com.clicbrics.consumer.fragment.PropertyDocumentFragment;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;


@SuppressWarnings("deprecation")
public class MyPropertyDetails extends BaseActivity implements PropertyDocumentListAdapter.PermissionCheckListener{
    private final String TAG = MyPropertyDetails.class.getSimpleName();
    public TextView mToolbarText;
    public AppBarLayout mToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_property_details);

        /*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        UtilityMethods.setStatusBarColor(this,R.color.status_bar_color_dark);*/
        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initLayout(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
        mToolbarText = (TextView) findViewById(R.id.id_property_details_toolbar_text);
        mToolbarLayout = (AppBarLayout) findViewById(R.id.toolbar_container_layout);
        mToolbarText.setText(getResources().getString(R.string.payment_history));

        MyPropertyDetailFragment myPropertyDetailFragment = new MyPropertyDetailFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if(myPropertyDetailFragment.isAdded()){
            fragmentTransaction.show(myPropertyDetailFragment);
        }else {
            fragmentTransaction.replace(R.id.id_fragment_details_container, myPropertyDetailFragment, Constants.AppConstants.TAG_MY_PROPERTY_DETAILS_FRAGMENT);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected: ");
        if(item.getItemId() == android.R.id.home){
            Fragment paymentHistoryFragment = getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_PAYMENT_HISTORY_FRAGMENT);
            Fragment serviceRequestFragment = getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_SERVICE_REQUEST_FRAGMENT);
            Fragment propertyDocumentFragment = getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_PROPERTY_DOCUMENT_FRAGMENT);
            if(paymentHistoryFragment != null && paymentHistoryFragment.isVisible()){
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack();
                return false;
            }
            else if(serviceRequestFragment != null && serviceRequestFragment.isVisible()){
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack();
                return false;
            }
            else if(propertyDocumentFragment != null && propertyDocumentFragment.isVisible()){
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack();
                return false;
            }
            else{
                return super.onOptionsItemSelected(item);
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void checkStoragePermission(String url) {
        Log.i(TAG, "checkStoragePermission: ");
        PropertyDocumentFragment fragment = (PropertyDocumentFragment) getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_PROPERTY_DOCUMENT_FRAGMENT);
        fragment.checkStoragePermission(url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: ");
        PropertyDocumentFragment fragment = (PropertyDocumentFragment) getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_PROPERTY_DOCUMENT_FRAGMENT);
        fragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
