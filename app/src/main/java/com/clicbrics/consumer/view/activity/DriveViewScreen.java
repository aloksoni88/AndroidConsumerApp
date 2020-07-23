package com.clicbrics.consumer.view.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.customview.CustomVideoView;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.utils.UtilityMethods;

public class DriveViewScreen extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_view_screen);

        initView();
    }

    private void initView(){
        try {
            final CustomVideoView videoView = findViewById(R.id.id_video_view);
            if (!UtilityMethods.isInternetConnected(this)) {
                Toast.makeText(this, "No internet connectivity", Toast.LENGTH_LONG);
                return;
            }

            String videoURL = getIntent().getStringExtra("URL");
            //String videoURL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
            //final String videoURL = "https://storage.googleapis.com/housingtestserver.appspot.com/" + "sobha-city-drive-view-new.mp4";
            Uri uri = Uri.parse(videoURL.replaceAll(" ","%20"));
            videoView.setVideoURI(uri);
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.hide();
            videoView.start();
            showProgressBar();

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                    Log.i("DriveViewScreen", "@@@@@ onError @@@@@" + "what=" + what + " extra=" + extra);
                    dismissProgressBar();
                    return false;
                }
            });

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                            Log.i("DriveViewScreen", "onError: " + "what=" + what + " extra=" + extra);
                            if (!UtilityMethods.isInternetConnected(DriveViewScreen.this)) {
                                UtilityMethods.showErrorSnackbarOnTop(DriveViewScreen.this);
                            }
                            return false;
                        }
                    });

                    mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                            Log.i("DriveViewScreen", "onInfo: " + "what=" + what + " extra=" + extra);
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                dismissProgressBar();
                                return true;
                            }
                            return false;
                        }
                    });
                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {

                            return false;
                        }
                    });

                    videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
                        @Override
                        public void onPlay() {

                        }

                        @Override
                        public void onPause() {

                        }
                    });
                }
            });


            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
