
package com.clicbrics.consumer.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.buy.housing.backend.userEndPoint.model.UpdateUserStatResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.ConceirgeActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.CircularImageView;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Calendar;


/**
 * Created by Alok on 19-04-2017.
 */

public class RelationshipManagerFragment extends BaseFragment{

    private static final String TAG = RelationshipManagerFragment.class.getSimpleName();

    private UserEndPoint mUserEndPoint;
    private String phoneNo = "";
    private CircularImageView conceirgePhoto;
    private TextView conceirgeName;
    private ProgressBar imageProgressBar;
    private ImageView blurImageView,defaultImage;;
    private NestedScrollView mParentLayout;
    private FloatingActionButton callButton;
    private Button submitButton;
    private RatingBar rateBar;
    private TextView rateMsg;
    private EditText comments;
    private long rmId;
    String rmComments="";
    float rmRating;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_releationship_manager_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        defaultImage = (ImageView) view.findViewById(R.id.id_default_image);
        conceirgePhoto = (CircularImageView) view.findViewById(R.id.id_concierge_icon);
        blurImageView = (ImageView) view.findViewById(R.id.id_concierge_blurr_image);
        conceirgeName = (TextView) view.findViewById(R.id.id_concierge_name);
        imageProgressBar = (ProgressBar) view.findViewById(R.id.id_image_progressBar);
        mParentLayout = (NestedScrollView) view.findViewById(R.id.rm_fragment_layout);
        submitButton = (Button) view.findViewById(R.id.id_submit_btn);
        callButton = (FloatingActionButton) view.findViewById(R.id.id_rm_call_btn);
        rateBar = (RatingBar)view.findViewById(R.id.rate_bar);
        rateMsg = (TextView) view.findViewById(R.id.id_rate_message);
        comments = (EditText) view.findViewById(R.id.id_rm_comments);
        buildUserWebService();
        getRMDetails();

        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rateBar.getRating() > 0 && ratingBar.getRating() <= 5){
                    comments.setVisibility(View.VISIBLE);
                    if(ratingBar.getRating() == rmRating && TextUtils.isEmpty(comments.getText().toString())){
                        submitButton.setVisibility(View.GONE);
                    }else{
                        submitButton.setVisibility(View.VISIBLE);
                    }
                }else{
                    comments.setVisibility(View.GONE);
                    submitButton.setVisibility(View.GONE);
                }

            }
        });

        comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(rateBar.getRating() > 0 && rateBar.getRating() <= 5){
                    if(rateBar.getRating() == rmRating && TextUtils.isEmpty(comments.getText().toString())){
                        submitButton.setVisibility(View.GONE);
                    }else{
                        submitButton.setVisibility(View.VISIBLE);
                    }
                }else{
                    submitButton.setVisibility(View.GONE);
                }
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.RELATIONSHIP_MANAGER,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.callClick.toString());
                Log.i(TAG, "onClick: Relationship Manager call button ");
                onClickCallButton();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.RELATIONSHIP_MANAGER,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.submitClick.toString());
                Log.i(TAG, "onClick: Relationship Manager submit button ");
                doRating(rateBar.getRating());
            }
        });
    }


    private void getRMDetails(){
        if(getActivity() != null && ((ConceirgeActivity) getActivity()).isRMAssigned()) {
            imageProgressBar.setVisibility(View.VISIBLE);
            rmId = ((ConceirgeActivity) getActivity()).getRmId();
            String name = ((ConceirgeActivity) getActivity()).getRmName();
            String imageURL = ((ConceirgeActivity) getActivity()).getRmImageURL();
            rmRating = ((ConceirgeActivity) getActivity()).getRmRating();
            rmComments = ((ConceirgeActivity) getActivity()).getRmComments();
            if (!TextUtils.isEmpty(name)) {
                UtilityMethods.saveStringInPref(getActivity(), Constants.SharedPreferConstants.CONCEIRGE_NAME, name);
                conceirgeName.setVisibility(View.VISIBLE);
                conceirgeName.setText(name);
            } else {
                conceirgeName.setVisibility(View.GONE);
            }
            if( rmRating != 0){
                //rateBar.setEnabled(false);
                rateBar.setRating(rmRating);
                //rateMsg.setVisibility(View.GONE);
            }else{
                rateBar.setRating(0);
                //rateBar.setEnabled(true);
            }
            if(!TextUtils.isEmpty(rmComments)){
                comments.setText(rmComments);
            }else{
                comments.setText("");
            }
            UtilityMethods.saveFloatInPref(getActivity(),Constants.SharedPreferConstants.RM_RATING,rmRating);
            if (imageURL != null) {
                UtilityMethods.saveStringInPref(getActivity(), Constants.SharedPreferConstants.CONCEIRGE_PHOTO_URL, imageURL);
                Picasso.get().load(imageURL)
                        .into(conceirgePhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                imageProgressBar.setVisibility(View.GONE);
                                defaultImage.setVisibility(View.GONE);
                                conceirgePhoto.setVisibility(View.VISIBLE);
                                blurImageView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                imageProgressBar.setVisibility(View.GONE);
                                conceirgePhoto.setVisibility(View.GONE);
                                blurImageView.setVisibility(View.GONE);
                                defaultImage.setVisibility(View.VISIBLE);
                            }
                        });
                new GetBitmapImage(imageURL).execute();
            } else {
                imageProgressBar.setVisibility(View.GONE);
                defaultImage.setVisibility(View.VISIBLE);
                conceirgePhoto.setVisibility(View.GONE);
                blurImageView.setVisibility(View.GONE);
            }

        }else{
            defaultImage.setVisibility(View.VISIBLE);
            conceirgePhoto.setVisibility(View.GONE);
            blurImageView.setVisibility(View.GONE);
            conceirgeName.setVisibility(View.GONE);
            rateBar.setVisibility(View.GONE);
            rateMsg.setVisibility(View.GONE);
            comments.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
        }
    }

    private void buildUserWebService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }



    private void onClickCallButton(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        if(TextUtils.isEmpty(phoneNo)){
            phoneNo = UtilityMethods
                    .getStringInPref(getActivity(),
                            Constants.AppConfigConstants.CONTACT_NUMBER, Constants.AppConstants.DEFAULT_NUMBER);
        }
        phoneNo.replaceAll("-","");
        intent.setData(Uri.parse("tel:" + phoneNo));
        startActivity(intent);
        //update stats data
        callUpdateStatAPI();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap getBlurImageBitmap(Bitmap image) {
        try {
            if(image != null) {
                final float BITMAP_SCALE = 0.5f;
                final float BLUR_RADIUS = 24.5f;
                int width = Math.round(image.getWidth() * BITMAP_SCALE);
                int height = Math.round(image.getHeight() * BITMAP_SCALE);

                Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
                Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

                RenderScript rs = RenderScript.create(getActivity());
                ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
                Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
                theIntrinsic.setRadius(BLUR_RADIUS);
                theIntrinsic.setInput(tmpIn);
                theIntrinsic.forEach(tmpOut);
                tmpOut.copyTo(outputBitmap);
                return outputBitmap;
            }else{
                return BitmapFactory.decodeResource(getResources(),R.drawable.splash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void doRating(final float rating){
        if(rating == 0.0){
            UtilityMethods.showSnackBar(mParentLayout,"Your feedback is valuable. \nPlease provide rating about your Relationship Manager",Snackbar.LENGTH_LONG);
            return;
        }
        if(rmRating == rating && rmComments != null && rmComments.equalsIgnoreCase(comments.getText().toString().trim())){
            UtilityMethods.showSnackBar(mParentLayout,"No changes!",Snackbar.LENGTH_LONG);
            return;
        }
        showProgressBar();
        final long userId = UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, 0l);
        new Thread(new Runnable() {
            String errorMsg = "", succussMsg="";
            @Override
            public void run() {
                try {
                    UserEndPoint.RateAgent rateAgent = mUserEndPoint.rateAgent(userId,rmId,rating);
                    if(!TextUtils.isEmpty(comments.getText().toString().trim())) {
                        rateAgent.setComments(comments.getText().toString().trim());
                    }
                    rateAgent.setRating(rateBar.getRating());
                    CommonResponse rateAgentResponse = rateAgent.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if(rateAgentResponse != null && rateAgentResponse.getStatus()){
                        new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.RELATIONSHIP_MANAGER,
                                null, Constants.ApiName.rateAgent.toString(),Constants.AnalyticsEvents.SUCCESS,
                               null);
                        succussMsg = "Thanks for your valuable feedback!";
                        rmRating = rateBar.getRating();
                        rmComments = comments.getText().toString();
                    }else{
                        if(rateAgentResponse != null && !rateAgentResponse.getErrorMessage().isEmpty()){
                            errorMsg = rateAgentResponse.getErrorMessage();
                        }else{
                            errorMsg = "Could not able to rate. \n Please try again!";
                        }
                    }
                } catch (UnknownHostException e) {
                    errorMsg = getResources().getString(R.string.network_error_msg);
                    e.printStackTrace();
                } catch (Exception e) {
                    errorMsg = getResources().getString(R.string.something_went_wrong);
                    e.printStackTrace();
                }
                if(!TextUtils.isEmpty(succussMsg)){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            comments.setText("");
                            //rateBar.setEnabled(false);
                            //rateMsg.setVisibility(View.GONE);
                            submitButton.setVisibility(View.GONE);
                            UtilityMethods.showSnackBar(mParentLayout,succussMsg,Snackbar.LENGTH_LONG);
                        }
                    });
                } else if(!TextUtils.isEmpty(errorMsg)){
                    new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.RELATIONSHIP_MANAGER,
                            null, Constants.ApiName.rateAgent.toString(),Constants.AnalyticsEvents.FAILED,
                            errorMsg);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            UtilityMethods.showErrorSnackBar(mParentLayout,errorMsg,Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    class GetBitmapImage extends AsyncTask<Void,Void,Bitmap> {
        String imageURL;
        GetBitmapImage(String imageURL){
            this.imageURL = imageURL;
        }

        @Override
        protected Bitmap doInBackground(Void... objects) {
            Bitmap bitmapImage=null;
            try {
                URL imageurl = new URL(imageURL);
                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                imageurl = uri.toURL();
                URLConnection connection = new URL(imageurl.toString()).openConnection();
                bitmapImage = BitmapFactory.decodeStream(connection.getInputStream()) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmapImage;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                Bitmap blurBitmap = getBlurImageBitmap(bitmap);
                if(blurBitmap != null){
                    blurImageView.setImageBitmap(blurBitmap);
                }else{
                    blurImageView.setImageResource(R.drawable.splash);
                }
            }else{
                blurImageView.setImageResource(R.drawable.splash);
            }
        }
    }

    private void callUpdateStatAPI(){
        try {
            if(getActivity() != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final long userId = UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, -1);
                        final long virtualUID = UtilityMethods.getLongInPref(getActivity(), Constants.AppConstants.VIRTUAL_UID, -1);
                        try {
                            UserEndPoint.UpdateStat updateStat = mUserEndPoint.updateStat(Constants.AppConstants.SOURCE);
                            if (userId != -1) {
                                updateStat.setUId(userId);
                            } else if (virtualUID != -1) {
                                updateStat.setUId(virtualUID);
                            }
                            updateStat.setCall(true);
                            UpdateUserStatResponse updateUserStatResponse = updateStat.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                            if (updateUserStatResponse != null && updateUserStatResponse.getStatus()) {
                                new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.RELATIONSHIP_MANAGER,
                                        null, Constants.ApiName.updateStat.toString(),Constants.AnalyticsEvents.SUCCESS,
                                        null);
                                long virtualId = updateUserStatResponse.getUid();
                                UtilityMethods.saveLongInPref(getActivity(), Constants.AppConstants.VIRTUAL_UID, virtualId);
                                Log.i(TAG, "Call UpdateStat Response successfull... " + " Virtual ID -> " + virtualId);
                            } else if (updateUserStatResponse != null && !updateUserStatResponse.getErrorMessage().isEmpty()) {
                                Log.i(TAG, "Call UpdateStat Response Failed... " + " Error Message -> " + updateUserStatResponse.getErrorMessage());
                            } else {
                                Log.i(TAG, "Call UpdateStat Response Failed... " + " Error Message null -> ");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  long onStartTime;
    @Override
    public void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();
        TrackAnalytics.trackEvent("RelationshipManager", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), getActivity());
    }*/

}
