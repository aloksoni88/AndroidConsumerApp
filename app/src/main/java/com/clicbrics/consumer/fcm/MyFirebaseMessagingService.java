package com.clicbrics.consumer.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.buy.housing.backend.blogEndPoint.BlogEndPoint;
import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.blogEndPoint.model.BlogResponse;
import com.buy.housing.backend.decorEndPoint.DecorEndPoint;
import com.buy.housing.backend.decorEndPoint.model.Decor;
import com.buy.housing.backend.decorEndPoint.model.DecorResponse;
import com.buy.housing.backend.newsEndPoint.NewsEndPoint;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.BlogListActivity;
import com.clicbrics.consumer.activities.DecorDetailViewActivity;
import com.clicbrics.consumer.activities.NotificationActivity;
import com.clicbrics.consumer.activities.ShareActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.Notificaiton;
import com.google.api.client.http.HttpHeaders;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;


/**
 * Created by Alok on 24-02-2017.
 */

@SuppressWarnings("deprecation")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private final int notificationID = 87987;
    private BlogEndPoint mBlogEndPoint;
    private NewsEndPoint mNewsEndPoint;
    private DecorEndPoint mDecorEndPoint;
    private int newsOrArticlesPosition = 0;
    private int newsOrArticlesListSize = 0;
    private final String newsPrefixURL = "https://www.clicbrics.com/news-and-articles/news-detail/";
    private final String newsPrefixURL2 = "https://www.clicbrics.com/news-and-articles/news?title=";
    private final String blogPrefixURL = "https://www.clicbrics.com/news-and-articles/post/";
    private final String decorPrefixURL = "https://www.clicbrics.com/home-decor-ideas/post/";
    private final String decorPrefixURL2 = "https://www.clicbrics.com/home-decor-ideas/list/?title=";
    private NotificationManager mNotificationManager;
    private String NOTIFICATION_CHANNEL_ID = "com.clicbrics.consumer";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        try {
            if(remoteMessage != null) {
                //Toast.makeText(this,"Sample",Toast.LENGTH_LONG).show();
                TrackAnalytics.trackEvent("FirebaseMessageService", Constants.AppConstants.HOLD_TIME , 1, this);
                String from = remoteMessage.getFrom();
                Map data = remoteMessage.getData();
                Log.i(TAG, "Firebase - onMessageReceived: getForm() " + from);
                Log.i(TAG, "Firebase - onMessageReceived: getData()" + data);
                if (remoteMessage.getData().size() > 0) {
                    Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                    if(remoteMessage.getData().get("messageType").equalsIgnoreCase(Constants.MessageType.PrivateNotification.toString())
                            && UtilityMethods.getLongInPref(this, Constants.ServerConstants.USER_ID,0) == 0){
                        return;
                    }
                    if (remoteMessage.getData().get("messageType").equalsIgnoreCase(Constants.MessageType.Notification.toString())) {
                        final Gson gson = new Gson();
                        final Notificaiton notification = gson.fromJson(remoteMessage.getData().get("notification"), Notificaiton.class);
                        if (notification.getType().equalsIgnoreCase("text")) {
                            sendNotificationList(remoteMessage.getData(), notification);
                        } else if (notification.getType().equalsIgnoreCase("Video")) {

                            try {

                                final String videoUrl = notification.getWelcomeUrl();
                                String videoId = "";
                                if(videoUrl != null && !videoUrl.isEmpty()) {
                                    if (videoUrl.contains("https://www.youtube.com/embed/")) {
                                        videoId = videoUrl.replace("https://www.youtube.com/embed/", "");
                                    } else if (videoUrl.contains("http://www.youtube.com/embed/")) {
                                        videoId = videoUrl.replace("http://www.youtube.com/embed/", "");
                                    } else if (videoUrl.contains("http://www.youtube.com/watch?v=")) {
                                        videoId = videoUrl.replace("http://www.youtube.com/watch?v=", "");
                                    } else if (videoUrl.contains("https://www.youtube.com/watch?v=")) {
                                        videoId = videoUrl.replace("https://www.youtube.com/watch?v=", "");
                                    } else {
                                        if(videoUrl.contains("=")){
                                            videoId = videoUrl.substring(videoUrl.lastIndexOf('=')+1,videoUrl.length());
                                        }
                                    }
                                }
                                //String videoId = url.replace("https://www.youtube.com/embed/", "");
                                String thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";


                                URL imageurl = new URL(thumbnailUrl);
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(remoteMessage.getData(), notification, createImageInImageCenter(bitmap));
                            } catch (Exception ex) {
                                ex.getStackTrace();
                            }
                        } else if (notification.getType().equalsIgnoreCase("Image")) {

                            try {
                                URL imageurl = new URL(notification.getWelcomeUrl());
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(remoteMessage.getData(), notification, bitmap);
                            } catch (Exception ex) {
                                Log.i(TAG, "onMessageReceived: Image Type Notification Exception -> " + ex.getMessage());
                                ex.printStackTrace();
                            }

                        }


                    } else if (remoteMessage.getData().get("messageType").
                            equalsIgnoreCase(Constants.MessageType.PrivateNotification.toString())) {
                        final Gson gson = new Gson();
                        final Notificaiton notification = gson.fromJson(remoteMessage.getData().get("notification"), Notificaiton.class);
                        if (notification.getType() != null && notification.getType().equalsIgnoreCase("text")) {
                            sendNotificationList(remoteMessage.getData(), notification);
                        } else if (notification.getType() != null && notification.getType().equalsIgnoreCase("Video")) {

                            try {

                                final String videoUrl = notification.getUrl();
                                String videoId = "";
                                if(videoUrl != null && !videoUrl.isEmpty()) {
                                    if (videoUrl.contains("https://www.youtube.com/embed/")) {
                                        videoId = videoUrl.replace("https://www.youtube.com/embed/", "");
                                    } else if (videoUrl.contains("http://www.youtube.com/embed/")) {
                                        videoId = videoUrl.replace("http://www.youtube.com/embed/", "");
                                    } else if (videoUrl.contains("http://www.youtube.com/watch?v=")) {
                                        videoId = videoUrl.replace("http://www.youtube.com/watch?v=", "");
                                    } else if (videoUrl.contains("https://www.youtube.com/watch?v=")) {
                                        videoId = videoUrl.replace("https://www.youtube.com/watch?v=", "");
                                    } else {
                                        if(videoUrl.contains("=")){
                                            videoId = videoUrl.substring(videoUrl.lastIndexOf('=')+1,videoUrl.length());
                                        }
                                    }
                                }
                                String thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";


                                URL imageurl = new URL(thumbnailUrl);
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(remoteMessage.getData(), notification, createImageInImageCenter(bitmap));
                            } catch (Exception ex) {
                                ex.getStackTrace();
                                try {
                                    sendNotificationImage(remoteMessage.getData(), notification,
                                            BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (notification.getType() != null && notification.getType().equalsIgnoreCase("Image")) {

                            try {
                                URL imageurl = new URL(notification.getUrl());
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(remoteMessage.getData(), notification, bitmap);
                            } catch (Exception ex) {
                                try {
                                    sendNotificationImage(remoteMessage.getData(), notification,
                                            BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }else if(remoteMessage.getData().get("messageType").equalsIgnoreCase(Constants.MessageType.News.toString())){
                        Log.i(TAG, "onMessageReceived: Notification Type News " + remoteMessage.getData().toString());
                        final Gson gson = new Gson();
                        final Notificaiton notification = gson.fromJson(remoteMessage.getData().get("notification"), Notificaiton.class);
                        buildNewsEndPoint();
                        //getNewsPosition(remoteMessage.getData(),notification);
                        getNews(remoteMessage.getData(),notification);
                    }else if(remoteMessage.getData().get("messageType").equalsIgnoreCase(Constants.MessageType.Articles.toString())){
                        Log.i(TAG, "onMessageReceived: Notification Type Articles " + remoteMessage.getData().toString());
                        final Gson gson = new Gson();
                        final Notificaiton notification = gson.fromJson(remoteMessage.getData().get("notification"), Notificaiton.class);
                        buildBlogService();
                        getArticle(remoteMessage.getData(),notification);
                    }else if(remoteMessage.getData().get("messageType").equalsIgnoreCase(Constants.MessageType.HomeDecor.toString())){
                        Log.i(TAG, "onMessageReceived: Notification Type Home Decor " + remoteMessage.getData().toString());
                        final Gson gson = new Gson();
                        final Notificaiton notification = gson.fromJson(remoteMessage.getData().get("notification"), Notificaiton.class);
                        buildDecorEndPoint();
                        getHomeDecor(remoteMessage.getData(),notification);
                    }else if(remoteMessage.getData().get("messageType").equalsIgnoreCase(Constants.MessageType.PropertyBooking.toString())){
                        UtilityMethods.saveBooleanInPref(getApplicationContext(),Constants.MORE_FRAGMENT_UPDATE,true);
                    }else if(remoteMessage.getData().get("messageType").equalsIgnoreCase(Constants.MessageType.SiteVisit.toString())){
                        UtilityMethods.saveBooleanInPref(getApplicationContext(),Constants.MORE_FRAGMENT_UPDATE,true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotificationList(Map<String,String> messageBody, Notificaiton notification) {
        try {
            Log.i(TAG, "Notification Count -> Notification arrived via notificaiton");

            Intent intent ;
            if(messageBody != null && messageBody.containsKey("messageType")){
                if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.News.toString())){
                    intent = new Intent(this,BlogListActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String newsTitle = "";
                        if(clickUrl.startsWith(newsPrefixURL)) {
                            newsTitle = clickUrl.replace(newsPrefixURL, "");
                        }else if(clickUrl.startsWith(newsPrefixURL2)){
                            newsTitle = clickUrl.replace(newsPrefixURL2, "");
                        }
                        intent.putExtra("NewsTitle",newsTitle);
                    }
                    intent.putExtra("Position",newsOrArticlesPosition);
                    intent.putExtra("TotalNewsCount",newsOrArticlesListSize);
                    intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                    intent.putExtra("ArticleType","news");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }else if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.Articles.toString())){
                    intent = new Intent(this,BlogListActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String blogTitle = "";
                        if(clickUrl.startsWith(blogPrefixURL)) {
                            blogTitle = clickUrl.replace(blogPrefixURL, "");
                        }
                        intent.putExtra("BlogTitle",blogTitle);
                    }
                    intent.putExtra("Position",newsOrArticlesPosition);
                    intent.putExtra("TotalBlogCount",newsOrArticlesListSize);
                    intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                    intent.putExtra("ArticleType","blog");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }else if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.HomeDecor.toString())){
                    intent = new Intent(this,DecorDetailViewActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String homeDecorTitle="";
                        if(clickUrl.startsWith(decorPrefixURL)) {
                            homeDecorTitle = clickUrl.replace(decorPrefixURL, "");
                        }else if(clickUrl.startsWith(decorPrefixURL2)){
                            homeDecorTitle = clickUrl.replace(decorPrefixURL2, "");
                        }
                        intent.putExtra("HomeDecorTitle",homeDecorTitle);
                        intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                        intent.putExtra("ArticleType","homedecor");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else{
                        intent = new Intent(this,ShareActivity.class);
                        intent.putExtra("TotalBlogCount",newsOrArticlesListSize);
                        intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                        intent.putExtra("ArticleType","homedecor");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                }else{
                    UtilityMethods.saveBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, true);
                    intent = new Intent(this, NotificationActivity.class);
                    intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }else {
                UtilityMethods.saveBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, true);
                intent = new Intent(this, NotificationActivity.class);
                intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            Log.i(TAG, "sendNotificationList:  intent action " + intent.getComponent());
            int requestCode = (int)System.currentTimeMillis();
            intent.setAction(Long.toString(System.currentTimeMillis()));
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                    PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap icon1 = BitmapFactory.decodeResource(MyFirebaseMessagingService.this.getResources(),
                    R.mipmap.ic_app_logo);
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            int defaults = 0;
            defaults |= Notification.DEFAULT_LIGHTS;
            defaults |= Notification.DEFAULT_VIBRATE;
            defaults |= Notification.DEFAULT_SOUND;
            String title= MyFirebaseMessagingService.this.getResources().getString(R.string.app_name);

            if(mNotificationManager == null) {
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                CharSequence name = "Clicbrics";// The user-visible name of the channel.
                NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
                mChannel.setShowBadge(true);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                mNotificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.notificaiton_small_icon).setLargeIcon(icon1)
                    .setContentTitle(title)
                    .setContentText(notification.getText())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setDefaults(defaults)
                    .setContentIntent(pendingIntent);
            mNotificationManager.notify(notificationID, notificationBuilder.build());
            TrackAnalytics.trackEvent("notification_arrived","homescreen",this);
            TrackAnalytics.trackEvent("TextNotificationSend", Constants.AppConstants.HOLD_TIME , 1, this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "sendNotificationImage: Exception " + e.getMessage());
        }
    }

    /*private void sendNotificationList(Map<String,String> messageBody, Notificaiton notification) {
        try {
            Log.i(TAG, "Notification Count -> Notification arrived via notificaiton");
            TrackAnalytics.trackEvent("notification_arrived","homescreen",this);
            TrackAnalytics.trackEvent("TextNotificationSend", Constants.AppConstants.HOLD_TIME , 1, this);

            Intent intent = new Intent(this, HomeActivity.class);
            if(messageBody != null && messageBody.containsKey("messageType")){
                if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.News.toString())){
                    //intent = new Intent(this,BlogListActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String newsTitle = "";
                        if(clickUrl.startsWith(newsPrefixURL)) {
                            newsTitle = clickUrl.replace(newsPrefixURL, "");
                        }else if(clickUrl.startsWith(newsPrefixURL2)){
                            newsTitle = clickUrl.replace(newsPrefixURL2, "");
                        }
                        intent.putExtra("NewsTitle",newsTitle);
                    }
                    intent.putExtra("Position",newsOrArticlesPosition);
                    intent.putExtra("TotalNewsCount",newsOrArticlesListSize);
                    intent.putExtra("ArticleType","news");
                    intent.putExtra("activity","BlogListActivity");
                }else if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.Articles.toString())){
                    //intent = new Intent(this,BlogListActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String blogTitle = "";
                        if(clickUrl.startsWith(blogPrefixURL)) {
                            blogTitle = clickUrl.replace(blogPrefixURL, "");
                        }
                        intent.putExtra("BlogTitle",blogTitle);
                    }
                    intent.putExtra("Position",newsOrArticlesPosition);
                    intent.putExtra("TotalBlogCount",newsOrArticlesListSize);
                    intent.putExtra("ArticleType","blog");
                    intent.putExtra("activity","BlogListActivity");
                }else if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.HomeDecor.toString())){
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        //intent = new Intent(this,DecorDetailViewActivity.class);
                        String clickUrl = notification.getClickUrl();
                        String homeDecorTitle="";
                        if(clickUrl.startsWith(decorPrefixURL)) {
                            homeDecorTitle = clickUrl.replace(decorPrefixURL, "");
                        }else if(clickUrl.startsWith(decorPrefixURL2)){
                            homeDecorTitle = clickUrl.replace(decorPrefixURL2, "");
                        }
                        intent.putExtra("HomeDecorTitle",homeDecorTitle);
                        intent.putExtra("ArticleType","homedecor");
                        intent.putExtra("activity","DecorDetailViewActivity");
                    } else{
                        //intent = new Intent(this,ShareActivity.class);
                        intent.putExtra("TotalBlogCount",newsOrArticlesListSize);
                        intent.putExtra("ArticleType","homedecor");
                        intent.putExtra("activity","ShareActivity");
                    }
                }else{
                    UtilityMethods.saveBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, true);
                    //intent = new Intent(this, NotificationActivity.class);
                    intent.putExtra("activity","NotificationActivity");
                }
            }else {
                UtilityMethods.saveBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, true);
                //intent = new Intent(this, NotificationActivity.class);
                intent.putExtra("activity","NotificationActivity");
            }
            intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i(TAG, "sendNotificationList:  intent action " + intent.getComponent());
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Bitmap icon1 = BitmapFactory.decodeResource(MyFirebaseMessagingService.this.getResources(),
                    R.mipmap.ic_launcher);
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            int defaults = 0;
            defaults |= Notification.DEFAULT_LIGHTS;
            defaults |= Notification.DEFAULT_VIBRATE;
            defaults |= Notification.DEFAULT_SOUND;
            String title= MyFirebaseMessagingService.this.getResources().getString(R.string.app_name);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notificaiton_small_icon).setLargeIcon(icon1)
                    .setContentTitle(title)
                    .setContentText(notification.getText())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setDefaults(defaults)
                    .setContentIntent(pendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(mNotificationManager != null) {
                mNotificationManager.notify(notificationID, notificationBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "sendNotificationImage: Exception " + e.getMessage());
        }
    }*/


    private void sendNotificationImage(Map<String,String> messageBody,Notificaiton notification,Bitmap bm) {
        try {
            Log.i(TAG, "Notification Count -> Notification arrived via notificaiton " + messageBody);

            Intent intent ;
            if(messageBody != null && messageBody.containsKey("messageType")){
                if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.News.toString())){
                    intent = new Intent(this,BlogListActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String newsTitle = "";
                        if(clickUrl.startsWith(newsPrefixURL)){
                            newsTitle = clickUrl.replace(newsPrefixURL,"");
                        }else if(clickUrl.startsWith(newsPrefixURL2)){
                            newsTitle = clickUrl.replace(newsPrefixURL2,"");
                        }
                        intent.putExtra("NewsTitle",newsTitle);
                    }
                    intent.putExtra("ArticleType","news");
                    intent.putExtra("Position",newsOrArticlesPosition);
                    intent.putExtra("TotalNewsCount",newsOrArticlesListSize);
                    intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }else if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.Articles.toString())){
                    intent = new Intent(this,BlogListActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String blogTitle = "";
                        if(clickUrl.startsWith(blogPrefixURL)){
                            blogTitle = clickUrl.replace(blogPrefixURL,"");
                        }
                        intent.putExtra("BlogTitle",blogTitle);
                    }
                    intent.putExtra("Position",newsOrArticlesPosition);
                    intent.putExtra("TotalBlogCount",newsOrArticlesListSize);
                    intent.putExtra("ArticleType","blog");
                    intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }else if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.HomeDecor.toString())){
                    intent = new Intent(this,DecorDetailViewActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String homeDecorTitle="";
                        if(clickUrl.startsWith(decorPrefixURL)){
                            homeDecorTitle = clickUrl.replace(decorPrefixURL,"");
                        }else if(clickUrl.startsWith(decorPrefixURL2)){
                            homeDecorTitle = clickUrl.replace(decorPrefixURL2,"");
                        }
                        intent.putExtra("HomeDecorTitle",homeDecorTitle);
                        intent.putExtra("ArticleType","homedecor");
                        intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    else{
                        intent = new Intent(this,ShareActivity.class);
                        intent.putExtra("TotalBlogCount",newsOrArticlesListSize);
                        intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                        intent.putExtra("ArticleType","homedecor");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                }else{
                    UtilityMethods.saveBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, true);
                    intent = new Intent(this, NotificationActivity.class);
                    intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }else {
                UtilityMethods.saveBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, true);
                intent = new Intent(this, NotificationActivity.class);
                intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            Log.i(TAG, "sendNotificationImage:  intent action " + intent.getComponent());
            int requestCode = (int)System.currentTimeMillis();
            intent.setAction(Long.toString(System.currentTimeMillis()));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                    PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap icon1 = BitmapFactory.decodeResource(MyFirebaseMessagingService.this.getResources(),
                    R.mipmap.ic_app_logo);
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            int defaults = 0;
            defaults |= Notification.DEFAULT_LIGHTS;
            defaults |= Notification.DEFAULT_VIBRATE;
            defaults |= Notification.DEFAULT_SOUND;
            String title= MyFirebaseMessagingService.this.getResources().getString(R.string.app_name);

            if(mNotificationManager == null) {
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                CharSequence name = "Clicbrics";// The user-visible name of the channel.
                NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                mChannel.setShowBadge(true);
                mNotificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.notificaiton_small_icon).setLargeIcon(icon1)
                    .setContentTitle(title)
                    .setContentText(notification.getText())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri).setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bm).setSummaryText(notification.getText()))
                    .setDefaults(defaults)
                    .setContentIntent(pendingIntent);

            mNotificationManager.notify(notificationID, notificationBuilder.build());

            TrackAnalytics.trackEvent("notification_arrived","homescreen",this);
            TrackAnalytics.trackEvent("ImageNotificationSend", Constants.AppConstants.HOLD_TIME , 1, this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "sendNotificationImage: Exception " + e.getMessage());
        }
    }

    /*private void sendNotificationImage(Map<String,String> messageBody,Notificaiton notification,Bitmap bm) {
        try {
            Log.i(TAG, "Notification Count -> Notification arrived via notificaiton");
            TrackAnalytics.trackEvent("notification_arrived","homescreen",this);

            TrackAnalytics.trackEvent("ImageNotificationSend", Constants.AppConstants.HOLD_TIME , 1, this);

            Intent intent = new Intent(this,HomeActivity.class);
            if(messageBody != null && messageBody.containsKey("messageType")){
                if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.News.toString())){
                    //intent = new Intent(this,BlogListActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String newsTitle = "";
                        if(clickUrl.startsWith(newsPrefixURL)){
                            newsTitle = clickUrl.replace(newsPrefixURL,"");
                        }else if(clickUrl.startsWith(newsPrefixURL2)){
                            newsTitle = clickUrl.replace(newsPrefixURL2,"");
                        }
                        intent.putExtra("NewsTitle",newsTitle);
                    }
                    intent.putExtra("activity","BlogListActivity");
                    intent.putExtra("ArticleType","news");
                    intent.putExtra("Position",newsOrArticlesPosition);
                    intent.putExtra("TotalNewsCount",newsOrArticlesListSize);
                }else if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.Articles.toString())){
                    //intent = new Intent(this,BlogListActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String blogTitle = "";
                        if(clickUrl.startsWith(blogPrefixURL)){
                            blogTitle = clickUrl.replace(blogPrefixURL,"");
                        }
                        intent.putExtra("BlogTitle",blogTitle);
                    }
                    intent.putExtra("activity","BlogListActivity");
                    intent.putExtra("Position",newsOrArticlesPosition);
                    intent.putExtra("TotalBlogCount",newsOrArticlesListSize);
                    intent.putExtra("ArticleType","blog");
                }else if(messageBody.get("messageType").equalsIgnoreCase(Constants.MessageType.HomeDecor.toString())){
                    //intent = new Intent(this,DecorDetailViewActivity.class);
                    if(notification != null && !TextUtils.isEmpty(notification.getClickUrl())){
                        String clickUrl = notification.getClickUrl();
                        String homeDecorTitle="";
                        if(clickUrl.startsWith(decorPrefixURL)){
                            homeDecorTitle = clickUrl.replace(decorPrefixURL,"");
                        }else if(clickUrl.startsWith(decorPrefixURL2)){
                            homeDecorTitle = clickUrl.replace(decorPrefixURL2,"");
                        }
                        intent.putExtra("HomeDecorTitle",homeDecorTitle);
                        intent.putExtra("ArticleType","homedecor");
                        intent.putExtra("activity","DecorDetailViewActivity");
                    }
                    else{
                      //  intent = new Intent(this,ShareActivity.class);
                        intent.putExtra("TotalBlogCount",newsOrArticlesListSize);
                        intent.putExtra("ArticleType","homedecor");
                        intent.putExtra("activity","ShareActivity");
                    }
                }else{
                    UtilityMethods.saveBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, true);
                    //intent = new Intent(this, NotificationActivity.class);
                    intent.putExtra("activity","NotificationActivity");
                }
            }else {
                UtilityMethods.saveBooleanInPref(this, Constants.AppConstants.IS_NEW_NOTIFICATION, true);
                intent.putExtra("activity","NotificationActivity");
                //intent = new Intent(this, NotificationActivity.class);
            }
            intent.putExtra(Constants.IntentKeyConstants.PUSH_NOTIFICATION, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i(TAG, "sendNotificationImage:  intent action " + intent.getComponent());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Bitmap icon1 = BitmapFactory.decodeResource(MyFirebaseMessagingService.this.getResources(),
                    R.mipmap.ic_launcher);
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            int defaults = 0;
            defaults |= Notification.DEFAULT_LIGHTS;
            defaults |= Notification.DEFAULT_VIBRATE;
            defaults |= Notification.DEFAULT_SOUND;
            String title= MyFirebaseMessagingService.this.getResources().getString(R.string.app_name);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notificaiton_small_icon).setLargeIcon(icon1)
                    .setContentTitle(title)
                    .setContentText(notification.getText())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri).setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bm).setSummaryText(notification.getText()))
                    .setDefaults(defaults)
                    .setContentIntent(pendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(mNotificationManager != null) {
                mNotificationManager.notify(notificationID, notificationBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "sendNotificationImage: Exception " + e.getMessage());
        }
    }*/

    public Bitmap createImageInImageCenter(Bitmap backgroundBitmap){
        Bitmap resultBitmap = null;
        try {
            if(backgroundBitmap != null && backgroundBitmap.getByteCount() > (1024*500)){
                backgroundBitmap = scaledBitmap(backgroundBitmap,420,320);
            }
            Bitmap bitmapToDrawInTheCenter = BitmapFactory.decodeResource(getResources(), R.drawable.video_play_icon);

            resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),backgroundBitmap.getHeight(), backgroundBitmap.getConfig());
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(backgroundBitmap, new Matrix(), null);

            Bitmap image = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            //image.eraseColor(getResources().getColor(R.color.translucent_red));
            canvas.drawBitmap(image, new Matrix(), null);


            canvas.drawBitmap(bitmapToDrawInTheCenter, (backgroundBitmap.getWidth() - bitmapToDrawInTheCenter.getWidth()) / 2, (backgroundBitmap.getHeight() - bitmapToDrawInTheCenter.getHeight()) / 2, new Paint());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (resultBitmap);
    }

    private void buildBlogService() {
        mBlogEndPoint = EndPointBuilder.getBlogEndPoint();
    }

    private void buildNewsEndPoint(){
        mNewsEndPoint = EndPointBuilder.getNewsEndPoint();
    }

    private void buildDecorEndPoint(){
        mDecorEndPoint = EndPointBuilder.getDecorEndPoint();
    }


    private void getArticle(final Map<String,String> messageBody,final Notificaiton notificaiton){
        if(!UtilityMethods.isInternetConnected(this)){
            return;
        }
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                String articleTitle = "";
                try {
                    if(notificaiton != null && !TextUtils.isEmpty(notificaiton.getClickUrl())){
                        String clickUrl = notificaiton.getClickUrl();
                        if(clickUrl.startsWith(blogPrefixURL)){
                            articleTitle = clickUrl.replace(blogPrefixURL,"");
                        }
                    }
                    HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                    httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                    final BlogResponse blogResponse = mBlogEndPoint.getBlog(1l,"1",articleTitle,false)
                            .setRequestHeaders(httpHeaders).execute();
                    if(blogResponse != null && blogResponse.getStatus()){
                        Blog sharedBlog = blogResponse.getBlog();
                        if(notificaiton.getType().equalsIgnoreCase("text")){
                            notificaiton.setText(articleTitle);
                            sendNotificationList(messageBody,notificaiton);
                        }else if(notificaiton.getType().equalsIgnoreCase("Video")){
                            try {
                                final String videoUrl = notificaiton.getWelcomeUrl();
                                String videoId = "";
                                if(videoUrl != null && !videoUrl.isEmpty()) {
                                    if (videoUrl.contains("https://www.youtube.com/embed/")) {
                                        videoId = videoUrl.replace("https://www.youtube.com/embed/", "");
                                    } else if (videoUrl.contains("http://www.youtube.com/embed/")) {
                                        videoId = videoUrl.replace("http://www.youtube.com/embed/", "");
                                    } else if (videoUrl.contains("http://www.youtube.com/watch?v=")) {
                                        videoId = videoUrl.replace("http://www.youtube.com/watch?v=", "");
                                    } else if (videoUrl.contains("https://www.youtube.com/watch?v=")) {
                                        videoId = videoUrl.replace("https://www.youtube.com/watch?v=", "");
                                    } else {
                                        if(videoUrl.contains("=")){
                                            videoId = videoUrl.substring(videoUrl.lastIndexOf('=')+1,videoUrl.length());
                                        }
                                    }
                                }
                                String thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
                                URL imageurl = new URL(thumbnailUrl);
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(messageBody, notificaiton, createImageInImageCenter(bitmap));
                            } catch (Exception ex) {
                                ex.getStackTrace();
                                try {
                                    sendNotificationImage(messageBody, notificaiton,
                                            BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }else if(notificaiton.getType().equalsIgnoreCase("Image")){
                            try {
                                URL imageurl = new URL(notificaiton.getWelcomeUrl());
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(messageBody, notificaiton, bitmap);
                            } catch (Exception ex) {
                                Log.i(TAG, "onMessageReceived: Image Type Notification Exception -> " + ex.getMessage());
                                ex.printStackTrace();
                                try {
                                    sendNotificationImage(messageBody, notificaiton, BitmapFactory.decodeResource(getResources(),
                                            R.drawable.drawer_logo));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            try {
                                URL imageurl = new URL(notificaiton.getWelcomeUrl());
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(messageBody, notificaiton, bitmap);
                            } catch (Exception ex) {
                                Log.i(TAG, "onMessageReceived: Image Type Notification Exception -> " + ex.getMessage());
                                ex.printStackTrace();
                                try {
                                    sendNotificationImage(messageBody, notificaiton,
                                            BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }else{
                        if(blogResponse != null && !TextUtils.isEmpty(blogResponse.getErrorMessage())){
                            errorMsg = blogResponse.getErrorMessage();
                        }else{
                            errorMsg = "Something went wrong.Please try again!";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Something went wrong.Please try again!";
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    newsOrArticlesPosition = 0;
                    newsOrArticlesListSize = 0;
                    sendNotificationList(messageBody,notificaiton);
                }
            }
        }).start();
    }

    private void getHomeDecor(final Map<String,String> messageBody,final Notificaiton notificaiton){
        if(!UtilityMethods.isInternetConnected(this)){
            return;
        }
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    String decorTitle = "";
                    if(notificaiton != null && !TextUtils.isEmpty(notificaiton.getClickUrl())){
                        String clickUrl = notificaiton.getClickUrl();
                        if(clickUrl.startsWith(decorPrefixURL)) {
                            decorTitle = clickUrl.replace(decorPrefixURL, "");
                        }else if(clickUrl.startsWith(decorPrefixURL2)){
                            decorTitle = clickUrl.replace(decorPrefixURL2,"");
                        }
                    }
                    Log.i(TAG, "get Home Decor Name "  + decorTitle);
                    HttpHeaders httpHeaders=UtilityMethods.getHttpHeaders();
                    httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
                    final DecorResponse decorResponse = mDecorEndPoint.getDecor(decorTitle)
                            .setRequestHeaders(httpHeaders).execute();
                    if(decorResponse != null && decorResponse.getStatus()){
                        Decor sharedDecor = decorResponse.getDecor();
                        if(notificaiton.getType().equalsIgnoreCase("text")){
                            notificaiton.setText(decorTitle);
                            sendNotificationList(messageBody,notificaiton);
                        }else if(notificaiton.getType().equalsIgnoreCase("Video")){
                            try {
                                final String videoUrl = notificaiton.getWelcomeUrl();
                                String videoId = "";
                                if(videoUrl != null && !videoUrl.isEmpty()) {
                                    if (videoUrl.contains("https://www.youtube.com/embed/")) {
                                        videoId = videoUrl.replace("https://www.youtube.com/embed/", "");
                                    } else if (videoUrl.contains("http://www.youtube.com/embed/")) {
                                        videoId = videoUrl.replace("http://www.youtube.com/embed/", "");
                                    } else if (videoUrl.contains("http://www.youtube.com/watch?v=")) {
                                        videoId = videoUrl.replace("http://www.youtube.com/watch?v=", "");
                                    } else if (videoUrl.contains("https://www.youtube.com/watch?v=")) {
                                        videoId = videoUrl.replace("https://www.youtube.com/watch?v=", "");
                                    } else {
                                        if(videoUrl.contains("=")){
                                            videoId = videoUrl.substring(videoUrl.lastIndexOf('=')+1,videoUrl.length());
                                        }
                                    }
                                }
                                String thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
                                URL imageurl = new URL(thumbnailUrl);
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(messageBody, notificaiton, createImageInImageCenter(bitmap));
                            } catch (Exception ex) {
                                ex.getStackTrace();
                                try {
                                    sendNotificationImage(messageBody, notificaiton,
                                            BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }else if(notificaiton.getType().equalsIgnoreCase("Image")){
                            try {
                                URL imageurl = new URL(notificaiton.getWelcomeUrl());
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Log.i(TAG, "Notificaiton Image URL "+ imageurl);
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(messageBody, notificaiton, bitmap);
                            } catch (Exception ex) {
                                Log.i(TAG, "onMessageReceived: Image Type Notification Exception -> " + ex.getMessage());
                                ex.printStackTrace();
                                try {
                                    sendNotificationImage(messageBody, notificaiton,
                                            BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            try {
                                URL imageurl = new URL(notificaiton.getWelcomeUrl());
                                URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                imageurl = uri.toURL();
                                URLConnection connection = new URL(imageurl.toString()).openConnection();
                                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                    bitmap = scaledBitmap(bitmap,420,320);
                                }
                                sendNotificationImage(messageBody, notificaiton, bitmap);
                            } catch (Exception ex) {
                                Log.i(TAG, "onMessageReceived: Image Type Notification Exception -> " + ex.getMessage());
                                ex.printStackTrace();
                                try {
                                    sendNotificationImage(messageBody, notificaiton,
                                            BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }else{
                        if(decorResponse != null && !TextUtils.isEmpty(decorResponse.getErrorMessage())){
                            errorMsg = decorResponse.getErrorMessage();
                        }else{
                            errorMsg = "Something went wrong.Please try again!";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Something went wrong.Please try again!";
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    newsOrArticlesPosition = 0;
                    newsOrArticlesListSize = 0;
                    sendNotificationList(messageBody,notificaiton);
                }
            }
        }).start();
    }

    private void getNews(final Map<String,String> messageBody,final Notificaiton notificaiton){
        new GetNewsDetails(messageBody,notificaiton).execute();
    }

    public class GetNewsDetails extends AsyncTask<String , Void ,StringBuffer> {
        String server_response;
        Map<String, String> messageBody = null;
        Notificaiton notificaiton;
        String newsTitle = "";
        GetNewsDetails(final Map<String,String> messageBody,final Notificaiton notificaiton){
            this.messageBody = messageBody;
            this.notificaiton = notificaiton;
        }
        @Override
        protected StringBuffer doInBackground(String... strings) {

            if(notificaiton != null && !TextUtils.isEmpty(notificaiton.getClickUrl())){
                String clickUrl = notificaiton.getClickUrl();
                if(clickUrl.startsWith(newsPrefixURL)){
                    newsTitle = clickUrl.replace(newsPrefixURL,"");
                }else if(clickUrl.startsWith(newsPrefixURL2)){
                    newsTitle = clickUrl.replace(newsPrefixURL2,"");
                }
                Log.i(TAG, "doInBackground: News Title -> "+ newsTitle);
                if(TextUtils.isEmpty(newsTitle)){
                    return null;
                }
            }else{
                return null;
            }
            String newsURL = BuildConfigConstants.SERVER_URL + "getnewsservlet?userToken=sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn&searchTitle=" + newsTitle;
            Log.i(TAG, "doInBackground: final server hit url -> " + newsURL);
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuffer result = new StringBuffer();
            try {
                url = new URL(newsURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "doInBackground: success with response code " + responseCode);
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(StringBuffer result) {
            super.onPostExecute(result);
            if(result != null) {
                Log.i(TAG, "Response " + server_response);
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if(jsonObject.get("status").toString().equalsIgnoreCase("true")){
                        if(notificaiton.getType().equalsIgnoreCase("text")){
                            notificaiton.setText(newsTitle);
                            sendNotificationList(messageBody,notificaiton);
                        }else if(notificaiton.getType().equalsIgnoreCase("Video")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final String videoUrl = notificaiton.getWelcomeUrl();
                                        String videoId = "";
                                        if(videoUrl != null && !videoUrl.isEmpty()) {
                                            if (videoUrl.contains("https://www.youtube.com/embed/")) {
                                                videoId = videoUrl.replace("https://www.youtube.com/embed/", "");
                                            } else if (videoUrl.contains("http://www.youtube.com/embed/")) {
                                                videoId = videoUrl.replace("http://www.youtube.com/embed/", "");
                                            } else if (videoUrl.contains("http://www.youtube.com/watch?v=")) {
                                                videoId = videoUrl.replace("http://www.youtube.com/watch?v=", "");
                                            } else if (videoUrl.contains("https://www.youtube.com/watch?v=")) {
                                                videoId = videoUrl.replace("https://www.youtube.com/watch?v=", "");
                                            } else {
                                                if(videoUrl.contains("=")){
                                                    videoId = videoUrl.substring(videoUrl.lastIndexOf('=')+1,videoUrl.length());
                                                }
                                            }
                                        }
                                        String thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
                                        URL imageurl = new URL(thumbnailUrl);
                                        URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                        imageurl = uri.toURL();
                                        URLConnection connection = new URL(imageurl.toString()).openConnection();
                                        Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                        if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                            bitmap = scaledBitmap(bitmap,420,320);
                                        }
                                        sendNotificationImage(messageBody, notificaiton, createImageInImageCenter(bitmap));
                                    } catch (Exception ex) {
                                        ex.getStackTrace();
                                        try {
                                            sendNotificationImage(messageBody, notificaiton,
                                                    BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        }else if(notificaiton.getType().equalsIgnoreCase("Image")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        URL imageurl = new URL(notificaiton.getWelcomeUrl());
                                        URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                        imageurl = uri.toURL();
                                        URLConnection connection = new URL(imageurl.toString()).openConnection();
                                        Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                        if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                            bitmap = scaledBitmap(bitmap,420,320);
                                        }
                                        sendNotificationImage(messageBody, notificaiton, bitmap);
                                    } catch (Exception ex) {
                                        Log.i(TAG, "onMessageReceived: Image Type Notification Exception -> " + ex.getMessage());
                                        ex.printStackTrace();
                                        try {
                                            sendNotificationImage(messageBody, notificaiton,
                                                    BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        URL imageurl = new URL(notificaiton.getWelcomeUrl());
                                        URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                                        imageurl = uri.toURL();
                                        URLConnection connection = new URL(imageurl.toString()).openConnection();
                                        Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                                        if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                            bitmap = scaledBitmap(bitmap,420,320);
                                        }
                                        sendNotificationImage(messageBody, notificaiton, bitmap);
                                    } catch (Exception ex) {
                                        Log.i(TAG, "onMessageReceived: Image Type Notification Exception -> " + ex.getMessage());
                                        ex.printStackTrace();
                                        try {
                                            sendNotificationImage(messageBody, notificaiton, BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        }
                    }else{
                        notificaiton.setText(newsTitle);
                        sendNotificationList(messageBody,notificaiton);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Log.i(TAG, "onPostExecute: found exception in checking news "+ e.getMessage());
                        sendNotificationImage(messageBody, notificaiton,
                                BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }else{
                Log.i(TAG," Response is null");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL imageurl = new URL(notificaiton.getWelcomeUrl());
                            URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
                            imageurl = uri.toURL();
                            URLConnection connection = new URL(imageurl.toString()).openConnection();
                            Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                            if(bitmap != null && bitmap.getByteCount() > (1024*500)){
                                bitmap = scaledBitmap(bitmap,420,320);
                            }
                            sendNotificationImage(messageBody, notificaiton, bitmap);
                        } catch (Exception ex) {
                            Log.i(TAG, "onMessageReceived: Image Type Notification Exception -> " + ex.getMessage());
                            ex.printStackTrace();
                            try {
                                sendNotificationImage(messageBody, notificaiton,
                                        BitmapFactory.decodeResource(getResources(),R.drawable.drawer_logo));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    /*@Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        startService(rootIntent);
    }*/
}
