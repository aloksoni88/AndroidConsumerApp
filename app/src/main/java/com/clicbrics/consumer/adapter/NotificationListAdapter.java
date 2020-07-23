package com.clicbrics.consumer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.SectioningAdapter;
import com.clicbrics.consumer.activities.NotificationActivity;
import com.clicbrics.consumer.activities.NotificationDetailsWebviewActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.clicbrics.consumer.wrapper.Notificaiton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Alok on 27-05-2016.
 */
@SuppressWarnings("deprecation")
public class NotificationListAdapter extends SectioningAdapter {

    private static final String TAG = NotificationListAdapter.class.getName();
    Activity context;
    ArrayList<Notificaiton> newNotifications;
    ArrayList<Notificaiton> savedNotifications;

    public NotificationListAdapter(Activity context, ArrayList<Notificaiton> notifications,
                                   ArrayList<Notificaiton> savedNotifications) {
        this.context = context;

        newNotifications = notifications;
        this.savedNotifications = savedNotifications;

        /*if ((newNotifications != null) && (newNotifications.size() >  1)) {
            Collections.sort(newNotifications, new Comparator<Notificaiton>() {
                @Override
                public int compare(Notificaiton lhs, Notificaiton rhs) {
                    return (int) (rhs.getTime() - lhs.getTime());
                }
            });
        }*/

        /*if ((savedNotifications != null) && (savedNotifications.size() > 1)) {
            Collections.sort(savedNotifications, new Comparator<Notificaiton>() {
                @Override
                public int compare(Notificaiton lhs, Notificaiton rhs) {
                    return (int) (rhs.getTime() - lhs.getTime());
                }
            });
        }*/

    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
        TextView notificationText,notificationTime;
        WebView webViewImage;
        ImageView notificationIcon, notificationImage, videoOverlay;
        LinearLayout clickLayout;
        ProgressBar mImagePB;

        public ItemViewHolder(View itemView) {
            super(itemView);
            notificationText = (TextView) itemView.findViewById(R.id.id_notification_text);
            notificationTime = (TextView) itemView.findViewById(R.id.id_notification_time);
            webViewImage = (WebView) itemView.findViewById(R.id.id_notification_webview);
            notificationIcon = (ImageView) itemView.findViewById(R.id.notification_new);
            notificationImage = (ImageView) itemView.findViewById(R.id.notification_image);
            clickLayout = (LinearLayout) itemView.findViewById(R.id.for_click);
            videoOverlay = (ImageView) itemView.findViewById(R.id.video_overlay);
            mImagePB = (ProgressBar) itemView.findViewById(R.id.id_notificaiton_image_PB);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        TextView notificationType;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            notificationType = (TextView) itemView.findViewById(R.id.id_notificaiton_section_header_text);
        }
    }

    @Override
    public int getNumberOfSections() {
        //Log.i(TAG, "getNumberOfSections: " + notificationMap.size());
        if ((newNotifications != null) && (!newNotifications.isEmpty())
                && ((savedNotifications != null) && (!savedNotifications.isEmpty()))) {
            return 2;
        } else if ((newNotifications != null) && (!newNotifications.isEmpty())
                || ((savedNotifications != null) && (!savedNotifications.isEmpty()))) {
            return 1;
        } else {
            return 0;
        }

    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
//        Log.i(TAG, "getNumberOfItemsInSection: " + notificationMap.get(notificationHeaderList.get(sectionIndex)).size());
//        return notificationMap.get(notificationHeaderList.get(sectionIndex)).size();
        if ((sectionIndex == 0) && (newNotifications != null) && (!newNotifications.isEmpty())) {
            return newNotifications.size();
        } else {
            return savedNotifications.size();
        }

    }


    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.notification_content_adapter_layout, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.notification_header_adapter_layout, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, final int itemIndex, int itemType) {
        final ItemViewHolder ivh = (ItemViewHolder) viewHolder;
        //viewHolder.setIsRecyclable(false);

        if (((newNotifications != null) && (!newNotifications.isEmpty())) && (sectionIndex == 0)) {
            Log.i(TAG, "New Notification position -> " + itemIndex);
            ivh.notificationIcon.setVisibility(View.VISIBLE);
            ivh.notificationTime.setText(UtilityMethods.getDate(newNotifications.get(itemIndex).getTime(),"dd MMM yyyy hh:mm aa"));
            if (newNotifications.get(itemIndex).getType().equalsIgnoreCase("TEXT")) {
                ivh.mImagePB.setVisibility(View.GONE);
                ivh.videoOverlay.setVisibility(View.GONE);
                ivh.webViewImage.setVisibility(View.GONE);
                ivh.notificationImage.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(newNotifications.get(itemIndex).getText())) {
                    ivh.notificationText.setText(newNotifications.get(itemIndex).getText());
                } else {
                    ivh.notificationText.setVisibility(View.GONE);
                }
                ivh.notificationTime.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                                        ,0
                                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                                        ,(int)context.getResources().getDimension(R.dimen.margin_10));
                ivh.notificationText.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,0);

            } else if (newNotifications.get(itemIndex).getType().equalsIgnoreCase("IMAGE")) {
                ivh.videoOverlay.setVisibility(View.GONE);
                ivh.webViewImage.setVisibility(View.GONE);
                ivh.notificationTime.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10));
                ivh.notificationText.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10));
                if (!TextUtils.isEmpty(newNotifications.get(itemIndex).getText())) {
                    ivh.notificationText.setText(newNotifications.get(itemIndex).getText());
                } else {
                    ivh.notificationText.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(newNotifications.get(itemIndex).getUrl())) {
                    ivh.notificationImage.setVisibility(View.VISIBLE);
                    String imgUrl = newNotifications.get(itemIndex).getUrl();
                    /*try {
                        imgUrl = URLEncoder.encode(imgUrl, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }*/
                    imgUrl = imgUrl.replaceAll(" ", "%20");
                    ivh.mImagePB.setVisibility(View.VISIBLE);
                    Picasso.get().load(imgUrl)//newNotifications.get(itemIndex).getUrl())
                            .placeholder(R.color.gray_300)
                            .resize(600,400)
                            .into(ivh.notificationImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    ivh.mImagePB.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    ivh.mImagePB.setVisibility(View.GONE);
                                }
                            });

                } else {
                    ivh.webViewImage.setVisibility(View.GONE);
                }

                if (newNotifications != null && newNotifications.get(itemIndex) != null
                    && !TextUtils.isEmpty(newNotifications.get(itemIndex).getClickUrl())) {
                    ivh.clickLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new EventAnalyticsHelper().ItemClickEvent(context, Constants.AnaylticsClassName.NotificationScreen,
                                    newNotifications.get(itemIndex), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.notoficationClick.toString());
                            try {
                                handleClicURL(newNotifications.get(itemIndex).getClickUrl(), ivh.notificationText);
                            } catch (Exception ex) {
                                String urlToOpen = newNotifications.get(itemIndex).getClickUrl();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(urlToOpen));
                                context.startActivity(i);
                            }
                        }
                    });
                } else {
                    //Dont do anything!
                    ivh.clickLayout.setOnClickListener(null);
                }

            } else if (newNotifications.get(itemIndex).getType().equalsIgnoreCase("HTML")) {
                ivh.notificationTime.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10));
                ivh.notificationText.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10));
                ivh.mImagePB.setVisibility(View.GONE);
                ivh.videoOverlay.setVisibility(View.GONE);
                ivh.notificationImage.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(newNotifications.get(itemIndex).getText())) {
                    ivh.notificationText.setText(newNotifications.get(itemIndex).getText());
                } else {
                    ivh.notificationText.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(newNotifications.get(itemIndex).getUrl())) {
                    ivh.webViewImage.setVisibility(View.INVISIBLE);
                    setWebView(ivh.webViewImage, newNotifications.get(itemIndex).getUrl());
                } else {
                    ivh.webViewImage.setVisibility(View.GONE);
                }

                if (newNotifications != null && newNotifications.get(itemIndex) != null
                    && !TextUtils.isEmpty(newNotifications.get(itemIndex).getClickUrl())) {
                    ivh.clickLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new EventAnalyticsHelper().ItemClickEvent(context, Constants.AnaylticsClassName.NotificationScreen,
                                    newNotifications.get(itemIndex), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.notoficationClick.toString());
                            try {
                                handleClicURL(newNotifications.get(itemIndex).getClickUrl(), ivh.notificationText);
                            } catch (Exception ex) {
                                String urlToOpen = newNotifications.get(itemIndex).getClickUrl();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(urlToOpen));
                                context.startActivity(i);
                            }
                        }
                    });
                } else {
                    //Dont do anything!
                    ivh.clickLayout.setOnClickListener(null);
                }

            } else if (newNotifications.get(itemIndex).getType().equalsIgnoreCase("VIDEO")) {
                ivh.notificationTime.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10));
                ivh.notificationText.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10)
                        ,(int)context.getResources().getDimension(R.dimen.margin_10));
                ivh.mImagePB.setVisibility(View.GONE);
                ivh.videoOverlay.setVisibility(View.VISIBLE);
                ivh.webViewImage.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(newNotifications.get(itemIndex).getText())) {
                    ivh.notificationText.setText(newNotifications.get(itemIndex).getText());
                } else {
                    ivh.notificationText.setVisibility(View.GONE);
                }

                final String url = newNotifications.get(itemIndex).getUrl();
                Log.i(TAG, "Actual URL -> " + url);
                String videoId = "";
                if(url.contains("https://www.youtube.com/embed/")){
                    videoId = url.replace("https://www.youtube.com/embed/", "");
                }else if(url.contains("http://www.youtube.com/embed/")){
                    videoId = url.replace("http://www.youtube.com/embed/", "");
                }else if(url.contains("http://www.youtube.com/watch?v=")){
                    videoId = url.replace("http://www.youtube.com/watch?v=", "");
                }else if(url.contains("https://www.youtube.com/watch?v=")){
                    videoId = url.replace("https://www.youtube.com/watch?v=", "");
                }
                String thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
                Log.i(TAG, "Video Thumbnail URL -> " + thumbnailUrl);
                if (!TextUtils.isEmpty(newNotifications.get(itemIndex).getUrl())) {
                    ivh.notificationImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(thumbnailUrl)
                            .placeholder(R.color.gray_300)
                            .resize(600,400)
                            .into(ivh.notificationImage);
                } else {
                    ivh.webViewImage.setVisibility(View.GONE);
                }

                ivh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new EventAnalyticsHelper().ItemClickEvent(context, Constants.AnaylticsClassName.NotificationScreen,
                                newNotifications.get(itemIndex), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.notoficationClick.toString());
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        youtubeIntent.putExtra("force_fullscreen", true);
                        context.startActivity(youtubeIntent);
                    }
                });
            }

        } else {
            if ((savedNotifications != null) && (!savedNotifications.isEmpty())) {
                Log.i(TAG, "Old Notification position -> " + itemIndex);
                ivh.notificationIcon.setVisibility(View.GONE);
                ivh.notificationTime.setText(UtilityMethods.getDate(savedNotifications.get(itemIndex).getTime(),"dd MMM yyyy hh:mm aa"));
                if (savedNotifications.get(itemIndex).getType().equalsIgnoreCase("TEXT")) {
                    ivh.notificationTime.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                            ,0
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10));
                    ivh.notificationText.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,0);
                    ivh.mImagePB.setVisibility(View.GONE);
                    ivh.videoOverlay.setVisibility(View.GONE);
                    ivh.webViewImage.setVisibility(View.GONE);
                    ivh.notificationImage.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(savedNotifications.get(itemIndex).getText())) {
                        ivh.notificationText.setText(savedNotifications.get(itemIndex).getText());
                    } else {
                        ivh.notificationText.setVisibility(View.GONE);
                    }
                } else if (savedNotifications.get(itemIndex).getType().equalsIgnoreCase("IMAGE")) {
                    ivh.notificationTime.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10));
                    ivh.notificationText.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10));
                    ivh.videoOverlay.setVisibility(View.GONE);
                    ivh.webViewImage.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(savedNotifications.get(itemIndex).getText())) {
                        ivh.notificationText.setText(savedNotifications.get(itemIndex).getText());
                    } else {
                        ivh.notificationText.setVisibility(View.GONE);
                    }

                    if (!TextUtils.isEmpty(savedNotifications.get(itemIndex).getUrl())) {
                        ivh.notificationImage.setVisibility(View.VISIBLE);
                        String imgUrl = savedNotifications.get(itemIndex).getUrl();
                        /*try {
                            imgUrl = URLEncoder.encode(imgUrl, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }*/

                        imgUrl = imgUrl.replaceAll(" ", "%20");
                        ivh.mImagePB.setVisibility(View.VISIBLE);
                        Picasso.get().load(imgUrl)//savedNotifications.get(itemIndex).getUrl())
                                .placeholder(R.color.gray_300)
                                .resize(600,400)
                                .into(ivh.notificationImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        ivh.mImagePB.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        ivh.mImagePB.setVisibility(View.GONE);
                                    }
                                });
                    } else {
                        ivh.webViewImage.setVisibility(View.GONE);
                    }

                    if (savedNotifications != null && savedNotifications.get(itemIndex) != null
                        && !TextUtils.isEmpty(savedNotifications.get(itemIndex).getClickUrl())) {
                        ivh.clickLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new EventAnalyticsHelper().ItemClickEvent(context, Constants.AnaylticsClassName.NotificationScreen,
                                        newNotifications.get(itemIndex), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.notoficationClick.toString());
                                try {
                                    handleClicURL(savedNotifications.get(itemIndex).getClickUrl(), ivh.notificationText);
                                } catch (Exception ex) {
                                    String urlToOpen = savedNotifications.get(itemIndex).getClickUrl();
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(urlToOpen));
                                    context.startActivity(i);
                                }
                            }
                        });
                    } else {
                        //Dont do anything!
                        ivh.clickLayout.setOnClickListener(null);
                    }

                } else if (savedNotifications.get(itemIndex).getType().equalsIgnoreCase("HTML")) {
                    ivh.notificationTime.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10));
                    ivh.notificationText.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10));
                    ivh.mImagePB.setVisibility(View.GONE);
                    ivh.videoOverlay.setVisibility(View.GONE);
                    ivh.notificationImage.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(savedNotifications.get(itemIndex).getText())) {
                        ivh.notificationText.setText(savedNotifications.get(itemIndex).getText());
                    } else {
                        ivh.notificationText.setVisibility(View.GONE);
                    }

                    if (!TextUtils.isEmpty(savedNotifications.get(itemIndex).getUrl())) {
                        ivh.webViewImage.setVisibility(View.INVISIBLE);
                        setWebView(ivh.webViewImage, savedNotifications.get(itemIndex).getUrl());
                    } else {
                        ivh.webViewImage.setVisibility(View.GONE);
                    }

                    if (savedNotifications != null && savedNotifications.get(itemIndex) != null
                        && !TextUtils.isEmpty(savedNotifications.get(itemIndex).getClickUrl())) {
                        ivh.clickLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new EventAnalyticsHelper().ItemClickEvent(context, Constants.AnaylticsClassName.NotificationScreen,
                                        newNotifications.get(itemIndex), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.notoficationClick.toString());
                                try {
                                    handleClicURL(savedNotifications.get(itemIndex).getClickUrl(), ivh.notificationText);
                                } catch (Exception ex) {
                                    String urlToOpen = savedNotifications.get(itemIndex).getClickUrl();
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(urlToOpen));
                                    context.startActivity(i);
                                }
                            }
                        });
                    } else {
                        //Dont do anything!
                        ivh.clickLayout.setOnClickListener(null);
                    }

                } else if (savedNotifications.get(itemIndex).getType().equalsIgnoreCase("VIDEO")){
                    ivh.notificationTime.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10));
                    ivh.notificationText.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10)
                            ,(int)context.getResources().getDimension(R.dimen.margin_10));
                    ivh.mImagePB.setVisibility(View.GONE);
                    if(Patterns.WEB_URL.matcher(savedNotifications.get(itemIndex).getUrl()).matches()){
                        ivh.videoOverlay.setVisibility(View.VISIBLE);
                        ivh.webViewImage.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(savedNotifications.get(itemIndex).getText())) {
                            ivh.notificationText.setText(savedNotifications.get(itemIndex).getText());
                        } else {
                            ivh.notificationText.setVisibility(View.GONE);
                        }

                        final String url = savedNotifications.get(itemIndex).getUrl();
                        Log.i(TAG, "Actual youtube URL -> " + url);
                        String videoId = "";
                        if(url.contains("https://www.youtube.com/embed/")){
                            videoId = url.replace("https://www.youtube.com/embed/", "");
                        }else if(url.contains("http://www.youtube.com/embed/")){
                            videoId = url.replace("http://www.youtube.com/embed/", "");
                        }else if(url.contains("http://www.youtube.com/watch?v=")){
                            videoId = url.replace("http://www.youtube.com/watch?v=", "");
                        }else if(url.contains("https://www.youtube.com/watch?v=")){
                            videoId = url.replace("https://www.youtube.com/watch?v=", "");
                        }
                        String thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
                        Log.i(TAG, "Notification Video Thumbnail URL ->" + thumbnailUrl);
                        if (!TextUtils.isEmpty(savedNotifications.get(itemIndex).getUrl())) {
                            ivh.notificationImage.setVisibility(View.VISIBLE);
                            Picasso.get().load(thumbnailUrl)
                                    .placeholder(R.color.gray_300)
                                    .resize(600,400)
                                    .into(ivh.notificationImage);
                        } else {
                            ivh.webViewImage.setVisibility(View.GONE);
                        }

                        ivh.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new EventAnalyticsHelper().ItemClickEvent(context, Constants.AnaylticsClassName.NotificationScreen,
                                        newNotifications.get(itemIndex), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.notoficationClick.toString());
                                try {
                                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    youtubeIntent.putExtra("force_fullscreen", true);
                                    context.startActivity(youtubeIntent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else{
                        ivh.notificationTime.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                                ,0
                                ,(int)context.getResources().getDimension(R.dimen.margin_10)
                                ,(int)context.getResources().getDimension(R.dimen.margin_10));
                        ivh.notificationText.setPadding((int)context.getResources().getDimension(R.dimen.margin_10)
                                ,(int)context.getResources().getDimension(R.dimen.margin_10)
                                ,(int)context.getResources().getDimension(R.dimen.margin_10)
                                ,0);
                        ivh.videoOverlay.setVisibility(View.GONE);
                        ivh.webViewImage.setVisibility(View.GONE);
                        ivh.notificationImage.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(savedNotifications.get(itemIndex).getText())) {
                            ivh.notificationText.setText(savedNotifications.get(itemIndex).getText());
                        } else {
                            ivh.notificationText.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder,
                                       int sectionIndex, int headerType) {
        HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;


        if (sectionIndex == 0) {
            if ((newNotifications != null) && (!newNotifications.isEmpty())) {
                if (newNotifications.size() == 1) {
                    hvh.notificationType.setText("1 New Notification");
                } else {
                    hvh.notificationType.setText(newNotifications.size() + " New Notifications");
                }
            } else {
                hvh.notificationType.setText("No New Notifications");
            }
        } else {
            hvh.notificationType.setText("Older Notifications");
        }
    }

    private void setWebView(WebView webView, final String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(TAG, "onPageStarted: ");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "shouldOverrideUrlLoading: ");
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "onPageFinished: " + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        webView.setOnTouchListener(new View.OnTouchListener() {

            public final static int FINGER_RELEASED = 0;
            public final static int FINGER_TOUCHED = 1;
            public final static int FINGER_DRAGGING = 2;
            public final static int FINGER_UNDEFINED = 3;

            private int fingerState = FINGER_RELEASED;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch!");
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (fingerState != FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;

                            String urlToOpen = url;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            context.startActivity(i);

                        } else if (fingerState == FINGER_DRAGGING)
                            fingerState = FINGER_RELEASED;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING)
                            fingerState = FINGER_DRAGGING;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    default:
                        fingerState = FINGER_UNDEFINED;
                }

                return false;
            }
        });
    }

    private void handleClicURL(String URL, TextView titleView){
        try {
            if (URL.contains("https://www.clicbrics.com/project/")) {
                String projectIdStr = UtilityMethods.getProjectIdFromURL(URL);
                String projectId = projectIdStr.substring(projectIdStr.lastIndexOf('-') + 1, projectIdStr.length());
                Intent intent = new Intent(context, ProjectDetailsScreen.class);
                intent.putExtra("ISDirectCall", true);
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, Long.valueOf(projectId));
                context.startActivity(intent);
            }else if(URL.contains("https://www.clicbrics.com/city/")){
                showAllCityProperty(URL,titleView);
            } else {
                Intent intent = new Intent(context, NotificationDetailsWebviewActivity.class);
                if (titleView != null && !TextUtils.isEmpty(titleView.getText().toString())) {
                    intent.putExtra("Title", titleView.getText().toString());
                }
                intent.putExtra("URL", URL);
                context.startActivity(intent);
            }
        } catch (Exception ex) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URL));
            context.startActivity(i);
        }
    }

    private void showAllCityProperty(String url,TextView titleView){
        try {
            if(context == null){
                return;
            }
            if(!UtilityMethods.isInternetConnected(context)){
                Toast.makeText(context,context.getResources().getString(R.string.no_internet_connection),Toast.LENGTH_LONG);
                return;
            }
            String cityName = "";
            long cityId=0;
            String regExp = "https://www.clicbrics.com/city/";
            String[] split = url.toString().split(regExp);
            if(split[1].contains("-")){
                cityName  = split[1].substring(0,split[1].lastIndexOf("-"));
                cityId = Long.parseLong(split[1].substring((split[1].lastIndexOf("-")+1),split[1].length()));
            }
            Intent intent = new Intent(context, HomeScreen.class);
            intent.putExtra(Constants.IntentKeyConstants.SHOW_PROPERTY_BY_CITY,true);
            if(cityId != -1 & cityId != 0){
                intent.putExtra("city_id",cityId);
                UtilityMethods.saveLongInPref(context,Constants.AppConstants.SAVED_CITY_ID,cityId);
            }
            if(!TextUtils.isEmpty(cityName)){
                UtilityMethods.saveStringInPref(context,Constants.AppConstants.SAVED_CITY,cityName);
                intent.putExtra("cityName",cityName);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ((NotificationActivity)context).finish();
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(context, NotificationDetailsWebviewActivity.class);
            if (titleView != null && !TextUtils.isEmpty(titleView.getText().toString())) {
                intent.putExtra("Title", titleView.getText().toString());
            }
            intent.putExtra("URL", url);
            context.startActivity(intent);
        }
    }

}
