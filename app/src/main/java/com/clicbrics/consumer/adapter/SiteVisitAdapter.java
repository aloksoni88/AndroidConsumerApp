package com.clicbrics.consumer.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.buy.housing.backend.taskEndPoint.TaskEndPoint;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.Task;
import com.buy.housing.backend.userEndPoint.model.TimeLineStats;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.view.activity.SiteVisitsActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.SectioningAdapter;
import com.clicbrics.consumer.activities.TaskActivity;
import com.clicbrics.consumer.customview.CustomProgressDialog;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by root on 15/12/16.
 */
public class SiteVisitAdapter extends SectioningAdapter {

    private final String TAG = SiteVisitAdapter.class.getSimpleName();

    Context mContext;
    List<Task> taskList;
    LinkedHashMap<String, List<CityData>> cityMap;
    List<String> cities;
    FrameLayout parentLayout;
    UserEndPoint mUserEndPoint;
    TaskEndPoint taskEndPoint;

    private class CityData {
        String cityName;
        String projectName;
        String status;
        String agentName;
        long dateTime;
        Long agentId;
        String taskId;
        ImageView calendarImage;
        List<TimeLineStats> timeLineStates;
    }

    public SiteVisitAdapter(Context activity, List<Task> taskList, FrameLayout parentLayout, UserEndPoint userEndPoint) {
        this.mContext = activity;
        this.taskList = taskList;
        this.parentLayout = parentLayout;
        mUserEndPoint = userEndPoint;

        buildRegService();

        cityMap = new LinkedHashMap<>();
        cities = new ArrayList<>();

        if (taskList != null) {

            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                CityData cityData = new CityData();
                if(task != null){
                    if (!TextUtils.isEmpty(task.getCity())) {
                        Log.d(TAG, "city->" + task.getCity());
                        cityData.cityName = task.getCity();
                    }

                    if (!TextUtils.isEmpty(task.getProjectName())) {
                        cityData.projectName = task.getProjectName();
                    }
                }

                cityData.status = task.getStatus();
                if (task.getRequestTime() != null) {
                    cityData.dateTime = task.getRequestTime();
                }

                cityData.agentId = task.getAssignedId();
                cityData.timeLineStates = task.getTimeLineStates();

                if (!TextUtils.isEmpty(task.getId()))
                    cityData.taskId = task.getId();

                if (!TextUtils.isEmpty(task.getAssignedName()))
                    cityData.agentName = task.getAssignedName();

                if (cityMap == null ||
                        !cityMap.containsKey(cityData.cityName)) {
                    List<CityData> cityDataList = new ArrayList<>();
                    cityDataList.add(cityData);
                    if(cityData.cityName == null){
                        cityData.cityName = "Other";
                    }
                    cityMap.put(cityData.cityName, cityDataList);
                    cities.add(cityData.cityName);
                } else {
                    if(cityData.cityName == null){
                        cityData.cityName = "Other";
                    }
                    cityMap.get(cityData.cityName).add(cityData);
                }
            }

            /*for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                CityData cityData = new CityData();
                if ((task.getProjectList() != null) && (!task.getProjectList().isEmpty())) {
                    Log.d(TAG, "city->" + task.getProjectList().get(0).getCity());

                    if (!TextUtils.isEmpty(task.getProjectList().get(0).getCity())) {
                        cityData.cityName = task.getProjectList().get(0).getCity();
                    }

                    if (!TextUtils.isEmpty(task.getProjectList().get(0).getName())) {
                        cityData.projectName = task.getProjectList().get(0).getName();
                    }
                }



                cityData.status = task.getStatus();
                if (task.getRequestTime() != null) {
                    cityData.dateTime = task.getRequestTime();
                }

                cityData.agentId = task.getAssignedId();
                cityData.timeLineStates = task.getTimeLineStates();

                if (!TextUtils.isEmpty(task.getId()))
                    cityData.taskId = task.getId();

                if (!TextUtils.isEmpty(task.getAssignedName()))
                    cityData.agentName = task.getAssignedName();

                if (cityMap == null ||
                        !cityMap.containsKey(cityData.cityName)) {
                    List<CityData> cityDataList = new ArrayList<>();
                    cityDataList.add(cityData);
                    if(cityData.cityName == null){
                        cityData.cityName = "Other";
                    }
                    cityMap.put(cityData.cityName, cityDataList);
                    cities.add(cityData.cityName);
                } else {
                    if(cityData.cityName == null){
                        cityData.cityName = "Other";
                    }
                    cityMap.get(cityData.cityName).add(cityData);
                }
            }*/
        }
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
        TextView projectName, status, reschedule, date, time;
        ImageView calendarIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            projectName = (TextView) itemView.findViewById(R.id.project_site_visit);
            //status = (TextView) itemView.findViewById(R.id.status_site_visit);
            reschedule = (TextView) itemView.findViewById(R.id.reschedule_site_visit);
            date = (TextView) itemView.findViewById(R.id.date_site_visit);
            time = (TextView) itemView.findViewById(R.id.time_site_visit);
            //calendarIcon = (ImageView) itemView.findViewById(R.id.icon_site_visit);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        TextView cityName;//, projectCount;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            cityName = (TextView) itemView.findViewById(R.id.city_name_txt);
            //projectCount = (TextView) itemView.findViewById(R.id.city_project_count);
        }
    }

    @Override
    public int getNumberOfSections() {
        return cityMap.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        //return sections.get(sectionIndex).people.size();
        return cityMap.get(cities.get(sectionIndex)).size();
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
        View v = inflater.inflate(R.layout.item_content_my_site_visits, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_header_my_site_visits, parent, false);
        return new HeaderViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        ItemViewHolder ivh = (ItemViewHolder) viewHolder;
        final CityData data = cityMap.get(cities.get(sectionIndex)).get(itemIndex);
        if (!TextUtils.isEmpty(data.projectName)) {
            ivh.projectName.setText("" + data.projectName);
        }
        if (data.dateTime != 0) {
            ivh.date.setText("" + getDate(data.dateTime));
            ivh.time.setText("" + getTime(data.dateTime));
        }

        if (data.status.equalsIgnoreCase("cancel")) {
            //ivh.status.setText("Visit Cancelled");
            ivh.reschedule.setText(R.string.cancelled);
            UtilityMethods.setTextViewColor(mContext, ivh.reschedule, R.color.light_grey);
        } else if (data.status.equalsIgnoreCase("closed")) {
            ivh.reschedule.setText(R.string.closed);
            UtilityMethods.setTextViewColor(mContext, ivh.reschedule, R.color.light_grey);
        } else if (data.status.equalsIgnoreCase("done")) {
            ivh.reschedule.setText(R.string.done);
            UtilityMethods.setTextViewColor(mContext, ivh.reschedule, R.color.light_grey);
        } else {
            ivh.reschedule.setText(R.string.scheduled);
            UtilityMethods.setTextViewColor(mContext, ivh.reschedule, R.color.light_grey);
            /*UtilityMethods.setTextViewColor(mContext, ivh.reschedule, R.color.colorAccent);
            ((ItemViewHolder) viewHolder).reschedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CalendarActivity.class);
                    intent.putExtra("timeUpdate", data.dateTime);
                    intent.putExtra("taskId", data.taskId);
                    intent.putExtra("agentName", data.agentName);
                    ArrayList<String> timelineData = new ArrayList<String>();
                    ArrayList<String> timeStamp = new ArrayList<String>();
                    if ((data.timeLineStates != null) && (!data.timeLineStates.isEmpty())) {

                        for (int i = 0; i < data.timeLineStates.size(); i++) {
                            timelineData.add(data.timeLineStates.get(i).getDescription());
                            timeStamp.add((data.timeLineStates.get(i).getTime()).toString());
                        }
                        intent.putExtra("timeline", timelineData);
                        intent.putExtra("timeStamp", timeStamp);
                    }
                    //     mContext.startActivity(intent);

                    setDateTimePicker(data.taskId, data.dateTime, data.agentName, timelineData, timeStamp);
                }
            });*/

            ((ItemViewHolder) viewHolder).reschedule.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
        }

        ivh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UtilityMethods.isInternetConnected(mContext)){
                    UtilityMethods.showErrorSnackbarOnTop((SiteVisitsActivity)mContext);
                    return;
                }
                Intent intent = new Intent(mContext, TaskActivity.class);
                if (!TextUtils.isEmpty(data.projectName))
                    intent.putExtra("project", data.projectName);
                if (data.agentId != null) {
                    Log.d(TAG, "agentId->" + data.agentId);
                    intent.putExtra("agentId", data.agentId);
                }
                if (data.dateTime != 0) {
                    intent.putExtra("timeUpdate", data.dateTime);
                }

                if (!TextUtils.isEmpty(data.taskId)) {
                    intent.putExtra("taskId", data.taskId);
                }

                if ((data.timeLineStates != null) && (!data.timeLineStates.isEmpty())) {
                    ArrayList<String> timelineData = new ArrayList<String>();
                    ArrayList<String> timeStamp = new ArrayList();
                    String timeLineStatus = "";
                    for (int i = 0; i < data.timeLineStates.size(); i++) {
                        timelineData.add(data.timeLineStates.get(i).getDescription());
                        timeStamp.add((data.timeLineStates.get(i).getTime()).toString());
                    }
                    timeLineStatus = data.status;
                    intent.putExtra("timeline", timelineData);
                    intent.putExtra("timeStamp", timeStamp);
                    intent.putExtra("timelineStatus", timeLineStatus);
                }
                ((SiteVisitsActivity) mContext).startActivityForResult(intent, 101);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
        hvh.cityName.setText("" + cities.get(sectionIndex));
        /*if (cityMap.get(cities.get(sectionIndex)).size() == 1)
            hvh.projectCount.setText(cityMap.get(cities.get(sectionIndex)).size() + " Project");
        else
            hvh.projectCount.setText(cityMap.get(cities.get(sectionIndex)).size() + " Projects");*/
        //hvh.itemView.setBackgroundColor(0x55ffffff);
        //hvh.titleTextView.setText(pad(sectionIndex * 2) + s.alpha);
        //hvh.titleTextView.setText(s.alpha);
    }


    private String getDate(long date) {
        String dayStr = UtilityMethods.getFormatedDay(date);
        String dateStr = DateFormat.format("MMM yyyy", date).toString();
        String formatedDate = dayStr + " " + dateStr;
        return formatedDate;
    }

    private String getTime(long time) {
        String timeStr = DateFormat.format("hh:mm a", time).toString();
        return timeStr;
    }

    private void setDateTimePicker(final String taskId, final Long datetime, final String agentName,
                                   final ArrayList<String> timelineData, final ArrayList<String> timeStamp) {

        if (UtilityMethods.getLongInPref(mContext, Constants.ServerConstants.USER_ID, -1) == -1) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
        } else {

            final int mYear, mMonth, mDay, mHour, mMinute;
            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);


            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, final int year,
                                              final int monthOfYear, final int dayOfMonth) {

                            Log.d(TAG, "date->" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay,
                                                              int minute) {
                                            Log.d(TAG, "time->" + hourOfDay + ":" + minute);
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                            final Long timestamp = calendar.getTimeInMillis();
                                            //bookAVisit(id, year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                            rescheduleTask(taskId, datetime, agentName, timelineData, timeStamp, timestamp);
                                        }
                                    }, mHour, mMinute, false);
                            timePickerDialog.show();

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

        }
    }


    private String getDateTime(long time) {
        //String date = DateFormat.format("dd MMM yyyy hh:mm a", time).toString();
        String date = DateFormat.format("dd MMM yyyy", time).toString();
        return date;
    }

    CustomProgressDialog customProgressDialog;

    private void rescheduleTask(final String taskId, final Long datetime, final String agentName,
                                final ArrayList<String> timelineData, final ArrayList<String> timeStampList, final Long timestamp) {
        if (!UtilityMethods.isInternetConnected(mContext)) {
            Snackbar snackbar = Snackbar
                    .make(parentLayout, mContext.getResources().getString(R.string.network_error_msg),
                            Snackbar.LENGTH_INDEFINITE).setAction(mContext.getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rescheduleTask(taskId, datetime, agentName, timelineData, timeStampList, timestamp);
                        }
                    });
            ;
            snackbar.setActionTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.uber_red));
            snackbar.show();
            return;
        }
        customProgressDialog = new CustomProgressDialog(mContext);
        customProgressDialog.show();
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    if (timestamp == 0) {
                        return;
                    }

                    ArrayList<String> timeline = new ArrayList<String>();
                    ArrayList<String> timeStamp = new ArrayList<String>();
                    com.buy.housing.backend.taskEndPoint.model.Task task = new com.buy.housing.backend.taskEndPoint.model.Task();
                    task.setRequestTime(timestamp);
                    task.setId(taskId);
                    com.buy.housing.backend.taskEndPoint.model.TimeLineStats tls =
                            new com.buy.housing.backend.taskEndPoint.model.TimeLineStats();

                    tls.setUserId(UtilityMethods.getLongInPref(mContext, Constants.ServerConstants.USER_ID, -1));
                    tls.setAgentName(agentName);
                    tls.setDescription("Rescheduled Visit Time to " + getDateTime(timestamp) + " IST");
                    tls.setTime(System.currentTimeMillis());
                    List<com.buy.housing.backend.taskEndPoint.model.TimeLineStats> timeLineStatsList = new ArrayList<com.buy.housing.backend.taskEndPoint.model.TimeLineStats>();

                    if ((timelineData != null) && (!timelineData.isEmpty())) {
                        timeline = timelineData;
                    }

                    if ((timeStampList != null) && (!timeStampList.isEmpty())) {
                        timeStamp = timeStampList;
                    }

                    if (timeline != null && !timeline.isEmpty()) {
                        for (int i = 0; i < timeline.size(); i++) {
                            com.buy.housing.backend.taskEndPoint.model.TimeLineStats tl = new com.buy.housing.backend.taskEndPoint.model.TimeLineStats();
                            tl.setTime(Long.parseLong(timeStamp.get(i)));
                            tl.setDescription(timeline.get(i));
                            timeLineStatsList.add(tl);
                        }
                    }


                    timeLineStatsList.add(tls);
                    task.setTimeLineStates(timeLineStatsList);
                    task.setStatus("open");
                    final com.buy.housing.backend.taskEndPoint.model.CommonResponse updateTask
                            = taskEndPoint.updateTask(task)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (updateTask.getStatus()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                customProgressDialog.dismiss();
                                //dismissProgressBar();
                                Snackbar snackbar = Snackbar.make(parentLayout, "Your Visit has been Rescheduled!",
                                        Snackbar.LENGTH_SHORT);
                                snackbar.show();

                                mCallback.onUpdate();
                            }
                        });
                    } else {
                        errorMsg = updateTask.getErrorMessage();

                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    errorMsg = mContext.getString(R.string.network_error_msg);
                }catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = mContext.getString(R.string.something_went_wrong);
                }

                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            customProgressDialog.dismiss();
                            //dismissProgressBar();
                            UtilityMethods.showSnackBar(parentLayout, errmsg, Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    protected void buildRegService() {
        taskEndPoint = EndPointBuilder.getTaskEndPoint();
    }


    UpdateListener mCallback;

    public void setUpdateListener(UpdateListener mCallback) {
        this.mCallback = mCallback;
    }

    public interface UpdateListener {
        public void onUpdate();
    }
}
