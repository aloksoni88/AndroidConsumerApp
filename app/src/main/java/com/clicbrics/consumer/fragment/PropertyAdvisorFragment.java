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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.ConceirgeActivity;
import com.clicbrics.consumer.activities.FavoritesActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.customview.CircularImageView;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Created by Alok on 19-04-2017.
 */

public class PropertyAdvisorFragment extends BaseFragment{

    private static final String TAG = PropertyAdvisorFragment.class.getSimpleName();

    private UserEndPoint mUserEndPoint;
    private String phoneNo = "";
    private CircularImageView advisorPhoto;
    private TextView advisorName;
    private ProgressBar imageProgressBar;
    private ImageView blurImageView,defaultImage;
    private NestedScrollView mParentLayout;
    private FloatingActionButton callButton;
    private Button submitButton;
    private RatingBar rateBar;
    private TextView rateMsg;
    private TextView helpMessage;
    private EditText comments;
    private long advisorId;
    String advisorComments="";
    float advisorRating;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_property_advisor_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        defaultImage = (ImageView) view.findViewById(R.id.id_default_image);
        advisorPhoto = (CircularImageView) view.findViewById(R.id.id_advisor_image);
        blurImageView = (ImageView) view.findViewById(R.id.id_concierge_blurr_image);
        advisorName = (TextView) view.findViewById(R.id.id_advisor_name);
        imageProgressBar = (ProgressBar) view.findViewById(R.id.id_image_progressBar);
        mParentLayout = (NestedScrollView) view.findViewById(R.id.advisor_fragment_layout);
        submitButton = (Button) view.findViewById(R.id.id_submit_btn);
        callButton = (FloatingActionButton) view.findViewById(R.id.id_advisor_call_btn);
        rateBar = (RatingBar)view.findViewById(R.id.rate_bar);
        rateMsg = (TextView) view.findViewById(R.id.id_rate_message);
        helpMessage = (TextView) view.findViewById(R.id.id_help_msg);
        comments = (EditText) view.findViewById(R.id.id_advisor_comments);
        buildUserWebService();
        getPropertyAdvisorDetail();

        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rateBar.getRating() > 0 && ratingBar.getRating() <= 5){
                    comments.setVisibility(View.VISIBLE);
                    if(ratingBar.getRating() == advisorRating && TextUtils.isEmpty(comments.getText().toString())){
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
                    if(rateBar.getRating() == advisorRating && TextUtils.isEmpty(comments.getText().toString())){
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
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.PROPERTY_ADVISOR,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.callClick.toString());
                Log.i(TAG, "onClick: Property Advisor call button ");
                onClickCallButton();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.PROPERTY_ADVISOR,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.submitClick.toString());
                Log.i(TAG, "onClick: Property Advisor submit button ");
                doRating(rateBar.getRating());
            }
        });
    }

    private void buildUserWebService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }


    private void getPropertyAdvisorDetail(){
        if(getActivity() != null && ((ConceirgeActivity) getActivity()).isAdvisorAssigned()) {
            imageProgressBar.setVisibility(View.VISIBLE);
            helpMessage.setText(getResources().getString(R.string.advisor_help_msg));
            advisorId = ((ConceirgeActivity) getActivity()).getAdvisorId();
            String name = ((ConceirgeActivity) getActivity()).getAdvisorName();
            String imageURL = ((ConceirgeActivity) getActivity()).getAdvisorImageURL();
            phoneNo = ((ConceirgeActivity) getActivity()).getAdvisorPhoneNo();
            advisorRating = ((ConceirgeActivity) getActivity()).getAdvisorRating();
            advisorComments = ((ConceirgeActivity) getActivity()).getAdvisorComments();
            if (!TextUtils.isEmpty(name)) {
                //UtilityMethods.saveStringInPref(getActivity(), Constants.SharedPreferConstants.CONCEIRGE_NAME, name);
                advisorName.setVisibility(View.VISIBLE);
                advisorName.setText(name);
            } else {
                advisorName.setVisibility(View.GONE);
            }

            if(advisorRating != 0){
                rateBar.setRating(advisorRating);
            }else{
                rateBar.setRating(0);
            }
            if(!TextUtils.isEmpty(advisorComments)){
                comments.setText(advisorComments);
            }else{
                comments.setText("");
            }
            UtilityMethods.saveFloatInPref(getActivity(),Constants.SharedPreferConstants.RM_RATING,advisorRating);
            if (imageURL != null) {
                //UtilityMethods.saveStringInPref(getActivity(), Constants.SharedPreferConstants.CONCEIRGE_PHOTO_URL, imageURL);
                Picasso.get().load(imageURL)
                        .into(advisorPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                imageProgressBar.setVisibility(View.GONE);
                                defaultImage.setVisibility(View.GONE);
                                advisorPhoto.setVisibility(View.VISIBLE);
                                blurImageView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                imageProgressBar.setVisibility(View.GONE);
                                advisorPhoto.setVisibility(View.GONE);
                                blurImageView.setVisibility(View.GONE);
                                defaultImage.setVisibility(View.VISIBLE);
                            }
                        });
                new PropertyAdvisorFragment.GetBitmapImage(imageURL).execute();
            } else {
                imageProgressBar.setVisibility(View.GONE);
                defaultImage.setVisibility(View.VISIBLE);
                advisorPhoto.setVisibility(View.GONE);
                blurImageView.setVisibility(View.GONE);
            }

        }else{
            imageProgressBar.setVisibility(View.GONE);
            defaultImage.setVisibility(View.VISIBLE);
            advisorPhoto.setVisibility(View.GONE);
            blurImageView.setVisibility(View.GONE);
            advisorName.setVisibility(View.GONE);
            rateBar.setVisibility(View.GONE);
            rateMsg.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
            comments.setVisibility(View.GONE);
            helpMessage.setText(getResources().getString(R.string.no_advisor_assigned_msg));
        }
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
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap getBlurImageBitmap(Bitmap image) {
        try {
            if(image != null && getActivity() != null) {
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
        if(advisorRating == rating && advisorComments != null && advisorComments.equalsIgnoreCase(comments.getText().toString().trim())){
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
                    UserEndPoint.RateAgent rateAgent = mUserEndPoint.rateAgent(userId,advisorId,rating);
                    if(!TextUtils.isEmpty(comments.getText().toString().trim())) {
                        rateAgent.setComments(comments.getText().toString().trim());
                    }
                    rateAgent.setRating(rateBar.getRating());
                    CommonResponse rateAgentResponse = rateAgent.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if(rateAgentResponse != null && rateAgentResponse.getStatus()){
                        new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.PROPERTY_ADVISOR,
                                null, Constants.ApiName.rateAgent.toString(),Constants.AnalyticsEvents.SUCCESS,
                                null);
                        advisorRating = rateBar.getRating();
                        advisorComments = comments.getText().toString();
                        succussMsg = "Thanks for your valuable feedback!";
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
                            submitButton.setVisibility(View.GONE);
                            UtilityMethods.showSnackBar(mParentLayout,succussMsg,Snackbar.LENGTH_LONG);
                        }
                    });
                } else if(!TextUtils.isEmpty(errorMsg)){
                    new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.PROPERTY_ADVISOR,
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
                if(blurBitmap != null) {
                    blurImageView.setImageBitmap(blurBitmap);
                }else{
                    blurImageView.setImageResource(R.drawable.splash);
                }
            }else{
                blurImageView.setImageResource(R.drawable.splash);
            }
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
        TrackAnalytics.trackEvent("PropertyAdvisor", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), getActivity());
    }*/
}
