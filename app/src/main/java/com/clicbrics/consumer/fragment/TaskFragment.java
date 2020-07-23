package com.clicbrics.consumer.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.taskEndPoint.TaskEndPoint;
import com.buy.housing.backend.taskEndPoint.model.CommonResponse;
import com.buy.housing.backend.taskEndPoint.model.Task;
import com.buy.housing.backend.taskEndPoint.model.TimeLineStats;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.Agent;
import com.clicbrics.consumer.EndPointBuilder;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.TaskActivity;
import com.clicbrics.consumer.adapter.TaskDetailAdapter;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.clicbrics.consumer.utils.UtilityMethods.getLongInPref;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends BaseFragment {

    private final String TAG = TaskFragment.class.getSimpleName();

    private LinearLayout parentLayout;
    RecyclerView recyclerView;
    TextView empty_view, agentName;
    UserEndPoint userEndPoint;
    ImageView agentPhoto;//, callAgent, textAgent, mailAgent;
    String phoneNumber, emailId;
    TaskEndPoint taskEndPoint;
    FloatingActionButton callButton;
    ProgressBar mImagePB;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildUserWebService();
        initView(view);
        //getMyTaskDetails();
        getSiteAgent();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Add your menu entries here
        inflater.inflate(R.menu.menu_task, menu);
        MenuItem cancelMenu = menu.findItem(R.id.cancel_visit);
        if (!TextUtils.isEmpty(TaskActivity.timelineStatus)) {
            if (TaskActivity.timelineStatus.equalsIgnoreCase("cancel") ||
                    TaskActivity.timelineStatus.equalsIgnoreCase("closed") ||
                    TaskActivity.timelineStatus.equalsIgnoreCase("done")) {
                cancelMenu.setVisible(false);
            } else {
                cancelMenu.setVisible(true);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel_visit:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.confirm));
                builder.setMessage(R.string.cancel_sitvisit_confimation_msg);
                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelVisit();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                                .setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_600));
                    }
                });
                alertDialog.show();
                break;
        }
        return true;

    }*/

    private void initView(View view) {
        parentLayout = (LinearLayout) view.findViewById(R.id.root_layout_my_task);
        recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        empty_view = (TextView) view.findViewById(R.id.empty_view_task);
        agentName = (TextView) view.findViewById(R.id.agent_name);
        //callAgent = (ImageView) view.findViewById(R.id.call_agent);
        //textAgent = (ImageView) view.findViewById(R.id.message_agent);
        //mailAgent = (ImageView) view.findViewById(R.id.email_agent);
        agentPhoto = (ImageView) view.findViewById(R.id.agent_image);
        mImagePB = (ProgressBar) view.findViewById(R.id.id_agent_image_pb);
        callButton = (FloatingActionButton) view.findViewById(R.id.id_taskdetail_call_button);

    }

    private void buildUserWebService() {
        userEndPoint = EndPointBuilder.getUserEndPoint();
        taskEndPoint = EndPointBuilder.getTaskEndPoint();
    }

    private void setData(Agent agent) {

        long userId = UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0l);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArrayList<String> timelineDescription = new ArrayList<>();
        ArrayList<String> timeStamp = new ArrayList<>();

        if (TaskActivity.timeline != null && !TaskActivity.timeline.isEmpty()) {
            timelineDescription = TaskActivity.timeline;
            timeStamp = TaskActivity.timeStamp;
        }

        if (timelineDescription != null && !timelineDescription.isEmpty()) {
            recyclerView.setAdapter(new TaskDetailAdapter(getActivity(), timelineDescription, timeStamp));
        }
        recyclerView.setFocusable(false);
        if (!TextUtils.isEmpty(agent.getName())) {
            agentName.setText(agent.getName());
        }

        emailId = agent.getEmail();
        phoneNumber = agent.getPhoneOfficial();
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialer(phoneNumber);
            }
        });
        /*callAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialer(phoneNumber);
            }
        });
        textAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageSender(phoneNumber);
            }
        });
        mailAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMailSender(emailId);
            }
        });*/

//        Log.d(TAG, "agentPhotoUrl->" + agent.getPhoto().getUrl());
        //Picasso.with(getActivity()).load(agent.getPhoto().getUrl()).into(agentPhoto);
        if ((agent != null) && (agent.getPhoto() != null) && (!TextUtils.isEmpty(agent.getPhoto().getUrl()))){
            //Glide.with(getActivity()).load(agent.getPhoto().getUrl()).into(agentPhoto);
            mImagePB.setVisibility(View.VISIBLE);
            Picasso.get().load(agent.getPhoto().getUrl())
                    .placeholder(R.drawable.ic_person)
                    .into(agentPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            mImagePB.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            mImagePB.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void cancelVisit() {

        if (!UtilityMethods.isInternetConnected(getActivity())) {
            Snackbar snackbar = Snackbar
                    .make(parentLayout, getResources().getString(R.string.network_error_msg),
                            Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cancelVisit();
                        }
                    });
            ;

            snackbar.show();
            return;
        }
        showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {

                    ArrayList<String> timeline = new ArrayList<String>();
                    ArrayList<String> timeStamp = new ArrayList<String>();
                    Task task = new Task();
                    //task.setRequestTime(System.currentTimeMillis());
                    Log.d(TAG, "taskId->" + getActivity().getIntent().getStringExtra("taskId"));
                    task.setId(getActivity().getIntent().getStringExtra("taskId"));
                    task.setStatus("cancel");
                    TimeLineStats tls = new TimeLineStats();

                    tls.setUserId(getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, -1));
                    tls.setAgentName(getActivity().getIntent().getStringExtra("agentName"));
                    tls.setDescription("Cancelled by user.");
                    tls.setTime(System.currentTimeMillis());
                    List<TimeLineStats> timeLineStatsList = new ArrayList<TimeLineStats>();


                    if (getActivity().getIntent().hasExtra("timeline")) {
                        timeline = getActivity().getIntent().getStringArrayListExtra("timeline");
                    }

                    if (getActivity().getIntent().hasExtra("timeStamp")) {
                        timeStamp = getActivity().getIntent().getStringArrayListExtra("timeStamp");
                    }
                    if (timeline != null && !timeline.isEmpty()) {
                        for (int i = 0; i < timeline.size(); i++) {
                            TimeLineStats tl = new TimeLineStats();
                            tl.setTime(Long.parseLong(timeStamp.get(i)));
                            tl.setDescription(timeline.get(i));
                            timeLineStatsList.add(tl);
                        }

                        TimeLineStats tlStats = new TimeLineStats();
                        tlStats.setTime(System.currentTimeMillis());
                        tlStats.setDescription("Site visit cancelled");
                        timeLineStatsList.add(tlStats);

                        task.setTimeLineStates(timeLineStatsList);
                    }
                    final CommonResponse updateTask
                            = taskEndPoint.updateTask(task)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (updateTask.getStatus()) {
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                dismissProgressBar();
                                Intent intent = new Intent();
                                intent.putExtra("SiteVisitCancelMsg", "Your scheduled visit has been cancelled!");
                                getActivity().setResult(getActivity().RESULT_OK, intent);
                                getActivity().finish();
                                /*Snackbar snackbar = Snackbar.make(parentLayout, "Your scheduled visit has been cancelled!",
                                        Snackbar.LENGTH_LONG);
                                snackbar.setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                    }
                                });
                                snackbar.show();*/
// setProjectValues();
                            }
                        });
                    } else {
                        errorMsg = updateTask.getErrorMessage();
                        Log.d(TAG, "err->" + errorMsg);
                    }
                } catch (UnknownHostException e) {
                    errorMsg = getString(R.string.network_error_msg);
                    Log.d(TAG, "errEx->" + errorMsg);
                    e.printStackTrace();
                }catch (Exception e) {
                    errorMsg = getString(R.string.something_went_wrong);
                    Log.d(TAG, "errEx->" + errorMsg);
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;
                    mHandler.post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void run() {
                            dismissProgressBar();
                            UtilityMethods.showSnackBar(parentLayout, errmsg, Snackbar.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void getSiteAgent() {

        if (!UtilityMethods.isInternetConnected(getActivity())) {
            Snackbar snackbar = Snackbar
                    .make(parentLayout, getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_INDEFINITE)
                    .setAction("TRY AGAIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getSiteAgent();
                        }
                    });
            snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));
            snackbar.show();
            return;
        }

        showProgressBar();
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    HousingLogs.i(TAG, "User Id " + getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0L)
                            + "\nagentId->" + TaskActivity.agentId);
                    if (TaskActivity.agentId != null && TaskActivity.agentId != 0) {
                        long agentId = TaskActivity.agentId;
                        final Agent agent = userEndPoint
                                .getAgentById(getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0L),
                                        agentId)
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();


                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                dismissProgressBar();
                                Log.d(TAG, "agent->" + agent.getName());
                                if (agent != null)
                                    setData(agent);
                            }
                        });
                    } else {
                        errorMsg = "No agent found";
                    }

                } catch (Exception e) {
                    dismissProgressBar();
                    e.printStackTrace();
                    errorMsg = getResources().getString(R.string.something_went_wrong);
//                    AnalyticsTrackers.trackException(e);
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            UtilityMethods.showSnackBar(parentLayout, errmsg, Snackbar.LENGTH_LONG);
                            //    recyclerView.setVisibility(View.GONE);
                            //    empty_view.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

        }).start();
    }

    private void showDialer(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void showMessageSender(String number) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + number));
        sendIntent.putExtra("address", number);
        startActivity(sendIntent);
    }


    private void showMailSender(String email) {
        Log.d(TAG, "email->" + email);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getTitle());
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private String getDateTime(long time) {
        String date = DateFormat.format("dd MMM yyyy hh:mm a", time).toString();
        return date;
    }
}

