package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.model.Task;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.List;

/**
 * Created by Alok on 15-03-2017.
 */

public class ServiceRequestHistoryAdapter extends RecyclerView.Adapter<ServiceRequestHistoryAdapter.RequestViewHolder> {

    private static final String TAG = ServiceRequestHistoryAdapter.class.getSimpleName();

    private Context mContext;
    private List<Task> taskList;
    public ServiceRequestHistoryAdapter(Context context, List<Task> taskList){
        this.mContext = context;
        this.taskList = taskList;
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView reqType, reqComments, reqTime, reqStatus;
        public RequestViewHolder(View itemView) {
            super(itemView);
            reqType = (TextView) itemView.findViewById(R.id.id_req_type);
            reqComments = (TextView) itemView.findViewById(R.id.id_req_comments);
            reqTime = (TextView) itemView.findViewById(R.id.id_req_time);
            reqStatus = (TextView) itemView.findViewById(R.id.id_req_status);
        }
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_history,parent,false);
        return  new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        if(taskList != null && taskList.size() > 0) {
            Task task = taskList.get(position);
            if(task != null) {
                if(task.getServiceRequestType() != null) {
                    if (task.getServiceRequestType().equals(Constants.ServiceRequestType.Booking.toString())) {
                        holder.reqType.setText("Property Bookings");
                    } else if (task.getServiceRequestType().equals(Constants.ServiceRequestType.Cancellation.toString())) {
                        holder.reqType.setText("Property Cancellation");
                    } else if (task.getServiceRequestType().equals(Constants.ServiceRequestType.Transfer.toString())) {
                        holder.reqType.setText("Property Transfer");
                    } else if (task.getServiceRequestType().equals(Constants.ServiceRequestType.ConstructionUpdate.toString())) {
                        holder.reqType.setText("Construction Updates");
                    } else if (task.getServiceRequestType().equals(Constants.ServiceRequestType.Other.toString())) {
                        holder.reqType.setText("Others");
                    }
                }else{
                    holder.reqType.setText("Others");
                }
                if(task.getDescription() != null && !task.getDescription().isEmpty()) {
                    holder.reqComments.setVisibility(View.VISIBLE);
                    holder.reqComments.setText(task.getDescription());
                }else{
                    holder.reqComments.setVisibility(View.GONE);
                }
                if(task.getRequestTime() != null) {
                    holder.reqTime.setText(UtilityMethods.getDate(task.getRequestTime(), "dd MMM yyyy hh:mm aa"));
                }
                if(task.getStatus() != null) {
                    if(task.getStatus().equals(Constants.TaskStatus.open.toString())){
                        holder.reqStatus.setText(task.getStatus());
                    }else if(task.getStatus().equals(Constants.TaskStatus.WaitingForAssign.toString())){
                        holder.reqStatus.setText("Waiting for assign");
                    }else if(task.getStatus().equals(Constants.TaskStatus.inProgress.toString())){
                        holder.reqStatus.setText("In Progress");
                    }else if(task.getStatus().equals(Constants.TaskStatus.done.toString())){
                        holder.reqStatus.setText(task.getStatus());
                    }else if(task.getStatus().equals(Constants.TaskStatus.closed.toString())){
                        holder.reqStatus.setText(task.getStatus());
                    }else if(task.getStatus().equals(Constants.TaskStatus.cancel.toString())){
                        holder.reqStatus.setText(task.getStatus());
                    }
                }else{
                    holder.reqStatus.setText("Open");
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
