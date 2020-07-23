package com.clicbrics.consumer.framework.customview;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.clicbrics.consumer.R;


public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);

	}

	Button positive,negative;
	TextView titleMsgView,msgView;
	int mTitleId=-1;
	String msg = null;
	int positivetextId=-1;
	View.OnClickListener positiveListener;
	int negativeTextId =-1;
	View.OnClickListener negetiveListener;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		titleMsgView = (TextView)findViewById(R.id.txt_title);
		msgView = (TextView)findViewById(R.id.txt_msg);
			int width = (int)(getContext().getResources().getDisplayMetrics().widthPixels*0.75);
		msgView.getLayoutParams().width=width;
		positive = (Button)findViewById(R.id.btn_positive);
		negative = (Button)findViewById(R.id.btn_negative);
		if(mTitleId!=-1){
			titleMsgView.setText(mTitleId);
		}
		if(msg!=null){
			msgView.setText(msg);
		}

		if(positivetextId!=-1){
			positive.setText(positivetextId);
			positive.setOnClickListener(positiveListener);
			final int version = Build.VERSION.SDK_INT;
			//positive.setBackgroundResource(R.drawable.btn_transparent_selector);
			positive.setVisibility(View.VISIBLE);
		}
		if(negativeTextId!=-1){
			negative.setText(negativeTextId);
			negative.setOnClickListener(negetiveListener);
			//negative.setBackgroundResource(R.drawable.btn_transparent_selector);
			negative.setVisibility(View.VISIBLE);
		}
		LinearLayout normal_view_layout = (LinearLayout)findViewById(R.id.normal_view_layout);
		normal_view_layout.setVisibility(View.VISIBLE);

	}

	public void setMessage(String msg){
		this.msg = msg;
	}

	public void setTitle(int id){
		mTitleId = id;
	}

	public void setPositiveButton(int textId,View.OnClickListener positiveListener){
		positivetextId = textId;
		this.positiveListener = positiveListener;
	}

	public void setNegativeButton(int textId,View.OnClickListener positiveListener){
		this.negetiveListener = positiveListener;
		this.negativeTextId = textId;

	}
}
