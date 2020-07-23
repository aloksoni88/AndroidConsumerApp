package com.clicbrics.consumer.framework.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.clicworth.GetEstimateActivity;
import com.clicbrics.consumer.customview.CustomProgressDialog;
import com.clicbrics.consumer.framework.customview.CustomDialog;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.DriveViewActivity;
import com.clicbrics.consumer.view.activity.DriveViewScreen;
import com.clicbrics.consumer.view.activity.DroneViewActivity;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.VirtualTourViewActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by Paras on 28-06-2016.
 */
public class BaseActivity extends AppCompatActivity {

    protected Handler mHandler;
    protected Activity mActivity;
    private CustomProgressDialog progressDialog = null;
    protected int deviceHeight;
    protected int deviceWidth;
    protected CustomDialog mCustomDialog;
    protected enum LoginFor {
        DEFAULT, SAVESEARCH, FAVORITE_LISTING, SAVESEARCHLIST,
        CONCIERGE, SITE_VISIT, My_PROPERTIES, NOTIFICATIONS, ABOUT_US;
    }
    protected enum StatusBarTheme{
        LIGHT,DARK
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        mActivity = this;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        deviceHeight = dm.heightPixels;
        deviceWidth = dm.widthPixels;

       // final Typeface mFont = Typeface.createFromAsset(getAssets(),
       //         "font/HelveticaNeueUltraLight.ttf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        UtilityMethods.setStatusBarColor(this,R.color.profile_bg_color);
        //UtilityMethods.setAppFont(mContainer, mFont, false);
        setStatusBar();
    }

    private void setStatusBar(){
        if(mActivity instanceof SplashActivity){
            Log.i("Activity Name", "SplashActivity");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if(mActivity instanceof VirtualTourViewActivity){
            Log.i("Activity Name", "VirtualTourViewActivity");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if(mActivity instanceof DriveViewActivity){
            Log.i("Activity Name", "DriveViewActivity");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if(mActivity instanceof DriveViewScreen){
            Log.i("Activity Name", "DriveViewScreen");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if(mActivity instanceof DroneViewActivity){
            Log.i("Activity Name", "DroneViewActivity");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            Log.i("Activity Name", mActivity.getPackageName());
        }
    }

    /*public void setStatusBarColor(int color, StatusBarTheme theme){
        if(mActivity instanceof HomeScreen){

        }
        UtilityMethods.setStatusBarColor(this,color);
        if(color == R.color.status_bar_color_light) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            UtilityMethods.setStatusBarColor(this, R.color.status_bar_color_light);
        }else if(){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }*/

    public void hideSoftKeyboard() {
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.RESULT_HIDDEN);

        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void showSoftKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constants.AppConstants.STORAGE_PERMISSION:{
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {

                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        //UtilityMethods.showSnackBar(parentLayout,getResources().getString(R.string.permision), Snackbar.LENGTH_SHORT);
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/


    public void showProgressBar(){
        try {
            dismissProgressBar();
            progressDialog = new CustomProgressDialog(this);
            progressDialog.setCancelable(false);
            if(!isFinishing() && !mActivity.isDestroyed())
                progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void dismissProgressBar(){
        try {
            if(progressDialog!=null  && progressDialog.isShowing() && mActivity != null
                    && !mActivity.isFinishing() && !mActivity.isDestroyed())
                progressDialog.dismiss();
            progressDialog=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void addToolBar(Toolbar tb){
        if(tb!=null) {
            tb.setTitleTextColor(Color.WHITE);
            setSupportActionBar(tb);
        }
    }
    protected void addBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

   /* private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(BaseActivity.this);

            if(heightDiff <= contentViewTop){
                onHideKeyboard();

                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);

                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;

    protected void onShowKeyboard(int keyboardHeight) {}
    protected void onHideKeyboard() {}

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        rootLayout = (ViewGroup) findViewById(R.id.rootLayout);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }*/

}
