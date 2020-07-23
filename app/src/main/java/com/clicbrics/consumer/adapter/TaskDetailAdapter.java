package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.ArrayList;

/**
 * Created by root on 8/12/16.
 */
public class TaskDetailAdapter extends RecyclerView.Adapter<TaskDetailAdapter.TaskViewHolder> {

    private static final String TAG = TaskDetailAdapter.class.getSimpleName();
    Context context;
    ArrayList<String> description;
    ArrayList<String> timeStamp;

    public TaskDetailAdapter(Context context, ArrayList<String> description, ArrayList<String> timeStamp){
        this.context = context;
        this.description = description;
        this.timeStamp = timeStamp;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView date,time;
        TextView taskdescription;
        TextView taskItemStatus;
        //ConnectorView connector;
        View connector, upperVerticalLine, downVerticalLine;

        public TaskViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.task_item_date_time);
            //time = (TextView) itemView.findViewById(R.id.task_item_time);
            taskItemStatus = (TextView) itemView.findViewById(R.id.task_item_status);
            taskdescription = (TextView)itemView.findViewById(R.id.task_item_detail);
            //connector = (ConnectorView) itemView.findViewById(R.id.task_connector);
            connector = (View) itemView.findViewById(R.id.connector);
            upperVerticalLine = (View) itemView.findViewById(R.id.vertical_connecting_upperline);
            downVerticalLine = (View) itemView.findViewById(R.id.vertical_connecting_downline);
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"CreateViewHolder!");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task_recycler_view2, parent, false);
        return new TaskViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Log.d(TAG,"position->"+position);

        //handle vertical line
        if(position == 0){
            holder.upperVerticalLine.setVisibility(View.GONE);
        }
        if(position == description.size()-1){
            holder.downVerticalLine.setVisibility(View.GONE);
        }

        /*if(position==(description.size()-1)){
            holder.connector.setVisibility(View.GONE);
        }*/

        holder.taskdescription.setText(description.get(position));
        holder.date.setText(getDate(Long.parseLong(timeStamp.get(position))));

        //holder.time.setText(getTime(Long.parseLong(timeStamp.get(position))));
        /*if(position==0)
            holder.connector.setType(ConnectorView.Type.START);
        else if(position==(dummy.length-1))
            holder.connector.setType(ConnectorView.Type.END);
        else
            holder.connector.setType(ConnectorView.Type.NODE);*/
    }

    @Override
    public int getItemCount() {
        return description.size();
    }


    private String getDate(long date) {
        String dayStr = UtilityMethods.getFormatedDay(date);
        String dateStr = DateFormat.format("MMM yyyy, hh:mm a", date).toString();
        String formatedDate = dayStr + " " + dateStr;
        return formatedDate;
    }

    /*private String getTime(long time){
        String timeStr = DateFormat.format("hh:mm a", time).toString();
        return timeStr;
    }*/
}
