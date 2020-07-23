package com.clicbrics.consumer.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.animators.FadeOutAnimation;
import com.clicbrics.consumer.customview.CustomVideoView;
import com.clicbrics.consumer.customview.VideoMetaData;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class DriveViewActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DriveViewActivity";
    private List<VideoMetaData> metaDataList = new ArrayList<>();
    private TextView txtETA, txtDistanceLeft, txtSpeed, txtLocation, txtUpcomingAreaDist, txtUpcomingArea, txtUpcomingDistUnit, txtDistanceLeftLabel;
    private ImageView imgArrow;
    private LinearLayout layoutInfoOverlay, layoutMapSwitchLayout;
    private long videoDuration = 0;
    private final int REPEAT_TASK_INTERVAL = 1000;  //in miliseconds
    private MediaController mediaController;
    private static final int LOCATION_SETTING_CHANGE_CODE = 1000;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final int VIDEO_VIEW = 1;
    private final int MAP_VIEW = 2;
    int currentViewIndex = 1;
    float zoomLevel = 18.0f;

    private enum ArrowDir {
        left, right, straight
    }

    private MapView mapView;

    //Map View
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FrameLayout mapLayout;
    private CustomVideoView videoView;
    private List<LatLng> latLngList = new ArrayList<>();
    private FrameLayout videoLayout;
    private final int ANIMATION_DURATION = 800;
    private Handler handler;
    private int smallScreenWidth, smallScreenHeight, screenWidth, screenHeight, statusBarHight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_view);
        smallScreenWidth = (int) (getResources().getDimension(R.dimen.margin_80));
        smallScreenHeight = (int) (getResources().getDimension(R.dimen.margin_50));
        screenWidth = UtilityMethods.getScreenWidth(this);
        screenHeight = UtilityMethods.getScreenHeight(this);
        statusBarHight = UtilityMethods.getStatusBarHeight(this);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        handler = new Handler();
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(getString(R.string.google_api_key));
        }
        mapView = findViewById(R.id.id_drive_map_view);
        mapView.onCreate(mapViewBundle);
        //buildGoogleApiClient();
        //mapView.getMapAsync(this);

        initView();
        //showVideo();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(getString(R.string.google_api_key));
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(getString(R.string.google_api_key), mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    private void initView() {
        videoLayout = findViewById(R.id.id_video_layout);
        videoView = findViewById(R.id.id_video_view);
        layoutInfoOverlay = findViewById(R.id.id_info_overlay);
        txtETA = findViewById(R.id.id_eta);
        txtDistanceLeft = findViewById(R.id.id_km_left);
        txtDistanceLeftLabel = findViewById(R.id.id_km_left_txt);
        txtSpeed = findViewById(R.id.id_speed);
        txtLocation = findViewById(R.id.id_location_name);
        txtUpcomingAreaDist = findViewById(R.id.id_upcoming_area_distance);
        txtUpcomingArea = findViewById(R.id.id_upcoming_area_name);
        imgArrow = findViewById(R.id.id_arrow_icon);
        txtUpcomingDistUnit = findViewById(R.id.id_upcoming_area_dist_unit);
        layoutMapSwitchLayout = findViewById(R.id.id_map_switch_btn_layout);
        mapLayout = findViewById(R.id.id_map_layout);

        /*final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoLayout.getLayoutParams();
        layoutParams.width = UtilityMethods.getScreenWidth(this);
        layoutParams.height = UtilityMethods.getScreenHeight(this) + 60;
        videoLayout.setLayoutParams(layoutParams);*/

        //handleScreenSize(VIDEO_VIEW);
        getVideoDataInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //createLocationRequest();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    int progressValue = 0;
    MediaMetadataRetriever metadataRetriever;
    int currentDuration = 0;
    int duration = 0;
    int current = 0;
    boolean isVideoStarted = false;

    private void showVideo() {
        if (!UtilityMethods.isInternetConnected(this)) {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_LONG);
            return;
        }

        //String videoURL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        final String videoURL = "https://storage.googleapis.com/housingtestserver.appspot.com/" + "sobha-city-drive-view-new.mp4";
        Uri uri = Uri.parse(videoURL);
        videoView.setVideoURI(uri);
        videoView.start();
        showProgressBar();
        /*metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(videoURL);*/
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                //close the progress dialog when buffering is done
                //handleScreenSize(VIDEO_VIEW);
                //mediaController.show(0);
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                        Log.i(TAG, "***** onError called ****** " + what + "," + extra);
                        if (!UtilityMethods.isInternetConnected(DriveViewActivity.this)) {
                            ///showNetworkErrorSnackBar();
                            UtilityMethods.showErrorSnackbarOnTop(DriveViewActivity.this);
                        }
                        return false;
                    }
                });

                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                        Log.i(TAG, "onInfo: " + "i=" + what + " i1=" + extra);
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            dismissProgressBar();
                            isVideoStarted = true;
                            if(metaDataList != null && !metaDataList.isEmpty()) {
                                //showOverlayContent();
                                initAnimationIconView();
                                showOverlayInfo();
                                duration = videoView.getDuration();
                                Log.i(TAG, "onPrepared: Total length of video " + duration);
                            }
                            return true;
                        }
                        return false;
                    }
                });

                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                        /*if((percent >= 5 && !isVideoStarted)){
                            dismissProgressBar();
                            isVideoStarted = true;
                            if(metaDataList != null && !metaDataList.isEmpty()) {
                                //showOverlayContent();
                                initAnimationIconView();
                                showOverlayInfo();
                                duration = videoView.getDuration();
                                Log.i(TAG, "onPrepared: Total length of video " + duration);
                            }
                        }*/
                        Log.i(TAG, "***** onBufferingUpdate called ********** " + percent);
                    }
                });
                videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
                    @Override
                    public void onPlay() {
                        /*if (metaDataList != null && !metaDataList.isEmpty()) {
                            showOverlayContent();
                        }*/
                    }

                    @Override
                    public void onPause() {
                        //resetTimer();
                        //handler.removeCallbacks(overlayInfoRunnable);
                    }
                });
            }
        });


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                count = 0;
                //resetTimer();
                handler.removeCallbacks(overlayInfoRunnable);
                handleInfoOverlay(View.GONE);
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isVideoStarted) {
                    if (handler != null) {
                        dismissProgressBar();
                        handler.removeCallbacksAndMessages(null);
                        UtilityMethods.showErrorSnackbarOnTop(DriveViewActivity.this);
                    }
                }
            }
        },1000*60);

    }

    private void hideMediaController() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaController.hide();
            }
        }, 3000);
    }

    private void resetTimer() {
        /*timer.cancel();
        timer.purge();
        timer = null;*/
    }

    Timer timer = new Timer();
    int count = 0;
    boolean isOverLayInfoVisible = false;
    /*private void showOverlayContent(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Log.i(TAG, "run: " + metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE));

                        if(metaDataList.size() > count){
                            VideoMetaData metaData = metaDataList.get(count);
                            if(metaData != null) {
                                if(!isOverLayInfoVisible){
                                    drawPolyLineAndAnimateCar();
                                    mapLayout.setVisibility(View.VISIBLE);
                                    layoutInfoOverlay.setVisibility(View.VISIBLE);
                                    videoLayout.setVisibility(View.VISIBLE);
                                    isOverLayInfoVisible = true;
                                }
                                if(metaData.getDistanceLeft() > 1000){
                                    txtDistanceLeftLabel.setText("KM Left");
                                    txtDistanceLeft.setText(String.format("%.2f", (metaData.getDistanceLeft()/1000)));
                                }else{
                                    txtDistanceLeftLabel.setText("Mtr Left");
                                    txtUpcomingAreaDist.setText(Math.round(metaData.getDistanceLeft())+"");
                                }
                                if(metaData.getEtaTime()==0){
                                    handleInfoOverlay(View.GONE);
                                    mGoogleMap.setOnMapClickListener(null);
                                    videoLayout.setOnClickListener(null);
                                }else {
                                    txtETA.setText(String.format("%.1f", metaData.getEtaTime()) + "");
                                }
                                txtSpeed.setText(Math.round(metaData.getSpeed())+"");
                                if(!TextUtils.isEmpty(metaData.getLocation())) {
                                    txtLocation.setVisibility(View.VISIBLE);
                                    txtLocation.setText(metaData.getLocation());
                                }else{
                                    txtLocation.setVisibility(View.INVISIBLE);
                                }
                                if(!TextUtils.isEmpty(metaData.getUpcomingArea())){
                                    txtUpcomingArea.setVisibility(View.VISIBLE);
                                    txtUpcomingArea.setText(metaData.getUpcomingArea());
                                }else{
                                    txtUpcomingArea.setVisibility(View.INVISIBLE);
                                    txtUpcomingDistUnit.setVisibility(View.INVISIBLE);
                                    txtUpcomingAreaDist.setVisibility(View.INVISIBLE);
                                    imgArrow.setVisibility(View.INVISIBLE);
                                }
                                if(txtUpcomingArea.getVisibility() == View.VISIBLE && metaData.getUpcomingAreaDistance() != 0) {
                                    txtUpcomingDistUnit.setVisibility(View.VISIBLE);
                                    txtUpcomingAreaDist.setVisibility(View.VISIBLE);
                                    if(!TextUtils.isEmpty(metaData.getArrowDir())){
                                        imgArrow.setVisibility(View.VISIBLE);
                                        if(metaData.getArrowDir().equalsIgnoreCase(ArrowDir.left.toString())){
                                            imgArrow.setImageResource(R.drawable.ic_left_arrow);
                                        }else if(metaData.getArrowDir().equalsIgnoreCase(ArrowDir.right.toString())){
                                            imgArrow.setImageResource(R.drawable.ic_right_arrow);
                                        }else if(metaData.getArrowDir().equalsIgnoreCase(ArrowDir.straight.toString())){
                                            imgArrow.setImageResource(R.drawable.ic_straight_arrow);
                                        }
                                    }
                                    if(metaData.getUpcomingAreaDistance() > 1000){
                                        txtUpcomingDistUnit.setText("KM");
                                        txtUpcomingAreaDist.setText(String.format("%.2f", (metaData.getUpcomingAreaDistance()/1000)));
                                    }else{
                                        txtUpcomingDistUnit.setText("Mtr");
                                        txtUpcomingAreaDist.setText(Math.round(metaData.getUpcomingAreaDistance())+"");
                                    }
                                }
                            }else{
                                isOverLayInfoVisible = false;
                                handleInfoOverlay(View.GONE);
                                mGoogleMap.setOnMapClickListener(null);
                                videoLayout.setOnClickListener(null);
                            }
                            count++;
                        }
                    }
                });
            }
        };
        if(timer == null){
            timer = new Timer();
        }
        timer.schedule(timerTask,0,REPEAT_TASK_INTERVAL);
    }*/

    private long previousDuration = -1;
    private int latLngIndex = 0;
    private int emptyRowIndex = 0;
    private void showOverlayInfo(){
        final long videoTotalDuration = TimeUnit.MILLISECONDS.toSeconds(videoView.getDuration());
        overlayInfoRunnable = new Runnable() {
            @Override
            public void run() {
                long videoCurrentDuration = TimeUnit.MILLISECONDS.toSeconds(videoView.getCurrentPosition());
                Log.i(TAG, "Video current duration " + videoCurrentDuration);
                count = (int)videoCurrentDuration;
                if(videoCurrentDuration < videoTotalDuration && metaDataList.size() > count){
                    if(previousDuration != videoCurrentDuration){
                        previousDuration = videoCurrentDuration;
                        VideoMetaData metaData = metaDataList.get(count);
                        Log.i(TAG, "MetaData " + metaData);
                        if(metaData != null) {
                            latLngIndex = (int)count - emptyRowIndex;
                            Log.i(TAG, "LatLng Index " + latLngIndex);
                            animateCarIcon();
                            if(!isOverLayInfoVisible){
                                Log.i(TAG, "#### Overlay content " + isOverLayInfoVisible);
                                //drawPolyLineAndAnimateCar();
                                mapLayout.setVisibility(View.VISIBLE);
                                layoutInfoOverlay.setVisibility(View.VISIBLE);
                                findViewById(R.id.id_pip_border_view).setVisibility(View.VISIBLE);
                                videoLayout.setVisibility(View.VISIBLE);
                                isOverLayInfoVisible = true;
                            }
                            if(metaData.getDistanceLeft() > 1000){
                                txtDistanceLeftLabel.setText("KM Left");
                                txtDistanceLeft.setText(String.format("%.2f", (metaData.getDistanceLeft()/1000)));
                            }else{
                                txtDistanceLeftLabel.setText("Mtr Left");
                                txtUpcomingAreaDist.setText(Math.round(metaData.getDistanceLeft())+"");
                            }
                            if(metaData.getEtaTime()==0){
                                handleInfoOverlay(View.GONE);
                                mGoogleMap.setOnMapClickListener(null);
                                videoLayout.setOnClickListener(null);
                            }else {
                                txtETA.setText(String.format("%.1f", metaData.getEtaTime()) + "");
                            }
                            txtSpeed.setText(Math.round(metaData.getSpeed())+"");
                            if(!TextUtils.isEmpty(metaData.getLocation())) {
                                txtLocation.setVisibility(View.VISIBLE);
                                txtLocation.setText(metaData.getLocation());
                            }else{
                                txtLocation.setVisibility(View.INVISIBLE);
                            }
                            if(!TextUtils.isEmpty(metaData.getUpcomingArea())){
                                txtUpcomingArea.setVisibility(View.VISIBLE);
                                txtUpcomingArea.setText(metaData.getUpcomingArea());
                            }else{
                                txtUpcomingArea.setVisibility(View.INVISIBLE);
                                txtUpcomingDistUnit.setVisibility(View.INVISIBLE);
                                txtUpcomingAreaDist.setVisibility(View.INVISIBLE);
                                imgArrow.setVisibility(View.INVISIBLE);
                            }
                            if(txtUpcomingArea.getVisibility() == View.VISIBLE && metaData.getUpcomingAreaDistance() != 0) {
                                txtUpcomingDistUnit.setVisibility(View.VISIBLE);
                                txtUpcomingAreaDist.setVisibility(View.VISIBLE);
                                if(!TextUtils.isEmpty(metaData.getArrowDir())){
                                    imgArrow.setVisibility(View.VISIBLE);
                                    if(metaData.getArrowDir().equalsIgnoreCase(ArrowDir.left.toString())){
                                        imgArrow.setImageResource(R.drawable.ic_left_arrow);
                                    }else if(metaData.getArrowDir().equalsIgnoreCase(ArrowDir.right.toString())){
                                        imgArrow.setImageResource(R.drawable.ic_right_arrow);
                                    }else if(metaData.getArrowDir().equalsIgnoreCase(ArrowDir.straight.toString())){
                                        imgArrow.setImageResource(R.drawable.ic_straight_arrow);
                                    }
                                }
                                if(metaData.getUpcomingAreaDistance() > 1000){
                                    txtUpcomingDistUnit.setText("KM");
                                    txtUpcomingAreaDist.setText(String.format("%.2f", (metaData.getUpcomingAreaDistance()/1000)));
                                }else{
                                    txtUpcomingDistUnit.setText("Mtr");
                                    txtUpcomingAreaDist.setText(Math.round(metaData.getUpcomingAreaDistance())+"");
                                }
                            }
                        }else{
                            emptyRowIndex++;
                            Log.i(TAG, "$$$$$$ Overlay content " + isOverLayInfoVisible);
                        /*isOverLayInfoVisible = false;
                        handleInfoOverlay(View.GONE);
                        mGoogleMap.setOnMapClickListener(null);
                        videoLayout.setOnClickListener(null);*/
                        }
                    }
                    handler.postDelayed(this,1000);
                }else {
                    handler.removeCallbacks(overlayInfoRunnable);
                }
            }
        };
        handler.postDelayed(overlayInfoRunnable,1000);
    }

    private Runnable overlayInfoRunnable;
    private boolean isAnitmationViewInit = false;
    private void showOverlayContent() {
        if(!isAnitmationViewInit){
            isAnitmationViewInit = true;
            initAnimationIconView();
        }
        overlayInfoRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Duration " + count);
                if (metaDataList.size() > count) {
                    VideoMetaData metaData = metaDataList.get(count);
                    if (metaData != null) {
                        animateCarIcon();
                        if (!isOverLayInfoVisible) {
                            Log.i(TAG, "#### Overlay content " + isOverLayInfoVisible);
                            //drawPolyLineAndAnimateCar();
                            mapLayout.setVisibility(View.VISIBLE);
                            layoutInfoOverlay.setVisibility(View.VISIBLE);
                            findViewById(R.id.id_pip_border_view).setVisibility(View.VISIBLE);
                            videoLayout.setVisibility(View.VISIBLE);
                            isOverLayInfoVisible = true;
                        }
                        if (metaData.getDistanceLeft() > 1000) {
                            txtDistanceLeftLabel.setText("KM Left");
                            txtDistanceLeft.setText(String.format("%.2f", (metaData.getDistanceLeft() / 1000)));
                        } else {
                            txtDistanceLeftLabel.setText("Mtr Left");
                            txtUpcomingAreaDist.setText(Math.round(metaData.getDistanceLeft()) + "");
                        }
                        if (metaData.getEtaTime() == 0) {
                            Log.i(TAG, "!!!!! Overlay content eta time is 0 or less than 0");
                            handleInfoOverlay(View.GONE);
                            mGoogleMap.setOnMapClickListener(null);
                            videoLayout.setOnClickListener(null);
                        } else {
                            txtETA.setText(String.format("%.1f", metaData.getEtaTime()) + "");
                        }
                        txtSpeed.setText(Math.round(metaData.getSpeed()) + "");
                        if (!TextUtils.isEmpty(metaData.getLocation())) {
                            txtLocation.setVisibility(View.VISIBLE);
                            txtLocation.setText(metaData.getLocation());
                        } else {
                            txtLocation.setVisibility(View.INVISIBLE);
                        }
                        if (!TextUtils.isEmpty(metaData.getUpcomingArea())) {
                            txtUpcomingArea.setVisibility(View.VISIBLE);
                            txtUpcomingArea.setText(metaData.getUpcomingArea());
                        } else {
                            txtUpcomingArea.setVisibility(View.INVISIBLE);
                            txtUpcomingDistUnit.setVisibility(View.INVISIBLE);
                            txtUpcomingAreaDist.setVisibility(View.INVISIBLE);
                            imgArrow.setVisibility(View.INVISIBLE);
                        }
                        if (txtUpcomingArea.getVisibility() == View.VISIBLE && metaData.getUpcomingAreaDistance() != 0) {
                            txtUpcomingDistUnit.setVisibility(View.VISIBLE);
                            txtUpcomingAreaDist.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(metaData.getArrowDir())) {
                                imgArrow.setVisibility(View.VISIBLE);
                                if (metaData.getArrowDir().equalsIgnoreCase(ArrowDir.left.toString())) {
                                    imgArrow.setImageResource(R.drawable.ic_left_arrow);
                                } else if (metaData.getArrowDir().equalsIgnoreCase(ArrowDir.right.toString())) {
                                    imgArrow.setImageResource(R.drawable.ic_right_arrow);
                                } else if (metaData.getArrowDir().equalsIgnoreCase(ArrowDir.straight.toString())) {
                                    imgArrow.setImageResource(R.drawable.ic_straight_arrow);
                                }
                            }
                            if (metaData.getUpcomingAreaDistance() > 1000) {
                                txtUpcomingDistUnit.setText("KM");
                                txtUpcomingAreaDist.setText(String.format("%.2f", (metaData.getUpcomingAreaDistance() / 1000)));
                            } else {
                                txtUpcomingDistUnit.setText("Mtr");
                                txtUpcomingAreaDist.setText(Math.round(metaData.getUpcomingAreaDistance()) + "");
                            }
                        }
                    } else {
                        Log.i(TAG, "$$$$$$ Overlay content " + isOverLayInfoVisible);
                        isOverLayInfoVisible = false;
                        handleInfoOverlay(View.GONE);
                        mGoogleMap.setOnMapClickListener(null);
                        videoLayout.setOnClickListener(null);
                    }
                    count++;
                    handler.postDelayed(this, 1000);
                }else{
                    handler.removeCallbacks(overlayInfoRunnable);
                }
            }
        };
        handler.postDelayed(overlayInfoRunnable, 1000);
    }

    private void getVideoDataInfo() {
        if(!UtilityMethods.isInternetConnected(this)){
            UtilityMethods.showErrorSnackbarOnTop(this);
            return;
        }
        String filePath = "https://storage.googleapis.com/housingtestserver.appspot.com/" + "sobha-city-location-new-file.csv";
        //String filePath = "https://storage.googleapis.com/housingtestserver.appspot.com/" + "sobha-city-location.csv";
        new DownloadInfoFile().execute(filePath);
    }

    private boolean isMetaDataNull(VideoMetaData metaData) {
        if (metaData.getArrowDir() == null && metaData.getDistanceLeft() == 0
                && metaData.getDuration() == 0 && metaData.getEtaTime() == 0
                && metaData.getLatitude() == 0 && metaData.getLongitude() == 0
                && metaData.getLocation() == null && metaData.getSpeed() == 0
                && metaData.getUpcomingArea() == null && metaData.getUpcomingAreaDistance() == 0) {
            return true;
        } else {
            return false;
        }
    }


    /*private String convertSecToMin(int seconds){
        int minutes = seconds/60;
        int sec = seconds%60;
        if(sec == 0){
            return (minutes + "." + "00");
        }else{
            return (minutes + "." + sec);
        }
    }*/
    private String convertSecToMin(float minutes) {
        return String.format("%.2f", minutes);
    }

    /*private String covertMeterToKM(int km){
        int kiloMeter = km/1000;
        int meter = km%1000;
        if(meter == 0){
            return (kiloMeter + "." + "0");
        }else{
            return kiloMeter + "." + meter;
        }
    }*/

    private String covertMeterToKM(float mtr) {
        float kiloMeter = mtr / 1000;
        return String.format("%.2f", kiloMeter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (latLngList != null && !latLngList.isEmpty()) {
            LatLng homeLatLng = latLngList.get(0);
            mGoogleMap.addMarker(new MarkerOptions().position(homeLatLng))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drive_view_source));
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(mGoogleMap.getCameraPosition().target)
                    .zoom(zoomLevel)
                    .build()));
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLngList.get(latLngList.size() - 1)))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_sobha_logo));
            //drawPolyLineAn1dAnimateCar();
            showVideo();
            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    switchView();
                    return true;
                }
            });
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    switchView();
                }
            });

            final TextView txtMapView = findViewById(R.id.id_map_view);
            final TextView txtSatelliteView = findViewById(R.id.id_satellite_view);

            txtMapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txtMapView.setEnabled(false);
                    txtSatelliteView.setEnabled(true);
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            });

            txtSatelliteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txtMapView.setEnabled(true);
                    txtSatelliteView.setEnabled(false);
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            });
        }

        /*if(latLngList != null && !latLngList.isEmpty()){
            LatLng homeLatLng = latLngList.get(0);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(homeLatLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
            if(latLngList.size() >= 2){
                mGoogleMap.addMarker(new MarkerOptions().position(latLngList.get(0))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_source)));
                mGoogleMap.addMarker(new MarkerOptions().position(latLngList.get(latLngList.size()-1))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination)));
            }
            mGoogleMap.addPolyline(new PolylineOptions()
                    .addAll(latLngList)
                    .width(10)
                    .color(getResources().getColor(R.color.blue_light))//Google maps blue color
                    .startCap(new SquareCap())
                    .endCap(new SquareCap())
                    .jointType(2)
                    .geodesic(true));

            setAnimation(latLngList);
        }*/

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    class DownloadInfoFile extends AsyncTask<String, Void, List<String[]>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
        }

        @Override
        protected List<String[]> doInBackground(String... path) {
            List<String[]> csvLine = new ArrayList<>();
            String[] content = null;
            try {
                URL url = new URL(path[0]);
                URLConnection urlConnection = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    content = line.split(",");
                    if (content != null) {
                        csvLine.add(content);
                    }
                }
                bufferedReader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return csvLine;
        }

        @Override
        protected void onPostExecute(List<String[]> csvLine) {
            super.onPostExecute(csvLine);
            if (csvLine != null && !csvLine.isEmpty()) {
                Log.i(TAG, "onPostExecute: MetaData Values\n");
                for (int i = 0; i < csvLine.size(); i++) {
                    if (i == 0) {
                        continue;
                    }
                    String[] metaDataArr = csvLine.get(i);
                    if (metaDataArr != null) {
                        VideoMetaData metaData = new VideoMetaData();
                        try {
                            if (metaDataArr.length > 0 && !TextUtils.isEmpty(metaDataArr[0])) {
                                float distanceLeft = Float.parseFloat(metaDataArr[0]);
                                metaData.setDistanceLeft(distanceLeft);
                            }

                            if (metaDataArr.length > 1 && !TextUtils.isEmpty(metaDataArr[1])) {
                                float speed = Integer.parseInt(metaDataArr[1]);
                                metaData.setSpeed(speed);
                            }

                            if (metaDataArr.length > 2 && !TextUtils.isEmpty(metaDataArr[2])) {
                                String location = metaDataArr[2];
                                metaData.setLocation(location);
                            }

                            if (metaDataArr.length > 3 && !TextUtils.isEmpty(metaDataArr[3])) {
                                String upcomingLoc = metaDataArr[3];
                                metaData.setUpcomingArea(upcomingLoc);
                            }

                            if (metaDataArr.length > 4 && !TextUtils.isEmpty(metaDataArr[4])) {
                                float upcomingLocDistance = Float.parseFloat(metaDataArr[4]);
                                metaData.setUpcomingAreaDistance(upcomingLocDistance);
                            }

                            if (metaDataArr.length > 5 && !TextUtils.isEmpty(metaDataArr[5])) {
                                double latitude = Double.parseDouble(metaDataArr[5]);
                                metaData.setLatitude(latitude);
                            }

                            if (metaDataArr.length > 6 && !TextUtils.isEmpty(metaDataArr[6])) {
                                double longitude = Double.parseDouble(metaDataArr[6]);
                                metaData.setLongitude(longitude);
                            }

                            if (metaDataArr.length > 6 && !TextUtils.isEmpty(metaDataArr[5]) && !TextUtils.isEmpty(metaDataArr[6])) {
                                double latitude = Double.parseDouble(metaDataArr[5]);
                                double longitude = Double.parseDouble(metaDataArr[6]);
                                latLngList.add(new LatLng(latitude, longitude));
                            }

                            if (metaDataArr.length > 7 && !TextUtils.isEmpty(metaDataArr[7])) {
                                String arrowDirection = metaDataArr[7];
                                metaData.setArrowDir(arrowDirection);
                            }

                            if (metaDataArr.length > 8 && !TextUtils.isEmpty(metaDataArr[8])) {
                                float etaTime = Float.parseFloat(metaDataArr[8]);
                                metaData.setEtaTime(etaTime);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            continue;
                        }
                        Log.i(TAG, "" + metaData);
                        if (isMetaDataNull(metaData)) {
                            metaDataList.add(null);
                        } else {
                            metaDataList.add(metaData);
                        }
                    }
                }
            }
            dismissProgressBar();
            mapView.getMapAsync(DriveViewActivity.this);
            //buildGoogleApiClient();
        }
    }

    private void handleInfoOverlay(int visibility) {
        mapLayout.setVisibility(visibility);
        layoutInfoOverlay.setVisibility(visibility);
        findViewById(R.id.id_pip_border_view).setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            //showOverlayContent();
            showOverlayInfo();
        } else {
            if (currentViewIndex == MAP_VIEW) {
                switchView();
            }
        }
    }

    private void handleScreenSize(final int viewIndex) {

        final int smallScreenWidth = (int) (getResources().getDimension(R.dimen.margin_80));
        final int smallScreenHeight = (int) (getResources().getDimension(R.dimen.margin_50));

        if (viewIndex == VIDEO_VIEW) {
            zoomLevel = 12.0f;
            final FrameLayout.LayoutParams smallScreenLytParam = (FrameLayout.LayoutParams) mapLayout.getLayoutParams();
            smallScreenLytParam.width = smallScreenWidth;
            smallScreenLytParam.height = smallScreenHeight;

            final FrameLayout.LayoutParams fullScreenLytParam = (FrameLayout.LayoutParams) videoLayout.getLayoutParams();
            fullScreenLytParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            fullScreenLytParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            videoLayout.setLayoutParams(fullScreenLytParam);
            mapLayout.setLayoutParams(smallScreenLytParam);
            smallScreenLytParam.gravity = Gravity.BOTTOM;
        } else if (viewIndex == MAP_VIEW) {
            final FrameLayout.LayoutParams fullScreenLytParam = (FrameLayout.LayoutParams) mapLayout.getLayoutParams();
            fullScreenLytParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            fullScreenLytParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            ;

            final FrameLayout.LayoutParams smallScreenLytParam = (FrameLayout.LayoutParams) videoLayout.getLayoutParams();
            smallScreenLytParam.width = smallScreenWidth;
            smallScreenLytParam.height = smallScreenHeight;
            mapLayout.setLayoutParams(fullScreenLytParam);
            videoLayout.setLayoutParams(smallScreenLytParam);
            smallScreenLytParam.gravity = Gravity.BOTTOM;
        }
        currentViewIndex = viewIndex;
    }

    int indexCounter = 0;

    private void setAnimation(final List<LatLng> directionPoint) {

        final Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car))
                .position(directionPoint.get(0))
                .anchor(0.5f, 0.5f)
                .flat(true));

        //myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(directionPoint.get(0), zoomLevel));
        animateMarker(marker, directionPoint, false);
        /*final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(indexCounter+1 < directionPoint.size()){
                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(directionPoint.get(indexCounter+1).latitude);
                            location.setLatitude(directionPoint.get(indexCounter+1).longitude);
                            indexCounter++;
                            animateMarkerNew(location, marker);
                        }else{
                            timer.cancel();
                            timer.purge();
                        }
                    }
                });
            }
        },0,3000);*/
    }

    private void animateMarker(final Marker marker, final List<LatLng> directionPoint,
                               final boolean hideMarker) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (i < directionPoint.size()) {
                            float bearing = 0;
                            if ((i + 1) < directionPoint.size()) {
                                bearing = (float) bearingBetweenLocations(directionPoint.get(i), directionPoint.get(i + 1));
                                rotateMarker(marker, bearing - 90);
                                marker.setPosition(directionPoint.get(i));
                                CameraPosition currentPlace = new CameraPosition.Builder()
                                        .target(directionPoint.get(i))
                                        .bearing(bearing).zoom(zoomLevel).build();
                                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
                            }
                        } else {
                            marker.setVisible(false);
                            timer.cancel();
                            timer.purge();
                        }
                        i++;
                    }
                });
            }
        }, 0, 1000);

        /*for(int i=0; i<directionPoint.size(); i++){
            if((i+1) < directionPoint.size()) {
                float bearing = (float) bearingBetweenLocations(directionPoint.get(i), directionPoint.get(i + 1));
                rotateMarker(marker, bearing - 90);

                                *//*marker.setPosition(directionPoint.get(i));
                                CameraPosition currentPlace = new CameraPosition.Builder()
                                        .target(directionPoint.get(i))
                                        .bearing(bearing).zoom(zoomLevel).build();
                                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));*//*

                final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(1000); // duration 3 second
                valueAnimator.setInterpolator(new LinearInterpolator());
                final int finalI = i;
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        try {
                            float v = animation.getAnimatedFraction();
                            LatLng newPosition = latLngInterpolator.interpolate(v, directionPoint.get(finalI), directionPoint.get(finalI +1));
                            marker.setPosition(newPosition);
                                            *//*mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                    .target(newPosition)
                                                    .zoom(15.5f)
                                                    .build()));

                                            marker.setRotation(getBearing(startPosition, new LatLng(destination.getLatitude(), destination.getLongitude())));*//*
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
                valueAnimator.start();
            }
        }*/
    }

    /*private void animateMarker(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                                      final boolean hideMarker) {
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                if (i < directionPoint.size())
                    marker.setPosition(directionPoint.get(i));
                i++;


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }*/

    private void switchView() {
        layoutInfoOverlay.setVisibility(View.GONE);
        layoutMapSwitchLayout.setVisibility(View.GONE);
        findViewById(R.id.id_pip_border_view).setVisibility(View.GONE);
        if (currentViewIndex == VIDEO_VIEW) {
            currentViewIndex = MAP_VIEW;
            zoomLevel = 18.0f;
            //mapLayout.measure(0,0);

            final FrameLayout.LayoutParams smallScreenLytParam = (FrameLayout.LayoutParams) videoLayout.getLayoutParams();

            //mapLayout.setLayoutParams(fullScreenLytParam);
            //videoLayout.setLayoutParams(smallScreenLytParam);
            smallScreenLytParam.gravity = Gravity.BOTTOM;
            videoLayout.bringToFront();

            IncreaseSizeAnimation IncreaseSizeAnimation = new IncreaseSizeAnimation(mapLayout, smallScreenWidth, screenWidth,
                    smallScreenHeight, screenHeight);
            IncreaseSizeAnimation.setDuration(ANIMATION_DURATION);
            mapLayout.startAnimation(IncreaseSizeAnimation);

            DecreaseSizeAnimation decreaseSizeAnimation = new DecreaseSizeAnimation(videoLayout, screenWidth, smallScreenWidth,
                    screenHeight, smallScreenHeight);
            decreaseSizeAnimation.setDuration(ANIMATION_DURATION);
            videoLayout.startAnimation(decreaseSizeAnimation);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FadeOutAnimation fadeOutAnimation = new FadeOutAnimation(layoutInfoOverlay);
                    fadeOutAnimation.setDuration(250);
                    fadeOutAnimation.animate();
                    layoutInfoOverlay.bringToFront();

                    FadeOutAnimation fadeOutAnimation2 = new FadeOutAnimation(layoutMapSwitchLayout);
                    fadeOutAnimation2.setDuration(250);
                    fadeOutAnimation2.animate();
                    layoutMapSwitchLayout.bringToFront();
                    findViewById(R.id.id_pip_border_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.id_pip_border_view).bringToFront();
                }
            }, ANIMATION_DURATION);

            mGoogleMap.setOnMapClickListener(null);
            videoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchView();
                }
            });
            UtilityMethods.setColorBackground(this, layoutInfoOverlay, R.color.overlay_color5);

        } else if (currentViewIndex == MAP_VIEW) {
            zoomLevel = 12.0f;
            currentViewIndex = VIDEO_VIEW;

            final FrameLayout.LayoutParams smallScreenLytParam = (FrameLayout.LayoutParams) mapLayout.getLayoutParams();

            //videoLayout.setLayoutParams(fullScreenLytParam);
            //mapLayout.setLayoutParams(smallScreenLytParam);
            smallScreenLytParam.gravity = Gravity.BOTTOM;
            mapLayout.bringToFront();

            IncreaseSizeAnimation IncreaseSizeAnimation = new IncreaseSizeAnimation(videoLayout, smallScreenWidth, screenWidth,
                    smallScreenHeight, screenHeight);
            IncreaseSizeAnimation.setDuration(ANIMATION_DURATION);
            videoLayout.startAnimation(IncreaseSizeAnimation);

            DecreaseSizeAnimation decreaseSizeAnimation = new DecreaseSizeAnimation(mapLayout, screenWidth, smallScreenWidth,
                    screenHeight, smallScreenHeight);
            decreaseSizeAnimation.setDuration(ANIMATION_DURATION);
            mapLayout.startAnimation(decreaseSizeAnimation);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FadeOutAnimation fadeOutAnimation = new FadeOutAnimation(layoutInfoOverlay);
                    fadeOutAnimation.setDuration(250);
                    fadeOutAnimation.animate();
                    layoutInfoOverlay.bringToFront();
                    findViewById(R.id.id_pip_border_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.id_pip_border_view).bringToFront();
                }
            }, ANIMATION_DURATION);

            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    switchView();
                }
            });
            videoLayout.setOnClickListener(null);
            UtilityMethods.setColorBackground(this, layoutInfoOverlay, R.color.overlay_color3);
        }
    }

    public class IncreaseSizeAnimation extends Animation {

        private int startWidth;
        private int deltaWidth; // distance between start and end height
        private int startHeight;
        private int deltaHeight;

        private int originalWidth;
        private int originalHeight;
        private View view;

        private boolean fillEnabled = false;


        public IncreaseSizeAnimation(View v, int startW, int endW, int startH, int endH) {
            view = v;
            startWidth = startW;
            deltaWidth = endW - startW;

            startHeight = startH;
            deltaHeight = endH - startH;

            //originalHeight = v.getHeight();
            //originalWidth = v.getWidth();
            originalWidth = endW;
            originalHeight = endH;
        }

        @Override
        public void setFillEnabled(boolean enabled) {
            fillEnabled = enabled;
            super.setFillEnabled(enabled);
        }


        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime == 1.0 && !fillEnabled) {
                view.getLayoutParams().height = originalHeight;
                view.getLayoutParams().width = originalWidth;
            } else {
                if (deltaHeight != 0)
                    view.getLayoutParams().height = (int) (startHeight + deltaHeight * interpolatedTime);
                if (deltaWidth != 0)
                    view.getLayoutParams().width = (int) (startWidth + deltaWidth * interpolatedTime);
            }
            Log.i(TAG, "applyTransformation: width" + view.getLayoutParams().width);
            Log.i(TAG, "applyTransformation: height" + view.getLayoutParams().height);
            view.requestLayout();
        }
    }

    public class DecreaseSizeAnimation extends Animation {

        private int startWidth;
        private int deltaWidth; // distance between start and end height
        private int startHeight;
        private int deltaHeight;

        private int originalWidth;
        private int originalHeight;
        private View view;

        private boolean fillEnabled = false;


        public DecreaseSizeAnimation(View v, int startW, int endW, int startH, int endH) {
            view = v;
            startWidth = startW;
            deltaWidth = endW - startW;

            startHeight = startH;
            deltaHeight = endH - startH;

            //originalHeight = v.getHeight();
            //originalWidth = v.getWidth();
            originalWidth = endW;
            originalHeight = endH;
        }

        @Override
        public void setFillEnabled(boolean enabled) {
            fillEnabled = enabled;
            super.setFillEnabled(enabled);
        }


        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime == 1.0 && !fillEnabled) {
                view.getLayoutParams().height = originalHeight;
                view.getLayoutParams().width = originalWidth;
            } else {
                if (deltaHeight != 0)
                    view.getLayoutParams().height = (int) (startHeight + deltaHeight * interpolatedTime);
                if (deltaWidth != 0)
                    view.getLayoutParams().width = (int) (startWidth + deltaWidth * interpolatedTime);
            }
            Log.i(TAG, "applyTransformation: width" + view.getLayoutParams().width);
            Log.i(TAG, "applyTransformation: height" + view.getLayoutParams().height);
            view.requestLayout();
        }
    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    boolean isMarkerRotating = false;

    private void rotateMarker(final Marker marker, final float toRotation) {
        if (!isMarkerRotating) {
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;
                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void animateMarkerNew(final Location destination, final Marker marker) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(18.0f)
                                .build()));

                        marker.setRotation(getBearing(startPosition, new LatLng(destination.getLatitude(), destination.getLongitude())));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }

    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private int index, next;
    private LatLng startPosition, endPosition;
    private Marker marker;
    private float v;
    private void initAnimationIconView(){
        PolylineOptions blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.width(16);
        blackPolylineOptions.color(Color.parseColor("#2272FD"));
        final Polyline polyline = mGoogleMap.addPolyline(blackPolylineOptions);

        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(2000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //List<LatLng> points = greyPolyLine.getPoints();
                int percentValue = (int) valueAnimator.getAnimatedValue();
                int size = latLngList.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = latLngList.subList(0, newPoints);
                polyline.setPoints(p);
            }
        });
        polylineAnimator.start();
        marker = mGoogleMap.addMarker(new MarkerOptions().position(latLngList.get(0))
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
        index = -1;
        next = 1;
    }

    private void animateCarIcon(){
        if (latLngIndex < latLngList.size() - 1) {
            next = latLngIndex + 1;
        }
        if (latLngIndex < latLngList.size() - 1) {
            startPosition = latLngList.get(latLngIndex);
            endPosition = latLngList.get(next);
        }
        float bearing = getBearing(startPosition, endPosition);
        marker.setRotation(bearing-90);
        /*final CameraPosition currentPlace = new CameraPosition.Builder()
                .target(endPosition)
                .bearing(bearing).zoom(zoomLevel).build();*/
        //mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
                /*mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (new CameraPosition.Builder().target(endPosition)
                                .zoom(zoomLevel).build()));*/
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float v = valueAnimator.getAnimatedFraction();
                double lng = v * endPosition.longitude + (1 - v)
                        * startPosition.longitude;
                double lat = v * endPosition.latitude + (1 - v)
                        * startPosition.latitude;
                LatLng newPos = new LatLng(lat, lng);
                marker.setPosition(newPos);
                marker.setAnchor(0.5f, 0.5f);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));
                //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                        /*marker.setRotation(getBearing(startPosition, newPos));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                (new CameraPosition.Builder().target(newPos)
                                        .zoom(15.5f).build()));*/
            }
        });
        valueAnimator.start();
    }

    private void showNetworkErrorSnackBar(){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.id_root_view),
                "You lost internet connectivity,please retry.", Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.uber_red));
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white));
        //final Snackbar finalSnackbar = snackbar;
        snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                if(!UtilityMethods.isInternetConnected(DriveViewActivity.this)){
                    showNetworkErrorSnackBar();
                }
                videoView.start();

            }
        });
        snackbar.show();
    }

    private void drawPolyLineAndAnimateCar() {
        //Adjusting bounds
        /*LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        mGoogleMap.animateCamera(mCameraUpdate);*/
        /*CameraPosition currentPlace = new CameraPosition.Builder()
                .target(latLngList.get(1))
                .bearing(getBearing(latLngList.get(0), latLngList.get(1))).zoom(zoomLevel).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));*/

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GRAY);
        polylineOptions.width(5);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(latLngList);
        final Polyline greyPolyLine = mGoogleMap.addPolyline(polylineOptions);

        PolylineOptions blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.width(5);
        blackPolylineOptions.color(Color.BLACK);
        blackPolylineOptions.startCap(new SquareCap());
        blackPolylineOptions.endCap(new SquareCap());
        blackPolylineOptions.jointType(ROUND);
        final Polyline blackPolyline = mGoogleMap.addPolyline(blackPolylineOptions);

        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(2000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                List<LatLng> points = greyPolyLine.getPoints();
                int percentValue = (int) valueAnimator.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                blackPolyline.setPoints(p);
            }
        });
        polylineAnimator.start();
        final Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLngList.get(0))
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
        index = -1;
        next = 1;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < latLngList.size() - 1) {
                    index++;
                    next = index + 1;
                }
                if (index < latLngList.size() - 1) {
                    startPosition = latLngList.get(index);
                    endPosition = latLngList.get(next);
                }
                float bearing = getBearing(startPosition, endPosition);
                marker.setRotation(bearing - 90);
                CameraPosition currentPlace = new CameraPosition.Builder()
                        .target(endPosition)
                        .bearing(bearing).zoom(zoomLevel).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
                /*mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (new CameraPosition.Builder().target(endPosition)
                                .zoom(zoomLevel).build()));*/
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(1000);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float v = valueAnimator.getAnimatedFraction();
                        double lng = v * endPosition.longitude + (1 - v)
                                * startPosition.longitude;
                        double lat = v * endPosition.latitude + (1 - v)
                                * startPosition.latitude;
                        LatLng newPos = new LatLng(lat, lng);
                        marker.setPosition(newPos);
                        marker.setAnchor(0.5f, 0.5f);
                        /*marker.setRotation(getBearing(startPosition, newPos));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                (new CameraPosition.Builder().target(newPos)
                                        .zoom(15.5f).build()));*/
                    }
                });
                valueAnimator.start();
                if (index != latLngList.size() - 1) {
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }
}
