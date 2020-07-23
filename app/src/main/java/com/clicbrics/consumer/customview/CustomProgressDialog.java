package com.clicbrics.consumer.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import com.clicbrics.consumer.R;

public class CustomProgressDialog extends Dialog {

//	private ImageView loader;
//	private TextView msg ;

	public CustomProgressDialog(Context context) {
		super(context, R.style.BGTransparentPB);
		WindowManager.LayoutParams wlmp = getWindow().getAttributes();
		//wlmp.gravity = Gravity.CENTER;
		getWindow().setAttributes(wlmp);
		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);
//
//		LinearLayout layout = new LinearLayout(context);
//		layout.setOrientation(LinearLayout.VERTICAL);
//		layout.setGravity(Gravity.CENTER);
//		layout.setBackgroundColor(Color.WHITE);
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT);
//		params.gravity = Gravity.CENTER;
//		loader = new ImageView(context);
//		loader.setImageResource(R.drawable.loader_animation);
//
//		msg = new TextView(context);
//		msg.setText("Loading...");
//		msg.setTextColor(Color.BLACK);
//		//		tv.setTypeface(null, Typeface.BOLD);
//		msg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//		msg.setPadding(0, 0, 0, 0);
//		LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.WRAP_CONTENT);
//		params.gravity = Gravity.CENTER_HORIZONTAL;
//		layout.addView(loader, imageParams);
//
//		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.WRAP_CONTENT);
//		params.gravity = Gravity.CENTER_HORIZONTAL;
//		//		params.set
//		msg.setGravity(Gravity.CENTER);
//		layout.addView(msg, params2);
//		addContentView(layout, params);
//		addContentView(R.layout.e_custom_progress_dialog,params);
		setContentView(R.layout.e_custom_progress_dialog);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	}


//	public void setMessage(String message) {
//		msg.setText(message);
//	}
	@Override
	public void show() {
		super.show();

//		final AnimationDrawable frameAnimation = (AnimationDrawable) loader.getDrawable();
//
//		// Start the animation (looped playback by default).
//		//		 frameAnimation.start();
//
//		loader.post(new Runnable(){
//			public void run(){
//				frameAnimation.start();
//			}
//		});

	}
	public void isCancelable(boolean isCancelable){
		setCancelable(isCancelable);
	}
}