package com.clicbrics.consumer.fragment;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;

import com.clicbrics.consumer.customview.CustomProgressDialog;

public class BaseFragment extends Fragment {

    protected Activity mActivity;
    protected int deviceHeight;
    protected int deviceWidth;
    private CustomProgressDialog progressDialog = null;
    protected Handler mHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mActivity = getActivity();

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)(mActivity)).getWindowManager().getDefaultDisplay().getMetrics(dm);
        deviceHeight = dm.heightPixels;
        deviceWidth = dm.widthPixels;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void showProgressBar(){
        try {
            dismissProgressBar();
            progressDialog = new CustomProgressDialog(mActivity);
            progressDialog.setCancelable(false);
            if(!mActivity.isFinishing())
                progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void dismissProgressBar(){
        try {
            if(progressDialog!=null  && progressDialog.isShowing() && mActivity != null
                    && !mActivity.isFinishing() && !mActivity.isDestroyed()) {
                progressDialog.dismiss();
            }
            progressDialog=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
