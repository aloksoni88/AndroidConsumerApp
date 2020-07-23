package com.clicbrics.consumer.framework.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.customview.CustomDialog;
import com.clicbrics.consumer.utils.UtilityMethods;

;

public class BaseActivityLight extends AppCompatActivity {

	protected Context mContext = this;
	private ProgressDialog progressDialog = null;
	protected Handler mHandler;
	protected double amt;
	protected String mode;
	CustomDialog mCustomDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		UtilityMethods.setStatusBarColor(this,R.color.status_bar_color_light);

	}
	protected void addBackButton(String txt){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		//getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
		getSupportActionBar().setTitle(txt);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:{
				hideSoftKeyboard();
				finish();
			}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	public void showProgressBar(String msg){
		try {
			dismissProgressBar();
			progressDialog = new ProgressDialog(mContext);
			if(TextUtils.isEmpty(msg))
                msg="Please Wait..";
			progressDialog.setMessage(msg);
			progressDialog.setIndeterminate(true);
			progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bg));
			progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bg));
			progressDialog.setCancelable(false);
			if(!isFinishing())
                progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void dismissProgressBar(){
		try {
			if(progressDialog!=null  && progressDialog.isShowing())
                progressDialog.dismiss();
			progressDialog=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void hideSoftKeyboard(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.RESULT_HIDDEN);
	}

	public void showSoftKeyboard(){
		InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

}
