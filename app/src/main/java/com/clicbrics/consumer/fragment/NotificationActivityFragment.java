package com.clicbrics.consumer.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.UserNotification;
import com.buy.housing.backend.userEndPoint.model.UserNotificationCollection;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.StickyHeaderLayoutManager;
import com.clicbrics.consumer.adapter.NotificationListAdapter;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.Notificaiton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A placeholderf fragment containing a simple view.
 */
public class NotificationActivityFragment extends BaseFragment {
    private final String TAG = NotificationActivityFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayout emptyView;
    private UserEndPoint mUserEndPoint;
    private RelativeLayout parentLayout;

    public NotificationActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        buildUserWebService();
        getNotificationList();
    }

    private void buildUserWebService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }

    private void initView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_notification);
        emptyView = (LinearLayout) v.findViewById(R.id.empty_view_notification);
        parentLayout = (RelativeLayout) v.findViewById(R.id.id_fragment_notifications);
    }

    private void getNotificationList() {
        if (!UtilityMethods.isInternetConnected(mActivity)) {
            new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.NotificationScreen,
                    null, Constants.ApiName.getUserNotificationList.toString(),Constants.AnalyticsEvents.FAILED,
                    getResources().getString(R.string.network_error_msg));
            Snackbar snackbar = Snackbar
                    .make(parentLayout, getString(R.string.network_error_msg), Snackbar.LENGTH_INDEFINITE)
                    .setAction("TRY AGAIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getNotificationList();
                        }
                    });

            snackbar.setActionTextColor(Color.WHITE);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));
            snackbar.show();
            return;
        }
        //showProgressBar();

        getActivity().findViewById(R.id.indeterminate_progress).setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    long userId = UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, -1);
                    long timestamp = UtilityMethods.getLongInPref(getActivity(), Constants.SharedPreferConstants.TIME_STAMP, 0);
                    UtilityMethods.saveBooleanInPref(getActivity(),Constants.SharedPreferConstants.IS_NOTIFICATION_SEEN,true);

                    removeExpiredNotifications(getActivity());

                    HousingLogs.i(TAG, "User Id " + userId);

                    UserNotificationCollection getUserNotificationList;
                    if (timestamp != 0) {
                        getUserNotificationList = mUserEndPoint.getUserNotificationList(userId)
                                .setTime(UtilityMethods.getLongInPref(getActivity(), Constants.SharedPreferConstants.TIME_STAMP, 0))
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    } else {
                        getUserNotificationList = mUserEndPoint.getUserNotificationList(userId)
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    }

                    final ArrayList<Notificaiton> savedList = getNotifications(getActivity());
                    if (((getUserNotificationList != null) && (getUserNotificationList.getItems() != null)
                            && (!getUserNotificationList.getItems().isEmpty()))
                            || ((savedList != null) && (!savedList.isEmpty()))) {
                        new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.NotificationScreen,
                                null, Constants.ApiName.getUserNotificationList.toString(),Constants.AnalyticsEvents.SUCCESS,
                                null);
                        List<UserNotification> serverNotificaitonList = getUserNotificationList.getItems();

                        UtilityMethods.saveLongInPref(getActivity(), Constants.SharedPreferConstants.TIME_STAMP,
                                System.currentTimeMillis());
                        try {
                            final ArrayList<Notificaiton> notificationList = new ArrayList<>();
                            if ((serverNotificaitonList != null) && (!serverNotificaitonList.isEmpty())) {
                                for (int i = 0; i < serverNotificaitonList.size(); i++) {

                                    Notificaiton notificaiton = new Notificaiton();
                                    notificaiton.setType(serverNotificaitonList.get(i).getType());
                                    notificaiton.setClickUrl(serverNotificaitonList.get(i).getClickUrl());
                                    notificaiton.setTime(serverNotificaitonList.get(i).getTime());
                                    notificaiton.setText(serverNotificaitonList.get(i).getText());
                                    notificaiton.setId(serverNotificaitonList.get(i).getId());
                                    notificaiton.setUid(serverNotificaitonList.get(i).getUid());
                                    notificaiton.setUrl(serverNotificaitonList.get(i).getUrl());
                                    if ((serverNotificaitonList.get(i).getStartTime() != null) && (serverNotificaitonList.get(i).getStartTime() != 0)) {
                                        notificaiton.setStartTime(serverNotificaitonList.get(i).getStartTime());
                                        Log.d(TAG,"startTimeToSave->"+serverNotificaitonList.get(i).getStartTime());
                                    }
                                    if ((serverNotificaitonList.get(i).getEndTime() != null) && (serverNotificaitonList.get(i).getEndTime() != 0)) {
                                        notificaiton.setEndTime(serverNotificaitonList.get(i).getEndTime());
                                    }

                                    if ((savedList != null) && (!savedList.isEmpty())) {
                                        boolean found = false;
                                        for (Notificaiton savedNotification : savedList) {
                                            if (savedNotification.getId().longValue() == notificaiton.getId().longValue()) {
                                                Log.d(TAG, "type->" + savedNotification.getType() + " " + notificaiton.getType() +
                                                        "savedId->" + savedNotification.getId() + " new->" + notificaiton.getId());
                                                found = true;
                                                break;
                                            } else {
                                                Log.d(TAG, "~ type->" + savedNotification.getType() + " " + notificaiton.getType() +
                                                        "savedId->" + savedNotification.getId() + " new->" + notificaiton.getId());
                                            }
                                        }
                                        if (!found) {
                                            notificationList.add(notificaiton);
                                        }
                                    } else {
                                        Log.d(TAG, "savedlist is null");
                                        notificationList.add(notificaiton);
                                    }
                                }
                            }

                            final UserNotificationCollection collection = getUserNotificationList;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(notificationList != null && notificationList.size() > 1) {
                                        Collections.sort(notificationList, new Comparator<Notificaiton>() {
                                            @Override
                                            public int compare(Notificaiton notificaiton1, Notificaiton notificaiton2) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    return Long.compare(notificaiton2.getTime(),notificaiton1.getTime());
                                                }else{
                                                    return (int)(notificaiton2.getTime()-notificaiton1.getTime());
                                                }
                                            }
                                        });
                                    }
                                    if(savedList != null && savedList.size() > 1) {
                                        Collections.sort(savedList, new Comparator<Notificaiton>() {
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public int compare(Notificaiton notificaiton1, Notificaiton notificaiton2) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    return Long.compare(notificaiton2.getTime(),notificaiton1.getTime());
                                                }else{
                                                    return (int)(notificaiton2.getTime()-notificaiton1.getTime());
                                                }
                                            }
                                        });
                                    }
                                    getActivity().findViewById(R.id.indeterminate_progress).setVisibility(View.GONE);
                                    //dismissProgressBar();
                                    mRecyclerView.setLayoutManager(new StickyHeaderLayoutManager());
                                    mRecyclerView.setNestedScrollingEnabled(false);
                                    mRecyclerView.setAdapter(new NotificationListAdapter(getActivity(),
                                            //(ArrayList<UserNotification>) collection.getItems(), getNotifications(getActivity())));
                                            notificationList, savedList));

                                    addToSavedNotifications(getActivity(), notificationList);
                                }
                            });

                        } catch (Exception ex) {
                            getActivity().findViewById(R.id.indeterminate_progress).setVisibility(View.GONE);
                            //dismissProgressBar();
                            ex.printStackTrace();
                        }
                    } else {
                        errorMsg = getResources().getString(R.string.no_notifications);
                    }
                } catch (UnknownHostException e) {
                    errorMsg = "Please check network connection.";
                    e.printStackTrace();
                }catch (Exception e) {
                    errorMsg = "Something went wrong.\nPlease try again.";
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    Log.i(TAG, "getNotificationList : Error " + errorMsg);
                    final String errmsg = errorMsg;
                    new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.NotificationScreen,
                            null, Constants.ApiName.getUserNotificationList.toString(),Constants.AnalyticsEvents.FAILED,
                            errmsg);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //dismissProgressBar();
                            try {
                                if(getActivity() != null) {
                                    getActivity().findViewById(R.id.indeterminate_progress).setVisibility(View.GONE);
                                }
                                if(errmsg.equalsIgnoreCase(getResources().getString(R.string.no_notifications))){
                                    //UtilityMethods.showSnackBar(parentLayout, errmsg, Snackbar.LENGTH_LONG);
                                    mRecyclerView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                }else{
                                    UtilityMethods.showErrorSnackBar(parentLayout, errmsg, Snackbar.LENGTH_LONG);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public static ArrayList<Notificaiton> getNotifications(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.NOTIFICATIONS, "");
        Type type = new TypeToken<ArrayList<Notificaiton>>() {
        }.getType();

        ArrayList<Notificaiton> list = gson.fromJson(json, type);
        if ((list != null) && (!list.isEmpty())) {
            Collections.reverse(list);
        }
        return list;
    }

    public static void addToSavedNotifications(Context context, ArrayList<Notificaiton> newNotificaitons) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.NOTIFICATIONS, "");
        Type type = new TypeToken<List<Notificaiton>>() {
        }.getType();

        List<Notificaiton> notificaitonList = gson.fromJson(json, type);

        if (notificaitonList == null || notificaitonList.isEmpty()) {
            notificaitonList = new ArrayList<>();
        }

        notificaitonList.addAll(newNotificaitons);

        SharedPreferences.Editor prefsEditor = prefs.edit();
        gson = new Gson();
        json = gson.toJson(notificaitonList);
        prefsEditor.putString(Constants.AppConstants.NOTIFICATIONS, json);
        prefsEditor.apply();
    }

    public void removeExpiredNotifications(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.NOTIFICATIONS, "");
        Type type = new TypeToken<List<Notificaiton>>() {
        }.getType();

        List<Notificaiton> notificationList = gson.fromJson(json, type);
        UtilityMethods.clearPreference(getActivity(), Constants.AppConstants.NOTIFICATIONS);

        List<Notificaiton> notExpiredNotifications = new ArrayList<>();
        if (notificationList == null || notificationList.isEmpty()) {
            return;
        } else {

            for (Notificaiton toRemove : notificationList) {
                if ((toRemove.getStartTime() != null) && (toRemove.getStartTime() != 0)) {
                    if (toRemove.getEndTime() > System.currentTimeMillis()) {
                        notExpiredNotifications.add(toRemove);
                    }
                } else {
                    notExpiredNotifications.add(toRemove);
                }
            }
        }

        SharedPreferences.Editor prefsEditor = prefs.edit();
        gson = new Gson();
        json = gson.toJson(notExpiredNotifications);

        prefsEditor.putString(Constants.AppConstants.NOTIFICATIONS, json);
        prefsEditor.apply();
    }
}
